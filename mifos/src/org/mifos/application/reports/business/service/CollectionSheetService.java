package org.mifos.application.reports.business.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.PersistenceException;

public class CollectionSheetService {
	public List<CollSheetCustBO> getCollectionSheetForCustomerOnMeetingDate(
			Date meetingDate, Integer centerId, Short loanOfficerId,
			CustomerLevel customerLevel) throws PersistenceException {
		Map parameters = populateQueryParams(meetingDate, centerId,
				customerLevel);
		parameters.put("LOAN_OFFICER_ID", loanOfficerId);
		return retrieveCollectionSheetCustomers(
				NamedQueryConstants.COLLECTION_SHEET_CUSTOMERS_ON_MEETING_DATE_FOR_LOAN_OFFICER,
				parameters);
	}

	public List<CollSheetCustBO> getCollectionSheetForCustomers(
			Date meetingDate, CollSheetCustBO parent, Short loanOfficerId)
			throws PersistenceException {
		return retrieveCollectionSheetOfChildrensOnMeetingDate(meetingDate,
				parent, CustomerLevel.CLIENT, loanOfficerId);
	}

	public List<CollSheetCustBO> getCollectionSheetForGroups(Date meetingDate,
			CollSheetCustBO parent, Short loanOfficerId)
			throws PersistenceException {
		return retrieveCollectionSheetOfChildrensOnMeetingDate(meetingDate,
				parent, CustomerLevel.GROUP, loanOfficerId);
	}

	private List<CollSheetCustBO> retrieveCollectionSheetOfChildrensOnMeetingDate(
			Date meetingDate, CollSheetCustBO parent,
			CustomerLevel childrenLevel, Short loanOfficerId)
			throws PersistenceException {
		Map parameters = new HashMap();
		parameters.put("CUSTOMER_LEVEL", childrenLevel.getValue());
		parameters.put("MEETING_DATE", meetingDate);
		parameters.put("PARENT_CUSTOMER_ID", parent.getCustId());
		parameters.put("LOAN_OFFICER_ID", loanOfficerId);
		List<CollSheetCustBO> collectionSheetOfChildrens = retrieveCollectionSheetCustomers(
				NamedQueryConstants.COLLECTION_SHEETS_OF_CHILDREN_ON_MEETING_DATE,
				parameters);
		return collectionSheetOfChildrens;
	}

	private Map populateQueryParams(Date meetingDate, Integer centerId,
			CustomerLevel customerLevel) {
		Map parameters = new HashMap();
		parameters.put("CUSTOMER_LEVEL", customerLevel.getValue());
		parameters.put("MEETING_DATE", meetingDate);
		parameters.put("CUSTOMER_ID", centerId);
		return parameters;
	}

	private List<CollSheetCustBO> retrieveCollectionSheetCustomers(
			String queryName, Map parameters) throws PersistenceException {
		List<CollSheetCustBO> centersCollectionSheet = new CollectionSheetPersistence()
				.executeNamedQuery(queryName, parameters);
		return centersCollectionSheet;
	}
}
