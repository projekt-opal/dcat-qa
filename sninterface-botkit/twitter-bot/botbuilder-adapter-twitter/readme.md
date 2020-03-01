# botbuilder-adapter-twitter
Connect [Botkit](https://www.npmjs.com/package/botkit) or [BotBuilder](https://www.npmjs.com/package/botbuilder) to Twitter.

This package contains an adapter that communicates directly with the Twitter API,
and translates messages to and from a standard format used by your bot. This package can be used alongside your favorite bot development framework to build bots that work with Twitter direct messages or tweets.

## Install Package

Add this package to your project using npm:

```bash
npm install --save botbuilder-adapter-twitter
```

Import the adapter class into your code:

```javascript
const { TwitterAdapter } = require('botbuilder-adapter-twitter');
```

## Get Started

If you are starting a brand new project, [follow these instructions to create a customized application template.](https://botkit.ai/getstarted.html)

## Use TwitterAdapter in your App

TwitterAdapter provides a translation layer for Botkit and BotBuilder so that bot developers can connect to Twitter and have access to Twitters's API.

### Botkit Basics

When used in concert with Botkit, developers need only pass the configured adapter to the Botkit constructor, as seen below. Botkit will automatically create and configure the webhook endpoints and other options necessary for communicating with Twitter.

Developers can then bind to Botkit's event emitting system using `controller.on` and `controller.hears` to filter and handle incoming events from the messaging platform. [Learn more about Botkit's core feature &rarr;](../docs/index.md).


```javascript
const adapter = new TwitterAdapter({
    oauth: {
        consumer_key: process.env.TWITTER_CONSUMER_KEY,
        consumer_secret: process.env.TWITTER_CONSUMER_SECRET,
        token: process.env.TWITTER_TOKEN,
        token_secret: process.env.TWITTER_TOKEN_SECRET
    },
    webhook_env: process.env.TWITTER_WEBHOOK_ENV,
    webhook_url: 'https://20656324.ngrok.io'
});

const controller = new Botkit({
    webhook_uri: '/api/twitter/messages',
    adapter,
    // ...other options
});
// direct message
controller.on('message', async(bot, message) => {
    await bot.reply(message, 'I heard a message!');
});
// tweet
controller.on('tweet', async(bot, message) => {
    await bot.reply(message, 'I heard a message!');
});
```

### BotBuilder Basics

Alternately, developers may choose to use `TwitterAdapter` with BotBuilder. With BotBuilder, the adapter is used more directly with a webserver, and all incoming events are handled as [Activities](https://docs.microsoft.com/en-us/javascript/api/botframework-schema/activity?view=botbuilder-ts-latest).

```javascript
const { TwitterAdapter, TwitterWebhookHelper, TwitterAPI } = require('botbuilder-adapter-twitter');
const restify = require('restify');

const adapter = new TwitterAdapter({
    oauth: {
        consumer_key: process.env.TWITTER_CONSUMER_KEY,
        consumer_secret: process.env.TWITTER_CONSUMER_SECRET,
        token: process.env.TWITTER_TOKEN,
        token_secret: process.env.TWITTER_TOKEN_SECRET
    },
    webhook_env: process.env.TWITTER_WEBHOOK_ENV,
    webhook_url: 'https://fbc97e1e.ngrok.io'
});

const server = restify.createServer();
server.use(restify.plugins.bodyParser());
server.use(restify.plugins.queryParser());

const webhook_uri = '/webhook';

const api = new TwitterAPI(adapter.options.oauth)
const webhookHelper = new TwitterWebhookHelper(api, adapter.options.webhook_env)

server.get(webhook_uri, (req, res) => {
    const crc = webhookHelper.validateWebhook(req.query['crc_token'], adapter.options.oauth)
    res.writeHead(200, {'content-type': 'application/json'});
    res.end(JSON.stringify(crc));
});


server.post(webhook_uri, (req, res) => {
    adapter.processActivity(req, res, async (context) => {
        await context.sendActivity('I heard a message');
    });
});


(async init => {
    server.listen(process.env.port || process.env.PORT || 3000, () => {
        console.log(`\n${ server.name } listening to ${ server.url }`);
    });

    await webhookHelper.removeWebhooks();
    await webhookHelper.setWebhook(url.resolve(adapter.options.webhook_url, webhook_uri));
    await webhookHelper.subscribe();
})();
```

## Class Reference



## Calling Twitter APIs

This package also includes a minimal Twitter API client for developers who want to use one of the many available API endpoints.

In Botkit handlers, the `bot` worker object passed into all handlers will contain a `bot.api` field that contains the client, preconfigured and ready to use.

To use with a BotBuilder application, the adapter provides the [getAPI() method]().

```javascript
controller.on('message', async(bot, message) {

    // call the Twitter API to get the bot's account settings
    const res = await bot.api.get('/account/settings.json');
    await bot.reply(message, { text: res.body });

});
```

## Botkit Extensions

In Botkit handlers, the `bot` worker for Twitter contains [all of the base methods](../docs/reference/core.md#BotWorker) as well as the following platform-specific extensions:

### Use call to actions and quick replies in direct message conversations

Botkit will automatically construct your outgoing messages according to Twitter's specifications. To use attachments, quick replies or other features, add them to the message object used to create the reply:

```javascript
controller.hears('quick replies', 'message', async (bot, message) => {
    await bot.reply(message, {
        text: 'Here are some quick reply options',
        type: 'message', 
        quick_replies: [
            {
                label: 'Foo',
                description: 'foo',
            },
            {
                label: 'Bar',
                description: 'bar',
            }
        ]}
    );
});

controller.hears('ctas', 'message', async (bot, message) => {
    await bot.reply(message, {
        text: 'Here are your call to actions',
        type: 'message',
        ctas: [
            {
                type: 'web_url',
                label: 'The OPAL Website',
                url: 'http://projekt-opal.de/'
            },
            {
                type: 'web_url',
                label: 'Fuseki Endpoint',
                url: 'https://openbot.cs.upb.de/fuseki/'
            }
        ]
    })
});
```


## Community & Support

Join our thriving community of Botkit developers and bot enthusiasts at large.
Over 10,000 members strong, [our open Slack group](https://community.botkit.ai) is
_the place_ for people interested in the art and science of making bots.
Come to ask questions, share your progress, and commune with your peers!

You can also find help from members of the Botkit team [in our dedicated Cisco Spark room](https://eurl.io/#SyNZuomKx)!

## About Botkit

Botkit is a part of the [Microsoft Bot Framework](https://dev.botframework.com).

Want to contribute? [Read the contributor guide](https://github.com/howdyai/botkit/blob/master/CONTRIBUTING.md)

Botkit is released under the [MIT Open Source license](https://github.com/howdyai/botkit/blob/master/LICENSE.md)
