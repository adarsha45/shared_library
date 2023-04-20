BUILD_NUMBER=env.BUILD_NUMBER
def call(){
properties([
        disableConcurrentBuilds(),
        parameters([
            booleanParam(name: 'IS_VALIDATION', defaultValue: 'true', description: 'Validates Packages in org by deploying source'),
            booleanParam(name: 'MANAGED_BETA', defaultValue: 'true', description: 'Validates package while creating version')
        ])
    ])
   
        stage('Authorize devhub'){
                sh 'echo "${BUILD_NUMBER}"'
        }
    

}
