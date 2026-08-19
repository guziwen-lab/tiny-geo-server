package com.supermap.core.filter;

import com.supermap.core.constant.TraceIdConstant;
import com.supermap.core.util.UUIDUtils;
import jakarta.servlet.*;
import org.slf4j.MDC;

import java.io.IOException;

public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        try {
            String traceId = UUIDUtils.get();

            MDC.put(TraceIdConstant.TRACE_ID, traceId);

            chain.doFilter(request, response);
        } finally {
            MDC.remove(TraceIdConstant.TRACE_ID);
        }
    }

}