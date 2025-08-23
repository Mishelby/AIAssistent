package ru.development.main.aop;

import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.model.snapshots.Unit;
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
public class ExecutionHttpRequestsTime {
    private final Histogram histogram;

    public ExecutionHttpRequestsTime(Histogram histogram) {
        this.histogram = histogram;
    }

    @Around("@annotation(HttpRequestServiceTime)")
    public Object executionHttpRequestsTime(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = nonNull(attrs) ? attrs.getRequest() : null;
        String pathInfo = nonNull(httpServletRequest) ? httpServletRequest.getPathInfo() : null;

        String method = nonNull(httpServletRequest) ? httpServletRequest.getMethod() : "UNKNOW";
        long startTime = System.nanoTime();

        try {
            Object proceed = joinPoint.proceed();
            histogram.labelValues(method, pathInfo, "200").observe(Unit.nanosToSeconds(System.nanoTime() - startTime));
            return proceed;
        } catch (Exception ex) {
            histogram.labelValues(method, pathInfo, "500").observe(Unit.nanosToSeconds(System.nanoTime() - startTime));
            throw ex;
        }
    }
}
