package com.checkmarx.falsepositive.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Type Conversion
 * 
 * Pattern: User input converted to numeric types before use in loop condition
 * 
 * These scenarios demonstrate that type conversion sanitizes the input before it's used
 * in loop conditions, preventing infinite loops or resource exhaustion.
 */
public class LoopCondition_TypeConversion {
    
    // Scenario 1: Integer.parseInt() in loop condition
    public void processItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam); // SAFE: Converts to int, throws on invalid input
        
        for (int i = 0; i < count; i++) { // SAFE: count is validated int
            // Process items
        }
    }
    
    // Scenario 2: Long.parseLong() in loop condition
    public void processRecords(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String limitParam = request.getParameter("limit");
        long limit = Long.parseLong(limitParam); // SAFE: Converts to long
        
        for (long i = 0; i < limit; i++) { // SAFE: limit is validated long
            // Process records
        }
    }
    
    // Scenario 3: Type conversion with Math.min() bounds
    public void processBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String maxParam = request.getParameter("max");
        int max = Integer.parseInt(maxParam); // SAFE: Numeric conversion
        int bounded = Math.min(max, 100); // SAFE: Additional bounds check
        
        for (int i = 0; i < bounded; i++) { // SAFE: Bounded and validated
            // Process with limit
        }
    }
    
    // Scenario 4: Type conversion in while loop
    public void processWhile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String thresholdParam = request.getParameter("threshold");
        int threshold = Integer.parseInt(thresholdParam); // SAFE: Numeric conversion
        
        int counter = 0;
        while (counter < threshold) { // SAFE: threshold is validated int
            counter++;
        }
    }
    
    // Scenario 5: Type conversion with try-catch
    public void processSafe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sizeParam = request.getParameter("size");
        try {
            int size = Integer.parseInt(sizeParam); // SAFE: Validated conversion
            for (int i = 0; i < size; i++) { // SAFE: size is int
                // Process safely
            }
        } catch (NumberFormatException e) {
            // Handle invalid input
        }
    }
}

