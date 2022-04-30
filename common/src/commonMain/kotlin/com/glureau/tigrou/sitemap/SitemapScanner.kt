package com.glureau.tigrou.sitemap

import com.glureau.tigrou.domain.Site
import com.glureau.tigrou.xml.XmlDeserializer
import com.glureau.tigrou.xml.provideXmlDeserializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

private val client = HttpClient()

class SitemapScanner(
    private val deserializer: XmlDeserializer = provideXmlDeserializer()
) {

    suspend fun scan(site: Site) {
        try {
            if (site.sitemapIndex == null) {
                val sitemapIndex = deserializer.parseSitemapIndex(download("${site.baseUrl}/sitemap_index.xml"))
                site.sitemapIndex = sitemapIndex
                site.notifyUpdate()
            }
            site.sitemapIndex!!.sitemaps.forEach {
                if (it.urlSet == null) {
                    it.urlSet = scanSitemap(it.loc)
                    site.notifyUpdate()
                }
            }
            return
        } catch (t: Throwable) {
            println("[${site.baseUrl}] - No sitemap_index.xml - $t")
            t.printStackTrace()
        }

        // If there is no sitemap index, it's possible that only one sitemap.xml is available:
        try {
            site.sitemapUrlSet = scanSitemap("${site.baseUrl}/sitemap.xml")
            site.notifyUpdate()
            return
        } catch (t: Throwable) {
            println("[${site.baseUrl}] - No sitemap.xml - $t")
            t.printStackTrace()
        }

        println("[${site.baseUrl}] - Cannot scan this site...")
    }

    private suspend fun scanSitemap(url: String): SitemapUrlSet {
        val sitemapXml = download(url)
        return deserializer.parseUrlSet(sitemapXml).also {
            println(it)
        }
    }

    private suspend fun download(url: String): String {
        val response: HttpResponse = client.get(url)
        return response.body()
    }
}
