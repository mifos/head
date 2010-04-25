/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.business.CustomerActivityEntity;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.client.business.CustomerPictureEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerRecentActivityDto;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

/**
 * TODO - keithw - move all methods of this on to either CustomerService or CustomerDao.
 *
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

    public QueryResult searchGroupClient(String searchString, Short userId) throws ServiceException {
        try {
            return new CustomerPersistence().searchGroupClient(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ConfigurationException ce) {
            throw new ServiceException(ce);
        }

    }

    public QueryResult searchCustForSavings(String searchString, Short userId) throws ServiceException {
        try {
            return new CustomerPersistence().searchCustForSavings(searchString, userId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public CustomerBO getCustomer(Integer customerId) throws ServiceException {
        try {
            return new CustomerPersistence().getCustomer(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public CustomerBO findBySystemId(String globalCustNum) throws ServiceException {
        try {
            return new CustomerPersistence().findBySystemId(globalCustNum);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<CustomerRecentActivityDto> getRecentActivityView(Integer customerId) throws ServiceException {
        CustomerBO customerBO;
        try {
            customerBO = new CustomerPersistence().getCustomer(customerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        List<CustomerActivityEntity> customerAtivityDetails = customerBO.getCustomerAccount()
                .getCustomerActivitDetails();
        List<CustomerRecentActivityDto> customerActivityViewList = new ArrayList<CustomerRecentActivityDto>();

        int count = 0;
        for (CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
            customerActivityViewList.add(getCustomerActivityView(customerActivityEntity));
            if (++count == 3) {
                break;
            }
        }
        return customerActivityViewList;
    }

    public List<CustomerRecentActivityDto> getAllActivityView(String globalCustNum) throws ServiceException {
        CustomerBO customerBO = findBySystemId(globalCustNum);
        List<CustomerActivityEntity> customerAtivityDetails = customerBO.getCustomerAccount()
                .getCustomerActivitDetails();
        List<CustomerRecentActivityDto> customerActivityViewList = new ArrayList<CustomerRecentActivityDto>();
        for (CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
            customerActivityViewList.add(getCustomerActivityView(customerActivityEntity));
        }
        return customerActivityViewList;
    }

    public QueryResult search(String searchString, Short officeId, Short userId, Short userOfficeId)
            throws ServiceException {

        try {
            return new CustomerPersistence().search(searchString, officeId, userId, userOfficeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public List<CustomerCheckListBO> getStatusChecklist(Short statusId, Short customerLevelId) throws ServiceException {
        try {
            return new CustomerPersistence().getStatusChecklist(statusId, customerLevelId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<CustomerStatusEntity> retrieveAllCustomerStatusList(Short levelId) throws ServiceException {
        try {
            return new CustomerPersistence().retrieveAllCustomerStatusList(levelId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public void initializeStateMachine(Short localeId, Short officeId, AccountTypes accountTypes,
            CustomerLevel customerLevel) throws ServiceException {
        try {
            AccountStateMachines.getInstance().initialize(localeId, officeId, accountTypes, customerLevel);
        } catch (StatesInitializationException sie) {
            throw new ServiceException(sie);
        }
    }

    public String getStatusName(Short localeId, CustomerStatus customerStatus, CustomerLevel customerLevel) {
        return AccountStateMachines.getInstance().getCustomerStatusName(localeId, customerStatus, customerLevel);
    }

    public String getFlagName(Short localeId, CustomerStatusFlag customerStatusFlag, CustomerLevel customerLevel) {
        return AccountStateMachines.getInstance().getCustomerFlagName(localeId, customerStatusFlag, customerLevel);
    }

    public List<CustomerStatusEntity> getStatusList(CustomerStatusEntity customerStatusEntity,
            CustomerLevel customerLevel, Short localeId) {
        List<CustomerStatusEntity> statusList = AccountStateMachines.getInstance().getStatusList(customerStatusEntity,
                customerLevel);
        if (null != statusList) {
            for (CustomerStatusEntity customerStatusObj : statusList) {
                customerStatusObj.setLocaleId(localeId);
            }
        }
        return statusList;
    }

    public QueryResult getAllCustomerNotes(Integer customerId) throws ServiceException {
        try {
            return new CustomerPersistence().getAllCustomerNotes(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public CustomerPictureEntity retrievePicture(Integer customerId) throws ServiceException {
        try {
            return new CustomerPersistence().retrievePicture(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public void checkPermissionForStatusChange(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) throws ServiceException {
        if (!isPermissionAllowed(newState, userContext, flagSelected, recordOfficeId, recordLoanOfficerId)) {
            throw new ServiceException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    public boolean isPermissionAllowed(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isStateChangePermittedForCustomer(newState.shortValue(),
                null != flagSelected ? flagSelected.shortValue() : 0, userContext, recordOfficeId, recordLoanOfficerId);
    }

    public List<AccountBO> getAllClosedAccount(Integer customerId, Short accountTypeId) throws ServiceException {
        try {
            return new CustomerPersistence().getAllClosedAccount(customerId, accountTypeId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<CustomerBO> getActiveCentersUnderUser(PersonnelBO personnel) throws ServiceException {
        try {
            return new CustomerPersistence().getActiveCentersUnderUser(personnel);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public Integer getCenterCountForOffice(OfficeBO office) throws ServiceException {
        return getCustomerCountForOffice(CustomerLevel.CENTER, office);
    }

    public Integer getClientCountForOffice(OfficeBO office) throws ServiceException {
        return getCustomerCountForOffice(CustomerLevel.CLIENT, office);
    }

    public Integer getGroupCountForOffice(OfficeBO office) throws ServiceException {
        return getCustomerCountForOffice(CustomerLevel.GROUP, office);
    }

    public List<CustomerBO> getGroupsUnderUser(PersonnelBO personnel) throws ServiceException {
        try {
            return new CustomerPersistence().getGroupsUnderUser(personnel);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
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

    public List<CustomerBO> getCustomersByLevelId(Short customerLevelId) throws ServiceException {
        try {
            return new CustomerPersistence().getCustomersByLevelId(customerLevelId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    private CustomerRecentActivityDto getCustomerActivityView(CustomerActivityEntity customerActivityEntity) {
        CustomerRecentActivityDto customerRecentActivityDto = new CustomerRecentActivityDto();
        customerRecentActivityDto.setActivityDate(customerActivityEntity.getCreatedDate());
        customerRecentActivityDto.setDescription(customerActivityEntity.getDescription());
        Money amount = removeSign(customerActivityEntity.getAmount());
        if (amount.isZero()) {
            customerRecentActivityDto.setAmount("-");
        } else {
            customerRecentActivityDto.setAmount(amount.toString());
        }
        if (customerActivityEntity.getPersonnel() != null) {
            customerRecentActivityDto.setPostedBy(customerActivityEntity.getPersonnel().getDisplayName());
        }
        return customerRecentActivityDto;
    }

    private Money removeSign(Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }
        return amount;
    }

    private Integer getCustomerCountForOffice(CustomerLevel customerLevel, OfficeBO office) throws ServiceException {
        try {

            return customerPersistence.getCustomerCountForOffice(customerLevel, office.getOfficeId());
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }
}
