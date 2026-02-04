using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Mvc;
using App.Core;

namespace App.Controllers
{
    /// <summary>
    /// Reflected XSS False Positive Scenarios
    /// All scenarios are SAFE but CxQL incorrectly flags them
    /// </summary>
    public class InputController : Controller
    {
        public enum Category { Electronics, Clothing, Food, Other }
        public enum Priority { Low, Medium, High, Critical }
        private static readonly Regex SafePattern = new Regex(@"^[a-zA-Z0-9_-]+$");
        private static readonly HashSet<string> AllowedStatuses = new HashSet<string> { "active", "inactive", "pending" };

        // #R01 - Regex validated alphanumeric (CxQL misses validation guard)
        public ActionResult HandleCode(string code)
        {
            if (Sanitizer.IsAlphanumeric(code))
            {
                return Content($"<code>{code}</code>", "text/html");
            }
            return new EmptyResult();
        }

        // #R02 - Custom HTML escape (CxQL doesn't recognize custom escaper)
        public ActionResult HandleName(string name)
        {
            string escaped = Sanitizer.EscapeHtml(name);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        // #R03 - GUID validation (CxQL doesn't recognize Guid.Parse as sanitizer)
        public ActionResult HandleUuid(string uuid)
        {
            var guid = Guid.Parse(uuid);
            return Content($"<span>{guid}</span>", "text/html");
        }

        // #R04 - Extract numeric only (CxQL doesn't recognize digit extraction)
        public ActionResult HandleExtracted(string mixed)
        {
            string numeric = Sanitizer.ExtractNumeric(mixed);
            return Content($"<span>{numeric}</span>", "text/html");
        }

        // #R05 - Masked input (CxQL doesn't recognize masking as sanitizer)
        public ActionResult HandleMasked(string card)
        {
            string masked = Sanitizer.Mask(card, 4);
            return Content($"<span>{masked}</span>", "text/html");
        }

        // #R06 - Enum.Parse validation (CxQL doesn't recognize enum validation)
        public ActionResult HandleCategory(string category)
        {
            var cat = (Category)Enum.Parse(typeof(Category), category, true);
            return Content($"<span>{cat}</span>", "text/html");
        }

        // #R07 - Enum with ToString (CxQL doesn't recognize enum.ToString as safe)
        public ActionResult HandlePriority(string priority)
        {
            var pri = (Priority)Enum.Parse(typeof(Priority), priority, true);
            return Content($"<span>{pri.ToString()}</span>", "text/html");
        }

        // #R08 - URL encoding (CxQL doesn't recognize UrlEncode as sanitizer)
        public ActionResult HandleUrlParam(string query)
        {
            string encoded = HttpUtility.UrlEncode(query);
            return Content($"<a href=\"search?{encoded}\">Link</a>", "text/html");
        }

        // #R09 - Base64 encoding (CxQL doesn't recognize Base64 as sanitizer)
        public ActionResult HandleBase64(string data)
        {
            string encoded = Convert.ToBase64String(Encoding.UTF8.GetBytes(data));
            return Content($"<span data-encoded=\"{encoded}\"></span>", "text/html");
        }

        // #R10 - Regex.IsMatch validation (CxQL doesn't recognize Pattern guard)
        public ActionResult HandleSafeId(string id)
        {
            if (SafePattern.IsMatch(id))
            {
                return Content($"<div id=\"{id}\"></div>", "text/html");
            }
            return new EmptyResult();
        }

        // #R11 - Regex.Replace sanitization (CxQL doesn't recognize Replace)
        public ActionResult HandleCleaned(string text)
        {
            string cleaned = Regex.Replace(text, @"[^a-zA-Z0-9 ]", "");
            return Content($"<p>{cleaned}</p>", "text/html");
        }

        // #R12 - Whitelist validation (CxQL doesn't recognize Contains check)
        public ActionResult HandleStatus(string status)
        {
            if (AllowedStatuses.Contains(status.ToLowerInvariant()))
            {
                return Content($"<span class=\"{status}\"></span>", "text/html");
            }
            return new EmptyResult();
        }

        // #R13 - Length-bounded substring (CxQL doesn't recognize length limit)
        public ActionResult HandleTruncated(string title)
        {
            string truncated = title.Length > 50 ? title.Substring(0, 50) : title;
            string escaped = HttpUtility.HtmlEncode(truncated);
            return Content($"<h1>{escaped}</h1>", "text/html");
        }

        // #R14 - Hex validation (CxQL doesn't recognize hex pattern)
        public ActionResult HandleHex(string color)
        {
            if (Regex.IsMatch(color, @"^[0-9a-fA-F]{6}$"))
            {
                return Content($"<div style=\"color:#{color}\"></div>", "text/html");
            }
            return new EmptyResult();
        }

        // #R15 - Int to hex string (CxQL doesn't recognize numeric hex conversion)
        public ActionResult HandleNumericHex(string value)
        {
            int val = Sanitizer.ToInt(value);
            string hex = val.ToString("X");
            return Content($"<span>0x{hex}</span>", "text/html");
        }

        // #R16 - StringBuilder with validation (CxQL doesn't track StringBuilder)
        public ActionResult HandleBuilt(string parts)
        {
            var sb = new StringBuilder();
            foreach (char c in parts)
            {
                if (char.IsLetterOrDigit(c) || c == ' ')
                {
                    sb.Append(c);
                }
            }
            return Content($"<span>{sb}</span>", "text/html");
        }

        // #R17 - HttpUtility.HtmlEncode
        public ActionResult HandleHtmlEscape(string content)
        {
            string escaped = HttpUtility.HtmlEncode(content);
            return Content($"<div>{escaped}</div>", "text/html");
        }

        // #R18 - SecurityElement.Escape simulation
        public ActionResult HandleXmlEscape(string attr)
        {
            string escaped = System.Security.SecurityElement.Escape(attr);
            return Content($"<input value=\"{escaped}\"/>", "text/html");
        }

        // #R19 - JSON escape (CxQL doesn't recognize JSON escaping)
        public ActionResult HandleJsonEscape(string json)
        {
            string escaped = EscapeJsonString(json);
            return Content($"<script>var data = \"{escaped}\";</script>", "text/html");
        }

        // #R20 - CSS value validation (CxQL doesn't recognize CSS pattern)
        public ActionResult HandleCssValue(string size)
        {
            if (Regex.IsMatch(size, @"^[0-9]+(px|em|rem|%)$"))
            {
                return Content($"<div style=\"width:{size}\"></div>", "text/html");
            }
            return new EmptyResult();
        }

        private string EscapeJsonString(string input)
        {
            if (string.IsNullOrEmpty(input)) return "";
            return input.Replace("\\", "\\\\").Replace("\"", "\\\"")
                        .Replace("\n", "\\n").Replace("\r", "\\r").Replace("\t", "\\t");
        }
    }
}

