package com.please.data.models.seller

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String,
    val content: String,
    val sender: MessageSender,
    val timestamp: Date = Date(),
    val actionButtons: List<String> = emptyList()
) {
    fun getFormattedTime(): String {
        val formatter = SimpleDateFormat("a h:mm", Locale.KOREAN)
        return formatter.format(timestamp)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatMessage) return false

        return id == other.id &&
                content == other.content &&
                sender == other.sender &&
                timestamp.time == other.timestamp.time &&
                actionButtons == other.actionButtons
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + sender.hashCode()
        result = 31 * result + timestamp.time.hashCode()
        result = 31 * result + actionButtons.hashCode()
        return result
    }
}


enum class MessageSender {
    USER, CHATBOT
}