using System;
using System.Web;
using System.Web.UI;

namespace Checkmarx.Handlers.Web
{
    public class RequestHandler : Page
    {
        public enum AccessLevel { Admin, User, Guest }

        // RX-MR:01
        protected void Scenario01()
        {
            string param = Request.QueryString["age"];
            int value = int.Parse(param);
            Response.Write("Age: " + value);
        }

        // RX-MR:02
        protected void Scenario02()
        {
            string param = Request.QueryString["id"];
            long value = long.Parse(param);
            Response.Write("ID: " + value);
        }

        // RX-MR:03
        protected void Scenario03()
        {
            string param = Request.QueryString["flag"];
            bool value = bool.Parse(param);
            Response.Write("Flag: " + value);
        }

        // RX-MR:04
        protected void Scenario04()
        {
            string param = Request.QueryString["guid"];
            if (Guid.TryParse(param, out Guid value))
            {
                Response.Write("GUID: " + value.ToString());
            }
        }

        // RX-MR:05
        protected void Scenario05()
        {
            string param = Request.QueryString["role"];
            if (Enum.TryParse<AccessLevel>(param, true, out AccessLevel value))
            {
                Response.Write("Role: " + value.ToString());
            }
        }

        // RX-BR:06
        protected void Scenario06()
        {
            string param = Request.QueryString["name"];
            Response.Write("Name: " + param);
        }

        // RX-BR:07
        protected void Scenario07()
        {
            string param = Request.Form["comment"];
            Response.Write("Comment: " + param);
        }

        // RX-BR:08
        protected void Scenario08()
        {
            string param = Request.QueryString["title"];
            Response.Write("<div title='" + param + "'>Content</div>");
        }

        // RX-BR:09
        protected void Scenario09()
        {
            string param = Request.QueryString["userName"];
            Response.Write("<script>var user = '" + param + "';</script>");
        }

        // RX-BR:10
        protected void Scenario10()
        {
            HttpCookie cookie = Request.Cookies["userPref"];
            if (cookie != null)
            {
                Response.Write("Preference: " + cookie.Value);
            }
        }

        // RX-BR:11
        protected void Scenario11()
        {
            string param = Request.Headers["Referer"];
            Response.Write("Came from: " + param);
        }

        // RX-BR:12
        protected void Scenario12()
        {
            string param = Request.PathInfo;
            Response.Write("Path: " + param);
        }

        // RX-MR:13 + RX-BR:14
        protected void Scenario13()
        {
            string idParam = Request.QueryString["id"];
            long id = long.Parse(idParam);
            Response.Write("ID: " + id);
            
            string name = Request.QueryString["name"];
            Response.Write(", Name: " + name);
        }
    }
}

