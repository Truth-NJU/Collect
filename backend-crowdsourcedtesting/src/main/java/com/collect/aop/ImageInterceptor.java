package com.collect.aop;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageInterceptor implements HandlerInterceptor {


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
//        final String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(403);
//            return false;
//        }
//        final String token = authHeader.substring(7);
//        try {
//            TokenUtil.parseJWT(token);
//        } catch (final MalformedJwtException | ExpiredJwtException e) {
//            response.sendError(404);
//            return false;
//        }
        return true;
    }
}
