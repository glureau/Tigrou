package com.glureau.tigrou.analytics

import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.file.readMarkdown

class Analytics {


    fun wordCount(study: Study): List<Pair<String, Int>> {
        val occurrences = mutableMapOf<String, Int>()
        val urls = study.allSitemapUrl().filter { it.enabled && it.markdownContentPath != null }
        urls.forEach {
            val md = study.readMarkdown(it.loc) ?: return@forEach
            md.split(Regex("[\\s:()\\[\\]]+")).forEach { word ->
                //https://stackoverflow.com/a/48489614/1898596
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
        return occurrences.toList().sortedByDescending { it.second }
    }
}