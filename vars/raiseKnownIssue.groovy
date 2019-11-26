import com.perficient.devops.log.*

def call( String issueCode, String message=''){

  // load the issueMap from the resources file
  issuesMap = readYaml text: libraryResource('issues.yml')

  // only raise issue if found
  if( issuesMap.containsKey( 'issues' ) && issuesMap.errors.containsKey( issueCode ) ){
    issue = issuesMap.errors.get(issueCode)
    message = "${issueCode} - ${issue.message}"
    echo PerficientMessage.log(message )
  }else{
    // when errorCode not found, degrade to standard error
    // append a message here to indicate a failed error code lookup
    message = "Error finding known issue ${issueCode} degrading to standard error. " + message
    echo PerficientMessage.error(message)
  }
  error(message)
}
