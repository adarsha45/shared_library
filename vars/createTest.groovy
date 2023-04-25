def call(){
 //def SF_CONSUMER_KEY=env.SF_CONSUMER_KEY
       def SF_USERNAME="ashishrajbanshi70@empathetic-otter-5k58pt.com"
//     def SERVER_KEY_CREDENTALS_ID=env.SERVER_KEY_CREDENTALS_ID
       SF_DEV_HUB_ALIAS="devHub"
       def SF_SCRATCH_ALIAS="testOrg"
     SF_DEV_INSTANCE_URL ="https://login.salesforce.com"
 stage("Authorize a devhub"){
//         sh "sfdx auth:web:login -a ${SF_DEV_HUB_ALIAS} -r ${SF_DEV_INSTANCE_URL}"
           //sh "sfdx auth:logout -u adarshashrestha957@cunning-hawk-qaejzs.com -p"
           sh "sfdx auth:device:login -d -a ${SF_DEV_HUB_ALIAS} "
           //sh "sf org login device --set-default-dev-hub --alias ${SF_DEV_HUB_ALIAS}"
        //sh "sf org login web --set-default-dev-hub --alias ${SF_DEV_HUB_ALIAS} -r ${SF_DEV_INSTANCE_URL}"
    }
   stage('Create Test Scratch Org'){
       sh "sfdx force:org:create -v ${SF_DEV_HUB_ALIAS} --setdefaultusername --definitionfile config/project-scratch-def.json -a ${SF_SCRATCH_ALIAS} --wait 10 --durationdays 1"
    }
  stage('Generate password for test scratch org'){
       sh "sfdx force:user:password:generate -v ${SF_DEV_HUB_ALIAS} -a ${SF_SCRATCH_ALIAS}"
    }
 
  stage('Push to Test Scratch Org'){
       sh "sfdx force:source:deploy -a ${SF_SCRATCH_ALIAS}"
    }
        
    stage('Assign the default user in the scratch org'){
       sh "sfdx force:user:permset:assign --permsetname ForJack"
    }

    stage("Create Package"){
       sh "sfdx force:package:create --name MovieBooking --description 'You can book movie here' --path force-app --packagetype Managed -v ${SF_DEV_HUB_ALIAS}"
    }
}
