package com.glureau.tigrou.ui.tab_analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glureau.tigrou.analytics.Analytics
import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.ui.components.simpleVerticalScrollbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AnalyticsPage(study: Study) {
    var analyticsInProgress by remember { mutableStateOf(false) }
    var wordCount by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect("init") {
        coroutineScope.launch(Dispatchers.Default) {
            wordCount = Analytics().wordCount(study)
            analyticsInProgress = false
            if (study.ignoredWords.isEmpty()) {
                study.notifyUpdate()
            }
        }
    }

    // TODO: the isEmpty check is bad, to be reworked
    if (analyticsInProgress || wordCount.isEmpty()) {
        Column(Modifier.fillMaxSize()) {
            Text("Analyse en cours...", Modifier.padding(top = 100.dp).align(Alignment.CenterHorizontally))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        return
    }

    Row(Modifier.padding(4.dp)) {
        Column {
            Text("Mots par occurrences")
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.simpleVerticalScrollbar(listState).width(250.dp).fillMaxHeight(0.6f),
                state = listState
            ) {
                wordCount.filter { (word, _) -> !study.ignoredWords.contains(word) }.forEach {
                    item(it) {
                        WordView(study, it.first, it.second, true)
                    }
                }
            }

            Text("Mots ignorés")
            val ignoredlistState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.simpleVerticalScrollbar(ignoredlistState).width(250.dp), state = ignoredlistState
            ) {
                wordCount.filter { (word, _) -> study.ignoredWords.contains(word) }.forEach {
                    item(it) {
                        WordView(study, it.first, it.second, false)
                    }
                }
            }
        }
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.background(Color.LightGray).fillMaxWidth().height(300.dp)) // Future word animation
            Column {
                var exportWordCount by remember { mutableStateOf(20) }
                Row {
                    Text("Nombre de mots à exporter")
                    TextField(exportWordCount.toString(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = { input ->
                            exportWordCount = input.filter { it in '0'..'9' }.toIntOrNull() ?: 0
                        })
                }
                val exportedWords =
                    wordCount.filter { (word, _) -> !study.ignoredWords.contains(word) }.take(exportWordCount)
                val firstExportedWordCount = exportedWords.first().second
                val lastExportedWordCount = exportedWords.last().second
                val text: String =
                    exportedWords.map { (word, count) -> word to (((count - lastExportedWordCount) * 100f) / firstExportedWordCount).toInt() }
                        .flatMap { (word, count) -> List(count) { word } }.joinToString(" ")
                val clipboard = LocalClipboardManager.current
                TextField(text, {}, readOnly = true, modifier = Modifier.fillMaxWidth().height(200.dp).clickable {
                    clipboard.setText(AnnotatedString(text))
                })
            }
        }
    }
}

@Composable
private fun WordView(study: Study, word: String, count: Int, visible: Boolean) {
    Card(Modifier.padding(bottom = 4.dp)) {
        Row {
            val coroutineScope = rememberCoroutineScope()
            IconButton({
                if (visible) study.ignoredWords.add(word)
                else study.ignoredWords.remove(word)
                coroutineScope.launch {
                    study.notifyUpdate()
                }
            }, Modifier.size(26.dp).padding(start = 8.dp)) {
                if (visible) Icon(Icons.Default.Visibility, "visible")
                else Icon(Icons.Default.VisibilityOff, "not visible")
            }
            Text(
                word,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically).weight(1f)
            )
            Text(
                count.toString(),
                overflow = TextOverflow.Visible,
                maxLines = 1,
                modifier = Modifier.padding(start = 4.dp, end = 8.dp).align(Alignment.CenterVertically)
            )
        }
    }
}