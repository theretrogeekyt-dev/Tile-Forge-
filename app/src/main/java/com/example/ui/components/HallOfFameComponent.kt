package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AchievementEntity
import com.example.data.db.GameStatsEntity
import com.example.ui.theme.ForgeThemeStyle

@Composable
fun HallOfFameComponent(
    stats: GameStatsEntity,
    achievements: List<AchievementEntity>,
    theme: ForgeThemeStyle,
    onClaimStreak: (Int, Int, Int) -> Unit,
    onClaimQuest: (Int, Int, Int) -> Unit,
    onEquipTitle: (String) -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyRewards = listOf(
        Triple(1, 50, 0),
        Triple(2, 0, 30),
        Triple(3, 100, 0),
        Triple(4, 0, 50),
        Triple(5, 150, 0),
        Triple(6, 0, 100),
        Triple(7, 300, 100)
    )

    val heroTitles = listOf(
        "Symbol of Peace",
        "No. 1 Pro Hero",
        "Class 1-A Elite",
        "Full Cowling Artisan",
        "Apprentice Blacksmith"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 7-Day Login Streak Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A22)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🔥 7-DAY LOGIN STREAK",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Daily rewards reset every week • Keep the fire burning!",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "Day ${stats.dailyStreak}/7",
                            color = Color(0xFFFFD700),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dailyRewards.size) { index ->
                            val (day, coins, energy) = dailyRewards[index]
                            val isClaimed = stats.claimedDailyStreakDay >= day
                            val isReadyToClaim = stats.claimedDailyStreakDay == day - 1

                            Card(
                                modifier = Modifier
                                    .width(82.dp)
                                    .clickable(enabled = isReadyToClaim) {
                                        onClaimStreak(day, coins, energy)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isClaimed -> Color(0xFF16151B)
                                        isReadyToClaim -> Color(0xFF2C2208)
                                        else -> Color(0xFF1C1B24)
                                    }
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isReadyToClaim) 1.5.dp else 0.5.dp,
                                    color = if (isReadyToClaim) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Day $day",
                                        color = if (isReadyToClaim) Color(0xFFFFD700) else Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (coins > 0) "+$coins🪙" else "+$energy⚡",
                                        color = if (coins > 0) Color(0xFFFFD700) else Color(0xFF00E5FF),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (isClaimed) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Claimed",
                                            tint = Color(0xFF00E676),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else if (isReadyToClaim) {
                                        Text(
                                            text = "CLAIM",
                                            color = Color(0xFFFFD700),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    } else {
                                        Text(
                                            text = "LOCKED",
                                            color = Color.White.copy(alpha = 0.3f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Bounties
        item {
            Text(
                text = "DAILY BOUNTIES & MISSIONS",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Quest 1: Forge 128
        item {
            val isDone = stats.highestTile >= 128
            val isClaimed = (stats.claimedQuestsMask and 1) != 0
            QuestCard(
                title = "Master Smith",
                description = "Forge a 128+ Tile in any game mode",
                progressText = if (isDone) "1 / 1" else "0 / 1",
                progressFraction = if (isDone) 1f else 0f,
                rewardText = "+50🪙 +30 XP",
                isDone = isDone,
                isClaimed = isClaimed,
                onClaim = { onClaimQuest(1, 50, 30) },
                theme = theme
            )
        }

        // Quest 2: Combo x3
        item {
            val isDone = stats.maxComboChain >= 3
            val isClaimed = (stats.claimedQuestsMask and 2) != 0
            QuestCard(
                title = "Chain Striker",
                description = "Achieve a 3x Combo streak in one game",
                progressText = "${minOf(stats.maxComboChain, 3)} / 3",
                progressFraction = minOf(stats.maxComboChain / 3f, 1f),
                rewardText = "+75🪙 +40 XP",
                isDone = isDone,
                isClaimed = isClaimed,
                onClaim = { onClaimQuest(2, 75, 40) },
                theme = theme
            )
        }

        // Quest 3: Energy 40
        item {
            val current = minOf(stats.totalEnergyCollected, 40)
            val isDone = current >= 40
            val isClaimed = (stats.claimedQuestsMask and 4) != 0
            QuestCard(
                title = "Energy Harvester",
                description = "Collect 40 Energy Crystals",
                progressText = "$current / 40",
                progressFraction = current / 40f,
                rewardText = "+100🪙 +50 XP",
                isDone = isDone,
                isClaimed = isClaimed,
                onClaim = { onClaimQuest(4, 100, 50) },
                theme = theme
            )
        }

        // MHA Titles Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "HERO TITLES (MY HERO ACADEMIA)",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        items(heroTitles.size) { index ->
            val titleName = heroTitles[index]
            val isEquipped = stats.equippedTitle == titleName

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEquipTitle(titleName) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEquipped) theme.primaryColor.copy(alpha = 0.15f) else Color(0xFF16151B)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isEquipped) 1.5.dp else 0.5.dp,
                    color = if (isEquipped) theme.primaryColor else Color.White.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = if (isEquipped) theme.primaryColor else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = titleName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = if (isEquipped) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    if (isEquipped) {
                        Text(
                            text = "EQUIPPED",
                            color = theme.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Text(
                            text = "EQUIP",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Action Buttons: Achievements & Backup
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenAchievements,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trophies", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenBackup,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuestCard(
    title: String,
    description: String,
    progressText: String,
    progressFraction: Float,
    rewardText: String,
    isDone: Boolean,
    isClaimed: Boolean,
    onClaim: () -> Unit,
    theme: ForgeThemeStyle
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16151B)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isClaimed) {
                    Text(
                        text = "CLAIMED",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Button(
                        onClick = onClaim,
                        enabled = isDone,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            disabledContainerColor = Color(0xFF2A2A34)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isDone) "CLAIM" else rewardText,
                            color = if (isDone) Color.Black else Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isDone) Color(0xFF00E676) else theme.primaryColor,
                    trackColor = Color(0xFF24232C)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = progressText,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
