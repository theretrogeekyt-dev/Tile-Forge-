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
}
