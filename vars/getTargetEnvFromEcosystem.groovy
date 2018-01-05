
def call( deploy_env, prefix = "env_" ){
  def target_application = ''

  def ecosystem = readJSON file: 'ecosystem.json'
  // Found silly issue where the default GStringImpl cannot be used to
  // directly compare to the values that are default String objects
  def env_match_string = "${prefix}${DEPLOY_ENV}" as String
  ecosystem.apps.each{ app ->
    if( app.keySet().contains( env_match_string ) ){
      target_application = app.name
    }
  }
  if ( target_application ){
    return target_application
  }else{
    throw new Exception( "No Matching application found matching [${env_match_string}]")
  }

}
