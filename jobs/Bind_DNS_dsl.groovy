freeStyleJob("Bind-DNS-test1"){
      description 'dsl for the Bind-DNS job'
      label('master')
      keepDependencies(true)

    logRotator(20,40)

    scm {
        git {
            remote {
                url('https://github.com/Einavf/bind-dns.git')
                
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
                    command "#!/bin/bash\n" +
              "sudo named-checkzone nyc3.example.com example.com.txt\n" +                
              "if [ '\$'? -ne 0 ];\n" +                
              "then\n" +                
              'echo \n' +             
              'echo \n' +             
              'echo \n' +             
              "echo named-checkzone failed due to errors in the file, please fix and run the job again\n" +
              'echo \n' +             
              'echo \n' +             
              'echo \n' +             
              "exit 1\n" +
              "fi\n"
            }
          }

        project / 'properties' <<'com.coravy.hudson.plugins.github.GithubProjectProperty'(plugin:'github@1.11.3'){
              projectUrl 'https://github.com/Einavf/bind-dns.git'
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
