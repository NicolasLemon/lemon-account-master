FROM java:8
MAINTAINER NicolasLemon
ADD ruoyi-admin.jar lam-master.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/.urandom","lam-master.jar"]