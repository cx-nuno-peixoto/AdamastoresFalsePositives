package com.checkmarx.processors.app;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AppProcessor extends HttpServlet {
    
    public static class Record {
        private Long id;
        private String name;
        private Integer code;
        
        public Record(Long id, String name, Integer code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Integer getCode() { return code; }
    }
    
    // MX-MR:01 - Combined: Type conversion (RX), numeric getter (SX), bounded loop (LC)
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // RX-MR:01 - Type conversion
        String idParam = request.getParameter("id");
        if (idParam != null) {
            long id = Long.parseLong(idParam);
            out.write("<p>ID: " + id + "</p>");
        }
        
        // SX-MR:01 - Numeric getter from collection
        List<Record> records = getRecords();
        if (!records.isEmpty()) {
            Long firstId = records.get(0).getId();
            out.write("<p>First ID: " + firstId + "</p>");
        }
        
        // LC-MR:01 - Bounded loop
        String countParam = request.getParameter("count");
        if (countParam != null) {
            int count = Integer.parseInt(countParam);
            int bounded = Math.min(count, 10);
            out.write("<ul>");
            for (int i = 0; i < bounded; i++) {
                out.write("<li>Item " + i + "</li>");
            }
            out.write("</ul>");
        }
        
        // PV-MR:01 - Metadata pattern
        String format = "XXX-XXX-XXXX";
        out.write("<p>Format: " + format + "</p>");
        
        out.close();
    }
    
    // MX-MR:02 - Integer code from collection
    public void scenario02(HttpServletRequest request, HttpServletResponse response, List<Record> records) throws IOException {
        PrintWriter out = response.getWriter();
        Integer value = records.get(0).getCode();
        out.write("Code: " + value);
    }
    
    // MX-MR:03 - Ternary bounded loop
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("limit");
        int value = Integer.parseInt(param);
        int bounded = (value > 50) ? 50 : value;
        PrintWriter out = response.getWriter();
        for (int i = 0; i < bounded; i++) {
            out.write("Row " + i + "\n");
        }
    }
    
    // MX-MR:04 - Enum validation with type conversion
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("type");
        String countParam = request.getParameter("count");
        int count = Integer.parseInt(countParam);
        PrintWriter out = response.getWriter();
        
        try {
            ActionType type = ActionType.valueOf(param.toUpperCase());
            out.write("Type: " + type + ", Count: " + count);
        } catch (IllegalArgumentException e) {
            out.write("Invalid");
        }
    }
    
    // MX-MR:05 - Regex validation with bounded loop
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("code");
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Z]{3}$");
        java.util.regex.Matcher matcher = pattern.matcher(param);
        PrintWriter out = response.getWriter();
        
        if (matcher.matches()) {
            String countParam = request.getParameter("count");
            int count = Math.min(Integer.parseInt(countParam), 100);
            for (int i = 0; i < count; i++) {
                out.write("Code: " + param + ", Item " + i + "\n");
            }
        }
    }
    
    private List<Record> getRecords() {
        List<Record> records = new ArrayList<>();
        records.add(new Record(1L, "Record One", 12345));
        records.add(new Record(2L, "Record Two", 67890));
        return records;
    }
    
    public enum ActionType { CREATE, READ, UPDATE, DELETE }
}

