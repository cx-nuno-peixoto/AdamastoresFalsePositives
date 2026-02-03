using System;
using System.Web;
using System.Web.UI;
using System.Text;

namespace Checkmarx.Handlers.Data
{
    public class DataOutputHandler : Page
    {
        private const string FIELD_KEY = "Password";
        private const string ID_KEY = "SSN";

        // PV-MR:01
        protected void Scenario01()
        {
            string param = Request.QueryString[FIELD_KEY];
            Response.Write("Field: " + FIELD_KEY);
        }

        // PV-MR:02
        protected void Scenario02()
        {
            string format = "Min 8 characters";
            Response.Write("Requirements: " + format);
        }

        // PV-MR:03
        protected void Scenario03()
        {
            string param = Request.QueryString["password"];
            StringBuilder sb = new StringBuilder();
            sb.Append("Field: ");
            sb.Append(param);
        }

        // PV-MR:04
        protected void Scenario04()
        {
            string param = Request.QueryString["accountId"];
            long value = long.Parse(param);
            Response.Write("Account ID: " + value);
        }

        // PV-MR:05
        protected void Scenario05()
        {
            string param = Request.QueryString["ssn"];
            string masked = "XXX-XX-" + param.Substring(param.Length - 4);
            Response.Write("SSN: " + masked);
        }

        // PV-BR:06
        protected void Scenario06()
        {
            string param = Request.QueryString["password"];
            Response.Write("Your password: " + param);
        }

        // PV-BR:07
        protected void Scenario07()
        {
            string param = Request.QueryString["ssn"];
            Response.Write("SSN: " + param);
        }

        // PV-BR:08
        protected void Scenario08()
        {
            string param = Request.QueryString["creditCard"];
            Response.Write("Credit Card: " + param);
        }

        // PV-BR:09
        protected void Scenario09()
        {
            string param = Request.QueryString["password"];
            Console.WriteLine("User password: " + param);
        }

        // PV-BR:10
        protected void Scenario10()
        {
            string param = Request.QueryString["authToken"];
            Response.Write("Token: " + param);
        }

        // PV-BR:11
        protected void Scenario11()
        {
            string param = Request.QueryString["password"];
            StringBuilder sb = new StringBuilder();
            sb.Append("Password: ");
            sb.Append(param);
            Response.Write(sb.ToString());
        }

        // PV-BR:12
        protected void Scenario12()
        {
            string email = Request.QueryString["email"];
            try
            {
                throw new Exception("Failed for user: " + email);
            }
            catch (Exception e)
            {
                Response.Write("Error: " + e.Message);
            }
        }

        // PV-MR:13 + PV-BR:14
        protected void Scenario13()
        {
            string format = "Min 8 characters";
            Response.Write("Requirements: " + format);
            
            string param = Request.QueryString["password"];
            Response.Write(", Your password: " + param);
        }
    }
}

