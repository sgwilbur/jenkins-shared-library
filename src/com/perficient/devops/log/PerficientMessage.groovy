
package com.perficient.devops.log

class PerficientMessage implements Serializable {
  static final long serialVersionUID = 1L

  static String formattedMessage( String message ){
    def currentTimestamp = (new Date()).format("yyyyMMdd-HH:mm:ss.SSS", TimeZone.getTimeZone('UTC'))
    return "${currentTimestamp } - ${message}"
  }

  static String log( String message = ''){
    // any additional handling we can do here ?
    return PerficientMessage.formattedMessage( "${message}")
  }

  static String error( String message = ''){
    // any additional handling we can do here ?
    return PerficientMessage.formattedMessage( "ERROR - ${message}")
  }

}
