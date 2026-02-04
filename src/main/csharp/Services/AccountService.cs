using System.Collections.Generic;
using System.Linq;
using App.Data;
using App.Models;

namespace App.Services
{
    public class AccountService
    {
        private readonly AccountRepository _repository;
        
        public AccountService(AccountRepository repository)
        {
            _repository = repository;
        }
        
        public long GetAccountId(long id)
        {
            var account = _repository.FindById(id);
            return account?.AccountId ?? 0L;
        }
        
        public int GetAccountTier(long id)
        {
            var account = _repository.FindById(id);
            return account?.Tier ?? 0;
        }
        
        public bool IsAccountVerified(long id)
        {
            var account = _repository.FindById(id);
            return account?.Verified ?? false;
        }
        
        public string GetMaskedSsn(long id)
        {
            var account = _repository.FindById(id);
            return account?.MaskedSsn ?? "***-**-****";
        }
        
        public string GetMaskedAccountNumber(long id)
        {
            var account = _repository.FindById(id);
            return account?.MaskedAccount ?? "****";
        }
        
        public int GetSsnChecksum(long id)
        {
            var account = _repository.FindById(id);
            return account?.SsnChecksum ?? 0;
        }
        
        public List<long> GetAllAccountIds()
        {
            return _repository.FindAll().Select(a => a.AccountId).ToList();
        }
        
        public List<int> GetAllAccountTiers()
        {
            return _repository.FindAll().Select(a => a.Tier).ToList();
        }
        
        public List<string> GetAllMaskedSsns()
        {
            return _repository.FindAll().Select(a => a.MaskedSsn).ToList();
        }

        // New methods for DataController scenarios
        public string GetSsn(long id)
        {
            var account = _repository.FindById(id);
            return account?.Ssn ?? "";
        }

        public string GetAccountNumber(long id)
        {
            var account = _repository.FindById(id);
            return account?.AccountNumber ?? "";
        }

        public string GetPassword(long id)
        {
            var account = _repository.FindById(id);
            return account?.Password ?? "";
        }

        public string GetCreditCardNumber(long id)
        {
            var account = _repository.FindById(id);
            return account?.CreditCardNumber ?? "";
        }

        public string GetAccountEmail(long id)
        {
            var account = _repository.FindById(id);
            return account?.Email ?? "";
        }

        public string GetPhone(long id)
        {
            var account = _repository.FindById(id);
            return account?.Phone ?? "";
        }

        public string GetFullName(long id)
        {
            var account = _repository.FindById(id);
            return account?.FullName ?? "";
        }

        public string GetAccountName(long id)
        {
            var account = _repository.FindById(id);
            return account?.AccountName ?? "";
        }
    }
}

