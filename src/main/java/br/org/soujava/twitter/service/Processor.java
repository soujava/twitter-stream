package br.org.soujava.twitter.service;

import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class Processor {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private List<Twitter> tweets;

    public Processor(List<Twitter> tweets) {
        this.tweets = tweets;
    }


    public SortedMap<User, SortedSet<Twitter>> process() {

        SortedMap<User, SortedSet<Twitter>> messagesGroupdByAuthors = new TreeMap<>(userSorter);

        Consumer<Twitter> groupByAuthor = (message) -> {

            logger.debug("Processing message: {}", message.getId());

            User twitterUser = message.getTwitterUser();

            if (!messagesGroupdByAuthors.containsKey(twitterUser)) {
                messagesGroupdByAuthors.put(twitterUser, new TreeSet<>(tweetSorter));
            }

            messagesGroupdByAuthors.get(twitterUser).add(message);
        };

        tweets.forEach(groupByAuthor);

        return messagesGroupdByAuthors;
    }

    private Comparator<Twitter> tweetSorter = (tweetOne, tweetTwo) -> {

        if (tweetTwo == null) {
            return -1;
        }
        return (int) (tweetOne.getCreated() - tweetTwo.getCreated());

    };

    private Comparator<User> userSorter = (userOne, userTwo) -> {

        if (userTwo == null) {
            return -1;
        }

        return (int) (userOne.getCreated() - userTwo.getCreated());

    };
}
