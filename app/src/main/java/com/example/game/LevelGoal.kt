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
    val initialObstacles: Int = 0,
    val chapterId: Int = 1,
    val chapterName: String = "Apprentice's Crucible",
    val goldReward: Int = 100,
    val xpReward: Int = 50
) {
    companion object {
        fun getDailyQuestGoal(dateStr: String): LevelGoal {
            return LevelGoal(
                levelId = 999,
                title = "Daily Crucible ($dateStr)",
                description = "Seeded Board • Score big & maintain combos! Mistakes are rescued by hearts.",
                targetScore = 5000,
                targetTileValue = 512,
                maxMoves = 0,
                chapterId = 0,
                chapterName = "Daily Trials",
                goldReward = 200,
                xpReward = 100
            )
        }

        fun getChapterName(chapterId: Int): String {
            return when (chapterId) {
                1 -> "Chapter I: Apprentice's Crucible"
                2 -> "Chapter II: Elemental Forges"
                3 -> "Chapter III: Plus Ultra Trials"
                else -> "Master Chapters"
            }
        }

        fun getLevel(levelId: Int): LevelGoal {
            return when (levelId) {
                // Chapter 1: The Apprentice's Crucible (Stages 1-5)
                1 -> LevelGoal(
                    levelId = 1,
                    title = "Ember Sparks",
                    description = "Master sliding: Forge a 64 tile & score 500 points",
                    targetScore = 500,
                    targetTileValue = 64,
                    maxMoves = 30,
                    chapterId = 1,
                    chapterName = "Apprentice's Crucible",
                    goldReward = 80,
                    xpReward = 40
                )
                2 -> LevelGoal(
                    levelId = 2,
                    title = "Crystal Harvester",
                    description = "Gather 50 Energy & forge a 128 tile",
                    targetScore = 1000,
                    targetTileValue = 128,
                    targetEnergyCollected = 50,
                    maxMoves = 35,
                    chapterId = 1,
                    chapterName = "Apprentice's Crucible",
                    goldReward = 100,
                    xpReward = 50
                )
                3 -> LevelGoal(
                    levelId = 3,
                    title = "Rocky Terrain",
                    description = "Navigate 2 Obstacles & reach 1,500 score",
                    targetScore = 1500,
                    targetTileValue = 128,
                    maxMoves = 35,
                    initialObstacles = 2,
                    chapterId = 1,
                    chapterName = "Apprentice's Crucible",
                    goldReward = 120,
                    xpReward = 60
                )
                4 -> LevelGoal(
                    levelId = 4,
                    title = "Artifact Awakening",
                    description = "Forge 1 Relic Artifact & reach a 256 tile",
                    targetScore = 2500,
                    targetTileValue = 256,
                    targetArtifactsForged = 1,
                    maxMoves = 40,
                    chapterId = 1,
                    chapterName = "Apprentice's Crucible",
                    goldReward = 150,
                    xpReward = 75
                )
                5 -> LevelGoal(
                    levelId = 5,
                    title = "Grand Apprenticeship",
                    description = "Gather 100 Energy & score 4,000 points to earn your Smith Crest",
                    targetScore = 4000,
                    targetTileValue = 256,
                    targetEnergyCollected = 100,
                    maxMoves = 40,
                    chapterId = 1,
                    chapterName = "Apprentice's Crucible",
                    goldReward = 200,
                    xpReward = 100
                )

                // Chapter 2: Elemental Forges (Stages 6-10)
                6 -> LevelGoal(
                    levelId = 6,
                    title = "Iron Anvil",
                    description = "Forge a 512 tile in under 45 moves with 1 obstacle",
                    targetScore = 6000,
                    targetTileValue = 512,
                    maxMoves = 45,
                    initialObstacles = 1,
                    chapterId = 2,
                    chapterName = "Elemental Forges",
                    goldReward = 220,
                    xpReward = 110
                )
                7 -> LevelGoal(
                    levelId = 7,
                    title = "Relic Master",
                    description = "Forge 2 Artifacts & reach 8,000 points",
                    targetScore = 8000,
                    targetTileValue = 512,
                    targetArtifactsForged = 2,
                    maxMoves = 50,
                    chapterId = 2,
                    chapterName = "Elemental Forges",
                    goldReward = 250,
                    xpReward = 125
                )
                8 -> LevelGoal(
                    levelId = 8,
                    title = "Molten Gauntlet",
                    description = "Overcome 3 initial obstacles & reach 10,000 score",
                    targetScore = 10000,
                    targetTileValue = 512,
                    maxMoves = 50,
                    initialObstacles = 3,
                    chapterId = 2,
                    chapterName = "Elemental Forges",
                    goldReward = 280,
                    xpReward = 140
                )
                9 -> LevelGoal(
                    levelId = 9,
                    title = "Grand Artisan",
                    description = "Forge a 1024 tile & gather 120 Energy",
                    targetScore = 15000,
                    targetTileValue = 1024,
                    targetEnergyCollected = 120,
                    maxMoves = 60,
                    chapterId = 2,
                    chapterName = "Elemental Forges",
                    goldReward = 320,
                    xpReward = 160
                )
                10 -> LevelGoal(
                    levelId = 10,
                    title = "Titan's Anvil",
                    description = "Forge a 2048 tile & 3 Artifacts against 2 obstacles!",
                    targetScore = 25000,
                    targetTileValue = 2048,
                    targetArtifactsForged = 3,
                    maxMoves = 75,
                    initialObstacles = 2,
                    chapterId = 2,
                    chapterName = "Elemental Forges",
                    goldReward = 400,
                    xpReward = 200
                )

                // Chapter 3: Plus Ultra Trials (Stages 11-15)
                11 -> LevelGoal(
                    levelId = 11,
                    title = "U.A. Hero Exam",
                    description = "Pass the Hero Exam: Harvest 150 Energy & forge a 1024 tile!",
                    targetScore = 18000,
                    targetTileValue = 1024,
                    targetEnergyCollected = 150,
                    maxMoves = 65,
                    initialObstacles = 2,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 450,
                    xpReward = 220
                )
                12 -> LevelGoal(
                    levelId = 12,
                    title = "All Might's Legacy",
                    description = "Unleash 100% Smash: Forge a 2048 tile and reach 30,000 score!",
                    targetScore = 30000,
                    targetTileValue = 2048,
                    targetArtifactsForged = 3,
                    maxMoves = 80,
                    initialObstacles = 3,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 500,
                    xpReward = 250
                )
                13 -> LevelGoal(
                    levelId = 13,
                    title = "Full Cowling 20%",
                    description = "Deku's Kinetic Rhythm: 200 Energy & 35,000 score",
                    targetScore = 35000,
                    targetTileValue = 2048,
                    targetEnergyCollected = 200,
                    maxMoves = 85,
                    initialObstacles = 2,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 550,
                    xpReward = 280
                )
                14 -> LevelGoal(
                    levelId = 14,
                    title = "Bakugo's Howitzer Impact",
                    description = "Clear 4 Obstacles with Arcane Shatter & score 42,000 points!",
                    targetScore = 42000,
                    targetTileValue = 2048,
                    targetArtifactsForged = 4,
                    maxMoves = 90,
                    initialObstacles = 4,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 600,
                    xpReward = 320
                )
                15 -> LevelGoal(
                    levelId = 15,
                    title = "United States of Smash",
                    description = "Zenith of Heroism: Forge a 4096 tile and score 55,000 points!",
                    targetScore = 55000,
                    targetTileValue = 4096,
                    targetArtifactsForged = 5,
                    maxMoves = 100,
                    initialObstacles = 3,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 1000,
                    xpReward = 500
                )

                else -> LevelGoal(
                    levelId = levelId,
                    title = "Master Forge $levelId",
                    description = "Reach ${levelId * 2500} points & forge a high tile",
                    targetScore = levelId * 2500,
                    targetTileValue = 1024,
                    maxMoves = 50 + levelId * 2,
                    chapterId = 3,
                    chapterName = "Plus Ultra Trials",
                    goldReward = 300,
                    xpReward = 150
                )
            }
        }
    }
}
