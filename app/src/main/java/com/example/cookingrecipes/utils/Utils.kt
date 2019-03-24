package com.example.cookingrecipes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.util.regex.Pattern
import javax.inject.Inject


class Utils @Inject constructor(private val context: Context) {

    fun isConnectedToInternet(): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
        }
        return false
    }

    companion object {

        fun getYoutubeThumbnailUrlFromVideoUrl(videoUrl: String): String {
            return "https://img.youtube.com/vi/" + getYoutubeVideoIdFromUrl(videoUrl) + "/0.jpg"
        }


        fun getYoutubeVideoIdFromUrl(inUrl: String): String? {
            var inUrl = inUrl
            inUrl = inUrl.replace("&feature=youtu.be", "")
            if (inUrl.toLowerCase().contains("youtu.be")) {
                return inUrl.substring(inUrl.lastIndexOf("/") + 1)
            }
            val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(inUrl)
            return if (matcher.find()) {
                matcher.group()
            } else null
        }
    }
}