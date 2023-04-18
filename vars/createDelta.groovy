def call(Map args){
  def featureBranchName = args.featureBranchName ?: 'default'
  def uiDeltaPath = args.uiDeltaPath ?: ''
  def cqDeltaPath = args.cqDeltaPath ?: ''
  if (uiDeltaPath) {
    sh "rm -rf ${uiDeltaPath}"
    sh "echo ${uiDeltaPath} feature:${featureBranchName}"
    sh "mkdir -p ${uiDeltaPath}"
    }

    if (cqDeltaPath) {
      sh "rm -rf ${cqDeltaPath}"
      sh "echo ${cqDeltaPath} feature:${featureBranchName}"
      sh "mkdir -p ${cqDeltaPath}"

    }

}
