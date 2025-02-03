# microservices-java


- terraform init
- terraform apply


## Push image to artifact 

```shell

docker login -u $ARTIFACTORY_USER -p $ARTIFACTORY_PASSWORD $ARTIFACTORY_URL
docker tag microservices-java $ARTIFACTORY_URL/microservices-java
docker push $ARTIFACTORY_URL/microservices-java

```
```shell
  docker images | grep inventory-service  
  mvn spring-boot:build-image   
  docker images | grep inventory-service  
  docker push $ARTIFACTORY_URL/inventory-service
```


gcloud auth print-access-token

mvn spring-boot:build-image  
docker images | grep inventory-service 