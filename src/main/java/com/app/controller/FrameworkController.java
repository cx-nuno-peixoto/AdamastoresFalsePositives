package com.app.controller;

import javax.servlet.http.*;
import java.io.*;

// Framework imports - these would be real in production
// import org.owasp.esapi.ESAPI;
// import org.apache.commons.text.StringEscapeUtils;
// import org.springframework.web.util.HtmlUtils;
// import org.apache.struts.util.ResponseUtils;
// import com.google.common.html.HtmlEscapers;
// import org.jsoup.Jsoup;
// import org.jsoup.safety.Safelist;

/**
 * Framework Sanitizer False Positive Scenarios
 * Tests known framework sanitizers that CxQL SHOULD recognize
 * If flagged, indicates CxQL sanitizer list is incomplete
 */
public class FrameworkController extends HttpServlet {

    /*
     * #F01 - OWASP ESAPI encodeForHTML
     * WHY SAFE: ESAPI.encoder().encodeForHTML() is the gold standard XSS sanitizer.
     *           Recommended by OWASP for all HTML context output.
     * WHY CXQL SHOULD RECOGNIZE: ESAPI is in CxQL's sanitizer list.
     * TEST PURPOSE: Verify ESAPI sanitizer recognition works correctly.
     */
    public void handleEsapiHtml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("data");
        // String safe = ESAPI.encoder().encodeForHTML(input);
        String safe = encodeForHTML(input); // Simulated
        resp.getWriter().write("<div>" + safe + "</div>");
    }

    /*
     * #F02 - OWASP ESAPI encodeForJavaScript
     * WHY SAFE: ESAPI.encoder().encodeForJavaScript() escapes for JS string context.
     *           Prevents XSS in JavaScript string literals.
     * WHY CXQL SHOULD RECOGNIZE: ESAPI JavaScript encoder is in sanitizer list.
     * TEST PURPOSE: Verify ESAPI JS encoder recognition.
     */
    public void handleEsapiJs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("data");
        // String safe = ESAPI.encoder().encodeForJavaScript(input);
        String safe = encodeForJavaScript(input); // Simulated
        resp.getWriter().write("<script>var x = '" + safe + "';</script>");
    }

    /*
     * #F03 - OWASP ESAPI encodeForURL
     * WHY SAFE: ESAPI.encoder().encodeForURL() URL-encodes for safe URL embedding.
     *           Prevents URL injection and XSS in URL context.
     * WHY CXQL SHOULD RECOGNIZE: ESAPI URL encoder is in sanitizer list.
     * TEST PURPOSE: Verify ESAPI URL encoder recognition.
     */
    public void handleEsapiUrl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("redirect");
        // String safe = ESAPI.encoder().encodeForURL(input);
        String safe = encodeForURL(input); // Simulated
        resp.getWriter().write("<a href=\"" + safe + "\">Link</a>");
    }

    /*
     * #F04 - Apache Commons Text escapeHtml4
     * WHY SAFE: StringEscapeUtils.escapeHtml4() escapes HTML special characters.
     *           Industry standard library used by millions of applications.
     * WHY CXQL SHOULD RECOGNIZE: Apache Commons is in CxQL's sanitizer list.
     * TEST PURPOSE: Verify Apache Commons HTML escaper recognition.
     */
    public void handleCommonsHtml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("text");
        // String safe = StringEscapeUtils.escapeHtml4(input);
        String safe = escapeHtml4(input); // Simulated
        resp.getWriter().write("<p>" + safe + "</p>");
    }

    /*
     * #F05 - Apache Commons Text escapeXml11
     * WHY SAFE: StringEscapeUtils.escapeXml11() escapes XML special characters.
     *           Safe for XML/XHTML output contexts.
     * WHY CXQL SHOULD RECOGNIZE: Apache Commons XML escaper is in sanitizer list.
     * TEST PURPOSE: Verify Apache Commons XML escaper recognition.
     */
    public void handleCommonsXml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("xml");
        // String safe = StringEscapeUtils.escapeXml11(input);
        String safe = escapeXml11(input); // Simulated
        resp.setContentType("application/xml");
        resp.getWriter().write("<data>" + safe + "</data>");
    }

    /*
     * #F06 - Apache Commons Text escapeEcmaScript
     * WHY SAFE: StringEscapeUtils.escapeEcmaScript() escapes for JavaScript.
     *           Prevents XSS in JavaScript string contexts.
     * WHY CXQL SHOULD RECOGNIZE: Apache Commons JS escaper should be recognized.
     * TEST PURPOSE: Verify Apache Commons JavaScript escaper recognition.
     */
    public void handleCommonsJs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("js");
        // String safe = StringEscapeUtils.escapeEcmaScript(input);
        String safe = escapeEcmaScript(input); // Simulated
        resp.getWriter().write("<script>alert('" + safe + "');</script>");
    }

    /*
     * #F07 - Spring HtmlUtils.htmlEscape
     * WHY SAFE: Spring's HtmlUtils.htmlEscape() is the standard Spring XSS sanitizer.
     *           Used throughout Spring MVC applications.
     * WHY CXQL SHOULD RECOGNIZE: Spring HtmlUtils is in CxQL's sanitizer list.
     * TEST PURPOSE: Verify Spring HtmlUtils recognition.
     */
    public void handleSpringHtml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("content");
        // String safe = HtmlUtils.htmlEscape(input);
        String safe = springHtmlEscape(input); // Simulated
        resp.getWriter().write("<span>" + safe + "</span>");
    }

    /*
     * #F08 - Spring HtmlUtils.htmlEscapeDecimal
     * WHY SAFE: Spring's htmlEscapeDecimal uses decimal numeric entities.
     *           Alternative encoding that's equally safe.
     * WHY CXQL SHOULD RECOGNIZE: Spring HtmlUtils variants should be recognized.
     * TEST PURPOSE: Verify Spring decimal escape recognition.
     */
    public void handleSpringDecimal(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("content");
        // String safe = HtmlUtils.htmlEscapeDecimal(input);
        String safe = springHtmlEscapeDecimal(input); // Simulated
        resp.getWriter().write("<div>" + safe + "</div>");
    }

    /*
     * #F09 - Spring HtmlUtils.htmlEscapeHex
     * WHY SAFE: Spring's htmlEscapeHex uses hexadecimal numeric entities.
     *           Alternative encoding that's equally safe.
     * WHY CXQL SHOULD RECOGNIZE: Spring HtmlUtils variants should be recognized.
     * TEST PURPOSE: Verify Spring hex escape recognition.
     */
    public void handleSpringHex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("content");
        // String safe = HtmlUtils.htmlEscapeHex(input);
        String safe = springHtmlEscapeHex(input); // Simulated
        resp.getWriter().write("<div>" + safe + "</div>");
    }

    /*
     * #F10 - Apache Struts ResponseUtils.filter
     * WHY SAFE: Struts ResponseUtils.filter() escapes HTML special characters.
     *           Legacy but still widely used in Struts applications.
     * WHY CXQL SHOULD RECOGNIZE: Struts ResponseUtils is in sanitizer list.
     * TEST PURPOSE: Verify Struts filter recognition.
     */
    public void handleStrutsFilter(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("message");
        // String safe = ResponseUtils.filter(input);
        String safe = strutsFilter(input); // Simulated
        resp.getWriter().write("<p>" + safe + "</p>");
    }

    /*
     * #F11 - Guava HtmlEscapers
     * WHY SAFE: Guava's HtmlEscapers.htmlEscaper().escape() is Google's HTML escaper.
     *           High-quality implementation from Google's core libraries.
     * WHY CXQL SHOULD RECOGNIZE: Guava is a major framework that should be recognized.
     * TEST PURPOSE: Verify Guava HTML escaper recognition.
     */
    public void handleGuavaHtml(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("text");
        // String safe = HtmlEscapers.htmlEscaper().escape(input);
        String safe = guavaHtmlEscape(input); // Simulated
        resp.getWriter().write("<span>" + safe + "</span>");
    }

    /*
     * #F12 - JSoup clean with Safelist
     * WHY SAFE: Jsoup.clean() with Safelist removes all non-whitelisted HTML.
     *           Powerful sanitizer that allows safe HTML while blocking XSS.
     * WHY CXQL SHOULD RECOGNIZE: JSoup is a major HTML sanitization library.
     * TEST PURPOSE: Verify JSoup clean recognition.
     */
    public void handleJsoupClean(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("html");
        // String safe = Jsoup.clean(input, Safelist.basic());
        String safe = jsoupClean(input); // Simulated
        resp.getWriter().write("<div>" + safe + "</div>");
    }

    /*
     * #F13 - Java URLEncoder.encode
     * WHY SAFE: URLEncoder.encode() converts special chars to %XX sequences.
     *           Standard Java URL encoding prevents XSS in URL contexts.
     * WHY CXQL SHOULD RECOGNIZE: Java standard library URL encoder.
     * TEST PURPOSE: Verify URLEncoder recognition for URL context.
     */
    public void handleUrlEncoder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("query");
        String safe = java.net.URLEncoder.encode(input, "UTF-8");
        resp.getWriter().write("<a href=\"search?" + safe + "\">Search</a>");
    }

    /*
     * #F14 - OWASP ESAPI encodeForHTMLAttribute
     * WHY SAFE: ESAPI encodeForHTMLAttribute() is specifically for attribute context.
     *           Handles attribute-specific XSS vectors.
     * WHY CXQL SHOULD RECOGNIZE: ESAPI attribute encoder should be recognized.
     * TEST PURPOSE: Verify ESAPI attribute encoder recognition.
     */
    public void handleEsapiAttr(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("title");
        // String safe = ESAPI.encoder().encodeForHTMLAttribute(input);
        String safe = encodeForHTMLAttribute(input); // Simulated
        resp.getWriter().write("<div title=\"" + safe + "\">Content</div>");
    }

    /*
     * #F15 - OWASP ESAPI encodeForCSS
     * WHY SAFE: ESAPI encodeForCSS() escapes for CSS context.
     *           Prevents CSS injection attacks.
     * WHY CXQL SHOULD RECOGNIZE: ESAPI CSS encoder should be recognized.
     * TEST PURPOSE: Verify ESAPI CSS encoder recognition.
     */
    public void handleEsapiCss(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("color");
        // String safe = ESAPI.encoder().encodeForCSS(input);
        String safe = encodeForCSS(input); // Simulated
        resp.getWriter().write("<div style=\"color: " + safe + "\">Text</div>");
    }

    // ========== Simulated Framework Methods ==========
    // These simulate the actual framework methods for testing purposes
    // In production, use the real framework imports

    private String encodeForHTML(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }

    private String encodeForJavaScript(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("'", "\\'")
                    .replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String encodeForURL(String input) {
        try {
            return java.net.URLEncoder.encode(input != null ? input : "", "UTF-8");
        } catch (Exception e) { return ""; }
    }

    private String escapeHtml4(String input) {
        return encodeForHTML(input);
    }

    private String escapeXml11(String input) {
        return encodeForHTML(input);
    }

    private String escapeEcmaScript(String input) {
        return encodeForJavaScript(input);
    }

    private String springHtmlEscape(String input) {
        return encodeForHTML(input);
    }

    private String springHtmlEscapeDecimal(String input) {
        return encodeForHTML(input);
    }

    private String springHtmlEscapeHex(String input) {
        return encodeForHTML(input);
    }

    private String strutsFilter(String input) {
        return encodeForHTML(input);
    }

    private String guavaHtmlEscape(String input) {
        return encodeForHTML(input);
    }

    private String jsoupClean(String input) {
        // Simulates Jsoup.clean with basic safelist
        if (input == null) return "";
        return input.replaceAll("<script[^>]*>.*?</script>", "")
                    .replaceAll("<[^>]+>", "");
    }

    private String encodeForHTMLAttribute(String input) {
        return encodeForHTML(input);
    }

    private String encodeForCSS(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9#]", "");
    }
}

