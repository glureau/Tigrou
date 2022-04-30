package com.glureau.tigrou.xml

import com.glureau.tigrou.sitemap.SitemapIndex
import com.glureau.tigrou.sitemap.SitemapUrlSet

interface XmlDeserializer {
    fun parseSitemapIndex(data: String): SitemapIndex
    fun parseUrlSet(data: String): SitemapUrlSet
}

expect fun provideXmlDeserializer(): XmlDeserializer