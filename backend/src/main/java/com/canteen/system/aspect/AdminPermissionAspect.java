package com.canteen.system.aspect;

import com.canteen.system.annotation.RequireAdmin;
import com.canteen.system.util.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AdminPermissionAspect {
    
    @Around("@annotation(com.canteen.system.annotation.RequireAdmin)")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Integer role = UserContext.getCurrentUserRole();
        
        if (role == null || (role != 1 && role != 2)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            RequireAdmin annotation = method.getAnnotation(RequireAdmin.class);
            throw new RuntimeException(annotation.message());
        }
        
        return joinPoint.proceed();
    }
}
