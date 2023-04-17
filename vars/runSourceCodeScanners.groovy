def runSourceCodeScanners(String pmdRulesetFile, String eslintConfigFile, String targetDir, String pmdReportFile, String eslintReportFile) {
  try {
    sh "sfdx scanner:run --engine 'pmd,retire-js' --pmdconfig '${pmdRulesetFile}' --format html --target '${targetDir}' -o '${pmdReportFile}' --severity-threshold 2"
    sh "sfdx scanner:run --engine 'eslint-lwc' --eslintconfig '${eslintConfigFile}' --format html --target '${targetDir}/lwc/**/*.js' -o '${eslintReportFile}' --normalize-severity --severity-threshold 1"
  } catch (Exception ex) {
    error(ex.getMessage())
  } finally {
    publishHTML([
      allowMissing: false,
      alwaysLinkToLastBuild: true,
      keepAll: true,
      reportDir: '${targetDir}',
      reportFiles: '${pmdReportFile}, ${eslintReportFile}',
      reportName: 'Source Scanner report',
      reportTitles: 'PMD Report, ESLint Report'
    ])
  }
}
