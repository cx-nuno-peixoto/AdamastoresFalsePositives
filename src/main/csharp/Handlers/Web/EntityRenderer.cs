using System;
using System.Web;
using System.Web.UI;
using System.Collections.Generic;

namespace Checkmarx.Handlers.Web
{
    public class EntityRenderer : Page
    {
        public class Entity
        {
            public long Id { get; set; }
            public string Name { get; set; }
            public string Email { get; set; }
            public int Count { get; set; }
            public bool IsActive { get; set; }
        }

        // SX-MR:01
        protected void Scenario01(List<Entity> items)
        {
            long value = items[0].Id;
            Response.Write("ID: " + value);
        }

        // SX-MR:02
        protected void Scenario02(List<Entity> items)
        {
            int value = items[0].Count;
            Response.Write("Count: " + value);
        }

        // SX-MR:03
        protected void Scenario03(List<Entity> items)
        {
            bool value = items[0].IsActive;
            Response.Write("Active: " + value);
        }

        // SX-MR:04
        protected void Scenario04(List<Entity> items)
        {
            foreach (Entity item in items)
            {
                long id = item.Id;
                Response.Write("<li>ID: " + id + "</li>");
            }
        }

        // SX-BR:05
        protected void Scenario05(List<Entity> items)
        {
            string value = items[0].Name;
            Response.Write("Name: " + value);
        }

        // SX-BR:06
        protected void Scenario06(List<Entity> items)
        {
            string value = items[0].Email;
            Response.Write("Email: " + value);
        }

        // SX-BR:07
        protected void Scenario07(List<Entity> items)
        {
            foreach (Entity item in items)
            {
                string name = item.Name;
                Response.Write("<li>" + name + "</li>");
            }
        }

        // SX-BR:08
        protected void Scenario08(List<Entity> items)
        {
            string value = items[0].Name;
            Response.Write("<div title='" + value + "'>Item</div>");
        }

        // SX-BR:09
        protected void Scenario09(List<Entity> items)
        {
            string value = items[0].Name;
            Response.Write("<script>var name = '" + value + "';</script>");
        }

        // SX-BR:10
        protected void Scenario10()
        {
            string input = Request.QueryString["input"];
            Session["storedInput"] = input;
            
            string stored = (string)Session["storedInput"];
            Response.Write("Stored: " + stored);
        }

        // SX-BR:11
        protected void Scenario11()
        {
            HttpCookie cookie = Request.Cookies["userPref"];
            if (cookie != null)
            {
                Response.Write("Preference: " + cookie.Value);
            }
        }

        // SX-MR:12 + SX-BR:13
        protected void Scenario12(List<Entity> items)
        {
            Entity item = items[0];
            
            long id = item.Id;
            Response.Write("ID: " + id);
            
            string name = item.Name;
            Response.Write(", Name: " + name);
        }
    }
}

