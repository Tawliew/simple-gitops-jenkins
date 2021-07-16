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
            }
        }
        stage ('Test')
        {
            steps
            {
                echo "Test..."
            }
        }
        stage ('Publish')
        {
            steps
            {
                echo "Publish..."
            }
        }
        stage ('CommitInfraRepo')
        {
            steps
            {
                echo "Clone APP repo"
                git branch: 'main', url: 'https://github.com/Tawliew/manifests-demo-argocd'
                sh "ls -l"
                echo "Build File"
                sh "ls -l"
                script 
                    {                       
                        def filename = 'deployment.yaml'
                        def data = readYaml file: filename

                        // Change something in the file
                        data.spec.spec.containers.image.tag = nginx

                        sh "rm $filename"
                        writeYaml file: filename, data: data
                    }
                sh "cat deployment.yaml"
            }
        }
    }
}
