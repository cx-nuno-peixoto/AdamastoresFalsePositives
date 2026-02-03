package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Import Apache Commons Text (would be in actual deployment)
// import org.apache.commons.text.StringEscapeUtils;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Apache Commons Text
 * 
 * Pattern: User input sanitized using Apache Commons Text StringEscapeUtils
 * 
 * Apache Commons Text's StringEscapeUtils provides robust encoding methods
 * that prevent XSS attacks. CxQL doesn't recognize these as sanitizers.
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_ApacheCommonsText {
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeHtml4()
     * HTML 4.0 entity escaping
     */
    public void showEscapeHtml4(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("content");
        
        // Apache Commons Text - HTML4 escaping
        String safeOutput = StringEscapeUtils.escapeHtml4(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("Content: " + safeOutput); // FALSE POSITIVE - Properly escaped
    }
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeXml11()
     * XML 1.1 entity escaping
     */
    public void showEscapeXml11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("xmlData");
        
        String safeOutput = StringEscapeUtils.escapeXml11(userInput);
        
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.write("<result>" + safeOutput + "</result>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeXml10()
     * XML 1.0 entity escaping
     */
    public void showEscapeXml10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("data");
        
        String safeOutput = StringEscapeUtils.escapeXml10(userInput);
        
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.write("<data>" + safeOutput + "</data>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeEcmaScript()
     * JavaScript/ECMAScript escaping
     */
    public void showEscapeEcmaScript(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("jsData");
        
        String safeOutput = StringEscapeUtils.escapeEcmaScript(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("<script>var data = '" + safeOutput + "';</script>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeJson()
     * JSON string escaping
     */
    public void showEscapeJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("jsonValue");
        
        String safeOutput = StringEscapeUtils.escapeJson(userInput);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write("{\"value\": \"" + safeOutput + "\"}"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: StringEscapeUtils.escapeCsv()
     * CSV field escaping
     */
    public void showEscapeCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("csvField");
        
        String safeOutput = StringEscapeUtils.escapeCsv(userInput);
        
        response.setContentType("text/csv");
        PrintWriter out = response.getWriter();
        out.write("field1," + safeOutput + ",field3"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: Combined escaping for mixed output
     */
    public void showCombinedEscaping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String htmlContent = request.getParameter("html");
        String jsContent = request.getParameter("js");
        
        String safeHtml = StringEscapeUtils.escapeHtml4(htmlContent);
        String safeJs = StringEscapeUtils.escapeEcmaScript(jsContent);
        
        PrintWriter out = response.getWriter();
        out.write("<div>" + safeHtml + "</div>");
        out.write("<script>var msg = '" + safeJs + "';</script>"); // FALSE POSITIVE
    }
    
    // ========== MOCK CLASS FOR COMPILATION ==========
    private static class StringEscapeUtils {
        public static String escapeHtml4(String input) {
            if (input == null) return null;
            return input.replace("&", "&amp;").replace("<", "&lt;")
                       .replace(">", "&gt;").replace("\"", "&quot;");
        }
        public static String escapeXml11(String input) { return escapeHtml4(input); }
        public static String escapeXml10(String input) { return escapeHtml4(input); }
        public static String escapeEcmaScript(String input) {
            if (input == null) return null;
            return input.replace("\\", "\\\\").replace("'", "\\'")
                       .replace("\"", "\\\"").replace("\n", "\\n");
        }
        public static String escapeJson(String input) { return escapeEcmaScript(input); }
        public static String escapeCsv(String input) {
            if (input == null) return null;
            if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
                return "\"" + input.replace("\"", "\"\"") + "\"";
            }
            return input;
        }
    }
}

