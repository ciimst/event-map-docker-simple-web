package com.imst.event.map.web.security.ldap;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class Validate {

    public static boolean isNullOrEmpty(Object object){

        return object == null || object.toString().isEmpty();
    }

    public static boolean isNullOrEmpty(Object[] objects){

        return objects == null || objects.length == 0;
    }

    public static boolean isAnyNullOrEmpty(Object...objects){
        for (Object object : objects) {
            if (object == null || object.toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllNullOrEmpty(Object...objects){
        boolean result = true;
        for (Object object : objects) {
            result &= object == null || object.toString().isEmpty();
        }
        return result;
    }

    public static boolean isAllNullOrNegative(Integer...objects){
        boolean result = true;
        for (Integer number : objects) {
            result &= number == null || number < 0;
        }
        return result;
    }
    public static boolean isAnyNullOrNegative(Integer...objects){
        for (Integer number : objects) {
            if (number == null || number < 0) {
                return true;
            }
        }
        return false;
    }

    public static Object getValidValue(Object object, @NotNull Object defaultValue) {

        return isNullOrEmpty(object) ? defaultValue : object;
    }

    public static boolean isNullOrEmpty(List<?> list) {

        return list == null || list.isEmpty();
    }

    public static boolean isNullOrEmpty(Set<?> list) {

        return list == null || list.isEmpty();
    }

    public static boolean isNullOrZero(Integer number){

        return number == null || number == 0;
    }

    public static boolean isNullOrPositive(Integer number){

        return number == null || number > 0;
    }

    public static boolean isNullOrNegative(Integer number){

        return number == null || number < 0;
    }

    public static boolean isNullOrZero(Long number){

        return number == null || number == 0;
    }

    public static boolean isNullOrZeroOrNegative(Long number){

        return number == null || number == 0 || number < 0;
    }

    public static boolean isNullOrZeroOrNegative(Integer number){

        return number == null || number == 0 || number < 0;
    }

    public static String emptySolrSpecialChars(String str){

        str = (str == null)? "": str.trim();
        //+ - && || ! ( ) { } [ ] ^ " ~ * ? : \
        String[] escapes = {"\\", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", ":", "/"};
        for (String escape : escapes) {

            str = StringUtils.replace(str, escape, " ");
        }
//        str = StringUtils.replaceEach(str, escapes, new String[]{ " " });
        return str;
    }

    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

}
