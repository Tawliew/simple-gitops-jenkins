pipeline
{
    agent 
    {
        kubernetes {
      label 'spring-petclinic-demo'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
metadata:
labels:
  component: ci
spec:
  # Use service account that can deploy to all namespaces
  serviceAccountName: cd-jenkins
  containers:
  - name: maven
    image: maven:latest
    command:
    - cat
    tty: true
    volumeMounts:
      - mountPath: "/root/.m2"
        name: m2
  - name: docker
    image: docker:latest
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
  volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock
    - name: m2
      persistentVolumeClaim:
        claimName: m2
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
