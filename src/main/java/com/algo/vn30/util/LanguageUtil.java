package com.algo.vn30.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LanguageUtil {

    private static Locale defaultLocale = new Locale("en", "US");

    public static SimpleDateFormat getDateFormat() {
        Locale locale = defaultLocale;
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return (SimpleDateFormat) formatter;
    }

    public static SimpleDateFormat getTimeFormat() {
        Locale locale = defaultLocale;
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        return (SimpleDateFormat) formatter;
    }

    public static SimpleDateFormat getDateTimeFormat() {
        Locale locale = defaultLocale;
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String pattern = ((SimpleDateFormat) formatter).toPattern();
        formatter = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
        pattern += " " + ((SimpleDateFormat) formatter).toPattern();
        return new SimpleDateFormat(pattern);
    }

    public static NumberFormat getNumberFormat() {
        Locale locale = defaultLocale;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMaximumFractionDigits(8);
        return numberFormat;
    }
}
