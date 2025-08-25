package ru.development.main.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.development.main.model.MetadataInfo;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Aspect
@Component
public class RetryableAspect {
    private final RetryTemplate retryTemplate;

    public RetryableAspect(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Around("@annotation(Retryable)")
    public Object getRetryable(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = nonNull(attributes) ?  attributes.getRequest() : null;
        String pathInfo = nonNull(request) ? request.getPathInfo() : null;
        String method = nonNull(request) ?  request.getMethod() : "UNKNOWN";

        MetadataInfo.MetadataInfoBuilder metadata = MetadataInfo
                .builder()
                .pathInfo(pathInfo)
                .methodName(method);
        List<String> params = new ArrayList<>();

        if(nonNull(request)){
            request.getAttributeNames().asIterator().forEachRemaining(name -> {
                if (nonNull(name)) params.add(name);
            });
        }
        metadata.params(params).build();

        return retryTemplate.execute((RetryCallback<Object, Throwable>) retryContext -> {
            try {
                Object response = joinPoint.proceed();
                if (isNull(response)) {
                    log.warn("[ERROR] Response is null");
                    throw new RuntimeException("Response is null");
                }

                return response;
            } catch (Throwable e) {
                log.warn("[WARN] Ошибка при вызове метода, metadata: {}. Попытка {}",
                        metadata, retryContext.getRetryCount() + 1);
                throw e;
            }
        });
    }
}
