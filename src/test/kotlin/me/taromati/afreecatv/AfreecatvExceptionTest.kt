package me.taromati.afreecatv

import me.taromati.afreecatv.exception.AfreecatvException
import me.taromati.afreecatv.exception.ExceptionCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AfreecatvExceptionTest {
    @Test
    fun exposesCodeAndMessage() {
        val code = ExceptionCode.API_CHAT_CHANNEL_ID_ERROR
        val exception = AfreecatvException(code)

        assertEquals(code.code, exception.code)
        assertEquals(code.message, exception.message)
    }
}
