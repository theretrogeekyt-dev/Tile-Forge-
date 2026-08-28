package com.example.game

import java.util.UUID
import kotlin.random.Random

enum class SwipeDirection {
    UP, DOWN, LEFT, RIGHT
}

data class BoardSnapshot(
    val grid: List<List<Tile?>>,
    val score: Int,
    val energy: Int,
    val moves: Int,
    val hearts: Int
)

class GameBoard {
    val size = 4
    var grid = Array(size) { arrayOfNulls<Tile>(size) }
        private set

    var score: Int = 0
        private set
    var energy: Int = 0
        private set
    var moves: Int = 0
        private set
    var comboCount: Int = 0
        private set
    var artifactsForgedCount: Int = 0
        private set
    var energyCollectedCount: Int = 0
        private set
    var highestTileValue: Int = 2
        private set
    var isGameOver: Boolean = false
        private set

    var hearts: Int = 3
        private set
    val maxHearts: Int = 3

    var hasAegisShield: Boolean = false
    var midasTurnsLeft: Int = 0
    var lastSlideDirection: SwipeDirection? = null
        private set
    var lastSlideTimestamp: Long = 0L
        private set

    private var rng: Random = Random.Default
    private val undoHistory = mutableListOf<BoardSnapshot>()

    fun resetBoard(initialObstacles: Int = 0, seed: Long? = null, isDailyQuest: Boolean = false) {
        grid = Array(size) { arrayOfNulls<Tile>(size) }
        score = 0
        energy = 0
        moves = 0
        comboCount = 0
        artifactsForgedCount = 0
        energyCollectedCount = 0
        highestTileValue = 2
        isGameOver = false
        hasAegisShield = false
        midasTurnsLeft = 0
        hearts = maxHearts
        undoHistory.clear()

        // Seeded deterministic generator if seed is supplied
        rng = if (seed != null) Random(seed) else Random.Default

        if (isDailyQuest) {
            // Fixed, crafted daily starting layout for all players
            grid[0][0] = Tile(value = 4, row = 0, col = 0)
            grid[1][2] = Tile(value = 2, row = 1, col = 2)
            grid[2][1] = Tile(type = TileType.ENERGY_CRYSTAL, energyBonus = 25, row = 2, col = 1)
            grid[3][3] = Tile(value = 4, row = 3, col = 3)
            highestTileValue = 4
        } else {
            // Place initial obstacles if any
            repeat(initialObstacles) {
                spawnTileAtRandomEmpty(Tile(type = TileType.OBSTACLE))
            }

            // Spawn 2 initial tiles
            spawnRandomTile()
            spawnRandomTile()
        }
    }

    fun canUndo(): Boolean = undoHistory.isNotEmpty() && energy >= 15

    fun undo(): Boolean {
        if (!canUndo()) return false
        val snapshot = undoHistory.removeAt(undoHistory.size - 1)
        energy -= 15
        grid = Array(size) { r ->
            Array(size) { c ->
                snapshot.grid[r][c]?.copy()
            }
        }
        score = snapshot.score
        moves = snapshot.moves
        hearts = snapshot.hearts
        isGameOver = false
        return true
    }

    private fun saveSnapshot() {
        val snapshotGrid = grid.map { row ->
            row.map { tile -> tile?.copy() }
        }
        undoHistory.add(BoardSnapshot(snapshotGrid, score, energy, moves, hearts))
        if (undoHistory.size > 5) {
            undoHistory.removeAt(0)
        }
    }

    fun recoverHeart(): Boolean {
        if (hearts < maxHearts) {
            hearts++
            return true
        }
        return false
    }

