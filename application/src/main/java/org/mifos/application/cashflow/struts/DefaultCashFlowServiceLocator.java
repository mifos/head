package org.mifos.application.cashflow.struts;

import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.service.MifosServiceFactory;

import javax.servlet.http.HttpServletRequest;

public class DefaultCashFlowServiceLocator implements CashFlowServiceLocator {
    @Override
    public CashFlowService getService(HttpServletRequest request) {
        return MifosServiceFactory.getCashFlowService(request);
    }
}
