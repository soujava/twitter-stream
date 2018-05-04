package br.org.soujava.twitter.controller;

import br.org.soujava.twitter.model.MessageGrupedVO;
import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.oauth.TwitterAuthenticationException;
import br.org.soujava.twitter.oauth.TwitterAuthenticator;
import br.org.soujava.twitter.service.CallableThread;
import br.org.soujava.twitter.service.Processor;
import br.org.soujava.twitter.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.http.*;

import br.org.soujava.twitter.service.Unmarshaller;
import br.org.soujava.twitter.utils.PropsUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * TwitterConnect is responsible for following, auth and log twitts
 */
public class TwitterConnect {

    private static final Logger logger = LoggerFactory.getLogger(TwitterConnect.class);
    private List<Twitter> twitters = new ArrayList<>();
    private HttpRequestFactory httpRequestFactory;
    private ExecutorService executorServiceMessagePool = Executors.newSingleThreadExecutor();
    private ExecutorService executorServiceMessageDeserializePool = Executors.newSingleThreadExecutor();
    private ObjectMapper mapper = new ObjectMapper();


    /**
     * Start Following for X tweets for X seconds
     *
     * @param stringToFind
     * @param maxMgsPerTime
     * @param TimeoutFollowing
     * @throws IOException
     */
    public void following(String stringToFind, int maxMgsPerTime, int TimeoutFollowing) throws IOException {

        String twitterUrl = new PropsUtils().getPropertyValueString(Constants.TWITTER_URL, Constants.TWITTER_URL_DEFAULT);

        logger.debug("Start Following, {} twitters, {} seconds url {}", maxMgsPerTime, TimeoutFollowing, twitterUrl + stringToFind);

        GenericUrl url = new GenericUrl(twitterUrl + stringToFind);
        HttpRequest request = httpRequestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();

        if (httpResponse.getStatusCode() == HttpStatusCodes.STATUS_CODE_OK) {

            processTwitts(httpResponse.getContent(), maxMgsPerTime, TimeoutFollowing, stringToFind);

        } else {
            logger.error("Error following() {} message: {} ", httpResponse.getStatusCode(), httpResponse.getStatusMessage());

        }

        httpResponse.disconnect();
        logger.debug("Following disconnected!");
    }

    /**
     * Start Following for X tweets for X seconds
     *
     * @param source
     * @param maxMgsPerTime
     * @param TimeoutFollowing
     */
    private void processTwitts(InputStream source, int maxMgsPerTime, int TimeoutFollowing, String stringToFind) {

        logger.debug("Start Following, {} twitters, {} seconds", maxMgsPerTime, TimeoutFollowing);

        Date dateStarter = new java.util.Date();

        LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(maxMgsPerTime);

        CallableThread tweetsCallableThread = new CallableThread(source, messageQueue, maxMgsPerTime, TimeoutFollowing);

        Future<Boolean> messageReaderResult = executorServiceMessagePool.submit(tweetsCallableThread);

        Unmarshaller tweetsUnmarshaller = new Unmarshaller(messageQueue);
        Future<List<Twitter>> messageDeserializationResult = executorServiceMessageDeserializePool.submit(tweetsUnmarshaller);

        Integer TOTAL_ALL_ROUNDS = 0;

        Integer ROUND_COUNT = 0;

        Double TOTAL_MESSEGES_SECOND = new Double(0);
        DB db = DBMaker.fileDB("stats.db").make();
        try {

            ConcurrentMap statistics = db.hashMap("STATISTICS").createOrOpen();

            boolean receivedAllMessages = messageReaderResult.get();

            if (receivedAllMessages) {

                Date dateFinished = new java.util.Date();

                long totalSecond = getDateDiff(dateStarter, dateFinished, TimeUnit.SECONDS);

                tweetsUnmarshaller.concludeProcessing();
                List<Twitter> deSerializedTweets = messageDeserializationResult.get();
                twitters.addAll(deSerializedTweets);

                Integer TOTAL_THIS_ROUND = deSerializedTweets.size();

                // ** PRINT STATISTICS ** //

                Object TOTAL_ALL_ROUNDS_OBJ = statistics.get("TOTAL_ALL_ROUNDS");

                Object ROUND_COUNT_OBJ = statistics.get("ROUND_COUNT");

                if (ROUND_COUNT_OBJ instanceof Integer) {
                    ROUND_COUNT = (Integer) ROUND_COUNT_OBJ;
                }

                if (TOTAL_ALL_ROUNDS_OBJ instanceof Integer) {
                    TOTAL_ALL_ROUNDS = (Integer) TOTAL_ALL_ROUNDS_OBJ;
                }

                TOTAL_ALL_ROUNDS = TOTAL_ALL_ROUNDS + TOTAL_THIS_ROUND;
                statistics.put("TOTAL_ALL_ROUNDS", TOTAL_ALL_ROUNDS);

                ROUND_COUNT = ROUND_COUNT + 1;
                statistics.put("ROUND_COUNT", ROUND_COUNT);


                TOTAL_MESSEGES_SECOND = TOTAL_THIS_ROUND.doubleValue() / totalSecond; //new Long(totalSecond) /new Long(TOTAL_THIS_ROUND.intValue()) ;

                String pattern = "###.##";
                DecimalFormat decimalFormat = new DecimalFormat(pattern);

                ObjectNode objectNode1 = mapper.createObjectNode();
                objectNode1.put("TOTAL_THIS_ROUND", TOTAL_THIS_ROUND);
                objectNode1.put("TOTAL_ALL_ROUNDS", TOTAL_ALL_ROUNDS);
                objectNode1.put("TOTAL_MESSAGES_PER_SECOND", decimalFormat.format(TOTAL_MESSEGES_SECOND));
                objectNode1.put("ROUND_COUNT", ROUND_COUNT);
                objectNode1.put("FOLLOW.KEYWORDS", stringToFind);

                logger.info(mapper.writer().writeValueAsString(objectNode1));

            }

        } catch (Exception e) {
            logger.error("Error: following(). Aborting tracking!", e);
        } finally {

            logger.debug("Shutting down....");
            executorServiceMessagePool.shutdown();

            logger.debug("Shutting down....");
            executorServiceMessageDeserializePool.shutdown();
            db.close();

        }
    }

