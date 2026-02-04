using System;
using System.Text;
using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    /// <summary>
    /// Loop Condition False Positive Scenarios
    /// All scenarios are SAFE but CxQL incorrectly flags them
    /// Pattern: User input -> Bound -> Loop condition
    /// </summary>
    public class BatchController : Controller
    {
        private readonly EntityService _entityService;
        private const int MaxItems = 100;
        private const int MaxPages = 50;
        private const int MaxRows = 25;

        public enum PageSize { Small = 10, Medium = 25, Large = 50 }

        public BatchController(EntityService entityService)
        {
            _entityService = entityService;
        }

        // #L01 - Ternary operator bound (CxQL doesn't recognize)
        public ActionResult ProcessPages(string pages)
        {
            int input = Sanitizer.ToInt(pages);
            int bounded = input > MaxPages ? MaxPages : input;
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<div>Page {i}</div>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L02 - Modulo bound (CxQL doesn't recognize)
        public ActionResult ProcessModulo(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = input % MaxItems;
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<li>{i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L03 - Bitwise AND bound (CxQL doesn't recognize)
        public ActionResult ProcessBitwise(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = input & 0x3F; // Max 63
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L04 - Bitwise shift right bound (CxQL doesn't recognize)
        public ActionResult ProcessShift(string value)
        {
            int input = Sanitizer.ToInt(value);
            uint unsigned = (uint)input;
            int bounded = (int)((unsigned >> 4) & 0x0F); // Max 15
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L05 - Enum value bound (CxQL doesn't recognize)
        public ActionResult ProcessEnumBound(string size)
        {
            var pageSize = (PageSize)Enum.Parse(typeof(PageSize), size, true);
            var sb = new StringBuilder();
            for (int i = 0; i < (int)pageSize; i++)
            {
                sb.Append($"<div>{i}</div>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L06 - Enum values length bound (CxQL doesn't recognize)
        public ActionResult ProcessEnumLength(string count)
        {
            int input = Sanitizer.ToInt(count);
            int bounded = input % Enum.GetValues(typeof(PageSize)).Length;
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L07 - Array length bound (CxQL doesn't recognize)
        public ActionResult ProcessArrayBound(string data)
        {
            string[] parts = data.Split(',');
            var sb = new StringBuilder();
            int limit = Math.Min(parts.Length, MaxRows);
            for (int i = 0; i < limit; i++)
            {
                sb.Append($"<tr><td>{Sanitizer.EscapeHtml(parts[i])}</td></tr>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L08 - String length bound (CxQL doesn't recognize)
        public ActionResult ProcessStringLength(string text)
        {
            int length = Math.Min(text.Length, MaxItems);
            var sb = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                char c = text[i];
                if (char.IsLetterOrDigit(c))
                {
                    sb.Append($"<span>{c}</span>");
                }
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L09 - Nested ternary bound (CxQL doesn't recognize)
        public ActionResult ProcessNestedTernary(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = input < 0 ? 0 : (input > MaxItems ? MaxItems : input);
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<li>{i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L10 - Compound modulo bound (CxQL doesn't recognize)
        public ActionResult ProcessCompoundModulo(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = (input % 1000) % MaxItems; // Max 99
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L11 - Math.Clamp pattern (CxQL doesn't recognize)
        public ActionResult ProcessClamp(string value)
        {
            int input = Sanitizer.ToInt(value);
            int clamped = Math.Max(0, Math.Min(input, MaxRows));
            var sb = new StringBuilder();
            for (int i = 0; i < clamped; i++)
            {
                sb.Append($"<tr><td>{i}</td></tr>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L12 - XOR bound (CxQL doesn't recognize)
        public ActionResult ProcessXorBound(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = (input ^ 0xFF) & 0x1F; // Max 31
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L13 - Division bound (CxQL doesn't recognize)
        public ActionResult ProcessDivision(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = Math.Abs(input) / 100;
            int limited = Math.Min(bounded, MaxRows);
            var sb = new StringBuilder();
            for (int i = 0; i < limited; i++)
            {
                sb.Append($"<div>{i}</div>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L14 - Absolute value with modulo (CxQL doesn't recognize)
        public ActionResult ProcessAbsModulo(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = Math.Abs(input) % MaxItems;
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #L15 - LINQ Take simulation (CxQL doesn't recognize)
        public ActionResult ProcessTake(string count)
        {
            int input = Sanitizer.ToInt(count);
            int take = Math.Min(input, MaxRows);
            var sb = new StringBuilder();
            for (int i = 0; i < take; i++)
            {
                sb.Append($"<li>{i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }
    }
}

