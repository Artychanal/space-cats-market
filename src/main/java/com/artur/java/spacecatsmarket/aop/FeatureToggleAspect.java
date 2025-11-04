package com.artur.java.spacecatsmarket.aop;

import com.artur.java.spacecatsmarket.service.exception.FeatureNotAvailableException;
import com.artur.java.spacecatsmarket.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class FeatureToggleAspect {

    private final FeatureToggleService featureToggleService;

    @Around("@annotation(com.artur.java.spacecatsmarket.aop.FeatureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        FeatureToggle featureToggle = method.getAnnotation(FeatureToggle.class);

        String featureName = featureToggle.value();

        log.debug("Checking feature toggle for feature: {}", featureName);

        if (!featureToggleService.isFeatureEnabled(featureName)) {
            log.warn("Feature '{}' is disabled. Method execution blocked: {}",
                    featureName, method.getName());
            throw new FeatureNotAvailableException(
                    "Feature '%s' is not available".formatted(featureName));
        }

        log.debug("Feature '{}' is enabled. Proceeding with method execution", featureName);
        return joinPoint.proceed();
    }
}