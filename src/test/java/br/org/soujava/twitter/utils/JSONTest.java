package br.org.soujava.twitter.utils;

import br.org.soujava.twitter.UnitTests;
import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

@Category({UnitTests.class})
public class JSONTest {


    ObjectMapper objectMapper = new ObjectMapper();

    private String json;

    @Before
    public void doBeforeEachTestCase() throws IOException {
        json = TestUtil.getValidRandomTwitt();
    }


    @Test
    public void testJsonUtil_Success() throws Exception {
        Twitter tweet = objectMapper.readValue(json, Twitter.class);

        assertNotNull(tweet);
        assertNotNull(tweet.getTwitterUser());

        assertNotNull(tweet.getTwitterUser().getScreenName());
        assertNotNull(tweet.getCreatedAt());
    }


    @Test
    public void testJsonUtil_WithDifferentObject() throws Exception {
        User tweet = objectMapper.readValue(json, User.class);
        assertNull(tweet.getScreenName());
        assertNull(tweet.getName());
    }


}