    /**
     * authenticate with twitter
     *
     * @throws TwitterAuthenticationException
     */
    public void authenticate() throws TwitterAuthenticationException {

        PropsUtils propsUtils = new PropsUtils();

        String consumerKEY = propsUtils.getPropertyValueString(Constants.TWITTER_CONSUMER_kEY, Constants.TWITTER_CONSUMER_kEY_DEFAULT);
        String consumerSECRET = propsUtils.getPropertyValueString(Constants.TWITTER_CONSUMER_SECRET, Constants.TWITTER_CONSUMER_SECRET_DEFAULT);

        TwitterAuthenticator twitterAuthenticator = new TwitterAuthenticator(
                System.out,
                consumerKEY,
                consumerSECRET
        );

        httpRequestFactory = twitterAuthenticator.getAuthorizedHttpRequestFactory();
    }

    /**
     * printAuthorMessages will log twitts to info; logback will pipe it to twitter.log file
     */
    public void printAuthorMessages() {
        Processor processorMessage = new Processor(twitters);
        SortedMap<User, SortedSet<Twitter>> sortedMessages = processorMessage.process();
        sortedMessages.forEach(printMessagesAuthor);

    }

    /**
     * Sort map
     *
     * @return
     */
    public SortedMap<User, SortedSet<Twitter>> getSortedMap() {

        Processor processorMsg = new Processor(twitters);
        SortedMap<User, SortedSet<Twitter>> sortedMessages = processorMsg.process();

        sortedMessages.forEach(printMessagesAuthor);
        return sortedMessages;
    }


    /**
     * Wrapper func for logMessages
     */
    private BiConsumer<User, SortedSet<Twitter>> printMessagesAuthor = (author, authorMessages) -> {

        logMessages(authorMessages);

    };

    /**
     * logMessages will log twitts to info; logback will pipe it to twitter.log file
     *
     * @param msgs
     */
    public void logMessages(SortedSet<Twitter> msgs) {

        LinkedHashSet<MessageGrupedVO> messages = new LinkedHashSet<>();
        msgs.forEach(tweet -> {
            MessageGrupedVO m = new MessageGrupedVO(
                    tweet.getId(),
                    tweet.getCreated(),
                    tweet.getText(),
                    tweet.getTwitterUser().getId(),
                    tweet.getTwitterUser().getCreated(),
                    tweet.getTwitterUser().getName(),
                    tweet.getTwitterUser().getScreenName());
            messages.add(m);
        });

        ObjectMapper mapper = new ObjectMapper();

        try {
            logger.info(mapper.writeValueAsString(messages));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
