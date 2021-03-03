node('master'){
    try{
       stage('Limpeza do cache') {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
 
       }
 
        stage('Compilação do maven e Teste'){
            checkout scm
            sh 'chmod +x mvnw'
            sh './mvnw clean compile package test'
        }
     
 
    }catch (exec)  {
        currentBuild.result = 'FAILURE'
        throw neAw Exception(exec)
    }finally{        
    } 

    stage('SonarQube analysis') {
			withSonarQubeEnv("sonarqube") {
 				sh './mvnw sonar:sonar -Dsonar.projectKey=projetotask2 -Dsonar.host.url=https://host.docker.internal:9000 -Dsonar.login=0402c6931ae5d3562aa79282d31fe30d5910ff46'
			}
		}

    stage('Armazenar imagem no Nexus') {
        sh 'docker images'
        sh 'apt remove golang-docker-credential-helpers'
        sh 'docker login localhost:8083 -u admin -p admin'
        //sh 'docker login localhost:8083'
        sh 'docker push localhost:8083/petclinic:1.0'
    }   
}       