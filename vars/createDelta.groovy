def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'default'
  def uiDeltaPath = args.uiDeltaPath ?: ''
  def cqDeltaPath = args.cqDeltaPath ?: ''
  if (uiDeltaPath) {
    def props = readJSON file: 'sfdx-project.json';
    SOURCE_API_NAME = props.sourceApiVersion;
    sh "rm -rf ui-delta"
    sh "mkdir -p ui-delta"
    sh "sfdx sgd:source:delta -f origin/${featureBranchName} -s ./extensions/cq-form -o ui-delta --api-version ${SOURCE_API_NAME}"
    }

    if (cqDeltaPath) {
      println "$featureBranchName"
      sh "rm -rf cq-delta"
      sh "echo ./${cqDeltaPath} "
      sh "mkdir -p cq-delta"
      println("Run staic Analysis of CQ Source Code ")
      staticAnalysis()
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
def staticAnalysis(){
    dir('cq-delta'){
      sh "touch sfdx-project.json"
           }
}

