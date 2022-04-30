package com.glureau.tigrou.htmldownload

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

private val client = HttpClient()

class HtmlDownloader {

    suspend fun download(url: String): Result<String> {
        try {
            val response = client.get(url)
            if (response.status.isSuccess()) return Result.success(response.bodyAsText())
            class BadHttpStatus(status: Int, body: String) : Exception()
            return Result.failure(BadHttpStatus(response.status.value, response.bodyAsText()))
        } catch (t: Throwable) {
            return Result.failure(t)
        }
    }
    /*
    fun downloadUrl(url: String) {
        File(localPath).mkdirs()
        val fileName = computeFilenameFrom(url)

    }

    private fun computeFilenameFrom(url: String): String {
        val lastPart = url.substringAfterLast("/")
        if (lastPart.isNotEmpty()) return lastPart.withoutExtension()
    }

    private fun String.withoutExtension(): String {
        if (this.endsWith(".html")) return this.substringBefore(".html")
    }*/
}