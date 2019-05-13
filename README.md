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
Run `mvn clean install` to build and compile the project. This will also generate all the necessary Thrift file source code for the backend.

#### Spring Boot Backend
Simply run the `ChatApplication.java` as a normal Java application. This will start the server. See the `resources/application.properties` file for server details.

#### Vue Frontend

// TODO: Generate the Thrift files for the frontend using
