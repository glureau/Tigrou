package com.glureau.tigrou.markdown

import io.github.furstenheim.CopyDown

actual class HtmlToMarkdownParser {
    private val converter = CopyDown()
    actual fun parseToMarkdown(htmlContent: String): String {
        return converter.convert(htmlContent)
    }
}