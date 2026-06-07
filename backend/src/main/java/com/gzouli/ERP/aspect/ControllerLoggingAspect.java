package com.gzouli.ERP.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * Pointcut qui intercepte toutes les méthodes des classes situées
     * dans le package com.gzouli.ERP.controller et ses sous-packages.
     */
    @Pointcut("within(com.gzouli.ERP.controller..*)")
    public void controllerPointcut() {
        // La méthode sert de signature au pointcut, elle reste vide.
    }

    /**
     * Log à l'entrée et à la sortie de chaque appel, et calcule le temps d'exécution.
     */
    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isInfoEnabled()) {
            log.info("Enter: {}.{}() with argument[s] = {}", 
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), 
                    Arrays.toString(joinPoint.getArgs()));
        }
        
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            if (log.isInfoEnabled()) {
                log.info("Exit: {}.{}() with execution time = {} ms", 
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), 
                        elapsedTime);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

    /**
     * Log les exceptions non interceptées par le try-catch ou rejetées plus haut.
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), 
                e.getCause() != null ? e.getCause().toString() : "NULL", e.getMessage());
    }
}
