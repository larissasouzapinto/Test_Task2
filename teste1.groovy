node('master'){

    stage('Stage 1') {
        checkout scm
        sh './mvnw clean compile package test'

    }
}   