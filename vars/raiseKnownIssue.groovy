import com.perficient.devops.log.*

def call( String issueCode, String message=''){

  // load the issuesMap from the resources file
  issuesResourceString = libraryResource('issues.yml')
  issuesMap = readYaml text: issuesResourceString

  //echo "issuesMap: ${issuesMap}"

  // only raise issue if found
  if( issuesMap && issuesMap.containsKey( 'issues' ) && issuesMap['issues'].containsKey( issueCode ) ){
    issue = issuesMap.errors.get(issueCode)
    message = "${issueCode} - ${issue.message}"
    // determine if we need to warn or end execution
  }else{
    // when errorCode not found, degrade to standard error
    // append a message here to indicate a failed error code lookup
    message = "Error finding known issue ${issueCode} degrading to standard error. " + message
  }

  echo PerficientMessage.error(message )
  error(message)

}
