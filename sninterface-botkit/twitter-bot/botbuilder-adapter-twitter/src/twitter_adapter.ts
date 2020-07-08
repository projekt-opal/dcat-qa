/**
 * @module botbuilder-adapter-twitter
 */
/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */
import * as url from 'url';
import * as Debug from 'debug';
import * as twitter_text from 'twitter-text';

import { TwitterAPI, TwitterOAuth, AuthType, PayloadType } from './twitter_api';
import { TwitterWebhookHelper } from './twitter_webhook_helper';
import { TwitterBotWorker } from './twitter_botworker';

import { Activity, ActivityTypes, BotAdapter, TurnContext, ConversationReference, ResourceResponse } from 'botbuilder';



const debug = Debug('botkit:Twitter');



/**
 * Connect [Botkit](https://www.npmjs.com/package/botkit) or [BotBuilder](https://www.npmjs.com/package/botbuilder) to Twitter.
 */
export class TwitterAdapter extends BotAdapter {
    /**
     * Name used by Botkit plugin loader
     * @ignore
     */
    public name = 'Twitter Adapter';

    /**
     * Object containing one or more Botkit middlewares to bind automatically.
     * @ignore
     */
    public middlewares;

    /**
     * A customized BotWorker object that exposes additional utility methods.
     * @ignore
     */
    public botkit_worker = TwitterBotWorker;


    public options: TwitterAdapterOptions;

    /**
     * Instance of the Twitter webhook-helper class.
     */
    private webhookHelper: TwitterWebhookHelper;

    /**
     * Instance of the Twitter API client.
     */
    private api: TwitterAPI;

    /**
     * User object of the bots twitter account to identify messages and tweet sent by himself. Set on initilization.
     */
    private user: any;

    /**
     * Create an adapter to handle incoming messages from Twitter and translate them into a standard format for processing by your bot.
     *
     * The Twitter Adapter can only be bound to a single Twitter page.
     *
     * To create an app bound to a single Twitter page, include that page's `access_token` in the options.
     *     *
     * To use with Botkit:
     * ```javascript
     * const adapter = new TwitterAdapter({
     *     oauth: {
     *         consumer_key: process.env.TWITTER_CONSUMER_KEY,
     *         consumer_secret: process.env.TWITTER_CONSUMER_SECRET,
     *         token: process.env.TWITTER_TOKEN,
     *         token_secret: process.env.TWITTER_TOKEN_SECRET
     *     },
     *     webhook_env: process.env.TWITTER_WEBHOOK_ENV,
     *     webhook_url: 'https://20656324.ngrok.io'
     * });
     * const controller = new Botkit({
     * webhook_uri: '/api/twitter/messages',
     * adapter: adapter,
     * });
     * ```
     *
     * To use with BotBuilder:
     * ```javascript
     * const adapter = new TwitterAdapter({
     *      verify_token: process.env.Twitter_VERIFY_TOKEN,
     *      app_secret: process.env.Twitter_APP_SECRET,
     *      access_token: process.env.Twitter_ACCESS_TOKEN
     * });
     * const server = restify.createServer();
     * server.use(restify.plugins.bodyParser());
     * server.post('/api/messages', (req, res) => {
     *      adapter.processActivity(req, res, async(context) => {
     *          // do your bot logic here!
     *      });
     * });
     * ```
     *
     *```
     *
     * @param options Configuration options
     */
    public constructor(options: TwitterAdapterOptions) {
        super();

        if (!options.oauth) {
            throw new Error('Adapter must receive full oauth credentials for the bot account(access_token, access_token_secret, consumer_key, consumer_secret')
        }
        if (!options.webhook_env) {
            throw new Error('The label of the developement enviroment was not provided')
        }
        if (!options.webhook_url) {
            throw new Error('The URL where the webhook should be registered was not provided')
        }

        this.options = {
            api_host: 'api.twitter.com',
            api_version: '1.1',
            ...options
        };
        // get api instance
        this.api = new TwitterAPI(this.options.oauth, this.options.api_host, this.options.api_version);
        // get webhook helper instance
        this.webhookHelper = new TwitterWebhookHelper(this.api, this.options.webhook_env);
        
        this.middlewares = {
            spawn: [
                async (bot, next) => {
                    bot.api = this.api;
                    next();
                }
            ]
        };
    }

