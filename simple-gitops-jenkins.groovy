pipeline
{
    agent any

    stages 
    {
        stage ('Clone Repo')
        {
            steps
            {
                echo "Clone APP repo"
                git branch: 'main', url: 'https://github.com/Tawliew/simple-project-demo-argocd'
                sh "ls -l"
            }
        }
        stage ('Build')
        {
            steps
            {
                echo "Build teste..."
                sh "ls -l"
            }
        }
        stage ('Test')
        {
            steps
            {
                echo "Test..."
                sh "ls -l"
            }
        }
        stage ('Publish')
        {
            steps
            {
                echo "Publish..."
                sh "ls -l"
            }
        }
        stage ('CommitInfraRepo')
        {
            steps
            {
                echo "Clone APP repo"
                git branch: 'develop', url: 'https://github.com/Tawliew/manifests-demo-argocd'
                echo "Build File"
                script 
                    {                       
                        def filename = 'deployment.yaml'
                        def data = readYaml file: filename

                        data.spec.template.spec.containers[0].image = "httpd"

                        sh "rm $filename" //Preciso remover para o arquivo para conseguir escrever com writeYaml
                        writeYaml file: filename, data: data
                    }
                
            }
        }
    }
}
