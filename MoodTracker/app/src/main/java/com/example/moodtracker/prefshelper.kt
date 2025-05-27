package com.example.moodtracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

data class MoodEntry(
    val mood: String,
    val date: String,
    val time: String,
    val responses: Map<String, String>,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class prefshelper(context: Context) {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("MoodTrackerPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveMoodEntry(mood: String, responses: Map<String, String>, score: Int) {
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val moodEntry = MoodEntry(
            mood = mood,
            date = currentDate,
            time = currentTime,
            responses = responses,
            score = score
        )

        val existingEntries = getMoodEntries().toMutableList()
        existingEntries.add(moodEntry)

        val json = gson.toJson(existingEntries)
        sharedPrefs.edit().putString("mood_entries", json).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = sharedPrefs.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getMoodHistory(): List<String> {
        val entries = getMoodEntries()
        return entries.map { entry ->
            "${entry.date} at ${entry.time}\nMood: ${entry.mood}\nScore: ${entry.score}/16"
        }
    }

    fun clearHistory() {
        sharedPrefs.edit().remove("mood_entries").apply()
    }

    fun getLastMoodEntry(): MoodEntry? {
        val entries = getMoodEntries()
        return entries.maxByOrNull { it.timestamp }
    }

    fun getMoodStreak(): Int {
        val entries = getMoodEntries().sortedByDescending { it.timestamp }
        if (entries.isEmpty()) return 0

        var streak = 1
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var currentDate = dateFormat.format(Date(entries[0].timestamp))

        for (i in 1 until entries.size) {
            val entryDate = dateFormat.format(Date(entries[i].timestamp))
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = entries[i-1].timestamp
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val expectedDate = dateFormat.format(calendar.time)

            if (entryDate == expectedDate) {
                streak++
            } else {
                break
            }
        }

        return streak
    }
}