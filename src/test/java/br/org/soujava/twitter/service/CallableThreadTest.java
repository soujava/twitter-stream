package br.org.soujava.twitter.service;

import br.org.soujava.twitter.UnitTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertEquals;

@Category({UnitTests.class})
public class CallableThreadTest {

    private CallableThread retriver;

    private BlockingQueue<String> queue;

    private InputStream source;



    @Before
    public void doBeforeEachTestCase() throws IOException {
        source = CallableThreadTest.class.getResourceAsStream("/message.txt");
        queue = new LinkedBlockingDeque<>();
        int maxTweets = 10;
        int trackingTimeOut = 20;
        retriver = new CallableThread(source, queue, maxTweets, trackingTimeOut );

    }

    @Test
    public void testDeserialization_Success() throws Exception {
        boolean messageRetrive = retriver.call();
        assertEquals(true,messageRetrive);
    }

    @Test
    public void testDeserialization_SuccessWithDifferentTimeOut() throws Exception {
        int maxTweets = 1;
        int trackingTimeOut =1;
        retriver = new CallableThread(source, queue, maxTweets, trackingTimeOut );
        boolean messageRetrive = retriver.call();
        assertEquals(true,messageRetrive);
    }
}
