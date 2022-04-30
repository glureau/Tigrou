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

private fun Study.links() = study() + fileSeparator() + "links.txt"
fun Study.readLinks() = readFile(links())
fun Study.writeLinks(content: String) = writeFile(links(), content)

private fun urlToFile(url: String) = url
    .substringAfter("http://")
    .substringAfter("https://")
    .replace("/", "_")

private fun Study.htmlDir() = study() + fileSeparator() + "html"
fun Study.readHtml(url: String) = readFile(htmlDir() + fileSeparator() + urlToFile(url) + ".html")
fun Study.writeHtml(url: String, content: String) =
    writeFile(htmlDir() + fileSeparator() + urlToFile(url) + ".html", content)
