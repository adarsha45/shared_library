//def SF_CONSUMER_KEY=env.SF_CONSUMER_KEY
       def SF_USERNAME="adarshashrestha957@cunning-hawk-qaejzs.com"
//     def SERVER_KEY_CREDENTALS_ID=env.SERVER_KEY_CREDENTALS_ID
       def SF_DEV_HUB_ALIAS="devHub"
       def SF_SCRATCH_ALIAS="testOrg"
    def SF_DEV_INSTANCE_URL ="https://login.salesforce.com"
    
def call(){
 stage("Authorize a devhub"){
        sh "sfdx auth:web:login -a ${SF_DEV_HUB_ALIAS} -r ${SF_DEV_INSTANCE_URL}"
    }  
}
