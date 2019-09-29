package net.thauvin.erik.android.tesremoteprogrammer.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringsTest {

    @Test
    fun isDigits() {
        assertTrue("0-9", "0123456789".isDigits())
        assertFalse("a", "a".isDigits())
        assertFalse("1.3", "1.3".isDigits())
        assertFalse("empty", "".isDigits())
        assertFalse("blank", "    ".isDigits())
    }

    @Test
    fun isDKS() {
        assertTrue(Dtmf.DKS.isDKS())
        assertFalse(Dtmf.LINEAR.isDKS())
    }

    @Test
    fun isLinear() {
        assertTrue(Dtmf.LINEAR.isLinear())
        assertFalse(Dtmf.DKS.isLinear())
    }

    @Test
    fun ifNull() {
        val n: String? = null
        assertEquals("null", n.ifNull(), "")
        assertEquals("test", "test".ifNull(), "test")
    }

    @Test
    fun replaceAll() {
        val s = "This is a test."
        val r = arrayOf(
            Pair("i", ""),
            Pair("is", "no"),
            Pair("TiT", "no"),
            Pair("t", "T"),
            Pair(".", "?"),
            Pair(" ", "-"),
            Pair("es", "i")
        )

        assertEquals(s.replaceAll(r), "Ths-s-a-TiT?")
    }
}
