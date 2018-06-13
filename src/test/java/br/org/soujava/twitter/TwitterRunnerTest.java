package br.org.soujava.twitter;

import br.org.soujava.twitter.controller.TwitterConnect;
import br.org.soujava.twitter.model.Twitter;
import br.org.soujava.twitter.model.User;
import br.org.soujava.twitter.utils.Constants;
import br.org.soujava.twitter.utils.PropsUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.SortedMap;
import java.util.SortedSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTests.class})
public class TwitterRunnerTest {

    @Test
    public void testTwitterApplication() throws Exception {

        PropsUtils propsUtils = new PropsUtils();
        TwitterConnect application = new TwitterConnect();

        application.authenticate();

        String searchKeyword = propsUtils.getPropertyValueString(Constants.FOLLOW_KEYWORD, "java");
        int limitMessages = 5;
        int duration = 30;

        application.following(searchKeyword, limitMessages, duration);
        application.printAuthorMessages();

        SortedMap<User, SortedSet<Twitter>> sort = application.getSortedMap();

        assertNotNull(sort);
        assertTrue(sort.size() <= 5);

        //assert a list contains textMessage with searchKeyword

        


    }

}
