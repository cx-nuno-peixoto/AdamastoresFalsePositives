package com.checkmarx.handlers.batch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class BatchProcessor {
    
    public enum ItemLimit {
        SMALL(10), MEDIUM(50), LARGE(100);
        private final int size;
        ItemLimit(int size) { this.size = size; }
        public int getSize() { return size; }
    }

    // LC-MR:01
    public void scenario01(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("limit");
        int value = Integer.parseInt(param);
        int result = Math.min(value, 100);
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:02
    public void scenario02(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        int result = Math.max(0, Math.min(value, 50));
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:03
    public void scenario03(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("size");
        int value = Integer.parseInt(param);
        int result = (value > 50) ? 50 : value;
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:04
    public void scenario04(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("size");
        int value = Integer.parseInt(param);
        int result = (value > 50) ? 50 : ((value < 0) ? 0 : value);
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:05
    public void scenario05(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("batch");
        ItemLimit level = ItemLimit.valueOf(param.toUpperCase());
        int value = level.getSize();
        for (int i = 0; i < value; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:06
    public void scenario06(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        Pattern pattern = Pattern.compile("^[0-9]{1,2}$");
        Matcher matcher = pattern.matcher(param);
        if (matcher.matches()) {
            int value = Integer.parseInt(param);
            for (int i = 0; i < value; i++) {
                out.write("Item " + i + "<br>");
            }
        }
    }

    // LC-MR:07
    public void scenario07(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        final int MAX = 100;
        for (int i = 0; i < value && i < MAX; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-MR:08
    public void scenario08(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        if (value > 0 && value <= 100) {
            for (int i = 0; i < value; i++) {
                out.write("Item " + i + "<br>");
            }
        }
    }

    // LC-BR:09
    public void scenario09(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        int value = Integer.parseInt(param);
        for (int i = 0; i < value; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:10
    public void scenario10(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("count");
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(param);
        if (matcher.matches()) {
            int value = Integer.parseInt(param);
            for (int i = 0; i < value; i++) {
                out.write("Item " + i + "<br>");
            }
        }
    }

    // LC-BR:11
    public void scenario11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("base");
        int base = Integer.parseInt(param);
        int value = (base > 10) ? base * 2 : base;
        for (int i = 0; i < value; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:12
    public void scenario12(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = value + 100;
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:13
    public void scenario13(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("max");
        int max = Integer.parseInt(param);
        int i = 0;
        while (i < max) {
            out.write("Item " + i + "<br>");
            i++;
        }
    }

    // LC-BR:14
    public void scenario14(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("max");
        int max = Integer.parseInt(param);
        int i = 0;
        do {
            out.write("Item " + i + "<br>");
            i++;
        } while (i < max);
    }

    // LC-BR:15
    public void scenario15(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param1 = request.getParameter("outer");
        String param2 = request.getParameter("inner");
        int outer = Integer.parseInt(param1);
        int inner = Integer.parseInt(param2);
        for (int i = 0; i < outer; i++) {
            for (int j = 0; j < inner; j++) {
                out.write("Item " + i + "," + j + "<br>");
            }
        }
    }

    // LC-BR:16
    public void scenario16(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = value * 2;
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:17
    public void scenario17(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = value % 1000000;
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:18
    public void scenario18(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param = request.getParameter("value");
        int value = Integer.parseInt(param);
        int result = Math.abs(value);
        for (int i = 0; i < result; i++) {
            out.write("Item " + i + "<br>");
        }
    }

    // LC-BR:19
    public void scenario19(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String param1 = request.getParameter("first");
        int value1 = Integer.parseInt(param1);
        int bounded = Math.min(value1, 50);
        for (int i = 0; i < bounded; i++) {
            out.write("Item " + i + "<br>");
        }
        String param2 = request.getParameter("second");
        int value2 = Integer.parseInt(param2);
        for (int i = 0; i < value2; i++) {
            out.write("Item " + i + "<br>");
        }
    }
}

