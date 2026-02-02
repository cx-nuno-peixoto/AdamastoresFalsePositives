package com.checkmarx.falsepositive.webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Application Servlet - Demonstrates false positives in web context
 * Combines multiple false positive patterns in a realistic servlet
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {
    
    // Mock User entity
    public static class User {
        private Long id;
        private String name;
        private Integer accountId;
        
        public User(Long id, String name, Integer accountId) {
            this.id = id;
            this.name = name;
            this.accountId = accountId;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Integer getAccountId() { return accountId; }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // FALSE POSITIVE: Type conversion (Reflected XSS)
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null) {
            long userId = Long.parseLong(userIdParam); // SAFE: Numeric conversion
            out.write("<p>User ID: " + userId + "</p>"); // SAFE: userId is long
        }
        
        // FALSE POSITIVE: Numeric getter from collection (Stored XSS)
        List<User> users = getUsersFromDatabase();
        if (!users.isEmpty()) {
            Long firstUserId = users.get(0).getId(); // SAFE: getId() returns Long
            out.write("<p>First user ID: " + firstUserId + "</p>"); // SAFE: Numeric
        }
        
        // FALSE POSITIVE: Loop with type conversion (Loop Condition)
        String countParam = request.getParameter("count");
        if (countParam != null) {
            int count = Integer.parseInt(countParam); // SAFE: Numeric conversion
            int bounded = Math.min(count, 10); // SAFE: Bounded
            
            out.write("<ul>");
            for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
                out.write("<li>Item " + i + "</li>");
            }
            out.write("</ul>");
        }
        
        // FALSE POSITIVE: Metadata variable (Privacy Violation)
        String phoneFormat = "XXX-XXX-XXXX"; // SAFE: Format pattern, not actual phone
        out.write("<p>Phone format: " + phoneFormat + "</p>"); // FLAGGED but SAFE
        
        out.close();
    }
    
    private List<User> getUsersFromDatabase() {
        // Mock database query
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "John Doe", 12345));
        users.add(new User(2L, "Jane Smith", 67890));
        return users;
    }
}

