def call(String instanceUrl, String clientId, String username, String jwtKeyFile, String setAlias) {
  withCredentials([file(credentialsId: 'SERVER_KEY', variable: 'jwt_key_file'), 
                   string(credentialsId: 'CLIENT_ID', variable: 'cq_consumer_secret')]) {
    def authResult = sh(returnStatus: true, 
                        script: "sfdx auth:jwt:grant --instanceurl ${instanceUrl} --clientid ${clientId} --username ${username} --jwtkeyfile ${jwt_key_file} --setalias ${setAlias}")
    if (authResult == 0) {
      echo "Successfully authenticated as ${username}"
    } else {
      error "Authentication failed"
    }
  }
}
