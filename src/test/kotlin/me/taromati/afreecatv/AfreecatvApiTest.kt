package me.taromati.afreecatv

import me.taromati.afreecatv.data.AfreecatvInfo
import me.taromati.afreecatv.event.implement.DonationChatEvent
import me.taromati.afreecatv.event.implement.MessageChatEvent
import me.taromati.afreecatv.listener.AfreecatvListener
import org.java_websocket.drafts.Draft_6455
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AfreecatvApiTest {
    @Test
    fun builderSetsChannelId() {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()

        assertEquals("bj", getPrivateField<String>(api, "channelId"))
    }

    @Test
    fun addListenersReturnsSameInstance() {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()
        val listener = object : AfreecatvListener {}

        val result = api.addListeners(listOf(listener))

        assertSame(api, result)
        assertEquals(1, api.listeners.size)
    }

    @Test
    fun dispatchesChatEventsToListeners() {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()
        var messageEvent: MessageChatEvent? = null
        var donationEvent: DonationChatEvent? = null
        val listener = object : AfreecatvListener {
            override fun onMessageChat(e: MessageChatEvent?) {
                messageEvent = e
            }

            override fun onDonationChat(e: DonationChatEvent?) {
                donationEvent = e
            }
        }

        api.addListeners(listOf(listener))
        val msg = MessageChatEvent(channelId = "bj", nickname = "nick", message = "hi")
        val donation = DonationChatEvent(
            channelId = "bj",
            userId = "user",
            nickname = "nick",
            message = "thanks",
            payAmount = 100,
            balloonAmount = 1
        )

        api.onMessageChat(msg)
        api.onDonationChat(donation)

        assertSame(msg, messageEvent)
        assertSame(donation, donationEvent)
    }

    @Test
    fun disconnectClearsState() {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()
        val socket = createSocket(api, "bj")
        setPrivateField(api, "socket", socket)

        val result = api.disconnect()

        assertSame(api, result)
        assertFalse(api.isConnected)
        assertEquals(null, getPrivateField<String?>(api, "channelId"))
        assertEquals(null, getPrivateField<Any?>(api, "socket"))
    }

    @Test
    fun isConnectedReflectsSocketPresence() {
        val api = AfreecatvAPI.AfreecatvBuilder().withData("bj").build()
        val socket = createSocket(api, "bj")

        assertFalse(api.isConnected)
        setPrivateField(api, "socket", socket)
        assertTrue(api.isConnected)
    }

    private fun createSocket(api: AfreecatvAPI, channelId: String): AfreecatvSocket {
        val info = AfreecatvInfo(
            channelDomain = "example.com",
            channelNumber = "1",
            channelPt = "443",
            streamerId = channelId
        )
        return AfreecatvSocket(
            api,
            "wss://example.com:443/Websocket/$channelId",
            Draft_6455(),
            info,
            channelId
        )
    }

    private fun setPrivateField(instance: Any, name: String, value: Any?) {
        val field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.set(instance, value)
    }

    private fun <T> getPrivateField(instance: Any, name: String): T? {
        val field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(instance) as T?
    }
}
