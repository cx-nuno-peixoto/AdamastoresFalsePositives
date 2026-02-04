using System;
using System.Collections.Generic;
using System.Linq;

namespace App.Core
{
    public static class Transformer
    {
        public static int StringToInt(string input) => int.Parse(input);
        
        public static long StringToLong(string input) => long.Parse(input);
        
        public static double StringToDouble(string input) => double.Parse(input);
        
        public static bool StringToBool(string input) => bool.Parse(input);
        
        public static T GetFirst<T>(List<T> list) where T : class
        {
            return list.Count == 0 ? null : list[0];
        }
        
        public static T GetLast<T>(List<T> list) where T : class
        {
            return list.Count == 0 ? null : list[list.Count - 1];
        }
        
        public static List<long> ExtractIds<T>(List<T> items) where T : IHasId
        {
            return items.Select(x => x.Id).ToList();
        }
        
        public static List<int> ExtractStatuses<T>(List<T> items) where T : IHasStatus
        {
            return items.Select(x => x.Status).ToList();
        }
    }
    
    public interface IHasId
    {
        long Id { get; }
    }
    
    public interface IHasStatus
    {
        int Status { get; }
    }
}

