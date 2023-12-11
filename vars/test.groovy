def call(){
            checkout([
                $class: 'GitSCM', 
                branches: [[name: "13.1.0"]], 
                extensions: [
                    [
                        $class: 'SubmoduleOption',
                        disableSubmodules: false,
                        parentCredentials: true,
                        recursiveSubmodules: true,
                        trackingSubmodules: true
                    ]
                    ,[
                        $class: 'WipeWorkspace'
                    ],
                    [$class: 'ChangelogToBranch', options: [compareRemote: 'origin', compareTarget: "13.0.0"]]
                ],
                userRemoteConfigs: [[credentialsId : 'BitbucketJenkinsCredential', url: 'https://bitbucket.org/compquest/sqx.git']]
            ])
}
