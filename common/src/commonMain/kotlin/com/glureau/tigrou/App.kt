package com.glureau.tigrou

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TriStateCheckbox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glureau.tigrou.domain.Site
import com.glureau.tigrou.sitemap.SitemapUrl
import com.glureau.tigrou.study.StudyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


val studyRepository = StudyRepository()

@Composable
fun App() {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val study by studyRepository.studyFlow.collectAsState(null)
    if (study == null) return

    Column {
        Column(Modifier.padding(4.dp)) {
            Text("Sites de l'étude:", fontSize = 24.sp)
            Column(Modifier.border(width = 2.dp, Color.Gray, RoundedCornerShape(4.dp))) {
                study!!.sites.forEach {
                    Row {
                        Text("- " + it.baseUrl, Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp))
                        IconButton(
                            { coroutineScope.launch { studyRepository.removeSite(it.baseUrl) } }, Modifier.padding(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }


                Row(Modifier.padding(top = 10.dp)) {
                    var newSiteUrl by remember { mutableStateOf("") }
                    TextField(value = newSiteUrl, onValueChange = { newSiteUrl = it }, Modifier.fillMaxWidth(0.5f))
                    IconButton({
                        coroutineScope.launch {
                            studyRepository.addSite(newSiteUrl)
                            newSiteUrl = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, "Add")
                    }
                }
            }
        }

        LazyColumn {
            items(study!!.sites) { site ->
                SiteView(site)
            }
        }
    }
}

@Composable
fun SiteView(site: Site) {
    Column {
        Collapsable({
            Text("Site: ${site.baseUrl.humanUrl()}", fontSize = 20.sp)
        }, {
            Column(Modifier.padding(start = 8.dp)) {
                if (site.sitemapIndex != null) {
                    Text("From sitemap_index.xml:")
                }
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

@Composable
fun UrlGroupView(groupName: String, urls: List<SitemapUrl>) {
    Collapsable({
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        val allEnabled = urls.all { it.enabled }
        val allDisabled = urls.none { it.enabled }
        var triState by remember {
            mutableStateOf(
                when {
                    allEnabled -> ToggleableState.On
                    allDisabled -> ToggleableState.Off
                    else -> ToggleableState.Indeterminate
                }
            )
        }
        Row {
            TriStateCheckbox(triState, {
                val shouldEnable = triState != ToggleableState.On
                urls.forEach {
                    it.enabled = shouldEnable
                }
                coroutineScope.launch {
                    studyRepository.onUpdate()
                }
                triState = if (shouldEnable) ToggleableState.On else ToggleableState.Off
            }, Modifier.size(30.dp))
            Text(groupName, Modifier.align(Alignment.CenterVertically))
        }
    }, {
        Column(Modifier.padding(start = 40.dp)) {
            urls.forEach { sitemapUrl ->
                UrlView(sitemapUrl)
            }
        }
    })
}

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
        Text(sitemapUrl.loc, Modifier.align(Alignment.CenterVertically))
        val openBrowserHandler = LocalUriHandler.current
        IconButton({
            openBrowserHandler.openUri(sitemapUrl.loc)
        }, Modifier.size(30.dp)) {
            Icon(Icons.Default.Share, null)
        }
    }
}

@Composable
fun Collapsable(header: @Composable () -> Unit, content: @Composable () -> Unit) {
    var collapsed by remember { mutableStateOf(true) }
    Row {
        IconToggleButton(collapsed, { collapsed = it }, Modifier.size(30.dp)) {
            Icon(if (collapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, null)
        }
        header()
    }
    if (!collapsed) {
        content()
    }
}

private fun String.humanUrl(): String =
    this.removePrefix("http://").removePrefix("https://").removePrefix("www.").removeSuffix("/index.html")
        .removeSuffix("/")
        .substringAfterLast("/")
