<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Display - False Positive Test</title>
</head>
<body>
    <h1>User Display - False Positive Scenarios</h1>
    
    <%
        // Mock User class
        class User {
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
        
        // FALSE POSITIVE: Type conversion (Reflected XSS)
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null) {
            long userId = Long.parseLong(userIdParam); // SAFE: Numeric conversion
    %>
            <p>User ID: <%= userId %></p><%-- SAFE: userId is long --%>
    <%
        }
        
        // FALSE POSITIVE: Numeric getter from collection (Stored XSS)
        List<User> users = new ArrayList<>();
        users.add(new User(1L, "John Doe", 12345));
        users.add(new User(2L, "Jane Smith", 67890));
        
        if (!users.isEmpty()) {
            Long firstUserId = users.get(0).getId(); // SAFE: getId() returns Long
    %>
            <p>First User ID: <%= firstUserId %></p><%-- SAFE: Numeric --%>
    <%
        }
        
        // FALSE POSITIVE: Loop with type conversion (Loop Condition)
        String countParam = request.getParameter("count");
        if (countParam != null) {
            int count = Integer.parseInt(countParam); // SAFE: Numeric conversion
            int bounded = Math.min(count, 10); // SAFE: Bounded
    %>
            <ul>
    <%
            for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
    %>
                <li>Item <%= i %></li>
    <%
            }
    %>
            </ul>
    <%
        }
        
        // FALSE POSITIVE: Metadata variable (Privacy Violation)
        String phoneFormat = "XXX-XXX-XXXX"; // SAFE: Format pattern, not actual phone
    %>
        <p>Phone format: <%= phoneFormat %></p><%-- FLAGGED but SAFE: Metadata --%>
    
</body>
</html>

