package com.glureau.tigrou.sitemap

import kotlinx.serialization.Serializable


// https://www.sitemaps.org/protocol.html
@Serializable
data class SitemapIndex(
    val sitemaps: List<Sitemap>
)

@Serializable
data class Sitemap(
    val loc: String,
    val lastmod: String? = null,
) {
    var urlSet: SitemapUrlSet? = null
}

@Serializable
data class SitemapUrlSet(
    val urls: List<SitemapUrl>
)

@Serializable
data class SitemapUrl(
    val loc: String,
    val lastmod: String? = null,
    val changefreq: String? = null,
    val priority: String? = null,
    val image: List<SitemapImage> = emptyList(),
) {
    var enabled: Boolean = true
    var htmlContentPath: String? = null
    var markdownContentPath: String? = null
}


// https://www.google.com/schemas/sitemap-image/1.1/sitemap-image.xsd
@Serializable
data class SitemapImage(
    val loc: String,
    val title: String? = null,
    val geo_location: String? = null,
    val caption: String? = null,
    val license: String? = null,
)
