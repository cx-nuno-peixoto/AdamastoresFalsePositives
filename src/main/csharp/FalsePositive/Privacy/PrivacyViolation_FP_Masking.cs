using System;
using System.Web;
using System.Web.UI;
using System.Text;
using System.Security.Cryptography;

namespace Checkmarx.FalsePositive.Privacy
{
    /// <summary>
    /// FALSE POSITIVE SCENARIOS: Privacy Violation - Safe Masking Patterns (C#)
    /// 
    /// Pattern: PII is properly masked before output, revealing only partial information
    /// 
    /// These patterns show PII that is redacted, masked, or hashed before output.
    /// The actual sensitive data is not exposed. CxQL doesn't recognize these
    /// masking patterns as safe.
    /// 
    /// All scenarios are FALSE POSITIVES - SAFE masked output.
    /// </summary>
    public class PrivacyViolation_FP_Masking : Page
    {
        /// <summary>
        /// FALSE POSITIVE: SSN masked to last 4 digits
        /// </summary>
        protected void ShowMaskedSSN()
        {
            string ssn = Request.QueryString["ssn"];
            
            string masked = "XXX-XX-" + ssn.Substring(ssn.Length - 4);
            
            Response.Write("SSN: " + masked); // FALSE POSITIVE - Properly masked
        }
        
        /// <summary>
        /// FALSE POSITIVE: Credit card masked to last 4 digits
        /// </summary>
        protected void ShowMaskedCreditCard()
        {
            string creditCard = Request.QueryString["creditCard"];
            
            string last4 = creditCard.Substring(creditCard.Length - 4);
            string masked = "**** **** **** " + last4;
            
            Response.Write("Card: " + masked); // FALSE POSITIVE - Properly masked
        }
        
        /// <summary>
        /// FALSE POSITIVE: Phone number masked
        /// </summary>
        protected void ShowMaskedPhone()
        {
            string phone = Request.QueryString["phone"];
            
            string last4 = phone.Substring(phone.Length - 4);
            string masked = "(***) ***-" + last4;
            
            Response.Write("Phone: " + masked); // FALSE POSITIVE - Properly masked
        }
        
        /// <summary>
        /// FALSE POSITIVE: Email masked
        /// </summary>
        protected void ShowMaskedEmail()
        {
            string email = Request.QueryString["email"];
            
            int atIndex = email.IndexOf('@');
            string masked = email[0] + "***" + email.Substring(atIndex);
            
            Response.Write("Email: " + masked); // FALSE POSITIVE - Properly masked
        }
        
        /// <summary>
        /// FALSE POSITIVE: Password replaced with dots
        /// </summary>
        protected void ShowMaskedPassword()
        {
            string password = Request.QueryString["password"];
            
            string masked = "••••••••";
            
            Response.Write("Password: " + masked); // FALSE POSITIVE - Password never shown
        }
        
        /// <summary>
        /// FALSE POSITIVE: Account number hashed
        /// </summary>
        protected void ShowHashedAccount()
        {
            string accountNumber = Request.QueryString["accountNumber"];
            
            using (SHA256 sha256 = SHA256.Create())
            {
                byte[] hash = sha256.ComputeHash(Encoding.UTF8.GetBytes(accountNumber));
                string hashed = BitConverter.ToString(hash).Replace("-", "").ToLower();
                
                Response.Write("Account Hash: " + hashed); // FALSE POSITIVE - Irreversible hash
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Length-only disclosure
        /// </summary>
        protected void ShowLengthOnly()
        {
            string secretKey = Request.QueryString["secretKey"];
            
            int length = secretKey.Length;
            
            Response.Write("Secret key length: " + length); // FALSE POSITIVE - Only length shown
        }
        
        /// <summary>
        /// FALSE POSITIVE: Existence check only
        /// </summary>
        protected void ShowExistenceOnly()
        {
            string apiKey = Request.QueryString["apiKey"];
            
            bool hasKey = !string.IsNullOrEmpty(apiKey);
            
            Response.Write("API Key configured: " + hasKey); // FALSE POSITIVE - Boolean only
        }
        
        /// <summary>
        /// FALSE POSITIVE: Truncated value
        /// </summary>
        protected void ShowTruncatedValue()
        {
            string driversLicense = Request.QueryString["driversLicense"];
            
            string truncated = driversLicense.Length > 2 
                ? driversLicense.Substring(0, 2) + "..." 
                : "...";
            
            Response.Write("DL: " + truncated); // FALSE POSITIVE - Truncated
        }
    }
}

