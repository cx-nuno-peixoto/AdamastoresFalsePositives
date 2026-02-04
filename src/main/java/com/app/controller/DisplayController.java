package com.app.controller;

import com.app.service.EntityService;
import com.app.service.AccountService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

public class DisplayController extends HttpServlet {
    
    private EntityService entityService;
    private AccountService accountService;
    
    // #X01 - DB numeric ID through service layer
    public void renderEntityId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        long entityId = entityService.getEntityId(id);
        resp.getWriter().write("<div data-id=\"" + entityId + "\"></div>");
    }
    
    // #X02 - DB status (int) through service layer
    public void renderEntityStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        int status = entityService.getEntityStatus(id);
        resp.getWriter().write("<span class=\"status-" + status + "\">Status: " + status + "</span>");
    }
    
    // #X03 - DB boolean through service layer
    public void renderEntityActive(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        boolean active = entityService.isEntityActive(id);
        resp.getWriter().write("<input type=\"checkbox\" " + (active ? "checked" : "") + "/>");
    }
    
    // #X04 - DB balance (double) through service layer
    public void renderEntityBalance(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        double balance = entityService.getEntityBalance(id);
        resp.getWriter().write("<span>$" + String.format("%.2f", balance) + "</span>");
    }
    
    // #X05 - DB derived code (int) through service layer
    public void renderEntityCode(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        int code = entityService.getEntityCode(id);
        resp.getWriter().write("<code>" + code + "</code>");
    }
    
    // #X06 - DB collection of IDs through service layer
    public void renderAllEntityIds(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        for (Long entityId : entityService.getAllEntityIds()) {
            resp.getWriter().write("<li>" + entityId + "</li>");
        }
    }
    
    // #X07 - DB collection of statuses through service layer
    public void renderAllEntityStatuses(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        for (Integer status : entityService.getAllEntityStatuses()) {
            resp.getWriter().write("<option value=\"" + status + "\">" + status + "</option>");
        }
    }
    
    // #X08 - DB escaped name through service layer (sanitized)
    public void renderEscapedName(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = entityService.getEscapedName(id);
        resp.getWriter().write("<span>" + name + "</span>");
    }
    
    // #X09 - DB first entity status from collection
    public void renderFirstStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        int status = entityService.getFirstEntityStatus();
        resp.getWriter().write("<div>" + status + "</div>");
    }
    
    // #X10 - DB first entity ID from collection
    public void renderFirstId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = entityService.getFirstEntityId();
        resp.getWriter().write("<span>" + id + "</span>");
    }
    
    // #X11 - Account tier (int) through service layer
    public void renderAccountTier(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        int tier = accountService.getAccountTier(id);
        resp.getWriter().write("<div class=\"tier-" + tier + "\">Tier " + tier + "</div>");
    }
    
    // #X12 - Account verified (boolean) through service layer
    public void renderAccountVerified(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        boolean verified = accountService.isAccountVerified(id);
        resp.getWriter().write("<span>" + (verified ? "Verified" : "Pending") + "</span>");
    }
}

