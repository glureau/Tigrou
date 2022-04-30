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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.glureau.tigrou.study.StudyRepository
import com.glureau.tigrou.ui.ui.SiteView
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
        }

        LazyColumn {
            items(study!!.sites) { site ->
                SiteView(site)
            }
        }
    }
}

