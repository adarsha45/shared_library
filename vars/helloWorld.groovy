def call() {
     stage('checkout source') {
        checkout scm
    }
     stage("Authorize a devhub"){
        sh "sfdx auth:device:login -a ${SF_DEV_HUB_ALIAS} --instanceurl ${SF_DEV_INSTANCE_URL}"
    }        
}
def ada(){
println "this is adarsh"
}
