using System;
using System.Web;
using System.Web.UI;
using System.Collections.Generic;
using System.Linq;

namespace Checkmarx.FalsePositive.XSS
{
    /// <summary>
    /// FALSE POSITIVE SCENARIOS: Reflected XSS - Whitelist Validation (C#)
    /// 
    /// Pattern: User input validated against a fixed whitelist of allowed values
    /// 
    /// Whitelist validation ensures only known-safe values are used.
    /// CxQL doesn't recognize these patterns as sanitization.
    /// 
    /// All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
    /// </summary>
    public class ReflectedXSS_FP_WhitelistValidation : Page
    {
        private static readonly HashSet<string> AllowedCategories = new HashSet<string>
        {
            "electronics", "books", "clothing", "food", "toys"
        };
        
        private static readonly List<string> AllowedColors = new List<string>
        {
            "red", "green", "blue", "yellow", "orange", "purple"
        };
        
        private static readonly string[] AllowedSizes = { "XS", "S", "M", "L", "XL", "XXL" };
        
        /// <summary>
        /// FALSE POSITIVE: HashSet.Contains() whitelist check
        /// </summary>
        protected void ShowHashSetWhitelist()
        {
            string category = Request.QueryString["category"];
            
            if (AllowedCategories.Contains(category))
            {
                Response.Write("Category: " + category); // FALSE POSITIVE - Whitelisted
            }
            else
            {
                Response.Write("Invalid category");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: List.Contains() whitelist check
        /// </summary>
        protected void ShowListWhitelist()
        {
            string color = Request.QueryString["color"];
            
            if (AllowedColors.Contains(color))
            {
                Response.Write("Color: " + color); // FALSE POSITIVE - Whitelisted
            }
            else
            {
                Response.Write("Invalid color");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Array.Contains() whitelist check
        /// </summary>
        protected void ShowArrayWhitelist()
        {
            string size = Request.QueryString["size"];
            
            if (AllowedSizes.Contains(size))
            {
                Response.Write("Size: " + size); // FALSE POSITIVE - Whitelisted
            }
            else
            {
                Response.Write("Invalid size");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Switch statement whitelist
        /// </summary>
        protected void ShowSwitchWhitelist()
        {
            string status = Request.QueryString["status"];
            
            switch (status)
            {
                case "pending":
                case "approved":
                case "rejected":
                case "completed":
                    Response.Write("Status: " + status); // FALSE POSITIVE - Switch whitelist
                    break;
                default:
                    Response.Write("Invalid status");
                    break;
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Enum.TryParse() validation
        /// </summary>
        protected void ShowEnumWhitelist()
        {
            string priority = Request.QueryString["priority"];
            
            if (Enum.TryParse<Priority>(priority, true, out Priority p))
            {
                Response.Write("Priority: " + p.ToString()); // FALSE POSITIVE - Valid enum
            }
            else
            {
                Response.Write("Invalid priority");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: LINQ Any() whitelist
        /// </summary>
        protected void ShowLinqWhitelist()
        {
            string type = Request.QueryString["type"];
            
            if (AllowedCategories.Any(a => a.Equals(type, StringComparison.OrdinalIgnoreCase)))
            {
                Response.Write("Type: " + type); // FALSE POSITIVE - LINQ filtered
            }
            else
            {
                Response.Write("Invalid type");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Explicit string comparison
        /// </summary>
        protected void ShowExplicitWhitelist()
        {
            string action = Request.QueryString["action"];
            
            if (action == "view" || action == "edit" || action == "delete")
            {
                Response.Write("Action: " + action); // FALSE POSITIVE - Explicit whitelist
            }
            else
            {
                Response.Write("Invalid action");
            }
        }
        
        private enum Priority
        {
            Low,
            Medium,
            High,
            Critical
        }
    }
}

