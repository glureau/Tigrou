package com.glureau.tigrou.sitemap


// https://www.sitemaps.org/protocol.html
data class SitemapIndex(
    val sitemaps: List<Sitemap>
)

data class Sitemap(
    val loc: String,
    val lastmod: String? = null,
)

data class SitemapUrlSet(
    val url: List<SitemapUrl>
)

data class SitemapUrl(
    val loc: String,
    val lastmod: String? = null,
    val changefreq: String? = null,
    val priority: String? = null,
    val image: List<SitemapImage> = emptyList(),
)

// https://www.google.com/schemas/sitemap-image/1.1/sitemap-image.xsd
data class SitemapImage(
    val loc: String,
    val title: String? = null,
    val geo_location: String? = null,
    val caption: String? = null,
    val license: String? = null,
)
