package com.example.moodtracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodtracker.databinding.ItemMoodPostBinding

class MoodPostAdapter(private var moodPosts: List<MoodPost>) :
    RecyclerView.Adapter<MoodPostAdapter.MoodPostViewHolder>() {

    inner class MoodPostViewHolder(private val binding: ItemMoodPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moodPost: MoodPost) {
            binding.apply {
                // Set mood emoji and background color
                tvMoodEmoji.text = moodPost.moodEmoji
                try {
                    tvMoodEmoji.setBackgroundColor(Color.parseColor(moodPost.getMoodColor()))
                } catch (e: IllegalArgumentException) {
                    tvMoodEmoji.setBackgroundColor(Color.GRAY)
                }

                // Set user info
                tvUsername.text = moodPost.userId
                tvTimestamp.text = moodPost.getTimeAgo()

                // Set mood text
                tvMoodText.text = moodPost.moodText

                // Handle admin response
                if (moodPost.hasAdminResponse && moodPost.adminResponseText.isNotEmpty()) {
                    layoutAdminResponse.visibility = View.VISIBLE
                    tvAdminResponse.text = moodPost.adminResponseText
                } else {
                    layoutAdminResponse.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodPostViewHolder {
        val binding = ItemMoodPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodPostViewHolder, position: Int) {
        holder.bind(moodPosts[position])
    }

    override fun getItemCount(): Int = moodPosts.size

    fun updatePosts(newPosts: List<MoodPost>) {
        moodPosts = newPosts
        notifyDataSetChanged()
    }

    fun addPost(moodPost: MoodPost) {
        val updatedList = moodPosts.toMutableList()
        updatedList.add(0, moodPost) // Add to beginning of list
        moodPosts = updatedList
        notifyItemInserted(0)
    }
}