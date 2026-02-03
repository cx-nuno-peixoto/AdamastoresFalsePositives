package com.checkmarx.handlers.web.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RequestHandler {

    public enum AccessLevel { ADMIN, USER, GUEST }

    // RX-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("age");
        int value = Integer.parseInt(param);
        out.write("Age: " + value);
    }

    // RX-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("id");
        long value = Long.parseLong(param);
        out.write("ID: " + value);
    }

    // RX-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("price");
        double value = Double.parseDouble(param);
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        String formatted = df.format(value);
        out.write("Price: " + formatted);
    }

    // RX-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("code");
        Pattern pattern = Pattern.compile("^[A-Z]{3}-[0-9]{4}$");
        Matcher matcher = pattern.matcher(param);
        if (matcher.matches()) {
            out.write("Code: " + param);
        }
    }

    // RX-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("role");
        try {
            AccessLevel level = AccessLevel.valueOf(param.toUpperCase());
            out.write("Role: " + level.name());
        } catch (IllegalArgumentException e) {
            out.write("Invalid");
        }
    }

    // RX-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        int result = (value > 100) ? 100 : value;
        out.write("Count: " + result);
    }

    // RX-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = value * 2 + 10;
        out.write("Result: " + result);
    }

    // RX-BR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("name");
        out.write("Name: " + param);
    }

    // RX-BR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("comment");
        String message = "Comment: " + param;
        out.write(message);
    }

    // RX-BR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("title");
        out.write("<div title='" + param + "'>Content</div>");
    }

    // RX-BR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("userName");
        out.write("<script>var user = '" + param + "';</script>");
    }

    // RX-BR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("redirect");
        out.write("<a href='" + param + "'>Click here</a>");
    }

    // RX-BR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param1 = request.getParameter("firstName");
        String param2 = request.getParameter("lastName");
        out.write("Name: " + param1 + " " + param2);
    }

    // RX-BR:14
    public void scenario14(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("input");
        Pattern pattern = Pattern.compile("^[a-z]+$");
        Matcher matcher = pattern.matcher(param);
        if (matcher.matches()) {
            out.write("Input: " + param);
        }
    }

    // RX-BR:15
    public void scenario15(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param1 = request.getParameter("id");
        long value = Long.parseLong(param1);
        out.write("ID: " + value);
        String param2 = request.getParameter("name");
        out.write(", Name: " + param2);
    }

    // RX-MR:16
    public void scenario16(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("flag");
        boolean value = Boolean.parseBoolean(param);
        out.write("Flag: " + value);
    }

    // RX-MR:17
    public void scenario17(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("uuid");
        try {
            java.util.UUID value = java.util.UUID.fromString(param);
            out.write("UUID: " + value.toString());
        } catch (IllegalArgumentException e) {
            out.write("Invalid");
        }
    }

    // RX-BR:18
    public void scenario18(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String header = request.getHeader("Referer");
        out.write("Came from: " + header);
    }

    // RX-BR:19
    public void scenario19(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            out.write("Cookie: " + cookies[0].getValue());
        }
    }

    // RX-BR:20
    public void scenario20(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();
        out.write("Path: " + path);
    }

    // RX-BR:21
    public void scenario21(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String query = request.getQueryString();
        out.write("Query: " + query);
    }

    // RX-BR:22
    public void scenario22(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("data");
        response.setContentType("application/json");
        out.write("{\"value\": \"" + param + "\"}");
    }
}

