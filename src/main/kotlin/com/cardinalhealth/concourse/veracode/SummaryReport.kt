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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import javax.xml.bind.annotation.XmlRootElement


@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "summaryreport")
data class SummaryReport(

        @JacksonXmlProperty(localName = "app_name")
        val application: String,

        @JacksonXmlProperty(localName = "version")
        val scanVersion: String,

        @JacksonXmlProperty(localName = "policy_compliance_status")
        val policyComplianceStatue: String,

        @JacksonXmlProperty(localName = "policy_rules_status")
        val policyRulesStatus: String,

        @JacksonXmlProperty(localName = "flaw-status")
        val flawStatus: FlawStatus,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "severity")
        val severity: List<Severity>) {

    @JsonIgnoreProperties(ignoreUnknown = true)


    data class Severity(
            @JacksonXmlProperty(localName = "level", isAttribute = true)
            val level: Int,


            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "category")
            val category: List<Category> = emptyList()
    )

    @JsonIgnoreProperties(ignoreUnknown = true)

    data class Category(

            @JacksonXmlProperty(localName = "categoryname", isAttribute = true)
            val categoryName: String,

            @JacksonXmlProperty(localName = "count", isAttribute = true)
            val count: Int,

            @JacksonXmlProperty(localName = "severity", isAttribute = true)
            val severity: String)

    data class FlawStatus(
            @JacksonXmlProperty(localName = "cannot-reproduce", isAttribute = true)
            val cannotReproduce: Int,

            @JacksonXmlProperty(localName = "fixed", isAttribute = true)
            val fixed: Int,

            @JacksonXmlProperty(localName = "new", isAttribute = true)
            val new: Int,

            @JacksonXmlProperty(localName = "not_mitigated", isAttribute = true)
            val notMitigated: Int,

            @JacksonXmlProperty(localName = "open", isAttribute = true)
            val open: Int,

            @JacksonXmlProperty(localName = "reopen", isAttribute = true)
            val repoen: Int,

            @JacksonXmlProperty(localName = "sev-1-change", isAttribute = true)
            val sev1Change: Int,

            @JacksonXmlProperty(localName = "sev-2-change", isAttribute = true)
            val sev2Change: Int,

            @JacksonXmlProperty(localName = "sev-3-change", isAttribute = true)
            val sev3Change: Int,

            @JacksonXmlProperty(localName = "sev-4-change", isAttribute = true)
            val sev4Change: Int,

            @JacksonXmlProperty(localName = "sev-5-change", isAttribute = true)
            val sev5Change: Int,

            @JacksonXmlProperty(localName = "total", isAttribute = true)
            val total: Int
    )
}