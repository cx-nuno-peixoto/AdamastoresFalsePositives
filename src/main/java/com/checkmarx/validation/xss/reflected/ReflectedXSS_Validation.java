package com.checkmarx.validation.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * VALIDATION FILE: Reflected XSS - Good vs Bad Findings
 *
 * This file contains BOTH:
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 */
public class ReflectedXSS_Validation {
    
    public enum UserRole {
        ADMIN, USER, GUEST
    }
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========

    // BAD: Type conversion to int
    public void badIntParse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ageParam = request.getParameter("age");
        int age = Integer.parseInt(ageParam); // SAFE: Converts to int
        out.write("Age: " + age); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Type conversion to long
    public void badLongParse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");
        long id = Long.parseLong(idParam); // SAFE: Converts to long
        out.write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Number formatting
    public void badNumberFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String priceParam = request.getParameter("price");
        double price = Double.parseDouble(priceParam); // SAFE: Converts to double
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        String formatted = df.format(price); // SAFE: DecimalFormat sanitizes
        out.write("Price: " + formatted); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Regex validation
    public void badRegexValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String codeParam = request.getParameter("code");
        Pattern pattern = Pattern.compile("^[A-Z]{3}-[0-9]{4}$");
        Matcher matcher = pattern.matcher(codeParam);
        if (matcher.matches()) { // SAFE: Only ABC-1234 format passes
            out.write("Code: " + codeParam); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
        }
    }

    // BAD: Enum validation
    public void badEnumValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String roleParam = request.getParameter("role");
        try {
            UserRole role = UserRole.valueOf(roleParam.toUpperCase()); // SAFE: Enum validation
            out.write("Role: " + role.name()); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
        } catch (IllegalArgumentException e) {
            out.write("Invalid role");
        }
    }

    // BAD: Ternary with numeric
    public void badTernary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam); // SAFE: Converts to int
        int bounded = (count > 100) ? 100 : count; // SAFE: Ternary bounds value
        out.write("Count: " + bounded); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Math operators
    public void badMathOperators(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String valueParam = request.getParameter("value");
        int value = Integer.parseInt(valueParam); // SAFE: Converts to int
        int result = value * 2 + 10; // SAFE: Math operations on int
        out.write("Result: " + result); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========

    // GOOD: Direct output without validation
    public void goodDirectOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name"); // VULNERABLE: No validation
        out.write("Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: String concatenation without sanitization
    public void goodStringConcat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String comment = request.getParameter("comment"); // VULNERABLE: No sanitization
        String message = "Comment: " + comment;
        out.write(message); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: HTML attribute without encoding
    public void goodHtmlAttribute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String title = request.getParameter("title"); // VULNERABLE: No encoding
        out.write("<div title='" + title + "'>Content</div>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: JavaScript context without encoding
    public void goodJavaScript(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userName = request.getParameter("userName"); // VULNERABLE: No encoding
        out.write("<script>var user = '" + userName + "';</script>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: URL parameter without encoding
    public void goodUrlParameter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String redirect = request.getParameter("redirect"); // VULNERABLE: No validation
        out.write("<a href='" + redirect + "'>Click here</a>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Multiple parameters without validation
    public void goodMultipleParams(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String firstName = request.getParameter("firstName"); // VULNERABLE
        String lastName = request.getParameter("lastName"); // VULNERABLE
        out.write("Name: " + firstName + " " + lastName); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Regex validation but output original string
    public void goodRegexButUnsafe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String input = request.getParameter("input");
        Pattern pattern = Pattern.compile("^[a-z]+$");
        Matcher matcher = pattern.matcher(input);

        // Even though we validate, we output the ORIGINAL input, not the validated one
        if (matcher.matches()) {
            out.write("Input: " + input); // TRUE POSITIVE (GOOD) - SHOULD be flagged (still user input)
        }
    }

    // MIXED: Bad/FP numeric + Good/TP string
    public void mixedGoodAndBad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        long id = Long.parseLong(idParam); // SAFE: Numeric conversion
        out.write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix

        String name = request.getParameter("name"); // VULNERABLE: No validation
        out.write(", Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // ========== ADDITIONAL FALSE POSITIVES (BAD) ==========

    // BAD: Boolean conversion - SAFE because boolean
    public void badBooleanParse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String flagParam = request.getParameter("flag");
        boolean flag = Boolean.parseBoolean(flagParam); // SAFE: Only "true" or "false"
        out.write("Flag: " + flag); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: UUID validation - SAFE because UUID format
    public void badUuidValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String uuidParam = request.getParameter("uuid");
        try {
            java.util.UUID uuid = java.util.UUID.fromString(uuidParam); // SAFE: Valid UUID only
            out.write("UUID: " + uuid.toString()); // FALSE POSITIVE (BAD) - UUID format only
        } catch (IllegalArgumentException e) {
            out.write("Invalid UUID");
        }
    }

    // ========== ADDITIONAL TRUE POSITIVES (GOOD) ==========

    // GOOD: Header injection - HTTP header value
    public void goodHeaderReflection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String referer = request.getHeader("Referer"); // VULNERABLE: HTTP header
        out.write("Came from: " + referer); // TRUE POSITIVE (GOOD) - Header value
    }

    // GOOD: Cookie value in output
    public void goodCookieReflection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            out.write("Cookie: " + cookies[0].getValue()); // TRUE POSITIVE (GOOD) - Cookie value
        }
    }

    // GOOD: Path info in output
    public void goodPathInfoReflection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo(); // VULNERABLE: URL path
        out.write("Path: " + pathInfo); // TRUE POSITIVE (GOOD) - Path info
    }

    // GOOD: Query string in output
    public void goodQueryStringReflection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String queryString = request.getQueryString(); // VULNERABLE: Full query string
        out.write("Query: " + queryString); // TRUE POSITIVE (GOOD) - Query string
    }

    // GOOD: JSON context without encoding
    public void goodJsonContext(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String data = request.getParameter("data"); // VULNERABLE: No JSON encoding
        response.setContentType("application/json");
        out.write("{\"value\": \"" + data + "\"}"); // TRUE POSITIVE (GOOD) - JSON injection
    }
}

