package com.example.moodtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moodtracker.databinding.ActivityRecommendationsBinding


class RecommendationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mood = intent.getStringExtra("mood") ?: "Fair"

        binding.tvMoodResult.text = "Your mood today: $mood"

        val recommendations = getRecommendations(mood)
        setupRecommendations(recommendations)

        binding.btnBackHome.setOnClickListener {
            finish()
        }
    }

    private fun getRecommendations(mood: String): List<Pair<String, String>> {
        return when (mood) {
            "Excellent" -> listOf(
                "🎉 Share your joy" to "Call a friend and share your happiness",
                "🏃‍♂️ Stay active" to "Go for a run or workout",
                "📚 Learn something" to "Read a book or take an online course",
                "🎨 Be creative" to "Try painting, writing, or music",
                "🌱 Help others" to "Volunteer or help a neighbor",
                "🧘‍♀️ Practice gratitude" to "Write in a gratitude journal"
            )
            "Good" -> listOf(
                "☀️ Get sunlight" to "Spend time outdoors",
                "👥 Socialize" to "Meet friends or family",
                "🍎 Eat healthy" to "Prepare a nutritious meal",
                "🎵 Listen to music" to "Play your favorite songs",
                "📖 Read a book" to "Dive into a good story",
                "🚶‍♀️ Take a walk" to "Explore your neighborhood"
            )
            "Fair" -> listOf(
                "😊 Practice self-care" to "Take a warm bath or shower",
                "🫖 Drink tea" to "Make yourself a calming herbal tea",
                "📱 Limit screen time" to "Take a break from social media",
                "🌸 Connect with nature" to "Water plants or sit in a garden",
                "📞 Call someone" to "Reach out to a loved one",
                "✍️ Journal" to "Write down your thoughts"
            )
            else -> listOf(
                "🛌 Rest well" to "Get adequate sleep tonight",
                "🧘‍♀️ Try meditation" to "Use a meditation app for 10 minutes",
                "🤗 Seek support" to "Talk to someone you trust",
                "🍵 Stay hydrated" to "Drink plenty of water",
                "🌅 Watch sunrise" to "Wake up early and watch the sunrise",
                "💆‍♀️ Relax" to "Do gentle stretching or yoga"
            )
        }
    }

    private fun setupRecommendations(recommendations: List<Pair<String, String>>) {
        binding.tvRec1Title.text = recommendations[0].first
        binding.tvRec1Desc.text = recommendations[0].second

        binding.tvRec2Title.text = recommendations[1].first
        binding.tvRec2Desc.text = recommendations[1].second

        binding.tvRec3Title.text = recommendations[2].first
        binding.tvRec3Desc.text = recommendations[2].second

        binding.tvRec4Title.text = recommendations[3].first
        binding.tvRec4Desc.text = recommendations[3].second

        binding.tvRec5Title.text = recommendations[4].first
        binding.tvRec5Desc.text = recommendations[4].second

        binding.tvRec6Title.text = recommendations[5].first
        binding.tvRec6Desc.text = recommendations[5].second
    }
}