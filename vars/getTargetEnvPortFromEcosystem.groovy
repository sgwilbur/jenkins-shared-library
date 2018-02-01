/*
Author: Geoffrey Rosenthal (Geoffrey.Rosenthal@perficient.com)
Modified from Sean Wilbur's (Sean.Wilbur@perficient.com) getTargetEnvFromEcosystem
*/

def call( deploy_env, prefix = "env_" ){
  def target_port = ''

  def ecosystem = readJSON file: 'ecosystem.json'
  // Found silly issue where the default GStringImpl cannot be used to
  // directly compare to the values that are default String objects
  def env_match_string = "${prefix}${DEPLOY_ENV}" as String
  ecosystem.apps.each{ app ->
    if( app.keySet().contains( env_match_string ) ){
      target_port = app.get(env_match_string).get('PORT')
    }
  }
  if ( target_application ){
    return target_port
  }else{
    throw new Exception( "No Matching application port found matching [${env_match_string}]")
  }

}
