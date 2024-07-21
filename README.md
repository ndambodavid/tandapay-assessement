## tandapay assessment

## Setup
- clone the project
    ```sh 
    git clone https://github.com/ndambodavid/tandapay-assessement
    ```
- set the daraja API keys in ``` integration-service/src/main/resources/application.properties```
    ```sh
    env.consumerKey=""
    env.consumerSecret=""
    env.secretCredential=""
    env.initiatorName=""
    env.resultCallbackUrl="http://${domain/IP:8090}/api/integration/result/callback"
    env.statusCallbackUrl="http://${domain/IP:8090}/api/integration/status/callback"
    env.shortCode=""
    ```
  
- set your keycloak realm name in ```core/src/main/resources/application.yml```
    ```shell
    spring:
  application:
    name: core-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: "http://localhost:9098/realms/${REALM_NAME}"
    ```
- start the docker services (mongodb, kafka and keycloak)

    ```shell
    docker compose up -d
    ```
  
- visit ```http://localhost:9098/admin``` to setup your keycloak realm and obtain client creedentials to use for authentication
- Build the core service 
    ```sh
    cd core
    mvn clean
    ```
- Build the integration service
    ```shell
    cd integration-service
    mvn clean
    ```
# Initiate a payment request to the core service
- API ```http://domain/IP:8040/api/core```
- method ```POST```
- payload 

    ```sh
    {
    "amount": 50.00,
    "mobileNumber": "2547xxxxxxxx"
    }
    ```
