const { Autohook } = require('twitter-autohook');
const url = require('url');
const http = require('http');
const api = require('./twitterApi');
require('dotenv').config();



const PORT = process.env.PORT;
const USERID = process.env.TWITTER_USER_ID;
const WEBHOOKURL = process.env.WEBHOOKURL;


const oAuthConfig = {
  token: process.env.TWITTER_ACCESS_TOKEN,
  token_secret: process.env.TWITTER_ACCESS_TOKEN_SECRET,
  consumer_key: process.env.TWITTER_CONSUMER_KEY,
  consumer_secret: process.env.TWITTER_CONSUMER_SECRET,
};




async function sayHi(event) {
  if (!event.direct_message_events) {
    return;
  }

  const message = event.direct_message_events.shift();

  if (typeof message === 'undefined' || typeof message.message_create === 'undefined') {
    return;
  }
 
  if (message.message_create.sender_id === message.message_create.target.recipient_id) {
    return;
  }

  await api.markAsRead(message.message_create.id, message.message_create.sender_id, oAuthConfig);
  await api.indicateTyping(message.message_create.sender_id, oAuthConfig);

  const senderScreenName = event.users[message.message_create.sender_id].screen_name;
  console.log(`${senderScreenName} says ${message.message_create.message_data.text}`);

  await api.sendDM(message.message_create.sender_id, `Hi @${senderScreenName}! 👋👋👋`)
 
}




async function tweetHi(event){
  if (!event.tweet_create_events) {
    return;
  }
  const tweet = event.tweet_create_events.shift();

  if (tweet.in_reply_to_user_id !== USERID) {
    return;
  }

  await api.tweetReply(tweet.id_str, `Hi ${tweet.user.screen_name}! 👋👋👋`);

}




function sleep(ms){
    return new Promise(resolve=>{
        setTimeout(resolve,ms)
    })
}

(async start => {
  try {

    const webhook = new Autohook({ port: PORT });
    await webhook.removeWebhooks();
    if (WEBHOOKURL) {
      webhook.startServer();
    }
    console.log('Listening on port: ' + PORT);  
    await webhook.start(WEBHOOKURL);

    webhook.on('event', async event => {
      console.log(JSON.stringify(event, null, 2))
      if (event.direct_message_events) {
        await sayHi(event);
      } else if (event.tweet_create_events) {
        await tweetHi(event);
      }
    });

    await webhook.subscribe({oauth_token: oAuthConfig.token, oauth_token_secret: oAuthConfig.token_secret});  
  } catch (e) {
    console.error(e);
    if (e.name === 'RateLimitError') {
      await sleep(e.resetAt - new Date().getTime());
      process.exit(1);
    }
  }
})();
