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

        /*
         * #C01 - FALSE POSITIVE: 5-layer flow with sanitization
         * FLOW: Request -> Transformer.StringToLong -> DB -> Sanitizer.EscapeHtml -> Processor -> Output
         * WHY SAFE: EscapeHtml() is called on DB string before any output.
         *           Even if DB contains XSS, it's escaped before reaching Content().
         *           5 layers of indirection but sanitization is present.
         * WHY CXQL FAILS: CxQL may lose track of sanitization through multiple layers.
         *                 Cross-file data flow through Processor.WrapInSpan may confuse analysis.
         * CXQL LIMITATION: Deep cross-file flows may lose sanitizer tracking.
         */
        public ActionResult DeepFlowName(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            string name = entity?.Name ?? "";
            string sanitized = Sanitizer.EscapeHtml(name);
            string processed = Processor.WrapInSpan(sanitized);
            return Content(processed, "text/html");
        }

        /*
         * #C02 - FALSE POSITIVE: Validated input + alphanumeric extraction + double encoding
         * FLOW: Request -> IsNumeric guard -> DB -> Regex strip -> UrlEncode + HtmlEncode -> Output
         * WHY SAFE: Multiple layers of protection:
         *           1. IsNumeric() validates ID is numeric only
         *           2. Regex.Replace strips non-alphanumeric from DB content
         *           3. Both UrlEncode and HtmlEncode applied to output
         * WHY CXQL FAILS: CxQL may not track that Regex.Replace strips dangerous chars.
         *                 Multiple sanitization steps may confuse flow analysis.
         * CXQL LIMITATION: Regex sanitization not analyzed; deep flow confuses tracking.
         */
        public ActionResult DeepFlowValidated(string id)
        {
            if (!Sanitizer.IsNumeric(id)) return new EmptyResult();
            long inputId = long.Parse(id);
            var entity = _entityRepo.FindById(inputId);
            string name = Regex.Replace(entity?.Name ?? "", @"[^a-zA-Z0-9 ]", "");
            string encoded = HttpUtility.UrlEncode(name);
            return Content($"<a href=\"?name={encoded}\">{HttpUtility.HtmlEncode(name)}</a>", "text/html");
        }

        /*
         * #C03 - FALSE POSITIVE: Cross-file SSN masking
         * FLOW: Request -> Transformer -> AccountRepository -> SSN -> Processor.MaskValue -> Log
         * WHY SAFE: MaskValue(ssn, 4) replaces all but last 4 chars with asterisks.
         *           Output shows "***-**-1234" - actual SSN is hidden.
         * WHY CXQL FAILS: CxQL tracks "ssn" variable as PII through cross-file flow.
         *                 It cannot determine that Processor.MaskValue transforms to non-PII.
         * CXQL LIMITATION: Cross-file masking not recognized; PII taint persists.
         */
        public ActionResult DeepFlowSsnMasked(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            string ssn = account?.Ssn ?? "";
            string masked = Processor.MaskValue(ssn, 4);
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }

        /*
         * #C04 - FALSE POSITIVE: Cross-file loop bounding
         * FLOW: Request -> Transformer.StringToInt -> Processor.BoundValue -> Loop
         * WHY SAFE: BoundValue(input, 0, 100) clamps value to [0, 100] range.
         *           Maximum loop iterations = 100, regardless of input.
         * WHY CXQL FAILS: CxQL cannot track bounding through cross-file method call.
         *                 Processor.BoundValue is custom method not recognized as sanitizer.
         * CXQL LIMITATION: Cross-file bounding methods not recognized.
         */
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

        /*
         * #C05 - FALSE POSITIVE: LINQ lambda chain with sanitization
         * FLOW: Request -> Repo.FindAll -> LINQ Where/Select with EscapeHtml -> First -> Output
         * WHY SAFE: EscapeHtml() is called inside Select lambda before any output.
         *           Even if DB contains XSS, it's sanitized in the LINQ projection.
         * WHY CXQL FAILS: CxQL struggles with LINQ lambda expression analysis.
         *                 Sanitization inside Select() lambda may not be tracked.
         * CXQL LIMITATION: LINQ lambda sanitization not analyzed.
         */
        public ActionResult LambdaChainName(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entities = _entityRepo.FindAll();
            var names = entities.Where(e => e.Id == inputId).Select(e => Sanitizer.EscapeHtml(e.Name));
            string name = names.FirstOrDefault() ?? "";
            return Content($"<span>{name}</span>", "text/html");
        }

        /*
         * #C06 - FALSE POSITIVE: Conditional output with regex validation
         * FLOW: Request -> DB -> Regex validation guard -> Conditional output
         * WHY SAFE: Regex ^[a-zA-Z]+$ ensures only letters in output.
         *           If type contains any non-letter char (including XSS), it's rejected.
         *           Only "Unknown" default or validated type is output.
         * WHY CXQL FAILS: CxQL may not analyze the if-condition as a validation guard.
         *                 It sees DB string flowing to output without recognizing regex guard.
         * CXQL LIMITATION: Inline regex validation guards not recognized.
         */
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

        /*
         * #C07 - FALSE POSITIVE: Enum validation chain
         * FLOW: Request -> DB -> Enum.TryParse guard -> Output enum name
         * WHY SAFE: Enum.TryParse() only succeeds for exact enum constant names.
         *           If DB contains "<script>alert(1)</script>", TryParse returns false.
         *           Only predefined enum names (Product, Service, etc.) reach output.
         * WHY CXQL FAILS: CxQL cannot determine that Enum.TryParse validates values.
         *                 It sees DB string flowing to output through cross-file flow.
         * CXQL LIMITATION: Enum validation not recognized as type-safe sanitizer.
         */
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

        /*
         * #C08 - FALSE POSITIVE: GUID validation chain
         * FLOW: Request -> DB -> Guid.TryParse guard -> Output GUID
         * WHY SAFE: Guid.TryParse() only succeeds for valid GUID format.
         *           XSS payloads don't match GUID format (8-4-4-4-12 hex).
         *           Only canonical GUID strings reach output.
         * WHY CXQL FAILS: CxQL cannot analyze Guid.TryParse as validation.
         *                 It sees DB UUID string flowing to output.
         * CXQL LIMITATION: GUID parsing not recognized as format validator.
         */
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

        /*
         * #C09 - FALSE POSITIVE: Multi-field numeric/boolean output
         * FLOW: Request -> DB -> Extract Id (long), Status (int), Active (bool) -> Output
         * WHY SAFE: All fields are numeric or boolean types.
         *           Long/int/bool.ToString() cannot produce XSS payloads.
         *           No string fields from DB are included in output.
         * WHY CXQL FAILS: CxQL may flag entity object as tainted from DB.
         *                 It may not distinguish safe numeric properties from strings.
         * CXQL LIMITATION: Entity property type analysis may be incomplete.
         */
        public ActionResult MultiFieldSafe(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            if (entity == null) return new EmptyResult();

            // All numeric/boolean fields - safe
            var output = $"ID: {entity.Id}, Status: {entity.Status}, Active: {entity.Active}";
            return Content($"<div>{output}</div>", "text/html");
        }

        /*
         * #C10 - FALSE POSITIVE: Aggregation produces only numbers
         * FLOW: Request -> Repo.FindAll -> Count/Average/Max aggregation -> Output
         * WHY SAFE: Count, Average, Max return numeric values only.
         *           Aggregation functions cannot produce string content.
         *           Output is pure numeric - no XSS possible.
         * WHY CXQL FAILS: CxQL may track collection from DB as tainted.
         *                 It may not analyze that LINQ aggregation produces safe types.
         * CXQL LIMITATION: LINQ aggregation return types not analyzed.
         */
        public ActionResult AggregationChain()
        {
            var entities = _entityRepo.FindAll();
            int count = entities.Count;
            double avgBalance = entities.Count > 0 ? entities.Average(e => e.Balance) : 0;
            long maxId = entities.Count > 0 ? entities.Max(e => e.Id) : 0;
            return Content($"<span>Count: {count}, Avg: {avgBalance:F2}, Max: {maxId}</span>", "text/html");
        }

        /*
         * #C11 - FALSE POSITIVE: Cross-file hashing chain
         * FLOW: Request -> AccountRepository -> SSN -> Processor.ComputeHash -> Output
         * WHY SAFE: Processor.ComputeHash() returns SHA-256 hash string.
         *           Hash output contains only hex characters [0-9a-f].
         *           Original SSN cannot be recovered; output is safe for HTML.
         * WHY CXQL FAILS: CxQL tracks SSN variable through cross-file hash method.
         *                 It may not recognize custom hash method as sanitizer.
         * CXQL LIMITATION: Cross-file hashing not recognized; PII taint persists.
         */
        public ActionResult HashChain(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            string ssn = account?.Ssn ?? "";
            string hashed = Processor.ComputeHash(ssn);
            return Content($"<span data-hash=\"{hashed}\"></span>", "text/html");
        }

        /*
         * #C12 - FALSE POSITIVE: Base64 encoding chain
         * FLOW: Request -> DB -> Name -> Base64 encoding -> Output in data attribute
         * WHY SAFE: Base64 encoding produces only [A-Za-z0-9+/=] characters.
         *           XSS characters like < > " ' are encoded to safe Base64.
         *           Output in data attribute is safe for HTML.
         * WHY CXQL FAILS: CxQL sees DB string flowing to output.
         *                 It does not recognize Base64 encoding as sanitization.
         * CXQL LIMITATION: Base64 encoding not in XSS sanitizer list.
         */
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

