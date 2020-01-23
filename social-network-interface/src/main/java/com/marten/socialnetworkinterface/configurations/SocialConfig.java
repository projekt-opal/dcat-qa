package com.marten.socialnetworkinterface.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;

@PropertySource("classpath:socialnetwork.properties")
@Configuration
public class SocialConfig {


    @Value("${twitter.consumerKey}")
    private String twitterConsumerKey;

    @Value("${twitter.consumerSecret}")
    private String twitterConsumerSecret;

    @Value("${twitter.accessToken}")
    private String twitterAccessToken;

    @Value("${twitter.accessTokenSecret}")
    private String twitterAccessTokenSecret;

    private final String ENV_NAME = "dev";

    private final String 


//    @PostConstruct
//    public void makeWebhookRequest() {
//        RestTemplate template = new RestTemplate();
//        template.postForObject("")
//    }


    public twitter4j.conf.Configuration config() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterConsumerKey)
                .setOAuthConsumerSecret(twitterConsumerSecret)
                .setOAuthAccessToken(twitterAccessToken)
                .setOAuthAccessTokenSecret(twitterAccessTokenSecret);
        return cb.build();
    }


    @Bean
    public Twitter twitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterConsumerKey)
                .setOAuthConsumerSecret(twitterConsumerSecret)
                .setOAuthAccessToken(twitterAccessToken)
                .setOAuthAccessTokenSecret(twitterAccessTokenSecret);



        return new TwitterFactory(cb.build()).getInstance();
    }



    public String getTwitterConsumerSecret() {
        return this.twitterConsumerSecret;
    }


    public void registerWebhook() {
        HttpClient httpClient = HttpClientFactory.getInstance(config().getHttpClientConfiguration());
        httpClient.post(String.format("https://api.twitter.com/1.1/account_activity/all/%s/webhooks.json?url=%s", ));

    }




}
