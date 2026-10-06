package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.GameBoard
import com.example.game.SwipeDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tile Forge", appName)
  }

  @Test
  fun `daily quest seeded board is deterministic`() {
    val board1 = GameBoard()
    board1.resetBoard(seed = 20260828L, isDailyQuest = true)

    val board2 = GameBoard()
    board2.resetBoard(seed = 20260828L, isDailyQuest = true)

    for (r in 0 until 4) {
      for (c in 0 until 4) {
        assertEquals(board1.grid[r][c]?.value, board2.grid[r][c]?.value)
      }
    }
  }

  @Test
  fun `fantasy quotes repository returns valid quotes`() {
    val quotes = com.example.game.FantasyQuotes.quotes
    assertTrue(quotes.isNotEmpty())
    val randomQuote = com.example.game.FantasyQuotes.getRandomQuote()
    assertTrue(randomQuote.quote.isNotBlank())
    assertTrue(randomQuote.speaker.isNotBlank())
    assertTrue(randomQuote.source.isNotBlank())
  }

  @Test
  fun `tutorial custom grid and slide movement works`() {
    val board = GameBoard().apply { preventRandomSpawns = true }
    board.setupCustomGrid(
      listOf(com.example.game.Tile(value = 2, row = 1, col = 0))
    )
    val result = board.slide(SwipeDirection.RIGHT)
    assertTrue(result.moved)
    assertEquals(2, board.grid[1][3]?.value)
  }

  @Test
  fun `tutorial combine numbers doubles value`() {
    val board = GameBoard().apply { preventRandomSpawns = true }
    board.setupCustomGrid(
      listOf(
        com.example.game.Tile(value = 2, row = 1, col = 0),
        com.example.game.Tile(value = 2, row = 1, col = 2)
      )
    )
    val result = board.slide(SwipeDirection.RIGHT)
    assertTrue(result.moved)
    assertEquals(1, result.mergesCount)
    assertEquals(4, board.grid[1][3]?.value)
  }

  @Test
  fun `tutorial energy crystal merge grants energy`() {
    val board = GameBoard().apply { preventRandomSpawns = true }
    board.setupCustomGrid(
      listOf(
        com.example.game.Tile(type = com.example.game.TileType.ENERGY_CRYSTAL, energyBonus = 25, row = 2, col = 1),
        com.example.game.Tile(type = com.example.game.TileType.ENERGY_CRYSTAL, energyBonus = 25, row = 2, col = 3)
      )
    )
    val result = board.slide(SwipeDirection.RIGHT)
    assertTrue(result.moved)
    assertTrue(result.energyGained >= 50)
    assertTrue(board.energy >= 50)
  }

  @Test
  fun `tutorial forge anvil creates artifact`() {
    val board = GameBoard().apply { preventRandomSpawns = true }
    board.setupCustomGrid(
      listOf(
        com.example.game.Tile(type = com.example.game.TileType.FORGE_ANVIL, row = 1, col = 1),
        com.example.game.Tile(value = 8, row = 1, col = 3)
      )
    )
    val result = board.slide(SwipeDirection.RIGHT)
    assertTrue(result.moved)
    assertTrue(result.artifactForged != null)
    assertEquals(com.example.game.TileType.ARTIFACT, board.grid[1][3]?.type)
  }
}
