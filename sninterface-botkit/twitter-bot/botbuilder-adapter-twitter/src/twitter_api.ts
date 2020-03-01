/**
 * @module botbuilder-adapter-twitter
 */
/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

import * as request from 'request-promise-native';

/**
 * A simple API client for the Twitter API. Automatically signs requests with auth credentials.
 * It can be used to call any API provided by Twitter.
 *
 */
export class TwitterAPI {
    private auth: TwitterOAuth;
    private api_host: string;
    private api_version: string;
    private _bearer: string;

    /**
     * Create a TwitterAPI client.
     * ```
     * let api = new TwitterAPI(OAUTH);
     * await api.callAPI('/some/api','POST', {some_options});
     * ```
     * @param oauth the oAuth credentials generated in the Twitter developer portal
     * @param api_host optional root hostname for constructing api calls, defaults to graph.Twitter.com
     * @param api_version optional api version used when constructing api calls, defaults to v3.2
     */
    public constructor(oauth: TwitterOAuth, api_host = 'api.twitter.com', api_version = '1.1') {
        this.auth = oauth;
        this.api_host = api_host;
        this.api_version = api_version;
    }
    
    /**
     * Fetch bearer token from Twitter API to authenticate further requests.
     */
    private async getBearerToken() {
        if (!this._bearer) {
            const requestConfig = {
                url: 'https://api.twitter.com/oauth2/token',
                auth: {
                    user: this.auth.consumer_key,
                    pass: this.auth.consumer_secret,
                },
                form: {
                    grant_type: 'client_credentials',
                },
                resolveWithFullResponse: true,
            };
            const res =  await request.post(requestConfig)
                .catch(err => { throw new Error(err.message)})
            this._bearer = JSON.parse(res.body).access_token
        }
        return this._bearer;
    }

    /**
     * Verify specified credentials with the Twitter API.
     * @param auth The credentials that should be verified.
     */
    public async verifyCredentials(auth: TwitterOAuth) {
        const response = await this.get('/account/verify_credentials.json');
        if (response.statusCode === 200) {
            return JSON.parse(response.body);
        } else {
            throw new Error(response);
            return null;
        }
    }

    /**
     * Send a HTTP GET request to one of the Twitter APIs.
     * @param path Path to the API endpoint, for example `/direct_messages/events/new.json`.
     * @param authType The type of authentication that the request should be signed with, for example USER_CONTEXT, BEARER, NONE. (default: USER_CONTEXT)
     * @param payload An object to be sent as path parameters to the API call.
     */
    public async get(
        path: string,
        authType: AuthType = AuthType.USER_CONTEXT,
        payload?: any,
    ): Promise<any> {
        return this.callAPI(path, HttpMethod.GET, authType, payload, PayloadType.PATH_PARAMS);
    }

    /**
     * Send a HTTP POST request to one of the Twitter APIs.
     * @param path Path to the API endpoint, for example `/direct_messages/events/new.json`.
     * @param authType The type of authentication that the request should be signed with, for example USER_CONTEXT, BEARER, NONE. (default: USER_CONTEXT)
     * @param payload An object to be sent as path parameters, json or form to the API call.
     * @param payloadType How the payload should be appended to the request, for example JSON, FORM, PATH_PARAMS. (default: JSON)
     */
    public async post(
        path: string,
        authType: AuthType = AuthType.USER_CONTEXT,
        payload?: any,
        payloadType: PayloadType = PayloadType.JSON
    ): Promise<any> {
        return this.callAPI(path, HttpMethod.POST, authType, payload, payloadType);
    }

    /**
     * Send a HTTP PUT request to one of the Twitter APIs.
     * @param path Path to the API endpoint, for example `/direct_messages/events/new.json`.
     * @param authType The type of authentication that the request should be signed with, for example USER_CONTEXT, BEARER, NONE. (default: USER_CONTEXT)
     * @param payload An object to be sent as path parameters, json or form to the API call.
     * @param payloadType How the payload should be appended to the request, for example JSON, FORM, PATH_PARAMS. (default: JSON)
     */
    public async put(
        path: string,
        authType: AuthType = AuthType.USER_CONTEXT,
        payload?: any,
        payloadType: PayloadType = PayloadType.JSON
    ): Promise<any> {
        return this.callAPI(path, HttpMethod.PUT, authType, payload, payloadType);
    }

