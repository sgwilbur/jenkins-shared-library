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

## Requirements

Requires script approval of the following:
method hudson.PluginManager getPlugins
method hudson.PluginWrapper getShortName
method hudson.PluginWrapper getVersion
method jenkins.model.Jenkins getPluginManager
staticMethod jenkins.model.Jenkins getInstance

## Usage



    assertPluginsInstalled(
      [
        'build-timestamp', '^1',
        'pipeline-utility-steps', '<2.18',
      ]
    )

### Reference
 * https://semver.org/
 * https://semver.npmjs.com/
 * https://stackoverflow.com/questions/43494302/how-to-test-whether-a-jenkins-plugin-is-installed-in-pipeline-dsl-groovy
