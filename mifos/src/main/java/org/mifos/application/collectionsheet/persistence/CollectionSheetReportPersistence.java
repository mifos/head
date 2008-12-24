package org.mifos.application.collectionsheet.persistence;

import static org.mifos.application.NamedQueryConstants.COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.BRANCH_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.CENTER_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.MEETING_DATE;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.PERSONNEL_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.reports.business.dto.CollectionSheetReportData;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class CollectionSheetReportPersistence extends Persistence {

	private static final String ALL_VALUE = "ALL";

	public List<CollectionSheetReportData> extractReportData(Integer branchId,
			Date meetingDate, Integer personnelId, Integer centerId)
			throws PersistenceException {
		Map<String, Object> params = populateCommonParams(branchId, meetingDate);
		params.put(PERSONNEL_ID, personnelId);
		params.put(CENTER_ID, centerId);
		return runQueryAndConvertResult(params);
	}

	public List<CollectionSheetReportData> extractReportDataAllLoanOfficersOneCenter(
			Integer branchId, java.util.Date meetingDate, Integer centerId)
			throws PersistenceException {
		Map<String, Object> params = populateCommonParams(branchId, meetingDate);
		setAllPersonnelParameter(params);
		params.put(CENTER_ID, centerId);
		return runQueryAndConvertResult(params);
	}

	public List<CollectionSheetReportData> extractReportDataAllLoanOfficersAllCenters(
			Integer branchId, java.util.Date meetingDate)
			throws PersistenceException {
		Map<String, Object> params = populateCommonParams(branchId, meetingDate);
		setAllPersonnelParameter(params);
		setAllCentersParameter(params);
		return runQueryAndConvertResult(params);
	}

	public List<CollectionSheetReportData> extractReportDataAllCentersUnderLoanOfficer(
			Integer branchId, java.util.Date meetingDate, Integer loanOfficer)
			throws PersistenceException {
		Map<String, Object> params = populateCommonParams(branchId, meetingDate);
		params.put(PERSONNEL_ID, loanOfficer);
		setAllCentersParameter(params);
		return runQueryAndConvertResult(params);
	}

	List<CollectionSheetReportData> convertResultToDTO(List<Object[]> results) {
		List<CollectionSheetReportData> result = new ArrayList<CollectionSheetReportData>();
		for (Object[] objects : results) {
			result.add(new CollectionSheetReportData(objects));
		}
		return result;
	}

	private Object setAllCentersParameter(Map<String, Object> params) {
		return params.put(CENTER_ID, ALL_VALUE);
	}

	private Object setAllPersonnelParameter(Map<String, Object> params) {
		return params
				.put(PERSONNEL_ID, ALL_VALUE);
	}

	private List<CollectionSheetReportData> runQueryAndConvertResult(
			Map<String, Object> params) throws PersistenceException {
		List<Object[]> results = executeNamedQuery(
				COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA, params);
		return convertResultToDTO(results);
	}

	private Map<String, Object> populateCommonParams(Integer branchId, Date meetingDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(BRANCH_ID, branchId);
		params.put(MEETING_DATE, meetingDate);
		return params;
	}

}
