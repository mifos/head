package org.mifos.accounts.fees.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.servicefacade.GenericDao;

public interface FeeDao extends GenericDao<FeeEntity, Short> {
    List<FeeEntity> retrieveCustomerFees();

}
