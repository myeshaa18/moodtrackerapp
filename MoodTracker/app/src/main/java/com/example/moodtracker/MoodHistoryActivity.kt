package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodtracker.databinding.ActivityMoodHistoryBinding

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodHistoryBinding
    private lateinit var prefsHelper: prefshelper
    private lateinit var historyAdapter: MoodHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = prefshelper(this)
        setupRecyclerView()
        setupBottomNavigation()
        loadMoodHistory()
        updateStats()

        binding.btnTakeSurvey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadMoodHistory()
        updateStats()
    }

    private fun setupRecyclerView() {
        historyAdapter = MoodHistoryAdapter { moodEntry ->
            // Handle item click - could show details
            showEntryDetails(moodEntry)
        }

        binding.rvMoodHistory.apply {
            layoutManager = LinearLayoutManager(this@MoodHistoryActivity)
            adapter = historyAdapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_history

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadMoodHistory() {
        val entries = prefsHelper.getMoodEntries().sortedByDescending { it.timestamp }

        if (entries.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvMoodHistory.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvMoodHistory.visibility = View.VISIBLE
            historyAdapter.updateEntries(entries)
        }
    }

    private fun updateStats() {
        val entries = prefsHelper.getMoodEntries()
        val streak = prefsHelper.getMoodStreak()

        binding.tvTotalEntries.text = entries.size.toString()
        binding.tvCurrentStreak.text = streak.toString()
    }

    private fun showEntryDetails(moodEntry: MoodEntry) {
        // You can implement a detail dialog or activity here
        // For now, we'll just show a toast with basic info
        android.widget.Toast.makeText(
            this,
            "Mood: ${moodEntry.mood}\nScore: ${moodEntry.score}/16\n${moodEntry.date} at ${moodEntry.time}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}