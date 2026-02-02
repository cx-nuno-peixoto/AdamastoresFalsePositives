using System;
using System.Web;
using System.Web.UI;
using System.Text.RegularExpressions;

namespace Checkmarx.Validation.Loop
{
    /// <summary>
    /// VALIDATION FILE: Unchecked Input for Loop Condition - Good vs Bad Findings (C#)
    /// 
    /// This file contains BOTH:
    /// - FALSE POSITIVES (BAD) - SAFE because they have EXPLICIT BOUNDS
    /// - TRUE POSITIVES (GOOD) - VULNERABLE because they have NO BOUNDS
    /// </summary>
    public class LoopCondition_Validation : Page
    {
        // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
        
        // BAD: Bounded loop with Math.Min - SAFE because max = 100
        protected void BadBoundedMathMin()
        {
            string limitParam = Request.QueryString["limit"];
            int limit = int.Parse(limitParam);
            int bounded = Math.Min(limit, 100); // SAFE: Max 100 iterations
            
            for (int i = 0; i < bounded; i++) // FALSE POSITIVE (BAD) - Max 100 iterations
            {
                Response.Write("Item " + i + "<br>");
            }
        }
        
        // BAD: Ternary expression that LIMITS value - SAFE because max = 50
        protected void BadTernaryBounded()
        {
            string sizeParam = Request.QueryString["size"];
            int size = int.Parse(sizeParam);
            int bounded = (size > 50) ? 50 : size; // SAFE: Max 50 iterations
            
            for (int i = 0; i < bounded; i++) // FALSE POSITIVE (BAD) - Max 50 iterations
            {
                Response.Write("Item " + i + "<br>");
            }
        }
        
        // BAD: Regex that LIMITS digit count - SAFE because max = 99
        protected void BadRegexBoundedDigits()
        {
            string countParam = Request.QueryString["count"];
            Regex pattern = new Regex("^[0-9]{1,2}$"); // SAFE: Only 1-2 digits (max 99)
            
            if (pattern.IsMatch(countParam))
            {
                int count = int.Parse(countParam);
                for (int i = 0; i < count; i++) // FALSE POSITIVE (BAD) - Max 99 iterations
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }
        
        // BAD: If-guard with explicit bound check
        protected void BadIfGuardBounded()
        {
            string countParam = Request.QueryString["count"];
            int count = int.Parse(countParam);
            
            if (count > 0 && count <= 100) // SAFE: Explicit bound check
            {
                for (int i = 0; i < count; i++) // FALSE POSITIVE (BAD) - Max 100 iterations
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }
        
        // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
        
        // GOOD: Type conversion WITHOUT bounds - VULNERABLE (can be 2 billion iterations)
        protected void GoodUnboundedIntParse()
        {
            string countParam = Request.QueryString["count"];
            int count = int.Parse(countParam); // NOT SAFE: No upper bound!
            
            for (int i = 0; i < count; i++) // TRUE POSITIVE (GOOD) - Unbounded, can cause DoS
            {
                Response.Write("Item " + i + "<br>");
            }
        }
        
        // GOOD: Regex validates numeric but doesn't limit VALUE - VULNERABLE
        protected void GoodRegexUnbounded()
        {
            string countParam = Request.QueryString["count"];
            Regex pattern = new Regex("^[0-9]+$"); // Allows "999999999"!
            
            if (pattern.IsMatch(countParam))
            {
                int count = int.Parse(countParam);
                for (int i = 0; i < count; i++) // TRUE POSITIVE (GOOD) - Regex doesn't bound value
                {
                    Response.Write("Item " + i + "<br>");
                }
            }
        }
        
        // GOOD: Ternary with MULTIPLICATION - VULNERABLE (can overflow or be huge)
        protected void GoodTernaryMultiplication()
        {
            string baseParam = Request.QueryString["base"];
            int baseVal = int.Parse(baseParam);
            int count = (baseVal > 10) ? baseVal * 2 : baseVal; // VULNERABLE: baseVal * 2 can be huge!
            
            for (int i = 0; i < count; i++) // TRUE POSITIVE (GOOD) - Multiplication doesn't bound
            {
                Response.Write("Item " + i + "<br>");
            }
        }
        
        // GOOD: While loop without bounds - VULNERABLE
        protected void GoodWhileUnbounded()
        {
            string maxParam = Request.QueryString["max"];
            int max = int.Parse(maxParam); // NOT SAFE: No upper bound!
            
            int i = 0;
            while (i < max) // TRUE POSITIVE (GOOD) - Unbounded while loop
            {
                Response.Write("Item " + i + "<br>");
                i++;
            }
        }
        
        // GOOD: Nested loops - EXPONENTIAL vulnerability
        protected void GoodNestedUnbounded()
        {
            string outerParam = Request.QueryString["outer"];
            string innerParam = Request.QueryString["inner"];
            int outer = int.Parse(outerParam);
            int inner = int.Parse(innerParam);
            
            // If outer=1000 and inner=1000, this is 1,000,000 iterations!
            for (int i = 0; i < outer; i++) // TRUE POSITIVE (GOOD)
            {
                for (int j = 0; j < inner; j++) // TRUE POSITIVE (GOOD)
                {
                    Response.Write("Item " + i + "," + j + "<br>");
                }
            }
        }
    }
}

