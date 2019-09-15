#!/usr/bin/env groovy

GroovyShell shell = new GroovyShell()
def assertPluginsInstalled = shell.parse(new File('assertPluginsInstalled.groovy'))

pluginDependencies = [
  'pipeline-utility-steps': '',       // installed at any version
  'scm-api': '2.6.3',                 // installed and at version 2.6.3
  'build-timestamp':'^1.0.3',         // installed and at version 1.*
  'warnings':'~5.0.0',                  // installed and at version 5.0.*
  'config-file-provider': '>3.6.1',   // installed and greather than 3.6.1
  'pipeline-utility-steps': '>=2.3.0',// installed and greater than or eq
  'workflow-basic-steps': '<2.20',    // installed and less than 2.20
  'maven-plugin': '<=3.4'             // installed and less than or eq 3.4
  ]

assertPluginsInstalled.call( requiredPlugins: pluginDependencies, mock: true, verbosity: 0 )


// Should fail with plugin not found exception
// pluginDependencies = [ 'build-timestamp':'1.0.3', 'github':'' ]
// assertPluginsInstalled.call( pluginDependencies )

// def map = pluginDependencies
// //e = map.entrySet().toList().first()
// e = map.iterator().next()
// // e = map.find { true }
// println e
// println e.getValue()
