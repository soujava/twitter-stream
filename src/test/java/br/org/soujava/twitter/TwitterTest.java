package br.org.soujava.twitter;

import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({UnitTests.class})
public class TwitterTest {

    private Twitter tweet;
    private String id;
    private String text;
    private User user;
    private String createdAt;

    @Before
    public void setup() {
        tweet = new Twitter();
        user = new User();
        id = "1";
        text = "Abc";
        createdAt = "Mon Apr 30 00:00:00 +0000 2018";
    }

    @Test
    public void testGettersAndSetters() {
        tweet.setCreatedAt(createdAt);
        tweet.setText(text);
        tweet.setId(id);
        tweet.setTwitterUser(user);

        assertEquals(id, tweet.getId());
        assertEquals(text, tweet.getText());
        assertEquals(createdAt, tweet.getCreatedAt());
        assertEquals(1525046400, tweet.getCreated());
    }

    @Test
    public void testEqualsHashCode_Symmetric() {
        Twitter x = TestUtil.createTweet("Sun Mar 26 00:00:00 +0000 2017", "990942810877693953", "Wed Feb 08 00:00:00 +0000 2012", 843877597226418180L);
        Twitter y = TestUtil.createTweet("Sun Mar 26 00:00:00 +0000 2017", "990942810877693953", "Wed Feb 08 00:00:00 +0000 2012", 843877597226418180L);
        Twitter z = TestUtil.createTweet("Sun Mar 26 00:00:00 +0000 2017", "990942694859042817", "Thu Aug 04 00:00:00 +0000 2016", 924475372359610368L);
        assertTrue(x.equals(y));
        assertFalse(z.equals(y));
        assertTrue(x.hashCode() == y.hashCode());
        assertFalse(z.hashCode() == y.hashCode());
    }

}