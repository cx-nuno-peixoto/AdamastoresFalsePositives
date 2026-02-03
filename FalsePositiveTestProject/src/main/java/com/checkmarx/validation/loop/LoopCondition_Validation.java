package com.checkmarx.validation.loop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * VALIDATION FILE: Loop Condition - Good vs Bad Findings
 *
 * This file contains BOTH:
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 */
public class LoopCondition_Validation {
    
    public enum BatchSize {
        SMALL(10), MEDIUM(50), LARGE(100);
        private final int size;
        BatchSize(int size) { this.size = size; }
        public int getSize() { return size; }
    }
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
    // These patterns are SAFE because they have EXPLICIT BOUNDS on the loop iterations

    // BAD: Bounded loop with Math.min - SAFE because max iterations = 100
    public void badBoundedMathMin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String limitParam = request.getParameter("limit");
        int limit = Integer.parseInt(limitParam);
        int bounded = Math.min(limit, 100); // SAFE: Bounded to max 100 iterations

        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Max 100 iterations
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: Bounded loop with Math.max for lower bound + Math.min for upper
    public void badBoundedMathMinMax(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam);
        int bounded = Math.max(0, Math.min(count, 50)); // SAFE: Between 0 and 50

        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Max 50 iterations
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: Ternary expression that LIMITS value - SAFE because max = 50
    public void badTernaryBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String sizeParam = request.getParameter("size");
        int size = Integer.parseInt(sizeParam);
        int bounded = (size > 50) ? 50 : size; // SAFE: Max 50 iterations

        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Max 50 iterations
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: Ternary with both branches bounded
    public void badTernaryBothBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String sizeParam = request.getParameter("size");
        int size = Integer.parseInt(sizeParam);
        int bounded = (size > 50) ? 50 : ((size < 0) ? 0 : size); // SAFE: Between 0 and 50

        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Max 50 iterations
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: Enum-based loop - SAFE because enum has finite values
    public void badEnumLoop(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String batchParam = request.getParameter("batch");
        BatchSize batch = BatchSize.valueOf(batchParam.toUpperCase()); // SAFE: Enum validation
        int limit = batch.getSize(); // SAFE: Max value is LARGE(100)

        for (int i = 0; i < limit; i++) { // FALSE POSITIVE (BAD) - Max 100 iterations
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: Regex that LIMITS digit count - SAFE because max = 99
    public void badRegexBoundedDigits(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        Pattern pattern = Pattern.compile("^[0-9]{1,2}$"); // SAFE: Only 1-2 digits (max 99)
        Matcher matcher = pattern.matcher(countParam);

        if (matcher.matches()) {
            int count = Integer.parseInt(countParam);
            for (int i = 0; i < count; i++) { // FALSE POSITIVE (BAD) - Max 99 iterations
                out.write("Item " + i + "<br>");
            }
        }
    }

    // BAD: Constant upper bound - SAFE because hardcoded limit
    public void badConstantBound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam);
        final int MAX_ITERATIONS = 100;

        for (int i = 0; i < count && i < MAX_ITERATIONS; i++) { // FALSE POSITIVE (BAD) - Max 100
            out.write("Item " + i + "<br>");
        }
    }

