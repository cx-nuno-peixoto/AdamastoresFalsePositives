package com.checkmarx.falsepositive;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * FALSE POSITIVE SCENARIOS: Test Files - Loop Condition Tests
 * 
 * Pattern: Test files with @Test annotations and mock loop data
 * 
 * These scenarios should NOT be flagged because they are test files with controlled
 * test data, not production code with unchecked user input.
 */
public class LoopConditionFalsePositiveTest {
    
    // Mock test data
    private static final String MOCK_COUNT = "100";
    private static final String MOCK_MALICIOUS_COUNT = "999999999";
    private static final String MOCK_INVALID_COUNT = "abc<script>";
    
    // Scenario 1: Test loop with type conversion
    @Test
    public void testLoopWithTypeConversion() {
        String countStr = MOCK_COUNT; // SAFE: Mock test data
        int count = Integer.parseInt(countStr);
        
        int iterations = 0;
        for (int i = 0; i < count; i++) { // SAFE: Test loop with validated count
            iterations++;
        }
        
        System.out.println("Completed " + iterations + " iterations"); // SAFE: Test output
        assertEquals(100, iterations);
    }
    
    // Scenario 2: Test loop with bounded input
    @Test
    public void testLoopWithBoundedInput() {
        String limitStr = MOCK_MALICIOUS_COUNT; // SAFE: Mock test data
        int limit = Integer.parseInt(limitStr);
        int bounded = Math.min(limit, 1000); // SAFE: Bounded in test
        
        int count = 0;
        for (int i = 0; i < bounded; i++) { // SAFE: Test loop with bounded value
            count++;
        }
        
        System.out.println("Bounded iterations: " + count); // SAFE: Test output
        assertEquals(1000, count);
    }
    
    // Scenario 3: Test loop with invalid input handling
    @Test
    public void testLoopWithInvalidInput() {
        String invalidStr = MOCK_INVALID_COUNT; // SAFE: Mock test data
        
        try {
            int count = Integer.parseInt(invalidStr);
            for (int i = 0; i < count; i++) {
                // Should not reach here
            }
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException e) {
            System.out.println("Correctly rejected: " + invalidStr); // SAFE: Test output
        }
    }
    
    // Scenario 4: Test loop with ternary expression
    @Test
    public void testLoopWithTernary() {
        String countStr = "150"; // SAFE: Mock test data
        int count = Integer.parseInt(countStr);
        int bounded = (count > 100) ? 100 : count; // SAFE: Ternary in test
        
        int iterations = 0;
        for (int i = 0; i < bounded; i++) { // SAFE: Test loop
            iterations++;
        }
        
        System.out.println("Ternary bounded iterations: " + iterations); // SAFE: Test output
        assertEquals(100, iterations);
    }
    
    // Scenario 5: Test loop with enum
    @Test
    public void testLoopWithEnum() {
        BatchSize size = BatchSize.MEDIUM; // SAFE: Mock enum
        int limit = size.getSize();
        
        int count = 0;
        for (int i = 0; i < limit; i++) { // SAFE: Test loop with enum value
            count++;
        }
        
        System.out.println("Enum-based iterations: " + count); // SAFE: Test output
        assertEquals(50, count);
    }
    
    // Scenario 6: Test while loop with type conversion
    @Test
    public void testWhileLoopWithTypeConversion() {
        String thresholdStr = "10"; // SAFE: Mock test data
        int threshold = Integer.parseInt(thresholdStr);
        
        int counter = 0;
        while (counter < threshold) { // SAFE: Test while loop
            counter++;
        }
        
        System.out.println("While loop iterations: " + counter); // SAFE: Test output
        assertEquals(10, counter);
    }
    
    // Scenario 7: Test do-while loop
    @Test
    public void testDoWhileLoop() {
        String maxStr = "5"; // SAFE: Mock test data
        int max = Integer.parseInt(maxStr);
        
        int i = 0;
        do {
            i++;
        } while (i < max); // SAFE: Test do-while loop
        
        System.out.println("Do-while iterations: " + i); // SAFE: Test output
        assertEquals(5, i);
    }
    
    // Scenario 8: Test nested loops
    @Test
    public void testNestedLoops() {
        String outerStr = "3"; // SAFE: Mock test data
        String innerStr = "4"; // SAFE: Mock test data
        
        int outer = Integer.parseInt(outerStr);
        int inner = Integer.parseInt(innerStr);
        
        int total = 0;
        for (int i = 0; i < outer; i++) { // SAFE: Test outer loop
            for (int j = 0; j < inner; j++) { // SAFE: Test inner loop
                total++;
            }
        }
        
        System.out.println("Nested loop iterations: " + total); // SAFE: Test output
        assertEquals(12, total);
    }
    
    // Mock enum for testing
    enum BatchSize {
        SMALL(10), MEDIUM(50), LARGE(100);
        
        private final int size;
        
        BatchSize(int size) {
            this.size = size;
        }
        
        public int getSize() {
            return size;
        }
    }
}

