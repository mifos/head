package org.mifos.rest.approval.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SystemArchitecture {

  @Pointcut("execution(public * org.mifos.platform.rest.controller.stub.*RESTController.*(..))")
  public void inWebLayer() {}

  @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
  public void inServiceLayer() {}

  @Pointcut("inWebLayer() && inServiceLayer()")
  public void inDataAccessLayer() {}

}