    // BAD: If-guard with explicit bound check
    public void badIfGuardBounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam);

        if (count > 0 && count <= 100) { // SAFE: Explicit bound check
            for (int i = 0; i < count; i++) { // FALSE POSITIVE (BAD) - Max 100 iterations
                out.write("Item " + i + "<br>");
            }
        }
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
    // These patterns are VULNERABLE because they have NO BOUNDS on loop iterations

    // GOOD: Type conversion WITHOUT bounds - VULNERABLE (can be 2 billion iterations)
    public void goodUnboundedIntParse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam); // NOT SAFE: No upper bound!

        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Unbounded, can cause DoS
            out.write("Item " + i + "<br>");
        }
    }

    // GOOD: Regex validates numeric but doesn't limit VALUE - VULNERABLE
    public void goodRegexUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        Pattern pattern = Pattern.compile("^[0-9]+$"); // Allows "999999999"!
        Matcher matcher = pattern.matcher(countParam);

        if (matcher.matches()) {
            int count = Integer.parseInt(countParam);
            for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Regex doesn't bound value
                out.write("Item " + i + "<br>");
            }
        }
    }

    // GOOD: Ternary with MULTIPLICATION - VULNERABLE (can overflow or be huge)
    public void goodTernaryMultiplication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String baseParam = request.getParameter("base");
        int base = Integer.parseInt(baseParam);
        int count = (base > 10) ? base * 2 : base; // VULNERABLE: base * 2 can be huge!

        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Multiplication doesn't bound
            out.write("Item " + i + "<br>");
        }
    }

    // GOOD: Math operations that DON'T bound - VULNERABLE
    public void goodMathUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String valueParam = request.getParameter("value");
        int value = Integer.parseInt(valueParam);
        int count = value + 100; // VULNERABLE: Adding doesn't bound!

        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Addition doesn't bound
            out.write("Item " + i + "<br>");
        }
    }

    // GOOD: While loop without bounds - VULNERABLE
    public void goodWhileUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String maxParam = request.getParameter("max");
        int max = Integer.parseInt(maxParam); // NOT SAFE: No upper bound!

        int i = 0;
        while (i < max) { // TRUE POSITIVE (GOOD) - Unbounded while loop
            out.write("Item " + i + "<br>");
            i++;
        }
    }

    // GOOD: Do-while loop without bounds - VULNERABLE
    public void goodDoWhileUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String maxParam = request.getParameter("max");
        int max = Integer.parseInt(maxParam);

        int i = 0;
        do {
            out.write("Item " + i + "<br>");
            i++;
        } while (i < max); // TRUE POSITIVE (GOOD) - Unbounded do-while
    }

    // GOOD: Nested loops - EXPONENTIAL vulnerability (outer * inner iterations)
    public void goodNestedUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String outerParam = request.getParameter("outer");
        String innerParam = request.getParameter("inner");
        int outer = Integer.parseInt(outerParam);
        int inner = Integer.parseInt(innerParam);

        // If outer=1000 and inner=1000, this is 1,000,000 iterations!
        for (int i = 0; i < outer; i++) { // TRUE POSITIVE (GOOD)
            for (int j = 0; j < inner; j++) { // TRUE POSITIVE (GOOD)
                out.write("Item " + i + "," + j + "<br>");
            }
        }
    }

    // GOOD: Integer overflow scenario - VULNERABLE (negative count = infinite loop)
    public void goodIntegerOverflow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String valueParam = request.getParameter("value");
        int value = Integer.parseInt(valueParam);
        int count = value * 2; // If value = 1073741824, count overflows to negative!

        // If count is negative, loop never terminates (i starts at 0, always < negative)
        // Actually terminates but with 0 iterations - still a logic bug
        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Overflow vulnerability
            out.write("Item " + i + "<br>");
        }
    }

    // GOOD: Modulo doesn't bound upper value - VULNERABLE
    public void goodModuloUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String valueParam = request.getParameter("value");
        int value = Integer.parseInt(valueParam);
        int count = value % 1000000; // Still allows up to 999,999 iterations!

        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Modulo doesn't sufficiently bound
            out.write("Item " + i + "<br>");
        }
    }

    // GOOD: Absolute value doesn't bound - VULNERABLE
    public void goodAbsoluteValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String valueParam = request.getParameter("value");
        int value = Integer.parseInt(valueParam);
        int count = Math.abs(value); // Still allows Integer.MAX_VALUE iterations!

        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - abs() doesn't bound
            out.write("Item " + i + "<br>");
        }
    }

    // MIXED: Bounded (BAD/FP) + Unbounded (GOOD/TP) in same method
    public void mixedBoundedAndUnbounded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // BAD/FP: Properly bounded loop
        String safeParam = request.getParameter("safe");
        int safe = Integer.parseInt(safeParam);
        int bounded = Math.min(safe, 50);
        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Max 50 iterations
            out.write("Safe item " + i + "<br>");
        }

        // GOOD/TP: Unbounded loop
        String unsafeParam = request.getParameter("unsafe");
        int unsafe = Integer.parseInt(unsafeParam);
        for (int i = 0; i < unsafe; i++) { // TRUE POSITIVE (GOOD) - Unbounded
            out.write("Unsafe item " + i + "<br>");
        }
    }
}

