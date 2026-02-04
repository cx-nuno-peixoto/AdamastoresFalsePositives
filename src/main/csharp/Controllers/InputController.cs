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
    /// Pattern: Request input -> Sanitization/Validation -> Web output
    /// </summary>
    public class InputController : Controller
    {
        public enum Category { Electronics, Clothing, Food, Other }
        public enum Priority { Low, Medium, High, Critical }
        private static readonly Regex SafePattern = new Regex(@"^[a-zA-Z0-9_-]+$");
        private static readonly HashSet<string> AllowedStatuses = new HashSet<string> { "active", "inactive", "pending" };

        /*
         * #R01 - FALSE POSITIVE: Regex validated alphanumeric input
         * WHY SAFE: IsAlphanumeric() validates that input contains ONLY [a-zA-Z0-9] characters.
         *           XSS requires special characters like <, >, ", ', etc. which are rejected.
         *           If validation fails, Content() with user input is never executed.
         * WHY CXQL FAILS: CxQL does not perform semantic analysis of custom validation methods.
         *                 It cannot determine that Sanitizer.IsAlphanumeric() restricts the character set.
         * CXQL LIMITATION: Missing support for conditional flow analysis with custom validation guards.
         */
        public ActionResult HandleCode(string code)
        {
            if (Sanitizer.IsAlphanumeric(code))
            {
                return Content($"<code>{code}</code>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #R02 - FALSE POSITIVE: Custom HTML escape applied
         * WHY SAFE: EscapeHtml() converts < > " ' & to HTML entities (&lt; &gt; etc.).
         *           Any XSS payload is neutralized before output to browser.
         *           This is the standard XSS mitigation approach.
         * WHY CXQL FAILS: CxQL does not recognize Sanitizer.EscapeHtml() as a sanitizer.
         *                 Only known framework methods (HttpUtility.HtmlEncode) are in the list.
         * CXQL LIMITATION: Custom sanitizer methods not recognized as XSS mitigation.
         */
        public ActionResult HandleName(string name)
        {
            string escaped = Sanitizer.EscapeHtml(name);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        /*
         * #R03 - FALSE POSITIVE: GUID parsing validates input
         * WHY SAFE: Guid.Parse() only accepts valid GUID format (32 hex chars + hyphens).
         *           If input contains XSS payload, ArgumentException is thrown.
         *           guid.ToString() outputs only canonical GUID format.
         * WHY CXQL FAILS: CxQL does not recognize Guid.Parse() as type-safe validation.
         *                 It sees string flowing to output without recognizing GUID constraint.
         * CXQL LIMITATION: GUID parsing not recognized as type-safe sanitizer.
         */
        public ActionResult HandleUuid(string uuid)
        {
            var guid = Guid.Parse(uuid);
            return Content($"<span>{guid}</span>", "text/html");
        }

        /*
         * #R04 - FALSE POSITIVE: Numeric extraction removes all non-digits
         * WHY SAFE: ExtractNumeric() returns only digit characters [0-9].
         *           XSS characters < > " ' are stripped, output is just numbers.
         * WHY CXQL FAILS: CxQL cannot analyze custom extraction methods.
         *                 It cannot determine that output is constrained to digits.
         * CXQL LIMITATION: Custom extraction/filtering methods not analyzed.
         */
        public ActionResult HandleExtracted(string mixed)
        {
            string numeric = Sanitizer.ExtractNumeric(mixed);
            return Content($"<span>{numeric}</span>", "text/html");
        }

        /*
         * #R05 - FALSE POSITIVE: Masked input with asterisks
         * WHY SAFE: Mask(card, 4) replaces all but last 4 chars with asterisks.
         *           XSS payload is destroyed - replaced with safe * characters.
         * WHY CXQL FAILS: CxQL does not recognize masking as sanitization.
         *                 It cannot determine that payload is replaced with asterisks.
         * CXQL LIMITATION: Masking/redaction not recognized as sanitization.
         */
        public ActionResult HandleMasked(string card)
        {
            string masked = Sanitizer.Mask(card, 4);
            return Content($"<span>{masked}</span>", "text/html");
        }

        /*
         * #R06 - FALSE POSITIVE: Enum.Parse validates against enum values
         * WHY SAFE: Enum.Parse() only accepts exact matches to enum constant names.
         *           If input is "<script>", ArgumentException is thrown.
         *           Only "Electronics", "Clothing", "Food", "Other" are accepted.
         * WHY CXQL FAILS: CxQL cannot determine that Enum.Parse() restricts values.
         *                 It sees string input flowing to output.
         * CXQL LIMITATION: Enum.Parse() not recognized as type-safe validation.
         */
        public ActionResult HandleCategory(string category)
        {
            var cat = (Category)Enum.Parse(typeof(Category), category, true);
            return Content($"<span>{cat}</span>", "text/html");
        }

        /*
         * #R07 - FALSE POSITIVE: Enum with ToString output
         * WHY SAFE: Same as R06 - Enum.Parse validates input to known constants.
         *           ToString() outputs only enum constant names ("Low", "High", etc.).
         * WHY CXQL FAILS: CxQL cannot track that ToString() returns predefined values.
         *                 It may see input string flowing to output.
         * CXQL LIMITATION: Enum.ToString() not recognized as safe output.
         */
        public ActionResult HandlePriority(string priority)
        {
            var pri = (Priority)Enum.Parse(typeof(Priority), priority, true);
            return Content($"<span>{pri.ToString()}</span>", "text/html");
        }

        /*
         * #R08 - FALSE POSITIVE: URL encoding applied
         * WHY SAFE: UrlEncode() converts < > " ' & to %XX escape sequences.
         *           These sequences are not interpreted as HTML by browsers.
         *           URL encoding is valid XSS mitigation for URL parameter context.
         * WHY CXQL FAILS: CxQL may recognize UrlEncode for URL injection but not HTML context.
         *                 It doesn't analyze the output context (href attribute).
         * CXQL LIMITATION: UrlEncode not in XSS sanitizer list for HTML context.
         */
        public ActionResult HandleUrlParam(string query)
        {
            string encoded = HttpUtility.UrlEncode(query);
            return Content($"<a href=\"search?{encoded}\">Link</a>", "text/html");
        }

        /*
         * #R09 - FALSE POSITIVE: Base64 encoding applied
         * WHY SAFE: Base64 produces only [A-Za-z0-9+/=] characters.
         *           XSS characters < > " ' are encoded to safe Base64 representation.
         *           Browser cannot interpret Base64 string as executable HTML.
         * WHY CXQL FAILS: CxQL does not recognize Base64 encoding as sanitization.
         *                 It cannot determine output character set is HTML-safe.
         * CXQL LIMITATION: Base64 encoding not in sanitizer list.
         */
        public ActionResult HandleBase64(string data)
        {
            string encoded = Convert.ToBase64String(Encoding.UTF8.GetBytes(data));
            return Content($"<span data-encoded=\"{encoded}\"></span>", "text/html");
        }

        /*
         * #R10 - FALSE POSITIVE: Regex validation guard
         * WHY SAFE: SafePattern ^[a-zA-Z0-9_-]+$ only allows safe characters.
         *           If input contains XSS characters, IsMatch() returns false.
         *           Output only happens when content is confirmed safe.
         * WHY CXQL FAILS: CxQL does not analyze Regex.IsMatch() guard conditions.
         *                 It cannot determine that the if-condition ensures safe content.
         * CXQL LIMITATION: Regex validation guards not recognized for Reflected XSS.
         */
        public ActionResult HandleSafeId(string id)
        {
            if (SafePattern.IsMatch(id))
            {
                return Content($"<div id=\"{id}\"></div>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #R11 - FALSE POSITIVE: Regex.Replace removes special characters
         * WHY SAFE: Regex.Replace with [^a-zA-Z0-9 ] strips ALL special characters.
         *           XSS requires < > " ' etc. which are removed by this transformation.
         *           Output contains only alphanumeric characters and spaces.
         * WHY CXQL FAILS: CxQL does not analyze Regex.Replace for sanitization effect.
         *                 It cannot determine that the transformation removes dangerous chars.
         * CXQL LIMITATION: Regex.Replace not analyzed for sanitization patterns.
         */
        public ActionResult HandleCleaned(string text)
        {
            string cleaned = Regex.Replace(text, @"[^a-zA-Z0-9 ]", "");
            return Content($"<p>{cleaned}</p>", "text/html");
        }

        /*
         * #R12 - FALSE POSITIVE: Whitelist validation
         * WHY SAFE: HashSet.Contains() only allows "active", "inactive", "pending".
         *           Any malicious input fails the whitelist check.
         *           Only predefined safe strings reach the output.
         * WHY CXQL FAILS: CxQL does not analyze whitelist/allowlist patterns.
         *                 It cannot determine that Contains() restricts to safe values.
         * CXQL LIMITATION: Whitelist validation patterns not recognized.
         */
        public ActionResult HandleStatus(string status)
        {
            if (AllowedStatuses.Contains(status.ToLowerInvariant()))
            {
                return Content($"<span class=\"{status}\"></span>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #R13 - FALSE POSITIVE: Truncated then HTML encoded
         * WHY SAFE: Truncation alone doesn't sanitize, but HtmlEncode() is called after.
         *           The final output has all XSS characters converted to entities.
         * WHY CXQL FAILS: CxQL SHOULD recognize HtmlEncode() - this may be a true FP.
         *                 If CxQL misses it, could be due to ternary expression analysis.
         * CXQL LIMITATION: Ternary with subsequent encoding may not be fully tracked.
         */
        public ActionResult HandleTruncated(string title)
        {
            string truncated = title.Length > 50 ? title.Substring(0, 50) : title;
            string escaped = HttpUtility.HtmlEncode(truncated);
            return Content($"<h1>{escaped}</h1>", "text/html");
        }

        /*
         * #R14 - FALSE POSITIVE: Hex color validation guard
         * WHY SAFE: Pattern ^[0-9a-fA-F]{6}$ only allows exactly 6 hex characters.
         *           XSS payloads cannot match this strict pattern.
         *           Output only happens for valid color codes like "FF0000".
         * WHY CXQL FAILS: CxQL does not analyze inline Regex.IsMatch() guards.
         *                 It cannot determine that pattern restricts to hex only.
         * CXQL LIMITATION: Inline regex validation not recognized as guard.
         */
        public ActionResult HandleHex(string color)
        {
            if (Regex.IsMatch(color, @"^[0-9a-fA-F]{6}$"))
            {
                return Content($"<div style=\"color:#{color}\"></div>", "text/html");
            }
            return new EmptyResult();
        }

        /*
         * #R15 - FALSE POSITIVE: Numeric conversion to hex string
         * WHY SAFE: ToInt() parses string to integer (or 0 on failure).
         *           ToString("X") formats integer as hex digits [0-9A-F].
         *           Output is constrained to hex characters only.
         * WHY CXQL FAILS: CxQL should recognize ToInt as type-safe conversion.
         *                 If flagged, may be due to ToString("X") not being analyzed.
         * CXQL LIMITATION: Formatted numeric output may not be fully tracked.
         */
        public ActionResult HandleNumericHex(string value)
        {
            int val = Sanitizer.ToInt(value);
            string hex = val.ToString("X");
            return Content($"<span>0x{hex}</span>", "text/html");
        }

        /*
         * #R16 - FALSE POSITIVE: StringBuilder with character filtering
         * WHY SAFE: Loop only appends chars passing IsLetterOrDigit/space check.
         *           XSS characters < > " ' are rejected and not included in output.
         *           The StringBuilder result contains only safe characters.
         * WHY CXQL FAILS: CxQL cannot track conditional StringBuilder append operations.
         *                 It does not analyze loop-based character filtering.
         * CXQL LIMITATION: No support for character-by-character filtering analysis.
         */
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

        /*
         * #R17 - FALSE POSITIVE: HttpUtility.HtmlEncode applied
         * WHY SAFE: HtmlEncode() is the standard .NET XSS sanitizer.
         *           Converts < > " ' & to HTML entities.
         * WHY CXQL FAILS: CxQL SHOULD recognize this as sanitizer.
         *                 If flagged, indicates C# sanitizer list issue.
         * CXQL LIMITATION: May be C# query configuration issue, not limitation.
         */
        public ActionResult HandleHtmlEscape(string content)
        {
            string escaped = HttpUtility.HtmlEncode(content);
            return Content($"<div>{escaped}</div>", "text/html");
        }

        /*
         * #R18 - FALSE POSITIVE: SecurityElement.Escape applied
         * WHY SAFE: SecurityElement.Escape() escapes XML/HTML special characters.
         *           Used for safely embedding content in XML/HTML attributes.
         * WHY CXQL FAILS: CxQL may not recognize SecurityElement.Escape() as sanitizer.
         *                 It's a less common method compared to HtmlEncode.
         * CXQL LIMITATION: SecurityElement.Escape() not in sanitizer list.
         */
        public ActionResult HandleXmlEscape(string attr)
        {
            string escaped = System.Security.SecurityElement.Escape(attr);
            return Content($"<input value=\"{escaped}\"/>", "text/html");
        }

        /*
         * #R19 - FALSE POSITIVE: JSON string escaping
         * WHY SAFE: EscapeJsonString() escapes \\ \" and control characters.
         *           In JavaScript string literal, this prevents string breakout.
         *           Standard approach for safely embedding data in JS strings.
         * WHY CXQL FAILS: CxQL does not recognize JSON/JavaScript string escaping.
         *                 It cannot determine that the JS string context is protected.
         * CXQL LIMITATION: JSON escaping not recognized for JavaScript context XSS.
         */
        public ActionResult HandleJsonEscape(string json)
        {
            string escaped = EscapeJsonString(json);
            return Content($"<script>var data = \"{escaped}\";</script>", "text/html");
        }

        /*
         * #R20 - FALSE POSITIVE: CSS value pattern validation
         * WHY SAFE: Pattern ^[0-9]+(px|em|rem|%)$ only allows numeric CSS values.
         *           Valid examples: "100px", "1.5em", "50%".
         *           XSS payloads cannot match this strict pattern.
         * WHY CXQL FAILS: CxQL does not analyze CSS context validation.
         *                 It cannot determine that pattern restricts to safe values.
         * CXQL LIMITATION: CSS value validation not recognized.
         */
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

