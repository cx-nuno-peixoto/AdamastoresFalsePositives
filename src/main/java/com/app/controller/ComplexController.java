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

/**
 * Complex Multi-Layer Flow False Positive Scenarios
 * All scenarios are SAFE but CxQL may incorrectly flag them
 * Pattern: 4+ layer cross-file data flows with type-safe transformations
 */
public class ComplexController extends HttpServlet {

    private EntityRepository entityRepo;
    private AccountRepository accountRepo;

    /*
     * #C01 - FALSE POSITIVE: 4-layer flow returning numeric ID
     * WHY SAFE: Flow: Request -> Transformer (Long.parseLong) -> Repository -> Processor -> Output
     *           Final output is Entity.id which is a long primitive (database auto-generated).
     *           Long primitives cannot contain XSS payloads - output is just a number.
     * WHY CXQL FAILS: CxQL tracks request parameter through 4 layers but loses type info.
     *                 It cannot determine that extractEntityId() returns a safe long type.
     *                 Cross-file flow analysis may not track return types accurately.
     * CXQL LIMITATION: Type inference lost across multiple file boundaries.
     */
    public void deepFlowNumeric(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        long outputId = Processor.extractEntityId(Processor.unwrap(entity));
        resp.getWriter().write("<span>" + outputId + "</span>");
    }

