package com.glureau.tigrou.file

import java.io.File

actual fun writeFile(path: String, content: String) {
    File(path).apply {
        parentFile.mkdirs()
        writeText(content)
    }
}

actual fun readFile(path: String): String? {
    val file = File(path)
    if (!file.exists()) return null
    return file.readText()
}

actual fun userPath(): String = System.getProperty("user.dir") // ou "user.home" for global/shared setup?

actual fun fileSeparator(): String = File.separator
