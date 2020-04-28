#!/bin/sh
#
# Copyright (C)2020 - Cardinal Health
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


set -eo pipefail

input="$(cat -)"

api_id=$(echo $input | jq -r '.source.api_id')
api_key=$(echo $input | jq -r '.source.api_key')
app_name=$(echo $input | jq -r '.source.app_name')
pathParam=$(echo $input | jq -r '.params.path')
filePath=$1

if [ "$pathParam" != "null" ] ; then
    filePath="$1/$pathParam"
fi

>&2 java -jar /opt/resource/veracode.jar \
-vid $api_id \
-vkey $api_key \
-action uploadandscan \
-appname $app_name \
-createprofile false \
-filepath $filePath \
-version ${BUILD_PIPELINE_NAME}_${BUILD_JOB_NAME}_${BUILD_NAME} \
-autoscan true \

echo "{}"
