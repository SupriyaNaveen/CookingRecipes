package com.example.cookingrecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Utility class has generic functions.
 */
@Suppress("DEPRECATION")
class Utils @Inject constructor(private val context: Context) {

    /**
     * Checks whether phone is connected or not.
     */
    fun isConnectedToInternet(): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        if (info != null)
            for (i in info.indices)
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
        return false
    }

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