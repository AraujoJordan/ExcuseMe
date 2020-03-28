package com.araujo.jordan.excuseme

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val reasons = listOf("first", "second", "third", "fourth")
        var reasonsFormated = ""

        when (reasons.size) {
            1 -> reasonsFormated.first()
            else -> {
                reasons.forEachIndexed { index, s ->
                    reasonsFormated += when (index) {
                        reasons.size - 2 -> "$s and "
                        reasons.size - 1 -> s
                        else -> "$s, "
                    }
                }
            }
        }

        print(reasonsFormated)
        assertEquals(4, 2 + 2)
    }
}
