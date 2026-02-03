package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Import Spring Security utilities (would be in actual deployment)
// import org.springframework.web.util.HtmlUtils;
// import org.springframework.security.core.context.SecurityContextHolder;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Spring Security Sanitizers
 * 
 * Pattern: User input sanitized using Spring Security's HtmlUtils methods
 * 
 * These scenarios demonstrate that Spring Security's HtmlUtils sanitization
 * prevents XSS attacks but CxQL doesn't recognize these as sanitizers.
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_SpringSecurity {
    
    /**
     * FALSE POSITIVE: HtmlUtils.htmlEscape() sanitization
     * Spring's HtmlUtils.htmlEscape() converts &lt;script&gt; to &amp;lt;script&amp;gt;
     * This prevents XSS execution in the browser.
     */
    public void showHtmlEscaped(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("name");
        
        // Spring Security sanitizer - escapes HTML entities
        String safeOutput = HtmlUtils.htmlEscape(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("Welcome: " + safeOutput); // FALSE POSITIVE - Properly escaped
    }
    
    /**
     * FALSE POSITIVE: HtmlUtils.htmlEscapeDecimal() sanitization
     * Escapes to decimal entities: < becomes &#60;
     */
    public void showHtmlEscapeDecimal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("comment");
        
        // Spring Security sanitizer - escapes to decimal entities
        String safeOutput = HtmlUtils.htmlEscapeDecimal(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("Comment: " + safeOutput); // FALSE POSITIVE - Properly escaped
    }
    
    /**
     * FALSE POSITIVE: HtmlUtils.htmlEscapeHex() sanitization
     * Escapes to hexadecimal entities: < becomes &#x3c;
     */
    public void showHtmlEscapeHex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("message");
        
        // Spring Security sanitizer - escapes to hex entities
        String safeOutput = HtmlUtils.htmlEscapeHex(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("Message: " + safeOutput); // FALSE POSITIVE - Properly escaped
    }
    
    /**
     * FALSE POSITIVE: Chain of HtmlUtils escaping
     * Multiple escape operations for extra safety
     */
    public void showChainedEscaping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("data");
        
        // Chain of sanitizers
        String escaped = HtmlUtils.htmlEscape(userInput);
        String safeOutput = escaped.replace("'", "&#39;"); // Extra single quote escaping
        
        PrintWriter out = response.getWriter();
        out.write("Data: " + safeOutput); // FALSE POSITIVE - Double escaped
    }
    
    /**
     * FALSE POSITIVE: HtmlUtils in attribute context
     * Escaping user input for HTML attribute
     */
    public void showAttributeEscaped(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        
        String safeTitle = HtmlUtils.htmlEscape(title);
        
        PrintWriter out = response.getWriter();
        out.write("<div title=\"" + safeTitle + "\">Content</div>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: HtmlUtils with null check
     * Safe handling of null values
     */
    public void showNullSafeEscaped(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("value");
        
        String safeOutput = (userInput != null) ? HtmlUtils.htmlEscape(userInput) : "";
        
        PrintWriter out = response.getWriter();
        out.write("Value: " + safeOutput); // FALSE POSITIVE - Null-safe escaped
    }
    
    // ========== MOCK CLASS FOR COMPILATION ==========
    // In actual Spring project, this would be org.springframework.web.util.HtmlUtils
    private static class HtmlUtils {
        public static String htmlEscape(String input) {
            if (input == null) return null;
            return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
        }
        
        public static String htmlEscapeDecimal(String input) {
            if (input == null) return null;
            StringBuilder sb = new StringBuilder();
            for (char c : input.toCharArray()) {
                if (c == '<' || c == '>' || c == '&' || c == '"') {
                    sb.append("&#").append((int) c).append(";");
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        
        public static String htmlEscapeHex(String input) {
            if (input == null) return null;
            StringBuilder sb = new StringBuilder();
            for (char c : input.toCharArray()) {
                if (c == '<' || c == '>' || c == '&' || c == '"') {
                    sb.append("&#x").append(Integer.toHexString(c)).append(";");
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
    }
}

