package com.checkmarx.falsepositive.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Number Formatting
 * 
 * Pattern: User input formatted with DecimalFormat/NumberFormat before use in loop
 * 
 * These scenarios demonstrate that number formatting validates numeric input
 * before it's used in loop conditions.
 */
public class LoopCondition_NumberFormatting {
    
    // Scenario 1: DecimalFormat parse before loop
    public void processWithDecimalFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String countParam = request.getParameter("count");
        DecimalFormat df = new DecimalFormat("#");
        Number number = df.parse(countParam); // SAFE: Parses to Number, throws on invalid
        int count = number.intValue();
        
        for (int i = 0; i < count; i++) { // SAFE: count is validated int
            // Process items
        }
    }
    
    // Scenario 2: NumberFormat parse before loop
    public void processWithNumberFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String limitParam = request.getParameter("limit");
        NumberFormat nf = NumberFormat.getIntegerInstance();
        Number number = nf.parse(limitParam); // SAFE: Parses to Number
        int limit = number.intValue();
        
        for (int i = 0; i < limit; i++) { // SAFE: limit is validated int
            // Process records
        }
    }
    
    // Scenario 3: Currency format parse before loop
    public void processWithCurrencyFormat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String amountParam = request.getParameter("amount");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        Number number = currencyFormat.parse(amountParam); // SAFE: Parses currency
        int iterations = number.intValue();
        
        for (int i = 0; i < iterations; i++) { // SAFE: iterations is validated
            // Process
        }
    }
    
    // Scenario 4: Format then parse in loop condition
    public void processFormatted(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sizeParam = request.getParameter("size");
        DecimalFormat df = new DecimalFormat("#,##0");
        Number number = df.parse(sizeParam); // SAFE: Validates numeric
        long size = number.longValue();
        
        for (long i = 0; i < size; i++) { // SAFE: size is validated long
            // Process
        }
    }
}

