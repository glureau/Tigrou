package com.glureau.tigrou

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.study.StudyRepository
import com.glureau.tigrou.ui.tab_analytics.AnalyticsPage
import com.glureau.tigrou.ui.tab_input.InputPage


val studyRepository = StudyRepository()

enum class Screen(val icon: ImageVector, val label: String, val content: @Composable (study: Study) -> Unit) {
    Sites(Icons.Default.FormatListBulleted, "Données", { InputPage(it) }),
    Filter(Icons.Default.FilterList, "Filtres", { InputPage(it) }),
    Analytics(Icons.Default.QueryStats, "Résultats", { AnalyticsPage(it) }),
}

@Composable
fun App() {
    val study by studyRepository.studyFlow.collectAsState(null)
    if (study == null) return

    var screenState by remember { mutableStateOf(Screen.Sites) }
    Row {
        MenuBar(screenState) { screenState = it }
        Column {
            screenState.content(study!!)
        }
    }
}


@Composable
fun MenuBar(screenState: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationRail {
        Screen.values().forEach { screenItem ->
            NavigationRailItem(
                selected = screenState == screenItem,
                onClick = { onScreenSelected(screenItem) },
                icon = { Icon(screenItem.icon, null) },
                label = { Text(screenItem.label) }
            )
        }
    }
}

