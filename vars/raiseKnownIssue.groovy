import com.perficient.devops.log.*

def call( String errorCode, String message=''){
  echo PerficientMessage.knownIssue(errorCode, message )
}
