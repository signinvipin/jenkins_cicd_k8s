#!/usr/bin/env groovy
def STATUS = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'danger']

pipeline {
    agent { label '' }
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
        // compile and package source code to .jar/.war/other using maven 
        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

    // Only used when deploying artifact to nexus repository
    //    stage('Deploy Artifact to Nexus') {
    //        steps {
    //            sh 'mvn deploy -DskipTests'
    //        }
    //    }


        stage('Docker Build & Push') {
            steps {
                script {
                    // Use Docker Pipeline Plugin's withRegistry to securely login & push && dockerhub-credentials is from jenkins credentials
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
                        configs: "app-deployment.yaml" , 
                        // from jenkins credentials - "jenkinsCluster"
                        kubeconfigId: "jenkinsCluster"
                    )
                }
            }
        }

    }
}
