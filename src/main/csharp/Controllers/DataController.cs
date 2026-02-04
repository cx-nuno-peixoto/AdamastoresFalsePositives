using System;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    /// <summary>
    /// Privacy Violation False Positive Scenarios
    /// All scenarios are SAFE but CxQL incorrectly flags them
    /// Pattern: PII variable -> Transformation -> Output (non-sensitive)
    /// </summary>
    public class DataController : Controller
    {
        private readonly AccountService _accountService;

        public DataController(AccountService accountService)
        {
            _accountService = accountService;
        }

        // #P01 - Masked SSN (CxQL flags but data is masked)
        public ActionResult LogMaskedSsn(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            string masked = Sanitizer.Mask(ssn ?? "", 4);  // Only last 4 visible
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }

        // #P02 - Masked account number (CxQL flags but data is masked)
        public ActionResult LogMaskedAccount(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string accountNumber = _accountService.GetAccountNumber(inputId);
            string masked = Sanitizer.Mask(accountNumber ?? "", 4);
            Console.WriteLine("Account: " + masked);
            return new EmptyResult();
        }

        // #P03 - SSN checksum only (CxQL flags but value is just checksum)
        public ActionResult LogSsnChecksum(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            int checksum = ComputeChecksum(ssn);
            Console.WriteLine("SSN checksum: " + checksum);
            return new EmptyResult();
        }

        // #P04 - SSN hashed (CxQL flags but value is hashed)
        public ActionResult LogHashedSsn(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            string hashed = ComputeSha256Hash(ssn ?? "");
            Console.WriteLine("SSN hash: " + hashed);
            return new EmptyResult();
        }

        // #P05 - SSN existence check (CxQL flags but only logging bool)
        public ActionResult LogSsnExists(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            bool exists = !string.IsNullOrEmpty(ssn);
            Console.WriteLine("SSN exists: " + exists);
            return new EmptyResult();
        }

        // #P06 - SSN length (CxQL flags but only logging length)
        public ActionResult LogSsnLength(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            int length = (ssn ?? "").Length;
            Console.WriteLine("SSN length: " + length);
            return new EmptyResult();
        }

        // #P07 - SSN format validation (CxQL flags but only logging bool)
        public ActionResult LogSsnFormat(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            bool valid = Regex.IsMatch(ssn, @"^\d{3}-?\d{2}-?\d{4}$");
            Console.WriteLine("SSN valid format: " + valid);
            return new EmptyResult();
        }

        // #P08 - Password strength (CxQL flags but only logging strength)
        public ActionResult LogPasswordStrength(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string password = _accountService.GetPassword(inputId) ?? "";
            string strength = password.Length >= 12 ? "strong" :
                              password.Length >= 8 ? "medium" : "weak";
            Console.WriteLine("Password strength: " + strength);
            return new EmptyResult();
        }

        // #P09 - Password hashed (CxQL flags but value is hashed)
        public ActionResult LogHashedPassword(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string password = _accountService.GetPassword(inputId);
            string hashed = ComputeSha256Hash(password ?? "");
            Console.WriteLine("Password hash: " + hashed);
            return new EmptyResult();
        }

        // #P10 - Credit card type (CxQL flags but only logging type)
        public ActionResult LogCardType(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string card = _accountService.GetCreditCardNumber(inputId) ?? "";
            string type = card.StartsWith("4") ? "Visa" :
                          card.StartsWith("5") ? "Mastercard" :
                          card.StartsWith("3") ? "Amex" : "Unknown";
            Console.WriteLine("Card type: " + type);
            return new EmptyResult();
        }

        // #P11 - Credit card masked (CxQL flags but data is masked)
        public ActionResult LogMaskedCard(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string card = _accountService.GetCreditCardNumber(inputId);
            string masked = Sanitizer.Mask(card ?? "", 4);
            Console.WriteLine("Card: " + masked);
            return new EmptyResult();
        }

        // #P12 - SSN state code (CxQL flags but only first 3 digits)
        public ActionResult LogSsnArea(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            string area = ssn.Length >= 3 ? ssn.Substring(0, 3) : "000";
            // Area number is not PII by itself - just geographic indicator
            Console.WriteLine("SSN area: " + area);
            return new EmptyResult();
        }

        // #P13 - Email domain only (CxQL flags but only domain)
        public ActionResult LogEmailDomain(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string email = _accountService.GetAccountEmail(inputId) ?? "";
            int atIndex = email.IndexOf('@');
            string domain = atIndex >= 0 ? email.Substring(atIndex + 1) : "";
            Console.WriteLine("Email domain: " + domain);
            return new EmptyResult();
        }

        // #P14 - Phone country code (CxQL flags but only country code)
        public ActionResult LogPhoneCountry(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string phone = _accountService.GetPhone(inputId) ?? "";
            string countryCode = phone.StartsWith("+") && phone.Length > 2
                ? phone.Substring(0, 3) : "+1";
            Console.WriteLine("Phone country: " + countryCode);
            return new EmptyResult();
        }

        // #P15 - Name initials only (CxQL flags but only initials)
        public ActionResult LogInitials(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _accountService.GetFullName(inputId) ?? "";
            string[] parts = name.Split(' ');
            var sb = new StringBuilder();
            foreach (var part in parts)
            {
                if (part.Length > 0) sb.Append(part[0]);
            }
            Console.WriteLine("Initials: " + sb.ToString().ToUpper());
            return new EmptyResult();
        }

        private int ComputeChecksum(string input)
        {
            int sum = 0;
            foreach (char c in input)
            {
                if (char.IsDigit(c)) sum += (int)char.GetNumericValue(c);
            }
            return sum % 10;
        }

        private string ComputeSha256Hash(string input)
        {
            using (var sha256 = SHA256.Create())
            {
                byte[] bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(input));
                var sb = new StringBuilder();
                foreach (byte b in bytes)
                {
                    sb.Append(b.ToString("x2"));
                }
                return sb.ToString();
            }
        }
    }
}

