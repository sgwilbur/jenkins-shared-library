# Assert Plugins Installed

Common problem when creating pipeline jobs is to rely on plugins to extend the base Jenkins syntax, but there is no clear way to assert that these are installed.

The intent of this helper is to provide a simple way to declare upfront that this pipeline should explicitly fail when plugins that you will require are not installed at the correct versions.

Semantic Version

NPM Stype dependency filters
online tester to validate usage https://semver.npmjs.com/

  * equals            - =   - M[.m[.p]]                 - match major && if present match minor && if present also match patch
  * same major        - ^   - ^4                        - match same major only, both minor and patch are ignored
  * major with minor  - ~   - ~1.1                      - match major, if minor present match minor, ignore patch as all patches are included
  * greater than      - >   - >3 or >3.2 or >3.2.1
  * gt or eq          - >=  - >=3 or >=3.2 or >=3.2.1   - same as above but includes the version
  * less than         - <   - <4 or <4.14 or <4.14.2
  * less than or eq   - <=  - <=4 or <=4.14 or <=4.14.2

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

With an output excerpt like this on success:

    ...
    [Pipeline] Start of Pipeline (hide)
    [Pipeline] echo
    Currently installed plugins: [github-branch-source:2.5.7, script-security:1.63, github:1.29.4, command-launcher:1.3, blueocean-commons:1.19.0, bouncycastle-api:2.17, blueocean-i18n:1.19.0, structs:1.20, blueocean-rest:1.19.0, workflow-step-api:2.20, pubsub-light:1.13, workflow-scm-step:2.9, credentials:2.3.0, pipeline-model-declarative-agent:1.1.1, apache-httpcomponents-client-4-api:4.5.5-3.0, htmlpublisher:1.18, ssh-credentials:1.17.2, jsch:0.1.55.1, blueocean:1.19.0, git-client:2.8.5, scm-api:2.6.3, variant:1.3, display-url-api:2.3.2, mailer:1.27, workflow-api:2.37, junit:1.28, blueocean-web:1.19.0, matrix-project:1.14, git:3.12.1, authentication-tokens:1.3, pipeline-milestone-step:1.3.1, blueocean-jwt:1.19.0, jquery-detached:1.2.1, jackson2-api:2.9.9.1, ace-editor:1.1, favorite:2.3.2, workflow-support:3.3, workflow-cps:2.74, blueocean-rest-impl:1.19.0, pipeline-input-step:2.11, mercurial:2.8, pipeline-stage-step:2.3, workflow-job:2.35, pipeline-model-definition:1.3.9, pipeline-graph-analysis:1.10, blueocean-git-pipeline:1.19.0, pipeline-rest-api:2.12, handlebars:1.1.1, momentjs:1.1.1, blueocean-config:1.19.0, pipeline-stage-view:2.12, blueocean-dashboard:1.19.0, pipeline-build-step:2.9, jira:3.0.9, plain-credentials:1.5, blueocean-jira:1.19.0, credentials-binding:1.20, blueocean-autofavorite:1.2.4, pipeline-model-api:1.3.9, workflow-aggregator:2.6, pipeline-model-extensions:1.3.9, blueocean-display-url:2.3.0, cloudbees-folder:6.9, git-server:1.8, jenkins-design-language:1.19.0, workflow-cps-global-lib:2.15, branch-api:2.5.4, workflow-multibranch:2.21, docker-commons:1.15, durable-task:1.30, blueocean-core-js:1.19.0, workflow-durable-task-step:2.34, sse-gateway:1.20, workflow-basic-steps:2.18, blueocean-events:1.19.0, docker-workflow:1.19, github-api:1.95, pipeline-stage-tags-metadata:1.3.9, token-macro:2.8, blueocean-pipeline-scm-api:1.19.0, blueocean-pipeline-api-impl:1.19.0, blueocean-github-pipeline:1.19.0, handy-uri-templates-2-api:2.1.7-1.0, cloudbees-bitbucket-branch-source:2.4.6, blueocean-bitbucket-pipeline:1.19.0, blueocean-personalization:1.19.0, blueocean-pipeline-editor:1.19.0, datadog:0.7.1, pipeline-utility-steps:2.3.0, claim:2.15, warnings:5.0.1, jdk-tool:1.3, javadoc:1.5, maven-plugin:3.4, antisamy-markup-formatter:1.6, analysis-core:1.96, checkstyle:4.0.0, build-timestamp:1.0.3, config-file-provider:3.6.2, timestamper:1.10, warnings-ng:6.0.4, radiatorviewplugin:1.29, ivy:2.1, artifactory:3.3.2, gradle:1.34, lockable-resources:2.5, analysis-model-api:6.0.2, forensics-api:0.4.1, ssh-agent:1.17, ant:1.10]
    [Pipeline] echo
    Success - Requirements met for [pipeline-utility-steps] with filter [>=2.3.0] by [pipeline-utility-steps=2.3.0]
    [Pipeline] echo
    Success - Requirements met for [scm-api] with filter [2.6.3] by [scm-api=2.6.3]
    [Pipeline] echo
    Success - Requirements met for [build-timestamp] with filter [^1.0.3] by [build-timestamp=1.0.3]
    [Pipeline] echo
    Success - Requirements met for [warnings] with filter [~5.0.0] by [warnings=5.0.1]
    [Pipeline] echo
    Success - Requirements met for [config-file-provider] with filter [>3.6.1] by [config-file-provider=3.6.2]
    [Pipeline] echo
    Success - Requirements met for [workflow-basic-steps] with filter [<2.20] by [workflow-basic-steps=2.18]
    [Pipeline] echo
    Success - Requirements met for [maven-plugin] with filter [<=3.4] by [maven-plugin=3.4]
    ...