    /**
     * Send a HTTP DELETE request to one of the Twitter APIs.
     * @param path Path to the API endpoint, for example `/direct_messages/events/new.json`.
     * @param authType The type of authentication that the request should be signed with, for example USER_CONTEXT, BEARER, NONE. (default: USER_CONTEXT)
     * @param payload An object to be sent as path parameters, json or form to the API call.
     * @param payloadType How the payload should be appended to the request, for example JSON, FORM, PATH_PARAMS. (default: PATH_PARAMS)
     */
    public async del(
        path: string,
        authType: AuthType = AuthType.USER_CONTEXT,
        payload?: any,
        payloadType: PayloadType = PayloadType.PATH_PARAMS
    ): Promise<any> {
        return this.callAPI(path, HttpMethod.DELETE, authType, payload, payloadType);
    }


    /**
     * Call one of the Twitter APIs
     * @param path Path to the API endpoint, for example `/direct_messages/events/new.json`.
     * @param method HTTP method, for example POST, GET, DELETE or PUT.
     * @param authType The type of authentication that the request should be signed with, for example USER_CONTEXT, BEARER, NONE.
     * @param payload An object to be sent as path parameters, json or form to the API call.
     * @param payloadType How the payload should be appended to the request, for example JSON, FORM, PATH_PARAMS
     */
    public async callAPI(path: string, method = 'POST', authType: AuthType, payload?: any, payloadType?: PayloadType): Promise<any> {

        let queryString = '?';
        let body;
        let auth;
        let json = false;

        // set authentication
        switch(authType) {
            case AuthType.USER_CONTEXT:
                auth = { oauth: this.auth };
                break;
            case AuthType.BEARER:
                auth = { auth: { bearer: await this.getBearerToken()}};
                break;
            case AuthType.NONE:
                break;
        }
        // set payload
        if (payload) {
            switch(payloadType) {
                case PayloadType.JSON:
                    body = { body: payload };
                    json = true;
                    break;
                case PayloadType.PATH_PARAMS:
                    for (const key in payload) {
                        queryString = queryString + `${ encodeURIComponent(key) }=${ encodeURIComponent(payload[key]) }&`;
                    }
                    break;
                case PayloadType.FORM:
                    body = { form: payload };
                    break;
            }
        }

       
        return request({
            method: method,
            url: `https://${ this.api_host }/${ this.api_version }${ path }${ queryString }`,
            json: json,
            ...body,
            ...auth,
            resolveWithFullResponse: true,
        })
    }


    
    /**
     * Posts tweets as thread in reply to the status defined in replyToId parameter.
     * @param payloads The tweet objects to post as reply.
     * @param replyToId The id of the tweet that should be replied to.
     */
    public async postThreadReply(payloads: any[], replyToId: string) {
        let in_reply_to_id = replyToId;
        for (let i = 0; i < payloads.length; i++) {
            payloads[i].in_reply_to_status_id = in_reply_to_id;
            const res = await this.post('/statuses/update.json', AuthType.USER_CONTEXT, payloads[i], PayloadType.FORM);
            in_reply_to_id = JSON.parse(res.body).id_str;
        }
    }
}

/**
 * This interface defines all keys necessary to make an user-context authenticated request to the Twitter API.
 * Can be obtained here https://developer.twitter.com/en/apps/
 */
export interface TwitterOAuth {
    token: string;
    token_secret: string;
    consumer_key: string;
    consumer_secret: string;
}

export enum HttpMethod {
    POST = 'POST',
    GET = 'GET',
    DELETE = 'DELETE',
    PUT = 'PUT'
}

/**
 * Look here for different authentication methods of the Twitter API:
 * https://developer.twitter.com/en/docs/basics/authentication/overview
 */
export enum AuthType {
    USER_CONTEXT,
    BEARER,
    NONE
}

export enum PayloadType {
    JSON,
    FORM,
    PATH_PARAMS
}