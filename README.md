A Question and Answering system for the DCAT vocabulary with a Social Media Bot as user interface developed as part of a bachelor thesis.


# DCAT QA System

See [qa-system readme](dcat-qa-system/README.md)


# Twitter Bot

See [twitter-bot readme](sninterface-botkit/twitter-bot/README.md)

## Botkit Twitter Adapter

The code for the Twitter adapter for botkit can be found on [Github](https://github.com/martenls/botkit/tree/twitter-adapter/packages/botbuilder-adapter-twitter) but a copy is included in the project as well under `sninterface-botkit/twitter-bot/botbuilder-adapter-twitter`

# Web Bot

See [web-bot readme](sninterface-botkit/web-bot/README.md)

# Nginx Reverse Proxy Config

See [nginx config](nginx-config/README.md)


# Local Building and Deployment of all Components

Prerequisites:
  - Docker
  - Docker-Compose
  - maven
  - ngrok (only for twitter bot)
  - npm


Steps:

## 1. Build Docker Images

### qa-system:

Run in `dcat-qa-system`:


    mvn clean package -DskipTests
    docker build -t qa-system .

See [qa-system readme](dcat-qa-system/README.md)

### sninterface-twitter:

Run in `sninterface-botkit/twitter-bot`:

    
    docker build -t twitter-bot .

See [twitter-bot readme](sninterface-botkit/twitter-bot/README.md). 

### sninterface-web:

Run in `sninterface-botkit/web-bot`:

    docker build -t web-bot .

See [web-bot readme](sninterface-botkit/web-bot/README.md)


## 2. Copy and Fill out .env Templates

**qa-system**:

change `SPARQL_ENDPOINT` when running your own Fuseki instance (see [Running your own fuseki triplestore instance](#running-your-own-fuseki-triplestore-instance))

**twitter-bot** (can be skipped if web interface is sufficient):

fill in (see [Twitter Bot Setup](docs/twitter-bot-account-creation.md) for how to obtain these values)
- `TWITTER_CONSUMER_KEY`
- `TWITTER_CONSUMER_SECRET`
- `TWITTER_TOKEN`
- `TWITTER_TOKEN_SECRET` 
- `TWITTER_WEBHOOK_ENV`


start ngrok with

    ngrok http 3000

and fill in the generated https address at `WEBHOOK_URL` (this will expose your local port 3000 to the internet so Twitter can send events to your application)

**web-bot:**

*nothing to be done*


## 3. Create and Start All Containers

run 

    docker-compose up

run 

    docker-compose up sninterface-web qa-system elastic

to run everything except the twitter bot 


## 4. Ask Bot Questions via Web Interface or the Registered Twitter Account

The web interface should be available under http://localhost:3000 (unless the port was changed)

## Running Your Own Fuseki Triplestore Instance

If the fuseki endpoint at <https://openbot.cs.upb.de/fuseki/> is not available anymore, it may be required to locally deploy a fuseki instance providing the data that should be queried by the qa-system.

The docker page of the [Jena Fuseki 2 image](https://hub.docker.com/r/stain/jena-fuseki) explains how to set up a fuseki instance as docker container and import data. The `docker-compose.local.yml` also contains a comment with configuration for the fuseki docker, which can be used as a starting point.

The current opal graph data is available at <https://hobbitdata.informatik.uni-leipzig.de/OPAL/OpalGraph/DCAT-QA/>.