    /*
     * #C02 - FALSE POSITIVE: 4-layer flow returning integer status
     * WHY SAFE: extractEntityStatus() returns Entity.status which is an int (0-5).
     *           Integer primitives cannot contain XSS - output is just a number.
     *           Status is database-stored enum ordinal, not user input.
     * WHY CXQL FAILS: CxQL may confuse the request parameter flow with DB output flow.
     *                 It cannot determine that status is an int from database, not input.
     * CXQL LIMITATION: Confusion between request parameter and DB entity property.
     */
    public void deepFlowStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        int status = Processor.extractEntityStatus(Processor.unwrap(entity));
        resp.getWriter().write("<div class=\"status-" + status + "\">" + status + "</div>");
    }

    /*
     * #C03 - FALSE POSITIVE: 4-layer flow returning boolean
     * WHY SAFE: extractEntityActive() returns Entity.active which is a boolean.
     *           Boolean can only be true/false - output is "checked" or "".
     *           No user input can influence the boolean value directly.
     * WHY CXQL FAILS: CxQL tracks request param but loses track of actual output type.
     *                 It cannot determine that ternary produces only static strings.
     * CXQL LIMITATION: Boolean-conditional string output not recognized as safe.
     */
    public void deepFlowBoolean(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        boolean active = Processor.extractEntityActive(Processor.unwrap(entity));
        resp.getWriter().write("<input type=\"checkbox\" " + (active ? "checked" : "") + "/>");
    }

    /*
     * #C04 - FALSE POSITIVE: 4-layer flow returning double
     * WHY SAFE: extractEntityBalance() returns Entity.balance which is a double.
     *           Double primitives formatted with %.2f produce only numeric output.
     *           XSS requires control characters which double cannot contain.
     * WHY CXQL FAILS: CxQL may not analyze String.format pattern for safety.
     *                 It sees DB data flowing to output but misses double type.
     * CXQL LIMITATION: Double/formatted numeric output not analyzed.
     */
    public void deepFlowDouble(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        double balance = Processor.extractEntityBalance(Processor.unwrap(entity));
        resp.getWriter().write("<span>$" + String.format("%.2f", balance) + "</span>");
    }

    /*
     * #C05 - FALSE POSITIVE: Collection flow returning ID
     * WHY SAFE: getIdFromFirst() returns Entity.id (long) from first collection element.
     *           This is DB-stored auto-generated ID, not user input.
     *           Long primitive cannot contain XSS payload.
     * WHY CXQL FAILS: CxQL may flag DB output without recognizing safe type.
     *                 It cannot track type through collection operations.
     * CXQL LIMITATION: Generic collection type inference failure.
     */
    public void collectionFlowId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        long id = Processor.getIdFromFirst(entities);
        resp.getWriter().write("<span>" + id + "</span>");
    }

    /*
     * #C06 - FALSE POSITIVE: Collection flow returning status
     * WHY SAFE: getStatusFromFirst() returns Entity.status (int) from first element.
     *           Integer primitive from DB cannot contain XSS.
     * WHY CXQL FAILS: Same as C05 - type tracking lost through collection operations.
     * CXQL LIMITATION: Generic collection property extraction not typed.
     */
    public void collectionFlowStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        int status = Processor.getStatusFromFirst(entities);
        resp.getWriter().write("<span>" + status + "</span>");
    }

    /*
     * #C07 - FALSE POSITIVE: Stream map extracting IDs
     * WHY SAFE: Stream.map(Entity::getId) extracts Long IDs into typed list.
     *           getFirst() returns first Long from list.
     *           Output is a Long value (or 0), both safe numeric types.
     * WHY CXQL FAILS: CxQL cannot analyze Stream/lambda operations for type safety.
     *                 Method reference Entity::getId not recognized for return type.
     * CXQL LIMITATION: Java Stream API and method references not analyzed.
     */
    public void genericExtractionId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Entity> entities = entityRepo.findAll();
        List<Long> ids = entities.stream().map(Entity::getId).collect(java.util.stream.Collectors.toList());
        Long firstId = Transformer.getFirst(ids);
        resp.getWriter().write("<span>" + (firstId != null ? firstId : 0) + "</span>");
    }

    /*
     * #C08 - FALSE POSITIVE: Account SSN masked through processor
     * WHY SAFE: extractMaskedSsn() applies Sanitizer.mask() to SSN.
     *           Only last 4 digits visible, rest replaced with asterisks.
     *           Masking is done in Processor, not visible to CxQL analysis.
     * WHY CXQL FAILS: CxQL sees SSN flowing through processor to output.
     *                 It cannot determine that Processor masks the value.
     *                 Cross-file masking operation not recognized.
     * CXQL LIMITATION: Masking in helper class not recognized as sanitization.
     */
    public void accountMaskedSsn(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        String masked = Processor.extractMaskedSsn(Processor.unwrap(account));
        System.out.println("SSN: " + masked);
    }

    /*
     * #C09 - FALSE POSITIVE: Account tier through processor
     * WHY SAFE: extractAccountTier() returns Account.tier which is an int (1-5).
     *           Tier is business logic derived value, not user input.
     *           Integer primitives cannot contain XSS.
     * WHY CXQL FAILS: CxQL tracks request param through layers, loses type info.
     *                 It cannot determine that output is DB integer field.
     * CXQL LIMITATION: Integer field extraction not typed through helpers.
     */
    public void accountTier(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        int tier = Processor.extractAccountTier(Processor.unwrap(account));
        resp.getWriter().write("<span>Tier: " + tier + "</span>");
    }

    /*
     * #C10 - FALSE POSITIVE: Account verified status through processor
     * WHY SAFE: extractAccountVerified() returns Account.verified (boolean).
     *           Output is only "Yes" or "No" literal strings.
     *           Boolean ternary with string literals is inherently safe.
     * WHY CXQL FAILS: CxQL may track account data to output.
     *                 It cannot determine that output is constrained to literals.
     * CXQL LIMITATION: Boolean ternary with literals not recognized as safe.
     */
    public void accountVerified(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Account> account = accountRepo.findById(inputId);
        boolean verified = Processor.extractAccountVerified(Processor.unwrap(account));
        resp.getWriter().write("<span>" + (verified ? "Yes" : "No") + "</span>");
    }

    /*
     * #C11 - FALSE POSITIVE: Lambda processing with Integer::parseInt
     * WHY SAFE: Processor.process() applies Integer::parseInt to input.
     *           If input is not numeric, NumberFormatException is thrown.
     *           Valid inputs produce Integer output - cannot contain XSS.
     * WHY CXQL FAILS: CxQL cannot analyze lambda/method reference for type conversion.
     *                 It sees request param flowing to output through generic process().
     * CXQL LIMITATION: Lambda type inference not supported.
     */
    public void lambdaProcessing(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String input = req.getParameter("value");
        Integer result = Processor.process(input, Integer::parseInt);
        resp.getWriter().write("<span>" + result + "</span>");
    }

    /*
     * #C12 - FALSE POSITIVE: Chained method call with Optional.map
     * WHY SAFE: entity.map(Entity::getCode) extracts Entity.code (int).
     *           Code is derived from ID (e.g., id.hashCode()) - numeric type.
     *           orElse(0) provides default int, output is always numeric.
     * WHY CXQL FAILS: CxQL cannot analyze Optional.map with method reference.
     *                 It loses type information through Optional chaining.
     * CXQL LIMITATION: Optional.map/flatMap type inference not supported.
     */
    public void chainedMethods(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long inputId = Transformer.stringToLong(req.getParameter("id"));
        Optional<Entity> entity = entityRepo.findById(inputId);
        int code = entity.map(Entity::getCode).orElse(0);
        resp.getWriter().write("<code>" + code + "</code>");
    }
}

