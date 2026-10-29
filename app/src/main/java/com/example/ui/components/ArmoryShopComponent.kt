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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeThemeStyle
import com.example.ui.theme.ForgeThemes

data class PerkItem(
    val bit: Int,
    val name: String,
    val description: String,
    val costGold: Int,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun ArmoryShopComponent(
    goldCoins: Int,
    totalEnergy: Int,
    unlockedPerksMask: Int,
    activeTheme: ForgeThemeStyle,
    onPurchasePerk: (Int, Int) -> Unit,
    onSelectTheme: (ForgeThemeStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    val perks = listOf(
        PerkItem(
            bit = 1,
            name = "Keen Eye",
            description = "Hones board foresight: 4 and 8 tiles spawn twice as frequently.",
            costGold = 200,
            icon = Icons.Default.RemoveRedEye,
            accentColor = Color(0xFF64B5F6)
        ),
        PerkItem(
            bit = 2,
            name = "Iron Will",
            description = "Enhanced resilience: Start every forge game with 4 Rescue Hearts instead of 3.",
            costGold = 350,
            icon = Icons.Default.Favorite,
            accentColor = Color(0xFFFF5252)
        ),
        PerkItem(
            bit = 4,
            name = "Arcane Flow",
            description = "Efficient spellcasting: Shatter, Duplicate & Transmute energy costs discounted.",
            costGold = 300,
            icon = Icons.Default.Bolt,
            accentColor = Color(0xFFE040FB)
        ),
        PerkItem(
            bit = 8,
            name = "Plus Ultra Surge",
            description = "All Might's Spirit: Fever combos trigger faster and multiply points by 2x!",
            costGold = 500,
            icon = Icons.Default.LocalFireDepartment,
            accentColor = Color(0xFFFFD700)
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vault Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A22)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚒️ FORGE ARMORY",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Spend gold earned from matches to unlock permanent perks",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFF2C2208), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🪙 $goldCoins",
                            color = Color(0xFFFFD700),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Section Title: Perks
        item {
            Text(
                text = "PERMANENT FORGE PERKS",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Perk Cards
        items(perks.size) { index ->
            val perk = perks[index]
            val isUnlocked = (unlockedPerksMask and perk.bit) != 0
            val canAfford = goldCoins >= perk.costGold

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) Color(0xFF1C1D24) else Color(0xFF16151B)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isUnlocked) perk.accentColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) perk.accentColor.copy(alpha = 0.25f) else Color(0xFF25252D)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = perk.icon,
                            contentDescription = perk.name,
                            tint = if (isUnlocked) perk.accentColor else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Text
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = perk.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ACTIVE",
                                    color = perk.accentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = perk.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action
                    if (isUnlocked) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(perk.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Owned",
                                tint = perk.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onPurchasePerk(perk.bit, perk.costGold) },
                            enabled = canAfford,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) Color(0xFFFFD700) else Color(0xFF33333C),
                                disabledContainerColor = Color(0xFF272630)
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${perk.costGold}🪙",
                                color = if (canAfford) Color.Black else Color.White.copy(alpha = 0.4f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Board Themes
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "BOARD THEMES & VISUALS",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Themes
        items(ForgeThemes.allThemes.size) { index ->
            val themeOption = ForgeThemes.allThemes[index]
            val isCurrent = themeOption.id == activeTheme.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTheme(themeOption) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16151B)),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isCurrent) 1.5.dp else 0.5.dp,
                    color = if (isCurrent) themeOption.primaryColor else Color.White.copy(alpha = 0.15f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color Palette swatch circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(themeOption.primaryColor, themeOption.accentColor)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = themeOption.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = themeOption.description,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }

                    if (isCurrent) {
                        Text(
                            text = "EQUIPPED",
                            color = themeOption.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    } else {
                        Text(
                            text = "EQUIP",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
