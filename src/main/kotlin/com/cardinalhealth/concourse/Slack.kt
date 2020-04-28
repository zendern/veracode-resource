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

import com.cardinalhealth.concourse.veracode.DetailedReport
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.postForEntity

@Profile("slack")
@Configuration
@ConfigurationProperties("slack")
class SlackProperties {
    lateinit var token: String
    lateinit var channel: String
}

@Profile("slack")
@Service
class SlackService(val slackProperties: SlackProperties, builder: RestTemplateBuilder, @Qualifier("json") val objectMapper: ObjectMapper) {

    //the xml objectMapper is wreaking havoc on message conversion
    val restTemplate = builder
            .interceptors(ClientHttpRequestInterceptor { request, body, execution ->
                request.headers.add("Authorization", "Bearer ${slackProperties.token}")
                request.headers.contentType = MediaType.APPLICATION_JSON_UTF8
                execution.execute(request, body)
            })
            .defaultMessageConverters()
            .build()

    fun sendMessage(detailedReport: DetailedReport) {
        val message = SlackMessage.slackReport(slackProperties.channel, detailedReport)
        val payload = objectMapper.writeValueAsString(message)
        val entity: ResponseEntity<String> = restTemplate.postForEntity("https://slack.com/api/chat.postMessage", payload)
        println(entity.body)
    }

}

data class SlackMessage(
        val channel: String,
        val as_user: Boolean = true,
        val attachments: List<SlackAttachment>) {

    data class SlackAttachment(val title: String, val text: String)

    companion object {
        fun slackReport(channel: String, report: DetailedReport): SlackMessage {
            val summary = """
                    |application: ${report.application}
                    |veracode scan: ${report.scanVersion}
                    |Static Results:
                    |sev5 (Very High) flaws: ${report.unmitigatedFlawsBySeverityLevel(5)}
                    |sev4 (High) flaws: ${report.unmitigatedFlawsBySeverityLevel(4)}
                    |sev3 (Medium) flaws: ${report.unmitigatedFlawsBySeverityLevel(3)}
                    |
                    |SCA Results:
                    |sev5 (Very High) flaws: ${report.scaVulnerabilitiesBySeverityLevel(5)}
                    |sev4 (High) flaws: ${report.scaVulnerabilitiesBySeverityLevel(4)}
                    |sev3 (Medium) flaws: ${report.scaVulnerabilitiesBySeverityLevel(3)}
                    """.trimMargin("|")

            val attachment = SlackAttachment(report.getScanTitle(), summary)
            return SlackMessage(channel = channel, attachments = listOf(attachment))
        }
    }
}
