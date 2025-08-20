package ru.development.infrastructurekafka.aop;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetaDataAspect {
    @Around("@annotation(metaDataInfo)")
    public Object getMetaData(ProceedingJoinPoint joinPoint, MetaDataInfo metaDataInfo) throws Throwable {
        Object proceed = joinPoint.proceed();

        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String annotationValue  = metaDataInfo.value();

        log.info("[META_DATA_INFO] MetaDataInfo для: {}, {}, {}", simpleClassName,  methodName, annotationValue);
        log.info("[META_DATA_INFO] Args: {}", Arrays.toString(joinPoint.getArgs()));

        return proceed;
    }
}
