using System;
using System.Web;
using System.Web.UI;
using System.Collections.Generic;
using System.Data;

namespace Checkmarx.Validation.XSS
{
    /// <summary>
    /// VALIDATION FILE: Stored XSS - Good vs Bad Findings (C#)
    /// 
    /// This file contains BOTH:
    /// - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
    /// - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
    /// </summary>
    public class StoredXSS_Validation : Page
    {
        // Mock entity
        public class User
        {
            public long Id { get; set; }
            public string Name { get; set; }
            public string Email { get; set; }
            public int LoginCount { get; set; }
            public bool IsActive { get; set; }
        }
        
        // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
        
        // BAD: Numeric getter from database
        protected void BadNumericId(List<User> users)
        {
            long userId = users[0].Id; // SAFE: Id is long (numeric)
            Response.Write("User ID: " + userId); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Integer getter from database
        protected void BadLoginCount(List<User> users)
        {
            int count = users[0].LoginCount; // SAFE: LoginCount is int (numeric)
            Response.Write("Login count: " + count); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Boolean getter from database
        protected void BadBooleanField(List<User> users)
        {
            bool active = users[0].IsActive; // SAFE: IsActive is boolean
            Response.Write("Active: " + active); // FALSE POSITIVE (BAD) - Should NOT be flagged
        }
        
        // BAD: Numeric ID in loop
        protected void BadNumericLoop(List<User> users)
        {
            foreach (User user in users)
            {
                long id = user.Id; // SAFE: Id is long (numeric)
                Response.Write("<li>User ID: " + id + "</li>"); // FALSE POSITIVE (BAD)
            }
        }
        
        // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
        
        // GOOD: String field from database (user-generated content)
        protected void GoodUserName(List<User> users)
        {
            string name = users[0].Name; // VULNERABLE: Name is string (user input)
            Response.Write("User name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: Email field from database (user-generated content)
        protected void GoodUserEmail(List<User> users)
        {
            string email = users[0].Email; // VULNERABLE: Email is string (user input)
            Response.Write("Email: " + email); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
        
        // GOOD: String field in loop
        protected void GoodStringLoop(List<User> users)
        {
            foreach (User user in users)
            {
                string name = user.Name; // VULNERABLE: String from database
                Response.Write("<li>" + name + "</li>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged
            }
        }
        
        // GOOD: String field in HTML attribute
        protected void GoodHtmlAttribute(List<User> users)
        {
            string name = users[0].Name; // VULNERABLE: String from database
            Response.Write("<div title='" + name + "'>User</div>"); // TRUE POSITIVE (GOOD)
        }
        
        // GOOD: String field in JavaScript
        protected void GoodJavaScript(List<User> users)
        {
            string name = users[0].Name; // VULNERABLE: String from database
            Response.Write("<script>var userName = '" + name + "';</script>"); // TRUE POSITIVE (GOOD)
        }
        
        // GOOD: Session attribute - user-controlled stored data
        protected void GoodSessionData()
        {
            string userInput = Request.QueryString["input"];
            Session["storedInput"] = userInput; // Stored XSS
            
            // Later retrieval and output
            string stored = (string)Session["storedInput"];
            Response.Write("Stored: " + stored); // TRUE POSITIVE (GOOD) - String from session
        }
        
        // GOOD: Cookie data - user-controlled
        protected void GoodCookieData()
        {
            HttpCookie cookie = Request.Cookies["userPref"];
            if (cookie != null)
            {
                Response.Write("Preference: " + cookie.Value); // TRUE POSITIVE (GOOD) - Cookie value
            }
        }
        
        // MIXED: Numeric ID (BAD/FP) + String name (GOOD/TP)
        protected void MixedGoodAndBad(List<User> users)
        {
            User user = users[0];
            
            long id = user.Id; // SAFE: Numeric
            Response.Write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged
            
            string name = user.Name; // VULNERABLE: String
            Response.Write(", Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged
        }
    }
}

