import com.perficient.devops.log.*

def call( String errorCode, String message=''){

  issueResource = libraryResource('issues.yml')

  echo "${issueResource}"

  echo PerficientMessage.knownIssue(errorCode, message )
}
