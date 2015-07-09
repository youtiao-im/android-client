package im.youtiao.android_client.util;
import android.content.Context;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import im.youtiao.android_client.R;


public class TimeWrap {
    /**
     * Convert a time to a formatted and readable date time string.
     *
     * @param millis
     * @return
     */
    public static String wrapDateTime(long millis) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return dateFormat.format(millis);
    }

//    public static final String SEVERAL_DAYS_AGO = "%d days ago";
//    public static final String YESTERDAY = "yesterday";
//    public static final String SEVERAL_HOURS_AGO = "%d hours ago";
//    public static final String ONE_HOUR_AGO = "1 hour ago";
//    public static final String SEVERAL_MINUTES_AGO = "%d minutes ago";
//    public static final String ONE_MINUTE_AGO = "1 minute ago";
//    public static final String A_FEW_SECONDS_AGO = "a few seconds ago";
//
//    public static final String IN_A_FEW_SECONDS = "in a few seconds";
//    public static final String IN_ONE_MINUTE = "in 1 minute";
//    public static final String IN_SEVERAL_MINUTES = "in %d minutes";
//    public static final String IN_ONE_HOUR = "in 1 hour";
//    public static final String IN_SEVERAL_HOURS = "in %d hours";
//    public static final String TOMORROW = "tomorrow";
//    public static final String IN_SEVELRAL_DAYS = "in %d days";

//    public static final String[] A_FEW_SECONDS = { A_FEW_SECONDS_AGO, IN_A_FEW_SECONDS };
//    public static final String[] ONE_MINUTE = { ONE_MINUTE_AGO, IN_ONE_MINUTE };
//    public static final String[] SEVERAL_MINUTES = { SEVERAL_MINUTES_AGO, IN_SEVERAL_MINUTES };
//    public static final String[] ONE_HOUR = { ONE_HOUR_AGO, IN_ONE_HOUR };
//    public static final String[] SEVERAL_HOURS = { SEVERAL_HOURS_AGO, IN_SEVERAL_HOURS };
//    public static final String[] ONE_DAY = { YESTERDAY, TOMORROW };
//    public static final String[] SEVERAL_DAYS = { SEVERAL_DAYS_AGO, IN_SEVELRAL_DAYS };



    /**
     * Convert a time to an easy-to-read date time string. For example,
     * something like "1 hour ago".
     *
     * @param millis
     * @return
     */
    public static String wrapTimeDisplyValue(long millis, Context context) {
        long timeSpan = System.currentTimeMillis() - millis;
        int idx = 0;
        if (timeSpan < 0) {
            idx = 1;
        }
        timeSpan = Math.abs(timeSpan);

        String[] A_FEW_SECONDS = { context.getString(R.string.a_few_second_ago), context.getString(R.string.in_a_few_second) };
        String[] ONE_MINUTE = { context.getString(R.string.one_minute_ago), context.getString(R.string.in_one_minute) };
        String[] SEVERAL_MINUTES = { context.getString(R.string.several_minutes_ago), context.getString(R.string.in_several_minutes) };
        String[] ONE_HOUR = { context.getString(R.string.one_hour_ago), context.getString(R.string.in_one_hour) };
        String[] SEVERAL_HOURS = { context.getString(R.string.several_hours_ago), context.getString(R.string.in_several_days) };
        String[] ONE_DAY = { context.getString(R.string.yesterday), context.getString(R.string.tomorrow) };
        String[] SEVERAL_DAYS = { context.getString(R.string.several_days_ago), context.getString(R.string.in_several_days) };


        long seconds = timeSpan / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 30;
        long years = days / 365;

        try {
            if (years > 0) {
                return wrapDateTime(millis);
            }

            if (months > 0) {
                return wrapDateTime(millis);
            }

            if (days > 1) {
                return String.format(SEVERAL_DAYS[idx], days);
            } else if (days == 1) {
                return ONE_DAY[idx];
            }

            if (hours > 1) {
                return String.format(SEVERAL_HOURS[idx], hours);
            } else if (hours == 1) {
                return ONE_HOUR[idx];
            }

            if (minutes > 1) {
                return String.format(SEVERAL_MINUTES[idx], minutes);
            } else if (minutes == 1) {
                return ONE_MINUTE[idx];
            }

            return A_FEW_SECONDS[idx];
        } catch (Exception e) {
            return wrapDateTime(millis);
        }
    }
}

