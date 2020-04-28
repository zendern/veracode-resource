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
package com.cardinalhealth.concourse

import com.cardinalhealth.concourse.veracode.VeracodeService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.veracode.apiwrapper.wrappers.ResultsAPIWrapper
import com.veracode.apiwrapper.wrappers.UploadAPIWrapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.io.File
import java.nio.file.Paths
import java.time.OffsetDateTime


@SpringBootApplication
class VeracodeConcourseResourceApplication {

    @Bean("xml")
    @Primary
    fun xmlMapper() = XmlMapper().registerKotlinModule().registerModule(JavaTimeModule()) as XmlMapper

    @Bean("json")
    fun jsonMapper() = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())

    @Bean
    @Profile("resource")
    fun resultsAPIWrapper(properties: ConcourseProperties) = ResultsAPIWrapper().apply {
        setUpApiCredentials(properties.source.apiId, properties.source.apiKey)
    }

    @Bean
    @Profile("resource")
    fun uploadAPIWrapper(properties: ConcourseProperties) = UploadAPIWrapper().apply {
        setUpApiCredentials(properties.source.apiId, properties.source.apiKey)
    }

    @Bean
    fun restTemplate() = RestTemplate()
}


@Profile("check")
@Component
class CheckRunner(val veracodeService: VeracodeService, val properties: ConcourseProperties, @Qualifier("json") val jsonMapper: ObjectMapper) : CommandLineRunner {
    override fun run(vararg args: String) {
        val since = properties.version?.publishedDate ?: OffsetDateTime.now().minusDays(10)
        val build = veracodeService.getLatestBuild(properties.source.appName, since = since)
        jsonMapper.writeValue(System.out, listOfNotNull(build))
    }
}

@Profile("in")
@Component
class InRunner(val veracodeService: VeracodeService, val properties: ConcourseProperties, @Qualifier("json") val jsonMapper: ObjectMapper, val concourseProperties: ConcourseProperties) : CommandLineRunner {
    override fun run(vararg args: String) {
        val dir = args[0]
        if (properties.version == null){
            System.err.println("no version passed in")
            System.out.println("{}")
            return
        }
        val report = veracodeService.getDetailedReport(properties.version!!.buildId)
        Paths.get(dir, "detailed-report.xml").toFile().writeText(report)
        jsonMapper.writeValue(System.err, properties.version)

        jsonMapper.writeValue(System.out, properties.version)
    }
}

@Profile("slack")
@Component
class AnalyzeRunner(val slackService: SlackService, @Qualifier("xml") val xmlMapper: ObjectMapper) : CommandLineRunner {
    override fun run(vararg args: String) {
        val reportText = File(args[0]).readText()
        slackService.sendMessage(xmlMapper.readValue(reportText))
    }
}


fun main(args: Array<String>) {
    runApplication<VeracodeConcourseResourceApplication>(*args)
}
