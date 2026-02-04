package com.app.util;

import java.util.regex.Pattern;

public class Sanitizer {
    
    private static final Pattern NUMERIC = Pattern.compile("^[0-9]+$");
    private static final Pattern ALPHANUMERIC = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    public static int toInt(String input) {
        return Integer.parseInt(input);
    }
    
    public static long toLong(String input) {
        return Long.parseLong(input);
    }
    
    public static double toDouble(String input) {
        return Double.parseDouble(input);
    }
    
    public static boolean isNumeric(String input) {
        return input != null && NUMERIC.matcher(input).matches();
    }
    
    public static boolean isAlphanumeric(String input) {
        return input != null && ALPHANUMERIC.matcher(input).matches();
    }
    
    public static boolean isValidEmail(String input) {
        return input != null && EMAIL.matcher(input).matches();
    }
    
    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#x27;");
    }
    
    public static String mask(String input, int visibleChars) {
        if (input == null || input.length() <= visibleChars) return "****";
        return "****" + input.substring(input.length() - visibleChars);
    }
    
    public static int bound(int value, int max) {
        return Math.min(Math.max(value, 0), max);
    }
    
    public static String extractNumeric(String input) {
        if (input == null) return "0";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
        }
        return sb.length() > 0 ? sb.toString() : "0";
    }
}

