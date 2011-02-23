package org.mifos.framework.hibernate.helper;

import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

public class AuditInterceptorFactory implements InterceptorFactory {
    @Override
    public Object create() {
        return new AuditInterceptor();
    }
}
