package com.glureau.tigrou.ui.tab_input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.glureau.tigrou.sitemap.SitemapUrl
import com.glureau.tigrou.studyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UrlView(sitemapUrl: SitemapUrl, modifier: Modifier = Modifier) {
    Row(modifier) {
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var enabled by remember { mutableStateOf(sitemapUrl.enabled) }
        Checkbox(enabled, {
            coroutineScope.launch {
                sitemapUrl.enabled = !sitemapUrl.enabled
                studyRepository.onUpdate()
                enabled = sitemapUrl.enabled
            }
        }, Modifier.size(30.dp))
        val cleanUrl = sitemapUrl.loc
            .replace(Regex("https?://[^/]+/"), "…/")
            .removeSuffix("/")
        Text(cleanUrl, Modifier.align(Alignment.CenterVertically))
        val openBrowserHandler = LocalUriHandler.current
        IconButton({
            openBrowserHandler.openUri(sitemapUrl.loc)
        }, Modifier.size(30.dp)) {
            Icon(Icons.Default.Share, null)
        }

        Icon(if (sitemapUrl.htmlContentPath == null) Icons.Default.Search else Icons.Default.Check, null)
    }
}