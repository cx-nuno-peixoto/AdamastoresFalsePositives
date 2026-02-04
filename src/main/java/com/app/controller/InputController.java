package com.app.controller;

import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;

public class InputController extends HttpServlet {
    
    public enum Category { ELECTRONICS, CLOTHING, FOOD, OTHER }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    
    // #R01 - Integer.parseInt type conversion
    public void displayAge(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int age = Sanitizer.toInt(req.getParameter("age"));
        resp.getWriter().write("<span>Age: " + age + "</span>");
    }
    
    // #R02 - Long.parseLong type conversion
    public void displayTimestamp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long ts = Sanitizer.toLong(req.getParameter("ts"));
        resp.getWriter().write("<time>" + ts + "</time>");
    }
    
    // #R03 - Double.parseDouble type conversion
    public void displayPrice(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        double price = Sanitizer.toDouble(req.getParameter("price"));
        resp.getWriter().write("<span>$" + String.format("%.2f", price) + "</span>");
    }
    
    // #R04 - Enum.valueOf type conversion
    public void displayCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("category");
        Category cat = Category.valueOf(input.toUpperCase());
        resp.getWriter().write("<span>" + cat.name() + "</span>");
    }
    
    // #R05 - Enum.valueOf with ordinal
    public void displayPriority(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("priority");
        Priority pri = Priority.valueOf(input.toUpperCase());
        resp.getWriter().write("<span data-level=\"" + pri.ordinal() + "\">" + pri.name() + "</span>");
    }
    
    // #R06 - Regex validated alphanumeric
    public void displayCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (Sanitizer.isAlphanumeric(code)) {
            resp.getWriter().write("<code>" + code + "</code>");
        }
    }
    
    // #R07 - Regex validated numeric
    public void displayId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (Sanitizer.isNumeric(id)) {
            resp.getWriter().write("<span id=\"item-" + id + "\">" + id + "</span>");
        }
    }
    
    // #R08 - HTML escaped through utility
    public void displayName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String escaped = Sanitizer.escapeHtml(name);
        resp.getWriter().write("<span>" + escaped + "</span>");
    }
    
    // #R09 - Ternary with numeric result
    public void displayCount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int input = Sanitizer.toInt(req.getParameter("count"));
        int result = input > 100 ? 100 : input;
        resp.getWriter().write("<span>" + result + "</span>");
    }
    
    // #R10 - Math operation result
    public void displaySum(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int a = Sanitizer.toInt(req.getParameter("a"));
        int b = Sanitizer.toInt(req.getParameter("b"));
        int sum = a + b;
        resp.getWriter().write("<span>" + sum + "</span>");
    }
    
    // #R11 - Boolean.parseBoolean
    public void displayFlag(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean flag = Boolean.parseBoolean(req.getParameter("flag"));
        resp.getWriter().write("<input type=\"checkbox\" " + (flag ? "checked" : "") + "/>");
    }
    
    // #R12 - UUID validation
    public void displayUuid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("uuid");
        java.util.UUID uuid = java.util.UUID.fromString(input);
        resp.getWriter().write("<span>" + uuid.toString() + "</span>");
    }
    
    // #R13 - Extracted numeric only
    public void displayExtracted(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("mixed");
        String numeric = Sanitizer.extractNumeric(input);
        resp.getWriter().write("<span>" + numeric + "</span>");
    }
    
    // #R14 - Masked input
    public void displayMasked(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("card");
        String masked = Sanitizer.mask(input, 4);
        resp.getWriter().write("<span>" + masked + "</span>");
    }
}

