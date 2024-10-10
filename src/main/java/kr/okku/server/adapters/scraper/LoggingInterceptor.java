package kr.okku.server.adapters.scraper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 URL 및 메서드 로깅
        System.out.printf("Request URL: {%s}, Method: {%s}", request.getRequestURL(), request.getMethod());

        // 파일 메타데이터 로깅
        if (request instanceof MultipartHttpServletRequest) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            multipartRequest.getFileMap().forEach((key, file) -> {
                System.out.printf("File Key: {%s}, File Name: {%s}, Size: {%s}, ContentType: {%s}", key, file.getOriginalFilename(), file.getSize(), file.getContentType());
            });
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 응답 상태 로깅
        System.out.printf("Response Status: {%s}", response.getStatus());
    }
}