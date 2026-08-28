package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GameStatsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalBackupDialog(
    gameStats: GameStatsEntity?,
    lastBackupTime: String,
    isProcessing: Boolean,
    statusMessage: String?,
    onExportZip: () -> Unit,
    onImportZip: () -> Unit,
    onShareZip: () -> Unit,
    onQuickBackup: () -> Unit,
    onQuickRestore: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1726),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF7C4DFF)),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF7C4DFF).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderZip,
                        contentDescription = "ZIP Save Backup",
                        tint = Color(0xFFB388FF),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SAVE BACKUP (.ZIP)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Export, import, or share your game progress as a compressed ZIP file with no external accounts needed.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Data Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF120E18))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Storage,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Last Backup:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Text(
                                text = lastBackupTime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB388FF)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "High Score:",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${gameStats?.highScore ?: 0}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF6D00)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Energy Gathered:",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${gameStats?.totalEnergyCollected ?: 0} ⚡",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                        }
                    }
                }

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statusMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color(0xFF7C4DFF),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportZip,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("export_zip_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.SaveAlt, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export Save as .ZIP File", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Button(
                            onClick = onImportZip,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("import_zip_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.UploadFile, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Import Save from .ZIP File", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        OutlinedButton(
                            onClick = onShareZip,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("share_zip_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Share, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share / Send Save .ZIP", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onQuickBackup,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Archive, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Quick Backup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            OutlinedButton(
                                onClick = onQuickRestore,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.DownloadForOffline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Quick Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("close_backup_dialog_btn")
                        ) {
                            Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
