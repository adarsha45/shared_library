BUILD_NUMBER=env.BUILD_NUMBER
PACKAGE_NAME = "CQ PLM Migration"
IS_RELEASE = false;
SERVICE_PATH = "force-app-migration"
STATUS_QUEUED = "Queued"
STATUS_SUCCESS = "Success"
DEFAULT_DEVHUB_USER = env.DEFAULT_DEVHUB_USER
BETA_ORG_ALIAS = "CQPLM_MIGRATION"
def call(Map params){
  withCredentials([file(credentialsId: 'SERVER_KEY', variable: 'jwt_key_file'), string(credentialsId: 'CLIENT_ID', variable: 'cq_consumer_secret')]) {
    stage('Authorize devhub') {
      sh "sfdx auth:jwt:grant --instanceurl ${INSTANCE_URL} --clientid ${cq_consumer_secret} --username ${DEFAULT_DEVHUB_USER} --jwtkeyfile ${jwt_key_file} --setdefaultdevhubusername"
    }
    stage('Verify CQ PLM Packages') {
      // Identify CQ dependency version id
      def propschk = readJSON file: 'sfdx-project.json'
      def cquiPackagechk = propschk.packageDirectories.find { element -> element.package == PACKAGE_NAME }
      def dependencyNamechk = cquiPackagechk.dependencies[0].package
      def dependencyIdchk = propschk.packageAliases[dependencyNamechk]
      
      // Method Calling to Check if the Package is installed.
      def dependencyAlreadyInstalled = checkIfPackageExistIn(BETA_ORG_ALIAS, dependencyIdchk)
      
      if (!dependencyAlreadyInstalled) { 
        try {
          //delete existing scratch org and create new org
          sh "sfdx force:org:delete -u ${BETA_ORG_ALIAS} -p"
        } catch (Exception e) {
          //continue if no scratch org exists
        }
        
        sh "sfdx force:org:create -f config/project-scratch-def.json -v ${DEFAULT_DEVHUB_USER} -a ${BETA_ORG_ALIAS} -d 1"
        
        // Identify CQ dependency version and install the package and validate ui package
        def props = readJSON file: 'sfdx-project.json'
        def cquiPackage = props.packageDirectories.find { element -> element.package == PACKAGE_NAME }
        
        // Install the dependency in the scratch org
        cquiPackage.dependencies.each { key, value ->
          def dependencyName = "${key.package}"
          def dependencyId = props.packageAliases[dependencyName]
          sh "sfdx force:package:install -p ${dependencyId} -r -u ${BETA_ORG_ALIAS} -s AdminsOnly -w 200"
        }
        
        try {
          sh "sfdx force:user:password:generate -u ${BETA_ORG_ALIAS}"
        } catch (Exception ex) {
          error(ex.getMessage())
        }                   
      } else {
        echo "Skipped for packaging"
      }
    }
  }
}
}
