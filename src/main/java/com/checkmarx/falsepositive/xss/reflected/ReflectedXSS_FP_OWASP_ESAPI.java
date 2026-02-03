package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Import OWASP ESAPI (would be in actual deployment)
// import org.owasp.esapi.ESAPI;
// import org.owasp.esapi.Encoder;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - OWASP ESAPI Encoders
 * 
 * Pattern: User input sanitized using OWASP ESAPI encoder methods
 * 
 * OWASP ESAPI is the industry standard for input encoding/sanitization.
 * CxQL should recognize these as sanitizers but currently doesn't.
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_OWASP_ESAPI {
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForHTML()
     * OWASP standard HTML context encoding
     */
    public void showEncodeForHTML(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("name");
        
        // OWASP ESAPI encoder - HTML context
        String safeOutput = ESAPI.encoder().encodeForHTML(userInput);
        
        PrintWriter out = response.getWriter();
        out.write("Welcome: " + safeOutput); // FALSE POSITIVE - ESAPI encoded
    }
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForHTMLAttribute()
     * Safe encoding for HTML attributes
     */
    public void showEncodeForHTMLAttribute(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        
        String safeTitle = ESAPI.encoder().encodeForHTMLAttribute(title);
        
        PrintWriter out = response.getWriter();
        out.write("<div title=\"" + safeTitle + "\">Content</div>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForJavaScript()
     * Safe encoding for JavaScript context
     */
    public void showEncodeForJavaScript(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter("data");
        
        String safeData = ESAPI.encoder().encodeForJavaScript(data);
        
        PrintWriter out = response.getWriter();
        out.write("<script>var data = '" + safeData + "';</script>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForURL()
     * Safe encoding for URL context
     */
    public void showEncodeForURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirect = request.getParameter("redirect");
        
        String safeUrl = ESAPI.encoder().encodeForURL(redirect);
        
        PrintWriter out = response.getWriter();
        out.write("<a href=\"/page?next=" + safeUrl + "\">Continue</a>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForCSS()
     * Safe encoding for CSS context
     */
    public void showEncodeForCSS(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String color = request.getParameter("color");
        
        String safeColor = ESAPI.encoder().encodeForCSS(color);
        
        PrintWriter out = response.getWriter();
        out.write("<div style=\"background: " + safeColor + "\">Styled</div>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: ESAPI.encoder().encodeForXML()
     * Safe encoding for XML context
     */
    public void showEncodeForXML(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String content = request.getParameter("content");
        
        String safeContent = ESAPI.encoder().encodeForXML(content);
        
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        out.write("<data>" + safeContent + "</data>"); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: Combined ESAPI encoding
     * Multiple context encoding for complex output
     */
    public void showCombinedEncoding(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String desc = request.getParameter("desc");
        
        String safeName = ESAPI.encoder().encodeForHTML(name);
        String safeDesc = ESAPI.encoder().encodeForHTMLAttribute(desc);
        
        PrintWriter out = response.getWriter();
        out.write("<div title=\"" + safeDesc + "\">Hello " + safeName + "</div>"); // FALSE POSITIVE
    }
    
    // ========== MOCK CLASSES FOR COMPILATION ==========
    private static class ESAPI {
        private static final Encoder encoder = new Encoder();
        public static Encoder encoder() { return encoder; }
    }
    
    private static class Encoder {
        public String encodeForHTML(String input) {
            if (input == null) return null;
            return input.replace("&", "&amp;").replace("<", "&lt;")
                       .replace(">", "&gt;").replace("\"", "&quot;");
        }
        public String encodeForHTMLAttribute(String input) { return encodeForHTML(input); }
        public String encodeForJavaScript(String input) {
            if (input == null) return null;
            return input.replace("\\", "\\\\").replace("'", "\\'")
                       .replace("\"", "\\\"").replace("\n", "\\n");
        }
        public String encodeForURL(String input) {
            if (input == null) return null;
            try { return java.net.URLEncoder.encode(input, "UTF-8"); }
            catch (Exception e) { return input; }
        }
        public String encodeForCSS(String input) { return encodeForHTML(input); }
        public String encodeForXML(String input) { return encodeForHTML(input); }
    }
}

