//  __   __  ___        ___
// |__) /  \  |  |__/ |  |  
// |__) \__/  |  |  \ |  |  

// This is the main file for the mybot bot.

// Import Botkit's core features
const { Botkit } = require('botkit');
const { BotkitCMSHelper } = require('botkit-plugin-cms');

// Import a platform-specific adapter for facebook.

const { TwitterAdapter } = require('botbuilder-adapter-twitter');

const path = require('path');
// Load process.env values from .env file
require('dotenv').config();

// let storage = null;
// if (process.env.MONGO_URI) {
//     storage = mongoStorage = new MongoDbStorage({
//         url : process.env.MONGO_URI,
//     });
// }


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
    webhook_uri: '/api/twitter/messages',
    adapter: adapter,
    debug: true

    // storage
});


// Once the bot has booted up its internal services, you can use them to do stuff.
controller.ready(() => {

    // load traditional developer-created local custom feature modules
    controller.loadModules(path.join(__dirname, '..', 'common_features_en'));


});


controller.webserver.get('/', (req, res) => {

    res.send(`This app is running Botkit ${ controller.version }.`);

});





