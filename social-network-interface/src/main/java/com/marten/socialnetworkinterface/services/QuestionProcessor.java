package com.marten.socialnetworkinterface.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Service
public class QuestionProcessor {

    @Autowired
    private Twitter twitter;

    @Autowired
    private QAService qaService;

    @Autowired
    private BlockingQueue<Status> twitterMentionsQueue;

    @Autowired
    private BlockingQueue<DirectMessage> twitterDMsQueue;


    /**
     * Gets answer to question in the given status from the qa service and posts it as reply to the tweet.
     * Supports answers that are longer than 280 characters.
     * @param question Tweet that contains the question to be answered
     * @throws TwitterException if twitter api request fails
     */
    @Async
    public void answerTwitterMention(Status question) throws TwitterException {
        String answer = qaService.answerQuestion(question.getText().replace("@opalbottest", ""));
        long replyToId = question.getId();
        if (answer.length() > 280) {
            // split answer in 280 letter chunks
            String[] answers = answer.split("(?<=\\G.{" + 280 + "})");
            for (String a : answers) {
                StatusUpdate statusUpdate = new StatusUpdate(a);
                statusUpdate.setInReplyToStatusId(replyToId);
                statusUpdate.setAutoPopulateReplyMetadata(true);
                twitter.updateStatus(statusUpdate);
                replyToId = twitter.getUserTimeline().get(0).getId();
            }
        }


    }

    @Async
    public void answerTwitterDM(DirectMessage question) throws TwitterException {
        String answer = qaService.answerQuestion(question.getText());
        twitter.sendDirectMessage(question.getSenderId(), answer);
    }

}
