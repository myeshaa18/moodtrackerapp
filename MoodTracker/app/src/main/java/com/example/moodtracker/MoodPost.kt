package com.example.moodtracker

data class MoodPost(
    val id: String = "",
    val userId: String = "Anonymous User",
    val moodEmoji: String,
    val moodText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val hasAdminResponse: Boolean = false,
    val adminResponseText: String = ""
) {
    fun getTimeAgo(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            else -> "A week ago"
        }
    }

    fun getMoodColor(): String {
        return when (moodEmoji) {
            "😊" -> "#4CAF50" // Green for very happy
            "🙂" -> "#8BC34A" // Light green for happy
            "😐" -> "#FFC107" // Yellow for neutral
            "😔" -> "#FF9800" // Orange for sad
            else -> "#9E9E9E" // Gray for unknown
        }
    }
}