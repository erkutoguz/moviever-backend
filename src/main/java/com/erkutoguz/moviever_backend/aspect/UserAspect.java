package com.erkutoguz.moviever_backend.aspect;

import com.erkutoguz.moviever_backend.dto.log.UserLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class UserAspect {

    private static final Logger logger = LoggerFactory.getLogger(UserAspect.class);

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

    @Around("movieAndCategoryAndReviewAndWatchlistController()")
    public Object userServicesTrack(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        UserLog log = new UserLog();
        log.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        log.setMethodSignature(joinPoint.getSignature().getName());
        log.setArgs(Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList());
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

}
