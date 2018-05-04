package br.org.soujava.twitter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    /**
     * Func to transform a string to long using a pattern
     * @param timeString
     * @param pattern
     * @return
     */
    public static long toEpochTime(String timeString, String pattern) {

        long epochTime = -1;

        try {

            TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.US).parse(timeString);
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);

            epochTime = localDateTime.toEpochSecond(ZoneOffset.UTC);

        } catch (Exception e) {
            logger.error("Unable to convert into epoch time. Time: '{}', Pattern: '{}'", timeString, pattern, e);
        }

        return epochTime;
    }
}
