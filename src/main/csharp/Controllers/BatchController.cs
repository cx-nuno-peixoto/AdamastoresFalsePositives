using System;
using System.Text;
using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    public class BatchController : Controller
    {
        private readonly EntityService _entityService;
        private const int MaxItems = 100;
        private const int MaxPages = 50;
        
        public BatchController(EntityService entityService)
        {
            _entityService = entityService;
        }
        
        // #L01 - Input bounded by Math.Min through utility
        public ActionResult ProcessItems(string count)
        {
            int bounded = Sanitizer.Bound(Sanitizer.ToInt(count), MaxItems);
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<li>Item {i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L02 - Input bounded by ternary with constant
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
        
        // #L03 - Input bounded by if-guard
        public ActionResult ProcessRecords(string records)
        {
            int input = Sanitizer.ToInt(records);
            if (input > MaxItems) input = MaxItems;
            if (input < 0) input = 0;
            var sb = new StringBuilder();
            for (int i = 0; i < input; i++)
            {
                sb.Append($"<tr><td>{i}</td></tr>");
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L04 - Input bounded by loop condition
        public ActionResult ProcessEntries(string entries)
        {
            int input = Sanitizer.ToInt(entries);
            var sb = new StringBuilder();
            for (int i = 0; i < input && i < MaxItems; i++)
            {
                sb.Append($"<p>Entry {i}</p>");
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L05 - Input bounded by nested Math.Min
        public ActionResult ProcessNested(string outer, string inner)
        {
            int outerVal = Math.Min(Sanitizer.ToInt(outer), 10);
            int innerVal = Math.Min(Sanitizer.ToInt(inner), 10);
            var sb = new StringBuilder();
            for (int i = 0; i < outerVal; i++)
            {
                for (int j = 0; j < innerVal; j++)
                {
                    sb.Append($"<span>{i},{j}</span>");
                }
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L06 - DB count bounded by constant
        public ActionResult ProcessDbCount()
        {
            int dbCount = _entityService.GetAllEntityIds().Count;
            int bounded = Math.Min(dbCount, MaxItems);
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<li>DB Item {i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L07 - Regex validated numeric input
        public ActionResult ProcessValidated(string count)
        {
            var sb = new StringBuilder();
            if (Sanitizer.IsNumeric(count))
            {
                int bounded = Math.Min(int.Parse(count), MaxItems);
                for (int i = 0; i < bounded; i++)
                {
                    sb.Append($"<div>{i}</div>");
                }
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L08 - Extracted numeric from mixed input
        public ActionResult ProcessExtracted(string mixed)
        {
            string numeric = Sanitizer.ExtractNumeric(mixed);
            int bounded = Math.Min(int.Parse(numeric), MaxItems);
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }
        
        // #L09 - Modulo bounded
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
        
        // #L10 - Bitwise AND bounded
        public ActionResult ProcessBitwise(string value)
        {
            int input = Sanitizer.ToInt(value);
            int bounded = input & 0x3F;
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<span>{i}</span>");
            }
            return Content(sb.ToString(), "text/html");
        }
    }
}

