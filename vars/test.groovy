def call(){
  pipeline {
        agent any
        parameters {
            booleanParam(name: 'IS_VALIDATION', defaultValue: pipelineParams.IS_VALIDATION, description: 'Validates Packages in org by deploying source')
            booleanParam(name: 'MANAGED_BETA', defaultValue: pipelineParams.MANAGED_BETA, description: 'Validates package while creating version')
        }}
}
