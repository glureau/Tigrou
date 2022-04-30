package com.glureau.tigrou.study

import com.glureau.tigrou.domain.Study
import com.glureau.tigrou.domain.UpdateListener
import com.glureau.tigrou.file.readStudyConf
import com.glureau.tigrou.file.writeStudyConf
import com.glureau.tigrou.scanner.SiteScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StudyRepository : UpdateListener {
    private val study: Study

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _studyFlow: MutableStateFlow<Study>
    val studyFlow: Flow<Study> get() = _studyFlow

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

        // Re-start previous work
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

    suspend fun removeSite(baseUrl: String) {
        study.sites.removeAll { it.baseUrl == baseUrl }
        onUpdate()
    }
}