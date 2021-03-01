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
        throw new Exception(exec)
        
    }finally{      
 
      print 'Compilação Funcionou!!!! o/'
       
    } 
    stage('Teste Sonarqube'){
      sh 'mvn sonar:sonar'
      sh '-Dsonar.projectKey=projetotask2'  
      sh '-Dsonar.host.url=http://localhost:9000'
      sh '-Dsonar.login=0402c6931ae5d3562aa79282d31fe30d5910ff46'
    }
}