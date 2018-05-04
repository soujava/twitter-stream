package br.org.soujava.twitter.service;

import br.org.soujava.twitter.UnitTests;
import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.utils.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@Category({UnitTests.class})
public class ProcessorTest {

    private Processor processor;

    private List<Twitter> twitters;

    @Before
    public void doBeforeEachTestCase() throws IOException {
        twitters = createListOfTweets();
        processor = new Processor(twitters);
    }

    private List<Twitter> createListOfTweets() {
        List<Twitter> tweets = new ArrayList<>();
        Twitter tweetOne = TestUtil.createTweet("Sat Mar 25 10:52:18 +0000 2017", "1234", "Tue Feb 07 14:21:34 +0000 2012", 485725488);
        Twitter tweetTwo = TestUtil.createTweet("Sat Mar 25 10:52:22 +0000 2017", "4321", "Thu Aug 04 14:34:05 +0000 2016", 761208591);
        Twitter tweetThree = TestUtil.createTweet("Sat Mar 25 10:52:21 +0000 2017", "4322", "Thu Aug 04 14:34:05 +0000 2016", 761208591);
        Twitter tweetFour = TestUtil.createTweet("Sat Mar 25 10:52:23 +0000 2017", "3333", "Thu Aug 04 14:34:05 +0000 2016", 761208692);
        tweets.add(tweetOne);
        tweets.add(tweetTwo);
        tweets.add(tweetThree);
        tweets.add(tweetFour);
        return tweets;
    }


    @Test
    public void testMessageProcessor_Success() {
        SortedMap<User, SortedSet<Twitter>> messageRetrive = processor.process();
        User a = messageRetrive.firstKey();
        User b = messageRetrive.lastKey();

        assertNotNull(a);
        assertNotNull(b);
        assertNotEquals(a, b);
    }

}
