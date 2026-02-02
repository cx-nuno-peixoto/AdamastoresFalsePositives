package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Regex Validation
 * 
 * Pattern: User input validated using Pattern.matcher() with strict patterns
 * 
 * These scenarios demonstrate that regex validation with Pattern.matcher() sanitizes input.
 * Only input matching safe patterns (alphanumeric, numeric, etc.) is output.
 */
public class ReflectedXSS_RegexValidation {
    
    // Scenario 1: Alphanumeric validation
    public void showValidatedUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(username);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only alphanumeric passes
            out.write("Username: " + username); // SAFE: Validated alphanumeric
        } else {
            out.write("Invalid username");
        }
    }
    
    // Scenario 2: Numeric validation
    public void showValidatedId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(id);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only digits pass
            out.write("ID: " + id); // SAFE: Validated numeric string
        } else {
            out.write("Invalid ID");
        }
    }
    
    // Scenario 3: Email validation
    public void showValidatedEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only valid email format passes
            out.write("Email: " + email); // SAFE: Validated email
        } else {
            out.write("Invalid email");
        }
    }
    
    // Scenario 4: Phone number validation
    public void showValidatedPhone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone = request.getParameter("phone");
        Pattern pattern = Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$");
        Matcher matcher = pattern.matcher(phone);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only XXX-XXX-XXXX format passes
            out.write("Phone: " + phone); // SAFE: Validated phone format
        } else {
            out.write("Invalid phone");
        }
    }
    
    // Scenario 5: Alphanumeric with underscore
    public void showValidatedCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(code);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only alphanumeric + underscore
            out.write("Code: " + code); // SAFE: Validated
        } else {
            out.write("Invalid code");
        }
    }
    
    // Scenario 6: UUID validation
    public void showValidatedUUID(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uuid = request.getParameter("uuid");
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Matcher matcher = pattern.matcher(uuid);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only UUID format passes
            out.write("UUID: " + uuid); // SAFE: Validated UUID
        } else {
            out.write("Invalid UUID");
        }
    }
    
    // Scenario 7: Hexadecimal validation
    public void showValidatedHex(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hex = request.getParameter("hex");
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]+$");
        Matcher matcher = pattern.matcher(hex);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only hex characters pass
            out.write("Hex: " + hex); // SAFE: Validated hex
        } else {
            out.write("Invalid hex");
        }
    }
    
    // Scenario 8: Date validation (YYYY-MM-DD)
    public void showValidatedDate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String date = request.getParameter("date");
        Pattern pattern = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
        Matcher matcher = pattern.matcher(date);
        
        PrintWriter out = response.getWriter();
        if (matcher.matches()) { // SAFE: Only YYYY-MM-DD format passes
            out.write("Date: " + date); // SAFE: Validated date format
        } else {
            out.write("Invalid date");
        }
    }
}

