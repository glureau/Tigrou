package com.glureau.tigrou.scanner

import com.glureau.tigrou.domain.Site
import com.glureau.tigrou.sitemap.SitemapScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SiteScanner {
    private val sitemapScanner = SitemapScanner()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    suspend fun scan(baseUrl: String): Site {
        val site = Site(baseUrl)

        coroutineScope.launch {
            sitemapScanner.scan(site)
        }
        //val downloaded = HtmlDownloader().download(allUrls)
        // downloaded.size != allUrls -> Cannot finish the work (probably blocked by security) will be retried later...
        return site
    }


    suspend fun updateSite(site: Site) {
        coroutineScope.launch {
            sitemapScanner.scan(site)
        }
    }
}