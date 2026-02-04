using System;
using System.Web.Mvc;
using App.Core;

namespace App.Controllers
{
    public class InputController : Controller
    {
        public enum Category { Electronics, Clothing, Food, Other }
        public enum Priority { Low, Medium, High, Critical }
        
        // #R01 - int.Parse type conversion
        public ActionResult DisplayAge(string age)
        {
            int val = Sanitizer.ToInt(age);
            return Content($"<span>Age: {val}</span>", "text/html");
        }
        
        // #R02 - long.Parse type conversion
        public ActionResult DisplayTimestamp(string ts)
        {
            long val = Sanitizer.ToLong(ts);
            return Content($"<time>{val}</time>", "text/html");
        }
        
        // #R03 - double.Parse type conversion
        public ActionResult DisplayPrice(string price)
        {
            double val = Sanitizer.ToDouble(price);
            return Content($"<span>${val:F2}</span>", "text/html");
        }
        
        // #R04 - Enum.Parse type conversion
        public ActionResult DisplayCategory(string category)
        {
            var cat = (Category)Enum.Parse(typeof(Category), category, true);
            return Content($"<span>{cat}</span>", "text/html");
        }
        
        // #R05 - Enum.Parse with ordinal
        public ActionResult DisplayPriority(string priority)
        {
            var pri = (Priority)Enum.Parse(typeof(Priority), priority, true);
            return Content($"<span data-level=\"{(int)pri}\">{pri}</span>", "text/html");
        }
        
        // #R06 - Regex validated alphanumeric
        public ActionResult DisplayCode(string code)
        {
            if (Sanitizer.IsAlphanumeric(code))
            {
                return Content($"<code>{code}</code>", "text/html");
            }
            return new EmptyResult();
        }
        
        // #R07 - Regex validated numeric
        public ActionResult DisplayId(string id)
        {
            if (Sanitizer.IsNumeric(id))
            {
                return Content($"<span id=\"item-{id}\">{id}</span>", "text/html");
            }
            return new EmptyResult();
        }
        
        // #R08 - HTML escaped through utility
        public ActionResult DisplayName(string name)
        {
            string escaped = Sanitizer.EscapeHtml(name);
            return Content($"<span>{escaped}</span>", "text/html");
        }
        
        // #R09 - Ternary with numeric result
        public ActionResult DisplayCount(string count)
        {
            int input = Sanitizer.ToInt(count);
            int result = input > 100 ? 100 : input;
            return Content($"<span>{result}</span>", "text/html");
        }
        
        // #R10 - Math operation result
        public ActionResult DisplaySum(string a, string b)
        {
            int valA = Sanitizer.ToInt(a);
            int valB = Sanitizer.ToInt(b);
            int sum = valA + valB;
            return Content($"<span>{sum}</span>", "text/html");
        }
        
        // #R11 - bool.Parse
        public ActionResult DisplayFlag(string flag)
        {
            bool val = bool.Parse(flag);
            return Content($"<input type=\"checkbox\" {(val ? "checked" : "")}/>");
        }
        
        // #R12 - GUID validation
        public ActionResult DisplayUuid(string uuid)
        {
            var guid = Guid.Parse(uuid);
            return Content($"<span>{guid}</span>", "text/html");
        }
        
        // #R13 - Extracted numeric only
        public ActionResult DisplayExtracted(string mixed)
        {
            string numeric = Sanitizer.ExtractNumeric(mixed);
            return Content($"<span>{numeric}</span>", "text/html");
        }
        
        // #R14 - Masked input
        public ActionResult DisplayMasked(string card)
        {
            string masked = Sanitizer.Mask(card, 4);
            return Content($"<span>{masked}</span>", "text/html");
        }
    }
}

