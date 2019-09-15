/**
 * @author Sean G Wilbur sgwilbur
 **/
def call( Map params ){
  // helper for local debugging
  def verbosity = (params.get('verbosity') ?: 0).toInteger()
  def mock = (params.get('mock') ?: 0).asBoolean()
  def requiredPlugins = (params.get('requiredPlugins') ?: [:])

  if( verbosity > 2 ){
    println "vebosity: ${verbosity}"
    println "mock: ${mock}"
    println "${requiredPlugins}"
  }
  def plugins = [:]

  // provide a simple map for testing
  if( mock  ){
    plugins = [
      'artifactory': '3.3.2',
      'workflow-basic-steps': '2.18',
      'scm-api': '2.6.3',
      'pipeline-utility-steps': '2.3.0',
      'maven-plugin': '3.4',
      'warnings': '5.0.1',
      'build-timestamp': '1.0.3',
      'config-file-provider': '3.6.2'
    ]
  }else{
    // Get currently installed plugins and collect name:version installed
    // less readable but closure will clean up intermediate for us automatically
    // N.B. pluginWrappers is non-serializable so deallocate explicitly asap
    plugins = jenkins.model.Jenkins.instance.getPluginManager().getPlugins().collectEntries { [(it.getShortName()):it.getVersion()] }
  }

  print "Currently installed plugins: ${plugins}"

  // capture the pieces of the version
  // [COMP]MAJOR[.MINOR[.PATCH]]
  def maxVersionPlaces = 6
  def pattern = ~/^([\^><~]?[=]?)?(\d{1,6})(?:\.(\d{1,6}))?(?:\.(\d{1,6}))?$/

  requiredPlugins.each{

    def pluginCheckPassing = false
    def pluginCheckMsg = ''
    def pluginName = it.key
    def pluginVersionFilter = it.value
    def requiredPluginSemVersionParts =  []

    // println "Assert plugin present"
    // println "           pluginName : [${pluginName}]"
    // println " pluginVersionFilter  : [${pluginVersionFilter}]"

    // first test for this plugin first
    foundPlugin = plugins.find { key, value -> "${key}" == "${pluginName}" }

    // if the plugin is not found we are in a failure state
    // TODO: should we collect all failures or just fail out hard here?
    if( !foundPlugin ){
      pluginCheckPassing = false
      pluginCheckMsg =  "Plugin ${pluginName} is not installed, this pipline requires it to be installed with version filter ${pluginVersionFilter}"
      pluginCheckMsg += "\nInstalled Plugins: ${plugins}"
      println pluginCheckMsg
      //error msg
      throw new Exception(pluginCheckMsg)
    }

    //println "      installedPlugin : ${foundPlugin}"

    // when no version filter is supplied pluginVersionFilter will be null,
    // and can continue as this we are done installed was the only requirement.
    // if a filter is defined we need to parse the version and work on
    // what comparisons are required
    if( pluginVersionFilter ){
      // capture the pieces of the version, non matched captures will be null
      // [COMP]MAJOR[.MINOR[.PATCH]]
      def requiredPluginMatch   = pluginVersionFilter     =~ pattern
      def installedPluginMatch  = foundPlugin.getValue()  =~ pattern

      if( requiredPluginMatch ){
        requiredPluginSemVersionParts = requiredPluginMatch[0][1..4]
      }else {
        msg = "ERROR - invalid version requirement for ${pluginName} ${pluginVersionFilter}, please correct in your assertPluginsInstalled statement."
        throw new Exception(msg)
      }

      if( installedPluginMatch ){
        installedPluginSemVersionParts = installedPluginMatch[0][1..4]
      }else {
        msg = "ERROR - invalid version ${foundPlugin}, this seems like a bug in this code :^(..."
        throw new Exception(msg)
      }

      // println " Required: " + requiredPluginSemVersionParts
      // println "Installed: " + installedPluginSemVersionParts

      def (compReq,majorReq,minorReq,patchReq) = requiredPluginSemVersionParts
      def (compIns,majorIns,minorIns,patchIns) = installedPluginSemVersionParts

      // Calculate a mask to make gt,gte,lt,lte comparisons simpler
      // MAJOR  - MINOR - PATCH
      // MMM,MMM|mmm,mmm|ppp,ppp
      BigInteger requiredVersionMask = majorReq.toInteger() * (10.power((maxVersionPlaces)*2)) +
        (((minorReq)? minorReq.toInteger() : 0)*(10.power(maxVersionPlaces))) +
        ((patchReq)? patchReq.toInteger() : 0)

      BigInteger installedVersionMask = majorIns.toInteger() * (10.power((maxVersionPlaces)*2)) +
        (((minorIns)? minorIns.toInteger() : 0)*(10.power(maxVersionPlaces))) +
        ((patchIns)? patchIns.toInteger() : 0)

      // println "Required Version Mask int: " + requiredVersionMask
      // println "   Installed Version Mask: " + installedVersionMask

      pluginCheckMsg = "with requested filter (${majorReq},${minorReq},${patchReq}) against the installed version(s) (${majorIns},${minorIns},${patchIns})"

      switch ( compReq ){
        case '^':  // major versions must match
          pluginCheckMsg = "Failed: Major version only comparison " + pluginCheckMsg
          pluginCheckPassing = majorReq == majorIns
          break
        case '~':  // major and minor match, use minor if part or requirement
          pluginCheckMsg = "Failed: Major and Minor version comparison " + pluginCheckMsg
          pluginCheckPassing = ( majorReq == majorIns ) && ( (minorReq)? minorReq == minorIns : true )
          break
        case '>':  // greater than
          pluginCheckMsg = "Failed: Greather than version comparison " + pluginCheckMsg
          pluginCheckPassing = installedVersionMask > requiredVersionMask
          break
        case '>=': // greater than or equal
          pluginCheckMsg = "Failed: Greater than or equal version comparison " + pluginCheckMsg
          pluginCheckPassing = installedVersionMask >= requiredVersionMask
          break
        case '<':  // less than
          pluginCheckMsg = "Failed: Less than version comparison " + pluginCheckMsg
          pluginCheckPassing = installedVersionMask < requiredVersionMask
          break
        case '<=': //less than or equal
          pluginCheckMsg = "Failed: Less than or equal version comparison " + pluginCheckMsg
          pluginCheckPassing = installedVersionMask <= requiredVersionMask
          break
        default:   // assume exact match of whatever versions are provided
          pluginCheckMsg = "Failed: Major and Minor and path exact match version comparison " + pluginCheckMsg
          pluginCheckPassing = ( majorReq == majorIns ) && ( (minorReq)? minorReq == minorIns : true ) && ( (patchReq)? patchReq == patchIns : true )
          break
      }

    }else{
      // no filter and it is installed so we are good!
      pluginCheckPassing = true
    }// end pluginVersionFilter

    // raise or collect error for each plugin
    if( pluginCheckPassing ){
      // success
      println "Success - Requirements met for [${pluginName}] with filter [${pluginVersionFilter}] by [${foundPlugin}]"
    }else{
      println "Failure - Requirements NOT met for [${pluginName}] with filter [${pluginVersionFilter}]."
      println pluginCheckMsg
      //error msg
      throw new Exception(pluginCheckMsg)
    }

  } // end params map each loop

}
