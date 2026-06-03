package com.supermap.common.filter;

import com.supermap.common.util.UUIDUtils;
import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;

public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        try {
            String traceId = UUIDUtils.get();

            MDC.put(TRACE_ID, traceId);

            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

}