# sninterface-botkit: twitter bot

A nodejs app that:
- subscribes to Twitters account-activity api
- passes all questions asked in tweets or direct-messages to the qa-system
- replies to tweets and dms with answers fetched from the qa-system
- implements introductory dialogs




## Build Docker

Build yourself with (run in `twitter-bot` folder)
```
docker build  -t sninterface-twitter .
```
 or pull from gitlab container registry with 
```
docker login hub.cs.upb.de
docker pull hub.cs.upb.de/martenls/bachelor-thesis-code/sninterface-twitter
```

## Run

### Environment Variables

The app depends on multiple environment variables to work properly.
These can be set manually or provided with a `.env` file.

#### OAuth:

Set access tokens from the twitter account that should be subscribed to.

Get Tokens: 
- Generate keys with a twitter developer account under https://developer.twitter.com/en/apps/ -> Keys and Tokens
- allow access to direct messages under -> Permissions
```
TWITTER_CONSUMER_KEY=***********
TWITTER_CONSUMER_SECRET=***********
TWITTER_ACCESS_TOKEN=***********
TWITTER_ACCESS_TOKEN_SECRET=***********
```

#### Webhook Config

For local deployment set only: (local port is opened automatically for twitter webhook throug ngrok: https://ngrok.com/)
```
TWITTER_WEBHOOK_ENV={{Name of the environment defined under https://developer.twitter.com/en/account/environments}}
TWITTER_USER_ID={{ID of the twitter account (For example from http://gettwitterid.com)}}
```

For deployment on openbot vm set additional variables:
```
WEBHOOKURL="https://openbot.cs.upb.de/sninterface/twitter/webhook/"
PORT=3000
```

#### General Config

```
BOT_LANG={{"de" or "en"}}
QA_URL=http://localhost:8080/qa
```
`BOT_LANG` defines in which language the bot responds but not the language in which questions can be asked.
`QA_URL` should point to the location of the qa-system.

### Locally

Prerequisites:
  - node
  - npm

Run in `twitter-bot` directory:


install dependencies

    npm install

run app

    node bot.js

### Docker

pass env variables via file:

```
docker run --name sninterface-twitter -p '3000:3000' --env_file .env sninterface-twitter
```

or use provided `docker-compose.yml`


# sninterface-botkit: web bot

A nodejs app that:
 - provides a web chat interface
 - passes questions asked to the qa-system
 - replies with answers fetched from qa-system
 - implements introductory dialogs


## Build Docker

Build yourself with (run in `web-bot` folder)
```
docker build -t sninterface-web .
```
 or pull from gitlab container registry with 
```
docker login hub.cs.upb.de
docker pull hub.cs.upb.de/martenls/bachelor-thesis-code/sninterface-web
```

## Run

### Environment Variables

```
PORT=3030

BOT_LANG={{"de" or "en"}}
QA_URL=http://localhost:8080/qa
```
`PORT` defines the port where the web interface should be delivered. `BOT_LANG` defines in which language the bot responds but not the language in which questions can be asked.
`QA_URL` should point to the location of the qa-system.

### Locally

Prerequisites:
  - node
  - npm

Run in `web-bot` directory:

install dependencies

    npm install

run app

    node bot.js

### Docker

pass env variables via file:

```
docker run --name sninterface-web -p '3030:3030' --env_file .env sninterface-web
```

or use provided `docker-compose.yml`