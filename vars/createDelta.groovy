def SOURCE_API_NAME = "56.0"
def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'main'
  def uiDeltaPath = args.uiDeltaPath ?: ''
  def cqDeltaPath = args.cqDeltaPath ?: ''
  
  
  if (!uiDeltaPath && !cqDeltaPath) {
        throw new IllegalArgumentException("Both uiDeltaPath and cqDeltaPath cannot be empty. Please provide at least one of them.")
    }
  if (uiDeltaPath) {
    def props = readJSON file: 'sfdx-project.json';
    SOURCE_API_NAME = props.sourceApiVersion;
    sh "rm -rf ui-delta"
    sh "mkdir -p ui-delta"
    sh "sfdx sgd:source:delta -f origin/${featureBranchName} -s ./extensions/cq-form -o ui-delta --api-version ${SOURCE_API_NAME}"
    staticAnalysisCQUI()
    }

    if (cqDeltaPath) {
      println "${featureBranchName}"
      sh "rm -rf cq-delta"
      sh "echo ./${cqDeltaPath} "
      sh "mkdir -p cq-delta"
      println("Run staic Analysis of CQ Source Code ")
      staticAnalysisCQ()
    }

}
def uitests(){
  println "running ui tests"
   try{
     sh "npm install"
     sh "npm run test:unit:coverage"
   }catch(Exception ex){
      error(ex.getMessage());
   }
}
def staticAnalysisCQ(){
  println "Run Static Analysis of CQ Source Code"
            sh "mkdir -p cq-delta/fullSource";
            sh "sfdx force:project:create -d cq-delta --projectname . --defaultpackagedir fullSource --template empty"
            
            sh "cp -a src cq-delta/fullSource"
            dir('cq-delta'){
                def project = readJSON file: 'sfdx-project.json';
                project.sourceApiVersion = SOURCE_API_NAME;
                writeJSON file: 'sfdx-project.json', json: project, pretty: 4
                sh "sfdx force:source:convert -x package/package.xml -d metadata"
            }

            try{
                sh "npm install";
                //pmd, eslint scan for cq-delta
                sh "sfdx scanner:run --engine 'pmd,retire-js,cpd' --pmdconfig './.pmdruleset.xml'  --format html --target cq-delta/metadata -o cq-delta/pmdscanresult.html --severity-threshold 2"
                sh "sfdx scanner:run --engine 'eslint-lwc' --eslintconfig './.eslintrc.json'  --format html --target 'cq-delta/metadata/lwc/**/*.js' -o cq-delta/eslintscanresult.html --normalize-severity --severity-threshold 1 --exclude=./.eslintignore";
            }catch(Exception ex){
                error(ex.getMessage());
            }finally{
                // Publish the result
                publishHTML([
                    allowMissing: false, 
                    alwaysLinkToLastBuild: true, 
                    keepAll: true, 
                    reportDir: 'cq-delta', 
                    reportFiles: 'pmdscanresult.html, eslintscanresult.html', 
                    reportName: 'Source Scanner Report of cq', 
                    reportTitles: 'Apex Report, UI Report'
                ]);
            }
}

def staticAnalysisCQUI(){
  println "Run Static Analysis of CQUI Source Code"
            sh "mkdir -p ui-delta/fullSource";
            sh "sfdx force:project:create -d ui-delta --projectname . --defaultpackagedir fullSource --template empty"
            
            sh "cp -a extensions/cq-form/force-app/. ui-delta/fullSource/"
            sh "cp -a extensions/cq-form/dependent/. ui-delta/fullSource/"

            dir('ui-delta'){
                def project = readJSON file: 'sfdx-project.json';
                project.sourceApiVersion = SOURCE_API_NAME;
                writeJSON file: 'sfdx-project.json', json: project, pretty: 4
                sh "sfdx force:source:convert -x package/package.xml -d metadata"
            }
            try{
                sh "sfdx scanner:run --engine 'pmd,retire-js' --pmdconfig 'extensions/cq-form/.cquipmdruleset.xml'  --format html --target ui-delta/metadata -o ui-delta/pmdscanresult.html --severity-threshold 2";
                sh "sfdx scanner:run --engine 'eslint-lwc' --eslintconfig 'extensions/cq-form/force-app/main/default/lwc/.eslintrc.json'  --format html --target 'ui-delta/metadata/lwc/**/*.js' -o ui-delta/eslintscanresult.html --normalize-severity --severity-threshold 1";
            }catch(Exception ex){
                error(ex.getMessage());
            }finally{
                //publish the result
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'ui-delta',
                    reportFiles: 'pmdscanresult.html, eslintscanresult.html',
                    reportName: 'Source Scanner report of cqui',
                    reportTitles: 'Apex Report, UI Report'
                ]);
            }
}

