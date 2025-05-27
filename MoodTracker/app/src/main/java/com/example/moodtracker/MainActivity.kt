package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moodtracker.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var moodPostAdapter: MoodPostAdapter
    private var selectedMoodButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPrefsHelper(this)

        setupMoodButtons()
        setupCommunitySection()
        setupBottomNavigation()
        loadMoodPosts()
    }

    private fun setupMoodButtons() {
        val moodButtons = listOf(
            binding.btnMoodGreat,
            binding.btnMoodGood,
            binding.btnMoodOkay,
            binding.btnMoodSad
        )

        moodButtons.forEach { button ->
            button.setOnClickListener {
                // Reset previous selection
                selectedMoodButton?.isSelected = false

                // Set current selection
                selectedMoodButton = button
                button.isSelected = true

                val mood = when (button.id) {
                    R.id.btnMoodGreat -> "😊"
                    R.id.btnMoodGood -> "🙂"
                    R.id.btnMoodOkay -> "😐"
                    R.id.btnMoodSad -> "😔"
                    else -> "😐"
                }

                saveMoodSelection(mood)
            }
        }

        // Handle detailed survey button
        binding.btnTakeFullSurvey.setOnClickListener {
            startActivity(Intent(this, SurveyActivity::class.java))
        }
    }

    private fun setupCommunitySection() {
        // Setup RecyclerView for mood posts
        moodPostAdapter = MoodPostAdapter(emptyList())
        binding.rvMoodPosts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = moodPostAdapter
            isNestedScrollingEnabled = false
        }

        // Handle add mood post button
        binding.btnAddMoodPost.setOnClickListener {
            toggleAddMoodLayout()
        }

        // Handle share mood button
        binding.btnShareMood.setOnClickListener {
            shareMoodPost()
        }

        // Handle cancel post button
        binding.btnCancelPost.setOnClickListener {
            cancelMoodPost()
        }
    }

    private fun loadMoodPosts() {
        val moodPosts = prefsHelper.getMoodPosts()
        moodPostAdapter.updatePosts(moodPosts)
    }

    private fun toggleAddMoodLayout() {
        if (binding.layoutAddMood.visibility == View.GONE) {
            binding.layoutAddMood.visibility = View.VISIBLE
            binding.btnAddMoodPost.text = "Cancel"
        } else {
            binding.layoutAddMood.visibility = View.GONE
            binding.btnAddMoodPost.text = "+ Share"
            binding.etMoodPost.setText("")
        }
    }

    private fun shareMoodPost() {
        val moodText = binding.etMoodPost.text.toString().trim()

        if (moodText.isNotEmpty()) {
            // Get current mood or default to neutral
            val currentMood = selectedMoodButton?.let {
                when (it.id) {
                    R.id.btnMoodGreat -> "😊"
                    R.id.btnMoodGood -> "🙂"
                    R.id.btnMoodOkay -> "😐"
                    R.id.btnMoodSad -> "😔"
                    else -> "😐"
                }
            } ?: "😐"

            // Create new mood post
            val newMoodPost = MoodPost(
                id = UUID.randomUUID().toString(),
                userId = prefsHelper.getUserDisplayName(),
                moodEmoji = currentMood,
                moodText = moodText,
                timestamp = System.currentTimeMillis()
            )

            // Save to preferences
            prefsHelper.addMoodPost(newMoodPost)

            // Update adapter
            moodPostAdapter.addPost(newMoodPost)

            // Hide layout and clear text
            binding.layoutAddMood.visibility = View.GONE
            binding.btnAddMoodPost.text = "+ Share"
            binding.etMoodPost.setText("")

            Toast.makeText(this, "Mood shared successfully!", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Please enter your mood first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelMoodPost() {
        binding.layoutAddMood.visibility = View.GONE
        binding.btnAddMoodPost.text = "+ Share"
        binding.etMoodPost.setText("")
    }

    private fun saveMoodSelection(mood: String) {
        prefsHelper.saveMood(mood)
        Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, MoodHistoryActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh mood posts when returning to the activity
        loadMoodPosts()
    }
}