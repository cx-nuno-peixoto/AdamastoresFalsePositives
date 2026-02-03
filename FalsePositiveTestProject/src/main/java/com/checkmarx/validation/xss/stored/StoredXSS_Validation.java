package com.checkmarx.validation.xss.stored;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * VALIDATION FILE: Stored XSS - Good vs Bad Findings
 *
 * This file contains BOTH:
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 *
 * Use this to validate that query fixes:
 * 1. Eliminate false positives (reduce noise) - BAD findings
 * 2. Preserve true positive detection (maintain security coverage) - GOOD findings
 */
public class StoredXSS_Validation {
    
    // Mock entities
    public static class User {
        private Long id;
        private String name;
        private String email;
        private Integer loginCount;
        private boolean active;
        
        public User(Long id, String name, String email, Integer loginCount, boolean active) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.loginCount = loginCount;
            this.active = active;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Integer getLoginCount() { return loginCount; }
        public boolean isActive() { return active; }
    }
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========

    // BAD: Numeric getter from database
    public void badNumericId(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Long userId = users.get(0).getId(); // SAFE: getId() returns Long
        out.write("User ID: " + userId); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Integer getter from database
    public void badLoginCount(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Integer count = users.get(0).getLoginCount(); // SAFE: getLoginCount() returns Integer
        out.write("Login count: " + count); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Boolean getter from database
    public void badBooleanField(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        boolean active = users.get(0).isActive(); // SAFE: isActive() returns boolean
        out.write("Active: " + active); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }

    // BAD: Numeric ID in loop
    public void badNumericLoop(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        for (User user : users) {
            Long id = user.getId(); // SAFE: getId() returns Long
            out.write("<li>User ID: " + id + "</li>"); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
        }
    }

    // BAD: Multiple numeric fields
    public void badMultipleNumeric(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        User user = users.get(0);
        out.write("ID: " + user.getId()); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
        out.write(", Count: " + user.getLoginCount()); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
        out.write(", Active: " + user.isActive()); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========

    // GOOD: String field from database (user-generated content)
    public void goodUserName(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String name = users.get(0).getName(); // VULNERABLE: getName() returns String (user input)
        out.write("User name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Email field from database (user-generated content)
    public void goodUserEmail(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String email = users.get(0).getEmail(); // VULNERABLE: getEmail() returns String (user input)
        out.write("Email: " + email); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: String field in loop
    public void goodStringLoop(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        for (User user : users) {
            String name = user.getName(); // VULNERABLE: String from database
            out.write("<li>" + name + "</li>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
        }
    }

    // GOOD: Direct getter in output
    public void goodDirectGetter(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("Name: " + users.get(0).getName()); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Multiple string fields
    public void goodMultipleStrings(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        User user = users.get(0);
        out.write("Name: " + user.getName()); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
        out.write(", Email: " + user.getEmail()); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: String field in HTML attribute
    public void goodHtmlAttribute(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String name = users.get(0).getName(); // VULNERABLE: String from database
        out.write("<div title='" + name + "'>User</div>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: String field in JavaScript
    public void goodJavaScript(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String name = users.get(0).getName(); // VULNERABLE: String from database
        out.write("<script>var userName = '" + name + "';</script>"); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // MIXED: Numeric ID (BAD/FP) + String name (GOOD/TP)
    public void mixedGoodAndBad(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        User user = users.get(0);

        Long id = user.getId(); // SAFE: Numeric
        out.write("ID: " + id); // FALSE POSITIVE (BAD) - Should NOT be flagged after fix

        String name = user.getName(); // VULNERABLE: String
        out.write(", Name: " + name); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // ========== ADDITIONAL FALSE POSITIVES (BAD) ==========

    // BAD: Numeric getter from session - SAFE because numeric
    public void badNumericFromSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        Integer pageCount = (Integer) request.getSession().getAttribute("pageCount");
        out.write("Page count: " + pageCount); // FALSE POSITIVE (BAD) - Numeric from session
    }

    // BAD: Enum value from database - SAFE because enum has limited values
    public void badEnumFromDatabase(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        boolean status = users.get(0).isActive(); // SAFE: Boolean value
        String statusText = status ? "Active" : "Inactive"; // SAFE: Ternary with literals
        out.write("Status: " + statusText); // FALSE POSITIVE (BAD) - Ternary produces safe literals
    }

    // ========== ADDITIONAL TRUE POSITIVES (GOOD) ==========

    // GOOD: Session attribute - user-controlled stored data
    public void goodSessionData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userInput = request.getParameter("input");
        request.getSession().setAttribute("storedInput", userInput); // Stored XSS

        // Later retrieval and output
        String stored = (String) request.getSession().getAttribute("storedInput");
        out.write("Stored: " + stored); // TRUE POSITIVE (GOOD) - String from session
    }

    // GOOD: Cookie data - user-controlled
    public void goodCookieData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("userPref".equals(cookie.getName())) {
                    out.write("Preference: " + cookie.getValue()); // TRUE POSITIVE (GOOD) - Cookie value
                }
            }
        }
    }

    // GOOD: CSS context - String from database in style
    public void goodCSSContext(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String theme = users.get(0).getName(); // Simulating theme stored in DB
        out.write("<div style='color: " + theme + "'>Content</div>"); // TRUE POSITIVE (GOOD) - CSS injection
    }

    // GOOD: Event handler context
    public void goodEventHandler(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String action = users.get(0).getName(); // Simulating action stored in DB
        out.write("<button onclick='" + action + "'>Click</button>"); // TRUE POSITIVE (GOOD) - Event handler XSS
    }
}

