package com.glureau.tigrou.domain

import com.glureau.tigrou.sitemap.SitemapIndex
import com.glureau.tigrou.sitemap.SitemapUrlSet
import kotlinx.serialization.Serializable

fun interface UpdateListener {
    suspend fun onUpdate()
}

abstract class Listenable {
    var listener: UpdateListener? = null
    suspend fun notifyUpdate() {
        listener?.onUpdate()
    }
}

@Serializable
data class Study(
    //var studyName: String = "study_" + Random.nextInt().toString(32),
    var studyName: String = "study_0",
    val sites: MutableList<Site> = mutableListOf(),
    val ignoredWords: MutableList<String> = mutableListOf(),
) : Listenable()

@Serializable
data class Site(
    val baseUrl: String,
) : Listenable() {
    var sitemapIndex: SitemapIndex? = null

    // If no index only! see [SitemapIndex.urlSets]
    var sitemapUrlSet: SitemapUrlSet? = null
}

data class InternalUrl(
    val url: String,
) {
    var enabled: Boolean = true
}
