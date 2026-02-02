using System;
using System.Web;
using System.Web.UI;
using System.Text;

namespace Checkmarx.Validation.Privacy
{
    /// <summary>
    /// VALIDATION FILE: Privacy Violation - Good vs Bad Findings (C#)
    /// 
    /// This file contains BOTH:
    /// - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
    /// - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
    /// </summary>
    public class PrivacyViolation_Validation : Page
    {
        // Constants for testing
        private const string PASSWORD = "Password";
        private const string SSN = "SSN";
        
        // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
        
        // BAD: Constant as parameter name (not actual value)
        protected void BadConstantAsKey()
        {
            string password = Request.QueryString[PASSWORD]; // PASSWORD is "Password" constant
            Response.Write("Field: " + PASSWORD); // FALSE POSITIVE (BAD) - Outputs "Password", not value
        }
        
        // BAD: Metadata variable (format pattern)
        protected void BadMetadataFormat()
        {
            string passwordFormat = "Min 8 characters"; // SAFE: Format description
            Response.Write("Password requirements: " + passwordFormat); // FALSE POSITIVE (BAD)
        }
        
        // BAD: StringBuilder.Append() without output
        protected void BadStringBuilderNoOutput()
        {
            string password = Request.QueryString["password"];
            StringBuilder sb = new StringBuilder();
            sb.Append("Password: ");
            sb.Append(password); // FALSE POSITIVE (BAD) - Just building string, no output
            // No output to response
        }
        
        // BAD: Account ID as numeric
        protected void BadAccountId()
        {
            string accountIdParam = Request.QueryString["accountId"];
            long accountId = long.Parse(accountIdParam); // SAFE: Numeric ID
            Response.Write("Account ID: " + accountId); // FALSE POSITIVE (BAD)
        }
        
        // BAD: Masked/redacted value
        protected void BadMaskedValue()
        {
            string ssn = Request.QueryString["ssn"];
            string masked = "XXX-XX-" + ssn.Substring(ssn.Length - 4); // SAFE: Only last 4 digits
            Response.Write("SSN: " + masked); // FALSE POSITIVE (BAD) - Properly masked
        }
        
        // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
        
        // GOOD: Actual password sent to response
        protected void GoodPasswordOutput()
        {
            string password = Request.QueryString["password"]; // VULNERABLE: Actual password
            Response.Write("Your password: " + password); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: SSN sent to response
        protected void GoodSSNOutput()
        {
            string ssn = Request.QueryString["ssn"]; // VULNERABLE: Actual SSN
            Response.Write("SSN: " + ssn); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: Credit card sent to response
        protected void GoodCreditCardOutput()
        {
            string creditCard = Request.QueryString["creditCard"]; // VULNERABLE
            Response.Write("Credit Card: " + creditCard); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: Password logged to console
        protected void GoodPasswordLogging()
        {
            string password = Request.QueryString["password"]; // VULNERABLE
            Console.WriteLine("User password: " + password); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: Auth token sent to response
        protected void GoodAuthTokenOutput()
        {
            string authToken = Request.QueryString["authToken"]; // VULNERABLE
            Response.Write("Token: " + authToken); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: StringBuilder WITH output to response
        protected void GoodStringBuilderWithOutput()
        {
            string password = Request.QueryString["password"];
            StringBuilder sb = new StringBuilder();
            sb.Append("Password: ");
            sb.Append(password);
            Response.Write(sb.ToString()); // TRUE POSITIVE (GOOD) - Password exposed
        }
        
        // GOOD: PII in exception message
        protected void GoodExceptionWithPII()
        {
            string email = Request.QueryString["email"];
            try
            {
                throw new Exception("Failed for user: " + email);
            }
            catch (Exception e)
            {
                Response.Write("Error: " + e.Message); // TRUE POSITIVE (GOOD) - PII in exception
            }
        }
        
        // MIXED: Bad/FP metadata + Good/TP actual PII
        protected void MixedGoodAndBad()
        {
            // BAD/FP: Metadata
            string passwordFormat = "Min 8 characters";
            Response.Write("Requirements: " + passwordFormat); // FALSE POSITIVE (BAD)
            
            // GOOD/TP: Actual password
            string password = Request.QueryString["password"];
            Response.Write(", Your password: " + password); // TRUE POSITIVE (GOOD)
        }
    }
}

