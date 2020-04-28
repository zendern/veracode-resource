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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.time.OffsetDateTime


/**
 * super nasty xml mapping that hopefully corresponds with this url: https://help.veracode.com/reader/LMv_dtSHyb7iIxAQznC~9w/MirrdEJck7FQ3aUuFAkaQg
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "applicationbuilds")
data class AppBuilds(

        @JacksonXmlProperty(localName = "application")
        @JacksonXmlElementWrapper(useWrapping = false)
        val applications: List<Application>
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Application(

            @JacksonXmlProperty(localName = "app_id", isAttribute = true)
            val appId: String,

            @JacksonXmlProperty(localName = "app_name", isAttribute = true)
            val appName: String,

            @JacksonXmlProperty(localName = "build")
            @JacksonXmlElementWrapper(useWrapping = false)
            val builds: List<Build>?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Build(

            @JacksonXmlProperty(localName = "build_id", isAttribute = true)
            val buildId: String,

            @JacksonXmlProperty(localName = "results_ready", isAttribute = true)
            val resultsReady: Boolean,

            @JacksonXmlProperty(localName = "version", isAttribute = true)
            val version: String,

            @JacksonXmlProperty(localName = "analysis_unit")
            val analysisUnit: AnalysisUnit
    ) {

        fun toConcourseVersion(): ConcourseProperties.Version = ConcourseProperties.Version().apply {
            this.buildId = this@Build.buildId
            publishedDate = analysisUnit.publishedDate
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AnalysisUnit(
            @JacksonXmlProperty(localName = "published_date_sec", isAttribute = true)
            val publishedDateSec: Long,

            @JsonDeserialize
            @JacksonXmlProperty(localName = "published_date", isAttribute = true)
            val publishedDate: OffsetDateTime
    )
}