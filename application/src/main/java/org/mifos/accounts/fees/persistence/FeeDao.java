package org.mifos.accounts.fees.persistence;

import java.util.List;


import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.persistence.GenericDao;

public interface FeeDao extends GenericDao<FeeEntity, Short> {
    List<FeeEntity> retrieveCustomerFees();

}
