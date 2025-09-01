package com.example.app.models

import androidx.lifecycle.ViewModel
import com.example.app.R

// ---------------- Video Data ----------------
data class VideoItem(
    val id: Int,
    val title: String,
    val channel: String,
    val views: String,
    val time: String,
    val thumbnail: Int
)

data class Comment(
    val user: String,
    val text: String
)

// ---------------- Filters ----------------
val videoFilters = listOf("All", "Farming", "Irrigation", "Fertilizers", "Poultry", "Diseases")

// ---------------- ViewModel ----------------
class VideoViewModel : ViewModel() {

    // Video feed
    private val _videos = listOf(
        VideoItem(1, "How to Plant Maize", "AgriTech Kenya", "12k views", "2 days ago", R.drawable.ic_launcher_background),
        VideoItem(2, "Irrigation Tips for Dry Season", "Farm Pro", "8.5k views", "1 week ago", R.drawable.ic_launcher_background),
        VideoItem(3, "Best Fertilizers for Beans", "Green Farm", "5.3k views", "3 days ago", R.drawable.ic_launcher_background),
        VideoItem(4, "Modern Poultry Housing", "Smart Farming", "20k views", "1 month ago", R.drawable.ic_launcher_background),
        VideoItem(5, "How to Prevent Crop Diseases", "Healthy Crops", "15k views", "5 days ago", R.drawable.ic_launcher_background)
    )
    val videos: List<VideoItem> get() = _videos

    // Comments
    private val _comments = listOf(
        Comment("Alice", "Great tutorial! Learned a lot."),
        Comment("Bob", "Thanks for sharing, very helpful."),
        Comment("Charlie", "Can you make a video on organic fertilizers?"),
        Comment("Daisy", "Loved the explanation, clear and concise."),
        Comment("Eve", "More videos like this please!")
    )
    val comments: List<Comment> get() = _comments

    // Get video by id
    fun getVideoById(id: Int): VideoItem? = _videos.find { it.id == id }

    // Related videos (exclude current)
    fun getRelatedVideos(currentId: Int): List<VideoItem> =
        _videos.filter { it.id != currentId }

    // Filter videos
    fun getFilteredVideos(filter: String): List<VideoItem> {
        return if (filter == "All") _videos
        else _videos.filter {
            it.title.contains(filter, ignoreCase = true) ||
                    it.channel.contains(filter, ignoreCase = true)
        }
    }
}
