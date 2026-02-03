package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

// Import Bean Validation / Hibernate Validator (would be in actual deployment)
// import javax.validation.Valid;
// import javax.validation.constraints.*;
// import org.hibernate.validator.constraints.*;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Bean Validation / Hibernate Validator
 * 
 * Pattern: User input validated using Bean Validation annotations and validators
 * 
 * Bean Validation with @Pattern annotations constrain input to safe patterns.
 * CxQL doesn't recognize this as validation/sanitization.
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_BeanValidation {
    
    /**
     * FALSE POSITIVE: @Pattern validated alphanumeric
     * Only alphanumeric characters pass validation
     */
    public void showPatternValidatedAlphanumeric(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        
        // Bean Validation: @Pattern(regexp = "^[a-zA-Z0-9]+$")
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Username: " + dto.getUsername()); // FALSE POSITIVE - Validated
        }
    }
    
    /**
     * FALSE POSITIVE: @Email validated email
     * Only valid email format passes
     */
    public void showEmailValidated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        
        // Bean Validation: @Email
        ContactDTO dto = new ContactDTO();
        dto.setEmail(email);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Email: " + dto.getEmail()); // FALSE POSITIVE - Validated email format
        }
    }
    
    /**
     * FALSE POSITIVE: @Size + @Pattern validated
     * Size constrained and pattern validated
     */
    public void showSizeAndPatternValidated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        
        // Bean Validation: @Size(min=4, max=10) @Pattern(regexp = "^[A-Z0-9]+$")
        ProductDTO dto = new ProductDTO();
        dto.setCode(code);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Product Code: " + dto.getCode()); // FALSE POSITIVE - Size and pattern validated
        }
    }
    
    /**
     * FALSE POSITIVE: @SafeHtml validated (Hibernate Validator)
     * HTML sanitized by Hibernate Validator
     */
    public void showSafeHtmlValidated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String description = request.getParameter("description");
        
        // Hibernate Validator: @SafeHtml(whitelistType = SafeHtml.WhiteListType.NONE)
        ProductDTO dto = new ProductDTO();
        dto.setDescription(description);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Description: " + dto.getDescription()); // FALSE POSITIVE - SafeHtml validated
        }
    }
    
    /**
     * FALSE POSITIVE: @Digits validated numeric
     * Only numeric values pass
     */
    public void showDigitsValidated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quantity = request.getParameter("quantity");
        
        // Bean Validation: @Digits(integer=5, fraction=0)
        OrderDTO dto = new OrderDTO();
        dto.setQuantity(quantity);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Quantity: " + dto.getQuantity()); // FALSE POSITIVE - Digits only
        }
    }
    
    /**
     * FALSE POSITIVE: Custom validator with whitelist
     * Custom validation logic with whitelist
     */
    public void showCustomValidatorWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String category = request.getParameter("category");
        
        // Custom: @AllowedValues({"electronics", "books", "clothing"})
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory(category);
        
        Set<String> violations = validate(dto);
        if (violations.isEmpty()) {
            PrintWriter out = response.getWriter();
            out.write("Category: " + dto.getCategory()); // FALSE POSITIVE - Whitelisted value
        }
    }
    
    // ========== MOCK CLASSES FOR COMPILATION ==========
    private Set<String> validate(Object dto) {
        return java.util.Collections.emptySet();
    }
    
    private static class UserDTO {
        private String username; // @Pattern(regexp = "^[a-zA-Z0-9]+$")
        public String getUsername() { return username; }
        public void setUsername(String u) { this.username = u; }
    }
    
    private static class ContactDTO {
        private String email; // @Email
        public String getEmail() { return email; }
        public void setEmail(String e) { this.email = e; }
    }
    
    private static class ProductDTO {
        private String code; // @Size(min=4, max=10) @Pattern(regexp = "^[A-Z0-9]+$")
        private String description; // @SafeHtml
        public String getCode() { return code; }
        public void setCode(String c) { this.code = c; }
        public String getDescription() { return description; }
        public void setDescription(String d) { this.description = d; }
    }
    
    private static class OrderDTO {
        private String quantity; // @Digits(integer=5, fraction=0)
        public String getQuantity() { return quantity; }
        public void setQuantity(String q) { this.quantity = q; }
    }
    
    private static class CategoryDTO {
        private String category; // @AllowedValues({"electronics", "books", "clothing"})
        public String getCategory() { return category; }
        public void setCategory(String c) { this.category = c; }
    }
}

