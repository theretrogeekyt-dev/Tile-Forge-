package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeThemeStyle
import com.example.ui.viewmodel.PowerAbilityType

@Composable
fun PowerUpActionBar(
    energy: Int,
    activeAbility: PowerAbilityType,
    theme: ForgeThemeStyle,
    onSelectAbility: (PowerAbilityType) -> Unit,
    onActivateIgnis: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        // Active Targeting Cancel/Instruction Banner
        AnimatedVisibility(
            visible = activeAbility != PowerAbilityType.NONE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFD54F))
                    .clickable { onSelectAbility(activeAbility) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ TAP TILE TO ${activeAbility.name} • TAP TO CANCEL",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PowerItem(
                title = "Shatter",
                cost = "20⚡",
                icon = Icons.Filled.DeleteForever,
                isEnoughEnergy = energy >= 20,
                isSelected = activeAbility == PowerAbilityType.SHATTER,
                accentColor = Color(0xFFFF5252),
                theme = theme,
                onClick = { onSelectAbility(PowerAbilityType.SHATTER) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("power_shatter")
            )

            PowerItem(
                title = "Duplicate",
                cost = "40⚡",
                icon = Icons.Filled.ContentCopy,
                isEnoughEnergy = energy >= 40,
                isSelected = activeAbility == PowerAbilityType.DUPLICATE,
                accentColor = Color(0xFF69F0AE),
                theme = theme,
                onClick = { onSelectAbility(PowerAbilityType.DUPLICATE) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("power_duplicate")
            )

            PowerItem(
                title = "Transmute",
                cost = "50⚡",
                icon = Icons.Filled.AutoAwesome,
                isEnoughEnergy = energy >= 50,
                isSelected = activeAbility == PowerAbilityType.TRANSMUTE,
                accentColor = Color(0xFFE040FB),
                theme = theme,
                onClick = { onSelectAbility(PowerAbilityType.TRANSMUTE) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("power_transmute")
            )

            PowerItem(
                title = "Ignis Burn",
                cost = "30⚡",
                icon = Icons.Filled.LocalFireDepartment,
                isEnoughEnergy = energy >= 30,
                isSelected = false,
                accentColor = Color(0xFFFF6E40),
                theme = theme,
                onClick = onActivateIgnis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("power_ignis")
            )
        }
    }
}

@Composable
private fun PowerItem(
    title: String,
    cost: String,
    icon: ImageVector,
    isEnoughEnergy: Boolean,
    isSelected: Boolean,
    accentColor: Color,
    theme: ForgeThemeStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> Color(0xFFFFD54F).copy(alpha = 0.25f)
        isEnoughEnergy -> theme.gridBgColor
        else -> theme.gridBgColor.copy(alpha = 0.4f)
    }

    val borderColor = when {
        isSelected -> Color(0xFFFFD54F)
        isEnoughEnergy -> accentColor.copy(alpha = 0.45f)
        else -> Color.White.copy(alpha = 0.08f)
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isEnoughEnergy) { onClick() }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color(0xFFFFD54F) else if (isEnoughEnergy) accentColor else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnoughEnergy) Color.White else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = cost,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (isEnoughEnergy) Color(0xFF00E5FF) else Color.Gray
            )
        }
    }
}
