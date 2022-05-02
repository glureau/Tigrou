package com.glureau.tigrou.ui.tab_input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.studyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun InputPage(study: Study) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Column {
        Column(Modifier.padding(4.dp)) {
            Text("Sites de l'étude:", fontSize = 24.sp)
            Row {
                Column(Modifier.border(width = 2.dp, Color.Gray, RoundedCornerShape(4.dp))) {
                    study.sites.forEach {
                        Row {
                            Text("- " + it.baseUrl, Modifier.align(Alignment.CenterVertically).padding(start = 8.dp))
                            IconButton(
                                { coroutineScope.launch { studyRepository.removeSite(it.baseUrl) } },
                                Modifier.size(40.dp)
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
                Column {
                    var downloadJob by remember { mutableStateOf<Job?>(null) }
                    val activeDownload = downloadJob?.isActive == true
                    Button({
                        if (!activeDownload) {
                            downloadJob = studyRepository.startDownloadWebPages()
                        } else {
                            downloadJob?.cancel()
                        }
                    }, Modifier.padding(start = 8.dp)) {
                        Icon(if (!activeDownload) Icons.Default.PlayArrow else Icons.Default.Clear, null)
                        Text(if (!activeDownload) "Télécharger les pages" else "Annuler")
                    }

                    val progress by studyRepository.progressFlow.collectAsState(0 to 0)
                    if (progress != 0 to 0) {
                        val progressF = progress.first.toFloat() / progress.second
                        val percent = (progressF * 100).toInt()
                        val decimalPrecision = 10
                        val percentDecimals = (progressF * 100 * decimalPrecision).toInt() - percent * decimalPrecision
                        Text("Progression: ${progress.first}/${progress.second} ($percent.$percentDecimals%)")
                        LinearProgressIndicator(progressF)
                        Text(
                            "Pourquoi c'est si lent ? Pour éviter de créer des soucis sur les sites webs / se faire bloquer pendant le scan...\n" +
                                    "As-tu penser à décocher les pages qui ne sont pas pertinentes pour te faire gagner du temps ?"
                        )
                    }
                }
            }
        }

        LazyColumn {
            items(study.sites) { site ->
                SiteView(site)
            }
        }
    }
}