package com.app.controller;

import com.app.core.Processor;
import com.app.core.Transformer;
import com.app.model.Entity;
import com.app.model.Account;
import com.app.repository.EntityRepository;
import com.app.repository.AccountRepository;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ComplexController extends HttpServlet {
    
    private EntityRepository entityRepo;
    private AccountRepository accountRepo;
    
    // #C01 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (numeric)
    public void deepFlowNumeric(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        long outputId = Processor.extractEntityId(Processor.unwrap(entity));
        resp.getWriter().write("<span>" + outputId + "</span>");
    }
    
    // #C02 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (status)
    public void deepFlowStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        int status = Processor.extractEntityStatus(Processor.unwrap(entity));
        resp.getWriter().write("<div class=\"status-" + status + "\">" + status + "</div>");
    }
    
    // #C03 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (boolean)
    public void deepFlowBoolean(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        boolean active = Processor.extractEntityActive(Processor.unwrap(entity));
        resp.getWriter().write("<input type=\"checkbox\" " + (active ? "checked" : "") + "/>");
    }
    
    // #C04 - 4-layer flow: Request -> Transformer -> Repository -> Processor -> Output (double)
    public void deepFlowDouble(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        double balance = Processor.extractEntityBalance(Processor.unwrap(entity));
        resp.getWriter().write("<span>$" + String.format("%.2f", balance) + "</span>");
    }
    
    // #C05 - Collection flow: Repository -> List -> Processor.firstOrNull -> getId
    public void collectionFlowId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        long id = Processor.getIdFromFirst(entities);
        resp.getWriter().write("<span>" + id + "</span>");
    }
    
    // #C06 - Collection flow: Repository -> List -> Processor.firstOrNull -> getStatus
    public void collectionFlowStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        int status = Processor.getStatusFromFirst(entities);
        resp.getWriter().write("<span>" + status + "</span>");
    }
    
    // #C07 - Generic extraction: Repository -> List -> Transformer.extractIds -> first
    public void genericExtractionId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        List<Long> ids = entities.stream().map(Entity::getId).collect(java.util.stream.Collectors.toList());
        Long firstId = Transformer.getFirst(ids);
        resp.getWriter().write("<span>" + (firstId != null ? firstId : 0) + "</span>");
    }
    
    // #C08 - Account masked SSN through processor
    public void accountMaskedSsn(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        String masked = Processor.extractMaskedSsn(Processor.unwrap(account));
        System.out.println("SSN: " + masked);
    }
    
    // #C09 - Account tier through processor
    public void accountTier(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        int tier = Processor.extractAccountTier(Processor.unwrap(account));
        resp.getWriter().write("<span>Tier: " + tier + "</span>");
    }
    
    // #C10 - Account verified through processor
    public void accountVerified(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        boolean verified = Processor.extractAccountVerified(Processor.unwrap(account));
        resp.getWriter().write("<span>" + (verified ? "Yes" : "No") + "</span>");
    }
    
    // #C11 - Lambda processing: Request -> process(input, Integer::parseInt) -> Output
    public void lambdaProcessing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("value");
        Integer result = Processor.process(input, Integer::parseInt);
        resp.getWriter().write("<span>" + result + "</span>");
    }
    
    // #C12 - Chained method calls: entity.getCode() returns int derived from id
    public void chainedMethods(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        int code = entity.map(Entity::getCode).orElse(0);
        resp.getWriter().write("<code>" + code + "</code>");
    }
}

