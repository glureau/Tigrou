package com.glureau.tigrou.file

import com.glureau.tigrou.domain.Study

expect fun writeFile(path: String, content: String)
expect fun readFile(path: String): String?

expect fun userPath(): String
expect fun fileSeparator(): String

private fun appDir() = userPath() + fileSeparator() + ".Tigrou"
private fun Study.study() = appDir() + fileSeparator() + "studies" + fileSeparator() + studyName
fun readStudyConf() = readFile(appDir() + fileSeparator() + "study_0.json")
fun writeStudyConf(content: String) = writeFile(appDir() + fileSeparator() + "study_0.json", content)

private fun urlToFile(url: String) = url.substringAfter("http://").substringAfter("https://").replace("/", "_")

private fun Study.htmlDir() = study() + fileSeparator() + "html"
private fun Study.htmlFile(url: String) = htmlDir() + fileSeparator() + urlToFile(url) + ".html"
fun Study.readHtml(url: String) = readFile(htmlFile(url))
fun Study.writeHtml(url: String, content: String): String {
    val path = htmlFile(url)
    writeFile(path, content)
    return path
}

private fun Study.markdownDir() = study() + fileSeparator() + "markdown"
private fun Study.markdownFile(url: String) = markdownDir() + fileSeparator() + urlToFile(url) + ".md"
fun Study.readMarkdown(url: String) = readFile(markdownFile(url))
fun Study.writeMarkdown(url: String, content: String): String {
    val path = markdownFile(url)
    writeFile(path, content)
    return path
}
