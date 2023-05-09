
def call(Map params) {
    def jname = env.JOB_NAME
    def g = "${env.JOB_NAME}"
    def org = [:]
    echo "${jname}"
    echo "${g}"
//     org.name = params.name
//     org.projectScratchDefPath = params.projectScratchDefPath
//     org.durationDays = params.durationDays
//     echo "Create scratch org ${org.name}"
//     echo "${jname}"

    // Username identifies the org in later stages
   // def create = shWithResult "sfdx force:org:create --definitionfile ${org.projectScratchDefPath} --json --setdefaultusername --durationdays ${org.durationDays} --setalias ${jName} --wait 10"
    //org.username = create.username
    //org.orgId = create.orgId

    // Password and instance useful for manual debugging after the build (if org kept)
    shWithStatus "sfdx force:user:password:generate --targetusername ${org.username}"
    def display = shWithResult "sfdx force:org:display --json --targetusername ${org.username}"
    org.password = display.password
    org.instanceUrl = display.instanceUrl

    echo "Created scratch org name ${org.name} username ${org.username} password ${org.password} url ${org.instanceUrl} orgId ${org.orgId}"
}
