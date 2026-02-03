package com.checkmarx.processors.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DataFormatter {
    
    private static final String FIELD_KEY = "Password";
    private static final String ID_KEY = "SSN";

    // PV-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssn = request.getParameter("ssn");
        String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4);
        PrintWriter out = response.getWriter();
        out.write("SSN: " + masked);
    }

    // PV-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String creditCard = request.getParameter("creditCard");
        String last4 = creditCard.substring(creditCard.length() - 4);
        String masked = "**** **** **** " + last4;
        PrintWriter out = response.getWriter();
        out.write("Card: " + masked);
    }

    // PV-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone = request.getParameter("phone");
        String last4 = phone.substring(phone.length() - 4);
        String masked = "(***) ***-" + last4;
        PrintWriter out = response.getWriter();
        out.write("Phone: " + masked);
    }

    // PV-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        int atIndex = email.indexOf('@');
        String masked = email.charAt(0) + "***" + email.substring(atIndex);
        PrintWriter out = response.getWriter();
        out.write("Email: " + masked);
    }

    // PV-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        String masked = "••••••••";
        PrintWriter out = response.getWriter();
        out.write("Password: " + masked);
    }

    // PV-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getParameter("accountNumber");
        String hashed = hashValue(account);
        PrintWriter out = response.getWriter();
        out.write("Account Hash: " + hashed);
    }

    // PV-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String license = request.getParameter("driversLicense");
        String checksum = license.substring(license.length() - 2);
        PrintWriter out = response.getWriter();
        out.write("DL Checksum: " + checksum);
    }

    // PV-MR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getParameter("secretKey");
        int length = key.length();
        PrintWriter out = response.getWriter();
        out.write("Secret key length: " + length);
    }

    // PV-MR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getParameter("apiKey");
        boolean hasKey = key != null && !key.isEmpty();
        PrintWriter out = response.getWriter();
        out.write("API Key configured: " + hasKey);
    }

    // PV-MR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter(FIELD_KEY);
        PrintWriter out = response.getWriter();
        out.write("Field: " + FIELD_KEY);
    }

    // PV-MR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String format = "Min 8 characters, 1 uppercase, 1 number";
        PrintWriter out = response.getWriter();
        out.write("Password requirements: " + format);
    }

    // PV-MR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("Password: ");
        sb.append(password);
    }

    // PV-MR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("accountId");
        long value = Long.parseLong(param);
        PrintWriter out = response.getWriter();
        out.write("Account ID: " + value);
    }

    private String hashValue(String value) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes());
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hash) {
                hexHash.append(String.format("%02x", b));
            }
            return hexHash.toString();
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
}

