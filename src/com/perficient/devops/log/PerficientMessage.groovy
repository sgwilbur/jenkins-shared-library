
package com.perficient.devops.log

class PerficientMessage implements Serializable {
  static final long serialVersionUID = 1L

  static String formattedMessage( String message ){
    def now = new Date()
    def ts = now.format("yyyyMMdd-HH:mm:ss.SSS", TimeZone.getTimeZone('UTC'))

    return "${ts} - ${message}"
  }

}
