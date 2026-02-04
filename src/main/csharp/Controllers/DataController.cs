using System;
using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    public class DataController : Controller
    {
        private readonly AccountService _accountService;
        
        public DataController(AccountService accountService)
        {
            _accountService = accountService;
        }
        
        // #P01 - Masked SSN through service layer (privacy safe)
        public ActionResult DisplayMaskedSsn(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string masked = _accountService.GetMaskedSsn(inputId);
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }
        
        // #P02 - Masked account number through service layer
        public ActionResult DisplayMaskedAccount(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string masked = _accountService.GetMaskedAccountNumber(inputId);
            Console.WriteLine("Account: " + masked);
            return new EmptyResult();
        }
        
        // #P03 - SSN checksum (int) through service layer
        public ActionResult DisplaySsnChecksum(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            int checksum = _accountService.GetSsnChecksum(inputId);
            Console.WriteLine("Checksum: " + checksum);
            return new EmptyResult();
        }
        
        // #P04 - Account ID (long) through service layer
        public ActionResult DisplayAccountId(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            long accountId = _accountService.GetAccountId(inputId);
            Console.WriteLine("Account ID: " + accountId);
            return new EmptyResult();
        }
        
        // #P05 - Account tier (int) through service layer
        public ActionResult DisplayAccountTier(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            int tier = _accountService.GetAccountTier(inputId);
            Console.WriteLine("Tier: " + tier);
            return new EmptyResult();
        }
        
        // #P06 - Account verified (boolean) through service layer
        public ActionResult DisplayAccountVerified(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            bool verified = _accountService.IsAccountVerified(inputId);
            Console.WriteLine("Verified: " + verified);
            return new EmptyResult();
        }
        
        // #P07 - All account IDs (List<long>) through service layer
        public ActionResult DisplayAllAccountIds()
        {
            var ids = _accountService.GetAllAccountIds();
            foreach (var accountId in ids)
            {
                Console.WriteLine("ID: " + accountId);
            }
            return new EmptyResult();
        }
        
        // #P08 - All account tiers (List<int>) through service layer
        public ActionResult DisplayAllAccountTiers()
        {
            var tiers = _accountService.GetAllAccountTiers();
            foreach (var tier in tiers)
            {
                Console.WriteLine("Tier: " + tier);
            }
            return new EmptyResult();
        }
        
        // #P09 - All masked SSNs through service layer
        public ActionResult DisplayAllMaskedSsns()
        {
            var maskedSsns = _accountService.GetAllMaskedSsns();
            foreach (var masked in maskedSsns)
            {
                Console.WriteLine("SSN: " + masked);
            }
            return new EmptyResult();
        }
        
        // #P10 - Constant field name with PII variable name
        public ActionResult DisplayFieldName()
        {
            string password = "password";
            string ssn = "ssn";
            Console.WriteLine("Required fields: " + password + ", " + ssn);
            return new EmptyResult();
        }
        
        // #P11 - Format requirements (not actual PII)
        public ActionResult DisplayFormatRequirements()
        {
            string ssnFormat = "XXX-XX-XXXX";
            string passwordFormat = "8+ chars, 1 uppercase, 1 number";
            Console.WriteLine("SSN format: " + ssnFormat);
            Console.WriteLine("Password requirements: " + passwordFormat);
            return new EmptyResult();
        }
        
        // #P12 - Validation result message
        public ActionResult DisplayValidationResult(string password)
        {
            bool valid = !string.IsNullOrEmpty(password) && password.Length >= 8;
            Console.WriteLine("Password validation: " + (valid ? "passed" : "failed"));
            return new EmptyResult();
        }
    }
}

