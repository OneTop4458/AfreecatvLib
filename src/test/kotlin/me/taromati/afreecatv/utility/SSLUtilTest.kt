package me.taromati.afreecatv.utility

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class SSLUtilTest {
    @Test
    fun createsSocketFactory() {
        val factory = SSLUtil.createSSLSocketFactory()
        assertNotNull(factory)
    }
}
