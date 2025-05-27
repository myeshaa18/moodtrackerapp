package com.example.moodtracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MoodTracker", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_MOOD_POSTS = "mood_posts"
        private const val KEY_CURRENT_MOOD = "current_mood"
        private const val KEY_LAST_MOOD_DATE = "last_mood_date"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_MOOD_HISTORY = "mood_history"
    }

    // ========== USER AUTHENTICATION ==========
    fun saveUser(username: String, password: String) {
        prefs.edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    fun validateLogin(username: String, password: String): Boolean {
        return prefs.getString(KEY_USERNAME, "") == username &&
                prefs.getString(KEY_PASSWORD, "") == password
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    fun updateUsername(newUsername: String) {
        prefs.edit().putString(KEY_USERNAME, newUsername).apply()
    }

    // ========== MOOD HISTORY ==========
    fun saveMoodEntry(mood: String, date: String) {
        val entries = getMoodHistory().toMutableList()
        entries.add("$date: $mood")
        val entriesString = entries.joinToString("|")
        prefs.edit().putString(KEY_MOOD_HISTORY, entriesString).apply()
    }

    fun getMoodHistory(): List<String> {
        val entriesString = prefs.getString(KEY_MOOD_HISTORY, "") ?: ""
        return if (entriesString.isEmpty()) emptyList() else entriesString.split("|")
    }

    // ========== SETTINGS ==========
    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    // ========== MOOD FUNCTIONALITY ==========

    // Current Mood (for quick mood selection)
    fun saveMood(mood: String) {
        val currentTime = System.currentTimeMillis()
        prefs.edit()
            .putString(KEY_CURRENT_MOOD, mood)
            .putLong(KEY_LAST_MOOD_DATE, currentTime)
            .apply()

        // Also save to existing mood history format
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        val formattedDate = dateFormat.format(java.util.Date(currentTime))
        saveMoodEntry(mood, formattedDate)
    }

    fun getCurrentMood(): String {
        return prefs.getString(KEY_CURRENT_MOOD, "") ?: ""
    }

    fun getLastMoodDate(): Long {
        return prefs.getLong(KEY_LAST_MOOD_DATE, 0L)
    }

    // Community Mood Posts
    fun saveMoodPosts(posts: List<MoodPost>) {
        val postsJson = gson.toJson(posts)
        prefs.edit().putString(KEY_MOOD_POSTS, postsJson).apply()
    }

    fun getMoodPosts(): List<MoodPost> {
        val postsJson = prefs.getString(KEY_MOOD_POSTS, "[]") ?: "[]"
        val type = object : TypeToken<List<MoodPost>>() {}.type
        return try {
            gson.fromJson(postsJson, type) ?: getSampleMoodPosts()
        } catch (e: Exception) {
            getSampleMoodPosts()
        }
    }

    fun addMoodPost(moodPost: MoodPost) {
        val currentPosts = getMoodPosts().toMutableList()
        // Use actual username if available, otherwise keep "Anonymous User"
        val postWithUsername = moodPost.copy(
            userId = if (getUsername().isNotEmpty()) getUsername() else "Anonymous User"
        )
        currentPosts.add(0, postWithUsername) // Add to beginning

        // Keep only last 50 posts
        if (currentPosts.size > 50) {
            currentPosts.removeAt(currentPosts.size - 1)
        }

        saveMoodPosts(currentPosts)
    }

    // Helper Methods
    fun getUserDisplayName(): String {
        val username = getUsername()
        return if (username.isNotEmpty()) username else "Anonymous User"
    }

    // Sample data for initial load
    private fun getSampleMoodPosts(): List<MoodPost> {
        return listOf(
            MoodPost(
                id = "1",
                userId = "Anonymous User",
                moodEmoji = "😔",
                moodText = "Feeling overwhelmed with work today. Sometimes it's hard to find balance between everything.",
                timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
                hasAdminResponse = true,
                adminResponseText = "Remember to take breaks and practice self-care. You're doing great! 💙"
            ),
            MoodPost(
                id = "2",
                userId = "Anonymous User",
                moodEmoji = "😊",
                moodText = "Had an amazing morning walk today! The fresh air really helps clear my mind. 🌱",
                timestamp = System.currentTimeMillis() - (5 * 60 * 60 * 1000) // 5 hours ago
            ),
            MoodPost(
                id = "3",
                userId = "Anonymous User",
                moodEmoji = "😐",
                moodText = "Just an average day. Nothing particularly good or bad happened.",
                timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000), // 1 day ago
                hasAdminResponse = true,
                adminResponseText = "Average days are okay too! Maybe try something small that brings you joy? ✨"
            )
        )
    }

    // Data Management
    fun clearMoodData() {
        prefs.edit()
            .remove(KEY_MOOD_POSTS)
            .remove(KEY_CURRENT_MOOD)
            .remove(KEY_LAST_MOOD_DATE)
            .remove(KEY_MOOD_HISTORY)
            .apply()
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}