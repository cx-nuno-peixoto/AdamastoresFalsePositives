package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Number Formatting
 * 
 * Pattern: User input formatted using DecimalFormat, NumberFormat, String.format with %d/%f
 * 
 * These scenarios demonstrate that number formatting sanitizes XSS payloads.
 * The formatters only accept numeric input and throw exceptions on XSS attempts.
 */
public class ReflectedXSS_NumberFormatting {
    
    // Scenario 1: DecimalFormat with pattern
    public void showFormattedPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        double price = Double.parseDouble(priceParam);
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formatted = df.format(price); // SAFE: DecimalFormat only outputs numbers
        
        PrintWriter out = response.getWriter();
        out.write("Price: $" + formatted); // SAFE: formatted is numeric string
    }
    
    // Scenario 2: NumberFormat.getCurrencyInstance()
    public void showCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String amountParam = request.getParameter("amount");
        double amount = Double.parseDouble(amountParam);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formatted = currencyFormat.format(amount); // SAFE: Currency formatting
        
        PrintWriter out = response.getWriter();
        out.write("Amount: " + formatted);
    }
    
    // Scenario 3: NumberFormat.getPercentInstance()
    public void showPercentage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rateParam = request.getParameter("rate");
        double rate = Double.parseDouble(rateParam);
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        String formatted = percentFormat.format(rate); // SAFE: Percent formatting
        
        PrintWriter out = response.getWriter();
        out.write("Rate: " + formatted);
    }
    
    // Scenario 4: String.format with %d (integer)
    public void showFormattedId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        int id = Integer.parseInt(idParam);
        String formatted = String.format("ID: %06d", id); // SAFE: %d only formats integers
        
        PrintWriter out = response.getWriter();
        out.write(formatted); // SAFE: formatted contains only numeric output
    }
    
    // Scenario 5: String.format with %f (float)
    public void showFormattedValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String valueParam = request.getParameter("value");
        double value = Double.parseDouble(valueParam);
        String formatted = String.format("Value: %.2f", value); // SAFE: %f only formats floats
        
        PrintWriter out = response.getWriter();
        out.write(formatted);
    }
    
    // Scenario 6: DecimalFormat with currency symbol
    public void showPriceWithSymbol(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        double price = Double.parseDouble(priceParam);
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        String formatted = df.format(price); // SAFE: DecimalFormat sanitizes
        
        PrintWriter out = response.getWriter();
        out.write("Total: " + formatted);
    }
    
    // Scenario 7: NumberFormat.getIntegerInstance()
    public void showInteger(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countParam = request.getParameter("count");
        long count = Long.parseLong(countParam);
        NumberFormat intFormat = NumberFormat.getIntegerInstance();
        String formatted = intFormat.format(count); // SAFE: Integer formatting
        
        PrintWriter out = response.getWriter();
        out.write("Count: " + formatted);
    }
    
    // Scenario 8: Multiple formatting operations
    public void showComplexFormatting(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String amountParam = request.getParameter("amount");
        double amount = Double.parseDouble(amountParam);
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formatted1 = df.format(amount); // SAFE: First formatting
        String formatted2 = String.format("$%.2f", amount); // SAFE: Second formatting
        
        PrintWriter out = response.getWriter();
        out.write("Amount 1: " + formatted1 + ", Amount 2: " + formatted2);
    }
}

