package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Ternary Expressions and Math Operators
 * 
 * Pattern: User input used in ternary expressions with numeric operations or math operators
 * 
 * These scenarios demonstrate that ternary expressions with numeric operations
 * and mathematical operators sanitize input by converting to numeric types.
 */
public class ReflectedXSS_TernaryMathOperators {
    
    // Scenario 1: Ternary with numeric comparison
    public void showDiscountedPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        double price = Double.parseDouble(priceParam);
        
        double finalPrice = (price > 100) ? price * 0.9 : price; // SAFE: Ternary with numeric operation
        
        PrintWriter out = response.getWriter();
        out.write("Price: $" + finalPrice); // SAFE: finalPrice is double
    }
    
    // Scenario 2: Addition operator
    public void showTotal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String price1Param = request.getParameter("price1");
        String price2Param = request.getParameter("price2");
        
        double price1 = Double.parseDouble(price1Param);
        double price2 = Double.parseDouble(price2Param);
        double total = price1 + price2; // SAFE: Addition of doubles
        
        PrintWriter out = response.getWriter();
        out.write("Total: $" + total); // SAFE: total is double
    }
    
    // Scenario 3: Subtraction operator
    public void showDiscount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String originalParam = request.getParameter("original");
        String discountParam = request.getParameter("discount");
        
        double original = Double.parseDouble(originalParam);
        double discount = Double.parseDouble(discountParam);
        double final_price = original - discount; // SAFE: Subtraction
        
        PrintWriter out = response.getWriter();
        out.write("Final price: $" + final_price);
    }
    
    // Scenario 4: Multiplication operator
    public void showTotalQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        String qtyParam = request.getParameter("qty");
        
        double price = Double.parseDouble(priceParam);
        int qty = Integer.parseInt(qtyParam);
        double total = price * qty; // SAFE: Multiplication
        
        PrintWriter out = response.getWriter();
        out.write("Total: $" + total);
    }
    
    // Scenario 5: Division operator
    public void showAverage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String totalParam = request.getParameter("total");
        String countParam = request.getParameter("count");
        
        double total = Double.parseDouble(totalParam);
        int count = Integer.parseInt(countParam);
        double average = total / count; // SAFE: Division
        
        PrintWriter out = response.getWriter();
        out.write("Average: " + average);
    }
    
    // Scenario 6: Modulo operator
    public void showRemainder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String numberParam = request.getParameter("number");
        String divisorParam = request.getParameter("divisor");
        
        int number = Integer.parseInt(numberParam);
        int divisor = Integer.parseInt(divisorParam);
        int remainder = number % divisor; // SAFE: Modulo
        
        PrintWriter out = response.getWriter();
        out.write("Remainder: " + remainder);
    }
    
    // Scenario 7: Ternary with Math.max()
    public void showMaxValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String value1Param = request.getParameter("value1");
        String value2Param = request.getParameter("value2");
        
        int value1 = Integer.parseInt(value1Param);
        int value2 = Integer.parseInt(value2Param);
        int max = (value1 > value2) ? value1 : value2; // SAFE: Ternary with numeric comparison
        
        PrintWriter out = response.getWriter();
        out.write("Max: " + max);
    }
    
    // Scenario 8: Ternary with Math.min()
    public void showMinValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String value1Param = request.getParameter("value1");
        String value2Param = request.getParameter("value2");
        
        int value1 = Integer.parseInt(value1Param);
        int value2 = Integer.parseInt(value2Param);
        int min = (value1 < value2) ? value1 : value2; // SAFE: Ternary with numeric comparison
        
        PrintWriter out = response.getWriter();
        out.write("Min: " + min);
    }
    
    // Scenario 9: Complex ternary with multiple operations
    public void showComplexCalculation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String amountParam = request.getParameter("amount");
        String taxRateParam = request.getParameter("taxRate");
        
        double amount = Double.parseDouble(amountParam);
        double taxRate = Double.parseDouble(taxRateParam);
        
        double total = (amount > 1000) ? amount * (1 + taxRate) : amount * (1 + taxRate * 0.5); // SAFE: Complex ternary
        
        PrintWriter out = response.getWriter();
        out.write("Total with tax: $" + total);
    }
    
    // Scenario 10: Chained ternary expressions
    public void showShippingCost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String weightParam = request.getParameter("weight");
        double weight = Double.parseDouble(weightParam);
        
        double shipping = (weight < 1) ? 5.0 : (weight < 5) ? 10.0 : 15.0; // SAFE: Chained ternary
        
        PrintWriter out = response.getWriter();
        out.write("Shipping: $" + shipping);
    }
}

