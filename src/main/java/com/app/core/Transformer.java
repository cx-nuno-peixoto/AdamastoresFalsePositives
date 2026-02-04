package com.app.core;

import java.util.List;
import java.util.stream.Collectors;

public class Transformer {
    
    public static int stringToInt(String input) {
        return Integer.parseInt(input);
    }
    
    public static long stringToLong(String input) {
        return Long.parseLong(input);
    }
    
    public static double stringToDouble(String input) {
        return Double.parseDouble(input);
    }
    
    public static boolean stringToBoolean(String input) {
        return Boolean.parseBoolean(input);
    }
    
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
    
    public static <T> T getLast(List<T> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }
    
    public static List<Long> extractIds(List<? extends HasId> items) {
        return items.stream().map(HasId::getId).collect(Collectors.toList());
    }
    
    public static List<Integer> extractStatuses(List<? extends HasStatus> items) {
        return items.stream().map(HasStatus::getStatus).collect(Collectors.toList());
    }
    
    public interface HasId {
        long getId();
    }
    
    public interface HasStatus {
        int getStatus();
    }
}

