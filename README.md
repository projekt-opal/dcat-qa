# bachelor-thesis-code

A Question and Answering system for the DCAT vocabulary with a Social Media Bot as user interface.
[Thesis Repository](https://git.cs.upb.de/martenls/bachelor-thesis)

# nginx-config

Config files for nginx reverse proxy on the openbot vm.
Including Dockerfile to build custom nginx container.

## Build

Build yourself with (run in `nginx-config` folder)
```
docker build  -t nginx-openbot .
```
 or pull from gitlab container registry with 
```
docker login hub.cs.upb.de
docker pull hub.cs.upb.de/martenls/bachelor-thesis-code/nginx-openbot
```

## Run

On openbot vm with
```
docker run --restart unless-stoped --name nginx -p '80:80' -p '443:443' -v '/etc/nginx/ssl:/etc/nginx/ssl' nginx-openbot
```
 or use provided `docker-compose.yml`.



# qa-system

See [qa-system readme](qa-system/README.md)


# sninterface-botkit

See [sninterface readme](sninterface-botkit/README.md)


# Local Deployment of all components with Docker Compose

Prerequisites:
  - Docker
  - Docker-Compose
  - maven
  - ngrok (only for twitter bot)


Steps:

## 1. Build docker container

qa-system:

    cd qa-system
    mvn package
    docker build -t qa-system .

sninterface-twitter:

    cd sninterface-botkit/twitter-bot
    docker build -t sninterface-botkit-twitter . 

sninterface-web:

    cd sninterface-botkit/web-bot
    docker build -t sninterface-botkit-web .

## 2. Copy and fill out .env templates

qa-system:

change `SPARQL_ENDPOINT` when running your own Fuseki instance (see [Running your own fuseki triplestore instance](#running-your-own-fuseki-triplestore-instance))

twitter-bot (can be skipped if web interface is sufficient):

fill in (see [Twitter Bot Setup](docs/twitter-bot-account.md) for how to obtain these values)
- `TWITTER_CONSUMER_KEY`
- `TWITTER_CONSUMER_SECRET`
- `TWITTER_TOKEN`
- `TWITTER_TOKEN_SECRET` 
- `TWITTER_WEBHOOK_ENV`


start ngrok with

    ngrok http 3000

and fill in the generated https address at `WEBHOOK_URL` (this will expose your local port 3000 to the internet so Twitter can send events to your application)

web-bot:

*nothing to be done*


## 3. create and start all containers

run 

    docker-compose -f docker-compose.local.yml up

run 

    docker-compose -f docker-compose.local.yml up sninterface-web qa-system elastic

to run everything except the twitter bot 


## 4. ask bot questions via web interface or the registered twitter account

## Running your own fuseki triplestore instance

If the fuseki endpoint at <https://openbot.cs.upb.de/fuseki/> is not available anymore, it may be required to locally deploy a fuseki instance providing the data that should be queried by the qa-system.

The docker page of the [Jena Fuseki 2 image](https://hub.docker.com/r/stain/jena-fuseki) explains how to set up a fuseki instance as docker container and import data. The `docker-compose.local.yml` also contains a comment with configuration for the fuseki docker, which can be used as a starting point.

The current opal graph data is available at <https://hobbitdata.informatik.uni-leipzig.de/OPAL/OpalGraph/>.