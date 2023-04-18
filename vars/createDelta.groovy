def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'default'
  def uiDeltaPath = args.uiDeltaPath ?: 'ui-delta'
  def cqDeltaPath = args.cqDeltaPath ?: 'cq-delta'
  if (uiDeltaPath) {
    sh "echo ${uiDeltaPath} feature:${featureBranchName}"
    }

    if (cqDeltaPath) {
      sh "echo ${cqDeltaPath} feature:${featureBranchName}"
    }

}
