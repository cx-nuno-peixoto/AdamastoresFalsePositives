package com.app.controller;

import com.app.service.EntityService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

public class BatchController extends HttpServlet {
    
    private EntityService entityService;
    private static final int MAX_ITEMS = 100;
    private static final int MAX_PAGES = 50;
    
    // #L01 - Input bounded by Math.min through utility
    public void processItems(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int count = Sanitizer.bound(Sanitizer.toInt(req.getParameter("count")), MAX_ITEMS);
        for (int i = 0; i < count; i++) {
            resp.getWriter().write("<li>Item " + i + "</li>");
        }
    }
    
    // #L02 - Input bounded by ternary with constant
    public void processPages(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int pages = Sanitizer.toInt(req.getParameter("pages"));
        int bounded = pages > MAX_PAGES ? MAX_PAGES : pages;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<div>Page " + i + "</div>");
        }
    }
    
    // #L03 - Input bounded by if-guard
    public void processRecords(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int records = Sanitizer.toInt(req.getParameter("records"));
        if (records > MAX_ITEMS) records = MAX_ITEMS;
        if (records < 0) records = 0;
        for (int i = 0; i < records; i++) {
            resp.getWriter().write("<tr><td>" + i + "</td></tr>");
        }
    }
    
    // #L04 - Input bounded by loop condition
    public void processEntries(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int entries = Sanitizer.toInt(req.getParameter("entries"));
        for (int i = 0; i < entries && i < MAX_ITEMS; i++) {
            resp.getWriter().write("<p>Entry " + i + "</p>");
        }
    }
    
    // #L05 - Input bounded by nested Math.min
    public void processNested(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int outer = Sanitizer.toInt(req.getParameter("outer"));
        int inner = Sanitizer.toInt(req.getParameter("inner"));
        int boundedOuter = Math.min(outer, 10);
        int boundedInner = Math.min(inner, 10);
        for (int i = 0; i < boundedOuter; i++) {
            for (int j = 0; j < boundedInner; j++) {
                resp.getWriter().write("<span>" + i + "," + j + "</span>");
            }
        }
    }
    
    // #L06 - DB count bounded by constant
    public void processDbCount(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        int dbCount = entityService.getAllEntityIds().size();
        int bounded = Math.min(dbCount, MAX_ITEMS);
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>DB Item " + i + "</li>");
        }
    }
    
    // #L07 - Regex validated numeric input
    public void processValidated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("count");
        if (Sanitizer.isNumeric(input)) {
            int count = Math.min(Integer.parseInt(input), MAX_ITEMS);
            for (int i = 0; i < count; i++) {
                resp.getWriter().write("<div>" + i + "</div>");
            }
        }
    }
    
    // #L08 - Extracted numeric from mixed input
    public void processExtracted(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("mixed");
        String numeric = Sanitizer.extractNumeric(input);
        int count = Math.min(Integer.parseInt(numeric), MAX_ITEMS);
        for (int i = 0; i < count; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }
    
    // #L09 - Modulo bounded
    public void processModulo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input % MAX_ITEMS;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<li>" + i + "</li>");
        }
    }
    
    // #L10 - Bitwise AND bounded
    public void processBitwise(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("value"));
        int bounded = input & 0x3F;
        for (int i = 0; i < bounded; i++) {
            resp.getWriter().write("<span>" + i + "</span>");
        }
    }
}

