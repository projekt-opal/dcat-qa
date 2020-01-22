package com.marten.socialnetworkinterface.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import twitter4j.*;

import javax.annotation.PostConstruct;
import java.util.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@PropertySource("classpath:socialnetwork.properties")
@Service
public class TwitterQuestionsProvider {


    @Autowired
    private Twitter twitter;

    @Autowired
    private QuestionProcessor questionProcessor;

    private long lastMentionId = 0;

    private long lastDMId = 0;

    /**
     * ID of Marten Schmidt's personal Account
     */
    @Value("${twitter.accountId}")
    private long BOTID;

    private BlockingQueue<Status> twitterMentionsQueue = new LinkedBlockingQueue<>();
    
    private BlockingQueue<DirectMessage> twitterDMsQueue = new LinkedBlockingQueue<>();


    /**
     * Looks at most recent tweets to search for the id of the tweet that was last answered by the bot.
     * @throws TwitterException if a problem with twitter api exists
     */
    @PostConstruct
    public void initLastAnsweredTweetId() throws TwitterException {
        List<Status> timeline = twitter.getUserTimeline();
        if (timeline.size() > 0) {
            for (Status status : timeline) {
                long id = status.getInReplyToStatusId();
                if (id > this.lastMentionId) {
                    this.lastMentionId = id;
                }
            }
        } else {
            this.lastMentionId = 0;
        }
    }

    /**
     * Polls the twitter api every 12s for new (unanswered) mentions and direct messages and puts them in the queues.
     * Only looks at the 100 most recent messages since the last poll.
     * @throws TwitterException if a problem with twitter api exists
     */
    //@Scheduled(fixedRate = 12000)
    public void fetchMentions() throws TwitterException {
        System.out.println("fetch mentions");
        List<Status> mentions = lastMentionId == 0 ? getMentions() : getMentionsSince(lastMentionId);

        mentions.forEach(m -> {
            if (m.getUser().getId() != BOTID) {
                System.out.println(String.format("User %s tweeted: %s , id: %d", m.getUser().getName(), m.getText(), m.getId()));
                if (m.getId() > lastMentionId)
                    lastMentionId = m.getId();
                this.twitterMentionsQueue.add(m);
                try {
                    this.questionProcessor.answerTwitterMention(m);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    //@Scheduled(fixedRate = 12000)
    public void fetchDMs() throws TwitterException {
        System.out.println("fetch dms");
        List<DirectMessage> messages = getDirectMessages();
        if (messages.get(0).getId() > lastDMId) {
            Set<Long> closedConversations = new HashSet<>();
            messages.forEach(m -> {
                if (m.getSenderId() == BOTID) {
                    closedConversations.add(m.getRecipientId());
                } else if (!closedConversations.contains(m.getSenderId())) {
                    System.out.println(m.getText());
                    twitterDMsQueue.add(m);
                    try {
                        questionProcessor.answerTwitterDM(m);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            });
            lastDMId = messages.get(0).getId();
        }
    }

    private ResponseList<Status> getMentions() throws TwitterException {
        return twitter.getMentionsTimeline();
    }

    private ResponseList<Status> getMentionsSince(long sinceId) throws TwitterException {
        return twitter.getMentionsTimeline(new Paging(sinceId));
    }

    private ResponseList<DirectMessage> getDirectMessages() throws TwitterException{
        return twitter.getDirectMessages(100);
    }

    @Bean
    public BlockingQueue<Status> twitterMentionsQueue() {
        return this.twitterMentionsQueue;
    }

    @Bean
    public BlockingQueue<DirectMessage> twitterDMsQueue() {
        return this.twitterDMsQueue;
    }

}


