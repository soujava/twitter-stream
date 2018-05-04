package br.org.soujava.twitter.utils;

import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.service.CallableThreadTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class TestUtil {

    private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    public static Twitter createTweet(String tweetCreatedDate, String tweetId, String userCreatedDate, long userId) {
        Twitter twitter = new Twitter();
        twitter.setTwitterUser(createUser(userCreatedDate, userId));
        twitter.setCreatedAt(tweetCreatedDate);
        twitter.setId(tweetId);
        twitter.setText("Say Hello");
        return twitter;
    }

    public static User createUser(String userCreatedDate, long userId) {
        User user = new User();
        user.setCreatedAt(userCreatedDate);
        user.setId(userId);
        user.setName("Old chicken");
        user.setScreenName("Old chicken");
        return user;
    }

    public static String getValidRandomTwitt() throws FileNotFoundException {
        InputStream source = CallableThreadTest.class.getResourceAsStream("/message.txt");
        int messagesReceived = 0;
        int maxMessages = 1;
        boolean keepReceiving = true;

        List<String> ls = new ArrayList();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(source));

            while ((messagesReceived != maxMessages) && keepReceiving) {

                String message = reader.readLine();

                messagesReceived++;

                ls.add(message);
            }

        } catch (Throwable t) {
            logger.debug("Exception while retrieving messages", t);
        } finally {
            logger.debug("Shutting down stop timer");
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, ls.size());

        return ls.get(randomNum);
    }

}
