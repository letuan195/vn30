package com.algo.vn30.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateTimeUtil {

    public static String[] json2JavaFormats = new String[]{"yyyy-MM-dd",
            "dd/MM/yyyy", "MM/dd/yyyy", "HH:mm:ss", "hh:mm:ss tt", "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"};
    private static String persistenFormat = "yyyyMMddHHmmss";
    private static String javaToStringFormat = "EEE MMM dd HH:mm:ss 'ICT' yyyy";
    private static String jsonToJavaFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String jsonValueToJavaFormat = "yyyy-MM-dd HH:mm:ss";

    private static String msgHeaderFormat = "dd-MMM-yyyy HH:mm:ss";
    private static String msgDateOnly = "dd/MM/yyyy";
    private static String msgDateTime = "dd/MM/yyyy hh:mm:ss a";

    private static String zipFileFormat = "ddMMyyyy";
    private static String historicalFormat = "yyyyMMdd";
    private static String csvFormat = "dd.MM.yyyy";

    public static SimpleDateFormat getPersistenFormat() {
        return new SimpleDateFormat(persistenFormat);
    }

    public static SimpleDateFormat getJavaToStringFormat() {
        return new SimpleDateFormat(javaToStringFormat);
    }

    public static SimpleDateFormat getJsonToJavaFormat() {
        return new SimpleDateFormat(jsonToJavaFormat);
    }

    public static SimpleDateFormat getJsonValueToJavaFormat() {
        return new SimpleDateFormat(jsonValueToJavaFormat);
    }

    public static SimpleDateFormat getDefaultDateFormat() {
        return LanguageUtil.getDateFormat();
    }

    public static SimpleDateFormat getDefaultTimeFormat() {
        return LanguageUtil.getTimeFormat();
    }

    public static SimpleDateFormat getDefaultDateTimeFormat() {
        return LanguageUtil.getDateTimeFormat();
    }

    public static SimpleDateFormat getVNTickDateFormat() {
        return new SimpleDateFormat(msgDateOnly);
    }

    public static SimpleDateFormat getHistoricalFormat() {
        return new SimpleDateFormat(historicalFormat);
    }

    public static SimpleDateFormat getZipFileFormat() {
        return new SimpleDateFormat(zipFileFormat);
    }

    public static SimpleDateFormat getCsvFormat() {
        return new SimpleDateFormat(csvFormat);
    }

    public static String formatString(Date date) {
        String rs = "";
        rs = getDefaultDateFormat().format(date);
        return rs;
    }

    public static Date parseDate(String dateString) {
        Date date = null;
        try {
            date = getDefaultDateFormat().parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    public static Date parseVnDate(String dateString) {
        Date date = null;
        try {
            date = getVNTickDateFormat().parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    public static Date getDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String parseVNDate(String dateString) {
        Date date = null;
        try {
            if (!CompareUtil.isEqualsNullOrEmpty(dateString)) {
                long miliseconds = Long.parseLong(dateString);
                date = new Date(miliseconds);
                return getJsonValueToJavaFormat().format(date);
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static String formatOriginString(Date date) {
        String rs = "";
        rs = getJavaToStringFormat().format(date);
        return rs;
    }

    public static Date parseOriginDate(String dateString) {
        Date date = null;
        try {
            date = getJavaToStringFormat().parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    public static String formatMSGHeaderDateString(Date date) {
        if (CompareUtil.isEqualsNull(date)) return "";
        SimpleDateFormat fomater = new SimpleDateFormat(msgHeaderFormat);
        String rs = fomater.format(date);
        return rs;
    }

    public static String formatMSGDateString(Date date) {
        if (CompareUtil.isEqualsNull(date)) return "";
        SimpleDateFormat fomater = new SimpleDateFormat(msgDateOnly);
        String rs = fomater.format(date);
        return rs;
    }

    public static String formatMSGDateTimeString(Date date) {
        if (CompareUtil.isEqualsNull(date)) return "";
        SimpleDateFormat formater = new SimpleDateFormat(msgDateTime);
        String rs = formater.format(date);
        return rs;
    }

    public static Date tryCastJsonStringToDate(String inDate) {

        if (inDate == null)
            return null;

        SimpleDateFormat dateFormat = getJsonToJavaFormat();
        dateFormat.setLenient(false);
        try {
            // parse the inDate parameter
            Date utcTime = dateFormat.parse(inDate.trim());
            Date localTime = new Date(utcTime.getTime() + TimeZone.getDefault().getOffset(new Date().getTime()));
            return localTime;
        } catch (ParseException pe) {
            return null;
        }
    }

    public static Date tryCastJsonStringToDate(String inDate, String format) {

        if (inDate == null)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            // parse the inDate parameter
            Date utcTime = dateFormat.parse(inDate.trim());
            Date localTime = new Date(utcTime.getTime() + TimeZone.getDefault().getOffset(new Date().getTime()));
            return localTime;
        } catch (ParseException pe) {
            return null;
        }
    }

    public static Date tryCastJsonValueToDate(String inDate) {

        if (inDate == null)
            return null;

        SimpleDateFormat dateFormat = getJsonValueToJavaFormat();
        dateFormat.setLenient(false);
        try {
            // parse the inDate parameter
            Date date = dateFormat.parse(inDate.trim());
            return date;
        } catch (ParseException pe) {
            return null;
        }
    }

    public static long dayBetweenDates(Date d1, Date d2) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(d1);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(d2);

        int days = 0;
        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            return 0;
        }
        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            ++days;
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date
        return days;
    }

    public static int compareTo(Date d1, Date d2) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(d1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(d2);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        Date val1 = startCal.getTime();
        Date val2 = endCal.getTime();

        return val1.compareTo(val2);
    }

    public static Date addDays(Date date, int days, List<Date> allHolidays) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        startCal.add(Calendar.DATE, days);
        //kiem tra neu target la ngay nghi thi tinh tien 1 ngay
        boolean isHoliday = true;
        while (isHoliday) {
            if (allHolidays.contains(startCal.getTime())) {
                startCal.add(Calendar.DAY_OF_MONTH, 1);
            } else isHoliday = false;
        }
        return startCal.getTime();
    }

    public static Date addBusDays(Date date, int days, List<Date> allHolidays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < days; ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (!allHolidays.contains(calendar.getTime())) {
                i++;
            }
        }
        return calendar.getTime();
    }

    public static Date addWorkingDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < Math.abs(days); ) {
            if (days < 0)
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            else
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            int dow = calendar.get(Calendar.DAY_OF_WEEK);
            boolean isWeekday = ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY));
            if (isWeekday) {
                i++;
            }
        }
        return calendar.getTime();
    }

    public static void calculateHolidays(Date start, Date end, List<Date> allHolidates) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            allHolidates.add(startCal.getTime());
            return;
        }
        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            allHolidates.add(startCal.getTime());
            return;
        }
        allHolidates.add(startCal.getTime());
        while (startCal.getTimeInMillis() < endCal.getTimeInMillis()) {
            //increment start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            allHolidates.add(startCal.getTime());
        }
    }

    public static Date parseHistoricalDate(String dateString) {
        Date date = null;
        try {
            date = getHistoricalFormat().parse(dateString);
        } catch (ParseException e) {
        }
        return date;
    }

    public static String formatHistoricalString(Date date) {
        String rs = "";
        rs = getHistoricalFormat().format(date);
        return rs;
    }

    public static String formatZipFileString(Date date) {
        String rs = "";
        rs = getZipFileFormat().format(date);
        return rs;
    }

    public static String formatCsvString(Date date) {
        String rs = "";
        rs = getCsvFormat().format(date);
        return rs;
    }

    public static Date getPreviousDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date toDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static Boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    public static int getYear(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getYear();
    }

    public static int getQuarter(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (localDate.getMonthValue() - 1) / 3 + 1;
    }

    public static int getMonth(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }
}