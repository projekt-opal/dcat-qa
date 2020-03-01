/**
 * @module botbuilder-adapter-twitter
 */
/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

import { Botkit, BotWorker } from 'botkit';
import { TwitterAPI } from './twitter_api';

/**
 * This is a specialized version of [Botkit's core BotWorker class](core.md#BotWorker) that includes additional methods for interacting with Twitter.
 * It includes all functionality from the base class, as well as the extension methods below.
 *
 * When using the TwitterAdapter with Botkit, all `bot` objects passed to handler functions will include these extensions.
 */
export class TwitterBotWorker extends BotWorker {
    /**
     * A copy of the TwitterAPI client giving access to `let res = await bot.api.get(path);`
     */
    public api: TwitterAPI;

    /**
     * Reserved for use internally by Botkit's `controller.spawn()`, this class is used to create a BotWorker instance that can send messages, replies, and make other API calls.
     *
     * 
     * @param botkit The Botkit controller object responsible for spawning this bot worker.
     * @param config A DialogContext object.
     */
    public constructor(botkit: Botkit, config: any) {
        super(botkit, config);
    }

    
}
