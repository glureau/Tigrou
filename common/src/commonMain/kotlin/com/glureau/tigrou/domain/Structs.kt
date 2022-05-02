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
    val ignoredWords: MutableList<String> = mutableListOf(
        "un", "une", "le", "la", "les", "au", "du", "a", "de", "des", "aux", "d'un", "d'une",
        "mais", "ou", "et", "donc", "or", "ni", "car", "y",
        "son", "sa", "ses", "ton", "ta", "tes", "cet", "cette", "notre",
        "est", "es", "ai", "dans", "pour", "plus", "en", "faire",
        "sur", "sous", "dessus", "dessous", "avec", "par",
        "je", "tu", "il", "elle", "nous", "vous", "ils", "elles",
        "se", "votre", "sont", "qui", "ce", "que", "c'est", "tout", "ont",
        "liens", "sites", "charte", "logo", "tél", "contactez-nous", "accueil", "plan", "and", "in", "newsletter",
        "more", "read", "mailto", "the",
        "http",
    ),
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
