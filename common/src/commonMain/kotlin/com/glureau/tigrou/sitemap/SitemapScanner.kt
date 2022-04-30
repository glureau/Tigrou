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
            return sitemapIndex.sitemaps.map { it.loc }
        } catch (t: Throwable) {
            println("[$baseUrl] - No sitemap_index.xml - ${t.message}")
        }

        try {
            val sitemapXml = download("$baseUrl/sitemap.xml")
            val urlSet = deserializer.parseUrlSet(sitemapXml)
            return urlSet.url.map { it.loc }
        } catch (t: Throwable) {
            println("[$baseUrl] - No sitemap.xml - ${t.message}")
        }

        println("[$baseUrl] - Cannot scan this site...")
        return emptyList()
    }

    private suspend fun download(url: String): String {
        val response: HttpResponse = client.get(url)
        return response.body()
    }
}
