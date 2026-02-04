using System;
using System.Web;
using System.Web.Mvc;
using System.Net;
using System.Text;
using System.Security;

namespace FalsePositiveTestProject.Controllers
{
    /// <summary>
    /// Framework Sanitizer False Positive Scenarios
    /// Tests known .NET framework sanitizers that CxQL SHOULD recognize
    /// If flagged, indicates CxQL sanitizer list is incomplete
    /// </summary>
    public class FrameworkController : Controller
    {
        /*
         * #F01 - System.Web.HttpUtility.HtmlEncode
         * WHY SAFE: HttpUtility.HtmlEncode() is the standard .NET XSS sanitizer.
         *           Converts < > " ' & to HTML entities.
         * WHY CXQL SHOULD RECOGNIZE: HttpUtility.HtmlEncode is in CxQL's sanitizer list.
         * TEST PURPOSE: Verify .NET HttpUtility sanitizer recognition.
         */
        public ActionResult HandleHttpUtilityHtml(string data)
        {
            string safe = HttpUtility.HtmlEncode(data);
            return Content($"<div>{safe}</div>", "text/html");
        }

        /*
         * #F02 - System.Web.HttpUtility.UrlEncode
         * WHY SAFE: HttpUtility.UrlEncode() URL-encodes special characters.
         *           Prevents XSS in URL contexts.
         * WHY CXQL SHOULD RECOGNIZE: HttpUtility.UrlEncode should be recognized for URL context.
         * TEST PURPOSE: Verify .NET URL encoder recognition.
         */
        public ActionResult HandleHttpUtilityUrl(string redirect)
        {
            string safe = HttpUtility.UrlEncode(redirect);
            return Content($"<a href=\"{safe}\">Link</a>", "text/html");
        }

        /*
         * #F03 - System.Web.HttpUtility.JavaScriptStringEncode
         * WHY SAFE: JavaScriptStringEncode() escapes for JavaScript string context.
         *           Prevents XSS in JavaScript string literals.
         * WHY CXQL SHOULD RECOGNIZE: JavaScript encoder should be in sanitizer list.
         * TEST PURPOSE: Verify .NET JavaScript encoder recognition.
         */
        public ActionResult HandleHttpUtilityJs(string data)
        {
            string safe = HttpUtility.JavaScriptStringEncode(data);
            return Content($"<script>var x = '{safe}';</script>", "text/html");
        }

        /*
         * #F04 - System.Net.WebUtility.HtmlEncode
         * WHY SAFE: WebUtility.HtmlEncode() is the modern .NET Core HTML encoder.
         *           Recommended for .NET Core and .NET 5+ applications.
         * WHY CXQL SHOULD RECOGNIZE: WebUtility.HtmlEncode should be recognized.
         * TEST PURPOSE: Verify .NET Core WebUtility recognition.
         */
        public ActionResult HandleWebUtilityHtml(string text)
        {
            string safe = WebUtility.HtmlEncode(text);
            return Content($"<p>{safe}</p>", "text/html");
        }

        /*
         * #F05 - System.Net.WebUtility.UrlEncode
         * WHY SAFE: WebUtility.UrlEncode() is the modern URL encoder.
         *           Standard for .NET Core URL encoding.
         * WHY CXQL SHOULD RECOGNIZE: WebUtility.UrlEncode should be recognized.
         * TEST PURPOSE: Verify .NET Core URL encoder recognition.
         */
        public ActionResult HandleWebUtilityUrl(string query)
        {
            string safe = WebUtility.UrlEncode(query);
            return Content($"<a href=\"search?q={safe}\">Search</a>", "text/html");
        }

        /*
         * #F06 - System.Security.SecurityElement.Escape
         * WHY SAFE: SecurityElement.Escape() escapes XML special characters.
         *           Used for safely embedding content in XML/HTML.
         * WHY CXQL SHOULD RECOGNIZE: SecurityElement.Escape is a system sanitizer.
         * TEST PURPOSE: Verify SecurityElement.Escape recognition.
         */
        public ActionResult HandleSecurityElement(string attr)
        {
            string safe = SecurityElement.Escape(attr);
            return Content($"<input value=\"{safe}\"/>", "text/html");
        }

