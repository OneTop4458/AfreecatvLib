package me.taromati.afreecatv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AfreecatvCallbackTest {
    @Test
    fun parsesCommandAndDataList() {
        val args = arrayOf("0005000123", "hello", "user", "extra")
        val callback = AfreecatvCallback(args)

        assertEquals("0005", callback.command)
        assertEquals(listOf("hello", "user", "extra"), callback.dataList)
    }

    @Test
    fun toStringIncludesCommandAndData() {
        val args = arrayOf("0018000061", "donation", "user", "nick")
        val callback = AfreecatvCallback(args)

        val output = callback.toString()
        assertTrue(output.contains("Command: 0018"))
        assertTrue(output.contains("donation"))
        assertTrue(output.contains("user"))
        assertTrue(output.contains("nick"))
    }
}
