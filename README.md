

#  ![](./img/springBoot.png)Spring Boot  ![](./img/bucket.png)AWS SDK (s3) ![](./img/ubuntu.png) linux



## Description

Ce projet est un exemple d'utilisation de [AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html) dans un microservice **spring boot** permettant d'effectuer des opérations de manipulation d'un **bucket S3**.



## Détail technique

extrait du fichier pom.xml

```xml
<properties>
    <awssdk-s3.version>2.14.14</awssdk-s3.version>
</properties>
<dependencies>    
	<dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
    </dependency>
    <dependency>
        <artifactId>apache-client</artifactId>
        <groupId>software.amazon.awssdk</groupId>
    </dependency>
</dependencies>
<dependencyManagement>
	<dependencies> 
        <dependency>
             <groupId>software.amazon.awssdk</groupId>
             <artifactId>s3</artifactId>
             <version>${awssdk-s3.version}</version>
        </dependency>
 		<dependency>
             <groupId>software.amazon.awssdk</groupId>
             <artifactId>apache-client</artifactId>
             <version>${awssdk-s3.version}</version>
 		</dependency>
 	</dependencies>
</dependencyManagement>
```



## Utilisation en local

### prérequis

#### aws cli poste linux (ubuntu)

documentation aws cli : https://docs.aws.amazon.com/fr_fr/cli/latest/userguide/cli-services-s3-commands.html

```shell
# Installation
apt-get update
sudo apt-get install awscli
```



```shell
ifconfig
#recupérer l'ip x.x.x.x
>>> docker0: -------
        inet x.x.x.x  netmask 255.255.0.0  broadcast 172.17.255.255
```

mettre à jour le fichier src/main/resources/**application-local.yml** et remplacer par x.x.x.x

```properties
s3:
  url: http://x.x.x.x:4566
  ....
```

**NB**: *x.x.x.x correspond à votre ip :-)*

### Démarrer votre projet

la conteneurisation du projet (voir fichier pom du projet) s'appuie sur le plugin maven [fabric8](https://dmp.fabric8.io/)

```xml
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.34.1</version>
    ...
<plugin>
```



pour lancer votre projet en local exécuter les commandes suivantes:



```shell
# se positionner dans le répertoire docker/
cd docker/
# exécuter la commande
docker-compose -p localaws up -d

# attendre que le container soit up avant d'exécuter la commande suivante

# à l'aide de aws cli installé au préalable, créer un bucket nommé local-s3-bucket
aws --endpoint-url=http://localhost:4566 s3 mb s3://local-s3-bucket

# revenir à la racine du projet (pwd: )
cd ..

# créer l'image docker du projet (cf plugin fabric8)
mvn clean package docker:build

# lancer le container avec le profil local nom du conteneur: my-s3-web correspondance des port 8080:8080
docker run --name my-s3-web -d -e "SPRING_PROFILES_ACTIVE=local" -p 8080:8080 s3-web:0.0.1-SNAPSHOT
```



Ensuite ouvrer votre client **REST** préféré,  pour ma part j'utilise [insomnia](https://insomnia.rest/) 

GET : http://localhost:8080/listbucket



![](/home/xgueret/Developpement/MyWorkspace/SpringBoot/s3-web/img/exemple_listBucker.png)





>  **soon doc swagger ;-)**