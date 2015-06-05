package im.youtiao.android_client.util;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;


public class TimeWrap {
    /**
     * Convert a time to a formatted and readable date time string.
     *
     * @param millis
     * @param timeZone
     * @param locale
     * @return
     */
    public static String wrapDateTime(long millis, TimeZone timeZone, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(millis);
    }

    public static final String BUNDLE_NAME = TimeWrap.class.getCanonicalName() + "Bundle";
    public static final String SEVERAL_DAYS_AGO = "%d days ago";
    public static final String YESTERDAY = "yesterday";
    public static final String SEVERAL_HOURS_AGO = "%d hours ago";
    public static final String ONE_HOUR_AGO = "1 hour ago";
    public static final String SEVERAL_MINUTES_AGO = "%d minutes ago";
    public static final String ONE_MINUTE_AGO = "1 minute ago";
    public static final String A_FEW_SECONDS_AGO = "a few seconds ago";

    public static final String IN_A_FEW_SECONDS = "in a few seconds";
    public static final String IN_ONE_MINUTE = "in 1 minute";
    public static final String IN_SEVERAL_MINUTES = "in %d minutes";
    public static final String IN_ONE_HOUR = "in 1 hour";
    public static final String IN_SEVERAL_HOURS = "in %d hours";
    public static final String TOMORROW = "tomorrow";
    public static final String IN_SEVELRAL_DAYS = "in %d days";

    public static final String[] A_FEW_SECONDS = { A_FEW_SECONDS_AGO, IN_A_FEW_SECONDS };
    public static final String[] ONE_MINUTE = { ONE_MINUTE_AGO, IN_ONE_MINUTE };
    public static final String[] SEVERAL_MINUTES = { SEVERAL_MINUTES_AGO, IN_SEVERAL_MINUTES };
    public static final String[] ONE_HOUR = { ONE_HOUR_AGO, IN_ONE_HOUR };
    public static final String[] SEVERAL_HOURS = { SEVERAL_HOURS_AGO, IN_SEVERAL_HOURS };
    public static final String[] ONE_DAY = { YESTERDAY, TOMORROW };
    public static final String[] SEVERAL_DAYS = { SEVERAL_DAYS_AGO, IN_SEVELRAL_DAYS };

    public static final Locale DEFAULT_LOCALE = new Locale("en", "US");
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    public static String wrapTimeDisplyValue(long millis) {
        return wrapTimeDisplyValue(millis, DEFAULT_TIME_ZONE, DEFAULT_LOCALE);
    }

    /**
     * Convert a time to an easy-to-read date time string. For example,
     * something like "1 hour ago".
     *
     * @param millis
     * @param timeZone
     * @param locale
     * @return
     */
    public static String wrapTimeDisplyValue(long millis, TimeZone timeZone, Locale locale) {
        long timeSpan = System.currentTimeMillis() - millis;
        int idx = 0;
        if (timeSpan < 0) {
            idx = 1;
        }
        timeSpan = Math.abs(timeSpan);

        long seconds = timeSpan / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        try {
            ResourceBundle strings = null;
            try {
                strings = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            } catch (Exception ex) {
                strings = ResourceBundle.getBundle(BUNDLE_NAME);
            }

            if (years > 0) {
                return wrapDateTime(millis, timeZone, locale);
            }

            if (months > 0) {
                return wrapDateTime(millis, timeZone, locale);
            }

            if (days > 1) {
                return String.format(strings.getString(SEVERAL_DAYS[idx]), days);
            } else if (days == 1) {
                return strings.getString(ONE_DAY[idx]);
            }

            if (hours > 1) {
                return String.format(strings.getString(SEVERAL_HOURS[idx]), hours);
            } else if (hours == 1) {
                return strings.getString(ONE_HOUR[idx]);
            }

            if (minutes > 1) {
                return String.format(strings.getString(SEVERAL_MINUTES[idx]), minutes);
            } else if (minutes == 1) {
                return strings.getString(ONE_MINUTE[idx]);
            }

            return strings.getString(A_FEW_SECONDS[idx]);
        } catch (Exception e) {
            return wrapDateTime(millis, timeZone, locale);
        }
    }
}

