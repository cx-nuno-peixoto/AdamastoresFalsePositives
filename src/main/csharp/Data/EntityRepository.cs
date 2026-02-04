using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using App.Models;

namespace App.Data
{
    public class EntityRepository
    {
        private readonly SqlConnection _connection;
        
        public EntityRepository(SqlConnection connection)
        {
            _connection = connection;
        }
        
        public Entity FindById(long id)
        {
            using (var cmd = new SqlCommand("SELECT Id, Name, Email, Status, Active, Balance FROM Entities WHERE Id = @id", _connection))
            {
                cmd.Parameters.AddWithValue("@id", id);
                using (var reader = cmd.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        return new Entity
                        {
                            Id = reader.GetInt64(0),
                            Name = reader.GetString(1),
                            Email = reader.GetString(2),
                            Status = reader.GetInt32(3),
                            Active = reader.GetBoolean(4),
                            Balance = reader.GetDouble(5)
                        };
                    }
                }
            }
            return null;
        }
        
        public List<Entity> FindAll()
        {
            var entities = new List<Entity>();
            using (var cmd = new SqlCommand("SELECT Id, Name, Email, Status, Active, Balance FROM Entities", _connection))
            {
                using (var reader = cmd.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        entities.Add(new Entity
                        {
                            Id = reader.GetInt64(0),
                            Name = reader.GetString(1),
                            Email = reader.GetString(2),
                            Status = reader.GetInt32(3),
                            Active = reader.GetBoolean(4),
                            Balance = reader.GetDouble(5)
                        });
                    }
                }
            }
            return entities;
        }
        
        public List<Entity> FindByStatus(int status)
        {
            var entities = new List<Entity>();
            using (var cmd = new SqlCommand("SELECT Id, Name, Email, Status, Active, Balance FROM Entities WHERE Status = @status", _connection))
            {
                cmd.Parameters.AddWithValue("@status", status);
                using (var reader = cmd.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        entities.Add(new Entity
                        {
                            Id = reader.GetInt64(0),
                            Name = reader.GetString(1),
                            Email = reader.GetString(2),
                            Status = reader.GetInt32(3),
                            Active = reader.GetBoolean(4),
                            Balance = reader.GetDouble(5)
                        });
                    }
                }
            }
            return entities;
        }
    }
}

