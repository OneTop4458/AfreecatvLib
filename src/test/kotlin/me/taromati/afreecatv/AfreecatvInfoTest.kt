package me.taromati.afreecatv

import me.taromati.afreecatv.data.AfreecatvInfo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AfreecatvInfoTest {
    @Test
    fun isLiveFalseWhenDomainMissing() {
        val info = AfreecatvInfo()
        assertFalse(info.isLive)
    }

    @Test
    fun isLiveTrueWhenDomainPresent() {
        val info = AfreecatvInfo(channelDomain = "live.sooplive.co.kr")
        assertTrue(info.isLive)
    }
}
