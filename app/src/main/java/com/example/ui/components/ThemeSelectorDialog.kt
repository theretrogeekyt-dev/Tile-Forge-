package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Palette
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
import com.example.ui.theme.ForgeThemeStyle
import com.example.ui.theme.ForgeThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectorDialog(
    activeTheme: ForgeThemeStyle,
    onSelectTheme: (ForgeThemeStyle) -> Unit,
    onDismiss: () -> Unit
) {
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
                .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("theme_dialog")
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
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Themes",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VISUAL THEMES",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF),
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("close_themes_btn")
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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ForgeThemes.allThemes) { theme ->
                        ThemeCard(
                            theme = theme,
                            isSelected = theme.id == activeTheme.id,
                            onClick = {
                                onSelectTheme(theme)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: ForgeThemeStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(theme.backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = theme.description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Palette Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(theme.backgroundColor).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(theme.gridBgColor).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(theme.primaryAccent))
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(theme.secondaryAccent))
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
