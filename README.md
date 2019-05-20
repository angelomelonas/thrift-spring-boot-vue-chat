# Thrift Chat
A very simple chat application using Spring Boot, Vue.js (in TypeScript) and Apache Thrift.

## Requirements

##### Thrift
1. Go to [the Apache Thrift releases page](https://thrift.apache.org/download)
2. Select the latest release version
3. Under "Releases", select and download the applicable file (e.g., for Windows `thrift-0.12.0.exe`)
4. Copy the downloaded file add it to the `${project.root}/thrift` directory.
5. Configure the `${backend}/pom.xml` to point to the correct Thrift release (e.g., `thrift-0.12.0.exe`)

## Install and Run

##### Thrift Chat
Execute `mvn clean install` to build and compile the project. This will also generate all the necessary Thrift file source code for the backend and frontend.

#### Spring Boot Backend
Simply run the `ChatApplication.java` as a normal Java application. This will start the server. See the `resources/application.properties` file for server details. By default it runs on http://localhost:8000.

#### Vue Frontend

To specifically generate Thrift files for the front-end client, navigate to the `frontend` directory and run the following command:
    
    $ npm run thrift
    
To serve the frontend code in a development environment, execute:

    $ npm run serve

## References and Resources
* [Type-Safe Microservices in Node.js with Thrift and TypeScript](https://medium.com/@KevinBGreene/type-safe-microservices-in-node-js-with-thrift-and-typescript-be01454e9e7d)
* [Apache Thrift Java Tutorial](https://thrift.apache.org/tutorial/java)
* [Apache Thrift JavaScript Tutorial](https://thrift.apache.org/tutorial/js)
* [creditkarma/thrift-typescript](https://github.com/creditkarma/thrift-typescript)
* [Spring: Enabling Cross Origin Requests for a RESTful Web Service](https://spring.io/guides/gs/rest-service-cors/)
