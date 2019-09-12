#!/usr/bin/groovy
/* Simple script to create a list of versions available from a specific Nexus v3.x
 * repository. Avoid using the built in HTTP clients as the keystore trusting is
 * not working as expected
 *
 * N.B
 *   See the swagger api for json format http://nexus-server/swagger-ui/
 *
 * Author: Sean Wilbur (Sean.Wilbur@perficient.com)
 */
import groovy.json.JsonSlurper
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern

def call(String serverUrl, String nexusRepo, String nexusGroup, String nexusArtifactId, String versionPattern)
{

  def version_ts_separator = '_'
  def date_format_str = 'yyyyMMdd-HHmmss'

  // V.M.R<version_ts_separator><date_format_str>
  SimpleDateFormat sdf = new SimpleDateFormat( date_format_str )
  Pattern version_pattern = ~/^(\d\.\d\.\d)${version_ts_separator}(\d{8}-\d{6})$/
  Pattern filterPattern = ~/^${versionPattern}$/
  def nexus_versions = []

  println "Calling shared library getNexusVersions"
  println "      serverUrl : ${serverUrl}"
  println "      nexusRepo : ${nexusRepo}"
  println "     nexusGroup : ${nexusGroup}"
  println "nexusArtifactId : ${nexusArtifactId}"
  println " versionPattern : ${versionPattern} "
  println " Using date format: ${date_format_str}"
  println " Timestamp Pattern: ${version_pattern}"
  println " Filter pattern   : ${filterPattern}"

  def has_more_results = true
  def continuation = ''

  while( has_more_results) {
    has_more_results = false

    // N.B. Valid for Nexus3 ONLY
    def nexus_component_query_url = "${serverUrl}/service/siesta/rest/beta/search?${continuation}repository=${nexusRepo}&q=${nexusGroup}&q=${nexusArtifactId}"
    def content = ''
    println "Running query -> ${nexus_component_query_url}"

    /*
    // https://chezsoi.org/lucas/blog/making-https-calls-in-a-pure-groovy-shared-lib-for-jenkins-pipeline.html
    // requires http_request plugin
    def response = args.jenkinsWorkflowScript.invokeMethod 'httpRequest', [[args.url: url]] as Object[]
    if (response.status != 200) {
        jenkinsWorkflowScript.invokeMethod 'echo', [response.content] as Object[]
        throw new HttpResponseException(response.status, 'HTTP error')
    }
    response.content
    */


    content = "wget --no-check-certificate -O -  ${nexus_component_query_url}".execute().text
    def json = new JsonSlurper().parseText(content)

    if( ! json.items ){
      println " >>> No results"
      break
    }

    // Need to explicitly cast from GString to String during collect so it shows in drop down properly, otherwise all options just shows as Object[...]
    // https://issues.jenkins-ci.org/browse/JENKINS-27916
    switch( json.items[0].format ) {
      case 'raw':
        // in a raw repo there is no version attribute but the name format is <group>/<artifactid>/<version>/<filename> so we pull it out of there
        nexus_versions.addAll( json.items.findAll({ it.name =~ '.zip$' && it.name =~ nexusArtifactId }).collect({ ( it.name.split('/')[2] ) as String }) );
        break;
      case 'maven':
        nexus_versions.addAll( json.items.findAll({ it.name =~ nexusArtifactId }).collect({ "${it.version}" as String }) );
        break;
      default:
        println "ERROR: unknown repo type: ${ json.items[0].format }"
        return []
    }

    // token is null on final page of results, otherwise set this so the next loop with get next page
    if( json.continuationToken ){
      continuation = "continuationToken=${json.continuationToken}&"
      has_more_results = true
    }
  } // end while

  println "Available Nexus versions: " + nexus_versions

  // After collecting all possible versions, we drop all non-matching versions.
  // TODO: Include this in the original collection steps above
  def filtered_versions = nexus_versions.findAll({ filterPattern.matcher( it ).matches() })
  println "Available after applying filter: " + filtered_versions

  // since this is a simple object it can be just sorted as a string, only caveat
  // is that we need to reverse the list afterwards as we cannot control sort order
  if ( filtered_versions ){
    def sorted_versions = filtered_versions.sort().reverse()
    println "Sorted: " + sorted_versions
    return sorted_versions
  }else{
    println "WARN: No versions being returned returned"
    return []
  }

}
