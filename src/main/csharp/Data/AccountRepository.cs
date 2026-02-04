using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using App.Models;

namespace App.Data
{
    public class AccountRepository
    {
        private readonly SqlConnection _connection;
        
        public AccountRepository(SqlConnection connection)
        {
            _connection = connection;
        }
        
        public Account FindById(long id)
        {
            using (var cmd = new SqlCommand("SELECT AccountId, AccountNumber, Ssn, Password, Tier, Verified FROM Accounts WHERE AccountId = @id", _connection))
            {
                cmd.Parameters.AddWithValue("@id", id);
                using (var reader = cmd.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        return new Account
                        {
                            AccountId = reader.GetInt64(0),
                            AccountNumber = reader.GetString(1),
                            Ssn = reader.GetString(2),
                            Password = reader.GetString(3),
                            Tier = reader.GetInt32(4),
                            Verified = reader.GetBoolean(5)
                        };
                    }
                }
            }
            return null;
        }
        
        public List<Account> FindAll()
        {
            var accounts = new List<Account>();
            using (var cmd = new SqlCommand("SELECT AccountId, AccountNumber, Ssn, Password, Tier, Verified FROM Accounts", _connection))
            {
                using (var reader = cmd.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        accounts.Add(new Account
                        {
                            AccountId = reader.GetInt64(0),
                            AccountNumber = reader.GetString(1),
                            Ssn = reader.GetString(2),
                            Password = reader.GetString(3),
                            Tier = reader.GetInt32(4),
                            Verified = reader.GetBoolean(5)
                        });
                    }
                }
            }
            return accounts;
        }
    }
}

