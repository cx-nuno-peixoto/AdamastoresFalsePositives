using System;
using System.Web;
using System.Web.UI;

namespace Checkmarx.FalsePositive.Loop
{
    /// <summary>
    /// FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Immutable Constants (C#)
    /// 
    /// Pattern: Loop bounds are constrained by readonly constants, ensuring safe iteration counts
    /// 
    /// Even though user input flows to the loop condition, it's bounded by immutable constants.
    /// CxQL doesn't recognize Math.Min/Max with constants as safe bounds.
    /// 
    /// All scenarios are FALSE POSITIVES - SAFE bounded loops.
    /// </summary>
    public class LoopCondition_FP_ImmutableConstants : Page
    {
        private const int MaxItems = 100;
        private const int MaxPageSize = 50;
        private const int MaxRetries = 10;
        private const int MaxBatchSize = 1000;
        private const long MaxIterations = 500L;
        
        /// <summary>
        /// FALSE POSITIVE: Math.Min() with const
        /// </summary>
        protected void ProcessMathMinBounded()
        {
            string countParam = Request.QueryString["count"];
            int userCount = int.Parse(countParam);
            
            int safeCount = Math.Min(userCount, MaxItems);
            
            for (int i = 0; i < safeCount; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Item " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Ternary with const
        /// </summary>
        protected void ProcessTernaryBounded()
        {
            string sizeParam = Request.QueryString["pageSize"];
            int userSize = int.Parse(sizeParam);
            
            int safeSize = (userSize > MaxPageSize) ? MaxPageSize : userSize;
            
            for (int i = 0; i < safeSize; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Row " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: If-else with const
        /// </summary>
        protected void ProcessIfElseBounded()
        {
            string retriesParam = Request.QueryString["retries"];
            int userRetries = int.Parse(retriesParam);
            
            int safeRetries;
            if (userRetries > MaxRetries)
                safeRetries = MaxRetries;
            else if (userRetries < 0)
                safeRetries = 0;
            else
                safeRetries = userRetries;
            
            for (int i = 0; i < safeRetries; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Retry " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Math.Min with long
        /// </summary>
        protected void ProcessLongBounded()
        {
            string iterParam = Request.QueryString["iterations"];
            long userIterations = long.Parse(iterParam);
            
            long safeIterations = Math.Min(userIterations, MaxIterations);
            
            for (long i = 0; i < safeIterations; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Iteration " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Both min and max bounds
        /// </summary>
        protected void ProcessDoubleBounded()
        {
            string batchParam = Request.QueryString["batchSize"];
            int userBatch = int.Parse(batchParam);
            
            int safeBatch = Math.Max(1, Math.Min(userBatch, MaxBatchSize));
            
            for (int i = 0; i < safeBatch; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Batch " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Clamp method with constants
        /// </summary>
        protected void ProcessClampBounded()
        {
            string valueParam = Request.QueryString["value"];
            int userValue = int.Parse(valueParam);
            
            int safeValue = Clamp(userValue, 0, MaxItems);
            
            for (int i = 0; i < safeValue; i++) // FALSE POSITIVE - Clamped
            {
                Response.Write("Value " + i + "<br/>");
            }
        }
        
        /// <summary>
        /// FALSE POSITIVE: Enum length with constant max
        /// </summary>
        protected void ProcessEnumBounded()
        {
            string levelParam = Request.QueryString["level"];
            int userLevel = int.Parse(levelParam);
            
            int enumLength = Enum.GetValues(typeof(Level)).Length;
            int safeLevel = Math.Min(userLevel, Math.Min(enumLength, MaxRetries));
            
            for (int i = 0; i < safeLevel; i++) // FALSE POSITIVE - Bounded
            {
                Response.Write("Level " + i + "<br/>");
            }
        }
        
        private static int Clamp(int value, int min, int max)
        {
            return Math.Max(min, Math.Min(value, max));
        }
        
        private enum Level
        {
            Low,
            Medium,
            High,
            Critical
        }
    }
}

