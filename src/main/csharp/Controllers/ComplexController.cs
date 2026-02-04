using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Mvc;
using App.Core;
using App.Data;
using App.Models;

namespace App.Controllers
{
    /// <summary>
    /// Complex Multi-Layer False Positive Scenarios
    /// All scenarios are SAFE but CxQL fails to track across multiple layers
    /// Pattern: Request -> Transformer -> Repository -> Service -> Processor -> Output
    /// </summary>
    public class ComplexController : Controller
    {
        private readonly EntityRepository _entityRepo;
        private readonly AccountRepository _accountRepo;

        public ComplexController(EntityRepository entityRepo, AccountRepository accountRepo)
        {
            _entityRepo = entityRepo;
            _accountRepo = accountRepo;
        }

        // #C01 - 5-layer XSS: Request -> Transform -> DB -> Sanitize -> Processor -> Output
        public ActionResult DeepFlowName(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string name = entity?.Name ?? "";
            string sanitized = Sanitizer.EscapeHtml(name);
            string processed = Processor.WrapInSpan(sanitized);
            return Content(processed, "text/html");
        }

        // #C02 - 5-layer XSS: Request -> Validate -> DB -> Extract -> Encode -> Output
        public ActionResult DeepFlowValidated(string id)
        {
            if (!Sanitizer.IsNumeric(id)) return new EmptyResult();
            long inputId = long.Parse(id);
            var entity = _entityRepo.FindById(inputId);
            string name = Regex.Replace(entity?.Name ?? "", @"[^a-zA-Z0-9 ]", "");
            string encoded = HttpUtility.UrlEncode(name);
            return Content($"<a href=\"?name={encoded}\">{HttpUtility.HtmlEncode(name)}</a>", "text/html");
        }

        // #C03 - Cross-file privacy: Request -> Account -> Extract SSN -> Mask -> Log
        public ActionResult DeepFlowSsnMasked(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            string ssn = account?.Ssn ?? "";
            string masked = Processor.MaskValue(ssn, 4);
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }

        // #C04 - Cross-file loop: Request -> Transform -> Bound -> Processor -> Loop
        public ActionResult DeepFlowLoop(string count)
        {
            int input = Transformer.StringToInt(count);
            int bounded = Processor.BoundValue(input, 0, 100);
            var sb = new StringBuilder();
            for (int i = 0; i < bounded; i++)
            {
                sb.Append($"<li>{i}</li>");
            }
            return Content(sb.ToString(), "text/html");
        }

        // #C05 - Lambda chain: Request -> Repo -> LINQ Select -> First -> Output
        public ActionResult LambdaChainName(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entities = _entityRepo.FindAll();
            var names = entities.Where(e => e.Id == inputId).Select(e => Sanitizer.EscapeHtml(e.Name));
            string name = names.FirstOrDefault() ?? "";
            return Content($"<span>{name}</span>", "text/html");
        }

        // #C06 - Conditional sanitization: Request -> DB -> Validate -> Conditional Output
        public ActionResult ConditionalSafe(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string type = entity?.Type ?? "";
            if (Regex.IsMatch(type, @"^[a-zA-Z]+$"))
            {
                return Content($"<span class=\"{type}\">{type}</span>", "text/html");
            }
            return Content("<span>Unknown</span>", "text/html");
        }

        // #C07 - Enum conversion chain: Request -> DB -> Enum Parse -> Output
        public ActionResult EnumChain(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string category = entity?.Category ?? "";
            if (Enum.TryParse<CategoryType>(category, true, out var cat))
            {
                return Content($"<span>{cat}</span>", "text/html");
            }
            return new EmptyResult();
        }

        // #C08 - GUID validation chain: Request -> DB -> GUID Parse -> Output
        public ActionResult GuidChain(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string uuid = entity?.Uuid ?? "";
            if (Guid.TryParse(uuid, out var guid))
            {
                return Content($"<span>{guid}</span>", "text/html");
            }
            return new EmptyResult();
        }

        // #C09 - Multi-field extraction: Request -> DB -> Multiple Fields -> Joined Output
        public ActionResult MultiFieldSafe(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            if (entity == null) return new EmptyResult();

            // All numeric/boolean fields - safe
            var output = $"ID: {entity.Id}, Status: {entity.Status}, Active: {entity.Active}";
            return Content($"<div>{output}</div>", "text/html");
        }

        // #C10 - Aggregation chain: Request -> Repo -> Aggregate -> Output
        public ActionResult AggregationChain()
        {
            var entities = _entityRepo.FindAll();
            int count = entities.Count;
            double avgBalance = entities.Count > 0 ? entities.Average(e => e.Balance) : 0;
            long maxId = entities.Count > 0 ? entities.Max(e => e.Id) : 0;
            return Content($"<span>Count: {count}, Avg: {avgBalance:F2}, Max: {maxId}</span>", "text/html");
        }

        // #C11 - Hash chain: Request -> DB -> Hash -> Output
        public ActionResult HashChain(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            string ssn = account?.Ssn ?? "";
            string hashed = Processor.ComputeHash(ssn);
            return Content($"<span data-hash=\"{hashed}\"></span>", "text/html");
        }

        // #C12 - Encoding chain: Request -> DB -> Base64 -> Output
        public ActionResult EncodingChain(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string name = entity?.Name ?? "";
            string encoded = Convert.ToBase64String(Encoding.UTF8.GetBytes(name));
            return Content($"<span data-encoded=\"{encoded}\"></span>", "text/html");
        }
    }

    public enum CategoryType { Product, Service, Category, Other }
}