        /*
         * #F07 - Microsoft AntiXss HtmlEncode (Microsoft.Security.Application)
         * WHY SAFE: AntiXss.HtmlEncode() is Microsoft's security-focused encoder.
         *           More restrictive than HttpUtility, whitelisting approach.
         * WHY CXQL SHOULD RECOGNIZE: Microsoft AntiXss is a security library.
         * TEST PURPOSE: Verify Microsoft AntiXss recognition.
         */
        public ActionResult HandleAntiXssHtml(string content)
        {
            // string safe = Microsoft.Security.Application.Encoder.HtmlEncode(content);
            string safe = AntiXssHtmlEncode(content); // Simulated
            return Content($"<span>{safe}</span>", "text/html");
        }

        /*
         * #F08 - Microsoft AntiXss JavaScriptEncode
         * WHY SAFE: AntiXss.JavaScriptEncode() escapes for JavaScript context.
         *           Uses whitelisting approach for maximum security.
         * WHY CXQL SHOULD RECOGNIZE: Microsoft AntiXss JS encoder.
         * TEST PURPOSE: Verify Microsoft AntiXss JS encoder recognition.
         */
        public ActionResult HandleAntiXssJs(string data)
        {
            // string safe = Microsoft.Security.Application.Encoder.JavaScriptEncode(data);
            string safe = AntiXssJavaScriptEncode(data); // Simulated
            return Content($"<script>var x = {safe};</script>", "text/html");
        }

        /*
         * #F09 - Microsoft AntiXss UrlEncode
         * WHY SAFE: AntiXss.UrlEncode() URL-encodes with strict whitelisting.
         *           More secure than standard URL encoding.
         * WHY CXQL SHOULD RECOGNIZE: Microsoft AntiXss URL encoder.
         * TEST PURPOSE: Verify Microsoft AntiXss URL encoder recognition.
         */
        public ActionResult HandleAntiXssUrl(string redirect)
        {
            // string safe = Microsoft.Security.Application.Encoder.UrlEncode(redirect);
            string safe = AntiXssUrlEncode(redirect); // Simulated
            return Content($"<a href=\"{safe}\">Link</a>", "text/html");
        }

        /*
         * #F10 - ASP.NET MVC Html.Encode
         * WHY SAFE: Html.Encode() is the MVC helper for HTML encoding.
         *           Standard approach in ASP.NET MVC views.
         * WHY CXQL SHOULD RECOGNIZE: MVC Html helper is a core sanitizer.
         * TEST PURPOSE: Verify MVC Html.Encode recognition.
         */
        public ActionResult HandleMvcHtmlEncode(string content)
        {
            // In view: @Html.Encode(content)
            string safe = HttpUtility.HtmlEncode(content); // Same as Html.Encode
            return Content($"<div>{safe}</div>", "text/html");
        }

        /*
         * #F11 - ASP.NET MVC Html.AttributeEncode
         * WHY SAFE: Html.AttributeEncode() escapes for HTML attribute context.
         *           Specifically handles attribute-context XSS vectors.
         * WHY CXQL SHOULD RECOGNIZE: MVC attribute encoder.
         * TEST PURPOSE: Verify MVC Html.AttributeEncode recognition.
         */
        public ActionResult HandleMvcAttrEncode(string title)
        {
            // In view: @Html.AttributeEncode(title)
            string safe = HttpUtility.HtmlAttributeEncode(title);
            return Content($"<div title=\"{safe}\">Content</div>", "text/html");
        }

