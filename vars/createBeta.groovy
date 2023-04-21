BUILD_NUMBER=env.BUILD_NUMBER
IS_RELEASE = false;
STATUS_QUEUED = "Queued"
STATUS_SUCCESS = "Success"
DEFAULT_DEVHUB_USER = env.DEFAULT_DEVHUB_USER
def call(Map args){
        PACKAGE_NAME = args.PACKAGE_NAME 
        SERVICE_PATH = args.SERVICE_PATH
        BETA_ORG_ALIAS = args.BETA_ORG_ALIAS
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
            sh 'sfdx auth:jwt:grant --instanceurl ${INSTANCE_URL} --clientid ${cq_consumer_secret} --username ${DEFAULT_DEVHUB_USER} --jwtkeyfile ${jwt_key_file} --setdefaultdevhubusername'
        }
        stage('Verify CQ PLM Packages'){
            // Identify CQ dependency version id
            def propschk = readJSON file: 'sfdx-project.json'
            def cquiPackagechk = propschk.packageDirectories.find {element -> element.package == PACKAGE_NAME}
            def dependencyNamechk = cquiPackagechk.dependencies[0].package
            def dependencyIdchk = propschk.packageAliases[dependencyNamechk]
            // Method Calling to Check if the Package is installed.
            def dependencyAlreadyInstalled = checkIfPackageExistIn(BETA_ORG_ALIAS, dependencyIdchk)
    
            if(!dependencyAlreadyInstalled){ 
                //if package isn't installed,run the following step
                try {
                    //delete existing scratch org and create new org
                    sh "sfdx force:org:delete -u ${BETA_ORG_ALIAS} -p"
                } catch (Exception e) {
                    //continue if no scratch org exists
                }
                sh "sfdx force:org:create -f config/project-scratch-def.json -v ${DEFAULT_DEVHUB_USER} -a ${BETA_ORG_ALIAS} -d 1"
                                
                // Identify CQ dependency version and install the package and validate ui package
                def props = readJSON file: 'sfdx-project.json'
                def cquiPackage = props.packageDirectories.find {element -> element.package == PACKAGE_NAME}
    
                // Install the dependency in the scratch org
                cquiPackage.dependencies.each{ key, value ->
                    def dependencyName = "${key.package}"
                    def dependencyId = props.packageAliases[dependencyName]
                    sh "sfdx force:package:install -p ${dependencyId} -r -u ${BETA_ORG_ALIAS} -s AdminsOnly -w 200"
                }
                try{
                    sh "sfdx force:user:password:generate -u ${BETA_ORG_ALIAS}";  
                }catch(Exception ex){
                    error(ex.getMessage());
                }                   
            }else {
                    echo "Skipped for packaging"
            }
        }
        stage("Pre-deployment"){
            sh "sfdx force:source:deploy -p version-create-fixes -w 100 -u ${BETA_ORG_ALIAS}"
        }
        stage('Verify CQ-PLM Packages'){
            if(params.IS_VALIDATION){
                //Run all tests and publish test result
                sh "sfdx force:source:deploy -p ${SERVICE_PATH} -w 100 -u ${BETA_ORG_ALIAS} -l RunLocalTests --verbose"
            }
            else{
                sh "sfdx force:source:deploy -p ${SERVICE_PATH} -w 100 -u ${BETA_ORG_ALIAS}"
            }
        }
        stage('Create Managed Package Version'){
            echo "CQ PLM Migration package version creation request in progress..."
            createPackageVersionFor(PACKAGE_NAME, SERVICE_PATH, IS_RELEASE)
        }
    }
        
    def checkIfPackageExistIn(def BETA_ORG_ALIAS, def packageVersionId) {
    def hasPackage = false;
    try{
        def rawPackageList = sh returnStdout: true, script: "sfdx force:package:installed:list -u '${BETA_ORG_ALIAS}' --json"
        def packageList = readJSON text: rawPackageList
        if(packageList.status == 0){
            hasPackage = packageList.result.any{element -> element.SubscriberPackageVersionId.startsWith(packageVersionId)}
        }
    }catch(Exception ex){}
    return hasPackage;
} 
    def createPackageVersionFor(def packageName, def path, def isRelease){
    def rmsg;
    if(params.MANAGED_BETA){
        echo "Package version creation and validation request in progress..."
        rmsg = sh returnStdout: true, script: "sfdx force:package:version:create -p '${PACKAGE_NAME}' -d ${SERVICE_PATH} -x -w 300 -v ${DEFAULT_DEVHUB_USER} -f config/project-scratch-def.json -c --json"
    }else{
        echo "Package version creation request in progress..."
        rmsg = sh returnStdout: true, script: "sfdx force:package:version:create -p '${PACKAGE_NAME}' -d ${SERVICE_PATH} -x -w 300 -v ${DEFAULT_DEVHUB_USER} -f config/project-scratch-def.json --skipvalidation --json"
    }
    def props = readJSON text: rmsg
    if(props.status == 0){
        if(!props.containsKey('result')){
            echo "No results found. Check the package version creation status manually"
        }else if(props.result.Status == "${STATUS_QUEUED}"){
            echo "Package version creation request status is 'Queued'. Run 'sfdx force:package:version:create:report -i ${props.result.Id}' to query for status."
        }else if(props.result.Status == "${STATUS_SUCCESS}"){
            echo "Successfully created the package version [${props.result.Id}]. Subscriber Package Version Id: ${props.result.SubscriberPackageVersionId}"
                        
            echo "Package Installation URL: https://login.salesforce.com/packaging/installPackage.apexp?p0=${props.result.SubscriberPackageVersionId}"
                        
            //Package version details display
            sh "sfdx force:package:version:report -p ${props.result.SubscriberPackageVersionId} -v ${DEFAULT_DEVHUB_USER}"
    
            if(params.IS_RELEASE){
                sh "sfdx force:package:version:promote -p ${props.result.SubscriberPackageVersionId} -n -v ${DEFAULT_DEVHUB_USER}"
            }
        }
    }else{
        currentBuild.result = "FAILED"
        echo "Package version creation request failed."
        echo "${props.stack}"
    }
}    
   

    

}
