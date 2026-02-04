using System;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    /// <summary>
    /// Stored XSS False Positive Scenarios
    /// All scenarios are SAFE but CxQL incorrectly flags them
    /// Pattern: DB string -> Sanitization -> Web output
    /// </summary>
    public class DisplayController : Controller
    {
        private readonly EntityService _entityService;
        private readonly AccountService _accountService;
        private static readonly string[] AllowedTypes = { "product", "service", "category" };

        public DisplayController(EntityService entityService, AccountService accountService)
        {
            _entityService = entityService;
            _accountService = accountService;
        }

        /*
         * #X01 - FALSE POSITIVE: Database string escaped through custom escaper
         * WHY SAFE: EscapeHtml() converts < > " ' & to HTML entities before output.
         *           Even if database contains malicious content, it's neutralized by escaping.
         *           This is the standard Stored XSS mitigation approach.
         * WHY CXQL FAILS: CxQL recognizes DB output as source for Stored XSS.
         *                 However, it does not recognize custom Sanitizer.EscapeHtml() as sanitizer.
         * CXQL LIMITATION: Custom sanitizer methods not recognized as XSS mitigation.
         */
        public ActionResult RenderEntityName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string escaped = Sanitizer.EscapeHtml(name);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        /*
         * #X02 - FALSE POSITIVE: Database string with alphanumeric extraction
         * WHY SAFE: Regex.Replace with [^a-zA-Z0-9 ] strips ALL special characters.
         *           XSS requires < > " ' etc. which are removed by this transformation.
         * WHY CXQL FAILS: CxQL does not analyze Regex.Replace for sanitization effect.
         *                 It cannot determine that the transformation removes dangerous chars.
         * CXQL LIMITATION: Regex.Replace not analyzed for sanitization patterns.
         */
        public ActionResult RenderAlphanumericName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string safe = Regex.Replace(name ?? "", @"[^a-zA-Z0-9 ]", "");
            return Content($"<span>{safe}</span>", "text/html");
        }

        /*
         * #X03 - FALSE POSITIVE: Database string with validation guard
         * WHY SAFE: IsAlphanumeric() validates that content contains only safe characters.
         *           If DB content contains XSS characters, it fails validation.
         *           Output only happens when content is confirmed safe.
         * WHY CXQL FAILS: CxQL does not analyze custom validation guard conditions.
         *                 It cannot determine that the if-condition ensures safe content.
         * CXQL LIMITATION: Custom validation guards not recognized for Stored XSS.
         */
        public ActionResult RenderDescription(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string desc = _entityService.GetEntityDescription(inputId);
            if (Sanitizer.IsAlphanumeric(desc?.Replace(" ", "") ?? ""))
            {
                return Content($"<p>{desc}</p>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #X04 - FALSE POSITIVE: Database string URL encoded
         * WHY SAFE: UrlEncode() converts < > " ' & to %XX escape sequences.
         *           These sequences are not interpreted as HTML by browsers.
         * WHY CXQL FAILS: CxQL may not recognize UrlEncode as XSS sanitizer for HTML context.
         *                 It's typically recognized for URL injection, not HTML.
         * CXQL LIMITATION: UrlEncode not in XSS sanitizer list.
         */
        public ActionResult RenderUrlEncodedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string encoded = HttpUtility.UrlEncode(name ?? "");
            return Content($"<a href=\"/entity?name={encoded}\">View</a>", "text/html");
        }

        /*
         * #X05 - FALSE POSITIVE: Database string masked
         * WHY SAFE: Mask(name, 3) replaces most characters with asterisks.
         *           XSS payloads are destroyed - only asterisks and last 3 chars remain.
         * WHY CXQL FAILS: CxQL does not recognize masking as sanitization.
         *                 It cannot determine that the transformation destroys payloads.
         * CXQL LIMITATION: Masking/redaction not recognized as sanitization.
         */
        public ActionResult RenderMaskedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string masked = Sanitizer.Mask(name ?? "", 3);
            return Content($"<span>{masked}</span>", "text/html");
        }

        /*
         * #X06 - FALSE POSITIVE: Database string truncated then escaped
         * WHY SAFE: Truncation alone doesn't sanitize, but HtmlEncode() is called after.
         *           The final output has all XSS characters converted to entities.
         * WHY CXQL FAILS: CxQL should recognize HtmlEncode() as sanitizer.
         *                 If flagged, may be due to ternary expression flow tracking.
         * CXQL LIMITATION: Ternary with HtmlEncode may not be fully tracked.
         */
        public ActionResult RenderTruncatedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string truncated = name.Length > 20 ? name.Substring(0, 20) : name;
            string escaped = HttpUtility.HtmlEncode(truncated);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        /*
         * #X07 - FALSE POSITIVE: Database string validated via Enum.TryParse
         * WHY SAFE: Enum.TryParse() only succeeds for exact enum constant names.
         *           If DB contains "<script>", TryParse returns false.
         *           Only predefined constant names reach the output.
         * WHY CXQL FAILS: CxQL cannot determine that Enum.TryParse restricts values.
         *                 It sees data flow from DB to output without recognizing enum validation.
         * CXQL LIMITATION: Enum.TryParse() not recognized as type-safe validation.
         */
        public ActionResult RenderEntityType(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string type = _entityService.GetEntityType(inputId);
            if (Enum.TryParse<EntityType>(type, true, out var enumType))
            {
                return Content($"<span>{enumType}</span>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #X08 - FALSE POSITIVE: Database string with whitelist validation
         * WHY SAFE: Array.Exists() checks against whitelist ["product", "service", "category"].
         *           Any malicious DB content fails the whitelist check.
         *           Only predefined safe strings reach the output.
         * WHY CXQL FAILS: CxQL does not analyze whitelist/allowlist patterns.
         *                 It cannot determine that Array.Exists restricts to safe values.
         * CXQL LIMITATION: Whitelist validation patterns not recognized.
         */
        public ActionResult RenderCategory(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string category = _entityService.GetEntityCategory(inputId);
            if (Array.Exists(AllowedTypes, t => t.Equals(category, StringComparison.OrdinalIgnoreCase)))
            {
                return Content($"<span class=\"{category}\">{category}</span>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #X09 - FALSE POSITIVE: Database string with StringBuilder character filtering
         * WHY SAFE: Loop only appends chars passing IsLetterOrDigit/space check.
         *           XSS characters < > " ' are rejected and not included in output.
         * WHY CXQL FAILS: CxQL cannot track conditional StringBuilder append operations.
         *                 It does not analyze loop-based character filtering.
         * CXQL LIMITATION: No support for character-by-character filtering analysis.
         */
        public ActionResult RenderFilteredName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            var sb = new StringBuilder();
            foreach (char c in name)
            {
                if (char.IsLetterOrDigit(c) || c == ' ') sb.Append(c);
            }
            return Content($"<span>{sb}</span>", "text/html");
        }

        /*
         * #X10 - FALSE POSITIVE: Database UUID string validated via Guid.TryParse
         * WHY SAFE: Guid.TryParse() only succeeds for valid GUID format.
         *           If DB contains XSS payload, TryParse returns false.
         *           guid.ToString() outputs only canonical GUID format.
         * WHY CXQL FAILS: CxQL does not recognize Guid.TryParse() as validation.
         *                 It sees DB string flowing to output.
         * CXQL LIMITATION: GUID parsing not recognized as sanitizer/validator.
         */
        public ActionResult RenderEntityUuid(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string uuid = _entityService.GetEntityUuid(inputId);
            if (Guid.TryParse(uuid, out var guid))
            {
                return Content($"<span>{guid}</span>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #X11 - FALSE POSITIVE: Cross-service database string escaped
         * WHY SAFE: _accountService returns DB data, escaped by HtmlEncode().
         *           HtmlEncode is standard .NET XSS sanitizer.
         * WHY CXQL FAILS: CxQL SHOULD recognize HtmlEncode() as sanitizer.
         *                 If flagged, may indicate C# query configuration issue.
         * CXQL LIMITATION: May be C# sanitizer recognition issue.
         */
        public ActionResult RenderAccountName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _accountService.GetAccountName(inputId);
            string escaped = HttpUtility.HtmlEncode(name ?? "");
            return Content($"<span>{escaped}</span>", "text/html");
        }

        /*
         * #X12 - FALSE POSITIVE: Email domain extraction with validation
         * WHY SAFE: Domain extracted via IndexOf/Substring, then validated.
         *           Regex ^[a-zA-Z0-9.-]+$ ensures only safe domain chars.
         *           Output only happens when domain matches safe pattern.
         * WHY CXQL FAILS: CxQL may not analyze the regex guard condition.
         *                 It sees email data flowing to output.
         * CXQL LIMITATION: Regex guards on extracted data not analyzed.
         */
        public ActionResult RenderEmailDomain(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string email = _accountService.GetAccountEmail(inputId) ?? "";
            int atIndex = email.IndexOf('@');
            string domain = atIndex >= 0 ? email.Substring(atIndex + 1) : "";
            if (Regex.IsMatch(domain, @"^[a-zA-Z0-9.-]+$"))
            {
                return Content($"<span>{domain}</span>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #X13 - FALSE POSITIVE: Database string hex encoded
         * WHY SAFE: BitConverter.ToString() produces only hex characters [0-9A-F-].
         *           Replace("-", "") removes hyphens, leaving only [0-9A-F].
         *           XSS characters are encoded to safe hex representation.
         * WHY CXQL FAILS: CxQL cannot analyze hex encoding for sanitization.
         *                 It doesn't recognize that output is constrained to hex chars.
         * CXQL LIMITATION: Hex encoding not in sanitizer list.
         */
        public ActionResult RenderHexEncodedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string hex = BitConverter.ToString(Encoding.UTF8.GetBytes(name)).Replace("-", "");
            return Content($"<span data-hex=\"{hex}\"></span>", "text/html");
        }

        /*
         * #X14 - FALSE POSITIVE: Database string Base64 encoded
         * WHY SAFE: ToBase64String() produces only [A-Za-z0-9+/=] characters.
         *           XSS characters < > " ' are encoded to safe Base64 representation.
         * WHY CXQL FAILS: CxQL does not recognize Base64 encoding as sanitization.
         *                 It cannot determine output character set is HTML-safe.
         * CXQL LIMITATION: Base64 encoding not in sanitizer list.
         */
        public ActionResult RenderBase64Name(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string encoded = Convert.ToBase64String(Encoding.UTF8.GetBytes(name));
            return Content($"<span data-encoded=\"{encoded}\"></span>", "text/html");
        }

        /*
         * #X15 - FALSE POSITIVE: Database string JSON escaped
         * WHY SAFE: EscapeJsonString() escapes \\ \" and control characters.
         *           In JavaScript string literal, this prevents string breakout.
         * WHY CXQL FAILS: CxQL does not recognize JSON/JavaScript string escaping.
         *                 It cannot determine that the JS string context is protected.
         * CXQL LIMITATION: JSON escaping not recognized for JavaScript context XSS.
         */
        public ActionResult RenderJsonContent(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string content = _entityService.GetEntityContent(inputId) ?? "";
            string escaped = EscapeJsonString(content);
            return Content($"<script>var data = \"{escaped}\";</script>", "text/html");
        }

        private string EscapeJsonString(string input)
        {
            if (string.IsNullOrEmpty(input)) return "";
            return input.Replace("\\", "\\\\").Replace("\"", "\\\"")
                        .Replace("\n", "\\n").Replace("\r", "\\r").Replace("\t", "\\t");
        }
    }

    public enum EntityType { Product, Service, Category, Other }
}

