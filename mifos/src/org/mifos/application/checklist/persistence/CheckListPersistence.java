package org.mifos.application.checklist.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class CheckListPersistence extends MasterPersistence {

	public CheckListPersistence() {
	}

	public List<CheckListMasterView> getCheckListMasterData(Short localeId)
			throws PersistenceException {
		List<CheckListMasterView> checkListMaster = new ArrayList();
		List<CheckListMasterView> masterData = new ArrayList();

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("localeId", localeId);
		masterData = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_CUSTOMER_CHECKLIST,
				queryParameters);
		for (CheckListMasterView checkListMasterDataView : masterData) {
			checkListMasterDataView.setIsCustomer(true);
		}
		checkListMaster.addAll(masterData);
		masterData = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_PRODUCT_CHECKLIST,
				queryParameters);
		for (CheckListMasterView checkListMasterDataView : masterData) {
			checkListMasterDataView.setIsCustomer(false);
		}
		checkListMaster.addAll(masterData);
		masterData = null;
		return checkListMaster;
	}

	public List<CheckListStatesView> retrieveAllCustomerStatusList(
			Short levelId, Short localeId) throws PersistenceException {
		List<CheckListStatesView> checkListStatesView = new ArrayList<CheckListStatesView>();
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", levelId);
		List<CustomerStatusEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.CHECKLIST_GET_VALID_CUSTOMER_STATES, queryParameters);
		for (CustomerStatusEntity customerStatus : queryResult) {
			checkListStatesView.add(new CheckListStatesView(customerStatus
					.getId(), customerStatus.getName(localeId), customerStatus
					.getCustomerLevel().getId()));
		}
		return checkListStatesView;
	}

	public List<CheckListStatesView> retrieveAllAccountStateList(
			Short prdTypeId, Short localeId) throws PersistenceException {
		List<CheckListStatesView> checkListStatesView = new ArrayList<CheckListStatesView>();
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("prdTypeId", prdTypeId);
		List<AccountStateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.CHECKLIST_GET_VALID_ACCOUNT_STATES, queryParameters);
		for (AccountStateEntity accountStatus : queryResult) {
			checkListStatesView.add(new CheckListStatesView(accountStatus
					.getId(), accountStatus.getName(localeId), accountStatus
					.getPrdType().getProductTypeID()));
		}
		return checkListStatesView;
	}

	public CheckListBO getCheckList(Short checkListId)
			throws PersistenceException {
		return (CheckListBO) getPersistentObject(CheckListBO.class, checkListId);
	}

	public int isValidCheckListState(Short levelId, Short stateId,
			boolean isCustomer) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("levelId", levelId);
		queryParameters.put("stateId", stateId);
		Integer count;
		if (isCustomer)
			count = (Integer) execUniqueResultNamedQuery(
					NamedQueryConstants.CUSTOMER_VALIDATESTATE, queryParameters);
		else
			count = (Integer) execUniqueResultNamedQuery(
					NamedQueryConstants.PRODUCT_VALIDATESTATE, queryParameters);
		return count;
	}

	public List<CustomerCheckListBO> retreiveAllCustomerCheckLists()
			throws PersistenceException {
		return executeNamedQuery(
				NamedQueryConstants.LOAD_ALL_CUSTOMER_CHECKLISTS, null);
	}

	public List<AccountCheckListBO> retreiveAllAccountCheckLists()
			throws PersistenceException {
		return executeNamedQuery(
				NamedQueryConstants.LOAD_ALL_ACCOUNT_CHECKLISTS, null);
	}
}
