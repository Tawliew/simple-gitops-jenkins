pipeline
{
    agent
    {
      kubernetes
      {
        label 'undercover-hudson'
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
                git branch: 'develop', url: 'https://github.com/Tawliew/simple-project-demo-argocd'
                sh "ls -l"
            }
        }
        stage ('Test')
        {
            steps
            {
                echo "Testing..."
            }
        }
        stage ('Build')
        {
            steps
            {
                echo "Build..."
                container('docker')
                {
                  sh 'docker version'
                  sh 'docker build -t tawliew/techhour:$BUILD_NUMBER .'

                }
            }
        }
        stage ('Publish')
        {
            environment
            {
              DOCKERHUB_CREDS = credentials('dockerhub')
            }
            steps
            {
                echo "Publish..."
                container('docker')
                {
                  sh 'docker images'
                  sh "docker login --username $DOCKERHUB_CREDS_USR --password $DOCKERHUB_CREDS_PSW && docker push tawliew/techhour:$BUILD_NUMBER"
                }
                sh "ls -l"
            }
        }
        stage ('DeployInfraCodeToRepo')
        {
            steps
            {
                echo "Clone APP repo"
                git branch: 'develop', credentialsId: 'github', url: 'https://github.com/Tawliew/manifests-demo-argocd'
                echo "Build File"
                script 
                    {                       
                        def filename = 'deployment.yaml'
                        def data = readYaml file: filename

                        data.spec.template.spec.containers[0].image = "tawliew/techhour:$BUILD_NUMBER"

                        sh "rm $filename" //Preciso remover para o arquivo para conseguir escrever com writeYaml
                        writeYaml file: filename, data: data
                    }
                sh  'git config --global user.email "lfluiz.lf@gmail.com"'
                sh  'git config --global user.name "tawliew"'
                sh 'cat deployment.yaml'
                sh 'git add deployment.yaml'
                sh 'git commit -m "📢 Jenkins auto-commit 👷‍♂️"'
                withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'pass', usernameVariable: 'user')])
                {
                  sh("git push https://$user:$pass@github.com/Tawliew/manifests-demo-argocd")
                }
            }
        }
    }
}
