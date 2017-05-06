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
              'if [ \$? -ne 0 ];\n' +                
              "then\n" +                
                'echo "************************************************************************************"\n' +             
              "echo named-checkzone failed due to errors in the file, please fix and run the job again\n" +
                'echo "*************************************************************************************"\n' +
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

               'hudson.plugins.s3.S3BucketPublisher'(plugin:'s3@0.10.12'){
                  'profileName' bind-dns-to-s3
                    'entries' {
                        'hudson.plugins.s3.Entry' {
                           'bucket' ("fuji-dns")
                            'sourceFile' ("example.com.txt")
                            'excludedFile'
                            'storageClass' STANDARD
                            'selectedRegion' ("us-west-2")
                            'noUploadOnFailure' true
                            'uploadFromSlave' false
                            'managedArtifacts' true
                            'useServerSideEncryption' false
                            'flatten' false
                            'gzipFiles' false
                            'showDirectlyInBrowser' false
                            'keepForever' false
                                                     
                        }
                      'dontWaitForConcurrentBuildCompletion' false
                        'consoleLogLevel' {
                          'name' INFO
                            'value' 800
                            'resourceBundleName' ("sun.util.logging.resources.logging")
                          }
                      'pluginFailureResultConstraint' {
                        'name' FAILURE
                        'ordinal' 2 
                        'color' RED
                        'completeBuild' true
                        }
                      'userMetadata'
                        
                       
                   }
                        
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
