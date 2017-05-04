freeStyleJob("Bind-DNS-dsl-test1"){
    	description 'dsl for the Bind-DNS job'
  		label('master')
  		keepDependencies(true)

    logRotator(20,40)

    scm {
        git {
            remote {
                url('git@github.com:kenshoo/jenkins.git')
                credentials('3f510033-65a9-4afd-9851-c7359bd3f9db')
                refspec('+refs/pull/*:refs/remotes/origin/pr/*')
            }

            configure { node ->
                node / 'extensions' / 'hudson.plugins.git.extensions.impl.CleanBeforeCheckout' {}
            }

            branch("origin/master")
        }
    }


    configure { project ->
      	def builder = project / 'builders'
      		builder << {
            'hudson.tasks.Shell' {
                    command "cd python_scripts\n" +
                            ". init.sh\n" +
                            "nosetests\n"
            }
          }

        project / 'properties' <<'com.coravy.hudson.plugins.github.GithubProjectProperty'(plugin:'github@1.11.3'){
              projectUrl 'https://github.com/kenshoo/jenkins/'
        }
    }

    configure { project ->
        	def publishers = project / 'publishers'
        		publishers << {

              'hudson.tasks.Mailer'{
                  recipients "\${ghprbActualCommitAuthorEmail}"
                  dontNotifyEveryUnstableBuild false
                  sendToIndividuals false
              }
          }
    }

    wrappers {
            preBuildCleanup()
            injectPasswords()
    }

    triggers {
                githubPush()
            }
}
