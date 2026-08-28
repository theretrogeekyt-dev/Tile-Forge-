package com.example.data.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.db.AchievementEntity
import com.example.data.db.GameStatsEntity
import com.example.data.db.LevelProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class LocalZipBackupManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("tileforge_zip_backup_prefs", Context.MODE_PRIVATE)

    fun getLastBackupTimeFormatted(): String {
        val timestamp = prefs.getLong("last_zip_backup_time", 0L)
        if (timestamp == 0L) return "Never"
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun setLastBackupTime(timestamp: Long) {
        prefs.edit().putLong("last_zip_backup_time", timestamp).apply()
    }

    fun serializePayload(
        stats: GameStatsEntity,
        levels: List<LevelProgressEntity>,
        achievements: List<AchievementEntity>
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        // Stats
        val statsObj = JSONObject()
        statsObj.put("id", stats.id)
        statsObj.put("highScore", stats.highScore)
        statsObj.put("dailyQuestHighScore", stats.dailyQuestHighScore)
        statsObj.put("lastDailyQuestDate", stats.lastDailyQuestDate)
        statsObj.put("highestTile", stats.highestTile)
        statsObj.put("totalEnergyCollected", stats.totalEnergyCollected)
        statsObj.put("totalArtifactsForged", stats.totalArtifactsForged)
        statsObj.put("totalGamesPlayed", stats.totalGamesPlayed)
        statsObj.put("maxComboChain", stats.maxComboChain)
        statsObj.put("activeThemeId", stats.activeThemeId)
        root.put("stats", statsObj)

        // Levels
        val levelsArray = JSONArray()
        for (lvl in levels) {
            val lvlObj = JSONObject()
            lvlObj.put("levelId", lvl.levelId)
            lvlObj.put("isUnlocked", lvl.isUnlocked)
            lvlObj.put("starsEarned", lvl.starsEarned)
            lvlObj.put("highScore", lvl.highScore)
            levelsArray.put(lvlObj)
        }
        root.put("levels", levelsArray)

        // Achievements
        val achArray = JSONArray()
        for (ach in achievements) {
            val achObj = JSONObject()
            achObj.put("id", ach.id)
            achObj.put("title", ach.title)
            achObj.put("description", ach.description)
            achObj.put("iconName", ach.iconName)
            achObj.put("isUnlocked", ach.isUnlocked)
            achObj.put("progress", ach.progress)
            achObj.put("maxProgress", ach.maxProgress)
            achArray.put(achObj)
        }
        root.put("achievements", achArray)

        return root.toString(2)
    }

    fun parsePayload(jsonString: String): TileForgeBackupPayload? {
        return try {
            val root = JSONObject(jsonString)
            val statsObj = root.optJSONObject("stats") ?: JSONObject()

            val stats = GameStatsEntity(
                id = statsObj.optInt("id", 1),
                highScore = statsObj.optInt("highScore", 0),
                dailyQuestHighScore = statsObj.optInt("dailyQuestHighScore", 0),
                lastDailyQuestDate = statsObj.optString("lastDailyQuestDate", ""),
                highestTile = statsObj.optInt("highestTile", 2),
                totalEnergyCollected = statsObj.optInt("totalEnergyCollected", 0),
                totalArtifactsForged = statsObj.optInt("totalArtifactsForged", 0),
                totalGamesPlayed = statsObj.optInt("totalGamesPlayed", 0),
                maxComboChain = statsObj.optInt("maxComboChain", 0),
                activeThemeId = statsObj.optString("activeThemeId", "classic_obsidian")
            )

            val levelsList = mutableListOf<LevelProgressEntity>()
            val levelsArray = root.optJSONArray("levels")
            if (levelsArray != null) {
                for (i in 0 until levelsArray.length()) {
                    val lvlObj = levelsArray.getJSONObject(i)
                    levelsList.add(
                        LevelProgressEntity(
                            levelId = lvlObj.getInt("levelId"),
                            isUnlocked = lvlObj.optBoolean("isUnlocked", false),
                            starsEarned = lvlObj.optInt("starsEarned", 0),
                            highScore = lvlObj.optInt("highScore", 0)
                        )
                    )
                }
            }

            val achList = mutableListOf<AchievementEntity>()
            val achArray = root.optJSONArray("achievements")
            if (achArray != null) {
                for (i in 0 until achArray.length()) {
                    val achObj = achArray.getJSONObject(i)
                    achList.add(
                        AchievementEntity(
                            id = achObj.getString("id"),
                            title = achObj.optString("title", ""),
                            description = achObj.optString("description", ""),
                            iconName = achObj.optString("iconName", ""),
                            isUnlocked = achObj.optBoolean("isUnlocked", false),
                            progress = achObj.optInt("progress", 0),
                            maxProgress = achObj.optInt("maxProgress", 1)
                        )
                    )
                }
            }

            TileForgeBackupPayload(
                version = root.optInt("version", 1),
                timestamp = root.optLong("timestamp", System.currentTimeMillis()),
                gameStats = stats,
                levelProgress = levelsList,
                achievements = achList
            )
        } catch (e: Exception) {
            Log.e("LocalZipBackupManager", "Error parsing payload: ${e.message}", e)
            null
        }
    }

    suspend fun exportToZipUri(
        uri: Uri,
        stats: GameStatsEntity,
        levels: List<LevelProgressEntity>,
        achievements: List<AchievementEntity>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonContent = serializePayload(stats, levels, achievements)
            val jsonBytes = jsonContent.toByteArray(Charsets.UTF_8)

            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val infoText = "TileForge Game Save Backup ZIP Archive\nExport Date: $dateStr\nApp: TileForge Game\nHigh Score: ${stats.highScore}\nEnergy: ${stats.totalEnergyCollected}\n"
            val infoBytes = infoText.toByteArray(Charsets.UTF_8)

            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: return@withContext Result.failure(Exception("Could not open file location for writing."))

            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // Write tileforge_save_data.json entry
                val jsonEntry = ZipEntry("tileforge_save_data.json")
                zipOut.putNextEntry(jsonEntry)
                zipOut.write(jsonBytes)
                zipOut.closeEntry()

                // Write backup_info.txt entry
                val infoEntry = ZipEntry("backup_info.txt")
                zipOut.putNextEntry(infoEntry)
                zipOut.write(infoBytes)
                zipOut.closeEntry()
            }

            // Also keep a quick internal backup cached
            saveQuickInternalBackup(stats, levels, achievements)

            val now = System.currentTimeMillis()
            setLastBackupTime(now)

            Result.success("Save data exported to ZIP file successfully!")
        } catch (e: Exception) {
            Log.e("LocalZipBackupManager", "Export to ZIP error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createShareableZip(
        stats: GameStatsEntity,
        levels: List<LevelProgressEntity>,
        achievements: List<AchievementEntity>
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val cacheDir = File(context.cacheDir, "backups")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val shareFile = File(cacheDir, "TileForge_Backup_$timeStamp.zip")

            val jsonContent = serializePayload(stats, levels, achievements)
            val jsonBytes = jsonContent.toByteArray(Charsets.UTF_8)

            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val infoText = "TileForge Game Save Backup ZIP Archive\nExport Date: $dateStr\nApp: TileForge Game\nHigh Score: ${stats.highScore}\nEnergy: ${stats.totalEnergyCollected}\n"
            val infoBytes = infoText.toByteArray(Charsets.UTF_8)

            FileOutputStream(shareFile).use { fos ->
                ZipOutputStream(BufferedOutputStream(fos)).use { zipOut ->
                    val jsonEntry = ZipEntry("tileforge_save_data.json")
                    zipOut.putNextEntry(jsonEntry)
                    zipOut.write(jsonBytes)
                    zipOut.closeEntry()

                    val infoEntry = ZipEntry("backup_info.txt")
                    zipOut.putNextEntry(infoEntry)
                    zipOut.write(infoBytes)
                    zipOut.closeEntry()
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                shareFile
            )

            // Cache quick internal backup
            saveQuickInternalBackup(stats, levels, achievements)

            val now = System.currentTimeMillis()
            setLastBackupTime(now)

            Result.success(uri)
        } catch (e: Exception) {
            Log.e("LocalZipBackupManager", "Create shareable ZIP error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun importFromZipUri(uri: Uri): Result<TileForgeBackupPayload> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Could not open selected backup file."))

            var jsonContent: String? = null

            // Try reading as a ZIP archive
            try {
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && (entry.name.contains("tileforge_save_data") || entry.name.endsWith(".json"))) {
                            jsonContent = zipIn.bufferedReader(Charsets.UTF_8).readText()
                            break
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            } catch (e: Exception) {
                Log.w("LocalZipBackupManager", "Not a valid ZIP stream, trying fallback: ${e.message}")
            }

            // Fallback: If not zipped or entry not found, try reading directly as plain text JSON
            if (jsonContent == null) {
                context.contentResolver.openInputStream(uri)?.use { plainStream ->
                    jsonContent = plainStream.bufferedReader(Charsets.UTF_8).readText()
                }
            }

            if (jsonContent.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Selected file does not contain valid TileForge save data."))
            }

            val payload = parsePayload(jsonContent!!)
                ?: return@withContext Result.failure(Exception("Corrupt or incompatible save data format in ZIP."))

            Result.success(payload)
        } catch (e: Exception) {
            Log.e("LocalZipBackupManager", "Import ZIP error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveQuickInternalBackup(
        stats: GameStatsEntity,
        levels: List<LevelProgressEntity>,
        achievements: List<AchievementEntity>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val localZipFile = File(context.filesDir, "tileforge_backup.zip")
            val jsonContent = serializePayload(stats, levels, achievements)
            val jsonBytes = jsonContent.toByteArray(Charsets.UTF_8)

            FileOutputStream(localZipFile).use { fos ->
                ZipOutputStream(BufferedOutputStream(fos)).use { zipOut ->
                    val jsonEntry = ZipEntry("tileforge_save_data.json")
                    zipOut.putNextEntry(jsonEntry)
                    zipOut.write(jsonBytes)
                    zipOut.closeEntry()
                }
            }

            val now = System.currentTimeMillis()
            setLastBackupTime(now)
            Result.success("Quick local backup saved.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreQuickInternalBackup(): Result<TileForgeBackupPayload> = withContext(Dispatchers.IO) {
        try {
            val localZipFile = File(context.filesDir, "tileforge_backup.zip")
            if (!localZipFile.exists()) {
                return@withContext Result.failure(Exception("No internal ZIP backup found."))
            }

            var jsonContent: String? = null
            FileInputStream(localZipFile).use { fis ->
                ZipInputStream(BufferedInputStream(fis)).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && entry.name.endsWith(".json")) {
                            jsonContent = zipIn.bufferedReader(Charsets.UTF_8).readText()
                            break
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }

            if (jsonContent.isNullOrBlank()) {
                return@withContext Result.failure(Exception("Internal ZIP backup is empty or invalid."))
            }

            val payload = parsePayload(jsonContent!!)
                ?: return@withContext Result.failure(Exception("Internal ZIP save data is corrupt."))

            Result.success(payload)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
