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

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.springframework.core.io.ClassPathResource

@RunWith(JUnit4::class)
class VeracodeAPITest {

    val mapper = XmlMapper().registerKotlinModule().registerModule(JavaTimeModule())

    @Test
    fun `deserialize app builds`() {
        val appBuilds: AppBuilds = mapper.readValue(ClassPathResource("app-builds.xml").inputStream)
        println(appBuilds)
        assertEquals(4, appBuilds.applications.size)
    }

    @Test
    fun `deserialize summary report`() {
        val report: SummaryReport = mapper.readValue(ClassPathResource("summary-report.xml").inputStream)
        println(report)

        assertNotNull(report)
        assertEquals("your-application", report.application)
        assertEquals("veracode-sample_veracode_3", report.scanVersion)
    }

    @Test
    fun `deserialize detailed report`() {
        val report: DetailedReport = mapper.readValue(ClassPathResource("detailed-report.xml").inputStream)
        println(report)

        assertEquals(0, report.unmitigatedFlawsBySeverityLevel(4))
        assertEquals(0, report.unmitigatedFlawsBySeverityLevel(5))

    }
}