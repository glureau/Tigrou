package com.glureau.tigrou.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.glureau.tigrou.sitemap.SitemapUrl
import com.glureau.tigrou.studyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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