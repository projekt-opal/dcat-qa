const assert = require('assert');
const { TwitterAdapter } = require('../lib');
const { FakeAPI, Res, Req, fakeVerifySignature } = require('./shared');

describe('TwitterAdapter', function() {

    let adapter;

    beforeEach(function () {
        adapter = new TwitterAdapter({
            oauth: {
                consumer_key: 'consumer_key',
                consumer_secret: 'consumer_secret',
                token: 'token',
                token_secret: 'token_secret'
            },
            webhook_env: 'env',
            webhook_url: 'url'
        });
        adapter.user = {
            "id": 1,
            "id_str": "1",
            "name": "Twitter API",
            "screen_name": "TwitterAPI",
            "location": "San Francisco, CA",
            "description": "The Real Twitter API. Tweets about API changes, service issues and our Developer Platform. Don't get an answer? It's on my website.",
            "url": "https://t.co/8IkCzCDr19",
            "entities": {
              "url": {
                "urls": [
                  {
                    "url": "https://t.co/8IkCzCDr19",
                    "expanded_url": "https://developer.twitter.com",
                    "display_url": "developer.twitter.com",
                    "indices": [
                      0,
                      23
                    ]
                  }
                ]
              },
              "description": {
                "urls": []
              }
            }
        }
    })

    it('should not construct without required parameters', function () {
        assert.throws(function () { let adapter = new Twitter({}) }, 'Foo');
    });

    it('should create a TwitterAdapter object', function () {
        assert((adapter instanceof TwitterAdapter), 'Adapter is wrong type');
    });

    it('should process an incoming tweet request that mentions the user into an activity...', function (done) {
        let res = new Res();
        adapter.processActivity(new Req({
            for_user_id: "2244994945",
            tweet_create_events: [
                {
                    "created_at": "Wed Oct 10 20:19:24 +0000 2018",
                    "id": 1050118621198921728,
                    "id_str": "1050118621198921728",
                    "text": "@TwitterAPI Hello!",
                    "truncated": true,
                    "entities": {
                        "hashtags": [],
                        "symbols": [],
                        "user_mentions": [
                        {
                            "screen_name": "TwitterAPI",
                            "name": "Twitter API",
                            "id": 1,
                            "id_str": "1",
                            "indices": [
                                0,
                                10
                            ]
                        }
                        ],
                        "urls": []
                    },
                    "source": "<a href='http://twitter.com' rel='nofollow'>Twitter Web Client</a>",
                    "in_reply_to_status_id": null,
                    "in_reply_to_status_id_str": null,
                    "in_reply_to_user_id": 1,
                    "in_reply_to_user_id_str": "1",
                    "in_reply_to_screen_name": "TwitterAPI",
                    "user": {
                        "id": 2,
                        "id_str": "2",
                        "name": "Twitter Test",
                        "screen_name": "TwitterTest",
                        "location": "San Francisco, CA",
                        "description": "The Real Twitter API. Tweets about API changes, service issues and our Developer Platform. Don't get an answer? It's on my website.",
                        "url": "https://t.co/8IkCzCDr19",
                        "entities": {
                        "url": {
                            "urls": [
                            {
                                "url": "https://t.co/8IkCzCDr19",
                                "expanded_url": "https://developer.twitter.com",
                                "display_url": "developer.twitter.com",
                                "indices": [
                                0,
                                23
                                ]
                            }
                            ]
                        },
                        "description": {
                            "urls": []
                        }
                        },
                        "protected": false,
                        "followers_count": 6128663,
                        "friends_count": 12,
                        "listed_count": 12900,
                        "created_at": "Wed May 23 06:01:13 +0000 2007",
                        "favourites_count": 32,
                        "utc_offset": null,
                        "time_zone": null,
                        "geo_enabled": null,
                        "verified": true,
                        "statuses_count": 3659,
                        "lang": "null",
                        "contributors_enabled": null,
                        "is_translator": null,
                        "is_translation_enabled": null,
                        "profile_background_color": "null",
                        "profile_background_image_url": "null",
                        "profile_background_image_url_https": "null",
                        "profile_background_tile": null,
                        "profile_image_url": "null",
                        "profile_image_url_https": "https://pbs.twimg.com/profile_images/942858479592554497/BbazLO9L_normal.jpg",
                        "profile_banner_url": "https://pbs.twimg.com/profile_banners/6253282/1497491515",
                        "profile_link_color": "null",
                        "profile_sidebar_border_color": "null",
                        "profile_sidebar_fill_color": "null",
                        "profile_text_color": "null",
                        "profile_use_background_image": null,
                        "has_extended_profile": null,
                        "default_profile": false,
                        "default_profile_image": false,
                        "following": null,
                        "follow_request_sent": null,
                        "notifications": null,
                        "translator_type": "null"
                    },
                    "geo": null,
                    "coordinates": null,
                    "place": null,
                    "contributors": null,
                    "is_quote_status": false,
                    "retweet_count": 161,
                    "favorite_count": 296,
                    "favorited": false,
                    "retweeted": false,
                    "possibly_sensitive": false,
                    "possibly_sensitive_appealable": false,
                    "lang": "en"
                    }
                
            ]
        }), res, async (context) => {
            assert(context.activity.type === 'tweet', 'activity is not a tweet');
            assert(context.activity.text === 'Hello!', 'text is wrong');
            assert(context.activity.from.id === "2",'from id is wrong');
            assert(context.activity.recipient.id === "1",'recipient id is wrong');
            assert(context.activity.channelData.user_mentions[0].id === 1, 'mention entity is not in channelData');
            done();
        });
    });



    it ('should not process an incoming tweet request that does not mention the user into an activity...', function(done) {
        adapter.verifySignature = fakeVerifySignature;
        let res = new Res();
        adapter.processActivity(new Req({
            for_user_id: "1",
            tweet_create_events: [
                {
                    "created_at": "Wed Oct 10 20:19:24 +0000 2018",
                    "id": 1050118621198921728,
                    "id_str": "1050118621198921728",
                    "text": "Hello!",
                    "truncated": true,
                    "entities": {
                        "hashtags": [],
                        "symbols": [],
                        "user_mentions": [],
                        "urls": []
                    },
                    "source": "<a href='http://twitter.com' rel='nofollow'>Twitter Web Client</a>",
                    "in_reply_to_status_id": null,
                    "in_reply_to_status_id_str": null,
                    "in_reply_to_user_id": null,
                    "in_reply_to_user_id_str": null,
                    "in_reply_to_screen_name": null,
                    "user": {
                        "id": 2,
                        "id_str": "2",
                        "name": "Twitter Test",
                        "screen_name": "TwitterTest",
                        "location": "San Francisco, CA",
                        "description": "The Real Twitter API. Tweets about API changes, service issues and our Developer Platform. Don't get an answer? It's on my website.",
                        "url": "https://t.co/8IkCzCDr19",
                        "entities": {
                        "url": {
                            "urls": [
                            {
                                "url": "https://t.co/8IkCzCDr19",
                                "expanded_url": "https://developer.twitter.com",
                                "display_url": "developer.twitter.com",
                                "indices": [
                                0,
                                23
                                ]
                            }
                            ]
                        },
                        "description": {
                            "urls": []
                        }
                        },
                        "protected": false,
                        "followers_count": 6128663,
                        "friends_count": 12,
                        "listed_count": 12900,
                        "created_at": "Wed May 23 06:01:13 +0000 2007",
                        "favourites_count": 32,
                        "utc_offset": null,
                        "time_zone": null,
                        "geo_enabled": null,
                        "verified": true,
                        "statuses_count": 3659,
                        "lang": "null",
                        "contributors_enabled": null,
                        "is_translator": null,
                        "is_translation_enabled": null,
                        "profile_background_color": "null",
                        "profile_background_image_url": "null",
                        "profile_background_image_url_https": "null",
                        "profile_background_tile": null,
                        "profile_image_url": "null",
                        "profile_image_url_https": "https://pbs.twimg.com/profile_images/942858479592554497/BbazLO9L_normal.jpg",
                        "profile_banner_url": "https://pbs.twimg.com/profile_banners/6253282/1497491515",
                        "profile_link_color": "null",
                        "profile_sidebar_border_color": "null",
                        "profile_sidebar_fill_color": "null",
                        "profile_text_color": "null",
                        "profile_use_background_image": null,
                        "has_extended_profile": null,
                        "default_profile": false,
                        "default_profile_image": false,
                        "following": null,
                        "follow_request_sent": null,
                        "notifications": null,
                        "translator_type": "null"
                    },
                    "geo": null,
                    "coordinates": null,
                    "place": null,
                    "contributors": null,
                    "is_quote_status": false,
                    "retweet_count": 161,
                    "favorite_count": 296,
                    "favorited": false,
                    "retweeted": false,
                    "possibly_sensitive": false,
                    "possibly_sensitive_appealable": false,
                    "lang": "en"
                    }
                
            ]
        }), res, async(context) => {
            assert(false, 'logic is run but should not');
            
        });
        done();
    });

    it ('should process a direct message incoming request into an activity...', function(done) {
        let res = new Res();
        adapter.processActivity(new Req({
            "for_user_id": "1",
	        "direct_message_events": [{
                "type": "message_create",
                "id": "954491830116155396",
                "created_timestamp": "1516403560557",
                "message_create": {
                    "target": {
                        "recipient_id": "1"
                    },
                    "sender_id": "2",
                    "source_app_id": "13090192",
                    "message_data": {
                        "text": "Hello World!",
                        "entities": {
                            "hashtags": [],
                            "symbols": [],
                            "user_mentions": [],
                            "urls": []
                        }
                    }
                }
	        }],
        }), res, async(context) => {
            assert(context.activity.type === 'message', 'activity is not a message');
            assert(context.activity.text === 'Hello World!', 'text is wrong');
            assert(context.activity.from.id === '2', 'from id is wrong');
            assert(context.activity.recipient.id === '1', 'recipient id is wrong')
            done();
        });
    });


    it ('should process a typing event incoming request into an activity...', function(done) {
        let res = new Res();
        adapter.processActivity(new Req({
            "for_user_id": "1",
	        "direct_message_indicate_typing_events": [{
                "created_timestamp": "1518127183443",
                "sender_id": "2",
                "target": {
                    "recipient_id": "1"
                }
	        }],
        }), res, async(context) => {
            assert(context.activity.type === 'typing', 'activity does not have type typing');
            assert(context.activity.from.id === '2', 'from id is wrong')
            assert(context.activity.recipient.id === '1', 'recipient id is wrong')
            done();
        });
    });

    it ('should process a mark read event incoming request into an activity...', function(done) {
        let res = new Res();
        adapter.processActivity(new Req({
            "for_user_id": "1",
	        "direct_message_mark_read_events": [{
                "created_timestamp": "1518452444662",
                "sender_id": "2",
                "target": {
                    "recipient_id": "1"
                },
                "last_read_event_id": "963085315333238788"
	        }],
        }), res, async(context) => {
            assert(context.activity.type === 'messageReaction', 'activity does not have type messageReaction');
            assert(context.activity.from.id === '2', 'from id is wrong')
            assert(context.activity.recipient.id === '1', 'recipient id is wrong')
            assert(context.activity.channelData.last_read_event_id === "963085315333238788", 'channel data is wrong')
            done();
        });
    });


      

});
