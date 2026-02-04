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

        /*
         * #L01 - FALSE POSITIVE: Ternary operator bounds the loop
         * WHY SAFE: Ternary (input > MaxPages ? MaxPages : input) limits loop to MaxPages (50).
         *           Regardless of user input, the loop runs at most 50 times.
         *           This prevents DoS from unbounded iteration.
         * WHY CXQL FAILS: CxQL does not recognize ternary operator as a bounding mechanism.
         *                 It sees user input flowing to loop condition but misses the upper bound.
         * CXQL LIMITATION: Ternary operator not recognized as loop bound sanitizer.
         */
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

        /*
         * #L02 - FALSE POSITIVE: Modulo operator bounds the loop
         * WHY SAFE: input % MaxItems always produces result in range [0, MaxItems-1].
         *           Maximum loop iterations = 99, regardless of user input value.
         *           This is mathematically guaranteed bounding.
         * WHY CXQL FAILS: CxQL does not analyze modulo operation for bounding effect.
         *                 It cannot determine that % MaxItems limits the range.
         * CXQL LIMITATION: Modulo not recognized as loop bound mechanism.
         */
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

        /*
         * #L03 - FALSE POSITIVE: Bitwise AND bounds the loop
         * WHY SAFE: input & 0x3F masks to 6 bits, max value 63 (binary 111111).
         *           Regardless of user input, loop runs at most 63 times.
         *           Bitwise AND with constant is a deterministic bound.
         * WHY CXQL FAILS: CxQL does not analyze bitwise operations for bounding.
         *                 It cannot determine that & 0x3F limits the value range.
         * CXQL LIMITATION: Bitwise AND not recognized as loop bound.
         */
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

        /*
         * #L04 - FALSE POSITIVE: Bit shift with AND bounds the loop
         * WHY SAFE: (unsigned >> 4) & 0x0F: shift right 4 bits, then mask to 4 bits.
         *           Maximum result is 15 (binary 1111). Loop runs at most 15 times.
         * WHY CXQL FAILS: CxQL does not analyze compound bitwise expressions.
         *                 It cannot determine the maximum value from shift+mask pattern.
         * CXQL LIMITATION: Bit shift operations not analyzed for bounds.
         */
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

        /*
         * #L05 - FALSE POSITIVE: Enum value bounds the loop
         * WHY SAFE: PageSize.Parse() only accepts Small(10), Medium(25), Large(50).
         *           Invalid input throws exception before reaching loop.
         *           Maximum iterations = 50 (Large enum value).
         * WHY CXQL FAILS: CxQL cannot analyze enum validation for loop bounding.
         *                 It doesn't recognize that (int)enum returns compile-time constants.
         * CXQL LIMITATION: Enum values not recognized as bounded source.
         */
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

        /*
         * #L06 - FALSE POSITIVE: Modulo by enum count bounds the loop
         * WHY SAFE: Enum.GetValues().Length = 3 (compile-time constant).
         *           input % 3 always produces [0, 1, 2]. Loop runs at most 2 times.
         * WHY CXQL FAILS: CxQL cannot determine that GetValues().Length is a constant.
         *                 It doesn't analyze enum array length as bound.
         * CXQL LIMITATION: Enum.GetValues().Length not recognized as constant bound.
         */
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

        /*
         * #L07 - FALSE POSITIVE: Array length with Math.Min bound
         * WHY SAFE: Math.Min(parts.Length, MaxRows) caps at MaxRows (25).
         *           Even large input arrays only iterate 25 times.
         * WHY CXQL FAILS: CxQL should recognize Math.Min, but may miss it
         *                 when combined with array length from user input.
         * CXQL LIMITATION: Math.Min with array length may not be fully analyzed.
         */
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

        /*
         * #L08 - FALSE POSITIVE: String length with Math.Min bound
         * WHY SAFE: Math.Min(text.Length, MaxItems) caps iterations at 100.
         *           Even very long strings only process first 100 characters.
         * WHY CXQL FAILS: CxQL SHOULD recognize Math.Min as a bound.
         *                 If flagged, may be issue with string length analysis.
         * CXQL LIMITATION: Math.Min with String.Length may not be fully analyzed.
         */
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

        /*
         * #L09 - FALSE POSITIVE: Nested ternary clamps to valid range
         * WHY SAFE: input < 0 ? 0 : (input > MaxItems ? MaxItems : input)
         *           This clamps value to [0, MaxItems] range. Max iterations = 100.
         *           Negative inputs become 0, values > 100 become 100.
         * WHY CXQL FAILS: CxQL cannot analyze nested ternary expressions.
         *                 It doesn't recognize the clamp pattern formed by nested ternary.
         * CXQL LIMITATION: Nested ternary clamping not recognized as bounds.
         */
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

        /*
         * #L10 - FALSE POSITIVE: Compound modulo guarantees small range
         * WHY SAFE: (input % 1000) % MaxItems - double modulo narrows range.
         *           First % 1000 limits to [0,999], then % 100 limits to [0,99].
         *           Maximum loop iterations = 99.
         * WHY CXQL FAILS: CxQL does not analyze compound arithmetic expressions.
         *                 It cannot track how chained operations constrain values.
         * CXQL LIMITATION: Compound modulo operations not analyzed.
         */
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

        /*
         * #L11 - FALSE POSITIVE: Math.Max/Min clamp pattern
         * WHY SAFE: Math.Max(0, Math.Min(input, MaxRows)) clamps to [0, 25].
         *           This is equivalent to Math.Clamp(input, 0, MaxRows).
         *           Maximum loop iterations = 25.
         * WHY CXQL FAILS: CxQL SHOULD recognize this pattern (Math.Min/Max).
         *                 If flagged, the nesting may confuse analysis.
         * CXQL LIMITATION: Nested Math.Max(Math.Min()) clamp may not be recognized.
         */
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

        /*
         * #L12 - FALSE POSITIVE: XOR then AND limits to 5 bits
         * WHY SAFE: (input ^ 0xFF) & 0x1F - XOR doesn't increase bits, AND masks to 5 bits.
         *           Maximum value = 31 (binary 11111). Loop runs at most 31 times.
         * WHY CXQL FAILS: CxQL cannot analyze compound bitwise operations.
         *                 XOR is not typically analyzed for bounding effects.
         * CXQL LIMITATION: XOR with AND not recognized as bound pattern.
         */
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

        /*
         * #L13 - FALSE POSITIVE: Division reduces magnitude, then Math.Min caps
         * WHY SAFE: Math.Abs(input) / 100 shrinks value by factor of 100.
         *           Math.Min(bounded, MaxRows) then caps at 25.
         *           Even Int32.MaxValue / 100 = 21M, but Min caps to 25.
         * WHY CXQL FAILS: CxQL may not recognize division as magnitude reduction.
         *                 Combined with Math.Min should be safe but may not be tracked.
         * CXQL LIMITATION: Division with subsequent Math.Min not fully analyzed.
         */
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

        /*
         * #L14 - FALSE POSITIVE: Absolute value with modulo bounds the loop
         * WHY SAFE: Math.Abs() makes value non-negative, % MaxItems limits to [0,99].
         *           Maximum loop iterations = 99.
         * WHY CXQL FAILS: CxQL does not recognize Math.Abs() + modulo pattern.
         *                 It cannot determine that combined operations bound the value.
         * CXQL LIMITATION: Math.Abs with modulo not analyzed together.
         */
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

        /*
         * #L15 - FALSE POSITIVE: Math.Min simulates LINQ Take bound
         * WHY SAFE: Math.Min(input, MaxRows) caps at MaxRows (25).
         *           This is the common "take first N" pattern.
         *           Maximum loop iterations = 25.
         * WHY CXQL FAILS: CxQL SHOULD recognize Math.Min as bound.
         *                 If flagged, may be C# configuration issue.
         * CXQL LIMITATION: Math.Min should be recognized; may be C# query issue.
         */
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

