# Veracode Resource

A concourse resource able to publish artifacts to veracode for scanning and fetch/retrieve scan results


Note: Multiple scan requests in quick succession will cause failures

## Source Configuration

* `api_id`: *Required.* The Veracode API ID you wish to publish to

* `api_key`: *Required.* The Veracode API Key required to publish to the `api_id`

* `app_name`: *Required.* The application name to be fetched from or published to. This application should belong to an existing application in Veracode.

### Usage:

## Behavior

### `check`:
Checks for the latest veracode builds

### `in`: Do
Downloads the detailed-report.xml for the given veracode build id 


### `out`: Publish artifact to Veracode

The logic for this is based on a self-contained script using the [veracode api wrapper jar](https://help.veracode.com/reader/y_H3nFK8RERrYT6OgB6zvQ/OYHJSYTMlU8j9iC8JfiDjg), everything else lives in the spring boot app.

A successful publish will have a version based off of concourse build variables in the format:
```sh
${BUILD_PIPELINE_NAME}_${BUILD_JOB_NAME}_${BUILD_NAME}
```

#### Parameters

* `path`: *Optional.* Path to directory containing files to be published to Veracode for scanning.

### `slack`: Non standard but see slack-alert example below
This will take the of the detailed-report.xml and craft a slack message to be sent to the webhook url of your liking. 


## Example pipeline configuration 
Using maven to download the artifact from your artifact repo, push it to veracode and then
obtain the scan results and push those details to slack. 

```yaml
registry_credentials: &registry_credentials
  username: ((github.username))
  password: ((github.token)) # must have read:packages permissions

resources:
- name: your-project-here-artifact
  type: maven-resource
  source:
    artifact: maven-group-id:your-project-here:jar
    url: <your artifact repository>
- name: veracode
  type: veracode
  source:
    api_id: ((veracode-api-id))
    api_key: ((veracode-api-key))
    app_name: your-project-here
resource_types:
- name: veracode
  type: docker-image
  source:
    repository: github.pkg/cardinal-health/veracode-resource
    <<: *registry_credentials
- name: maven-resource
  type: docker-image
  source:
    repository: pivotalpa/maven-resource
jobs:
- name: veracode-publish
  plan:
    - get: artifact
      trigger: true
      resource: your-project-here-artifact
    - put: artifact
      resource: veracode
- name: veracode-slack-alert
  plan:
    - get: veracode-report
      trigger: true
      resource: veracode
    - task: slack-alert
      config:
        platform: linux
        image_resource:
          type: docker-image
          source:
            repository: github.pkg/cardinal-health/veracode-resource
            <<: *registry_credentials
        inputs:
          - name: veracode-report
        params:
          SLACK_CHANNEL: '#slack-integration'
          SLACK_TOKEN: ((slack-webhook-token))
        run:
          path: /opt/resource/slack
          args: [summary-report.xml]
          dir: veracode-report
```

[sample-pipeine.yml](sample-pipeline.yml) includes a more in-depth example that pushes artifacts to veracode and fetches scan results

## Contribution

Contribution through pull requests is recommended. Once approved, you may run the command `./gradlew pushDocker` to update the Docker image, updating the resource for all pipelines utilizing the `latest` Docker tag for the Veracode resource.