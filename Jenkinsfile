#!groovy

node {

    stage('Checkout') {
        checkout scm
    }
    
    stage('Build') {
    	sh './gradlew build'
    	archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
    }
}
