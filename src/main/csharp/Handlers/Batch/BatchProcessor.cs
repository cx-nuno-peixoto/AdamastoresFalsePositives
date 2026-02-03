using System;
using System.Web;
using System.Web.UI;
using System.Text.RegularExpressions;

namespace Checkmarx.Handlers.Batch
{
    public class BatchProcessor : Page
    {
        private const int MAX_ITEMS = 100;
        private const int MAX_PAGE = 50;

        // LC-MR:01
        protected void Scenario01()
        {
            string param = Request.QueryString["limit"];
            int value = int.Parse(param);
            int bounded = Math.Min(value, MAX_ITEMS);
            
            for (int i = 0; i < bounded; i++)
            {
                Response.Write("Item " + i + "<br>");
            }
        }

        // LC-MR:02
        protected void Scenario02()
        {
            string param = Request.QueryString["size"];
            int value = int.Parse(param);
            int bounded = (value > MAX_PAGE) ? MAX_PAGE : value;
            
            for (int i = 0; i < bounded; i++)
            {
                Response.Write("Item " + i + "<br>");
            }
        }

        // LC-MR:03
        protected void Scenario03()
        {
            string param = Request.QueryString["count"];
            Regex pattern = new Regex("^[0-9]{1,2}$");
            
            if (pattern.IsMatch(param))
            {
                int count = int.Parse(param);
                for (int i = 0; i < count; i++)
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }

        // LC-MR:04
        protected void Scenario04()
        {
            string param = Request.QueryString["count"];
            int value = int.Parse(param);
            
            if (value > 0 && value <= MAX_ITEMS)
            {
                for (int i = 0; i < value; i++)
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }

        // LC-BR:05
        protected void Scenario05()
        {
            string param = Request.QueryString["count"];
            int value = int.Parse(param);
            
            for (int i = 0; i < value; i++)
            {
                Response.Write("Item " + i + "<br>");
            }
        }

        // LC-BR:06
        protected void Scenario06()
        {
            string param = Request.QueryString["count"];
            Regex pattern = new Regex("^[0-9]+$");
            
            if (pattern.IsMatch(param))
            {
                int count = int.Parse(param);
                for (int i = 0; i < count; i++)
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }

        // LC-BR:07
        protected void Scenario07()
        {
            string param = Request.QueryString["base"];
            int baseVal = int.Parse(param);
            int count = (baseVal > 10) ? baseVal * 2 : baseVal;
            
            for (int i = 0; i < count; i++)
            {
                Response.Write("Item " + i + "<br>");
            }
        }

        // LC-BR:08
        protected void Scenario08()
        {
            string param = Request.QueryString["max"];
            int max = int.Parse(param);
            
            int i = 0;
            while (i < max)
            {
                Response.Write("Item " + i + "<br>");
                i++;
            }
        }

        // LC-BR:09
        protected void Scenario09()
        {
            string outerParam = Request.QueryString["outer"];
            string innerParam = Request.QueryString["inner"];
            int outer = int.Parse(outerParam);
            int inner = int.Parse(innerParam);
            
            for (int i = 0; i < outer; i++)
            {
                for (int j = 0; j < inner; j++)
                {
                    Response.Write("Item " + i + "," + j + "<br>");
                }
            }
        }
    }
}

