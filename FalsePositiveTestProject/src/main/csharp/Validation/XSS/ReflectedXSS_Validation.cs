using System;
using System.Web;
using System.Web.UI;

namespace Checkmarx.Validation.XSS
{
    /// <summary>
    /// VALIDATION FILE: Reflected XSS - Good vs Bad Findings (C#)
    /// 
    /// This file contains BOTH:
    /// - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
    /// - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
    /// </summary>
    public class ReflectedXSS_Validation : Page
    {
        // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
        
        // BAD: Type conversion to int
        protected void BadIntParse()
        {
            string ageParam = Request.QueryString["age"];
            int age = int.Parse(ageParam); // SAFE: Converts to int
            Response.Write("Age: " + age); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Type conversion to long
        protected void BadLongParse()
        {
            string idParam = Request.QueryString["id"];
            long id = long.Parse(idParam); // SAFE: Converts to long
            Response.Write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Boolean conversion
        protected void BadBoolParse()
        {
            string flagParam = Request.QueryString["flag"];
            bool flag = bool.Parse(flagParam); // SAFE: Only "true" or "false"
            Response.Write("Flag: " + flag); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Guid validation - SAFE because GUID format is strict
        protected void BadGuidValidation()
        {
            string guidParam = Request.QueryString["guid"];
            if (Guid.TryParse(guidParam, out Guid guid))
            {
                Response.Write("GUID: " + guid.ToString()); // FALSE POSITIVE (BAD) - GUID format only
            }
        }
        
        // BAD: Enum validation
        protected void BadEnumValidation()
        {
            string roleParam = Request.QueryString["role"];
            if (Enum.TryParse<UserRole>(roleParam, true, out UserRole role))
            {
                Response.Write("Role: " + role.ToString()); // FALSE POSITIVE (BAD) - Enum value only
            }
        }
        
        // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
        
        // GOOD: Direct output without validation
        protected void GoodDirectOutput()
        {
            string name = Request.QueryString["name"]; // VULNERABLE: No validation
            Response.Write("Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: Form data without sanitization
        protected void GoodFormData()
        {
            string comment = Request.Form["comment"]; // VULNERABLE: No sanitization
            Response.Write("Comment: " + comment); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: HTML attribute without encoding
        protected void GoodHtmlAttribute()
        {
            string title = Request.QueryString["title"]; // VULNERABLE: No encoding
            Response.Write("<div title='" + title + "'>Content</div>"); // TRUE POSITIVE (GOOD)
        }
        
        // GOOD: JavaScript context without encoding
        protected void GoodJavaScript()
        {
            string userName = Request.QueryString["userName"]; // VULNERABLE: No encoding
            Response.Write("<script>var user = '" + userName + "';</script>"); // TRUE POSITIVE (GOOD)
        }
        
        // GOOD: Cookie value in output
        protected void GoodCookieReflection()
        {
            HttpCookie cookie = Request.Cookies["userPref"];
            if (cookie != null)
            {
                Response.Write("Preference: " + cookie.Value); // TRUE POSITIVE (GOOD) - Cookie value
            }
        }
        
        // GOOD: Header value reflected
        protected void GoodHeaderReflection()
        {
            string referer = Request.Headers["Referer"]; // VULNERABLE: HTTP header
            Response.Write("Came from: " + referer); // TRUE POSITIVE (GOOD) - Header value
        }
        
        // GOOD: Path info reflected
        protected void GoodPathInfoReflection()
        {
            string pathInfo = Request.PathInfo; // VULNERABLE: URL path
            Response.Write("Path: " + pathInfo); // TRUE POSITIVE (GOOD) - Path info
        }
        
        // MIXED: Bad/FP numeric + Good/TP string
        protected void MixedGoodAndBad()
        {
            string idParam = Request.QueryString["id"];
            long id = long.Parse(idParam); // SAFE: Numeric conversion
            Response.Write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged
            
            string name = Request.QueryString["name"]; // VULNERABLE: No validation
            Response.Write(", Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // Helper enum
        public enum UserRole { Admin, User, Guest }
    }
}

