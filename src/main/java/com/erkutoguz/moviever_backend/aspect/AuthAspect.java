package com.erkutoguz.moviever_backend.aspect;

import com.erkutoguz.moviever_backend.dto.log.AuthLog;
import com.erkutoguz.moviever_backend.dto.request.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class AuthAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuthAspect.class);

    @Pointcut("execution(* com.erkutoguz.moviever_backend.controller.AuthenticationController.*(..))")
    public void authController() {}

    @Around("authController() && !execution(* com.erkutoguz.moviever_backend.controller.AuthenticationController.logoutUser(..))")
    public Object authTrack(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        AuthLog log = new AuthLog();
        String username = "";
        if(joinPoint.getSignature().getName().equals("registerUser")
                || joinPoint.getSignature().getName().equals("registerUser")) {
            AuthRequest authRequest = (AuthRequest) joinPoint.getArgs()[0];
            username = authRequest.username();
        } else if(joinPoint.getSignature().getName().equals("logoutUser")
                || joinPoint.getSignature().getName().equals("refreshToken")) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        log.setUsername(username);
        log.setMethodSignature(joinPoint.getSignature().getName());
        log.setExecutionTime(executionTime);

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();

            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setResponseCode(response.getStatus());
        }

        logger.info("{}", log);
        return proceed;
    }

    @Before("execution(* com.erkutoguz.moviever_backend.controller.AuthenticationController.logoutUser(..))")
    public void beforeLogout(JoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        long executionTime = System.currentTimeMillis() - start;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Authentication authenticationAfter = SecurityContextHolder.getContext().getAuthentication();
        AuthLog log = new AuthLog();
        log.setUsername(authenticationAfter.getName());
        log.setMethodSignature(joinPoint.getSignature().getName());
        log.setExecutionTime(executionTime);

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setResponseCode(response.getStatus());
        }

        logger.info("{}", log);
    }

}
