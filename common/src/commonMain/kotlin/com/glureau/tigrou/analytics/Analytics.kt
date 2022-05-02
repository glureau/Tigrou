package com.glureau.tigrou.analytics

import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.file.readMarkdown

class Analytics {

    private val defaultExclusionList = listOf(
        "un", "une", "le", "la", "les", "au", "du", "a", "de", "des", "aux", "d'un", "d'une",
        "mais", "ou", "et", "donc", "or", "ni", "car", "y",
        "son", "sa", "ses", "ton", "ta", "tes", "cet", "cette", "notre",
        "est", "es", "ai", "dans", "pour", "plus", "en", "faire",
        "sur", "sous", "dessus", "dessous", "avec", "par",
        "je", "tu", "il", "elle", "nous", "vous", "ils", "elles",
        "se", "votre", "sont", "qui", "ce", "que", "c'est", "tout", "ont",
        "liens", "sites", "charte", "logo", "tél", "contactez-nous", "accueil", "plan", "and", "in", "newsletter",
        "more", "read", "mailto", "the",
        "http",
    )

    fun wordCount(study: Study) {
        val occurrences = mutableMapOf<String, Int>()
        val urls = study.allSitemapUrl().filter { it.enabled && it.markdownContentPath != null }
        urls.forEach {
            val md = study.readMarkdown(it.loc) ?: return@forEach
            md.split(Regex("[\\s:()\\[\\]]+")).forEach { word ->
                //TODO: val mdLinkRegex = Regex("!*([^\\]]([^)]*)")

                val w = word
                    .replace(Regex("[\"*:<>«»”]"), "")
                    .replace("’", "'")
                    .replace(Regex("[éèê]"), "e")
                    .replace(Regex("[àâ]"), "a")
                    //.replace(mdLinkRegex, "$1")
                    .removeSuffix("...")
                    .replace(Regex("[?,.!…:]$"), "")
                    .removeSuffix("s")
                    .lowercase()
                if (w.contains(Regex("[a-zA-Z]+")) && !w.contains(Regex("[\\\\/@]+"))) {
                    val count = occurrences.getOrElse(w) { 0 }
                    occurrences[w] = count + 1
                }
            }
        }
        val result = occurrences.toList().sortedByDescending { it.second }
        println("--------------")
        result.forEach { (word, count) ->
            if (!defaultExclusionList.contains(word))
                println("$word: $count")
        }
    }
}