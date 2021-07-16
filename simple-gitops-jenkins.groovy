pipeline
{
    agent
    {
      kubernetes
      {
        label 'test docker'
        yaml """
apiVersion: v1
kind: Pod
spec:
  volumes:
  - name: docker-socket
    emptyDir: {}
  containers:
  - name: docker
    image: docker:19.03.1
    command:
    - sleep
    args:
    - 99d
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run
  - name: docker-daemon
    image: docker:19.03.1-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run
"""
      }
    }
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
                echo "Build..."
                sh "docker build -t test_image:1.0 ."
                sh "ls -l"
            }
        }
        stage ('Test')
        {
            steps
            {
                sh "docker run -d -p 80:80 test_image:1.0"
                sh "curl localhost:80"
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
