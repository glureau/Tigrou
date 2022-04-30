package com.glureau.tigrou.sitemap

import com.glureau.tigrou.xml.XmlDeserializer
import com.glureau.tigrou.xml.provideXmlDeserializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class SitemapScanner(
    private val deserializer: XmlDeserializer = provideXmlDeserializer()
) {

    private val client = HttpClient()

    suspend fun scan(baseUrl: String): List<String> {
        try {
            val sitemapIndex = deserializer.parseSitemapIndex(download("$baseUrl/sitemap_index.xml"))
            return sitemapIndex.sitemaps.flatMap { scanSitemap(it.loc) }
        } catch (t: Throwable) {
            println("[$baseUrl] - No sitemap_index.xml - $t")
            t.printStackTrace()
        }

        // If there is no sitemap index, it's possible that only one sitemap.xml is available:
        try {
            return scanSitemap("$baseUrl/sitemap.xml")
        } catch (t: Throwable) {
            println("[$baseUrl] - No sitemap.xml - $t")
            t.printStackTrace()
        }

        println("[$baseUrl] - Cannot scan this site...")
        return emptyList()
    }

    private suspend fun scanSitemap(url: String): List<String> {
        val sitemapXml = download(url)
        val urlSet = deserializer.parseUrlSet(sitemapXml)
        return urlSet.url.map { it.loc }
    }

    private suspend fun download(url: String): String {
        val response: HttpResponse = client.get(url)
        return response.body()
    }
}