    /**
     * Botkit-only: Initialization function called automatically when used with Botkit.
     * Adds listener on webserver to answer Twitter webhook verification challenge.
     * Subscribes to accounts activity when the webhook registration was successfull.
     * @param botkit
     */
    public async init(botkit): Promise<any> {
        debug('Verify credentials.')
        // verify credentials and get user id
        this.user = await this.api.verifyCredentials(this.options.oauth);
        debug('Add GET webhook endpoint for verification at: ', botkit.getConfig('webhook_uri'));
        // listen for crc challegen on webhook
        botkit.webserver.get(botkit.getConfig('webhook_uri'), (req, res) => {
            const crc = this.webhookHelper.validateWebhook(req.query['crc_token'], this.options.oauth)
            res.writeHead(200, {'content-type': 'application/json'});
            res.end(JSON.stringify(crc));
        });
        await this.webhookHelper.removeWebhooks();
        await this.webhookHelper.setWebhook(url.resolve(this.options.webhook_url, botkit.getConfig('webhook_uri')));
        await this.webhookHelper.subscribe();
    }

    /**
     * Converts an Activity object to a Twitter messenger outbound message ready for the API.
     * @param activity
     */
    private activityToTwitterDM(activity: any): any {
        const message = {
            event: {
                type: 'message_create',
                message_create: {
                  target: {
                    recipient_id: activity.recipient.id,
                  },
                  message_data: {
                    text: activity.text,
                    quick_reply: null,
                    ctas: null
                  },
                }
            }
        };
        // map these fields to their appropriate place
        if (activity.channelData) {
            if (activity.channelData.quick_replies) {
                message.event.message_create.message_data.quick_reply = {
                    type: 'options',
                    options: activity.channelData.quick_replies
                }
            }
            if (activity.channelData.ctas) {
                message.event.message_create.message_data.ctas = activity.channelData.ctas;
            }
        }
        debug('OUT TO Twitter > ', message);
        return message;
    }

    /**
     * Build tweet objects from an activity.
     * If the text is longer than the maximum of 280 chars that are allowed in a tweet it will be split into multiple objects.
     * These can then be posted as a reply thread.
     * @param activity The activity to be converted to tweet objects.
     */
    private activityToTweets(activity: any): any {
        let text = activity.text;
        let texts = [];

        let startIndex = 0;
        let endIndex = 0;
        let validEndIndex = 0;

        while (startIndex < activity.text.length - 1) {
            validEndIndex = twitter_text.parseTweet(text.substring(startIndex)).validRangeEnd + startIndex
            endIndex = text.substring(startIndex, validEndIndex).lastIndexOf('\n') + startIndex;
            if (endIndex === -1 || endIndex === startIndex) {
                endIndex = validEndIndex;
            }
            texts.push(text.substring(startIndex, endIndex + 1));
            startIndex = endIndex;
            
        }
       
        return texts.map((text) => {return {
            status: text,
            auto_populate_reply_metadata: true,
        }});
    }

    /**
     * Standard BotBuilder adapter method to send a message from the bot to the messaging API.
     * [BotBuilder reference docs](https://docs.microsoft.com/en-us/javascript/api/botbuilder-core/botadapter?view=botbuilder-ts-latest#sendactivities).
     * @param context A TurnContext representing the current incoming message and environment.
     * @param activities An array of outgoing activities to be sent back to the messaging API.
     */
    public async sendActivities(context: TurnContext, activities: Partial<Activity>[]): Promise<ResourceResponse[]> {
        const responses = [];
        for (let a = 0; a < activities.length; a++) {
            const activity = activities[a];
            if (activity.type === 'tweet') {
                const messages = this.activityToTweets(activity);
                try {
                    await this.api.postThreadReply(messages, activity.replyToId);
                } catch (err) {
                    console.error('Error sending activity to Twitter:', err);
                }
            } else if (activity.type == ActivityTypes.Message) {     
                const message = this.activityToTwitterDM(activity);
                try {
                    const res = await this.api.post('/direct_messages/events/new.json', AuthType.USER_CONTEXT, message);
                    if (res) {
                        responses.push({ id: res.message_id });
                    }
                    debug('RESPONSE FROM Twitter > ', res);
                } catch (err) {
                    console.error('Error sending activity to Twitter:', err);
                }
            } else if (activity.type === ActivityTypes.Typing) {
                const message = { recipient_id: activity.recipient.id }
                try {
                    const res = await this.api.post('/direct_messages/indicate_typing.json', AuthType.USER_CONTEXT, message, PayloadType.FORM);
                    if (res) {
                        responses.push({ id: res.message_id });
                    }
                    debug('RESPONSE FROM Twitter > ', res);
                } catch (err) {
                    console.error('Error sending activity to Twitter:', err);
                }
            } else {
                // If there are ever any non-message type events that need to be sent, do it here.
                debug('Unknown message type encountered in sendActivities: ', activity.type);
            }
        }

        return responses;
    }

