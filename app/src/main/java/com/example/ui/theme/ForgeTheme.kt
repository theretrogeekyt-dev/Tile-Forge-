package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class ForgeThemeStyle(
    val id: String,
    val name: String,
    val description: String,
    val backgroundColor: Color,
    val gridBgColor: Color,
    val gridCellBgColor: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tileColors: Map<Int, Color>,
    val specialTileColors: Map<String, Color>
)

object ForgeThemes {

    val ClassicObsidian = ForgeThemeStyle(
        id = "classic_obsidian",
        name = "Classic Obsidian",
        description = "Dark volcanic stone with fiery ember accents",
        backgroundColor = Color(0xFF141018),
        gridBgColor = Color(0xFF221A28),
        gridCellBgColor = Color(0xFF32283A),
        primaryAccent = Color(0xFFFF6D00),
        secondaryAccent = Color(0xFFFFAB00),
        tileColors = mapOf(
            2 to Color(0xFF3E3048),
            4 to Color(0xFF533F5F),
            8 to Color(0xFF8C382A),
            16 to Color(0xFFB84523),
            32 to Color(0xFFD85721),
            64 to Color(0xFFE65100),
            128 to Color(0xFFFF6F00),
            256 to Color(0xFFFF8F00),
            512 to Color(0xFFFFA000),
            1024 to Color(0xFFFFB300),
            2048 to Color(0xFFFFC107),
            4096 to Color(0xFFFFD54F)
        ),
        specialTileColors = mapOf(
            "crystal" to Color(0xFF00E5FF),
            "anvil" to Color(0xFF7C4DFF),
            "flame" to Color(0xFFFF3D00),
            "obstacle" to Color(0xFF424242),
            "artifact" to Color(0xFFFFD700)
        )
    )

    val CyberRune = ForgeThemeStyle(
        id = "cyber_rune",
        name = "Cyber Rune Neon",
        description = "Futuristic glowing grid with neon cyan & magenta",
        backgroundColor = Color(0xFF0A0E17),
        gridBgColor = Color(0xFF121B2C),
        gridCellBgColor = Color(0xFF1A263D),
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFFFF007F),
        tileColors = mapOf(
            2 to Color(0xFF1D2F4D),
            4 to Color(0xFF2B4268),
            8 to Color(0xFF00838F),
            16 to Color(0xFF00ACC1),
            32 to Color(0xFF00BCD4),
            64 to Color(0xFF00E5FF),
            128 to Color(0xFFC2185B),
            256 to Color(0xFFD81B60),
            512 to Color(0xFFE91E63),
            1024 to Color(0xFFF48FB1),
            2048 to Color(0xFF00E676),
            4096 to Color(0xFF69F0AE)
        ),
        specialTileColors = mapOf(
            "crystal" to Color(0xFF18FFFF),
            "anvil" to Color(0xFFD500F9),
            "flame" to Color(0xFFFF1744),
            "obstacle" to Color(0xFF37474F),
            "artifact" to Color(0xFF64FFDA)
        )
    )

    val CelestialGold = ForgeThemeStyle(
        id = "celestial_gold",
        name = "Celestial Gold",
        description = "Ethereal starry cosmos with pure golden resonance",
        backgroundColor = Color(0xFF0D111E),
        gridBgColor = Color(0xFF171E32),
        gridCellBgColor = Color(0xFF222B45),
        primaryAccent = Color(0xFFFFD700),
        secondaryAccent = Color(0xFF7C4DFF),
        tileColors = mapOf(
            2 to Color(0xFF2A3452),
            4 to Color(0xFF38456C),
            8 to Color(0xFF4A5A8C),
            16 to Color(0xFF5C6BC0),
            32 to Color(0xFF3F51B5),
            64 to Color(0xFF7E57C2),
            128 to Color(0xFFAB47BC),
            256 to Color(0xFFD4AF37),
            512 to Color(0xFFFFD700),
            1024 to Color(0xFFFFE082),
            2048 to Color(0xFFFFF59D),
            4096 to Color(0xFFFFFFFF)
        ),
        specialTileColors = mapOf(
            "crystal" to Color(0xFF80DEEA),
            "anvil" to Color(0xFFB388FF),
            "flame" to Color(0xFFFF8A80),
            "obstacle" to Color(0xFF455A64),
            "artifact" to Color(0xFFFFD700)
        )
    )

    val FrostSpire = ForgeThemeStyle(
        id = "frost_spire",
        name = "Frost Spire",
        description = "Icy glacial blue with crystalline sapphire glow",
        backgroundColor = Color(0xFF0B192C),
        gridBgColor = Color(0xFF1E3E62),
        gridCellBgColor = Color(0xFF2B547E),
        primaryAccent = Color(0xFF80D8FF),
        secondaryAccent = Color(0xFF40C4FF),
        tileColors = mapOf(
            2 to Color(0xFF1F4A6D),
            4 to Color(0xFF2A628F),
            8 to Color(0xFF3E82B8),
            16 to Color(0xFF0288D1),
            32 to Color(0xFF03A9F4),
            64 to Color(0xFF29B6F6),
            128 to Color(0xFF4FC3F7),
            256 to Color(0xFF81D4FA),
            512 to Color(0xFFB3E5FC),
            1024 to Color(0xFFE0F7FA),
            2048 to Color(0xFF80CBC4),
            4096 to Color(0xFFA7FFEB)
        ),
        specialTileColors = mapOf(
            "crystal" to Color(0xFF84FFFF),
            "anvil" to Color(0xFFB388FF),
            "flame" to Color(0xFFFFAB40),
            "obstacle" to Color(0xFF546E7A),
            "artifact" to Color(0xFFE0F7FA)
        )
    )

    val allThemes = listOf(ClassicObsidian, CyberRune, CelestialGold, FrostSpire)

    fun getThemeById(id: String): ForgeThemeStyle {
        return allThemes.find { it.id == id } ?: ClassicObsidian
    }
}
