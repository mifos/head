package org.mifos.accounts.fees.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.framework.exceptions.ServiceException;

public interface FeeServiceFacade {

    public List<FeeDto> getProductFees() throws ServiceException;

    public List<FeeDto> getCustomerFees() throws ServiceException;

    public FeeParameters parameters(Short localeId) throws ServiceException;

}
