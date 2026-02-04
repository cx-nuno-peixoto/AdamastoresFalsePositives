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

        /*
         * #P01 - FALSE POSITIVE: Masked SSN shows only last 4 digits
         * WHY SAFE: Sanitizer.Mask(ssn, 4) replaces all but last 4 chars with asterisks.
         *           Output: "***-**-1234" - original SSN value is hidden.
         *           Last 4 digits alone don't uniquely identify individuals.
         * WHY CXQL FAILS: CxQL tracks the variable name "ssn" as PII source.
         *                 It cannot analyze that Mask() transforms data to non-sensitive form.
         * CXQL LIMITATION: Masking/redaction not recognized as PII transformation.
         */
        public ActionResult LogMaskedSsn(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            string masked = Sanitizer.Mask(ssn ?? "", 4);  // Only last 4 visible
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }

        /*
         * #P02 - FALSE POSITIVE: Masked account number shows only last 4 digits
         * WHY SAFE: Mask(accountNumber, 4) shows only last 4 characters.
         *           Output: "****5678" - account number is protected.
         *           This is standard banking display format (safe for receipts, etc.).
         * WHY CXQL FAILS: CxQL tracks "accountNumber" as PII source.
         *                 It cannot determine that masking protects the data.
         * CXQL LIMITATION: Custom masking methods not in sanitizer list.
         */
        public ActionResult LogMaskedAccount(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string accountNumber = _accountService.GetAccountNumber(inputId);
            string masked = Sanitizer.Mask(accountNumber ?? "", 4);
            Console.WriteLine("Account: " + masked);
            return new EmptyResult();
        }

        /*
         * #P03 - FALSE POSITIVE: Only SSN checksum is logged
         * WHY SAFE: ComputeChecksum() returns sum of digits mod 10 (single digit 0-9).
         *           Original SSN cannot be reconstructed from checksum.
         *           Multiple SSNs produce same checksum - no unique identification.
         * WHY CXQL FAILS: CxQL sees data flow from SSN variable to output.
         *                 It cannot analyze that checksum is one-way, lossy transformation.
         * CXQL LIMITATION: Derived values (checksums) not recognized as safe.
         */
        public ActionResult LogSsnChecksum(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            int checksum = ComputeChecksum(ssn);
            Console.WriteLine("SSN checksum: " + checksum);
            return new EmptyResult();
        }

        /*
         * #P04 - FALSE POSITIVE: Only SSN hash is logged
         * WHY SAFE: SHA-256 hash is one-way cryptographic function.
         *           Original SSN cannot be recovered from hash (without brute force).
         *           Hash is standard for storing sensitive data references.
         * WHY CXQL FAILS: CxQL tracks data flow from SSN to hash to output.
         *                 It cannot determine that hashing destroys original value.
         * CXQL LIMITATION: Cryptographic hashing not recognized as PII protection.
         */
        public ActionResult LogHashedSsn(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            string hashed = ComputeSha256Hash(ssn ?? "");
            Console.WriteLine("SSN hash: " + hashed);
            return new EmptyResult();
        }

        /*
         * #P05 - FALSE POSITIVE: Only boolean existence check logged
         * WHY SAFE: Output is only true/false - no SSN value exposed.
         *           "SSN exists: true" reveals nothing about the actual SSN.
         *           Boolean derived from string is complete information loss.
         * WHY CXQL FAILS: CxQL sees SSN variable used in condition.
         *                 It cannot determine that only boolean result is output.
         * CXQL LIMITATION: Boolean derivation from PII not recognized as safe.
         */
        public ActionResult LogSsnExists(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            bool exists = !string.IsNullOrEmpty(ssn);
            Console.WriteLine("SSN exists: " + exists);
            return new EmptyResult();
        }

        /*
         * #P06 - FALSE POSITIVE: Only SSN length is logged
         * WHY SAFE: Output is just a number (typically 9, 11 with dashes).
         *           "SSN length: 11" reveals nothing about actual SSN value.
         *           Length is non-identifying metadata.
         * WHY CXQL FAILS: CxQL tracks SSN variable to .Length property.
         *                 It cannot determine that Length output is non-sensitive.
         * CXQL LIMITATION: String length property not recognized as safe derivation.
         */
        public ActionResult LogSsnLength(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId);
            int length = (ssn ?? "").Length;
            Console.WriteLine("SSN length: " + length);
            return new EmptyResult();
        }

        /*
         * #P07 - FALSE POSITIVE: Only format validation result logged
         * WHY SAFE: Regex.IsMatch() returns only true/false.
         *           "SSN valid format: true" reveals SSN matches pattern, not its value.
         *           No PII is exposed - just format compliance status.
         * WHY CXQL FAILS: CxQL sees SSN used in Regex.IsMatch().
         *                 It cannot determine that only boolean result is logged.
         * CXQL LIMITATION: Regex validation result not recognized as safe.
         */
        public ActionResult LogSsnFormat(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            bool valid = Regex.IsMatch(ssn, @"^\d{3}-?\d{2}-?\d{4}$");
            Console.WriteLine("SSN valid format: " + valid);
            return new EmptyResult();
        }

        /*
         * #P08 - FALSE POSITIVE: Only password strength category logged
         * WHY SAFE: Output is "strong", "medium", or "weak" - not the password.
         *           Strength derived from length ranges, not password content.
         *           No way to determine password from strength category.
         * WHY CXQL FAILS: CxQL tracks password variable to length comparison.
         *                 It cannot determine that only category string is output.
         * CXQL LIMITATION: Derived category from PII not recognized as safe.
         */
        public ActionResult LogPasswordStrength(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string password = _accountService.GetPassword(inputId) ?? "";
            string strength = password.Length >= 12 ? "strong" :
                              password.Length >= 8 ? "medium" : "weak";
            Console.WriteLine("Password strength: " + strength);
            return new EmptyResult();
        }

        /*
         * #P09 - FALSE POSITIVE: Only password hash is logged
         * WHY SAFE: SHA-256 hash is one-way cryptographic function.
         *           Original password cannot be recovered from hash.
         *           This is standard secure password handling.
         * WHY CXQL FAILS: CxQL tracks password variable through hash function.
         *                 It cannot determine that hashing protects the value.
         * CXQL LIMITATION: Cryptographic hashing not recognized as sanitizer.
         */
        public ActionResult LogHashedPassword(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string password = _accountService.GetPassword(inputId);
            string hashed = ComputeSha256Hash(password ?? "");
            Console.WriteLine("Password hash: " + hashed);
            return new EmptyResult();
        }

        /*
         * #P10 - FALSE POSITIVE: Only credit card type/brand logged
         * WHY SAFE: Output is "Visa", "Mastercard", "Amex", or "Unknown".
         *           First digit determines card network - not sensitive info.
         *           No actual card number is exposed.
         * WHY CXQL FAILS: CxQL sees card number used in StartsWith() checks.
         *                 It cannot determine that only brand name is output.
         * CXQL LIMITATION: Derived category from first character not recognized.
         */
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

        /*
         * #P11 - FALSE POSITIVE: Masked credit card shows only last 4
         * WHY SAFE: Mask(card, 4) shows only last 4 digits: "****1234".
         *           This is PCI-DSS compliant display format.
         *           Standard for receipts, account pages, confirmation emails.
         * WHY CXQL FAILS: CxQL tracks creditCardNumber variable to output.
         *                 It cannot analyze that Mask() protects the value.
         * CXQL LIMITATION: Custom masking not recognized as PCI-safe transformation.
         */
        public ActionResult LogMaskedCard(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string card = _accountService.GetCreditCardNumber(inputId);
            string masked = Sanitizer.Mask(card ?? "", 4);
            Console.WriteLine("Card: " + masked);
            return new EmptyResult();
        }

        /*
         * #P12 - FALSE POSITIVE: SSN area code (first 3 digits) is not PII
         * WHY SAFE: SSN area number indicates geographic region of issuance.
         *           It's assigned to geographic areas, not individuals.
         *           Multiple people share same area code - not identifying.
         * WHY CXQL FAILS: CxQL sees SSN variable with Substring() extraction.
         *                 It cannot determine that first 3 digits are non-PII.
         * CXQL LIMITATION: SSN structure semantics not understood.
         */
        public ActionResult LogSsnArea(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string ssn = _accountService.GetSsn(inputId) ?? "";
            string area = ssn.Length >= 3 ? ssn.Substring(0, 3) : "000";
            // Area number is not PII by itself - just geographic indicator
            Console.WriteLine("SSN area: " + area);
            return new EmptyResult();
        }

        /*
         * #P13 - FALSE POSITIVE: Only email domain logged
         * WHY SAFE: Domain extraction removes username portion.
         *           "gmail.com" is not PII - it's the email provider.
         *           Millions of users share same domain.
         * WHY CXQL FAILS: CxQL sees email variable with Substring after '@'.
         *                 It cannot determine that domain alone is not PII.
         * CXQL LIMITATION: Email domain extraction not recognized as safe.
         */
        public ActionResult LogEmailDomain(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string email = _accountService.GetAccountEmail(inputId) ?? "";
            int atIndex = email.IndexOf('@');
            string domain = atIndex >= 0 ? email.Substring(atIndex + 1) : "";
            Console.WriteLine("Email domain: " + domain);
            return new EmptyResult();
        }

        /*
         * #P14 - FALSE POSITIVE: Only phone country code logged
         * WHY SAFE: Country code "+1" indicates country (USA/Canada).
         *           It's shared by all phone numbers in that country.
         *           No personal phone number is exposed.
         * WHY CXQL FAILS: CxQL sees phone variable with Substring extraction.
         *                 It cannot determine that country code is non-PII.
         * CXQL LIMITATION: Phone number structure semantics not understood.
         */
        public ActionResult LogPhoneCountry(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string phone = _accountService.GetPhone(inputId) ?? "";
            string countryCode = phone.StartsWith("+") && phone.Length > 2
                ? phone.Substring(0, 3) : "+1";
            Console.WriteLine("Phone country: " + countryCode);
            return new EmptyResult();
        }

        /*
         * #P15 - FALSE POSITIVE: Only name initials logged
         * WHY SAFE: Initials "JS" from "John Smith" is highly ambiguous.
         *           Many people share same initials - not identifying.
         *           Common practice for privacy-preserving display.
         * WHY CXQL FAILS: CxQL sees fullName variable processed to initials.
         *                 It cannot determine that initials are non-identifying.
         * CXQL LIMITATION: Initial extraction not recognized as anonymization.
         */
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

