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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AchievementEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsDialog(
    achievements: List<AchievementEntity>,
    onDismiss: () -> Unit
) {
    val unlockedCount = achievements.count { it.isUnlocked }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF191322),
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .border(1.5.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("achievements_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TROPHIES ($unlockedCount/${achievements.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            letterSpacing = 0.8.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("close_achievements_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(achievements) { item ->
                        AchievementRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(item: AchievementEntity) {
    val bgColor = if (item.isUnlocked) Color(0xFF261D32) else Color(0xFF140F1B)
    val borderColor = if (item.isUnlocked) Color(0xFFFFD700).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isUnlocked) Color(0xFFFFD700).copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.isUnlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (item.isUnlocked) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = if (item.isUnlocked) Color.White else Color.White.copy(alpha = 0.4f)
                )
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = if (item.isUnlocked) Color.White.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}
