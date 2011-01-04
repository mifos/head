package org.mifos.application.servicefacade;

import org.mifos.customers.checklist.business.AccountCheckListBO;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.admin.servicefacade.CheckListServiceFacade;
import org.mifos.application.master.MessageLookup;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.business.service.CheckListBusinessService;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.dto.domain.CheckListMasterDto;
import org.mifos.dto.screen.AccountCheckBoxItemDto;
import org.mifos.dto.screen.CheckListStatesView;
import org.mifos.dto.screen.CustomerCheckBoxItemDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

public class CheckListServiceFacadeWebTier implements CheckListServiceFacade {

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
                dto.setName(MessageLookup.getInstance().lookup(dto.getLookUpName()));
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
                    dto.setName(MessageLookup.getInstance().lookup(dto.getLookUpName()));
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
                    checkListMasterDto.setMasterTypeName(MessageLookup.getInstance().lookupLabel(
                            checkListMasterDto.getLookupKey()));
                } else {
                    checkListMasterDto.setMasterTypeName(MessageLookup.getInstance().lookup(
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
}
