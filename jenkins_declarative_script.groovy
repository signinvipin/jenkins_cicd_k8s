#!/usr/bin/env groovy

def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label '' }

    tools {
        maven 'Apache Maven 3.9.10'  // 👈 Match the name configured in Global Tool Configuration
    }

    environment {
        VER = VersionNumber([
            versionNumberString : '${BUILD_YEAR}.${BUILD_MONTH}.${BUILD_DAY}.ARTECH-${BUILDS_ALL_TIME}', 
            projectStartDate : '2019-8-27'
        ]);
        imageName = "pipe";
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

        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'  // 👈 No sudo needed
            }
        }

    // Optional stage for publishing to Nexus (commented out)
    //    stage('Deploy Artifact to Nexus') {
    //        steps {
    //            sh 'mvn deploy -DskipTests'
    //        }
    //    }

        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        def app = docker.build("signinvipin/pipe:${VER}")
                        app.push()
                    }
                }
            }
        }

        stage('Deploying App to Kubernetes') {
            steps {
                script {
                    kubernetesDeploy(
                        configs: "app-deployment.yaml", 
                        kubeconfigId: "jenkinsCluster"
                    )
                }
            }
        }
    }
}
