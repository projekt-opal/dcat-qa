//  __   __  ___        ___
// |__) /  \  |  |__/ |  |  
// |__) \__/  |  |  \ |  |  

// This is the main file for the mybot web bot.

// Import Botkit's core features
const { Botkit } = require('botkit');

const path = require('path');

// Import a platform-specific adapter for web.
const { WebAdapter } = require('botbuilder-adapter-web');


// Load process.env values from .env file
require('dotenv').config();


const adapter = new WebAdapter();


const controller = new Botkit({
    webhook_uri: '/api/messages',
    adapter: adapter,

    // storage
});


// Once the bot has booted up its internal services, you can use them to do stuff.
controller.ready(() => {
    // load traditional developer-created local custom feature modules
    controller.loadModules(path.join(__dirname,'common_features_de'));

    // make public/index.html available as localhost/index.html
    // by making the /public folder a static/public asset
    controller.publicFolder('/', path.join(__dirname, 'public'));

    console.log('Chat with me: http://localhost:' + (process.env.WEB_PORT || 3000));

});





