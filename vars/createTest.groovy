def call(){
 //def SF_CONSUMER_KEY=env.SF_CONSUMER_KEY
       def SF_USERNAME="adarshashrestha957@cunning-hawk-qaejzs.com"
//     def SERVER_KEY_CREDENTALS_ID=env.SERVER_KEY_CREDENTALS_ID
       SF_DEV_HUB_ALIAS="devHub"
       def SF_SCRATCH_ALIAS="testOrg"
     SF_DEV_INSTANCE_URL ="https://login.salesforce.com"
 stage("Authorize a devhub"){
//         sh "sfdx auth:web:login -a ${SF_DEV_HUB_ALIAS} -r ${SF_DEV_INSTANCE_URL}"
           sh "sf org login device --set-default-dev-hub --alias ${SF_DEV_HUB_ALIAS}"
        //sh "sf org login web --set-default-dev-hub --alias ${SF_DEV_HUB_ALIAS} -r ${SF_DEV_INSTANCE_URL}"
    }  
}
