namespace App.Models
{
    public class Account
    {
        public long AccountId { get; set; }
        public string AccountNumber { get; set; }
        public string Ssn { get; set; }
        public string Password { get; set; }
        public int Tier { get; set; }
        public bool Verified { get; set; }
        
        public string MaskedSsn
        {
            get
            {
                if (string.IsNullOrEmpty(Ssn) || Ssn.Length < 4) return "***-**-****";
                return "***-**-" + Ssn.Substring(Ssn.Length - 4);
            }
        }
        
        public string MaskedAccount
        {
            get
            {
                if (string.IsNullOrEmpty(AccountNumber) || AccountNumber.Length < 4) return "****";
                return "****" + AccountNumber.Substring(AccountNumber.Length - 4);
            }
        }
        
        public int SsnChecksum
        {
            get
            {
                if (string.IsNullOrEmpty(Ssn)) return 0;
                int sum = 0;
                foreach (char c in Ssn)
                {
                    if (char.IsDigit(c)) sum += (int)char.GetNumericValue(c);
                }
                return sum % 10;
            }
        }
    }
}

