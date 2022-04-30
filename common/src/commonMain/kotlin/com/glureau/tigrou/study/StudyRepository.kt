package com.glureau.tigrou.study

import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.domain.UpdateListener
import com.glureau.tigrou.file.readStudyConf
import com.glureau.tigrou.file.writeHtml
import com.glureau.tigrou.file.writeStudyConf
import com.glureau.tigrou.htmldownload.HtmlDownloader
import com.glureau.tigrou.scanner.SiteScanner
import com.glureau.tigrou.sitemap.SitemapUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StudyRepository : UpdateListener {
    private val study: Study

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val downloader = HtmlDownloader()

    private val _studyFlow: MutableStateFlow<Study>
    val studyFlow: Flow<Study> get() = _studyFlow

    private val _progressFlow = MutableStateFlow(0 to 0)
    val progressFlow: Flow<Pair<Int, Int>> get() = _progressFlow

    init {
        val studyJson = readStudyConf()
        study = if (studyJson == null) {
            Study()
        } else {
            try {
                Json {
                    ignoreUnknownKeys = true
                }.decodeFromString(studyJson)
            } catch (t: Throwable) {
                println("Cannot restore previous study!")
                t.printStackTrace()
                Study()
            }
        }
        _studyFlow = MutableStateFlow(study)
        study.listener = this
        study.sites.forEach {
            it.listener = this
        }

        // Re-start previous unfinished site-scanning work. (Doesn't retry pages that are in cache.)
        coroutineScope.launch {
            study.sites.forEach {
                SiteScanner().updateSite(it)
            }
        }
    }

    override suspend fun onUpdate() {
        _studyFlow.emit(study)
        writeStudyConf(Json.encodeToString(study))
        println("UPDATE !!")
    }

    suspend fun addSite(baseUrl: String) {
        val newSite = SiteScanner().scan(baseUrl)
        newSite.listener = this
        study.sites.add(newSite)
        onUpdate()
    }

    fun startDownloadWebPages() =
        coroutineScope.launch {
            val tasks = studySitemapUrl().filter { it.enabled && it.htmlContentPath == null }
            val tasksCount = tasks.count()
            _progressFlow.update { 0 to tasksCount }
            tasks.forEachIndexed { index, sitemapUrl ->
                val res = downloader.download(sitemapUrl.loc)
                if (res.isSuccess) {
                    sitemapUrl.htmlContentPath = study.writeHtml(sitemapUrl.loc, res.getOrThrow())
                    onUpdate()
                } else {
                    res.exceptionOrNull()?.printStackTrace()
                }
                _progressFlow.update { index + 1 to tasksCount }
                delay(1000)
            }
            _progressFlow.update { tasksCount to tasksCount }
        }.also {
            it.invokeOnCompletion { _progressFlow.update { 0 to 0 } }
        }

    fun studySitemapUrl(): List<SitemapUrl> {
        // Using copy to avoid concurrent modifications
        val urls = mutableListOf<SitemapUrl>()
        val sitesCopy = study.sites.toList()
        sitesCopy.forEach { site ->
            val sitemapsCopy = site.sitemapIndex?.sitemaps?.toList()
            sitemapsCopy?.forEach { sitemap ->
                sitemap.urlSet?.urls?.forEach { urls.add(it) }
            }

            site.sitemapUrlSet?.urls?.forEach { urls.add(it) }
        }
        return urls
    }

    suspend fun removeSite(baseUrl: String) {
        study.sites.removeAll { it.baseUrl == baseUrl }
        onUpdate()
    }
}