package com.checkmarx.processors.batch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IterationProcessor {
    
    private static final int MAX_ITEMS = 100;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int MAX_RETRIES = 10;
    private static final int MAX_BATCH_SIZE = 1000;
    private static final long MAX_ITERATIONS = 500L;
    
    public enum Level { LOW, MEDIUM, HIGH, CRITICAL }

    // LC-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        int result = Math.min(value, MAX_ITEMS);
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "\n");
        }
    }

    // LC-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("pageSize");
        int value = Integer.parseInt(param);
        int result = (value > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : value;
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Row " + i + "\n");
        }
    }

    // LC-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("retries");
        int value = Integer.parseInt(param);
        int result;
        if (value > MAX_RETRIES) {
            result = MAX_RETRIES;
        } else if (value < 0) {
            result = 0;
        } else {
            result = value;
        }
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Retry " + i + "\n");
        }
    }

    // LC-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("iterations");
        long value = Long.parseLong(param);
        long result = Math.min(value, MAX_ITERATIONS);
        PrintWriter out = response.getWriter();
        for (long i = 0; i < result; i++) {
            out.write("Iteration " + i + "\n");
        }
    }

    // LC-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("batchSize");
        int value = Integer.parseInt(param);
        int result = Math.max(1, Math.min(value, MAX_BATCH_SIZE));
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Batch item " + i + "\n");
        }
    }

    // LC-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = clamp(value, 0, MAX_ITEMS);
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Value " + i + "\n");
        }
    }

    // LC-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("level");
        int value = Integer.parseInt(param);
        int result = Math.min(value, Math.min(Level.values().length, MAX_RETRIES));
        PrintWriter out = response.getWriter();
        for (int i = 0; i < result; i++) {
            out.write("Level " + i + "\n");
        }
    }

    // LC-MR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        final int MAX = 100;
        PrintWriter out = response.getWriter();
        for (int i = 0; i < value && i < MAX; i++) {
            out.write("Item " + i + "\n");
        }
    }

    // LC-MR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        PrintWriter out = response.getWriter();
        if (value > 0 && value <= 100) {
            for (int i = 0; i < value; i++) {
                out.write("Item " + i + "\n");
            }
        }
    }

    // LC-MR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String param = request.getParameter("count");
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[0-9]{1,2}$");
        java.util.regex.Matcher matcher = pattern.matcher(param);
        PrintWriter out = response.getWriter();
        if (matcher.matches()) {
            int value = Integer.parseInt(param);
            for (int i = 0; i < value; i++) {
                out.write("Item " + i + "\n");
            }
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}

