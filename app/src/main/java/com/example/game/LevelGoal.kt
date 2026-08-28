package com.example.game

enum class GameModeType {
    ENDLESS,
    CHALLENGE,
    DAILY_QUEST
}

data class LevelGoal(
    val levelId: Int,
    val title: String,
    val description: String,
    val targetScore: Int,
    val targetTileValue: Int = 0,
    val targetEnergyCollected: Int = 0,
    val targetArtifactsForged: Int = 0,
    val maxMoves: Int = 0, // 0 = unlimited
    val initialObstacles: Int = 0
) {
    companion object {
        fun getDailyQuestGoal(dateStr: String): LevelGoal {
            return LevelGoal(
                levelId = 999,
                title = "Daily Crucible ($dateStr)",
                description = "Fixed Seeded Board • Compete for daily high score! Hearts rescue mistakes.",
                targetScore = 5000,
                targetTileValue = 512,
                maxMoves = 0
            )
        }

        fun getLevel(levelId: Int): LevelGoal {
            return when (levelId) {
                1 -> LevelGoal(1, "Ember Forge", "Forge a 64 tile & score 500 points", targetScore = 500, targetTileValue = 64, maxMoves = 30)
                2 -> LevelGoal(2, "Crystal Gathering", "Gather 50 Energy & forge a 128 tile", targetScore = 1000, targetTileValue = 128, targetEnergyCollected = 50, maxMoves = 35)
                3 -> LevelGoal(3, "Rocky Terrain", "Clear board with 2 Obstacles & reach 1500 score", targetScore = 1500, targetTileValue = 128, maxMoves = 35, initialObstacles = 2)
                4 -> LevelGoal(4, "Artifact Awakening", "Forge 1 Artifact & reach 256 tile", targetScore = 2500, targetTileValue = 256, targetArtifactsForged = 1, maxMoves = 40)
                5 -> LevelGoal(5, "High Voltage", "Gather 100 Energy & reach 4000 score", targetScore = 4000, targetTileValue = 256, targetEnergyCollected = 100, maxMoves = 40)
                6 -> LevelGoal(6, "Iron Anvil", "Forge a 512 tile in under 45 moves", targetScore = 6000, targetTileValue = 512, maxMoves = 45, initialObstacles = 1)
                7 -> LevelGoal(7, "Relic Master", "Forge 2 Artifacts & reach 8000 score", targetScore = 8000, targetTileValue = 512, targetArtifactsForged = 2, maxMoves = 50)
                8 -> LevelGoal(8, "Obstacle Rush", "Clear 3 initial obstacles & reach 10,000 score", targetScore = 10000, targetTileValue = 512, maxMoves = 50, initialObstacles = 3)
                9 -> LevelGoal(9, "Grand Artisan", "Forge a 1024 tile!", targetScore = 15000, targetTileValue = 1024, maxMoves = 60)
                10 -> LevelGoal(10, "Titan's Forge", "Forge a 2048 tile & 3 Artifacts!", targetScore = 25000, targetTileValue = 2048, targetArtifactsForged = 3, maxMoves = 75, initialObstacles = 2)
                else -> LevelGoal(levelId, "Master Forge $levelId", "Reach ${levelId * 2000} points & forge a high tile", targetScore = levelId * 2000, targetTileValue = 1024, maxMoves = 50 + levelId * 2)
            }
        }
    }
}
