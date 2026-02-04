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

    // #R01 - Regex validated alphanumeric (CxQL misses validation guard)
    public void handleCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (Sanitizer.isAlphanumeric(code)) {
            resp.getWriter().write("<code>" + code + "</code>");
        }
    }

    // #R02 - Custom HTML escape (CxQL doesn't recognize custom escaper)
    public void handleName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    // #R03 - UUID validation (CxQL doesn't recognize UUID.fromString as sanitizer)
    public void handleUuid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("uuid");
        java.util.UUID uuid = java.util.UUID.fromString(input);
        resp.getWriter().write("<span>" + uuid.toString() + "</span>");
    }

    // #R04 - Extract numeric only (CxQL doesn't recognize digit extraction)
    public void handleExtracted(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("mixed");
        String numeric = Sanitizer.extractNumeric(input);
        resp.getWriter().write("<span>" + numeric + "</span>");
    }

    // #R05 - Masked input (CxQL doesn't recognize masking as sanitizer)
    public void handleMasked(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("card");
        String masked = Sanitizer.mask(input, 4);
        resp.getWriter().write("<span>" + masked + "</span>");
    }

    // #R06 - Enum.valueOf (CxQL doesn't recognize enum validation)
    public void handleCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("category");
        Category cat = Category.valueOf(input.toUpperCase());
        resp.getWriter().write("<span>" + cat.name() + "</span>");
    }

    // #R07 - Enum with toString (CxQL doesn't recognize enum.toString as safe)
    public void handlePriority(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("priority");
        Priority pri = Priority.valueOf(input.toUpperCase());
        resp.getWriter().write("<span>" + pri.toString() + "</span>");
    }

    // #R08 - URL encoding (CxQL doesn't recognize URLEncoder as sanitizer)
    public void handleUrlParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("query");
        String encoded = URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        resp.getWriter().write("<a href=\"search?" + encoded + "\">Link</a>");
    }

    // #R09 - Base64 encoding (CxQL doesn't recognize Base64 as sanitizer)
    public void handleBase64(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("data");
        String encoded = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
        resp.getWriter().write("<span data-encoded=\"" + encoded + "\"></span>");
    }

    // #R10 - Pattern.matches validation (CxQL doesn't recognize Pattern guard)
    public void handleSafeId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (SAFE_PATTERN.matcher(id).matches()) {
            resp.getWriter().write("<div id=\"" + id + "\"></div>");
        }
    }

    // #R11 - String.replaceAll sanitization (CxQL doesn't recognize replaceAll)
    public void handleCleaned(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("text");
        String cleaned = input.replaceAll("[^a-zA-Z0-9 ]", "");
        resp.getWriter().write("<p>" + cleaned + "</p>");
    }

    // #R12 - Whitelist validation (CxQL doesn't recognize contains check)
    public void handleStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String status = req.getParameter("status");
        java.util.Set<String> allowed = java.util.Set.of("active", "inactive", "pending");
        if (allowed.contains(status.toLowerCase())) {
            resp.getWriter().write("<span class=\"" + status + "\"></span>");
        }
    }

    // #R13 - Length-bounded substring (CxQL doesn't recognize length limit)
    public void handleTruncated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("title");
        String truncated = input.length() > 50 ? input.substring(0, 50) : input;
        String escaped = Sanitizer.escapeHtml(truncated);
        resp.getWriter().write("<h1>" + escaped + "</h1>");
    }

    // #R14 - Hex encoding (CxQL doesn't recognize hex conversion)
    public void handleHex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("color");
        if (input.matches("^[0-9a-fA-F]{6}$")) {
            resp.getWriter().write("<div style=\"color:#" + input + "\"></div>");
        }
    }

    // #R15 - Integer.toHexString (CxQL doesn't recognize numeric hex conversion)
    public void handleNumericHex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int value = Sanitizer.toInt(req.getParameter("value"));
        String hex = Integer.toHexString(value);
        resp.getWriter().write("<span>0x" + hex + "</span>");
    }

    // #R16 - StringBuilder with validation (CxQL doesn't track StringBuilder)
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

    // #R17 - Apache Commons StringEscapeUtils simulation
    public void handleHtmlEscape(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("content");
        String escaped = escapeHtml4(input);
        resp.getWriter().write("<div>" + escaped + "</div>");
    }

    // #R18 - OWASP Encoder simulation
    public void handleOwaspEncode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("attr");
        String encoded = encodeForHtmlAttribute(input);
        resp.getWriter().write("<input value=\"" + encoded + "\"/>");
    }

    // #R19 - JSON string escape (CxQL doesn't recognize JSON escaping)
    public void handleJsonEscape(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("json");
        String escaped = escapeJsonString(input);
        resp.getWriter().write("<script>var data = \"" + escaped + "\";</script>");
    }

    // #R20 - CSS escape (CxQL doesn't recognize CSS escaping)
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

