package com.martenls.qasystem.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class Utils {

    public static String calendarToXsdDate(Calendar time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return "\"" + formatter.format(time.getTime()) + "\"^^<http://www.w3.org/2001/XMLSchema#date>";
    }

    public static String calendarToXsdDateTime(Calendar time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return "\"" + formatter.format(time.getTime()) + "\"^^<http://www.w3.org/2001/XMLSchema#dateTime>";
    }

    public static <T> void addIfNotPresent(List<T> list, T item) {
        if (!list.contains(item)) {
            list.add(item);
        }
    }

    public static <T> void addAllIfNotPresent(List<T> list, List<T> listToAdd) {
        for (T item : listToAdd) {
            addIfNotPresent(list, item);
        }
    }
}
