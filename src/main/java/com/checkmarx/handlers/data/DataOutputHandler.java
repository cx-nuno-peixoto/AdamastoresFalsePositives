package com.checkmarx.handlers.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DataOutputHandler {

    private static final String FIELD_KEY = "Password";
    private static final String ID_KEY = "SSN";
    private static final String CARD_KEY = "CreditCard";

    // PV-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter(FIELD_KEY);
        out.write("Field: " + FIELD_KEY);
    }

    // PV-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String format = "Min 8 characters";
        out.write("Requirements: " + format);
    }

    // PV-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String format = "XXX-XX-XXXX";
        out.write("Format: " + format);
    }

    // PV-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("Data: ");
        sb.append(password);
    }

    // PV-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String fieldName = "password";
        out.write("Missing field: " + fieldName);
    }

    // PV-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("accountId");
        long value = Long.parseLong(param);
        out.write("Account ID: " + value);
    }

    // PV-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String format = "(XXX) XXX-XXXX";
        out.write("Phone format: " + format);
    }

    // PV-BR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String password = request.getParameter("password");
        out.write("Your password: " + password);
    }

    // PV-BR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ssn = request.getParameter("ssn");
        out.write("SSN: " + ssn);
    }

    // PV-BR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String creditCard = request.getParameter("creditCard");
        out.write("Credit Card: " + creditCard);
    }

    // PV-BR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        System.out.println("User password: " + password);
    }

    // PV-BR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String credentials = request.getParameter("credentials");
        out.write("Credentials: " + credentials);
    }

    // PV-BR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String secret = request.getParameter("secret");
        out.write("Secret: " + secret);
    }

    // PV-BR:14
    public void scenario14(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String authToken = request.getParameter("authToken");
        out.write("Token: " + authToken);
    }

    // PV-BR:15
    public void scenario15(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String dob = request.getParameter("dob");
        out.write("Date of Birth: " + dob);
    }

    // PV-BR:16
    public void scenario16(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String passport = request.getParameter("passport");
        out.write("Passport: " + passport);
    }

    // PV-BR:17
    public void scenario17(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String socialSecurity = request.getParameter("socialSecurity");
        out.write("Social Security: " + socialSecurity);
    }

    // PV-BR:18
    public void scenario18(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("Data: ");
        sb.append(password);
        out.write(sb.toString());
    }

    // PV-BR:19
    public void scenario19(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String format = "Min 8 characters";
        out.write("Requirements: " + format);
        String password = request.getParameter("password");
        out.write(", Your password: " + password);
    }

    // PV-MR:20
    public void scenario20(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String errorField = "creditCard";
        out.write("Validation error on field: " + errorField);
    }

    // PV-MR:21
    public void scenario21(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ssn = request.getParameter("ssn");
        String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4);
        out.write("SSN: " + masked);
    }

    // PV-BR:22
    public void scenario22(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String email = request.getParameter("email");
        try {
            throw new RuntimeException("Failed for user: " + email);
        } catch (RuntimeException e) {
            out.write("Error: " + e.getMessage());
        }
    }

    // PV-BR:23
    public void scenario23(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssn = request.getParameter("ssn");
        System.err.println("Processing SSN: " + ssn);
    }

    // PV-BR:24
    public void scenario24(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        String ssn = request.getParameter("ssn");
        String dob = request.getParameter("dob");
        out.write("User: " + name + ", SSN: " + ssn + ", DOB: " + dob);
    }

    // PV-BR:25
    public void scenario25(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String mrn = request.getParameter("medicalRecordNumber");
        out.write("Medical Record: " + mrn);
    }

    // PV-BR:26
    public void scenario26(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String license = request.getParameter("driversLicense");
        out.write("License: " + license);
    }
}

