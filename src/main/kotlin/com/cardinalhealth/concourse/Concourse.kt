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

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.time.OffsetDateTime


@Configuration
@ConfigurationProperties
class ConcourseProperties {

    var source: Source = Source()
    var version: Version? = null
    val params: Params? = null

    class Source {
        lateinit var apiId: String
        lateinit var apiKey: String
        lateinit var appName: String
        override fun toString(): String {
            return "Source(apiId='$apiId', apiKey='$apiKey', appName='$appName')"
        }
    }

    class Version {

        lateinit var buildId: String

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        lateinit var publishedDate: OffsetDateTime

        override fun toString(): String {
            return "Version(buildId='$buildId', publishedDate='$publishedDate')"
        }
    }

    class Params {

    }

    override fun toString(): String {
        return "ConcourseProperties(source=$source, version=$version, params=$params)"
    }
}
