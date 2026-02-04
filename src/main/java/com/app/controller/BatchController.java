package com.app.controller;

import com.app.service.EntityService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

/**
 * Loop Condition False Positive Scenarios
 * All scenarios are SAFE but CxQL incorrectly flags them
 * Pattern: User input -> Bound -> Loop condition
 */
public class BatchController extends HttpServlet {

    private EntityService entityService;
    private static final int MAX_ITEMS = 100;
    private static final int MAX_PAGES = 50;
    private static final int MAX_ROWS = 25;

    public enum PageSize { SMALL(10), MEDIUM(25), LARGE(50);
        final int value; PageSize(int v) { this.value = v; }
        public int getValue() { return value; }
    }

    /*
     * #L01 - FALSE POSITIVE: Ternary operator bounds the loop
     * WHY SAFE: Ternary (pages > MAX_PAGES ? MAX_PAGES : pages) limits loop to MAX_PAGES (50).
     *           Regardless of user input, the loop runs at most 50 times.
     *           This prevents DoS from unbounded iteration.
     * WHY CXQL FAILS: CxQL does not recognize ternary operator as a bounding mechanism.
     *                 It sees user input flowing to loop condition but misses the upper bound.
     *                 CxQL's AbsInt (Abstract Value Analysis) doesn't analyze ternary expressions.
     * CXQL LIMITATION: Ternary operator not recognized as loop bound sanitizer.
     */
    public void processPages(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int pages = Sanitizer.toInt(req.getParameter("pages"));
        int bounded = pages > MAX_PAGES ? MAX_PAGES : pages;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<div>Page " + i + "</div>");
        }
    }

    /*
     * #L02 - FALSE POSITIVE: Modulo operator bounds the loop
     * WHY SAFE: input % MAX_ITEMS always produces result in range [0, MAX_ITEMS-1].
     *           Maximum loop iterations = 99, regardless of user input value.
     *           This is mathematically guaranteed bounding.
     * WHY CXQL FAILS: CxQL does not analyze modulo operation for bounding effect.
     *                 It cannot determine that % MAX_ITEMS limits the range.
     * CXQL LIMITATION: Modulo not recognized as loop bound mechanism.
     */
    public void processModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input % MAX_ITEMS;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>" + i + "</li>");
        }
    }

    /*
     * #L03 - FALSE POSITIVE: Bitwise AND bounds the loop
     * WHY SAFE: input & 0x3F masks to 6 bits, max value 63 (binary 111111).
     *           Regardless of user input, loop runs at most 63 times.
     *           Bitwise AND with constant is a deterministic bound.
     * WHY CXQL FAILS: CxQL does not analyze bitwise operations for bounding.
     *                 It cannot determine that & 0x3F limits the value range.
     * CXQL LIMITATION: Bitwise AND not recognized as loop bound.
     */
    public void processBitwise(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input & 0x3F;  // Max 63
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    /*
     * #L04 - FALSE POSITIVE: Bit shift with AND bounds the loop
     * WHY SAFE: (input >>> 4) & 0x0F: shift right 4 bits, then mask to 4 bits.
     *           Maximum result is 15 (binary 1111). Loop runs at most 15 times.
     * WHY CXQL FAILS: CxQL does not analyze compound bitwise expressions.
     *                 It cannot determine the maximum value from shift+mask pattern.
     * CXQL LIMITATION: Bit shift operations not analyzed for bounds.
     */
    public void processShift(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input >>> 4) & 0x0F;  // Max 15
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    /*
     * #L05 - FALSE POSITIVE: Enum value bounds the loop
     * WHY SAFE: PageSize.valueOf() only accepts SMALL(10), MEDIUM(25), LARGE(50).
     *           Invalid input throws exception before reaching loop.
     *           Maximum iterations = 50 (LARGE enum value).
     * WHY CXQL FAILS: CxQL cannot analyze enum validation for loop bounding.
     *                 It doesn't recognize that getValue() returns compile-time constants.
     * CXQL LIMITATION: Enum values not recognized as bounded source.
     */
    public void processEnumBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("size");
        PageSize size = PageSize.valueOf(input.toUpperCase());
        for (int i = 0; i < size.getValue(); i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    /*
     * #L06 - FALSE POSITIVE: Modulo by enum length bounds the loop
     * WHY SAFE: PageSize.values().length = 3 (compile-time constant).
     *           input % 3 always produces [0, 1, 2]. Loop runs at most 2 times.
     * WHY CXQL FAILS: CxQL cannot determine that values().length is a constant.
     *                 It doesn't analyze enum array length as bound.
     * CXQL LIMITATION: Enum.values().length not recognized as constant bound.
     */
    public void processEnumLength(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("count"));
        int bounded = input % PageSize.values().length;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    /*
     * #L07 - FALSE POSITIVE: Array length with MAX_ROWS bound
     * WHY SAFE: Compound condition (i < parts.length && i < MAX_ROWS).
     *           Loop limited to minimum of array size and MAX_ROWS (25).
     *           Even large input arrays only iterate 25 times.
     * WHY CXQL FAILS: CxQL may not fully analyze compound && conditions in loops.
     *                 It might miss that MAX_ROWS provides an upper bound.
     * CXQL LIMITATION: Compound loop conditions not fully analyzed.
     */
    public void processArrayBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = req.getParameter("data").split(",");
        for (int i = 0; i < parts.length && i < MAX_ROWS; i++) {
            resp.getWriter().write("<tr><td>" + Sanitizer.escapeHtml(parts[i]) + "</td></tr>");
        }
    }

    /*
     * #L08 - FALSE POSITIVE: String length with Math.min bound
     * WHY SAFE: Math.min(input.length(), MAX_ITEMS) caps iterations at 100.
     *           Even very long strings only process first 100 characters.
     * WHY CXQL FAILS: CxQL SHOULD recognize Math.min as a bound, but may miss it
     *                 when combined with String.length() on user input.
     * CXQL LIMITATION: Complex Math.min patterns may not be fully analyzed.
     */
    public void processStringLength(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("text");
        int length = Math.min(input.length(), MAX_ITEMS);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                resp.getWriter().write("<span>" + c + "</span>");
            }
        }
    }

    /*
     * #L09 - FALSE POSITIVE: Nested ternary clamps value to [0, MAX_ITEMS]
     * WHY SAFE: input < 0 ? 0 : (input > MAX_ITEMS ? MAX_ITEMS : input)
     *           Negative values become 0, values > 100 become 100.
     *           Loop bounded to [0, 100] regardless of input.
     * WHY CXQL FAILS: CxQL does not analyze nested ternary expressions for bounds.
     *                 This is a common "clamp" pattern that CxQL misses.
     * CXQL LIMITATION: Nested ternary not analyzed for value range.
     */
    public void processNestedTernary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input < 0 ? 0 : (input > MAX_ITEMS ? MAX_ITEMS : input);
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>" + i + "</li>");
        }
    }

    /*
     * #L10 - FALSE POSITIVE: Compound modulo bounds the loop
     * WHY SAFE: (input % 1000) % MAX_ITEMS: first mod gives [0,999], second gives [0,99].
     *           Maximum iterations = 99, mathematically guaranteed.
     * WHY CXQL FAILS: CxQL does not analyze chained modulo operations.
     *                 It cannot determine the final value range.
     * CXQL LIMITATION: Compound modulo expressions not analyzed.
     */
    public void processCompoundModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input % 1000) % MAX_ITEMS;  // Max 99
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    /*
     * #L11 - FALSE POSITIVE: Integer.min bounds the loop
     * WHY SAFE: Integer.min(input, MAX_ITEMS) caps at 100.
     *           Functionally identical to Math.min() which CxQL may recognize.
     * WHY CXQL FAILS: CxQL may not recognize Integer.min() as equivalent to Math.min().
     *                 Different method name despite identical behavior.
     * CXQL LIMITATION: Integer.min() may not be in recognizer list.
     */
    public void processIntegerMin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("count"));
        int bounded = Integer.min(input, MAX_ITEMS);
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    /*
     * #L12 - FALSE POSITIVE: Math.max/Math.min clamp pattern
     * WHY SAFE: Math.max(0, Math.min(input, MAX_ROWS)) clamps to [0, 25].
     *           This is standard clamp pattern used throughout Java ecosystem.
     * WHY CXQL FAILS: CxQL may analyze each Math call separately but miss composition.
     *                 The nested Math.max(0, Math.min()) pattern may not be recognized.
     * CXQL LIMITATION: Nested Math.max/min composition not fully analyzed.
     */
    public void processClamp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int clamped = Math.max(0, Math.min(input, MAX_ROWS));
        for (int i = 0; i < clamped; i++) {
            resp.getWriter().write("<tr><td>" + i + "</td></tr>");
        }
    }

    /*
     * #L13 - FALSE POSITIVE: XOR with AND bounds the loop
     * WHY SAFE: (input ^ 0xFF) produces unpredictable value, but & 0x1F masks to 5 bits.
     *           Maximum value = 31 (binary 11111), regardless of XOR result.
     * WHY CXQL FAILS: CxQL cannot analyze compound bitwise expressions.
     *                 The final AND mask determines the bound, but CxQL misses this.
     * CXQL LIMITATION: XOR+AND bitwise pattern not analyzed.
     */
    public void processXorBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input ^ 0xFF) & 0x1F;  // Max 31
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    /*
     * #L14 - FALSE POSITIVE: Division with Math.min bounds the loop
     * WHY SAFE: Math.abs(input) / 100 divides, then Math.min limits to MAX_ROWS.
     *           Even Integer.MAX_VALUE / 100 = ~21 million, but Math.min caps at 25.
     * WHY CXQL FAILS: CxQL may miss the Math.min after division.
     *                 Division alone doesn't bound (large input / small divisor = large).
     * CXQL LIMITATION: Division + Math.min composition may not be fully tracked.
     */
    public void processDivision(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = Math.abs(input) / 100;  // Large divisor limits result
        int limited = Math.min(bounded, MAX_ROWS);
        for (int i = 0; i < limited; i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    /*
     * #L15 - FALSE POSITIVE: Math.abs with modulo bounds the loop
     * WHY SAFE: Math.abs(input) ensures positive, % MAX_ITEMS caps at [0, 99].
     *           Negative inputs handled by abs(), then bounded by modulo.
     * WHY CXQL FAILS: CxQL may not analyze Math.abs() + modulo combination.
     *                 The modulo alone should bound, but CxQL misses it.
     * CXQL LIMITATION: Math.abs + modulo pattern not recognized.
     */
    public void processAbsModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = Math.abs(input) % MAX_ITEMS;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }
}