    /**
     * Twitter adapter does not support updateActivity.
     * @ignore
     */
    // eslint-disable-next-line
    public async updateActivity(context: TurnContext, activity: Partial<Activity>): Promise<void> {
        debug('Twitter adapter does not support updateActivity.');
    }

    /**
     * Twitter adapter does not support updateActivity.
     * @ignore
     */
    // eslint-disable-next-line
     public async deleteActivity(context: TurnContext, reference: Partial<ConversationReference>): Promise<void> {
        debug('Twitter adapter does not support deleteActivity.');
    }

    /**
     * Standard BotBuilder adapter method for continuing an existing conversation based on a conversation reference.
     * [BotBuilder reference docs](https://docs.microsoft.com/en-us/javascript/api/botbuilder-core/botadapter?view=botbuilder-ts-latest#continueconversation)
     * @param reference A conversation reference to be applied to future messages.
     * @param logic A bot logic function that will perform continuing action in the form `async(context) => { ... }`
     */
    public async continueConversation(reference: Partial<ConversationReference>, logic: (context: TurnContext) => Promise<void>): Promise<void> {
        const request = TurnContext.applyConversationReference(
            { type: 'event', name: 'continueConversation' },
            reference,
            true
        );
        const context = new TurnContext(this, request);

        return this.runMiddleware(context, logic);
    }

    /**
     * Accept an incoming webhook request and convert it into a TurnContext which can be processed by the bot's logic.
     * @param req A request object from Restify or Express
     * @param res A response object from Restify or Express
     * @param logic A bot logic function in the form `async(context) => { ... }`
     */
    public async processActivity(req, res, logic: (context: TurnContext) => Promise<void>): Promise<void> {
        debug('IN FROM Twitter >', req.body);
        const event = req.body;
        if (event.tweet_create_events) {
            for (let i = 0; i < event.tweet_create_events.length; i++) {
                await this.processSingleMentionTweet(event.tweet_create_events[i], logic);
            }
        }
        if (event.direct_message_events) {
            for (let i = 0; i < event.direct_message_events.length; i++) {
                await this.processSingleDM(event.direct_message_events[i], logic);
            }
        }
        // if (event.direct_message_indicate_typing_events) {
        //     for (let i = 0; i < event.direct_message_indicate_typing_events.length; i++) {
        //         await this.processSingleDMTypingEvent(event.direct_message_indicate_typing_events[i], logic);
        //     }
        // }
        // if (event.direct_message_mark_read_events) {
        //     for (let i = 0; i < event.direct_message_mark_read_events.length; i++) {
        //         await this.processSingleMarkReadEvent(event.direct_message_mark_read_events[i], logic);
        //     }
        // }
        res.status(200);
        res.end();
    }

    /**
     * Handles each individual direct message inside a webhook payload (webhook may deliver more than one message at a time)
     * @param message A direct message object from the Twitter Api.
     * @param logic A bot logic function in the form `async(context) => { ... }`
     */
    private async processSingleDM(message: any, logic: any): Promise<void> {
        // filter out messages sent by the bot
        if (message.message_create.sender_id != this.user.id_str) {
            const activity: Activity = {
                channelId: 'twitter',
                timestamp: new Date(),
                // @ts-ignore ignore missing optional fields
                conversation: {
                    id: message.message_create.sender_id
                },
                from: {
                    id: message.message_create.sender_id,
                    name: message.message_create.sender_id
                },
                recipient: {
                    id: message.message_create.target.recipient_id,
                    name: message.message_create.target.recipient_id
                },
                channelData: message,
                type: ActivityTypes.Message,
                text: message.message_create.message_data.text
            };
            for (const key in message.message_create.message_data.entities) {
                activity.channelData[key] = message.message_create.message_data.entities[key];
            }

            const context = new TurnContext(this, activity as Activity);
            await this.runMiddleware(context, logic);
        }        
    }

