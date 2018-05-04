package br.org.soujava.twitter;

import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({UnitTests.class})
public class UserTest {

    private User user;

    private long id;

    private String screenName;

    private String name;

    private String createdAt;


    @Before
    public void setup() {
        user = new User();
        id = 1;
        screenName = "Old chicken";
        name = "Old chicken";
        createdAt = "Sun Apr 29 10:52:18 +0000 2018";
    }

    @Test
    public void testGettersAndSetters() {
        user.setCreatedAt(createdAt);
        user.setScreenName(screenName);
        user.setName(name);
        user.setId(id);

        assertEquals(id,user.getId());
        assertEquals(name, user.getName());
        assertEquals(screenName, user.getScreenName());
        assertEquals(createdAt,user.getCreatedAt());
        assertEquals(1524999138,user.getCreated());
    }

    @Test
    public void testEqualsHashCode_Symmetric() {
        User x = TestUtil.createUser("Tue Feb 07 14:21:34 +0000 2012", 485725488);
        User y = TestUtil.createUser("Tue Feb 07 14:21:34 +0000 2012", 485725488);
        User z = TestUtil.createUser("Thu Aug 04 14:34:05 +0000 2016", 761208591);
        assertTrue(x.equals(y));
        assertFalse(z.equals(y));
        assertTrue(x.hashCode() == y.hashCode());
        assertFalse(z.hashCode() == y.hashCode());
    }

}