        /*
         * #F12 - Razor automatic encoding (@variable)
         * WHY SAFE: Razor @variable syntax automatically HTML-encodes.
         *           Default safe behavior in ASP.NET MVC/Core views.
         * WHY CXQL SHOULD RECOGNIZE: Razor encoding is automatic.
         * TEST PURPOSE: Verify Razor automatic encoding recognition.
         */
        public ActionResult HandleRazorAuto(string text)
        {
            // In Razor view: @text (automatically encoded)
            string safe = HttpUtility.HtmlEncode(text); // Simulates Razor behavior
            return Content($"<span>{safe}</span>", "text/html");
        }

        /*
         * #F13 - System.Text.Encodings.Web.HtmlEncoder
         * WHY SAFE: HtmlEncoder.Default.Encode() is the .NET Core encoder.
         *           Modern replacement for HttpUtility in .NET Core.
         * WHY CXQL SHOULD RECOGNIZE: .NET Core encoder should be recognized.
         * TEST PURPOSE: Verify .NET Core HtmlEncoder recognition.
         */
        public ActionResult HandleTextEncoderHtml(string data)
        {
            // string safe = System.Text.Encodings.Web.HtmlEncoder.Default.Encode(data);
            string safe = TextEncoderHtml(data); // Simulated
            return Content($"<p>{safe}</p>", "text/html");
        }

        /*
         * #F14 - System.Text.Encodings.Web.JavaScriptEncoder
         * WHY SAFE: JavaScriptEncoder.Default.Encode() is the .NET Core JS encoder.
         *           Modern JavaScript encoding for .NET Core.
         * WHY CXQL SHOULD RECOGNIZE: .NET Core JS encoder.
         * TEST PURPOSE: Verify .NET Core JavaScriptEncoder recognition.
         */
        public ActionResult HandleTextEncoderJs(string data)
        {
            // string safe = System.Text.Encodings.Web.JavaScriptEncoder.Default.Encode(data);
            string safe = TextEncoderJs(data); // Simulated
            return Content($"<script>var x = '{safe}';</script>", "text/html");
        }

        /*
         * #F15 - System.Text.Encodings.Web.UrlEncoder
         * WHY SAFE: UrlEncoder.Default.Encode() is the .NET Core URL encoder.
         *           Modern URL encoding for .NET Core applications.
         * WHY CXQL SHOULD RECOGNIZE: .NET Core URL encoder.
         * TEST PURPOSE: Verify .NET Core UrlEncoder recognition.
         */
        public ActionResult HandleTextEncoderUrl(string query)
        {
            // string safe = System.Text.Encodings.Web.UrlEncoder.Default.Encode(query);
            string safe = TextEncoderUrl(query); // Simulated
            return Content($"<a href=\"search?q={safe}\">Search</a>", "text/html");
        }

        // ========== Simulated Framework Methods ==========
        // These simulate the actual framework methods for testing purposes

        private string AntiXssHtmlEncode(string input)
        {
            if (string.IsNullOrEmpty(input)) return "";
            return input.Replace("&", "&amp;").Replace("<", "&lt;")
                       .Replace(">", "&gt;").Replace("\"", "&quot;").Replace("'", "&#x27;");
        }

        private string AntiXssJavaScriptEncode(string input)
        {
            if (string.IsNullOrEmpty(input)) return "''";
            return "'" + input.Replace("\\", "\\\\").Replace("'", "\\'")
                             .Replace("\"", "\\\"").Replace("\n", "\\n").Replace("\r", "\\r") + "'";
        }

        private string AntiXssUrlEncode(string input)
        {
            return HttpUtility.UrlEncode(input ?? "");
        }

        private string TextEncoderHtml(string input)
        {
            return AntiXssHtmlEncode(input);
        }

        private string TextEncoderJs(string input)
        {
            if (string.IsNullOrEmpty(input)) return "";
            return input.Replace("\\", "\\\\").Replace("'", "\\'")
                       .Replace("\"", "\\\"").Replace("\n", "\\n");
        }

        private string TextEncoderUrl(string input)
        {
            return HttpUtility.UrlEncode(input ?? "");
        }
    }
}

