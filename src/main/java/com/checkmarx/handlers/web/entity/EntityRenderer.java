package com.checkmarx.handlers.web.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class EntityRenderer {

    public static class Entity {
        private Long id;
        private String name;
        private String email;
        private Integer count;
        private boolean active;

        public Entity(Long id, String name, String email, Integer count, boolean active) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.count = count;
            this.active = active;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Integer getCount() { return count; }
        public boolean isActive() { return active; }
    }

    // SX-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Long value = items.get(0).getId();
        out.write("ID: " + value);
    }

    // SX-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Integer value = items.get(0).getCount();
        out.write("Count: " + value);
    }

    // SX-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        boolean value = items.get(0).isActive();
        out.write("Active: " + value);
    }

    // SX-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        for (Entity item : items) {
            Long value = item.getId();
            out.write("<li>ID: " + value + "</li>");
        }
    }

    // SX-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Entity item = items.get(0);
        out.write("ID: " + item.getId());
        out.write(", Count: " + item.getCount());
        out.write(", Active: " + item.isActive());
    }

    // SX-BR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getName();
        out.write("Name: " + value);
    }

    // SX-BR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getEmail();
        out.write("Email: " + value);
    }

    // SX-BR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        for (Entity item : items) {
            String value = item.getName();
            out.write("<li>" + value + "</li>");
        }
    }

    // SX-BR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("Name: " + items.get(0).getName());
    }

    // SX-BR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Entity item = items.get(0);
        out.write("Name: " + item.getName());
        out.write(", Email: " + item.getEmail());
    }

    // SX-BR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getName();
        out.write("<div title='" + value + "'>Content</div>");
    }

    // SX-BR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getName();
        out.write("<script>var data = '" + value + "';</script>");
    }

    // SX-BR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        Entity item = items.get(0);
        Long id = item.getId();
        out.write("ID: " + id);
        String name = item.getName();
        out.write(", Name: " + name);
    }

    // SX-MR:14
    public void scenario14(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        Integer value = (Integer) request.getSession().getAttribute("pageCount");
        out.write("Page count: " + value);
    }

    // SX-MR:15
    public void scenario15(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        boolean status = items.get(0).isActive();
        String text = status ? "Active" : "Inactive";
        out.write("Status: " + text);
    }

    // SX-BR:16
    public void scenario16(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("input");
        request.getSession().setAttribute("storedInput", param);
        String stored = (String) request.getSession().getAttribute("storedInput");
        out.write("Stored: " + stored);
    }

    // SX-BR:17
    public void scenario17(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("userPref".equals(cookie.getName())) {
                    out.write("Preference: " + cookie.getValue());
                }
            }
        }
    }

    // SX-BR:18
    public void scenario18(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getName();
        out.write("<div style='color: " + value + "'>Content</div>");
    }

    // SX-BR:19
    public void scenario19(HttpServletRequest request, HttpServletResponse response, List<Entity> items) throws IOException {
        PrintWriter out = response.getWriter();
        String value = items.get(0).getName();
        out.write("<button onclick='" + value + "'>Click</button>");
    }
}

