package me.taromati.afreecatv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AfreecatvCallbackTest {
    @Test
    fun parsesCommandAndDataList() {
        val args = arrayOf("0005000123", "hello", "user", "extra")
        val callback = AfreecatvCallback(args)

        assertEquals("0005", callback.command)
        assertEquals(listOf("hello", "user", "extra"), callback.dataList)
    }
}
