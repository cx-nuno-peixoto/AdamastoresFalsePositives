package com.checkmarx.falsepositive.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Ternary and Enum
 * 
 * Pattern: User input validated through ternary expressions or enum before use in loop
 * 
 * These scenarios demonstrate that ternary expressions with numeric operations
 * and enum validation sanitize input before loop conditions.
 */
public class LoopCondition_TernaryEnum {
    
    public enum BatchSize {
        SMALL(10), MEDIUM(50), LARGE(100);
        
        private final int size;
        
        BatchSize(int size) {
            this.size = size;
        }
        
        public int getSize() {
            return size;
        }
    }
    
    // Scenario 1: Ternary expression in loop condition
    public void processTernary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam); // SAFE: Numeric conversion
        int bounded = (count > 100) ? 100 : count; // SAFE: Ternary bounds the value
        
        for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated and limited
            // Process items
        }
    }
    
    // Scenario 2: Enum-based loop condition
    public void processEnum(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sizeParam = request.getParameter("size");
        
        try {
            BatchSize batchSize = BatchSize.valueOf(sizeParam.toUpperCase()); // SAFE: Enum validation
            int limit = batchSize.getSize(); // SAFE: Enum getter returns int
            
            for (int i = 0; i < limit; i++) { // SAFE: limit from enum
                // Process batch
            }
        } catch (IllegalArgumentException e) {
            // Invalid enum value
        }
    }
    
    // Scenario 3: Ternary with Math.min()
    public void processBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String maxParam = request.getParameter("max");
        int max = Integer.parseInt(maxParam); // SAFE: Numeric conversion
        int bounded = (max < 1000) ? max : 1000; // SAFE: Ternary bounds to max 1000
        
        for (int i = 0; i < bounded; i++) { // SAFE: bounded is limited
            // Process
        }
    }
    
    // Scenario 4: Enum ordinal in loop
    public void processEnumOrdinal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sizeParam = request.getParameter("size");
        
        try {
            BatchSize batchSize = BatchSize.valueOf(sizeParam.toUpperCase()); // SAFE: Enum validation
            int iterations = batchSize.ordinal() + 1; // SAFE: ordinal() returns int
            
            for (int i = 0; i < iterations; i++) { // SAFE: iterations from enum ordinal
                // Process
            }
        } catch (IllegalArgumentException e) {
            // Invalid enum
        }
    }
    
    // Scenario 5: Chained ternary in loop
    public void processChainedTernary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String levelParam = request.getParameter("level");
        int level = Integer.parseInt(levelParam); // SAFE: Numeric conversion
        
        int iterations = (level == 1) ? 10 : (level == 2) ? 50 : 100; // SAFE: Chained ternary
        
        for (int i = 0; i < iterations; i++) { // SAFE: iterations from ternary
            // Process
        }
    }
    
    // Scenario 6: Ternary with multiplication
    public void processTernaryMath(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseParam = request.getParameter("base");
        int base = Integer.parseInt(baseParam); // SAFE: Numeric conversion
        
        int count = (base > 10) ? base * 2 : base; // SAFE: Ternary with math operation
        
        for (int i = 0; i < count; i++) { // SAFE: count from ternary math
            // Process
        }
    }
}

