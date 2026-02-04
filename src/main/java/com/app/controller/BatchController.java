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

    // #L01 - Ternary operator bound (CxQL doesn't recognize)
    public void processPages(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int pages = Sanitizer.toInt(req.getParameter("pages"));
        int bounded = pages > MAX_PAGES ? MAX_PAGES : pages;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<div>Page " + i + "</div>");
        }
    }

    // #L02 - Modulo bound (CxQL doesn't recognize)
    public void processModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input % MAX_ITEMS;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>" + i + "</li>");
        }
    }

    // #L03 - Bitwise AND bound (CxQL doesn't recognize)
    public void processBitwise(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input & 0x3F;  // Max 63
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    // #L04 - Bitwise shift right bound (CxQL doesn't recognize)
    public void processShift(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input >>> 4) & 0x0F;  // Max 15
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    // #L05 - Enum ordinal bound (CxQL doesn't recognize enum.ordinal)
    public void processEnumBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("size");
        PageSize size = PageSize.valueOf(input.toUpperCase());
        for (int i = 0; i < size.getValue(); i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    // #L06 - Enum values length bound (CxQL doesn't recognize)
    public void processEnumLength(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("count"));
        int bounded = input % PageSize.values().length;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    // #L07 - Array length bound (CxQL doesn't recognize)
    public void processArrayBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = req.getParameter("data").split(",");
        for (int i = 0; i < parts.length && i < MAX_ROWS; i++) {
            resp.getWriter().write("<tr><td>" + Sanitizer.escapeHtml(parts[i]) + "</td></tr>");
        }
    }

    // #L08 - String length bound (CxQL doesn't recognize)
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

    // #L09 - Nested ternary bound (CxQL doesn't recognize)
    public void processNestedTernary(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input < 0 ? 0 : (input > MAX_ITEMS ? MAX_ITEMS : input);
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>" + i + "</li>");
        }
    }

    // #L10 - Compound modulo bound (CxQL doesn't recognize)
    public void processCompoundModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input % 1000) % MAX_ITEMS;  // Max 99
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    // #L11 - Integer.min bound (CxQL should recognize but might miss)
    public void processIntegerMin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("count"));
        int bounded = Integer.min(input, MAX_ITEMS);
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    // #L12 - Clamp pattern (CxQL doesn't recognize)
    public void processClamp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int clamped = Math.max(0, Math.min(input, MAX_ROWS));
        for (int i = 0; i < clamped; i++) {
            resp.getWriter().write("<tr><td>" + i + "</td></tr>");
        }
    }

    // #L13 - XOR bound (CxQL doesn't recognize)
    public void processXorBound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = (input ^ 0xFF) & 0x1F;  // Max 31
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }

    // #L14 - Division bound (CxQL doesn't recognize)
    public void processDivision(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = Math.abs(input) / 100;  // Large divisor limits result
        int limited = Math.min(bounded, MAX_ROWS);
        for (int i = 0; i < limited; i++) {
            resp.getWriter().write("<div>" + i + "</div>");
        }
    }

    // #L15 - Absolute value with modulo (CxQL doesn't recognize)
    public void processAbsModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = Math.abs(input) % MAX_ITEMS;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }
}

