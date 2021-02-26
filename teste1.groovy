node('master'){

    stage('Stage 1') {
        sh './mvnw package'

    }
}   