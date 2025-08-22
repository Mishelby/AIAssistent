package ru.development.main.aop;

import io.prometheus.metrics.core.metrics.Counter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.util.Objects.nonNull;


@Slf4j
@Aspect
@Component
public class CountHttpRequest {
    private final Counter counter;

    public CountHttpRequest(Counter counter) {
        this.counter = counter;
    }

    @Around("@annotation(HttpRequestsCounter)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = nonNull(attrs) ? attrs.getRequest() : null;

        String method = nonNull(httpServletRequest) ? httpServletRequest.getMethod() : "UNKNOW";

        try{
            Object proceed = joinPoint.proceed();
            counter.labelValues(method, "200").inc();
            return proceed;
        }catch (Exception e){
            counter.labelValues(method, "500").inc();
            throw e;
        }

    }
}
