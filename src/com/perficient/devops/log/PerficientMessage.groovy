
package com.perficient.devops.log

class PerficientMessage implements Serializable {
  static final long serialVersionUID = 1L

  static String formattedMessage( String message ){
    def currentTimestamp = (new Date()).format("yyyyMMdd-HH:mm:ss.SSS", TimeZone.getTimeZone('UTC'))

    return "${currentTimestamp } - ${message}"
  }

  static String error( String message = ''){
    // any additional handling we can do here ?
    return DTCCMessage.formattedMessage( "ERROR - ${message}")
  }

  static String knownIssue( String issueCode, String message = ''){
    // if issueCode is not found or invalid, revert to error

    if( issueFound ){
      def catalogMsg = "Known issues ABC"
      return DTCCMessage.formattedMessage(" KNOWN ISSUE - ${issueCode} - ${catalogMsg}" )
    else
    {
      message = "Failed to lookup known issue ${issueCode} passing on to standard error handlers" + message
      return DTCCMessage.error( message )
    }
  }

}
