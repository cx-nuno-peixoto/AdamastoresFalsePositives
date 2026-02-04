using System.Collections.Generic;
using System.Linq;
using App.Core;
using App.Data;
using App.Models;

namespace App.Services
{
    public class EntityService
    {
        private readonly EntityRepository _repository;
        
        public EntityService(EntityRepository repository)
        {
            _repository = repository;
        }
        
        public long GetEntityId(long id)
        {
            var entity = _repository.FindById(id);
            return entity?.Id ?? 0L;
        }
        
        public int GetEntityStatus(long id)
        {
            var entity = _repository.FindById(id);
            return entity?.Status ?? -1;
        }
        
        public bool IsEntityActive(long id)
        {
            var entity = _repository.FindById(id);
            return entity?.Active ?? false;
        }
        
        public double GetEntityBalance(long id)
        {
            var entity = _repository.FindById(id);
            return entity?.Balance ?? 0.0;
        }
        
        public int GetEntityCode(long id)
        {
            var entity = _repository.FindById(id);
            return entity?.Code ?? 0;
        }
        
        public List<long> GetAllEntityIds()
        {
            return _repository.FindAll().Select(e => e.Id).ToList();
        }
        
        public List<int> GetAllEntityStatuses()
        {
            return _repository.FindAll().Select(e => e.Status).ToList();
        }
        
        public string GetEscapedName(long id)
        {
            var entity = _repository.FindById(id);
            return entity != null ? Sanitizer.EscapeHtml(entity.Name) : "";
        }
        
        public int GetFirstEntityStatus()
        {
            var entities = _repository.FindAll();
            return entities.Count == 0 ? -1 : entities[0].Status;
        }
        
        public long GetFirstEntityId()
        {
            var entities = _repository.FindAll();
            return entities.Count == 0 ? 0L : entities[0].Id;
        }
    }
}

