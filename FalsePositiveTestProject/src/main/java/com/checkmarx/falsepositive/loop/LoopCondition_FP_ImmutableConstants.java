package com.checkmarx.falsepositive.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Unchecked Input for Loop Condition - Immutable Constants
 * 
 * Pattern: Loop bounds are constrained by final constants, ensuring safe iteration counts
 * 
 * Even though user input flows to the loop condition, it's bounded by immutable constants.
 * CxQL doesn't recognize Math.min/max with constants as safe bounds.
 * 
 * All scenarios are FALSE POSITIVES - SAFE bounded loops.
 */
public class LoopCondition_FP_ImmutableConstants {
    
    // Immutable constant limits
    private static final int MAX_ITEMS = 100;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int MAX_RETRIES = 10;
    private static final int MAX_BATCH_SIZE = 1000;
    private static final long MAX_ITERATIONS = 500L;
    
    /**
     * FALSE POSITIVE: Math.min() with final constant
     * User input bounded by MAX_ITEMS constant (100)
     */
    public void processMathMinBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countParam = request.getParameter("count");
        int userCount = Integer.parseInt(countParam);
        
        // SAFE: Math.min ensures max of 100 iterations
        int safeCount = Math.min(userCount, MAX_ITEMS);
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeCount; i++) { // FALSE POSITIVE - Bounded by constant
            out.write("Item " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: Ternary with final constant
     * User input bounded by ternary check against MAX_PAGE_SIZE
     */
    public void processTernaryBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sizeParam = request.getParameter("pageSize");
        int userSize = Integer.parseInt(sizeParam);
        
        // SAFE: Ternary ensures max of 50 iterations
        int safeSize = (userSize > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : userSize;
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeSize; i++) { // FALSE POSITIVE - Bounded by constant
            out.write("Row " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: If-else with final constant
     * User input bounded by explicit if check
     */
    public void processIfElseBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String retriesParam = request.getParameter("retries");
        int userRetries = Integer.parseInt(retriesParam);
        
        // SAFE: If check ensures max of 10 retries
        int safeRetries;
        if (userRetries > MAX_RETRIES) {
            safeRetries = MAX_RETRIES;
        } else if (userRetries < 0) {
            safeRetries = 0;
        } else {
            safeRetries = userRetries;
        }
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeRetries; i++) { // FALSE POSITIVE - Bounded by constant
            out.write("Retry " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: Math.min with long constant
     * Long values bounded by MAX_ITERATIONS
     */
    public void processLongBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String iterParam = request.getParameter("iterations");
        long userIterations = Long.parseLong(iterParam);
        
        // SAFE: Math.min ensures max of 500 iterations
        long safeIterations = Math.min(userIterations, MAX_ITERATIONS);
        
        PrintWriter out = response.getWriter();
        for (long i = 0; i < safeIterations; i++) { // FALSE POSITIVE - Bounded by constant
            out.write("Iteration " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: Multiple constant bounds
     * User input bounded by both min and max constants
     */
    public void processDoubleBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String batchParam = request.getParameter("batchSize");
        int userBatch = Integer.parseInt(batchParam);
        
        // SAFE: Both min and max bounds applied
        int safeBatch = Math.max(1, Math.min(userBatch, MAX_BATCH_SIZE));
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeBatch; i++) { // FALSE POSITIVE - Bounded by constants
            out.write("Batch item " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: Clamp utility method
     * User input bounded by custom clamp method using constants
     */
    public void processClampBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String valueParam = request.getParameter("value");
        int userValue = Integer.parseInt(valueParam);
        
        // SAFE: Clamp ensures value is within [0, MAX_ITEMS]
        int safeValue = clamp(userValue, 0, MAX_ITEMS);
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeValue; i++) { // FALSE POSITIVE - Clamped by constants
            out.write("Value " + i + "\n");
        }
    }
    
    /**
     * FALSE POSITIVE: Enum ordinal with constant max
     * Enum ordinal already bounded, plus constant check
     */
    public void processEnumOrdinalBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String levelParam = request.getParameter("level");
        int userLevel = Integer.parseInt(levelParam);
        
        // SAFE: Bounded by enum size and constant
        int safeLevel = Math.min(userLevel, Math.min(Level.values().length, MAX_RETRIES));
        
        PrintWriter out = response.getWriter();
        for (int i = 0; i < safeLevel; i++) { // FALSE POSITIVE - Bounded by enum + constant
            out.write("Level " + i + "\n");
        }
    }
    
    // Helper method
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    private enum Level {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}

