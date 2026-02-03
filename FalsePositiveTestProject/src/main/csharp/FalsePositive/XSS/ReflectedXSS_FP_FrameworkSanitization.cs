using System;
using System.Web;
using System.Web.UI;
using System.Net;
using System.Text;

namespace Checkmarx.FalsePositive.XSS
{
    /// <summary>
    /// FALSE POSITIVE SCENARIOS: Reflected XSS - Framework Sanitization (C#)
    /// 
    /// Pattern: User input sanitized using .NET Framework encoding methods
    /// 
    /// These scenarios demonstrate that .NET encoding methods prevent XSS
    /// but CxQL doesn't recognize them as sanitizers.
    /// 
    /// All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
    /// </summary>
    public class ReflectedXSS_FP_FrameworkSanitization : Page
    {
        /// <summary>
        /// FALSE POSITIVE: HttpUtility.HtmlEncode()
        /// Standard .NET HTML encoding - escapes &lt;script&gt; etc.
        /// </summary>
        protected void ShowHtmlEncoded()
        {
            string userInput = Request.QueryString["name"];
            
            // .NET Framework encoder
            string safeOutput = HttpUtility.HtmlEncode(userInput);
            
            Response.Write("Welcome: " + safeOutput); // FALSE POSITIVE - Encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: WebUtility.HtmlEncode()
        /// .NET 4.0+ HTML encoding
        /// </summary>
        protected void ShowWebUtilityEncoded()
        {
            string userInput = Request.QueryString["comment"];
            
            string safeOutput = WebUtility.HtmlEncode(userInput);
            
            Response.Write("Comment: " + safeOutput); // FALSE POSITIVE - Encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: HttpUtility.UrlEncode()
        /// URL encoding escapes dangerous characters
        /// </summary>
        protected void ShowUrlEncoded()
        {
            string userInput = Request.QueryString["redirect"];
            
            string safeOutput = HttpUtility.UrlEncode(userInput);
            
            Response.Write("Redirect: " + safeOutput); // FALSE POSITIVE - URL encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: HttpUtility.JavaScriptStringEncode()
        /// JavaScript string encoding
        /// </summary>
        protected void ShowJavaScriptEncoded()
        {
            string userInput = Request.QueryString["data"];
            
            string safeOutput = HttpUtility.JavaScriptStringEncode(userInput);
            
            Response.Write("<script>var data = '" + safeOutput + "';</script>"); // FALSE POSITIVE
        }
        
        /// <summary>
        /// FALSE POSITIVE: HttpUtility.HtmlAttributeEncode()
        /// HTML attribute encoding
        /// </summary>
        protected void ShowAttributeEncoded()
        {
            string userInput = Request.QueryString["title"];
            
            string safeOutput = HttpUtility.HtmlAttributeEncode(userInput);
            
            Response.Write("<div title=\"" + safeOutput + "\">Content</div>"); // FALSE POSITIVE
        }
        
        /// <summary>
        /// FALSE POSITIVE: Server.HtmlEncode()
        /// Legacy ASP.NET encoding
        /// </summary>
        protected void ShowServerEncoded()
        {
            string userInput = Request.QueryString["message"];
            
            string safeOutput = Server.HtmlEncode(userInput);
            
            Response.Write("Message: " + safeOutput); // FALSE POSITIVE - Server encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: AntiXssEncoder (Microsoft.Security.Application)
        /// OWASP recommended encoder for .NET
        /// </summary>
        protected void ShowAntiXssEncoded()
        {
            string userInput = Request.QueryString["text"];
            
            // Microsoft AntiXSS Library (would be imported in actual deployment)
            string safeOutput = AntiXssEncoder.HtmlEncode(userInput);
            
            Response.Write("Text: " + safeOutput); // FALSE POSITIVE - AntiXSS encoded
        }
        
        /// <summary>
        /// FALSE POSITIVE: Combined encoding for complex output
        /// </summary>
        protected void ShowCombinedEncoding()
        {
            string htmlContent = Request.QueryString["html"];
            string jsContent = Request.QueryString["js"];
            
            string safeHtml = HttpUtility.HtmlEncode(htmlContent);
            string safeJs = HttpUtility.JavaScriptStringEncode(jsContent);
            
            Response.Write("<div>" + safeHtml + "</div>");
            Response.Write("<script>var msg = '" + safeJs + "';</script>"); // FALSE POSITIVE
        }
        
        // Mock class for AntiXSS (would be Microsoft.Security.Application.AntiXssEncoder)
        private static class AntiXssEncoder
        {
            public static string HtmlEncode(string input)
            {
                return HttpUtility.HtmlEncode(input);
            }
        }
    }
}

