package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moodtracker.databinding.ActivitySurveyBinding

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var prefsHelper: prefshelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = prefshelper(this)

        binding.btnSubmitSurvey.setOnClickListener {
            if (validateAnswers()) {
                val responses = collectResponses()
                val score = calculateScore()
                val mood = getMoodFromScore(score)

                // Save the complete entry with responses
                prefsHelper.saveMoodEntry(mood, responses, score)

                val intent = Intent(this, RecommendationsActivity::class.java)
                intent.putExtra("mood", mood)
                intent.putExtra("score", score)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateAnswers(): Boolean {
        return binding.rgQuestion1.checkedRadioButtonId != -1 &&
                binding.rgQuestion2.checkedRadioButtonId != -1 &&
                binding.rgQuestion3.checkedRadioButtonId != -1 &&
                binding.rgQuestion4.checkedRadioButtonId != -1
    }

    private fun collectResponses(): Map<String, String> {
        val responses = mutableMapOf<String, String>()

        // Question 1: How are you feeling today?
        when (binding.rgQuestion1.checkedRadioButtonId) {
            R.id.rb_q1_great -> responses["feeling"] = "Great! 😊"
            R.id.rb_q1_good -> responses["feeling"] = "Good 🙂"
            R.id.rb_q1_okay -> responses["feeling"] = "Okay 😐"
            R.id.rb_q1_bad -> responses["feeling"] = "Not good 😔"
        }

        // Question 2: Energy level
        when (binding.rgQuestion2.checkedRadioButtonId) {
            R.id.rb_q2_high -> responses["energy"] = "High Energy ⚡"
            R.id.rb_q2_medium -> responses["energy"] = "Medium Energy 🔋"
            R.id.rb_q2_low -> responses["energy"] = "Low Energy 🪫"
            R.id.rb_q2_very_low -> responses["energy"] = "Very Low 😴"
        }

        // Question 3: Stress level
        when (binding.rgQuestion3.checkedRadioButtonId) {
            R.id.rb_q3_none -> responses["stress"] = "Not stressed 😌"
            R.id.rb_q3_little -> responses["stress"] = "A little stressed 😕"
            R.id.rb_q3_moderate -> responses["stress"] = "Moderately stressed 😰"
            R.id.rb_q3_high -> responses["stress"] = "Very stressed 😫"
        }

        // Question 4: Social interaction
        when (binding.rgQuestion4.checkedRadioButtonId) {
            R.id.rb_q4_love -> responses["social"] = "Love socializing 🥳"
            R.id.rb_q4_enjoy -> responses["social"] = "Enjoy it 😊"
            R.id.rb_q4_neutral -> responses["social"] = "Neutral 😐"
            R.id.rb_q4_avoid -> responses["social"] = "Want to avoid it 😔"
        }

        return responses
    }

    private fun calculateScore(): Int {
        var score = 0

        // Question 1: How are you feeling today?
        when (binding.rgQuestion1.checkedRadioButtonId) {
            R.id.rb_q1_great -> score += 4
            R.id.rb_q1_good -> score += 3
            R.id.rb_q1_okay -> score += 2
            R.id.rb_q1_bad -> score += 1
        }

        // Question 2: Energy level
        when (binding.rgQuestion2.checkedRadioButtonId) {
            R.id.rb_q2_high -> score += 4
            R.id.rb_q2_medium -> score += 3
            R.id.rb_q2_low -> score += 2
            R.id.rb_q2_very_low -> score += 1
        }

        // Question 3: Stress level
        when (binding.rgQuestion3.checkedRadioButtonId) {
            R.id.rb_q3_none -> score += 4
            R.id.rb_q3_little -> score += 3
            R.id.rb_q3_moderate -> score += 2
            R.id.rb_q3_high -> score += 1
        }

        // Question 4: Social interaction
        when (binding.rgQuestion4.checkedRadioButtonId) {
            R.id.rb_q4_love -> score += 4
            R.id.rb_q4_enjoy -> score += 3
            R.id.rb_q4_neutral -> score += 2
            R.id.rb_q4_avoid -> score += 1
        }

        return score
    }

    private fun getMoodFromScore(score: Int): String {
        return when {
            score >= 14 -> "Excellent"
            score >= 11 -> "Good"
            score >= 8 -> "Fair"
            else -> "Needs Improvement"
        }
    }
}