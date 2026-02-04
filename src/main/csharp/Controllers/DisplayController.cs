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

        // #X01 - DB name escaped through custom escaper (CxQL doesn't recognize)
        public ActionResult RenderEntityName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string escaped = Sanitizer.EscapeHtml(name);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        // #X02 - DB name extracted alphanumeric only (CxQL doesn't recognize)
        public ActionResult RenderAlphanumericName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string safe = Regex.Replace(name ?? "", @"[^a-zA-Z0-9 ]", "");
            return Content($"<span>{safe}</span>", "text/html");
        }

        // #X03 - DB description with Pattern validation (CxQL doesn't recognize)
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

        // #X04 - DB name URL encoded (CxQL doesn't recognize UrlEncode)
        public ActionResult RenderUrlEncodedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string encoded = HttpUtility.UrlEncode(name ?? "");
            return Content($"<a href=\"/entity?name={encoded}\">View</a>", "text/html");
        }

        // #X05 - DB name masked (CxQL doesn't recognize masking)
        public ActionResult RenderMaskedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId);
            string masked = Sanitizer.Mask(name ?? "", 3);
            return Content($"<span>{masked}</span>", "text/html");
        }

        // #X06 - DB name substring with length limit (CxQL doesn't recognize)
        public ActionResult RenderTruncatedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string truncated = name.Length > 20 ? name.Substring(0, 20) : name;
            string escaped = HttpUtility.HtmlEncode(truncated);
            return Content($"<span>{escaped}</span>", "text/html");
        }

        // #X07 - DB code via Enum validation (CxQL doesn't recognize)
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

        // #X08 - DB value with whitelist check (CxQL doesn't recognize)
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

        // #X09 - DB name with StringBuilder filtering (CxQL doesn't recognize)
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

        // #X10 - DB name with GUID validation (CxQL doesn't recognize)
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

        // #X11 - Account name escaped (CxQL doesn't recognize)
        public ActionResult RenderAccountName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _accountService.GetAccountName(inputId);
            string escaped = HttpUtility.HtmlEncode(name ?? "");
            return Content($"<span>{escaped}</span>", "text/html");
        }

        // #X12 - Account email domain only (CxQL doesn't recognize)
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

        // #X13 - DB name through hex encoding (CxQL doesn't recognize)
        public ActionResult RenderHexEncodedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string hex = BitConverter.ToString(Encoding.UTF8.GetBytes(name)).Replace("-", "");
            return Content($"<span data-hex=\"{hex}\"></span>", "text/html");
        }

        // #X14 - DB name through Base64 (CxQL doesn't recognize)
        public ActionResult RenderBase64Name(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEntityName(inputId) ?? "";
            string encoded = Convert.ToBase64String(Encoding.UTF8.GetBytes(name));
            return Content($"<span data-encoded=\"{encoded}\"></span>", "text/html");
        }

        // #X15 - DB content with JSON escape (CxQL doesn't recognize)
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

