# Assert Plugins Installed

Common problem when creating pipeline jobs is to rely on plugins to extend the base Jenkins syntax, but there is no clear way to assert that these are installed.

The intent of this helper is to provide a simple way to declare upfront that this pipeline should explicitly fail when plugins that you will require are not installed at the correct versions.

Semantic Version

NPM Stype dependency filters
online tester to validate usage https://semver.npmjs.com/

 equals            - =   - M[.m[.p]]                 - match major && if present match minor && if present also match patch
 same major        - ^   - ^4                        - match same major only, both minor and patch are ignored
 major with minor  - ~   - ~1.1                      - match major, if minor present match minor, ignore patch as all patches are included
 greater than      - >   - >3 or >3.2 or >3.2.1
 gt or eq          - >=  - >=3 or >=3.2 or >=3.2.1   - same as above but includes the version
 less than         - <   - <4 or <4.14 or <4.14.2
 less than or eq   - <=  - <=4 or <=4.14 or <=4.14.2

only support simple 1.0.0 type versions not the other decorators as
Jenkins plugins do not allow full Semantic Versioning

Also this plugin does not support the more complex range syntax like NPM.

## Requirements

If not running as a system Shared Library this requires script approval of the following:

method hudson.PluginManager getPlugins
method hudson.PluginWrapper getShortName
method hudson.PluginWrapper getVersion
method jenkins.model.Jenkins getPluginManager
staticMethod jenkins.model.Jenkins getInstance

See your [https://jenkins.company.com/scriptApproval/](https://jenkins.company.com/scriptApproval/)

## Usage

Sample usage:


    @Library('shared-utilities@development') _

    pluginDependencies = [
      'pipeline-utility-steps': '',       // installed at any version
      'scm-api': '2.6.3',                 // installed and at version 2.6.3
      'build-timestamp':'^1.0.3',         // installed and at version 1.*
      'warnings':'~5.0.0',                // installed and at version 5.0.*
      'config-file-provider': '>3.6.1',   // installed and greater than 3.6.1
      'pipeline-utility-steps': '>=2.3.0',// installed and greater than or eq
      'workflow-basic-steps': '<2.20',    // installed and less than 2.20
      'maven-plugin': '<=3.4'             // installed and less than or eq 3.4
      ]

    assertPluginsInstalled( requiredPlugins: pluginDependencies )

    pipeline{
        agent any

        stages{
            stage( 'one' ){
                steps{
                    sh "echo 'Running stage after making sure required plugins are installed'"
                }
            }
        }
    }


### Reference
 * https://jenkins.io/doc/book/managing/script-approval/
 * https://semver.org/
 * https://semver.npmjs.com/
 * https://stackoverflow.com/questions/43494302/how-to-test-whether-a-jenkins-plugin-is-installed-in-pipeline-dsl-groovy
