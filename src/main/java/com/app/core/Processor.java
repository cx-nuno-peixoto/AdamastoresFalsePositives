package com.app.core;

import com.app.model.Entity;
import com.app.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Processor {
    
    public static <T, R> R process(T input, Function<T, R> transformer) {
        return transformer.apply(input);
    }
    
    public static long extractEntityId(Entity entity) {
        return entity != null ? entity.getId() : 0L;
    }
    
    public static int extractEntityStatus(Entity entity) {
        return entity != null ? entity.getStatus() : -1;
    }
    
    public static boolean extractEntityActive(Entity entity) {
        return entity != null && entity.isActive();
    }
    
    public static double extractEntityBalance(Entity entity) {
        return entity != null ? entity.getBalance() : 0.0;
    }
    
    public static long extractAccountId(Account account) {
        return account != null ? account.getAccountId() : 0L;
    }
    
    public static int extractAccountTier(Account account) {
        return account != null ? account.getTier() : 0;
    }
    
    public static boolean extractAccountVerified(Account account) {
        return account != null && account.isVerified();
    }
    
    public static String extractMaskedSsn(Account account) {
        return account != null ? account.getMaskedSsn() : "***-**-****";
    }
    
    public static <T> T unwrap(Optional<T> optional) {
        return optional.orElse(null);
    }
    
    public static <T> T firstOrNull(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
    
    public static long getIdFromFirst(List<Entity> entities) {
        Entity first = firstOrNull(entities);
        return extractEntityId(first);
    }
    
    public static int getStatusFromFirst(List<Entity> entities) {
        Entity first = firstOrNull(entities);
        return extractEntityStatus(first);
    }
}

