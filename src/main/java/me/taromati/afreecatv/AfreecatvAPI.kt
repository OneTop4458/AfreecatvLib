package me.taromati.afreecatv

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import lombok.Getter
import me.taromati.afreecatv.data.AfreecatvInfo
import me.taromati.afreecatv.data.AfreecatvLiveInfo
import me.taromati.afreecatv.event.implement.DonationChatEvent
import me.taromati.afreecatv.event.implement.MessageChatEvent
import me.taromati.afreecatv.exception.AfreecatvException
import me.taromati.afreecatv.exception.ExceptionCode
import me.taromati.afreecatv.listener.AfreecatvListener
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.protocols.IProtocol
import org.java_websocket.protocols.Protocol
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

@Getter
class AfreecatvAPI(private var channelId: String?) {
    private var socket: AfreecatvSocket? = null

    val listeners: MutableList<AfreecatvListener> = ArrayList()

    fun connect(): AfreecatvAPI {
        val info = try {
            val info = getInfo(this.channelId)
            if(!info.isLive) {
                disconnect()
                return this
            }
            info
        } catch (e: Exception) {
            this.channelId = null
            this.socket = null
            return this
        }

        if (!isConnected) {
            try {
                val draft6455 = Draft_6455(
                    emptyList(),
                    listOf<IProtocol>(Protocol("chat"))
                )
                val webSocket = AfreecatvSocket(
                    this,
                    "wss://" + info.channelDomain + ":" + info.channelPt + "/Websocket/" + info.streamerId,
                    draft6455,
                    info,
                    channelId!!
                )
                webSocket.connect()
                this.socket = webSocket
                return this
            } catch (e: Exception) {
                this.channelId = null
                this.socket = null
                return this
            }
        }
        return this
    }

    fun disconnect(): AfreecatvAPI {
        if (this.socket != null) {
            socket!!.close()
            this.socket = null
            this.channelId = null
        }
        return this
    }

    fun addListeners(listeners: List<AfreecatvListener>): AfreecatvAPI {
        this.listeners.addAll(listeners)
        return this
    }

    fun onMessageChat(e: MessageChatEvent?) {
        listeners.forEach { listener -> listener.onMessageChat(e) }
    }

    fun onDonationChat(e: DonationChatEvent?) {
        listeners.forEach { listener -> listener.onDonationChat(e) }
    }

    val isConnected: Boolean
        get() = socket != null && !socket!!.isClosed

    class AfreecatvBuilder {
        private var channelId: String? = null

        fun withData(channelId: String?): AfreecatvBuilder {
            this.channelId = channelId
            return this
        }

        fun build(): AfreecatvAPI {
            return createAPI(this.channelId)
        }
    }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        private val PLAYER_LIVE_API_BASES = listOf(
            "https://live.sooplive.co.kr",
            "https://live.afreecatv.com",
        )

        fun getLiveInfo(bjId: String): AfreecatvLiveInfo {
            try {
                val channel = fetchChannel(bjId)
                val categoryTags: MutableList<String> = ArrayList()
                for (s in JSONParser().parse(channel["CATEGORY_TAGS"].toString()) as JSONArray) {
                    categoryTags.add(s.toString())
                }
                return AfreecatvLiveInfo(
                    channel["BJID"].toString(),
                    channel["BJNICK"].toString(),
                    channel["TITLE"].toString(),
                    categoryTags
                )
            } catch (e: Exception) {
                throw AfreecatvException(ExceptionCode.API_CHAT_CHANNEL_ID_ERROR)
            }
        }

        fun createAPI(channelId: String?): AfreecatvAPI {
            return AfreecatvAPI(channelId)
        }

        private fun getInfo(bjId: String?): AfreecatvInfo {
            try {
                val channel = fetchChannel(bjId ?: "")
                return AfreecatvInfo(
                    channel["CHDOMAIN"].takeIf { it != "null" }?.toString(),
                    channel["CHATNO"].takeIf { it != "null" }?.toString(),
                    (channel["CHPT"].takeIf { it != "null" }?.toString()?.toInt()?.plus(1))?.toString(),
                    channel["FTK"].takeIf { it != "null" }?.toString(),
                    channel["TITLE"].takeIf { it != "null" }?.toString(),
                    channel["BJID"].takeIf { it != "null" }?.toString(),
                    channel["BNO"].takeIf { it != "null" }?.toString()
                )
            } catch (e: Exception) {
                throw AfreecatvException(ExceptionCode.API_CHAT_CHANNEL_ID_ERROR)
            }
        }

        private fun fetchChannel(bjId: String): JSONObject {
            for (baseUrl in PLAYER_LIVE_API_BASES) {
                try {
                    val response = sendPlayerLiveRequest(bjId, baseUrl)
                    if (response.statusCode() != 200) {
                        continue
                    }
                    val jsonObject = JSONParser().parse(response.body()) as JSONObject
                    return jsonObject["CHANNEL"] as JSONObject
                } catch (e: Exception) {
                    continue
                }
            }
            throw AfreecatvException(ExceptionCode.API_CHAT_CHANNEL_ID_ERROR)
        }

        private fun sendPlayerLiveRequest(bjId: String, baseUrl: String): HttpResponse<String> {
            val request = HttpRequest.newBuilder()
                .POST(formData(buildPlayerLiveBody(bjId)))
                .uri(URI.create("$baseUrl/afreeca/player_live_api.php?bjid=$bjId"))
                .headers(
                    "User-Agent",
                    USER_AGENT,
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                .build()
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
        }

        private fun buildPlayerLiveBody(bjId: String): Map<String, String?> {
            return mapOf(
                "bid" to bjId,
                "type" to "live",
                "confirm_adult" to "false",
                "player_type" to "html5",
                "mode" to "landing",
                "from_api" to "0",
                "pwd" to "",
                "stream_type" to "common",
                "quality" to "HD",
            )
        }

        private fun formData(data: Map<String, String?>): BodyPublisher {
            val builder = StringBuilder()
            for (pair in data) {
                builder.apply {
                    if (isNotEmpty()) {
                        append("&")
                    }
                    append(pair.key)
                    append("=")
                    append(urlEncode(pair.value ?: ""))
                }
            }
            return HttpRequest.BodyPublishers.ofString(builder.toString())
        }

        private fun urlEncode(data: String): String {
            return URLEncoder.encode(data, StandardCharsets.UTF_8)
        }
    }
}
