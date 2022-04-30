package com.glureau.tigrou.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glureau.tigrou.domain.Site
import com.glureau.tigrou.ui.Collapsable
import com.glureau.tigrou.ui.UrlGroupView
import com.glureau.tigrou.ui.humanUrl

@Composable
fun SiteView(site: Site) {
    Column {
        Collapsable({
            Text("Site: ${site.baseUrl.humanUrl()}", fontSize = 20.sp)
        }, {
            Column(Modifier.padding(start = 8.dp)) {
                site.sitemapIndex?.sitemaps?.forEach { sitemap ->
                    sitemap.urlSet?.let {
                        UrlGroupView(sitemap.loc.humanUrl(), it.urls)
                    }
                }

                site.sitemapUrlSet?.urls?.let {
                    UrlGroupView("sitemap", it)
                }
            }
        })
    }
}