And an exception thrown for failure:

    ...
    [Pipeline] Start of Pipeline
    [Pipeline] echo
    Currently installed plugins: [github-branch-source:2.5.7, script-security:1.63, github:1.29.4, command-launcher:1.3, blueocean-commons:1.19.0, bouncycastle-api:2.17, blueocean-i18n:1.19.0, structs:1.20, blueocean-rest:1.19.0, workflow-step-api:2.20, pubsub-light:1.13, workflow-scm-step:2.9, credentials:2.3.0, pipeline-model-declarative-agent:1.1.1, apache-httpcomponents-client-4-api:4.5.5-3.0, htmlpublisher:1.18, ssh-credentials:1.17.2, jsch:0.1.55.1, blueocean:1.19.0, git-client:2.8.5, scm-api:2.6.3, variant:1.3, display-url-api:2.3.2, mailer:1.27, workflow-api:2.37, junit:1.28, blueocean-web:1.19.0, matrix-project:1.14, git:3.12.1, authentication-tokens:1.3, pipeline-milestone-step:1.3.1, blueocean-jwt:1.19.0, jquery-detached:1.2.1, jackson2-api:2.9.9.1, ace-editor:1.1, favorite:2.3.2, workflow-support:3.3, workflow-cps:2.74, blueocean-rest-impl:1.19.0, pipeline-input-step:2.11, mercurial:2.8, pipeline-stage-step:2.3, workflow-job:2.35, pipeline-model-definition:1.3.9, pipeline-graph-analysis:1.10, blueocean-git-pipeline:1.19.0, pipeline-rest-api:2.12, handlebars:1.1.1, momentjs:1.1.1, blueocean-config:1.19.0, pipeline-stage-view:2.12, blueocean-dashboard:1.19.0, pipeline-build-step:2.9, jira:3.0.9, plain-credentials:1.5, blueocean-jira:1.19.0, credentials-binding:1.20, blueocean-autofavorite:1.2.4, pipeline-model-api:1.3.9, workflow-aggregator:2.6, pipeline-model-extensions:1.3.9, blueocean-display-url:2.3.0, cloudbees-folder:6.9, git-server:1.8, jenkins-design-language:1.19.0, workflow-cps-global-lib:2.15, branch-api:2.5.4, workflow-multibranch:2.21, docker-commons:1.15, durable-task:1.30, blueocean-core-js:1.19.0, workflow-durable-task-step:2.34, sse-gateway:1.20, workflow-basic-steps:2.18, blueocean-events:1.19.0, docker-workflow:1.19, github-api:1.95, pipeline-stage-tags-metadata:1.3.9, token-macro:2.8, blueocean-pipeline-scm-api:1.19.0, blueocean-pipeline-api-impl:1.19.0, blueocean-github-pipeline:1.19.0, handy-uri-templates-2-api:2.1.7-1.0, cloudbees-bitbucket-branch-source:2.4.6, blueocean-bitbucket-pipeline:1.19.0, blueocean-personalization:1.19.0, blueocean-pipeline-editor:1.19.0, datadog:0.7.1, pipeline-utility-steps:2.3.0, claim:2.15, warnings:5.0.1, jdk-tool:1.3, javadoc:1.5, maven-plugin:3.4, antisamy-markup-formatter:1.6, analysis-core:1.96, checkstyle:4.0.0, build-timestamp:1.0.3, config-file-provider:3.6.2, timestamper:1.10, warnings-ng:6.0.4, radiatorviewplugin:1.29, ivy:2.1, artifactory:3.3.2, gradle:1.34, lockable-resources:2.5, analysis-model-api:6.0.2, forensics-api:0.4.1, ssh-agent:1.17, ant:1.10]
    [Pipeline] echo
    Success - Requirements met for [pipeline-utility-steps] with filter [>=2.3.0] by [pipeline-utility-steps=2.3.0]
    [Pipeline] echo
    Success - Requirements met for [scm-api] with filter [2.6.3] by [scm-api=2.6.3]
    [Pipeline] echo
    Success - Requirements met for [build-timestamp] with filter [^1.0.3] by [build-timestamp=1.0.3]
    [Pipeline] echo
    Success - Requirements met for [warnings] with filter [~5.0.0] by [warnings=5.0.1]
    [Pipeline] echo
    Success - Requirements met for [config-file-provider] with filter [>3.6.1] by [config-file-provider=3.6.2]
    [Pipeline] echo
    Failure - Requirements NOT met for [workflow-basic-steps] with filter [<2.10].
    [Pipeline] echo
    Failed: Less than version comparison with requested filter (2,10,null) against the installed version(s) (2,18,null)
    [Pipeline] End of Pipeline
    java.lang.Exception: Failed: Less than version comparison with requested filter (2,10,null) against the installed version(s) (2,18,null)
    	at assertPluginsInstalled.call(assertPluginsInstalled.groovy:161)
    	at com.cloudbees.groovy.cps.CpsDefaultGroovyMethods.callClosureForMapEntry(CpsDefaultGroovyMethods:5228)
    	at com.cloudbees.groovy.cps.CpsDefaultGroovyMethods.each(CpsDefaultGroovyMethods:2107)
    	at assertPluginsInstalled.call(assertPluginsInstalled.groovy:43)
    	at WorkflowScript.run(WorkflowScript:14)
    	at ___cps.transform___(Native Method)
    	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    	at org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:83)
    	at org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrapNoCoerce.callConstructor(ConstructorSite.java:105)
    	at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:60)
    	at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:235)
    	at com.cloudbees.groovy.cps.sandbox.DefaultInvoker.constructorCall(DefaultInvoker.java:25)
    	at com.cloudbees.groovy.cps.impl.FunctionCallBlock$ContinuationImpl.dispatchOrArg(FunctionCallBlock.java:97)
    	at com.cloudbees.groovy.cps.impl.FunctionCallBlock$ContinuationImpl.fixArg(FunctionCallBlock.java:83)
    	at sun.reflect.GeneratedMethodAccessor217.invoke(Unknown Source)
    	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    	at java.lang.reflect.Method.invoke(Method.java:498)
    	at com.cloudbees.groovy.cps.impl.ContinuationPtr$ContinuationImpl.receive(ContinuationPtr.java:72)
    	at com.cloudbees.groovy.cps.impl.LocalVariableBlock$LocalVariable.get(LocalVariableBlock.java:39)
    	at com.cloudbees.groovy.cps.LValueBlock$GetAdapter.receive(LValueBlock.java:30)
    	at com.cloudbees.groovy.cps.impl.LocalVariableBlock.evalLValue(LocalVariableBlock.java:28)
    	at com.cloudbees.groovy.cps.LValueBlock$BlockImpl.eval(LValueBlock.java:55)
    	at com.cloudbees.groovy.cps.LValueBlock.eval(LValueBlock.java:16)
    	at com.cloudbees.groovy.cps.Next.step(Next.java:83)
    	at com.cloudbees.groovy.cps.Continuable$1.call(Continuable.java:174)
    	at com.cloudbees.groovy.cps.Continuable$1.call(Continuable.java:163)
    	at org.codehaus.groovy.runtime.GroovyCategorySupport$ThreadCategoryInfo.use(GroovyCategorySupport.java:129)
    	at org.codehaus.groovy.runtime.GroovyCategorySupport.use(GroovyCategorySupport.java:268)
    	at com.cloudbees.groovy.cps.Continuable.run0(Continuable.java:163)
    	at org.jenkinsci.plugins.workflow.cps.SandboxContinuable.access$001(SandboxContinuable.java:18)
    	at org.jenkinsci.plugins.workflow.cps.SandboxContinuable.run0(SandboxContinuable.java:51)
    	at org.jenkinsci.plugins.workflow.cps.CpsThread.runNextChunk(CpsThread.java:186)
    	at org.jenkinsci.plugins.workflow.cps.CpsThreadGroup.run(CpsThreadGroup.java:370)
    	at org.jenkinsci.plugins.workflow.cps.CpsThreadGroup.access$200(CpsThreadGroup.java:93)
    	at org.jenkinsci.plugins.workflow.cps.CpsThreadGroup$2.call(CpsThreadGroup.java:282)
    	at org.jenkinsci.plugins.workflow.cps.CpsThreadGroup$2.call(CpsThreadGroup.java:270)
    	at org.jenkinsci.plugins.workflow.cps.CpsVmExecutorService$2.call(CpsVmExecutorService.java:66)
    	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    	at hudson.remoting.SingleLaneExecutorService$1.run(SingleLaneExecutorService.java:131)
    	at jenkins.util.ContextResettingExecutorService$1.run(ContextResettingExecutorService.java:28)
    	at jenkins.security.ImpersonatingExecutorService$1.run(ImpersonatingExecutorService.java:59)
    	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
    	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
    	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    	at java.lang.Thread.run(Thread.java:748)
    Finished: FAILURE
    ...


### Reference
 * https://jenkins.io/doc/book/managing/script-approval/
 * https://semver.org/
 * https://semver.npmjs.com/
 * https://stackoverflow.com/questions/43494302/how-to-test-whether-a-jenkins-plugin-is-installed-in-pipeline-dsl-groovy
