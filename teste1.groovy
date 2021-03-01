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
            sh 'pwd'
            sh 'chmod +x mvnw'
            withSonarQubeEnv('sonar') {
            sh './mvnw clean compile package test'
            }
 
        }
     
 
    }catch (exec)  {
        currentBuild.result = 'FAILURE'
        throw new Exception(exec)
        
    }finally{      
 
      print 'Compilação Funcionou!!!! o/'
       
    } 

       stage("Quality Gate") { 
           withSonarQubeEnv('sonar') {
            timeout(time: 5, unit: 'MINUTES') { 
                def qualityGate = waitForQualityGate() 
                if (qualityGate.status != 'OK') {
                    error "O código não está de acordo com as regras do Sonar: ${qualityGate.status}"
                }
            }
           }
        }

}