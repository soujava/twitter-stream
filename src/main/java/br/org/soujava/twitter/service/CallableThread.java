package br.org.soujava.twitter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.concurrent.*;

/**
 * This class is
 */
public class CallableThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(CallableThread.class);

    private InputStream source;

    private BlockingQueue<String> messageQueue;

    private int maxMessages;

    private int followingTimeout;

    private boolean keepReceiving = true;

    public CallableThread(InputStream source, BlockingQueue<String> messageQueue, int maxMessages,
                          int followingTimeout) {
        this.source = source;
        this.followingTimeout = followingTimeout;
        this.messageQueue = messageQueue;
        this.maxMessages = maxMessages;

    }

    @Override
    public Boolean call() throws Exception {

        logger.debug("Started retrieving messages");

        int messagesReceived = 0;

        //set timeout for expiration, eg: 30 secs
        ExecutorService timeOut = setTimeOut();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(source));

            while ((messagesReceived != maxMessages) && keepReceiving) {

                String message = reader.readLine();
                if (message == null) {
                    break;
                }
                messageQueue.offer(message);
                logger.debug("Retrieved message: {}", message);

                messagesReceived++;
            }

        } catch (Throwable t) {
            logger.debug("Exception while retrieving messages", t);
        } finally {

            logger.debug("Shutting down stop timer");
            timeOut.shutdownNow();
        }

        logger.debug("Tweet retrieving stopped");
        logger.debug("Total {} message(s) retrieved", messagesReceived);

        return Boolean.TRUE;
    }

    /**
     * Set the timeout for the thread to be executed, eg: 30 secs
     * @return
     */
    private ExecutorService setTimeOut() {

        Runnable stopTimer = () -> {
            keepReceiving = false;
            logger.debug("Stop timer expired. Halting message retriever!");
        };

        logger.debug("Setting stop timer");
        ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.schedule(stopTimer, followingTimeout, TimeUnit.SECONDS);

        return scheduledExecutor;
    }
}
