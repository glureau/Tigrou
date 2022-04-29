package com.glureau.tigrou.domain

data class Settings(
    val ignoredWords: MutableList<String> = mutableListOf()
)

data class Site(
    val baseUrl: String,
) {
    val urls: MutableList<InternalUrl> = mutableListOf()
}

data class InternalUrl(
    val url: String,
) {
    var enabled: Boolean = true
}