    fun slide(direction: SwipeDirection): SlideResult {
        if (isGameOver) return SlideResult(
            moved = false,
            scoreGained = 0,
            energyGained = 0,
            mergesCount = 0,
            artifactForged = null,
            heartLost = false,
            heartsRemaining = hearts,
            wasRescued = false
        )

        saveSnapshot()

        // Reset mergedInTurn flags and sync previous coordinates
        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[r][c] = grid[r][c]?.copy(
                    mergedInTurn = false,
                    isNew = false,
                    previousRow = r,
                    previousCol = c
                )
            }
        }

        var moved = false
        var totalScoreGained = 0
        var totalEnergyGained = 0
        var totalMerges = 0
        var forgedArtifact: ArtifactType? = null

        val vector = when (direction) {
            SwipeDirection.UP -> Pair(-1, 0)
            SwipeDirection.DOWN -> Pair(1, 0)
            SwipeDirection.LEFT -> Pair(0, -1)
            SwipeDirection.RIGHT -> Pair(0, 1)
        }

        // Processing order
        val rowIndices = if (direction == SwipeDirection.DOWN) (size - 1 downTo 0) else (0 until size)
        val colIndices = if (direction == SwipeDirection.RIGHT) (size - 1 downTo 0) else (0 until size)

        for (r in rowIndices) {
            for (c in colIndices) {
                val tile = grid[r][c] ?: continue
                if (tile.type == TileType.OBSTACLE || tile.isFrozen) continue

                var currR = r
                var currC = c
                var nextR = currR + vector.first
                var nextC = currC + vector.second

                while (nextR in 0 until size && nextC in 0 until size) {
                    val targetTile = grid[nextR][nextC]

                    if (targetTile == null) {
                        // Move to empty space
                        grid[nextR][nextC] = grid[currR][currC]?.copy(row = nextR, col = nextC)
                        grid[currR][currC] = null
                        currR = nextR
                        currC = nextC
                        nextR = currR + vector.first
                        nextC = currC + vector.second
                        moved = true
                    } else if (canMerge(grid[currR][currC]!!, targetTile)) {
                        // Merge!
                        val source = grid[currR][currC]!!
                        val mergeResult = performMerge(source, targetTile, nextR, nextC)

                        grid[nextR][nextC] = mergeResult.newTile
                        grid[currR][currC] = null

                        moved = true
                        totalScoreGained += mergeResult.scoreGained
                        totalEnergyGained += mergeResult.energyGained
                        totalMerges++

                        if (mergeResult.artifactForged != null) {
                            forgedArtifact = mergeResult.artifactForged
                        }

                        // Trigger Bomb Power if present
                        if (source.powerType == PowerType.BOMB || targetTile.powerType == PowerType.BOMB) {
                            triggerBombAt(nextR, nextC)
                        }

                        break
                    } else {
                        break
                    }
                }
            }
        }

        var heartLost = false
        var wasRescued = false

        if (moved) {
            moves++
            comboCount = if (totalMerges > 1) comboCount + 1 else 1
            lastSlideDirection = direction
            lastSlideTimestamp = System.currentTimeMillis()

            // Apply Midas Ring Multiplier if active
            var finalScoreGained = totalScoreGained
            if (midasTurnsLeft > 0) {
                finalScoreGained *= 2
                midasTurnsLeft--
            }

            // Combo multiplier bonus
            if (comboCount > 1) {
                finalScoreGained = (finalScoreGained * (1.0 + comboCount * 0.25)).toInt()
            }

            score += finalScoreGained
            energy += totalEnergyGained
            energyCollectedCount += totalEnergyGained

            // Update highest tile value
            for (r in 0 until size) {
                for (c in 0 until size) {
                    val value = grid[r][c]?.value ?: 0
                    if (value > highestTileValue) {
                        highestTileValue = value
                    }
                }
            }

            // Spawn new tile after move
            spawnRandomTile()

            // Check game over or heart salvage
            val checkResult = checkGameOverOrRescue()
            heartLost = checkResult.heartLost
            wasRescued = checkResult.wasRescued
        }

        return SlideResult(
            moved = moved,
            scoreGained = totalScoreGained,
            energyGained = totalEnergyGained,
            mergesCount = totalMerges,
            artifactForged = forgedArtifact,
            heartLost = heartLost,
            heartsRemaining = hearts,
            wasRescued = wasRescued
        )
    }

    private fun canMerge(tile1: Tile, tile2: Tile): Boolean {
        if (tile1.mergedInTurn || tile2.mergedInTurn) return false
        if (tile1.type == TileType.OBSTACLE || tile2.type == TileType.OBSTACLE) return false
        if (tile1.isFrozen || tile2.isFrozen) return false

        // Anvil merges with standard/flame numbered tiles
        if (tile1.type == TileType.FORGE_ANVIL || tile2.type == TileType.FORGE_ANVIL) {
            val other = if (tile1.type == TileType.FORGE_ANVIL) tile2 else tile1
            return other.type == TileType.STANDARD || other.type == TileType.FORGE_FLAME
        }

        // Energy crystals merge together
        if (tile1.type == TileType.ENERGY_CRYSTAL && tile2.type == TileType.ENERGY_CRYSTAL) {
            return true
        }

        // Standard or Flame matching values
        if (tile1.type == TileType.ARTIFACT || tile2.type == TileType.ARTIFACT) return false
        return tile1.value == tile2.value
    }

    private fun performMerge(source: Tile, target: Tile, r: Int, c: Int): MergeOutcome {
        var artifactForged: ArtifactType? = null

        // 1. Anvil Merge -> Forges an Artifact!
        if (source.type == TileType.FORGE_ANVIL || target.type == TileType.FORGE_ANVIL) {
            val otherTile = if (source.type == TileType.FORGE_ANVIL) target else source
            val newValue = maxOf(4, otherTile.value * 2)
            val artifacts = ArtifactType.values()
            val randomArtifact = artifacts[rng.nextInt(artifacts.size)]
            artifactForged = randomArtifact
            artifactsForgedCount++

            if (randomArtifact == ArtifactType.AEGIS_SHIELD) {
                hasAegisShield = true
            } else if (randomArtifact == ArtifactType.MIDAS_RING) {
                midasTurnsLeft += 5
            } else if (randomArtifact == ArtifactType.CHRONOS_RELIC) {
                // Grant 5 extra moves back in challenge mode!
                moves = maxOf(0, moves - 5)
            }

            val newTile = Tile(
                value = newValue,
                type = TileType.ARTIFACT,
                artifactType = randomArtifact,
                mergedInTurn = true,
                isNew = true,
                row = r,
                col = c
            )
            return MergeOutcome(newTile, scoreGained = newValue * 3 + 100, energyGained = 25, artifactForged = randomArtifact)
        }

        // 2. Energy Crystals Merge
        if (source.type == TileType.ENERGY_CRYSTAL && target.type == TileType.ENERGY_CRYSTAL) {
            val totalEnergy = source.energyBonus + target.energyBonus + 15
            val newTile = Tile(
                value = 4,
                type = TileType.STANDARD,
                mergedInTurn = true,
                isNew = true,
                row = r,
                col = c
            )
            return MergeOutcome(newTile, scoreGained = 50, energyGained = totalEnergy, artifactForged = null)
        }

        // 3. Flame Forge Tile Merge
        if (source.type == TileType.FORGE_FLAME || target.type == TileType.FORGE_FLAME) {
            val newValue = maxOf(source.value, target.value) * 2
            val newTile = Tile(
                value = newValue,
                type = TileType.FORGE_FLAME,
                mergedInTurn = true,
                isNew = true,
                row = r,
                col = c
            )
            return MergeOutcome(newTile, scoreGained = (newValue * 2.5).toInt(), energyGained = 10, artifactForged = null)
        }

        // 4. Standard Matching Value Merge
        val newValue = source.value * 2
        var bonusEnergy = 5

        // Duplicator effect
        if (source.powerType == PowerType.DUPLICATOR || target.powerType == PowerType.DUPLICATOR) {
            val emptySlot = getRandomEmptySlot()
            if (emptySlot != null) {
                grid[emptySlot.first][emptySlot.second] = Tile(
                    value = newValue / 2,
                    type = TileType.STANDARD,
                    isNew = true,
                    row = emptySlot.first,
                    col = emptySlot.second
                )
                bonusEnergy += 10
            }
        }

        // Freeze effect (clears or neutralizes 1 obstacle if any)
        if (source.powerType == PowerType.FREEZE || target.powerType == PowerType.FREEZE) {
            for (or in 0 until size) {
                for (oc in 0 until size) {
                    if (grid[or][oc]?.type == TileType.OBSTACLE) {
                        grid[or][oc] = null
                        score += 50
                        break
                    }
                }
            }
        }

        val powerType = PowerType.NONE
        val baseScore = newValue
        val multiplier = if (source.powerType == PowerType.MULTIPLIER_2X || target.powerType == PowerType.MULTIPLIER_2X) 2 else 1

        val newTile = Tile(
            value = newValue,
            type = TileType.STANDARD,
            powerType = powerType,
            mergedInTurn = true,
            isNew = true,
            row = r,
            col = c
        )

        return MergeOutcome(newTile, scoreGained = baseScore * multiplier, energyGained = bonusEnergy, artifactForged = null)
    }

    private fun triggerBombAt(centerR: Int, centerC: Int) {
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = centerR + dr
                val nc = centerC + dc
                if (nr in 0 until size && nc in 0 until size) {
                    val tile = grid[nr][nc]
                    if (tile != null && tile.type != TileType.ARTIFACT) {
                        grid[nr][nc] = null
                        score += 25
                    }
                }
            }
        }
    }

    // Power Ability Actions
    fun shatterTile(r: Int, c: Int): Boolean {
        if (energy < 20) return false
        val tile = grid[r][c] ?: return false
        if (tile.type == TileType.ARTIFACT) return false
        grid[r][c] = null
        energy -= 20
        score += 30
        checkGameOverOrRescue()
        return true
    }

    fun duplicateTile(r: Int, c: Int): Boolean {
        if (energy < 40) return false
        val tile = grid[r][c] ?: return false
        if (tile.type == TileType.OBSTACLE || tile.type == TileType.ARTIFACT) return false
        val emptyPos = getAdjacentEmptyPosition(r, c) ?: getRandomEmptySlot() ?: return false

        grid[emptyPos.first][emptyPos.second] = tile.copy(
            id = UUID.randomUUID().toString(),
            isNew = true,
            row = emptyPos.first,
            col = emptyPos.second
        )
        energy -= 40
        highestTileValue = maxOf(highestTileValue, tile.value)
        return true
    }

    fun transmuteTile(r: Int, c: Int): Boolean {
        if (energy < 50) return false
        val tile = grid[r][c] ?: return false
        if (tile.type == TileType.ARTIFACT) return false

        val newType = if (rng.nextFloat() < 0.35f) TileType.FORGE_FLAME else TileType.STANDARD
        val newValue = if (tile.type == TileType.OBSTACLE) 4 else tile.value * 2

        grid[r][c] = Tile(
            id = UUID.randomUUID().toString(),
            value = newValue,
            type = newType,
            isNew = true,
            row = r,
            col = c
        )
        energy -= 50
        score += newValue
        highestTileValue = maxOf(highestTileValue, newValue)
        return true
    }

    fun activateIgnisEmberArtifact(): Boolean {
        var removed = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                val tile = grid[r][c]
                if (tile != null && tile.type == TileType.STANDARD && tile.value <= 4) {
                    grid[r][c] = null
                    removed++
                }
            }
        }
        score += removed * 20
        return removed > 0
    }

    private fun getRandomEmptySlot(): Pair<Int, Int>? {
        val emptySlots = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == null) {
                    emptySlots.add(Pair(r, c))
                }
            }
        }
        return if (emptySlots.isNotEmpty()) emptySlots[rng.nextInt(emptySlots.size)] else null
    }

    private fun getAdjacentEmptyPosition(r: Int, c: Int): Pair<Int, Int>? {
        val dirs = listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
        for (d in dirs) {
            val nr = r + d.first
            val nc = c + d.second
            if (nr in 0 until size && nc in 0 until size && grid[nr][nc] == null) {
                return Pair(nr, nc)
            }
        }
        return null
    }

    fun spawnRandomTile(): Boolean {
        val emptySlots = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == null) {
                    emptySlots.add(Pair(r, c))
                }
            }
        }

        if (emptySlots.isEmpty()) return false

        val slot = emptySlots[rng.nextInt(emptySlots.size)]
        val roll = rng.nextFloat()

        val tileToSpawn = when {
            roll < 0.60f -> Tile(value = if (rng.nextFloat() < 0.85f) 2 else 4, isNew = true, row = slot.first, col = slot.second)
            roll < 0.75f -> Tile(type = TileType.ENERGY_CRYSTAL, energyBonus = listOf(10, 20, 30)[rng.nextInt(3)], isNew = true, row = slot.first, col = slot.second)
            roll < 0.85f -> Tile(type = TileType.FORGE_ANVIL, isNew = true, row = slot.first, col = slot.second)
            roll < 0.93f -> Tile(type = TileType.FORGE_FLAME, value = 8, isNew = true, row = slot.first, col = slot.second)
            else -> {
                val powers = listOf(PowerType.FREEZE, PowerType.BOMB, PowerType.DUPLICATOR, PowerType.MULTIPLIER_2X)
                val power = powers[rng.nextInt(powers.size)]
                Tile(value = 4, powerType = power, isNew = true, row = slot.first, col = slot.second)
            }
        }

        grid[slot.first][slot.second] = tileToSpawn
        return true
    }

    private fun spawnTileAtRandomEmpty(tile: Tile): Boolean {
        val emptySlots = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == null) {
                    emptySlots.add(Pair(r, c))
                }
            }
        }
        if (emptySlots.isEmpty()) return false
        val slot = emptySlots[rng.nextInt(emptySlots.size)]
        grid[slot.first][slot.second] = tile.copy(row = slot.first, col = slot.second)
        return true
    }

    private fun salvageBoard(): Int {
        // Clears 3-4 lowest non-artifact tiles to rescue player and open grid slots!
        var cleared = 0
        val lowTiles = mutableListOf<Triple<Int, Int, Int>>() // row, col, value
        for (r in 0 until size) {
            for (c in 0 until size) {
                val tile = grid[r][c]
                if (tile != null && tile.type != TileType.ARTIFACT) {
                    lowTiles.add(Triple(r, c, if (tile.type == TileType.OBSTACLE) 0 else tile.value))
                }
            }
        }
        lowTiles.sortBy { it.third }
        for (item in lowTiles.take(4)) {
            grid[item.first][item.second] = null
            cleared++
        }
        return cleared
    }

    private fun checkGameOverOrRescue(): GameOverCheckResult {
        // If there is any empty slot, game is not over
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == null) return GameOverCheckResult(heartLost = false, wasRescued = false)
            }
        }

        // Check if any merges are possible
        for (r in 0 until size) {
            for (c in 0 until size) {
                val current = grid[r][c] ?: continue
                // Right neighbour
                if (c + 1 < size) {
                    val right = grid[r][c + 1]
                    if (right != null && canMerge(current, right)) return GameOverCheckResult(heartLost = false, wasRescued = false)
                }
                // Down neighbour
                if (r + 1 < size) {
                    val down = grid[r + 1][c]
                    if (down != null && canMerge(current, down)) return GameOverCheckResult(heartLost = false, wasRescued = false)
                }
            }
        }

        // If Aegis Shield is active, trigger shield save!
        if (hasAegisShield) {
            hasAegisShield = false
            salvageBoard()
            return GameOverCheckResult(heartLost = false, wasRescued = true)
        }

        // When messed up / stuck: Use heart rescue if player has hearts left!
        if (hearts > 1) {
            hearts--
            salvageBoard()
            isGameOver = false
            return GameOverCheckResult(heartLost = true, wasRescued = true)
        } else {
            hearts = 0
            isGameOver = true
            return GameOverCheckResult(heartLost = true, wasRescued = false)
        }
    }
}

data class GameOverCheckResult(
    val heartLost: Boolean,
    val wasRescued: Boolean
)

data class SlideResult(
    val moved: Boolean,
    val scoreGained: Int,
    val energyGained: Int,
    val mergesCount: Int,
    val artifactForged: ArtifactType?,
    val heartLost: Boolean = false,
    val heartsRemaining: Int = 3,
    val wasRescued: Boolean = false
)

data class MergeOutcome(
    val newTile: Tile,
    val scoreGained: Int,
    val energyGained: Int,
    val artifactForged: ArtifactType?
)

