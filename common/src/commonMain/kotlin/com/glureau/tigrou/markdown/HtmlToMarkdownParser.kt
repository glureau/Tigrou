package com.glureau.tigrou.markdown

expect class HtmlToMarkdownParser() {
    fun parseToMarkdown(htmlContent: String): String
}