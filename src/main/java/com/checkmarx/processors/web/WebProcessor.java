package com.checkmarx.processors.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WebProcessor {
    
    public enum AccessLevel { ADMIN, USER, GUEST, MODERATOR }
    public enum StatusType { PENDING, PROCESSING, SHIPPED, DELIVERED }

    // RX-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("age");
        int value = Integer.parseInt(param);
        PrintWriter out = response.getWriter();
        out.write("Age: " + value);
    }

    // RX-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("account");
        long value = Long.parseLong(param);
        PrintWriter out = response.getWriter();
        out.write("Account: " + value);
    }

    // RX-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("price");
        double value = Double.parseDouble(param);
        PrintWriter out = response.getWriter();
        out.write("Price: $" + value);
    }

    // RX-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("rating");
        float value = Float.parseFloat(param);
        PrintWriter out = response.getWriter();
        out.write("Rating: " + value);
    }

    // RX-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("role");
        PrintWriter out = response.getWriter();
        try {
            AccessLevel level = AccessLevel.valueOf(param.toUpperCase());
            out.write("Role: " + level);
        } catch (IllegalArgumentException e) {
            out.write("Invalid");
        }
    }

    // RX-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("status");
        PrintWriter out = response.getWriter();
        try {
            StatusType status = StatusType.valueOf(param.toUpperCase());
            out.write("Status: " + status.name());
        } catch (IllegalArgumentException e) {
            out.write("Invalid");
        }
    }

    // RX-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("username");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(param);
        PrintWriter out = response.getWriter();
        if (matcher.matches()) {
            out.write("Username: " + param);
        } else {
            out.write("Invalid");
        }
    }

    // RX-MR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("id");
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(param);
        PrintWriter out = response.getWriter();
        if (matcher.matches()) {
            out.write("ID: " + param);
        } else {
            out.write("Invalid");
        }
    }

    // RX-MR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("phone");
        Pattern pattern = Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$");
        Matcher matcher = pattern.matcher(param);
        PrintWriter out = response.getWriter();
        if (matcher.matches()) {
            out.write("Phone: " + param);
        } else {
            out.write("Invalid");
        }
    }

    // RX-MR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("price");
        double price = Double.parseDouble(param);
        double result = (price > 100) ? price * 0.9 : price;
        PrintWriter out = response.getWriter();
        out.write("Price: $" + result);
    }

    // RX-MR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String p1 = request.getParameter("price1");
        String p2 = request.getParameter("price2");
        double v1 = Double.parseDouble(p1);
        double v2 = Double.parseDouble(p2);
        double total = v1 + v2;
        PrintWriter out = response.getWriter();
        out.write("Total: $" + total);
    }

    // RX-MR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("weight");
        double weight = Double.parseDouble(param);
        double shipping = (weight < 1) ? 5.0 : (weight < 5) ? 10.0 : 15.0;
        PrintWriter out = response.getWriter();
        out.write("Shipping: $" + shipping);
    }

    // RX-MR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("id");
        PrintWriter out = response.getWriter();
        try {
            int id = Integer.parseInt(param);
            out.write("ID: " + id);
        } catch (NumberFormatException e) {
            out.write("Invalid");
        }
    }
}

