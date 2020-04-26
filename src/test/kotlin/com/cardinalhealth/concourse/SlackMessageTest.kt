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
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.springframework.core.io.ClassPathResource

@RunWith(JUnit4::class)
class SlackMessageTest {

    val mapper = XmlMapper().registerKotlinModule().registerModule(JavaTimeModule())

    @Test
    fun itSetsTheCorrectTitle() {
        val report: DetailedReport = mapper.readValue(ClassPathResource("detailed-report.xml").inputStream)
        val subject = SlackMessage.slackReport("someChannel", report)
        val expected = report.getScanTitle()

        assertEquals(expected, subject.attachments.first().title)

    }
}