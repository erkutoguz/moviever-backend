package com.erkutoguz.moviever_backend.aspect;

import com.erkutoguz.moviever_backend.dto.log.ErrorLog;
import com.erkutoguz.moviever_backend.exception.AccessDeniedException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class ErrorAspect {
    private static final Logger logger = LoggerFactory.getLogger(ErrorAspect.class);

    @Pointcut("execution(* com.erkutoguz.moviever_backend.service.MovieService.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.service.ReviewService.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.service.WatchlistService.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.service.CategoryService.*(..))")
    public void movieAndCategoryAndReviewAndWatchlistService() {}

    @Pointcut("execution(* com.erkutoguz.moviever_backend.controller.MovieController.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.controller.ReviewController.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.controller.WatchlistController.*(..)) || " +
            "execution(* com.erkutoguz.moviever_backend.controller.CategoryController.*(..))")
    public void movieAndCategoryAndReviewAndWatchlistController() {}

    @Pointcut("execution(* com.erkutoguz.moviever_backend.controller.AuthenticationController.*(..))")
    public void authController() {}

    @AfterThrowing(value = "movieAndCategoryAndReviewAndWatchlistController() || authController()", throwing = "exception")
    public void trackErrors(JoinPoint joinPoint, Throwable exception) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorLog log = new ErrorLog();
        log.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        log.setMethodSignature(joinPoint.getSignature().getName());

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.setRequestUrl(request.getRequestURL().toString());
            log.setRequestMethod(request.getMethod());
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }
        if(exception instanceof AccessDeniedException) {
            log.setErrorCode(((AccessDeniedException) exception).getStatusCode());
        } else if(exception instanceof ResourceNotFoundException) {
            log.setErrorCode(((ResourceNotFoundException) exception).getStatusCode());
        } else {
            log.setErrorCode(status.value());
        }

        log.setErrorMessage(exception.getMessage());

        logger.info("{}", log);

    }
}
