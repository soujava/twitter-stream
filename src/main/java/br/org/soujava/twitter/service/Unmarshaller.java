package br.org.soujava.twitter.service;

import br.org.soujava.twitter.model.Twitter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Unmarshaller implements Callable<List<Twitter>> {

    private static final Logger logger = LoggerFactory.getLogger(Unmarshaller.class);

    private boolean keepRunning = true;

    private BlockingQueue<String> messageQueue;


    ObjectMapper mapper = new ObjectMapper();


    public Unmarshaller(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public List<Twitter> call() throws Exception {


        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        ArrayList<Twitter> tweets = new ArrayList<>();

        logger.debug("Started un-marshalling received tweets");

        while (!messageQueue.isEmpty() || keepRunning) {

            try {

                String jsonString = messageQueue.poll(1, TimeUnit.SECONDS);

                if (jsonString != null) {


                    Twitter tweet = mapper.readValue(jsonString, Twitter.class);

                    if (tweet.getId() == null || tweet.getId().trim().isEmpty()) {
                        continue;
                    }

                    tweets.add(tweet);
                    logger.debug("Tweet : {}", tweet.getId());
                }

            } catch (Throwable t) {
                logger.debug("Exception while un-marshalling message", t);
            }
        }

        logger.debug("Tweet un-marshalling completed");
        logger.debug("Total {} message(s) un-marshalling", tweets.size());

        return tweets;
    }

    public void concludeProcessing() {
        keepRunning = false;
    }

}
