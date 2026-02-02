package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Type Conversion
 * 
 * Pattern: User input converted to numeric types (Integer.parseInt, Long.parseLong, etc.)
 * 
 * These scenarios demonstrate that type conversion to numeric types sanitizes XSS payloads.
 * If the input contains XSS, the conversion throws NumberFormatException.
 */
public class ReflectedXSS_TypeConversion {
    
    // Scenario 1: Integer.parseInt()
    public void showAge(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ageParam = request.getParameter("age");
        int age = Integer.parseInt(ageParam); // SAFE: Converts to int, throws exception on XSS
        PrintWriter out = response.getWriter();
        out.write("Age: " + age); // SAFE: age is int, cannot contain XSS
    }
    
    // Scenario 2: Long.parseLong()
    public void showAccountNumber(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountParam = request.getParameter("account");
        long account = Long.parseLong(accountParam); // SAFE: Converts to long
        PrintWriter out = response.getWriter();
        out.write("Account: " + account);
    }
    
    // Scenario 3: Double.parseDouble()
    public void showPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        double price = Double.parseDouble(priceParam); // SAFE: Converts to double
        PrintWriter out = response.getWriter();
        out.write("Price: $" + price);
    }
    
    // Scenario 4: Float.parseFloat()
    public void showRating(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ratingParam = request.getParameter("rating");
        float rating = Float.parseFloat(ratingParam); // SAFE: Converts to float
        PrintWriter out = response.getWriter();
        out.write("Rating: " + rating);
    }
    
    // Scenario 5: Integer.valueOf()
    public void showQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String qtyParam = request.getParameter("qty");
        Integer qty = Integer.valueOf(qtyParam); // SAFE: Converts to Integer
        PrintWriter out = response.getWriter();
        out.write("Quantity: " + qty);
    }
    
    // Scenario 6: Type conversion with try-catch
    public void showIdSafe(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        PrintWriter out = response.getWriter();
        try {
            int id = Integer.parseInt(idParam); // SAFE: Numeric conversion
            out.write("ID: " + id);
        } catch (NumberFormatException e) {
            out.write("Invalid ID");
        }
    }
}

