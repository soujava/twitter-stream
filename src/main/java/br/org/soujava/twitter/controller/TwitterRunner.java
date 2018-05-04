package br.org.soujava.twitter.controller;

import java.io.IOException;

import br.org.soujava.twitter.oauth.TwitterAuthenticationException;
import br.org.soujava.twitter.utils.Constants;
import br.org.soujava.twitter.utils.PropsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterRunner {

    private static final Logger logger = LoggerFactory.getLogger(TwitterRunner.class);

    public static void main(String[] args) {

        try {

            logger.debug("Start application");
            TwitterConnect application = new TwitterConnect();
            application.authenticate();

            //re-start constants, if override config present
            PropsUtils propsUtils = new PropsUtils();
            String tweetFollowWords = propsUtils.getPropertyValueString(Constants.FOLLOW_KEYWORD, Constants.FOLLOW_KEYWORD_VALUE);
            int limitMessages = propsUtils.getPropertyValueInt(Constants.FOLLOW_LIMIT, Constants.FOLLOW_LIMIT_VALUE);
            int maxFollowWordsTime = propsUtils.getPropertyValueInt(Constants.FOLLOW_TIME_OUT, Constants.FOLLOW_TIME_OUT_VALUE);

            logger.debug("Application Following {} twitters, until {} messages or {} seconds", Constants.FOLLOW_KEYWORD_VALUE, Constants.FOLLOW_LIMIT_VALUE, Constants.FOLLOW_TIME_OUT_VALUE);

            application.following(tweetFollowWords, limitMessages, maxFollowWordsTime);
            application.printAuthorMessages();

        } catch (TwitterAuthenticationException | IOException e) {
            logger.debug("Error: TwitterRunner application", e);
        }
    }

}
