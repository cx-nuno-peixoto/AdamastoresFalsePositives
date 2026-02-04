package com.app.controller;

import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Reflected XSS False Positive Scenarios
 * All scenarios are SAFE but CxQL incorrectly flags them
 */
public class InputController extends HttpServlet {

    public enum Category { ELECTRONICS, CLOTHING, FOOD, OTHER }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    private static final Pattern SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /*
     * #R01 - FALSE POSITIVE: Regex validated alphanumeric input
     * WHY SAFE: The isAlphanumeric() method validates that input contains ONLY [a-zA-Z0-9] characters.
     *           XSS requires special characters like <, >, ", ', etc. which are rejected by this validation.
     *           If validation fails, the code path with output is never executed.
     * WHY CXQL FAILS: CxQL does not perform semantic analysis of custom validation methods.
     *                 It cannot determine that Sanitizer.isAlphanumeric() restricts the character set.
     *                 The if-guard pattern with custom method calls is not recognized as a sanitizer.
     * CXQL LIMITATION: Missing support for conditional flow analysis with custom validation guards.
     */
    public void handleCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (Sanitizer.isAlphanumeric(code)) {
            resp.getWriter().write("<code>" + code + "</code>");
        }
    }

    /*
     * #R02 - FALSE POSITIVE: Custom HTML escape sanitization
     * WHY SAFE: escapeHtml() replaces all XSS-dangerous characters: < > " ' & with their HTML entities.
     *           This is the standard approach used by OWASP, Apache Commons, and all major frameworks.
     *           The output cannot contain executable HTML/JavaScript after escaping.
     * WHY CXQL FAILS: CxQL maintains a hardcoded list of known sanitizers (e.g., ESAPI.encoder()).
     *                 Custom implementations like Sanitizer.escapeHtml() are not in this list.
     *                 CxQL cannot analyze method bodies to determine sanitization behavior.
     * CXQL LIMITATION: No support for custom sanitizer recognition or method body analysis.
     */
    public void handleName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    /*
     * #R03 - FALSE POSITIVE: UUID validation sanitization
     * WHY SAFE: UUID.fromString() only accepts strings in format xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.
     *           Only hex digits [0-9a-fA-F] and hyphens are valid. Any XSS payload will throw exception.
     *           uuid.toString() outputs only the canonical UUID format - no user-controlled content.
     * WHY CXQL FAILS: CxQL does not recognize UUID.fromString() as a type-safe conversion.
     *                 It cannot determine that the output is constrained to a safe character set.
     * CXQL LIMITATION: Missing UUID as a recognized sanitizer/type-safe conversion.
     */
    public void handleUuid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("uuid");
        java.util.UUID uuid = java.util.UUID.fromString(input);
        resp.getWriter().write("<span>" + uuid.toString() + "</span>");
    }

    /*
     * #R04 - FALSE POSITIVE: Numeric extraction sanitization
     * WHY SAFE: extractNumeric() strips all non-digit characters, keeping only [0-9].
     *           The result contains no characters that could form XSS payload.
     *           Even if input is "<script>alert(1)</script>", output is only "1".
     * WHY CXQL FAILS: CxQL cannot analyze custom method implementations.
     *                 It does not recognize extractNumeric() as producing a safe output.
     * CXQL LIMITATION: No semantic analysis of custom transformation methods.
     */
    public void handleExtracted(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("mixed");
        String numeric = Sanitizer.extractNumeric(input);
        resp.getWriter().write("<span>" + numeric + "</span>");
    }

    /*
     * #R05 - FALSE POSITIVE: Masking sanitization
     * WHY SAFE: mask(input, 4) replaces all characters except the last 4 with asterisks (*).
     *           XSS payloads are destroyed by the masking - only "*" and last 4 chars remain.
     *           For input "<script>", output would be "******pt" - not executable.
     * WHY CXQL FAILS: CxQL does not recognize masking as a sanitization technique.
     *                 It cannot determine that the masking operation destroys malicious content.
     * CXQL LIMITATION: No recognition of masking/redaction as sanitizers.
     */
    public void handleMasked(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("card");
        String masked = Sanitizer.mask(input, 4);
        resp.getWriter().write("<span>" + masked + "</span>");
    }

    /*
     * #R06 - FALSE POSITIVE: Enum.valueOf type-safe validation
     * WHY SAFE: Enum.valueOf() only accepts exact matches to defined enum constants.
     *           Input "ELECTRONICS" works, but "<script>" throws IllegalArgumentException.
     *           cat.name() returns only the enum constant name - a compile-time fixed string.
     * WHY CXQL FAILS: CxQL tracks data flow but doesn't recognize enum conversion as sanitization.
     *                 It cannot determine that the output is constrained to predefined values.
     * CXQL LIMITATION: Enum.valueOf() not recognized as type-safe conversion/sanitizer.
     */
    public void handleCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("category");
        Category cat = Category.valueOf(input.toUpperCase());
        resp.getWriter().write("<span>" + cat.name() + "</span>");
    }

    /*
     * #R07 - FALSE POSITIVE: Enum toString() output
     * WHY SAFE: Same as R06 - enum validation ensures only valid constant names are accepted.
     *           pri.toString() returns the enum name, not user input.
     * WHY CXQL FAILS: CxQL sees the flow from user input to output but misses the enum constraint.
     *                 It treats toString() as potentially returning tainted data.
     * CXQL LIMITATION: No support for enum type safety analysis.
     */
    public void handlePriority(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("priority");
        Priority pri = Priority.valueOf(input.toUpperCase());
        resp.getWriter().write("<span>" + pri.toString() + "</span>");
    }

    /*
     * #R08 - FALSE POSITIVE: URL encoding sanitization
     * WHY SAFE: URLEncoder.encode() converts all special characters to %XX format.
     *           "<script>" becomes "%3Cscript%3E" which is not executed as HTML.
     *           This is a standard encoding used for URL parameters.
     * WHY CXQL FAILS: CxQL does not recognize URLEncoder as an XSS sanitizer.
     *                 While it may recognize it for URL injection, it misses the HTML context safety.
     * CXQL LIMITATION: URLEncoder not in XSS sanitizer list despite encoding dangerous chars.
     */
    public void handleUrlParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("query");
        String encoded = URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        resp.getWriter().write("<a href=\"search?" + encoded + "\">Link</a>");
    }

    /*
     * #R09 - FALSE POSITIVE: Base64 encoding sanitization
     * WHY SAFE: Base64 encoding converts all bytes to [A-Za-z0-9+/=] character set.
     *           "<script>" becomes "PHNjcmlwdD4=" - no executable characters remain.
     *           The encoded output cannot be interpreted as HTML/JavaScript.
     * WHY CXQL FAILS: CxQL does not recognize Base64 encoding as a sanitizer.
     *                 It cannot determine that the output character set is HTML-safe.
     * CXQL LIMITATION: Base64.getEncoder() not recognized as producing safe output.
     */
    public void handleBase64(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("data");
        String encoded = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        resp.getWriter().write("<span data-encoded=\"" + encoded + "\"></span>");
    }

    /*
     * #R10 - FALSE POSITIVE: Regex pattern validation guard
     * WHY SAFE: Pattern ^[a-zA-Z0-9_-]+$ allows ONLY alphanumeric, underscore, and hyphen.
     *           XSS characters like < > " ' are not in the allowed set.
     *           If pattern doesn't match, output code is never executed.
     * WHY CXQL FAILS: CxQL does not analyze regex patterns to determine character constraints.
     *                 It cannot determine that the if-condition ensures safe character set.
     * CXQL LIMITATION: No regex pattern analysis for validation guards.
     */
    public void handleSafeId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (SAFE_PATTERN.matcher(id).matches()) {
            resp.getWriter().write("<div id=\"" + id + "\"></div>");
        }
    }

    /*
     * #R11 - FALSE POSITIVE: String.replaceAll sanitization
     * WHY SAFE: replaceAll("[^a-zA-Z0-9 ]", "") removes ALL characters except alphanumeric and space.
     *           XSS characters < > " ' & are stripped, leaving only safe characters.
     *           Input "<script>alert(1)</script>" becomes "scriptalert1script".
     * WHY CXQL FAILS: CxQL does not analyze replaceAll regex patterns for sanitization effect.
     *                 It cannot determine that the transformation removes dangerous characters.
     * CXQL LIMITATION: String.replaceAll() not recognized as potential sanitizer.
     */
    public void handleCleaned(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("text");
        String cleaned = input.replaceAll("[^a-zA-Z0-9 ]", "");
        resp.getWriter().write("<p>" + cleaned + "</p>");
    }

    /*
     * #R12 - FALSE POSITIVE: Whitelist validation
     * WHY SAFE: Set.contains() only allows exact matches from predefined safe values.
     *           Only "active", "inactive", "pending" can reach the output.
     *           Any XSS payload like "<script>" will fail the contains check.
     * WHY CXQL FAILS: CxQL does not analyze whitelist/allowlist validation patterns.
     *                 It cannot determine that only predefined safe values can reach output.
     * CXQL LIMITATION: No support for whitelist validation pattern recognition.
     */
    public void handleStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String status = req.getParameter("status");
        java.util.Set<String> allowed = java.util.Set.of("active", "inactive", "pending");
        if (allowed.contains(status.toLowerCase())) {
            resp.getWriter().write("<span class=\"" + status + "\"></span>");
        }
    }

    /*
     * #R13 - FALSE POSITIVE: Truncation with HTML escape
     * WHY SAFE: Even if truncation doesn't sanitize, escapeHtml() is called on the result.
     *           The escaped output has all dangerous characters converted to entities.
     * WHY CXQL FAILS: CxQL may flag this because it doesn't recognize custom escapeHtml().
     *                 The truncation operation is correctly not considered a sanitizer.
     * CXQL LIMITATION: Custom escapeHtml() not in sanitizer list.
     */
    public void handleTruncated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("title");
        String truncated = input.length() > 50 ? input.substring(0, 50) : input;
        String escaped = Sanitizer.escapeHtml(truncated);
        resp.getWriter().write("<h1>" + escaped + "</h1>");
    }

    /*
     * #R14 - FALSE POSITIVE: Hex color validation
     * WHY SAFE: Regex ^[0-9a-fA-F]{6}$ only allows exactly 6 hex characters.
     *           XSS characters are not hex digits and will fail validation.
     *           The if-condition prevents any non-hex input from reaching output.
     * WHY CXQL FAILS: CxQL does not analyze inline regex patterns in String.matches().
     *                 It cannot determine that the pattern constrains to safe characters.
     * CXQL LIMITATION: No inline regex pattern analysis for validation.
     */
    public void handleHex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("color");
        if (input.matches("^[0-9a-fA-F]{6}$")) {
            resp.getWriter().write("<div style=\"color:#" + input + "\"></div>");
        }
    }

    /*
     * #R15 - FALSE POSITIVE: Numeric to hex string conversion
     * WHY SAFE: Sanitizer.toInt() converts input to integer (numeric type conversion).
     *           Integer.toHexString() output is always [0-9a-f] - no XSS characters possible.
     *           The transformation chain produces inherently safe output.
     * WHY CXQL FAILS: CxQL may miss this because toInt is custom, though Integer.toHexString
     *                 itself produces safe output regardless of the integer value.
     * CXQL LIMITATION: Custom type conversion (toInt) not recognized.
     */
    public void handleNumericHex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int value = Sanitizer.toInt(req.getParameter("value"));
        String hex = Integer.toHexString(value);
        resp.getWriter().write("<span>0x" + hex + "</span>");
    }

    /*
     * #R16 - FALSE POSITIVE: StringBuilder character filtering
     * WHY SAFE: Loop only appends characters that pass isLetterOrDigit() or space check.
     *           XSS characters < > " ' & fail this check and are not appended.
     *           The StringBuilder result contains only safe characters.
     * WHY CXQL FAILS: CxQL does not track StringBuilder append operations with conditions.
     *                 It cannot determine that character filtering produces safe output.
     * CXQL LIMITATION: No support for loop-based character filtering analysis.
     */
    public void handleBuilt(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("parts");
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == ' ') {
                sb.append(c);
            }
        }
        resp.getWriter().write("<span>" + sb.toString() + "</span>");
    }

    /*
     * #R17 - FALSE POSITIVE: Local HTML escape implementation (simulates Apache Commons)
     * WHY SAFE: escapeHtml4() replaces & < > " ' with HTML entities.
     *           This is identical behavior to Apache Commons StringEscapeUtils.escapeHtml4().
     *           All XSS attack vectors are neutralized by entity encoding.
     * WHY CXQL FAILS: CxQL does not recognize local method implementations as sanitizers.
     *                 Even though it implements standard HTML escaping, it's not in CxQL's list.
     * CXQL LIMITATION: Private helper methods not analyzed for sanitization behavior.
     */
    public void handleHtmlEscape(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("content");
        String escaped = escapeHtml4(input);
        resp.getWriter().write("<div>" + escaped + "</div>");
    }

    /*
     * #R18 - FALSE POSITIVE: Attribute encoding (simulates OWASP Encoder)
     * WHY SAFE: encodeForHtmlAttribute() converts non-alphanumeric chars to &#XX; entities.
     *           This is the OWASP-recommended approach for attribute context.
     *           XSS payloads are completely neutralized by entity encoding.
     * WHY CXQL FAILS: CxQL cannot analyze the local implementation.
     *                 It would recognize org.owasp.encoder.Encode.forHtmlAttribute() but not this.
     * CXQL LIMITATION: Local implementations of OWASP patterns not recognized.
     */
    public void handleOwaspEncode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("attr");
        String encoded = encodeForHtmlAttribute(input);
        resp.getWriter().write("<input value=\"" + encoded + "\"/>");
    }

    /*
     * #R19 - FALSE POSITIVE: JSON string escaping
     * WHY SAFE: escapeJsonString() escapes \ " and control characters for JSON context.
     *           In JavaScript string context, this prevents breaking out of the string.
     *           Proper JSON escaping is a recognized XSS mitigation for JS string context.
     * WHY CXQL FAILS: CxQL does not recognize JSON escaping as XSS mitigation.
     *                 It cannot determine that the JavaScript string context is protected.
     * CXQL LIMITATION: JSON/JavaScript string escaping not in sanitizer list.
     */
    public void handleJsonEscape(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("json");
        String escaped = escapeJsonString(input);
        resp.getWriter().write("<script>var data = \"" + escaped + "\";</script>");
    }

    /*
     * #R20 - FALSE POSITIVE: CSS value validation
     * WHY SAFE: Regex ^[0-9]+(px|em|rem|%)$ only allows numeric values with CSS units.
     *           No characters needed for XSS (<, >, ", etc.) can pass this validation.
     *           Invalid input fails the if-condition and produces no output.
     * WHY CXQL FAILS: CxQL does not analyze regex patterns for CSS context safety.
     *                 It cannot determine that the pattern restricts to safe CSS values.
     * CXQL LIMITATION: No CSS context-specific validation pattern recognition.
     */
    public void handleCssValue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("size");
        if (input.matches("^[0-9]+(px|em|rem|%)$")) {
            resp.getWriter().write("<div style=\"width:" + input + "\"></div>");
        }
    }

    // Helper methods simulating common sanitization libraries
    private String escapeHtml4(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private String encodeForHtmlAttribute(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isLetterOrDigit(c)) sb.append(c);
            else sb.append("&#").append((int) c).append(";");
        }
        return sb.toString();
    }

    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}

