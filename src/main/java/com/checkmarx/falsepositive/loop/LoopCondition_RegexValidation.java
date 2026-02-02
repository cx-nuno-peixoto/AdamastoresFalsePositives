package com.checkmarx.falsepositive.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Regex Validation
 * 
 * Pattern: User input validated with regex before use in loop condition
 * 
 * These scenarios demonstrate that regex validation ensures only safe numeric
 * input is used in loop conditions.
 */
public class LoopCondition_RegexValidation {
    
    // Scenario 1: Numeric regex validation before loop
    public void processValidatedCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countParam = request.getParameter("count");
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(countParam);
        
        if (matcher.matches()) { // SAFE: Only numeric strings pass
            int count = Integer.parseInt(countParam); // SAFE: Already validated as numeric
            for (int i = 0; i < count; i++) { // SAFE: count is validated int
                // Process items
            }
        }
    }
    
    // Scenario 2: Bounded numeric validation
    public void processBoundedCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String limitParam = request.getParameter("limit");
        Pattern pattern = Pattern.compile("^[0-9]{1,3}$"); // Max 3 digits
        Matcher matcher = pattern.matcher(limitParam);
        
        if (matcher.matches()) { // SAFE: Only 1-3 digit numbers pass
            int limit = Integer.parseInt(limitParam); // SAFE: Validated and bounded
            for (int i = 0; i < limit; i++) { // SAFE: limit is validated and bounded
                // Process
            }
        }
    }
    
    // Scenario 3: Decimal validation before loop
    public void processDecimalCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String valueParam = request.getParameter("value");
        Pattern pattern = Pattern.compile("^[0-9]+\\.[0-9]+$");
        Matcher matcher = pattern.matcher(valueParam);
        
        if (matcher.matches()) { // SAFE: Only decimal format passes
            double value = Double.parseDouble(valueParam); // SAFE: Validated decimal
            int iterations = (int) value;
            for (int i = 0; i < iterations; i++) { // SAFE: iterations from validated double
                // Process
            }
        }
    }
    
    // Scenario 4: Hex validation before loop
    public void processHexCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hexParam = request.getParameter("hex");
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]+$");
        Matcher matcher = pattern.matcher(hexParam);
        
        if (matcher.matches()) { // SAFE: Only hex characters pass
            int count = Integer.parseInt(hexParam, 16); // SAFE: Validated hex to int
            for (int i = 0; i < count; i++) { // SAFE: count from validated hex
                // Process
            }
        }
    }
}

