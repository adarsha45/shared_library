def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'default'
  def uiDeltaPath = args.uiDeltaPath ?: ''
  def cqDeltaPath = args.cqDeltaPath ?: ''
  if (uiDeltaPath) {
    sh "rm -rf ${uiDeltaPath}"
    sh "echo ${uiDeltaPath} feature:${featureBranchName}"
    sh "mkdir -p ${uiDeltaPath}"
    uitests()
    }

    if (cqDeltaPath) {
      sh "rm -rf ${cqDeltaPath}"
      sh "echo ${cqDeltaPath} feature:${featureBranchName}"
      sh "mkdir -p ${cqDeltaPath}"

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
