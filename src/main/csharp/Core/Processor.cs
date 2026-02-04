using System;
using System.Collections.Generic;
using App.Models;

namespace App.Core
{
    public static class Processor
    {
        public static R Process<T, R>(T input, Func<T, R> transformer)
        {
            return transformer(input);
        }
        
        public static long ExtractEntityId(Entity entity)
        {
            return entity?.Id ?? 0L;
        }
        
        public static int ExtractEntityStatus(Entity entity)
        {
            return entity?.Status ?? -1;
        }
        
        public static bool ExtractEntityActive(Entity entity)
        {
            return entity?.Active ?? false;
        }
        
        public static double ExtractEntityBalance(Entity entity)
        {
            return entity?.Balance ?? 0.0;
        }
        
        public static long ExtractAccountId(Account account)
        {
            return account?.AccountId ?? 0L;
        }
        
        public static int ExtractAccountTier(Account account)
        {
            return account?.Tier ?? 0;
        }
        
        public static bool ExtractAccountVerified(Account account)
        {
            return account?.Verified ?? false;
        }
        
        public static string ExtractMaskedSsn(Account account)
        {
            return account?.MaskedSsn ?? "***-**-****";
        }
        
        public static T FirstOrNull<T>(List<T> list) where T : class
        {
            return list != null && list.Count > 0 ? list[0] : null;
        }
        
        public static long GetIdFromFirst(List<Entity> entities)
        {
            var first = FirstOrNull(entities);
            return ExtractEntityId(first);
        }
        
        public static int GetStatusFromFirst(List<Entity> entities)
        {
            var first = FirstOrNull(entities);
            return ExtractEntityStatus(first);
        }
    }
}

