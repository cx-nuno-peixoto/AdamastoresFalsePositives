using System.Web.Mvc;
using App.Core;
using App.Services;

namespace App.Controllers
{
    public class DisplayController : Controller
    {
        private readonly EntityService _entityService;
        private readonly AccountService _accountService;
        
        public DisplayController(EntityService entityService, AccountService accountService)
        {
            _entityService = entityService;
            _accountService = accountService;
        }
        
        // #X01 - DB numeric ID through service layer
        public ActionResult RenderEntityId(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            long entityId = _entityService.GetEntityId(inputId);
            return Content($"<div data-id=\"{entityId}\"></div>", "text/html");
        }
        
        // #X02 - DB status (int) through service layer
        public ActionResult RenderEntityStatus(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            int status = _entityService.GetEntityStatus(inputId);
            return Content($"<span class=\"status-{status}\">Status: {status}</span>", "text/html");
        }
        
        // #X03 - DB boolean through service layer
        public ActionResult RenderEntityActive(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            bool active = _entityService.IsEntityActive(inputId);
            return Content($"<input type=\"checkbox\" {(active ? "checked" : "")}/>");
        }
        
        // #X04 - DB balance (double) through service layer
        public ActionResult RenderEntityBalance(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            double balance = _entityService.GetEntityBalance(inputId);
            return Content($"<span>${balance:F2}</span>", "text/html");
        }
        
        // #X05 - DB derived code (int) through service layer
        public ActionResult RenderEntityCode(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            int code = _entityService.GetEntityCode(inputId);
            return Content($"<code>{code}</code>", "text/html");
        }
        
        // #X06 - DB collection of IDs through service layer
        public ActionResult RenderAllEntityIds()
        {
            var ids = _entityService.GetAllEntityIds();
            var html = string.Join("", ids.ConvertAll(id => $"<li>{id}</li>"));
            return Content(html, "text/html");
        }
        
        // #X07 - DB collection of statuses through service layer
        public ActionResult RenderAllEntityStatuses()
        {
            var statuses = _entityService.GetAllEntityStatuses();
            var html = string.Join("", statuses.ConvertAll(s => $"<option value=\"{s}\">{s}</option>"));
            return Content(html, "text/html");
        }
        
        // #X08 - DB escaped name through service layer (sanitized)
        public ActionResult RenderEscapedName(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            string name = _entityService.GetEscapedName(inputId);
            return Content($"<span>{name}</span>", "text/html");
        }
        
        // #X09 - DB first entity status from collection
        public ActionResult RenderFirstStatus()
        {
            int status = _entityService.GetFirstEntityStatus();
            return Content($"<div>{status}</div>", "text/html");
        }
        
        // #X10 - DB first entity ID from collection
        public ActionResult RenderFirstId()
        {
            long id = _entityService.GetFirstEntityId();
            return Content($"<span>{id}</span>", "text/html");
        }
        
        // #X11 - Account tier (int) through service layer
        public ActionResult RenderAccountTier(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            int tier = _accountService.GetAccountTier(inputId);
            return Content($"<div class=\"tier-{tier}\">Tier {tier}</div>", "text/html");
        }
        
        // #X12 - Account verified (boolean) through service layer
        public ActionResult RenderAccountVerified(string id)
        {
            long inputId = Sanitizer.ToLong(id);
            bool verified = _accountService.IsAccountVerified(inputId);
            return Content($"<span>{(verified ? "Verified" : "Pending")}</span>", "text/html");
        }
    }
}

