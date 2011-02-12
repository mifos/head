/*
 * Copyright Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.business.service;

import static org.mifos.framework.util.helpers.NumberUtils.getPercentage;

import java.math.BigDecimal;

import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

/**
 * @deprecated - do not add any more behaviour to this {@link BusinessService}. Service level logic should go on {@link CustomerService}. Any data-retrieval methods should go on {@link CustomerDao}.
 */
@Deprecated
public class CustomerBusinessService implements BusinessService {

    private final CustomerPersistence customerPersistence;

    public CustomerBusinessService() {
        this(new CustomerPersistence());
    }

    public CustomerBusinessService(CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
    }

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    public CustomerBO getCustomer(Integer customerId) throws ServiceException {
        try {
            return customerPersistence.getCustomer(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public CustomerBO findBySystemId(String globalCustNum) throws ServiceException {
        try {
            return customerPersistence.findBySystemId(globalCustNum);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public Integer getCenterCountForOffice(OfficeBO office) throws ServiceException {
        return getCustomerCountForOffice(CustomerLevel.CENTER, office);
    }

    public Integer getGroupCountForOffice(OfficeBO office) throws ServiceException {
        return getCustomerCountForOffice(CustomerLevel.GROUP, office);
    }

    public Integer getActiveClientCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getActiveClientCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorClientCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getVeryPoorClientCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getActiveBorrowersCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getActiveBorrowersCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorActiveBorrowersCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getVeryPoorActiveBorrowersCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getCustomerReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue)
            throws ServiceException {
        try {
            return customerPersistence.getCustomerReplacementsCountForOffice(office, fieldId, fieldValue);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getCustomerVeryPoorReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue)
            throws ServiceException {
        try {
            return customerPersistence.getVeryPoorReplacementsCountForOffice(office, fieldId, fieldValue);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getDormantClientsCountByLoanAccountForOffice(OfficeBO office, Integer loanCyclePeriod)
            throws ServiceException {
        try {
            return customerPersistence.getDormantClientsCountByLoanAccountForOffice(office, loanCyclePeriod);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getDormantClientsCountBySavingAccountForOffice(OfficeBO office, Integer loanCyclePeriod)
            throws ServiceException {
        try {
            return customerPersistence.getDormantClientsCountBySavingAccountForOffice(office, loanCyclePeriod);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorDormantClientsCountByLoanAccountForOffice(OfficeBO office, Integer loanCyclePeriod)
            throws ServiceException {
        try {
            return customerPersistence.getVeryPoorDormantClientsCountByLoanAccountForOffice(office, loanCyclePeriod);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorDormantClientsCountBySavingAccountForOffice(OfficeBO office, Integer loanCyclePeriod)
            throws ServiceException {
        try {
            return customerPersistence.getVeryPoorDormantClientsCountBySavingAccountForOffice(office, loanCyclePeriod);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getDropOutClientsCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getDropOutClientsCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public BigDecimal getClientDropOutRateForOffice(OfficeBO office) throws ServiceException {
        Integer dropOutClientsCountForOffice = getDropOutClientsCountForOffice(office);
        try {
            Integer activeOrHoldClientCountForOffice = customerPersistence.getActiveOrHoldClientCountForOffice(office);
            return getPercentage(dropOutClientsCountForOffice, dropOutClientsCountForOffice
                    + activeOrHoldClientCountForOffice);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public BigDecimal getVeryPoorClientDropoutRateForOffice(OfficeBO office) throws ServiceException {
        Integer veryPoorDropOutClientsCountForOffice = getVeryPoorDropOutClientsCountForOffice(office);

        try {
            Integer veryPoorActiveOrHoldClientCountForOffice = customerPersistence
                    .getVeryPoorActiveOrHoldClientCountForOffice(office);
            return getPercentage(veryPoorDropOutClientsCountForOffice, veryPoorDropOutClientsCountForOffice
                    + veryPoorActiveOrHoldClientCountForOffice);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorDropOutClientsCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getVeryPoorDropOutClientsCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getOnHoldClientsCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getOnHoldClientsCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorOnHoldClientsCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getVeryPoorOnHoldClientsCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getActiveSaversCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getActiveSaversCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Integer getVeryPoorActiveSaversCountForOffice(OfficeBO office) throws ServiceException {
        try {
            return customerPersistence.getVeryPoorActiveSaversCountForOffice(office);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    private Integer getCustomerCountForOffice(CustomerLevel customerLevel, OfficeBO office) throws ServiceException {
        try {

            return customerPersistence.getCustomerCountForOffice(customerLevel, office.getOfficeId());
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }
}
