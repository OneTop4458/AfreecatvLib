package me.taromati.afreecatv

import me.taromati.afreecatv.data.AfreecatvInfo
import org.java_websocket.drafts.Draft_6455
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AfreecatvSocketPacketTest {
    @Test
    fun makeLengthPacketFormatsLength() {
        val socket = createSocket()
        val data = "hello"
        val expected = String.format("%06d00", data.length)

        val length = invokePrivate<String>(socket, "makeLengthPacket", data)

        assertEquals(expected, length)
    }

    @Test
    fun makePacketIncludesCommandLengthAndData() {
        val socket = createSocket()
        val command = "0001"
        val data = "hello"
        val esc = getPrivateField<String>(socket, "ESC")
        val length = invokePrivate<String>(socket, "makeLengthPacket", data)

        val packet = invokePrivate<String>(socket, "makePacket", command, data)

        assertEquals("$esc$command$length$data", packet)
    }

    private fun createSocket(): AfreecatvSocket {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()
        val info = AfreecatvInfo(
            channelDomain = "example.com",
            channelNumber = "1",
            channelPt = "443",
            streamerId = "bj"
        )
        return AfreecatvSocket(
            api,
            "wss://example.com:443/Websocket/bj",
            Draft_6455(),
            info,
            "bj"
        )
    }

    private fun <T> invokePrivate(instance: Any, name: String, vararg args: Any): T {
        val argTypes = args.map { it.javaClass }.toTypedArray()
        val method = instance.javaClass.getDeclaredMethod(name, *argTypes)
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(instance, *args) as T
    }

    private fun <T> getPrivateField(instance: Any, name: String): T {
        val field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(instance) as T
    }
}
