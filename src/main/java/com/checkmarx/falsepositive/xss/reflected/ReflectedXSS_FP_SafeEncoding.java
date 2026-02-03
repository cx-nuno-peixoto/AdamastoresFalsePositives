package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Safe Encoding Transformations
 * 
 * Pattern: User input encoded/transformed in ways that prevent XSS
 * 
 * These encoding transformations produce output that cannot execute as XSS:
 * - Base64 encoding produces alphanumeric + /+= only
 * - URL encoding escapes special characters
 * - Hex encoding produces only 0-9, A-F
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_SafeEncoding {
    
    /**
     * FALSE POSITIVE: Base64 encoding
     * Base64 output contains only A-Za-z0-9+/= (safe characters)
     */
    public void showBase64Encoded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("data");
        
        // Base64 encoded output cannot contain XSS characters
        String encoded = Base64.getEncoder().encodeToString(userInput.getBytes(StandardCharsets.UTF_8));
        
        PrintWriter out = response.getWriter();
        out.write("Encoded data: " + encoded); // FALSE POSITIVE - Base64 safe alphabet
    }
    
    /**
     * FALSE POSITIVE: URL encoding
     * URLEncoder escapes < > " ' & and other dangerous characters
     */
    public void showUrlEncoded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("query");
        
        // URL encoding escapes XSS characters
        String encoded = URLEncoder.encode(userInput, StandardCharsets.UTF_8.name());
        
        PrintWriter out = response.getWriter();
        out.write("Query: " + encoded); // FALSE POSITIVE - URL encoded
    }
    
    /**
     * FALSE POSITIVE: Hex encoding
     * Hex output is only 0-9, A-F characters
     */
    public void showHexEncoded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("value");
        
        // Hex encoding produces only safe characters
        StringBuilder hex = new StringBuilder();
        for (byte b : userInput.getBytes(StandardCharsets.UTF_8)) {
            hex.append(String.format("%02X", b));
        }
        
        PrintWriter out = response.getWriter();
        out.write("Hex: " + hex.toString()); // FALSE POSITIVE - Hex safe alphabet
    }
    
    /**
     * FALSE POSITIVE: Base64 URL-safe encoding
     * Base64 URL-safe uses - and _ instead of + and /
     */
    public void showBase64UrlEncoded(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("token");
        
        String encoded = Base64.getUrlEncoder().encodeToString(userInput.getBytes(StandardCharsets.UTF_8));
        
        PrintWriter out = response.getWriter();
        out.write("Token: " + encoded); // FALSE POSITIVE - URL-safe Base64
    }
    
    /**
     * FALSE POSITIVE: Integer parsing - output is numeric
     * If parsing succeeds, the value is a pure integer
     */
    public void showParsedInteger(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("id");
        
        try {
            int id = Integer.parseInt(userInput);
            PrintWriter out = response.getWriter();
            out.write("ID: " + id); // FALSE POSITIVE - Numeric only
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid ID");
        }
    }
    
    /**
     * FALSE POSITIVE: Long parsing - output is numeric
     */
    public void showParsedLong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("accountId");
        
        try {
            long accountId = Long.parseLong(userInput);
            PrintWriter out = response.getWriter();
            out.write("Account: " + accountId); // FALSE POSITIVE - Numeric only
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid account ID");
        }
    }
    
    /**
     * FALSE POSITIVE: Double parsing - output is numeric
     */
    public void showParsedDouble(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("price");
        
        try {
            double price = Double.parseDouble(userInput);
            PrintWriter out = response.getWriter();
            out.write("Price: " + price); // FALSE POSITIVE - Numeric only
        } catch (NumberFormatException e) {
            response.getWriter().write("Invalid price");
        }
    }
    
    /**
     * FALSE POSITIVE: Combined encoding transformation
     * Multiple safe transformations
     */
    public void showCombinedEncoding(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("data");
        
        // First Base64, then URL encode (extra safety)
        String base64 = Base64.getEncoder().encodeToString(userInput.getBytes(StandardCharsets.UTF_8));
        String urlEncoded = URLEncoder.encode(base64, StandardCharsets.UTF_8.name());
        
        PrintWriter out = response.getWriter();
        out.write("Safe data: " + urlEncoded); // FALSE POSITIVE - Double encoded
    }
    
    /**
     * FALSE POSITIVE: Hash digest (MD5/SHA)
     * Hash output is only hexadecimal characters
     */
    public void showHashDigest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userInput = request.getParameter("value");
        
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(userInput.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hash) {
                hexHash.append(String.format("%02x", b));
            }
            
            PrintWriter out = response.getWriter();
            out.write("Hash: " + hexHash.toString()); // FALSE POSITIVE - Hex only
        } catch (Exception e) {
            response.getWriter().write("Error");
        }
    }
}

