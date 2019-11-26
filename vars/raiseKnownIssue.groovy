import com.perficient.devops.log.*

def call( String errorCode, String message=''){

  // load the issueMap from the resources file
  issuesMap = readYaml text: libraryResource('issues.yml')

  // only raise issue if found
  if( issuesMap.containsKey( 'errors' ) && issuesMap.errors.containsKey( errorCode ) ){
    issue = issuesMap.errors.get(errorCode)
    message = "${issueCode} "
    echo PerficientMessage.log(message )
  }else{
    // when errorCode not found, degrade to standard error
    // append a message here to indicate a failed error code lookup
    message = "Error finding known issue ${issueCode} degrading to standard error. " + message
    echo PerficientMessage.error(message)
  }
  error(message)
}
