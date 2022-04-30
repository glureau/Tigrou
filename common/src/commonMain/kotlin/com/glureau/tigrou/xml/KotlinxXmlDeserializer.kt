package com.glureau.tigrou.xml

import com.glureau.tigrou.sitemap.SitemapIndex
import com.glureau.tigrou.sitemap.SitemapUrlSet

class KotlinxXmlDeserializer : XmlDeserializer {
    override fun parseSitemapIndex(data: String): SitemapIndex {
        TODO("Not yet implemented")
    }

    override fun parseUrlSet(data: String): SitemapUrlSet {
        TODO("Not yet implemented")
    }
}

/*

// https://www.sitemaps.org/protocol.html
/*
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
   <url>
      <loc>http://www.example.com/</loc>
      <lastmod>2005-01-01</lastmod>
      <changefreq>monthly</changefreq>
      <priority>0.8</priority>
   </url>
</urlset>
 */
@Serializable
@SerialName("urlset")
data class UrlSet(
    val url: List<Url>
)

@Serializable
@SerialName("url")
data class Url(
    @XmlElement(true)
    val loc: String,
    @XmlElement(true)
    val lastmod: String? = null,
    @XmlElement(true)
    val changefreq: String? = null,
    @XmlElement(true)
    val priority: String? = null,
    @XmlElement(true)
    val image: Image? = null,
)

@Serializable
@XmlSerialName("image", "http://www.google.com/schemas/sitemap-image/1.1", "image")//<image:image>
//@SerialName("image")
data class Image(
    @XmlElement(true)
    @XmlSerialName("loc", "http://www.google.com/schemas/sitemap-image/1.1", "image")
    val loc: String,

    @XmlElement(true)
    @XmlSerialName("title", "http://www.google.com/schemas/sitemap-image/1.1", "image")
    val title: String? = null,

    @XmlElement(true)
    @XmlSerialName("caption", "http://www.google.com/schemas/sitemap-image/1.1", "image")
    val caption: String? = null,
)

// Or
/*
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	<sitemap>
		<loc>https://www.parcduluberon.fr/post-sitemap.xml</loc>
		<lastmod>2022-04-28T17:21:35+00:00</lastmod>
	</sitemap>
 */

@Serializable
@XmlSerialName(value = "sitemapindex", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9", prefix = "")
data class SitemapIndex(
    val sitemaps: List<Sitemap>
)

@Serializable
@SerialName("sitemap")
data class Sitemap(
    @XmlElement(true)
    val loc: String,
    @XmlElement(true)
    val lastmod: String? = null,
)

 */