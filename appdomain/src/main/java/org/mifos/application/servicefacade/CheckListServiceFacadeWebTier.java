/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.CheckListServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.business.service.CheckListBusinessService;
import org.mifos.customers.checklist.exceptions.CheckListException;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.CheckListMasterDto;
import org.mifos.dto.screen.AccountCheckBoxItemDto;
import org.mifos.dto.screen.CheckListStatesView;
import org.mifos.dto.screen.CustomerCheckBoxItemDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class CheckListServiceFacadeWebTier implements CheckListServiceFacade {

    private final CustomerDao customerDao;
    private HibernateTransactionHelper hibernateTransactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Autowired
    public CheckListServiceFacadeWebTier(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public List<CustomerCheckBoxItemDto> retreiveAllCustomerCheckLists() {
        try {
            List<CustomerCheckListBO> customerCheckLists = new CheckListBusinessService().retreiveAllCustomerCheckLists();
            List<CustomerCheckBoxItemDto> dtoList = new ArrayList<CustomerCheckBoxItemDto>();
            for (CustomerCheckListBO bo: customerCheckLists) {
                String  lookUpName = bo.getCustomerStatus().getLookUpValue() != null ?
                        bo.getCustomerStatus().getLookUpValue().getLookUpName(): null;
                CustomerCheckBoxItemDto dto = new CustomerCheckBoxItemDto(bo.getChecklistId(), bo.getChecklistName(),
                        bo.getChecklistStatus(), null ,
                        lookUpName, bo.getCustomerStatus().getId(),
                        bo.getCustomerLevel().getId());

                if(dto.getLookUpName() != null) {
                dto.setName(ApplicationContextProvider.getBean(MessageLookup.class).lookup(dto.getLookUpName()));
                }
                dtoList.add(dto);
            }
            return dtoList;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<AccountCheckBoxItemDto> retreiveAllAccountCheckLists() {
        try {
            List<AccountCheckListBO> accountCheckLists = new CheckListBusinessService().retreiveAllAccountCheckLists();
            List<AccountCheckBoxItemDto> dtoList = new ArrayList<AccountCheckBoxItemDto>();
            for (AccountCheckListBO bo: accountCheckLists) {
                String lookUpName = bo.getAccountStateEntity().getLookUpValue() != null ?
                        bo.getAccountStateEntity().getLookUpValue().getLookUpName(): null;
                AccountCheckBoxItemDto dto = new AccountCheckBoxItemDto(bo.getChecklistId(), bo.getChecklistName(),
                        bo.getChecklistStatus(), null ,
                        lookUpName, bo.getAccountStateEntity().getId(),
                        bo.getProductTypeEntity().getProductTypeID());
                if(dto.getLookUpName() != null) {
                    dto.setName(ApplicationContextProvider.getBean(MessageLookup.class).lookup(dto.getLookUpName()));
                    }
                dtoList.add(dto);
            }
            return dtoList;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CheckListMasterDto> retrieveChecklistMasterData() {

        try {
            Short localeIdNotUsed = null;

            List<CheckListMasterDto> masterData = new CheckListPersistence().getCheckListMasterData(localeIdNotUsed);

            for (CheckListMasterDto checkListMasterDto : masterData) {
                if (checkListMasterDto.isCustomer()) {
                    checkListMasterDto.setMasterTypeName(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(
                            checkListMasterDto.getLookupKey()));
                } else {
                    checkListMasterDto.setMasterTypeName(ApplicationContextProvider.getBean(MessageLookup.class).lookup(
                            checkListMasterDto.getLookupKey()));
                }
            }
            return masterData;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CheckListStatesView> retrieveAllAccountStates(Short prdTypeId) {
        Short localeIdNotUsed = null;
        try {
            return new CheckListPersistence().retrieveAllAccountStateList(prdTypeId, localeIdNotUsed);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CheckListStatesView> retrieveAllCustomerStates(Short levelId) {
        Short localeIdNotUsed = null;
        try {
            return new CheckListPersistence().retrieveAllCustomerStatusList(levelId, localeIdNotUsed);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void validateIsValidCheckListState(Short masterTypeId, Short stateId, boolean isCustomer) {
        try {
            Long records = new CheckListPersistence().isValidCheckListState(masterTypeId, stateId, isCustomer);
            if (records.intValue() != 0) {
                throw new BusinessRuleException(CheckListConstants.EXCEPTION_STATE_ALREADY_EXIST);
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void createCustomerChecklist(Short levelId, Short stateId, String checklistName, List<String> checklistDetails) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        CustomerLevel level = CustomerLevel.getLevel(levelId);
        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(level);
        CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(stateId);

        try {
            hibernateTransactionHelper.startTransaction();
            CustomerCheckListBO customerCheckListBO = new CustomerCheckListBO(customerLevelEntity,
                    customerStatusEntity, checklistName, CheckListConstants.STATUS_ACTIVE,
                    checklistDetails, userContext.getLocaleId(), userContext.getId());
            customerDao.save(customerCheckListBO);
            hibernateTransactionHelper.commitTransaction();
        } catch (CheckListException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public void createAccountChecklist(Short productId, Short stateId, String checklistName, List<String> checklistDetails) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            ProductTypeEntity productTypeEntity = null;
            for (ProductTypeEntity prdTypeEntity : new ProductCategoryBusinessService().getProductTypes()) {
                if (productId.equals(prdTypeEntity.getProductTypeID())) {
                    productTypeEntity = prdTypeEntity;
                    break;
                }
            }

            hibernateTransactionHelper.startTransaction();
            AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState.fromShort(stateId));
            AccountCheckListBO accountCheckListBO = new AccountCheckListBO(productTypeEntity, accountStateEntity,
                    checklistName, CheckListConstants.STATUS_ACTIVE, checklistDetails, userContext.getLocaleId(), userContext.getId());
            customerDao.save(accountCheckListBO);
            hibernateTransactionHelper.commitTransaction();
        } catch (ServiceException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (CheckListException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public void updateCustomerChecklist(Short checklistId, Short levelId, Short stateId, Short checklistStatus,
            String checklistName, List<String> details) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.getLevel(levelId));
        CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(stateId);

        try {
            hibernateTransactionHelper.startTransaction();
            CustomerCheckListBO customerCheckList = (CustomerCheckListBO) new CheckListPersistence().getCheckList(checklistId);

            customerCheckList.update(customerLevelEntity, customerStatusEntity, checklistName,
                    checklistStatus, details, userContext.getLocaleId(),userContext.getId());
            customerDao.save(customerCheckList);
            hibernateTransactionHelper.commitTransaction();
        } catch (CheckListException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (PersistenceException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public void updateAccountChecklist(Short checklistId, Short productId, Short stateId, Short checklistStatus, String checklistName, List<String> checklistDetails) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            ProductTypeEntity productTypeEntity = null;
            for (ProductTypeEntity prdTypeEntity : new ProductCategoryBusinessService().getProductTypes()) {
                if (productId.equals(prdTypeEntity.getProductTypeID())) {
                    productTypeEntity = prdTypeEntity;
                    break;
                }
            }

            hibernateTransactionHelper.startTransaction();

            AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState.fromShort(stateId));
            AccountCheckListBO accountCheckList = (AccountCheckListBO) new CheckListPersistence().getCheckList(checklistId);
            accountCheckList.update(productTypeEntity, accountStateEntity, checklistName,
                    checklistStatus,
                    checklistDetails, userContext.getLocaleId(),
                    userContext.getId());

            customerDao.save(accountCheckList);
            hibernateTransactionHelper.commitTransaction();
        } catch (ServiceException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (CheckListException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (PersistenceException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

}