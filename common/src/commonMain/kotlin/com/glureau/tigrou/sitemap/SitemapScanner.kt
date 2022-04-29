package com.glureau.tigrou.sitemap

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class SitemapScanner() {
    private val sitemapNames = listOf(
        "sitemap.xml",
        "index_sitemap.xml",
    )

    suspend fun scan(baseUrl: String): List<String> {
        sitemapNames.forEach { sitemap ->
            val res = scanSitemap("$baseUrl/$sitemap")
            if (res.isNotEmpty()) return res
        }
        // Filter all unrelated files (finishing by .png, .svg, ...) before returning
        return emptyList()
    }

    private suspend fun scanSitemap(url: String): List<String> {
        val client = HttpClient()
        val response: HttpResponse = client.get("https://ktor.io/")

        println("response: ${response.body<String>()}")
        TODO()
        // Read XML
        // returns all available urls
        // BONUS : returns urls with the last modification time if possible!
    }
}