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

        stage('SonarQube analysis') {
			withSonarQubeEnv("sonarqube") {
 				sh './mvnw sonar:sonar -Dsonar.projectKey=projetotask2 -Dsonar.host.url=https://host.docker.internal:9000 -Dsonar.login=0402c6931ae5d3562aa79282d31fe30d5910ff46'
			}
		}

        stage('Armazenar imagem no Nexus') {
            sh 'docker images'
            //sh 'apt remove golang-docker-credential-helpers'
            sh 'docker build --no-cache -t localhost:8083/petclinic:1.${env.BUILD_ID} .'
            sh 'docker login localhost:8083 -u admin -p admin'
            //sh 'docker login localhost:8083'
            sh 'docker push localhost:8083/petclinic:1.1'
            sh 'docker logout'

        }   


        stage('Transformar Conteiners em Imagens'){
            sh 'docker ps'
            sh 'docker commit jenkins larissasouzapinto/jobjenkins:lts'
            sh 'docker commit nexus larissasouzapinto/jobjenkins_nexus:lts'
            sh 'docker commit sonarqube larissasouzapinto/jobjenkins_sonarqube:lts'
            sh 'docker commit postgres larissasouzapinto/jobjenkins_postgres:lts'
            sh 'docker commit infra_nginx-nexusproxy_1 larissasouzapinto/jobjenkins_nginx:lts'   
        }

        stage('Carregar imagem do Job no DockerHub'){       
            sh 'docker login -u larissasouzapinto -p adminadmin'

            sh 'docker tag larissasouzapinto/jobjenkins:lts larissasouzapinto/task2:jobjenkins'
            sh 'docker push larissasouzapinto/task2:jobjenkins'

            sh 'docker tag larissasouzapinto/jobjenkins_nexus:lts larissasouzapinto/task2:jobjenkins_nexus'
            sh 'docker push larissasouzapinto/task2:jobjenkins_nexus'

            sh 'docker tag larissasouzapinto/jobjenkins_sonarqube:lts larissasouzapinto/task2:jobjenkins_sonarqube'
            sh 'docker push larissasouzapinto/task2:jobjenkins_sonarqube'

            sh 'docker tag larissasouzapinto/jobjenkins_postgres:lts larissasouzapinto/task2:jobjenkins_postgres'
            sh 'docker push larissasouzapinto/task2:jobjenkins_postgres'

            sh 'docker tag larissasouzapinto/jobjenkins_nginx:lts larissasouzapinto/task2:jobjenkins_nginx'
            sh 'docker push larissasouzapinto/task2:jobjenkins_nginx'

            sh 'docker logout'

        }

    }catch (exec)  {
        currentBuild.result = 'FAILURE'
        throw new Exception(exec)
        
    }finally{        
     
     }  
}       