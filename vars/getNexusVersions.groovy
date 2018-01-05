#!/usr/bin/groovy
/* Simple script to create a list of versions available from a specific Nexus
 * repository.
 *
 * N.B
 *   See the swagger api for json format http://nexus-server/swagger-ui/
 *
 * Make SSL to self-signed certificates work
 * https://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests?rq=1
 * https://stackoverflow.com/questions/3242335/how-to-use-ssl-with-a-self-signed-certificate-in-groovy
 *
 *Author: Sean Wilbur (Sean.Wilbur@perficient.com)
 */
import groovy.json.JsonSlurper
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

// Making this method NonCPS as the SSL classes are non-serializable
@NonCPS
def call(String serverUrl, String nexusRepo, String nexusGroup, String nexusArtifactId, String versionPattern)
{
  // Helper code for trusting self-signed certs
  def nullTrustManager = [getAcceptedIssuers: { null }, checkClientTrusted: { chain, authType -> }, checkServerTrusted: { chain, authType -> }]
  def nullHostnameVerifier = [verify: { hostname, session -> true }]
  def sc = SSLContext.getInstance("SSL")
  sc.init(null, [nullTrustManager as X509TrustManager] as TrustManager[], null )
  HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())
  HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier as HostnameVerifier)

  def version_ts_separator = '-'
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
    println "Running query -> ${nexus_component_query_url}"
    def conn = ''
    if ( nexus_component_query_url =~ /^https/ )
    {
      conn = new URL( nexus_component_query_url ).openConnection() as HttpsURLConnection
    }else{
      conn = new URL( nexus_component_query_url ).openConnection() as HttpURLConnection
    }
    conn.setRequestProperty( 'User-Agent', 'groovy-2.4.12' )
    conn.setRequestProperty('Accept', 'application/json')

    // reading the response triggers the request
    if ( conn.responseCode != 200 ){
      println "ERROR: Connection to Nexus failed: ${conn}"
      println "ResponseCode: ${conn.responseCode}"
      println "ResponseMessage: ${conn.responseMessage}"
      println "Error Stream ${conn.errorStream}"
      println "Content: ${conn.content}"
      return
    }

    println "Response OK, parsing JSON Object."
    def json = new JsonSlurper().parseText(conn.content.text)

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
      // TODO: Nuget
      default:
        println "ERROR: unknown repo type: ${json.items[0].format}"
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
