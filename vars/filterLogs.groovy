#!/usr/bin/env groovy

import org.apache.commons.lang.StringUtils

def call(String filter_string, int occurrence) {
    def logs = currentBuild.rawBuild.getLog(10000).join('\n')
    int count = StringUtils.countMatches(logs, filter_string);
    sh """ echo `this is oboe` """
    if (occurrence == 50) {
        currentBuild.result='UNSTABLE'
    }

}
def call2(){
  sh " echo `this is adarsha`"
}
