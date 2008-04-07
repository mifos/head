package org.mifos.application.reports.persistence;

import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_BRANCHES_AS_SELECTION_ITEM;
import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM;
import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_LOAN_OFFICERS_UNDER_OFFICE_AS_SELECTION_ITEM;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SelectionItemPersistence extends Persistence {
	public List<SelectionItem> getActiveBranchesUnderUser(String officeSearchId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("officeLevelId", OfficeLevel.BRANCHOFFICE
				.getValue());
		queryParameters.put("officeSearchId", officeSearchId + "%");
		queryParameters.put("ACTIVE", OfficeStatus.ACTIVE.getValue());
		return executeNamedQuery(GET_ACTIVE_BRANCHES_AS_SELECTION_ITEM,
				queryParameters);
	}

	public List<SelectionItem> getActiveLoanOfficersUnderOffice(Short officeId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("officeId", officeId);
		queryParameters.put("personnelLevelId", PersonnelLevel.LOAN_OFFICER
				.getValue());
		queryParameters.put("ACTIVE", PersonnelStatus.ACTIVE.getValue());
		return executeNamedQuery(
				GET_ACTIVE_LOAN_OFFICERS_UNDER_OFFICE_AS_SELECTION_ITEM,
				queryParameters);
	}

	public List<SelectionItem> getActiveCentersUnderUser(Short branchId,
			Short loanOfficerId) throws PersistenceException {
		HashMap<String, Object> queryParameters = populateCustomerQueryParams(
				branchId, loanOfficerId, CustomerLevel.CENTER.getValue(),
				CustomerStatus.CENTER_ACTIVE.getValue());
		return executeNamedQuery(
				GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM,
				queryParameters);
	}

	private HashMap<String, Object> populateCustomerQueryParams(Short branchId,
			Short loanOfficerId, Short customerLevel, Short customerStatus) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("personnelId", loanOfficerId);
		queryParameters.put("officeId", branchId);
		queryParameters.put("customerLevelId", customerLevel);
		queryParameters.put("ACTIVE", customerStatus);
		return queryParameters;
	}

	public List<SelectionItem> getActiveGroupsUnderUser(Short officeId,
			Short personnelId) throws PersistenceException {
		HashMap<String, Object> queryParameters = populateCustomerQueryParams(
				officeId, personnelId, CustomerLevel.GROUP.getValue(),
				CustomerStatus.GROUP_ACTIVE.getValue());
		return executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM,
				queryParameters);
	}
	
	public List<DateSelectionItem> getMeetingDates(Short branchId,
			Short loanOfficerId, Integer customerId, Date from)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("COLL_SHEET_RUN_STATUS", CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);
		queryParameters.put("customerId", customerId);
		queryParameters.put("CUSTOMER_LEVEL", CustomerLevel.CENTER.getValue());
		queryParameters.put("branchId", branchId);
		queryParameters.put("loanOfficerId", loanOfficerId);
		queryParameters.put("fromDate", from);
		return executeNamedQuery(
				NamedQueryConstants.GET_MEETING_DATES_AS_SELECTION_ITEM,
				queryParameters);
	}
}
