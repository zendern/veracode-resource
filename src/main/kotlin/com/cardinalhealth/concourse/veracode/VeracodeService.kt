/**
 * Copyright (C)2020 - Cardinal Health
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cardinalhealth.concourse.veracode

import com.cardinalhealth.concourse.ConcourseProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.veracode.apiwrapper.wrappers.ResultsAPIWrapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Profile("resource")
@Service
class VeracodeService(
        val resultsAPIWrapper: ResultsAPIWrapper,
        val mapper: XmlMapper) {


    private fun getBuildsSinceDate(since: OffsetDateTime): AppBuilds {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val sinceStr = since.format(formatter)
        val apiResult = resultsAPIWrapper.getAppBuilds(sinceStr, null, null)
        return mapper.readValue(apiResult)
    }

    fun getLatestBuild(appName: String, since: OffsetDateTime): ConcourseProperties.Version? {
        val app = getBuildsSinceDate(since).applications.first { it.appName == appName }
        val builds = app.builds?.map { it.toConcourseVersion() } ?: emptyList()
        return builds.firstOrNull { it.publishedDate.isAfter(since) }
    }

    fun getSummaryReport(buildId: String): String {
        return resultsAPIWrapper.summaryReport(buildId)
    }

    fun getDetailedReport(buildId: String): String {
        return resultsAPIWrapper.detailedReport(buildId)
    }
}
