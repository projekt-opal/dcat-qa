import * as crypto from 'crypto';
import * as url from 'url';

import { TwitterError, TooManySubscriptionsError, UserSubscriptionError, WebhookURIError, RateLimitError} from './errors'
import { TwitterAPI, TwitterOAuth, AuthType, PayloadType } from './twitter_api';

/**
 * Provides methods to help setup a twitter webhook and account activity subscription.
 */
export class TwitterWebhookHelper {
    private _getSubscriptionsCount: any = null;
    
    private env;
    private api: TwitterAPI;
    
    constructor(api: TwitterAPI, env: string) {
        this.api = api;
        this.env = env;
    }
    
    /**
     * Get number of current active subscriptions to the account activity.
     */
    private async getSubscriptionsCount() {
        if (this._getSubscriptionsCount) {
            return this._getSubscriptionsCount;
        }
        
        const response = await this.api.get('/account_activity/all/subscriptions/count.json', AuthType.BEARER);
        
        switch (response.statusCode) {
            case 200:
                break;
            case 429:
                throw new RateLimitError(response);
                break;
            default:
                throw new TwitterError(response);
        }
        
        this._getSubscriptionsCount = JSON.parse(response.body);
        return this._getSubscriptionsCount;
    }
    
    private updateSubscriptionCount(increment) {
        if (!this._getSubscriptionsCount) {
            return;
        }
        
        this._getSubscriptionsCount.subscriptions_count += increment;
    }
    
    /**
     * Fetch all registered webhook endpoints.
     */
    private async getWebhooks() {
        console.log('Getting webhooks…');
        const response = await this.api.get(`/account_activity/all/${this.env}/webhooks.json`);
        switch (response.statusCode) {
            case 200:
                break;
            case 429:
                throw new RateLimitError(response);
                return [];
            default:
                throw new URIError([
                    `Cannot get webhooks. Please check that '${this.env}' is a valid environment defined in your`,
                    `Developer dashboard at https://developer.twitter.com/en/account/environments, and that`,
                    `your OAuth credentials are valid and can access '${this.env}'. (HTTP status: ${response.statusCode})`].join(' '));
                return [];
                }
                
                try {
                    return JSON.parse(response.body);
                } catch (e) {
                    throw TypeError('Error while parsing the response from the Twitter API:' + e.message);
                    return [];
                }
    }
    
    /**
     * Register specified url as new webhook.
     * @param webhookUrl The url that should be registered.
     */
    public async setWebhook(webhookUrl) { 
        const parsedUrl = url.parse(webhookUrl);
        if (parsedUrl.protocol === null || parsedUrl.host === 'null') {
            throw new TypeError(`${webhookUrl} is not a valid URL. Please provide a valid URL and try again.`);
            return;
        } else if (parsedUrl.protocol !== 'https:') {
            throw new TypeError(`${webhookUrl} is not a valid URL. Your webhook must be HTTPS.`);
            return;
        }
        
        console.log(`Registering ${webhookUrl} as a new webhook…`);        
        const response = await this.api.post(`/account_activity/all/${this.env}/webhooks.json`, AuthType.USER_CONTEXT, { url : webhookUrl}, PayloadType.PATH_PARAMS);
        
        switch (response.statusCode) {
            case 200:
            case 204:
                break;
            case 400:
            case 403:
                throw new WebhookURIError(response);
                return;
            case 429:
                console.log(response.headers);
                throw new RateLimitError(response);
                return;
            default:
                throw new URIError([
                    `Cannot get webhooks. Please check that '${this.env}' is a valid environment defined in your`,
                    `Developer dashboard at https://developer.twitter.com/en/account/environments, and that`,
                    `your OAuth credentials are valid and can access '${this.env}'. (HTTP status: ${response.statusCode})`].join(' '));
                    return;
                }
                
                const body = JSON.parse(response.body);
                return body;
    }
        
    /**
     * Solve crc challenge to verify the webhook endpoint is controlled by the owner of the account.
     * @param token The token that Twitter sends to the endpoint as part of the challenge request.
     * @param auth The full auth credentials of the account that registers the webhook.
     */
    public validateWebhook(token, auth: TwitterOAuth) {
        const responseToken = crypto.createHmac('sha256', auth.consumer_secret).update(token).digest('base64');
        return { response_token: `sha256=${responseToken}` };
    }
    
    
    /**
     * Delete/Deregister specified webhooks.
     * @param webhooks The endpoints that should be derigstered.
     */
    private async deleteWebhooks(webhooks) {
        console.log('Removing webhooks…');
        for (const {id, url} of webhooks) {
            console.log(`Removing ${url}…`);
            const response = await this.api.del(`/account_activity/all/${this.env}/webhooks/${id}.json`);
            
            switch (response.statusCode) {
                case 200:
                case 204:
                    return true;
                case 429:
                    throw new RateLimitError(response);
                    return false;
                default:
                    throw new URIError([
                        `Cannot remove ${url}. Please make sure it belongs to '${this.env}', and that '${this.env}' is a`,
                        `valid environment defined in your Developer dashboard at`,
                        `https://developer.twitter.com/en/account/environments. Also check that your OAuth`,
                        `credentials are valid and can access '${this.env}'. (HTTP status: ${response.statusCode})`,
                    ].join(' '));
                    return false;
            }
        }
    }
    /**
     * Fetch and remove webhooks.
     */
    public async removeWebhooks() {
        const webhooks = await this.getWebhooks();
        await this.deleteWebhooks(webhooks);
    }
    
    /**
     * Subscribe to the account activity of the authenticated user.
     */
    public async subscribe() {       
        const {subscriptions_count, provisioned_count} = await this.getSubscriptionsCount();
        
        if (subscriptions_count === provisioned_count) {
            throw new TooManySubscriptionsError([`Cannot subscribe to activities:`,
            'you exceeded the number of subscriptions available to you.',
            'Please remove a subscription or upgrade your premium access at',
            'https://developer.twitter.com/apps.',
        ].join(' '));
        return false;
        }
        const response = await this.api.post(`/account_activity/all/${this.env}/subscriptions.json`);
        if (response.statusCode === 204) {
            console.log(`Subscribed to activities.`);
            this.updateSubscriptionCount(1);
            return true;
        } else {
            throw new UserSubscriptionError(response);
            return false;
        }
    }
    
    /**
     * Unsubscribe from the account activity of the authenticated user.
     * @param userId The id of the account which acitvity should be unsubscribed.
     */
    private async unsubscribe(userId) {
        const response = await this.api.del(`/account_activity/all/${this.env}/subscriptions/${userId}.json`, AuthType.BEARER);
        
        if (response.statusCode === 204) {
            console.log(`Unsubscribed from ${userId}'s activities.`);
            this.updateSubscriptionCount(-1);
            return true;
        } else {
            throw new UserSubscriptionError(response);
            return false;
        }
    }
        
        
        
        
        
        
        
        
        
    }