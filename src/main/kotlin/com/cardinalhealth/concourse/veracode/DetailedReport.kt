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

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import javax.xml.bind.annotation.XmlRootElement

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "detailedreport")
data class DetailedReport(

        @JacksonXmlProperty(localName = "app_name")
        val application: String,

        @JacksonXmlProperty(localName = "version")
        val scanVersion: String,

        @JacksonXmlProperty(localName = "policy_compliance_status")
        val policyComplianceStatue: String,

        @JacksonXmlProperty(localName = "policy_rules_status")
        val policyRulesStatus: String,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "severity")
        val severity: List<Severity>,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "static-analysis")
        val staticAnalysis: StaticAnalysis,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "software_composition_analysis")
        val softwareCompositionAnalysis: SCA?) {


    fun unmitigatedFlawsBySeverityLevel(level: Int): Int {
        return severity
                .first { it.level == level }
                .category
                .flatMap { it.cwes }
                .flatMap { it.staticFlaws.flaws }
                .filter { it.mitigationStatus != "accepted" }
                .filter { !"fixed".equals(it.remediationStatus, ignoreCase = true) }
                .sumBy { it.count }
    }

    fun getScanTitle(): String {
        val firstModuleName: String? = staticAnalysis.modules?.first()?.name

        return "Scan of " + (firstModuleName ?: scanVersion) + " complete"
    }

    fun scaVulnerabilitiesBySeverityLevel(level: Int): Int {
        return softwareCompositionAnalysis?.vulnerableComponents
                ?.flatMap { it.vulnerabilities.orEmpty() }
                ?.filter { (it as LinkedHashMap<String, String>)["severity"]?.toInt() == level }
                ?.size ?: 0
    }

    /**
     *     Static Analysis Properties
     **/
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

            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "cwe")
            val cwes: List<CWE> = emptyList())


    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StaticAnalysis(
            @JacksonXmlProperty(localName = "modules")
            val modules: List<VeracodeModule>
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class VeracodeModule(
            @JacksonXmlProperty(localName = "name", isAttribute = true)
            val name: String
    )

    /**
     *     Software Composition Analysis Properties
     **/

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SCA(
            @JsonFormat(with = arrayOf(JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY))
            @JacksonXmlProperty(localName = "vulnerable_components")
            val vulnerableComponents: List<Component>?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Component(
            @JsonFormat(with = arrayOf(JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY))
            @JacksonXmlProperty(localName = "vulnerabilities")
            val vulnerabilities: List<Any>?
    )
}

/**
 *     Static Analysis Properties
 **/

@JsonIgnoreProperties(ignoreUnknown = true)
data class CWE(
        @JacksonXmlProperty(localName = "staticflaws")
        val staticFlaws: StaticFlaws

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StaticFlaws(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "flaw")
        val flaws: List<Flaw> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Flaw(

        @JacksonXmlProperty(localName = "count", isAttribute = true)
        val count: Int,

        @JacksonXmlProperty(localName = "mitigation_status", isAttribute = true)
        val mitigationStatus: String,

        @JacksonXmlProperty(localName = "remediation_status", isAttribute = true)
        val remediationStatus: String

)
