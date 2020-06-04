# qa-system

A Spring Boot application that:
  - implements a question and answering system for the dcat vocabulary
  - provides a rest endpoint for sending questions
  - needs an elastic search instance for entity recognition
  - needs a sparql endpoint for querying data

## Build

Prerequisites:
  - maven
  - jdk 11

Run

    mvn package

to build jar


## Build Docker

Run

    docker build -t qa-system .

to build a docker image

## Environments

The following environment variables can be set to overwrite the default values in the `application.yml`

    ES_HOST=localhost
    ES_PORT=9200
    ES_PROPERTY_INDEX=propdcat3
    ES_CLASS_INDEX=classdcat3
    ES_LAUNUTS_INDEX=launuts

    SPARQL_ENDPOINT=https://openbot.cs.upb.de/fuseki/opal/query

    ONTOLOGY_FILE=src/data/dcat2+german-labels.rdf
    LAUNUTS_FILE=src/data/launuts.ttl
    TEMPLATES_FILE=src/data/templates.txt

## Run Locally

Prerequisites:
  - jdk/jre 11
  - elastic search index
  - apache jena fuseki triplestore holding data described with the dcat vocabulary



Run

    java -jar target/qa-system-0.0.1-SNAPSHOT.jar

the rest endpoint should then be available under `http://localhost:8080/qa`

questions can be sent, for example, with curl:

    curl -G --data-urlencode "question=Which formats are available?" localhost:8080/qa


Run Docker

Run

    docker run -p 8080:8080 qa_system

or use provided `docker-compose.yml`