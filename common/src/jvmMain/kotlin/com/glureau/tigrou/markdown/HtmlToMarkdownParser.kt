package com.glureau.tigrou.markdown

import io.github.furstenheim.CodeBlockStyle
import io.github.furstenheim.CopyDown
import io.github.furstenheim.HeadingStyle
import io.github.furstenheim.LinkReferenceStyle
import io.github.furstenheim.LinkStyle
import io.github.furstenheim.OptionsBuilder

actual class HtmlToMarkdownParser {
    private val converter = CopyDown(
        OptionsBuilder.anOptions()
            .withBr("")
            .withFence("")
            .withBulletListMaker("-")
            .withHr("")
            .withEmDelimiter("")
            .withStrongDelimiter("")
            .withHeadingStyle(HeadingStyle.SETEXT)
            .withCodeBlockStyle(CodeBlockStyle.INDENTED)
            .withLinkReferenceStyle(LinkReferenceStyle.SHORTCUT)
            .withLinkStyle(LinkStyle.INLINED)
            .build()
    )

    actual fun parseToMarkdown(htmlContent: String): String {
        return converter.convert(htmlContent)
    }
}