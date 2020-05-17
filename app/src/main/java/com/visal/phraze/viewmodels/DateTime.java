package com.visal.phraze.viewmodels;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;


//class and method to get dates
public class DateTime {
    static String TAG = DateTime.class.getSimpleName();
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //method to calculate the number of minutes/hours/days have passed after adding the phrase
    public static String getDaysPassed(String dateTime){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime addedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long difference = ChronoUnit.DAYS.between(addedDate, today);
        if (difference == 0){
            if (ChronoUnit.HOURS.between(addedDate, today) == 0){
                return ChronoUnit.MINUTES.between(addedDate, today) + "m ago";
            }
            return ChronoUnit.HOURS.between(addedDate, today) + "h ago";
        }
        return (difference + "d ago");
    }
}
