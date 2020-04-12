//  __   __  ___        ___
// |__) /  \  |  |__/ |  |  
// |__) \__/  |  |  \ |  |  

// This is the main file for the mybot bot.

// Import Botkit's core features
const { Botkit } = require('botkit');

// Import a platform-specific adapter for facebook.

const { TwitterAdapter } = require('botbuilder-adapter-twitter');

const path = require('path');
// Load process.env values from .env file
require('dotenv').config();


const adapter = new TwitterAdapter({
    oauth: {
        consumer_key: process.env.TWITTER_CONSUMER_KEY,
        consumer_secret: process.env.TWITTER_CONSUMER_SECRET,
        token: process.env.TWITTER_TOKEN,
        token_secret: process.env.TWITTER_TOKEN_SECRET
    },
    webhook_env: process.env.TWITTER_WEBHOOK_ENV,
    webhook_url: process.env.WEBHOOK_URL
});



const controller = new Botkit({
    webhook_uri: process.env.WEBHOOK_URI || '/',
    adapter: adapter,
    debug: true

    // storage
});


// Once the bot has booted up its internal services, you can use them to do stuff.
controller.ready(() => {
    if (process.env.BOT_LANG && process.env.BOT_LANG === 'en') {
        controller.loadModules(path.join(__dirname, 'features', 'dialogs_en'))
    } else {
        controller.loadModules(path.join(__dirname, 'features', 'dialogs_de'))
    }


});


controller.webserver.get('/', (req, res) => {

    res.send(`This app is running Botkit ${ controller.version }.`);

});





