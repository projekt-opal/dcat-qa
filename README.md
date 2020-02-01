# bachelor-thesis-code

Social Bot for Open Metadata.

# nginx-config

Holds config files for nginx reverse proxy on the openbot vm.

## Build

Build yourself with 
```
docker build  -t nginx-openbot
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

# sninteface-config

Nodejs app that:
- subscribes to Twitters account-activity api
- passes all questions answered in tweets or direct-messages to the qa-system
- replies to tweets and dms with answers fetched from the qa-system

## Build Docker

Build yourself with 
```
docker build  -t sninterface-twitter
```
 or pull from gitlab container registry with 
```
docker login hub.cs.upb.de
docker pull hub.cs.upb.de/martenls/bachelor-thesis-code/sninterface-twitter
```

## Run

### Environment Variables

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

### Docker

pass env variables via file:

```
docker run --name sninterface-twitter -p '3000:3000' --env_file .env sninterface-twitter
```

or use provided `docker-compose.yml` on openbot-vm.
