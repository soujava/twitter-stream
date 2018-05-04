package br.org.soujava.twitter.utils;

import br.org.soujava.twitter.UnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category({UnitTests.class})
public class DateTimeTest {

    private final String DATE_PATTERN_ALL_DATA = "EEE MMM dd HH:mm:ss Z yyyy";
    private final String DATE_PATTERN_YEAR_HOURS = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    public void testDateUtil_Success()  {
        long epochTime = DateUtils.toEpochTime("Sat Mar 25 10:52:18 +0000 2017",DATE_PATTERN_ALL_DATA);
        assertEquals(1490439138,epochTime);
    }

    @Test
    public void testDateUtil_WithDifferentPattern()  {
        long epochTime = DateUtils.toEpochTime("2001-07-04T12:08:56",DATE_PATTERN_YEAR_HOURS);
        assertEquals(994248536,epochTime);
    }

}
