package com.glureau.tigrou.domain

import com.glureau.tigrou.sitemap.SitemapIndex
import com.glureau.tigrou.sitemap.SitemapUrl
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
) : Listenable() {
    fun allSitemapUrl(): List<SitemapUrl> {
        val urls = mutableListOf<SitemapUrl>()
        // Using copy to avoid concurrent modifications
        val sitesCopy = sites.toList()
        sitesCopy.forEach { site ->
            val sitemapsCopy = site.sitemapIndex?.sitemaps?.toList()
            sitemapsCopy?.forEach { sitemap ->
                sitemap.urlSet?.urls?.forEach { urls.add(it) }
            }

            site.sitemapUrlSet?.urls?.forEach { urls.add(it) }
        }
        return urls
    }
}

@Serializable
data class Site(
    val baseUrl: String,
) : Listenable() {
    var sitemapIndex: SitemapIndex? = null

    // If no index only! see [SitemapIndex.urlSets]
    var sitemapUrlSet: SitemapUrlSet? = null
}
