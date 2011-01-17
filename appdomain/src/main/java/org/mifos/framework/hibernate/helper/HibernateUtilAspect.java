package org.mifos.framework.hibernate.helper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class HibernateUtilAspect {

    @Around("@annotation(org.mifos.framework.hibernate.helper.Transactional)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {

      Object retVal = pjp.proceed();

      return retVal;
    }

}
