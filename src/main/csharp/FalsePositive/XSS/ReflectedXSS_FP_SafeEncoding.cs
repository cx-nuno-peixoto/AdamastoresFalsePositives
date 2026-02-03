using System;
using System.Web;
using System.Web.UI;
using System.Text;
using System.Security.Cryptography;

namespace Checkmarx.FalsePositive.XSS
{
    /// <summary>
    /// FALSE POSITIVE SCENARIOS: Reflected XSS - Safe Encoding Transformations (C#)
    /// 
    /// Pattern: User input encoded/transformed in ways that prevent XSS
    /// 
    /// These encoding transformations produce output that cannot execute as XSS:
    /// - Base64 encoding produces alphanumeric + /+= only
    /// - URL encoding escapes special characters
    /// - Numeric parsing produces only numbers
    /// 
    /// All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
    /// </summary>
    public class ReflectedXSS_FP_SafeEncoding : Page
    {
        /// <summary>
        /// FALSE POSITIVE: Base64 encoding
        /// Base64 output contains only A-Za-z0-9+/= (safe characters)
        /// </summary>
        protected void ShowBase64Encoded()
        {
            string userInput = Request.QueryString["data"];
            
            byte[] bytes = Encoding.UTF8.GetBytes(userInput);
            string encoded = Convert.ToBase64String(bytes);
            
            Response.Write("Encoded: " + encoded); // FALSE POSITIVE - Base64 safe
        }
        
        /// <summary>
        /// FALSE POSITIVE: URL encoding
        /// </summary>
        protected void ShowUrlEncoded()
        {
            string userInput = Request.QueryString["query"];
            
            string encoded = HttpUtility.UrlEncode(userInput);
            
            Response.Write("Query: " + encoded); // FALSE POSITIVE - URL encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: Hex encoding
        /// Hex output is only 0-9, A-F characters
        /// </summary>
        protected void ShowHexEncoded()
        {
            string userInput = Request.QueryString["value"];
            
            byte[] bytes = Encoding.UTF8.GetBytes(userInput);
            string hex = BitConverter.ToString(bytes).Replace("-", "");
            
            Response.Write("Hex: " + hex); // FALSE POSITIVE - Hex safe
        }
        
        /// <summary>
        /// FALSE POSITIVE: int.Parse() - output is numeric
        /// </summary>
        protected void ShowParsedInt()
        {
            string userInput = Request.QueryString["id"];
            
            if (int.TryParse(userInput, out int id))
            {
                Response.Write("ID: " + id); // FALSE POSITIVE - Numeric only
            }
            else
            {
                Response.Write("Invalid ID");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: long.Parse() - output is numeric
        /// </summary>
        protected void ShowParsedLong()
        {
            string userInput = Request.QueryString["accountId"];
            
            if (long.TryParse(userInput, out long accountId))
            {
                Response.Write("Account: " + accountId); // FALSE POSITIVE - Numeric only
            }
            else
            {
                Response.Write("Invalid account");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: decimal.Parse() - output is numeric
        /// </summary>
        protected void ShowParsedDecimal()
        {
            string userInput = Request.QueryString["price"];
            
            if (decimal.TryParse(userInput, out decimal price))
            {
                Response.Write("Price: $" + price); // FALSE POSITIVE - Numeric only
            }
            else
            {
                Response.Write("Invalid price");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: SHA256 hash - output is hex only
        /// </summary>
        protected void ShowHashDigest()
        {
            string userInput = Request.QueryString["value"];
            
            using (SHA256 sha256 = SHA256.Create())
            {
                byte[] hash = sha256.ComputeHash(Encoding.UTF8.GetBytes(userInput));
                string hexHash = BitConverter.ToString(hash).Replace("-", "").ToLower();
                
                Response.Write("Hash: " + hexHash); // FALSE POSITIVE - Hex only
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: GUID parsing - output is GUID format only
        /// </summary>
        protected void ShowParsedGuid()
        {
            string userInput = Request.QueryString["guid"];
            
            if (Guid.TryParse(userInput, out Guid guid))
            {
                Response.Write("GUID: " + guid); // FALSE POSITIVE - GUID format only
            }
            else
            {
                Response.Write("Invalid GUID");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Combined safe transformations
        /// </summary>
        protected void ShowCombinedSafe()
        {
            string userInput = Request.QueryString["data"];
            
            // Base64 then URL encode
            string base64 = Convert.ToBase64String(Encoding.UTF8.GetBytes(userInput));
            string urlEncoded = HttpUtility.UrlEncode(base64);
            
            Response.Write("Safe: " + urlEncoded); // FALSE POSITIVE - Double encoded
        }
    }
}

