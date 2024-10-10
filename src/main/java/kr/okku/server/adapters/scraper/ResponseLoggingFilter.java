package kr.okku.server.adapters.scraper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@Component
public class ResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);
        chain.doFilter(request, responseCopier);
        byte[] copy = responseCopier.getCopy();

        String responseBody = new String(copy, response.getCharacterEncoding());
        System.out.printf("Response Body: {%s}", responseBody);
    }
}

