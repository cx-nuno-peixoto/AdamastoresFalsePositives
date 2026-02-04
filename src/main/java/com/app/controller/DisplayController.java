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

    // #X01 - DB name escaped through custom escaper (CxQL doesn't recognize)
    public void renderEntityName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    // #X02 - DB name extracted alphanumeric only (CxQL doesn't recognize)
    public void renderCleanedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String cleaned = name.replaceAll("[^a-zA-Z0-9 ]", "");
        resp.getWriter().write("<div>" + cleaned + "</div>");
    }

    // #X03 - DB description with Pattern validation (CxQL doesn't recognize guard)
    public void renderValidatedDescription(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String desc = entityService.getEntityDescription(id);
        if (SAFE_CHARS.matcher(desc).matches()) {
            resp.getWriter().write("<p>" + desc + "</p>");
        }
    }

    // #X04 - DB name URL encoded (CxQL doesn't recognize URLEncoder)
    public void renderUrlEncodedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
        resp.getWriter().write("<a href=\"/entity?name=" + encoded + "\">View</a>");
    }

    // #X05 - DB name masked (CxQL doesn't recognize mask)
    public void renderMaskedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String masked = Sanitizer.mask(name, 3);
        resp.getWriter().write("<span>" + masked + "</span>");
    }

    // #X06 - DB name substring with length limit (CxQL doesn't track length)
    public void renderTruncatedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String truncated = name.length() > 20 ? name.substring(0, 20) : name;
        String escaped = Sanitizer.escapeHtml(truncated);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    // #X07 - DB code via Enum.valueOf (CxQL doesn't recognize enum validation)
    public void renderEntityType(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String typeStr = entityService.getEntityType(id);
        EntityType type = EntityType.valueOf(typeStr.toUpperCase());
        resp.getWriter().write("<span class=\"type-" + type.name() + "\">" + type.name() + "</span>");
    }

    // #X08 - DB value with whitelist check (CxQL doesn't recognize contains)
    public void renderEntityCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String category = entityService.getEntityCategory(id);
        java.util.Set<String> allowed = java.util.Set.of("product", "service", "subscription");
        if (allowed.contains(category.toLowerCase())) {
            resp.getWriter().write("<div class=\"" + category + "\">" + category + "</div>");
        }
    }

    // #X09 - DB name with StringBuilder filtering (CxQL doesn't track StringBuilder)
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

    // #X10 - DB name with UUID validation (CxQL doesn't recognize UUID.fromString)
    public void renderEntityUuid(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String uuidStr = entityService.getEntityUuid(id);
        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
        resp.getWriter().write("<span data-uuid=\"" + uuid.toString() + "\"></span>");
    }

    // #X11 - Account name escaped (CxQL doesn't recognize cross-file sanitizer)
    public void renderAccountName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = accountService.getAccountName(id);
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    // #X12 - Account email domain only (CxQL doesn't recognize string split)
    public void renderAccountDomain(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String email = accountService.getAccountEmail(id);
        String domain = email.contains("@") ? email.split("@")[1] : "";
        String escaped = Sanitizer.escapeHtml(domain);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }

    // #X13 - DB name through hex encoding (CxQL doesn't recognize hex conversion)
    public void renderHexEncodedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        StringBuilder hex = new StringBuilder();
        for (char c : name.toCharArray()) {
            hex.append(String.format("%02x", (int) c));
        }
        resp.getWriter().write("<span data-name=\"" + hex.toString() + "\"></span>");
    }

    // #X14 - DB name through Base64 (CxQL doesn't recognize Base64)
    public void renderBase64Name(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEntityName(id);
        String encoded = java.util.Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        resp.getWriter().write("<span data-encoded=\"" + encoded + "\"></span>");
    }

    // #X15 - DB content with JSON escape (CxQL doesn't recognize JSON escaping)
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

