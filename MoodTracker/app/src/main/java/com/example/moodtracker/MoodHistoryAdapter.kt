package com.example.moodtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MoodHistoryAdapter(
    private val onItemClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodHistoryAdapter.MoodHistoryViewHolder>() {

    private var entries = listOf<MoodEntry>()

    fun updateEntries(newEntries: List<MoodEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_history, parent, false)
        return MoodHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodHistoryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size

    inner class MoodHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardMoodEntry)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvMood: TextView = itemView.findViewById(R.id.tvMood)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvFeeling: TextView = itemView.findViewById(R.id.tvFeeling)
        private val tvEnergy: TextView = itemView.findViewById(R.id.tvEnergy)
        private val tvStress: TextView = itemView.findViewById(R.id.tvStress)
        private val tvSocial: TextView = itemView.findViewById(R.id.tvSocial)

        fun bind(entry: MoodEntry) {
            tvDate.text = entry.date
            tvTime.text = entry.time
            tvMood.text = entry.mood
            tvScore.text = "${entry.score}/16"

            // Set mood responses
            tvFeeling.text = "Feeling: ${entry.responses["feeling"] ?: "N/A"}"
            tvEnergy.text = "Energy: ${entry.responses["energy"] ?: "N/A"}"
            tvStress.text = "Stress: ${entry.responses["stress"] ?: "N/A"}"
            tvSocial.text = "Social: ${entry.responses["social"] ?: "N/A"}"

            // Set mood color based on score
            val moodColor = when {
                entry.score >= 14 -> R.color.mood_very_happy
                entry.score >= 11 -> R.color.mood_happy
                entry.score >= 8 -> R.color.mood_neutral
                else -> R.color.mood_sad
            }

            tvMood.setTextColor(ContextCompat.getColor(itemView.context, moodColor))

            cardView.setOnClickListener {
                onItemClick(entry)
            }
        }
    }
}