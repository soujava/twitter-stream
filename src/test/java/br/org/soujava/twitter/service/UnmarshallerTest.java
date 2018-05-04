package br.org.soujava.twitter.service;

import br.org.soujava.twitter.model.Twitter;

import java.util.concurrent.LinkedBlockingDeque;

import br.org.soujava.twitter.UnitTests;
import br.org.soujava.twitter.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({UnitTests.class})
public class UnmarshallerTest {

    private Unmarshaller unmarshaller;

    private LinkedBlockingDeque<String> messageQueue;


    @Before
    public void doBeforeEachTestCase() throws IOException {
        // GET RANDOM TWITTER FROM FILE
        String json = TestUtil.getValidRandomTwitt();

        messageQueue = new LinkedBlockingDeque<>();

        messageQueue.add(json);
        unmarshaller = new Unmarshaller(messageQueue);
    }

    @Test
    public void testDeserialization_Success() throws Exception {
        unmarshaller.concludeProcessing();
        List<Twitter> msg = unmarshaller.call();
        assertEquals(1, msg.size());
        assertNotNull(msg.get(0));

        assertNotNull(msg.get(0).getCreated());
        assertNotNull(msg.get(0).getCreatedAt());

        assertNotNull(msg.get(0).getTwitterUser());
        assertNotNull(msg.get(0).getTwitterUser().getScreenName());
    }


}
