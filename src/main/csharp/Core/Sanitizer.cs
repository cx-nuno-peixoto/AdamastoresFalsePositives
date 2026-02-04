using System;
using System.Text.RegularExpressions;
using System.Web;

namespace App.Core
{
    public static class Sanitizer
    {
        private static readonly Regex Numeric = new Regex(@"^[0-9]+$");
        private static readonly Regex Alphanumeric = new Regex(@"^[a-zA-Z0-9]+$");
        
        public static int ToInt(string input) => int.Parse(input);
        
        public static long ToLong(string input) => long.Parse(input);
        
        public static double ToDouble(string input) => double.Parse(input);
        
        public static bool IsNumeric(string input) => input != null && Numeric.IsMatch(input);
        
        public static bool IsAlphanumeric(string input) => input != null && Alphanumeric.IsMatch(input);
        
        public static string EscapeHtml(string input) => HttpUtility.HtmlEncode(input);
        
        public static string Mask(string input, int visibleChars)
        {
            if (string.IsNullOrEmpty(input) || input.Length <= visibleChars) return "****";
            return "****" + input.Substring(input.Length - visibleChars);
        }
        
        public static int Bound(int value, int max) => Math.Min(Math.Max(value, 0), max);
        
        public static string ExtractNumeric(string input)
        {
            if (string.IsNullOrEmpty(input)) return "0";
            var sb = new System.Text.StringBuilder();
            foreach (char c in input)
            {
                if (char.IsDigit(c)) sb.Append(c);
            }
            return sb.Length > 0 ? sb.ToString() : "0";
        }
    }
}

