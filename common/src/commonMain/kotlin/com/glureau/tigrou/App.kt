package com.glureau.tigrou

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.glureau.tigrou.scanner.SiteScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Button(onClick = {
        text = "Hello World"
        coroutineScope.launch {
            SiteScanner().scan("https://www.parcduluberon.fr/")
        }
    }) {
        Text(text)
    }
}
