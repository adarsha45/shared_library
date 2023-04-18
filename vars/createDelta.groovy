def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'default'
  def uiDeltaPath = args.uiDeltaPath ?: ''
  def cqDeltaPath = args.cqDeltaPath ?: ''
  if (uiDeltaPath) {
    sh "echo ${uiDeltaPath} feature:${featureBranchName}"
    }

    if (cqDeltaPath) {
      sh "echo ${cqDeltaPath} feature:${featureBranchName}"
    }

}
