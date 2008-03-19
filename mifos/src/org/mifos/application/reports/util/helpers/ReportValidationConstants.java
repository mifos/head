package org.mifos.application.reports.util.helpers;


public class ReportValidationConstants {

	public static final String LOAN_OFFICER_ID_INVALID_MSG = "loanOfficerId.invalid";
	public static final String CENTER_ID_INVALID_MSG = "centerId.invalid";
	public static final String MEETING_DATE_INVALID_MSG = "meetingDate.invalid";
	public static final String BRANCH_ID_INVALID_MSG = "branchId.invalid";
	public static final String LOAN_PRODUCT_ID_INVALID_MSG = "loanProductId.invalid";
	public static final String RUN_DATE_INVALID_MSG = "runDate.invalid";
	public static final String BRANCH_REPORT_NO_DATA_FOUND_MSG = "runDate.notFound";

	public static final String BRANCH_ID_PARAM = "branchId";
	public static final String LOAN_OFFICER_ID_PARAM = "loanOfficerId";
	public static final String CENTER_ID_PARAM = "centerId";
	public static final String MEETING_DATE_PARAM = "meetingDate";
	public static final String LOAN_PRODUCT_ID_PARAM = "loanProductId";
	public static final String RUN_DATE_PARAM = "branchReportDate";

	public static final String[] COLLECTION_SHEET_REPORT_PARAMS_ARRAY = {
			BRANCH_ID_PARAM, LOAN_OFFICER_ID_PARAM, CENTER_ID_PARAM,
			MEETING_DATE_PARAM };
	
	public static final String[] DETAILED_AGING_REPORT_PARAMS_ARRAY = {
		BRANCH_ID_PARAM, LOAN_OFFICER_ID_PARAM, LOAN_PRODUCT_ID_PARAM};
	
	public static final String[] BRANCH_REPORT_PARAMS_ARRAY = {
			BRANCH_ID_PARAM, RUN_DATE_PARAM };
	

}
