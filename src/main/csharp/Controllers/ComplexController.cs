using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Mvc;
using App.Core;
using App.Data;
using App.Models;

namespace App.Controllers
{
    public class ComplexController : Controller
    {
        private readonly EntityRepository _entityRepo;
        private readonly AccountRepository _accountRepo;
        
        public ComplexController(EntityRepository entityRepo, AccountRepository accountRepo)
        {
            _entityRepo = entityRepo;
            _accountRepo = accountRepo;
        }
        
        // #C01 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (numeric)
        public ActionResult DeepFlowNumeric(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            long outputId = Processor.ExtractEntityId(entity);
            return Content($"<span>{outputId}</span>", "text/html");
        }
        
        // #C02 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (status)
        public ActionResult DeepFlowStatus(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            int status = Processor.ExtractEntityStatus(entity);
            return Content($"<div class=\"status-{status}\">{status}</div>", "text/html");
        }
        
        // #C03 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (boolean)
        public ActionResult DeepFlowBoolean(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            bool active = Processor.ExtractEntityActive(entity);
            return Content($"<input type=\"checkbox\" {(active ? "checked" : "")}/>");
        }
        
        // #C04 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (double)
        public ActionResult DeepFlowDouble(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            double balance = Processor.ExtractEntityBalance(entity);
            return Content($"<span>${balance:F2}</span>", "text/html");
        }
        
        // #C05 - Collection flow: Repository -> List -> Processor.FirstOrNull -> Id
        public ActionResult CollectionFlowId()
        {
            var entities = _entityRepo.FindAll();
            long id = Processor.GetIdFromFirst(entities);
            return Content($"<span>{id}</span>", "text/html");
        }
        
        // #C06 - Collection flow: Repository -> List -> Processor.FirstOrNull -> Status
        public ActionResult CollectionFlowStatus()
        {
            var entities = _entityRepo.FindAll();
            int status = Processor.GetStatusFromFirst(entities);
            return Content($"<span>{status}</span>", "text/html");
        }
        
        // #C07 - Generic extraction: Repository -> List -> Transformer.ExtractIds -> first
        public ActionResult GenericExtractionId()
        {
            var entities = _entityRepo.FindAll();
            var ids = entities.Select(e => e.Id).ToList();
            var firstId = ids.Count > 0 ? ids[0] : 0L;
            return Content($"<span>{firstId}</span>", "text/html");
        }
        
        // #C08 - Account masked SSN through processor
        public ActionResult AccountMaskedSsn(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            string masked = Processor.ExtractMaskedSsn(account);
            Console.WriteLine("SSN: " + masked);
            return new EmptyResult();
        }
        
        // #C09 - Account tier through processor
        public ActionResult AccountTier(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            int tier = Processor.ExtractAccountTier(account);
            return Content($"<span>Tier: {tier}</span>", "text/html");
        }
        
        // #C10 - Account verified through processor
        public ActionResult AccountVerified(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var account = _accountRepo.FindById(inputId);
            bool verified = Processor.ExtractAccountVerified(account);
            return Content($"<span>{(verified ? "Yes" : "No")}</span>", "text/html");
        }
        
        // #C11 - Lambda processing: Request -> Process(input, int.Parse) -> Output
        public ActionResult LambdaProcessing(string value)
        {
            int result = Processor.Process(value, int.Parse);
            return Content($"<span>{result}</span>", "text/html");
        }
        
        // #C12 - Chained method calls: entity.Code returns int derived from id
        public ActionResult ChainedMethods(string id)
        {
            long inputId = Transformer.StringToLong(id);
            var entity = _entityRepo.FindById(inputId);
            int code = entity?.Code ?? 0;
            return Content($"<code>{code}</code>", "text/html");
        }
    }
}