    /**
     * Handles each individual direct message typing event inside a webhook payload (webhook may deliver more than one event at a time)
     * @param message A direct message object from the Twitter Api.
     * @param logic A bot logic function in the form `async(context) => { ... }`
     */
    private async processSingleDMTypingEvent(message: any, logic: any) {
        const activity: Activity = {
            channelId: 'twitter',
            timestamp: new Date(),
            // @ts-ignore ignore missing optional fields
            conversation: {
                id: message.sender_id
            },
            from: {
                id: message.sender_id,
                name: message.sender_id
            },
            recipient: {
                id: message.target.recipient_id,
                name: message.target.recipient_id
            },
            channelData: message,
            type: ActivityTypes.Typing
        };
        const context = new TurnContext(this, activity as Activity);
        await this.runMiddleware(context, logic);
    }

    /**
     * Handles each individual direct message mark-read event inside a webhook payload (webhook may deliver more than one event at a time)
     * @param message A direct message object from the Twitter Api.
     * @param logic A bot logic function in the form `async(context) => { ... }`
     */
    private async processSingleMarkReadEvent(message: any, logic: any) {
        const activity: Activity = {
            channelId: 'twitter',
            timestamp: new Date(),
            // @ts-ignore ignore missing optional fields
            conversation: {
                id: message.sender_id
            },
            from: {
                id: message.sender_id,
                name: message.sender_id
            },
            recipient: {
                id: message.target.recipient_id,
                name: message.target.recipient_id
            },
            channelData: message,
            type: ActivityTypes.MessageReaction
        };
        const context = new TurnContext(this, activity as Activity);
        await this.runMiddleware(context, logic);
    }

    /**
     * Handles each individual tweet inside a webhook payload (webhook may deliver more than one tweet at a time)
     * @param tweet A tweet object from the Twitter Api.
     * @param logic A bot logic function in the form `async(context) => { ... }`
     */
    private async processSingleMentionTweet(tweet: any, logic: any) {
        // filter out messages sent by the bot
        if (tweet.user.id_str != this.user.id_str && (tweet.in_reply_to_user_id_str == this.user.id_str || this.doesTweetMentionUser(tweet, this.user.id_str)) ) {
            const activity: Activity = {
                channelId: 'twitter',
                timestamp: new Date(),
                id: tweet.id_str,
                // @ts-ignore ignore missing optional fields
                conversation: {
                    id: tweet.user.id_str
                },
                from: {
                    id: tweet.user.id_str,
                    name: tweet.user.screen_name
                },
                recipient: {
                    id: this.user.id_str,
                    name: this.user.screen_name
                },
                channelData: tweet,
                type: 'tweet',
                text: tweet.text.replace(`@${this.user.screen_name} `, '')
            };
            for (const key in tweet.entities) {
                activity.channelData[key] = tweet.entities[key];
            }

            const context = new TurnContext(this, activity as Activity);
            await this.runMiddleware(context, logic);
        }     
    }

    /**
     * Checks wether the given tweet mentions the user with the given id.
     * @param tweet to check
     * @param userid of user
     */
    private doesTweetMentionUser(tweet: any, userid: string) {
        if (tweet.entities && tweet.entities.user_mentions && tweet.entities.user_mentions.length > 0) {
            for (const mention of tweet.entities.user_mentions) {
                if (mention.id_str === userid) {
                    return true;
                }
            }
        }
        return false;
    }
}

/**
 * This interface defines the options that can be passed into the TwitterAdapter constructor function.
 */
export interface TwitterAdapterOptions {
    /**
     * Alternate root url used to contruct calls to Twitter's API.  Defaults to 'api.twitter.com' but can be changed (for mocking, proxy, etc).
     */
    api_host?: string;
    /**
     * Alternate API version used to construct calls to Twitter's API. Defaults to v1.1
     */
    api_version?: string;
    /**
     * Full oauth credentials of the bots twitter account. Mandatory.
     */
    oauth: TwitterOAuth;
    /**
     * The label of the dev environment defined in the twitter dev dashboard. Mandatory.
     */
    webhook_env: string;
    /**
     * URL where the webserver of the bot is reachable. Mandatory.
     */
    webhook_url: string;
}
