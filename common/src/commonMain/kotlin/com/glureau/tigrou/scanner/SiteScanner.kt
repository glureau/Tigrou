package com.glureau.tigrou.scanner

import com.glureau.tigrou.domain.Site
import com.glureau.tigrou.sitemap.SitemapScanner

class SiteScanner {
    private val sitemapScanner = SitemapScanner()

    suspend fun scan(baseUrl: String): Site {
        val allUrls = sitemapScanner.scan(baseUrl)

        TODO()
        //val downloaded = HtmlDownloader().download(allUrls)
        // downloaded.size != allUrls -> Cannot finish the work (probably blocked by security) will be retried later...
    }
}