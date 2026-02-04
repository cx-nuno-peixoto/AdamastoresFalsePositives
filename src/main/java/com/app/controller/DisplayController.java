package com.app.controller;

import com.app.service.EntityService;
import com.app.service.AccountService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Stored XSS False Positive Scenarios
 * All scenarios are SAFE but CxQL incorrectly flags them
 * Pattern: DB data -> Sanitization -> Output
 */
public class DisplayController extends HttpServlet {

    private EntityService entityService;
    private AccountService accountService;
    private static final Pattern SAFE_CHARS = Pattern.compile("^[a-zA-Z0-9_\\s-]+$");

    /*
     * #X01 - FALSE POSITIVE: Database string escaped through custom escaper
     * WHY SAFE: escapeHtml() converts < > " ' & to HTML entities before output.
     *           Even if database contains malicious content, it's neutralized by escaping.
     *           This is the standard Stored XSS mitigation approach.
     * WHY CXQL FAILS: CxQL recognizes DB output (Find_DB_Out) as source for Stored XSS.
     *                 However, it does not recognize custom Sanitizer.escapeHtml() as sanitizer.
     *                 Only framework-specific sanitizers (ESAPI, OWASP, etc.) are in the list.
     * CXQL LIMITATION: Custom sanitizer methods not recognized as XSS mitigation.
     */
    public void renderEntityName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    /*
     * #X02 - FALSE POSITIVE: Database string with alphanumeric extraction
     * WHY SAFE: replaceAll("[^a-zA-Z0-9 ]", "") strips ALL special characters.
     *           XSS requires < > " ' etc. which are removed by this transformation.
     *           Database content is sanitized to safe character set.
     * WHY CXQL FAILS: CxQL does not analyze replaceAll() regex for sanitization effect.
     *                 It cannot determine that the transformation removes dangerous chars.
     * CXQL LIMITATION: String.replaceAll() not analyzed for sanitization patterns.
     */
    public void renderCleanedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String cleaned = name.replaceAll("[^a-zA-Z0-9 ]", "");
        resp.getWriter().write("<div>" + cleaned + "</div>");
    }

    /*
     * #X03 - FALSE POSITIVE: Database string with regex validation guard
     * WHY SAFE: Pattern ^[a-zA-Z0-9_\\s-]+$ only allows safe characters.
     *           If DB content contains XSS characters, it fails validation.
     *           Output only happens when content is confirmed safe.
     * WHY CXQL FAILS: CxQL does not analyze Pattern.matcher() guard conditions.
     *                 It cannot determine that the if-condition ensures safe content.
     * CXQL LIMITATION: Regex validation guards not recognized for Stored XSS.
     */
    public void renderValidatedDescription(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String desc = entityService.getEntityDescription(id);
        if (SAFE_CHARS.matcher(desc).matches()) {
            resp.getWriter().write("<p>" + desc + "</p>");
        }
    }

    /*
     * #X04 - FALSE POSITIVE: Database string URL encoded
     * WHY SAFE: URLEncoder.encode() converts < > " ' & to %XX escape sequences.
     *           These sequences are not interpreted as HTML by browsers.
     *           URL encoding is a valid XSS mitigation for URL parameter context.
     * WHY CXQL FAILS: CxQL does not recognize URLEncoder as XSS sanitizer.
     *                 It may recognize it for URL injection but not HTML context.
     * CXQL LIMITATION: URLEncoder not in XSS sanitizer list.
     */
    public void renderUrlEncodedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
        resp.getWriter().write("<a href=\"/entity?name=" + encoded + "\">View</a>");
    }

    /*
     * #X05 - FALSE POSITIVE: Database string masked
     * WHY SAFE: mask(name, 3) replaces most characters with asterisks (*).
     *           XSS payloads are destroyed - only asterisks and last 3 chars remain.
     *           Even if last 3 chars were malicious, incomplete tag can't execute.
     * WHY CXQL FAILS: CxQL does not recognize masking operations as sanitization.
     *                 It cannot determine that the transformation destroys payloads.
     * CXQL LIMITATION: Masking/redaction not recognized as sanitization.
     */
    public void renderMaskedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String masked = Sanitizer.mask(name, 3);
        resp.getWriter().write("<span>" + masked + "</span>");
    }

    /*
     * #X06 - FALSE POSITIVE: Database string truncated then escaped
     * WHY SAFE: Truncation alone doesn't sanitize, but escapeHtml() is called after.
     *           The final output has all XSS characters converted to entities.
     * WHY CXQL FAILS: CxQL doesn't recognize custom escapeHtml() as sanitizer.
     *                 The truncation is correctly not treated as sanitization.
     * CXQL LIMITATION: Custom escapeHtml() not in sanitizer list.
     */
    public void renderTruncatedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String truncated = name.length() > 20 ? name.substring(0, 20) : name;
        String escaped = Sanitizer.escapeHtml(truncated);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    /*
     * #X07 - FALSE POSITIVE: Database string validated via Enum.valueOf
     * WHY SAFE: Enum.valueOf() only accepts exact matches to enum constants.
     *           If DB contains "<script>", it throws IllegalArgumentException.
     *           type.name() returns only predefined constant names.
     * WHY CXQL FAILS: CxQL cannot determine that Enum.valueOf() restricts values.
     *                 It sees data flow from DB to output without recognizing enum validation.
     * CXQL LIMITATION: Enum.valueOf() not recognized as type-safe validation.
     */
    public void renderEntityType(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String typeStr = entityService.getEntityType(id);
        EntityType type = EntityType.valueOf(typeStr.toUpperCase());
        resp.getWriter().write("<span class=\"type-" + type.name() + "\">" + type.name() + "</span>");
    }

    /*
     * #X08 - FALSE POSITIVE: Database string with whitelist validation
     * WHY SAFE: Set.contains() only allows "product", "service", "subscription".
     *           Any malicious DB content fails the whitelist check.
     *           Only predefined safe strings reach the output.
     * WHY CXQL FAILS: CxQL does not analyze whitelist/allowlist patterns.
     *                 It cannot determine that contains() restricts to safe values.
     * CXQL LIMITATION: Whitelist validation patterns not recognized.
     */
    public void renderEntityCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String category = entityService.getEntityCategory(id);
        java.util.Set<String> allowed = java.util.Set.of("product", "service", "subscription");
        if (allowed.contains(category.toLowerCase())) {
            resp.getWriter().write("<div class=\"" + category + "\">" + category + "</div>");
        }
    }

    /*
     * #X09 - FALSE POSITIVE: Database string with StringBuilder character filtering
     * WHY SAFE: Loop only appends chars passing isLetterOrDigit/space/hyphen check.
     *           XSS characters < > " ' & are rejected and not included in output.
     *           The StringBuilder result contains only safe characters.
     * WHY CXQL FAILS: CxQL cannot track conditional StringBuilder append operations.
     *                 It does not analyze loop-based character filtering.
     * CXQL LIMITATION: No support for character-by-character filtering analysis.
     */
    public void renderFilteredName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == ' ' || c == '-') {
                sb.append(c);
            }
        }
        resp.getWriter().write("<span>" + sb.toString() + "</span>");
    }

    /*
     * #X10 - FALSE POSITIVE: Database UUID string validated via UUID.fromString
     * WHY SAFE: UUID.fromString() only accepts valid UUID format (hex + hyphens).
     *           If DB contains XSS payload, it throws IllegalArgumentException.
     *           uuid.toString() outputs only canonical UUID format.
     * WHY CXQL FAILS: CxQL does not recognize UUID.fromString() as type-safe validation.
     *                 It sees DB string flowing to output without recognizing UUID constraint.
     * CXQL LIMITATION: UUID parsing not recognized as sanitizer/validator.
     */
    public void renderEntityUuid(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String uuidStr = entityService.getEntityUuid(id);
        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
        resp.getWriter().write("<span data-uuid=\"" + uuid.toString() + "\"></span>");
    }

    /*
     * #X11 - FALSE POSITIVE: Cross-service database string escaped
     * WHY SAFE: accountService.getAccountName() returns DB data, escaped by escapeHtml().
     *           Same sanitization logic as X01, but from different service.
     * WHY CXQL FAILS: Same as X01 - custom escapeHtml() not recognized.
     *                 Cross-file flow doesn't affect the limitation.
     * CXQL LIMITATION: Custom sanitizers not recognized regardless of call depth.
     */
    public void renderAccountName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = accountService.getAccountName(id);
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    /*
     * #X12 - FALSE POSITIVE: Email domain extraction with escape
     * WHY SAFE: Domain extraction via split("@")[1] + escapeHtml() for output.
     *           Even if domain part contained XSS, it's neutralized by escaping.
     * WHY CXQL FAILS: CxQL doesn't recognize custom escapeHtml().
     *                 The split operation correctly isn't treated as sanitization.
     * CXQL LIMITATION: Custom sanitizer not recognized.
     */
    public void renderAccountDomain(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String email = accountService.getAccountEmail(id);
        String domain = email.contains("@") ? email.split("@")[1] : "";
        String escaped = Sanitizer.escapeHtml(domain);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    /*
     * #X13 - FALSE POSITIVE: Database string hex encoded
     * WHY SAFE: Each character converted to 2-digit hex code (00-FF).
     *           Output contains only [0-9a-f] characters - no XSS possible.
     *           "<" becomes "3c", ">" becomes "3e", etc.
     * WHY CXQL FAILS: CxQL cannot analyze the hex encoding loop.
     *                 It doesn't recognize that output is constrained to hex chars.
     * CXQL LIMITATION: Hex encoding loop pattern not recognized.
     */
    public void renderHexEncodedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        StringBuilder hex = new StringBuilder();
        for (char c : name.toCharArray()) {
            hex.append(String.format("%02x", (int) c));
        }
        resp.getWriter().write("<span data-name=\"" + hex.toString() + "\"></span>");
    }

    /*
     * #X14 - FALSE POSITIVE: Database string Base64 encoded
     * WHY SAFE: Base64 encoding produces only [A-Za-z0-9+/=] characters.
     *           XSS characters < > " ' are encoded to safe Base64 representation.
     *           Browser cannot interpret Base64 as executable HTML.
     * WHY CXQL FAILS: CxQL does not recognize Base64 encoding as sanitization.
     *                 It cannot determine output character set is HTML-safe.
     * CXQL LIMITATION: Base64 encoding not in sanitizer list.
     */
    public void renderBase64Name(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String encoded = java.util.Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        resp.getWriter().write("<span data-encoded=\"" + encoded + "\"></span>");
    }

    /*
     * #X15 - FALSE POSITIVE: Database string JSON escaped
     * WHY SAFE: escapeJsonString() escapes \ " and control characters for JS context.
     *           In JavaScript string literal, this prevents string breakout attacks.
     *           Standard approach for safely embedding data in JS strings.
     * WHY CXQL FAILS: CxQL does not recognize JSON/JavaScript string escaping.
     *                 It cannot determine that the JS string context is protected.
     * CXQL LIMITATION: JSON escaping not recognized for JavaScript context XSS.
     */
    public void renderJsonContent(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String content = entityService.getEntityContent(id);
        String escaped = escapeJsonString(content);
        resp.getWriter().write("<script>var data = \"" + escaped + "\";</script>");
    }

    // Helper for JSON escaping
    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public enum EntityType { PRODUCT, SERVICE, SUBSCRIPTION, CATEGORY }
}

