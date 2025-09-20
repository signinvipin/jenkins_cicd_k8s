FROM anapsix/alpine-java
MAINTAINER myNAME
CMD ["java","-jar","/var/lib/jenkins/workspace/pipeline-end-to-end-testing/ECSCode/target/myapp-1.0-SNAPSHOT.jar"]