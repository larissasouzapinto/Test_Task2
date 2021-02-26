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
            sh './mvnw clean compile package test'
 
        }
    catch (exec)
    {
        currentBuild.result = 'FAILURE'
        throw new Exception(exec)
        
    }finally{
       
       
    } 
}
}