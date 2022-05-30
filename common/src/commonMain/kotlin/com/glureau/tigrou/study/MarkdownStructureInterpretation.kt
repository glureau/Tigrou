package com.glureau.tigrou.study

data class DataPage(val blocks: List<DataBlock>)
data class DataBlock(val title: String, val phrases: List<DataPhrase>)

//data class DataLink(val url: String, val text: String)
data class DataImg(val url: String, val description: String)
data class DataWord(val index: Int, val word: String)
data class DataPhrase(val raw: String, val words: List<DataWord>, val imgs: List<DataImg>)

class MarkdownStructureInterpretation {
    fun parse(markdown: String): DataPage {
        val lines = markdown.split(Regex("\\n")).filter { it.isNotBlank() }

        val titleIndices = mutableListOf<Int>()
        lines.forEachIndexed { index, line ->
            if (index >= 1) {
                val previousLine = lines[index - 1]
                if (Regex("[=-]{${previousLine.length}}").matches(line)) {
                    titleIndices.add(index - 1)
                }
            }
        }

        // If title has not been matched at the beginning, we'll use the first line as the block title
        //if (titleIndices.firstOrNull() != 0) {
        //    titleIndices.add(0, 0)
        //}
        println("TITLE : $titleIndices")

        // Add the end of the file as the last line
        titleIndices.add(lines.lastIndex + 1)
        var lastTitleIndex = 0
        val endOfPhraseRegex = Regex("\\. ([A-ZÀÁÂÄÅÃÆÇÉÈÊËÍÌÎÏÑÓÒÔÖØÕOEÚÙÛÜÝY])")
        val linkRegex = Regex("\\[([^]]*)\\]\\(([^)]*)\\)")
        val imageRegex = Regex("!\\[([^]]*)\\]\\(([^)]*)\\)")
        val listPrefix = Regex("^\\s*-\\s+")
        val notAWordRegex = Regex("^[\\s\\p{Punct}•«»–\\d]*$")
        val blocks = titleIndices.map { index ->
            val title = lines[lastTitleIndex]
            val blockLines = lines.subList(lastTitleIndex, index)
            val phrases = blockLines.flatMap {
                endOfPhraseRegex.replace(it) { a -> ".\n" + a.groupValues[1] }.split("\n")
            }.map { rawPhrase ->
                val rawImgs = imageRegex.findAll(rawPhrase)
                var onlyTxt = rawPhrase
                val images = rawImgs.map {
                    onlyTxt = onlyTxt.removeRange(it.range)
                    DataImg(
                        description = it.groupValues[1],
                        url = it.groupValues[2]
                    )
                }.toList()
                val links = linkRegex.findAll(onlyTxt)
                links.forEach {
                    // Keeping the link text as a standard text (but removing the link)
                    //onlyTxt = onlyTxt.replaceRange(it.range, it.groupValues[1])
                    onlyTxt = linkRegex.replace(onlyTxt) { it.groupValues[1] }
                }
                onlyTxt = onlyTxt.replace(listPrefix, "")
                    // When websites uses JS to generate emails
                    .replace("\\*protected email\\*", "")
                val words = onlyTxt.split(Regex("[,.?!\\s]+"))
                    .mapIndexedNotNull { index: Int, word: String ->
                        if (notAWordRegex.matches(word)) null
                        else DataWord(index = index, word = word)
                    }
                DataPhrase(rawPhrase, words = words, imgs = images)
            }
            lastTitleIndex = index

            DataBlock(title = title, phrases = phrases)
        }

        blocks.forEach { b ->
            println("title: ${b.title}")
            val words = b.phrases.flatMap { it.words.map { it.word } }
                .distinct()
            println("words $words")
            /*
            b.phrases.take(10).forEachIndexed { index, s ->
                println("phrase $index: $s")
            }
            println("last:${b.phrases.lastOrNull()}")
            */
        }

        return DataPage(blocks = blocks)
    }
}