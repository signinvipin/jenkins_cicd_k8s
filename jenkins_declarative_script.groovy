#!/usr/bin/env groovy

def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label '' }

    tools {
        maven 'apache-maven-3.9.10' // 👈 Make sure this tool is configured in Jenkins
    }

    environment {
        VER = VersionNumber([
            versionNumberString : '${BUILD_YEAR}.${BUILD_MONTH}.${BUILD_DAY}.ARTECH-${BUILDS_ALL_TIME}', 
            projectStartDate : '2019-8-27'
        ])
        imageName = "pipe"
        dockerRegistry = "signinvipin"
    }

    stages {
        stage('Clone the Git Repository') {
            steps {
                script {
                    currentBuild.displayName = VER
                }
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[
                        credentialsId: 'git-credentials',
                        url: 'https://github.com/signinvipin/jenkins_cicd_k8s.git'
                    ]]
                ])
            }
        }

        stage('Debug PATH') {
            steps {
                sh 'echo $PATH'
            }
        }

        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

/*        stage('Docker Build & Push') {
            steps {
               script {
                    sh '''
                       # Enable buildx
                        docker buildx create --use --name multiarch || docker buildx use multiarch

                        # Build multi-arch image and push to Docker Hub
                        docker buildx build \
                          --platform linux/amd64,linux/arm64 \
                          -t ${dockerRegistry}/${imageName}:${VER} \
                          --push .
                    '''
                }
           }
        }
*/
        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        def app = docker.build("${dockerRegistry}/${imageName}:${VER}")
                        app.push()
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    // Replace ${VER} in the deployment YAML using envsubst
                    sh 'envsubst < app-deployment.yaml > app-deployment-final.yaml'
                    // Optional: Display the final YAML
                    // sh 'cat app-deployment-final.yaml'
                    
                    // Deploy to Kubernetes using Jenkins Kubernetes plugin
                    kubernetesDeploy(
                        configs: "app-deployment-final.yaml",
                        kubeconfigId: "jenkinsCluster"
                    )
                }
            }
        }
    }
}
