package com.checkmarx.processors.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

public class EntityProcessor {
    
    public enum Status { ACTIVE, INACTIVE, SUSPENDED, DELETED }
    public enum OrderType { PENDING, CONFIRMED, SHIPPED, DELIVERED }
    
    public static class Entity {
        private Long id;
        private String name;
        private Double price;
        private Integer quantity;
        private boolean active;
        private Boolean verified;
        private Status status;
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public boolean isActive() { return active; }
        public Boolean getVerified() { return verified; }
        public Status getStatus() { return status; }
    }
    
    private EntityRepository repository;
    
    // SX-MR:01 - Numeric ID from collection
    public void scenario01(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Long value = items.get(0).getId();
        out.write("ID: " + value);
    }
    
    // SX-MR:02 - Integer from collection
    public void scenario02(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Integer value = items.get(0).getQuantity();
        out.write("Quantity: " + value);
    }
    
    // SX-MR:03 - Double from collection
    public void scenario03(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Double value = items.get(0).getPrice();
        out.write("Price: $" + value);
    }
    
    // SX-MR:04 - Boolean from collection
    public void scenario04(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        boolean value = items.get(0).isActive();
        out.write("Active: " + value);
    }
    
    // SX-MR:05 - Boolean wrapper from collection
    public void scenario05(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Boolean value = items.get(0).getVerified();
        out.write("Verified: " + value);
    }
    
    // SX-MR:06 - Enum from collection
    public void scenario06(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Status value = items.get(0).getStatus();
        out.write("Status: " + value);
    }
    
    // SX-MR:07 - Enum.name() from collection
    public void scenario07(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getStatus().name();
        out.write("Status: " + value);
    }
    
    // SX-MR:08 - Enum.ordinal() from collection
    public void scenario08(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        int value = items.get(0).getStatus().ordinal();
        out.write("Ordinal: " + value);
    }
    
    // SX-MR:09 - Optional.get().getId()
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Entity> opt = repository.findById(1L);
        if (opt.isPresent()) {
            Long value = opt.get().getId();
            PrintWriter out = response.getWriter();
            out.write("ID: " + value);
        }
    }
    
    // SX-MR:10 - Chained numeric getter
    public void scenario10(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("ID: " + items.get(0).getId());
    }
    
    // SX-MR:11 - Multiple numeric getters
    public void scenario11(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("ID: " + items.get(0).getId() + ", Qty: " + items.get(0).getQuantity());
    }
    
    // SX-MR:12 - Boolean ternary expression
    public void scenario12(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        boolean active = items.get(0).isActive();
        String message = active ? "Active" : "Inactive";
        out.write("Status: " + message);
    }
    
    // SX-MR:13 - Multiple boolean fields
    public void scenario13(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Entity e = items.get(0);
        out.write("Active: " + e.isActive() + ", Verified: " + e.getVerified());
    }
    
    private interface EntityRepository {
        List<Entity> findAll();
        Optional<Entity> findById(Long id);
    }
}

