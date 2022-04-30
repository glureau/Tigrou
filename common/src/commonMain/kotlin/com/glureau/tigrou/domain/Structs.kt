package com.glureau.tigrou.domain

import kotlin.random.Random

data class Settings(
    val ignoredWords: MutableList<String> = mutableListOf()
)

data class Study(
    var studyName: String = "study_" + Random.nextInt().toString(32),
    val sites: MutableList<Site> = mutableListOf()
)

data class Site(
    val baseUrl: String,
) {
    var urls: List<InternalUrl> = emptyList()
//    val urls: MutableList<InternalUrl> = mutableListOf()
}

data class InternalUrl(
    val url: String,
) {
    var enabled: Boolean = true
}
