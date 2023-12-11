def call(repoName, BRANCH_NAME, compareBranch = BRANCH_NAME) {
    def repositoryUrl = "https://bitbucket.org/compquest/${repoName}.git"
    checkout([
        $class: 'GitSCM', 
        branches: [[name: BRANCH_NAME]], 
        extensions: [
            [
                $class: 'SubmoduleOption',
                disableSubmodules: false,
                parentCredentials: true,
                recursiveSubmodules: true,
                trackingSubmodules: true
            ],
            [
                $class: 'WipeWorkspace'
            ],
            [$class: 'ChangelogToBranch', options: [compareRemote: 'origin', compareTarget: compareBranch]]
        ],
        userRemoteConfigs: [[credentialsId: 'BitbucketJenkinsCredential', url: repositoryUrl]]
    ])
}
