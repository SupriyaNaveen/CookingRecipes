package com.example.cookingrecipes.utils

import android.content.Context
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Utility class has generic functions.
 */
class Utils @Inject constructor(private val context: Context) {

    companion object {

        /**
         * Get youtube thumbnail image URL.
         */
        fun getYoutubeThumbnailUrlFromVideoUrl(videoUrl: String): String {
            return "https://img.youtube.com/vi/" + getYoutubeVideoIdFromUrl(videoUrl) + "/0.jpg"
        }

        fun getYoutubeVideoIdFromUrl(url: String): String? {
            var inUrl = url
            inUrl = inUrl.replace("&feature=youtu.be", "")
            if (inUrl.toLowerCase().contains("youtu.be")) {
                return inUrl.substring(inUrl.lastIndexOf("/") + 1)
            }
            val pattern = "(?<=watch\\?v=|/videos/|embed/)[^#&?]*"
            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(inUrl)
            return if (matcher.find()) {
                matcher.group()
            } else null
        }
    }
}