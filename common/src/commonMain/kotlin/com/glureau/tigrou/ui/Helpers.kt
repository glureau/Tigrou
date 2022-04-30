package com.glureau.tigrou.ui

fun String.humanUrl(): String =
    this.removePrefix("http://").removePrefix("https://").removePrefix("www.").removeSuffix("/index.html")
        .removeSuffix("/")
        .substringAfterLast("/")
        .removeSuffix("-sitemap.xml")
