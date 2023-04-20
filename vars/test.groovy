def call(){
properties([
        disableConcurrentBuilds(),
        parameters([
            booleanParam(name: 'IS_VALIDATION', defaultValue: 'true', description: 'Validates Packages in org by deploying source'),
            booleanParam(name: 'MANAGED_BETA', defaultValue: 'true', description: 'Validates package while creating version')
        ])
    ])
   withCredentials([file(credentialsId: 'SERVER_KEY', variable:'jwt_key_file'), string(credentialsId: 'CLIENT_ID', variable:'cq_consumer_secret')])
    {   
        stage('Authorize devhub'){
            sh 'ada'
        }
    }

}
