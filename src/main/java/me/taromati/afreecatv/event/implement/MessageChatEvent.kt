package me.taromati.afreecatv.event.implement

import me.taromati.afreecatv.event.AfreecatvEvent

class MessageChatEvent(
    val channelId: String? = null,
    val nickname: String? = null,
    val message: String? = null,
) : AfreecatvEvent
