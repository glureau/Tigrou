package com.glureau.tigrou.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Collapsable(header: @Composable () -> Unit, content: @Composable () -> Unit) {
    var collapsed by remember { mutableStateOf(true) }
    Row(Modifier.clickable {
        collapsed = !collapsed
    }) {
        IconToggleButton(collapsed, { collapsed = it }, Modifier.size(30.dp)) {
            Icon(if (collapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, null)
        }
        header()
    }
    if (!collapsed) {
        content()
    }
}