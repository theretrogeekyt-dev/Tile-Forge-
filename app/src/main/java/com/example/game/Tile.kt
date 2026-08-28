package com.example.game

import java.util.UUID

enum class TileType {
    STANDARD,
    ENERGY_CRYSTAL,
    FORGE_ANVIL,
    FORGE_FLAME,
    OBSTACLE,
    ARTIFACT
}

enum class PowerType {
    NONE,
    FREEZE,
    BOMB,
    DUPLICATOR,
    MULTIPLIER_2X
}

enum class ArtifactType(val displayName: String, val description: String) {
    AEGIS_SHIELD("Aegis Shield", "Prevents game over once when board is full"),
    IGNIS_EMBER("Ignis Ember", "Destroys all low-value (2 & 4) tiles automatically"),
    MIDAS_RING("Midas Ring", "Doubles all points earned for the next 5 moves"),
    CHRONOS_RELIC("Chronos Relic", "Grants +5 free moves in Challenge Mode")
}

data class Tile(
    val id: String = UUID.randomUUID().toString(),
    val value: Int = 2,
    val type: TileType = TileType.STANDARD,
    val powerType: PowerType = PowerType.NONE,
    val artifactType: ArtifactType? = null,
    val energyBonus: Int = 0,
    val isFrozen: Boolean = false,
    val mergedInTurn: Boolean = false,
    val isNew: Boolean = false,
    val row: Int = 0,
    val col: Int = 0,
    val previousRow: Int = row,
    val previousCol: Int = col
) {
    val displayLabel: String
        get() = when (type) {
            TileType.ENERGY_CRYSTAL -> "+$energyBonus⚡"
            TileType.FORGE_ANVIL -> "⚒️"
            TileType.FORGE_FLAME -> "🔥$value"
            TileType.OBSTACLE -> "🪨"
            TileType.ARTIFACT -> artifactType?.displayName?.take(4)?.uppercase() ?: "✨"
            TileType.STANDARD -> when (powerType) {
                PowerType.FREEZE -> "❄️$value"
                PowerType.BOMB -> "💣$value"
                PowerType.DUPLICATOR -> "✨$value"
                PowerType.MULTIPLIER_2X -> "x2 $value"
                PowerType.NONE -> value.toString()
            }
        }
}
