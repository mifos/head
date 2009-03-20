/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application;

/**
 * This class holds the names of named queries which are used for master data 
 * retrieval.
 */
public interface NamedQueryConstants {
	// for fee (create fee)
	public String MASTERDATA_CATAGORY = "masterdata.category";
	public String MASTERDATA_FEEPAYMENT = "masterdata.feepayment";
	public String MASTERDATA_FEEFORMULA = "masterdata.feeformula";

	public String MASTERDATA_LOANOFFICERS = "masterdata.loanofficers";
	public String MASTERDATA_CUSTOMERCUSTOMFIELDDEFINITION = "masterdata.customercustomfielddefinition";
	public String MASTERDATA_CUSTOMFIELDDEFINITION = "masterdata.customfielddefinition";
	public String MASTERDATA_FEES = "masterdata.fees";
	public String MASTERDATA_PROGRAM = "masterdata.programs";
	public String MASTERDATA_POSITIONS = "masterdata.position";
	public String MASTERDATA_CHECKLIST = "masterdata.checklist";
	public String MASTERDATA_FEES_WITHOUT_LEVEL = "masterdata.feeswithoutlevel";
	public String MASTERDATA_FEES_WITH_LEVEL = "masterdata.feelevel";
	public String MASTERDATA_GL_CODES = "masterdata.GlCodes";
	//fund related
	public String DOES_FUND_NAME_EXIST = "fund.doesFundNameExist";
	public String CHECK_FUND_NAME_EXIST = "fund.checkFundNameExist";
	public String GET_FUND_CODES = "fund.getFundCodes";
	public String GET_FUND_FOR_GIVEN_NAME = "fund.getFundForGivenName";
	public String GET_OFFICE_COUNT = "office.getOfficeCountForLevel";
	// for group status 
	public String MASTERDATA_STATUS = "masterdata.status";
	public String MASTERDATA_SPECIFIC_STATUS = "masterdata.specificstatus";
	public String MASTERDATA_STATUS_FLAG = "masterdata.statusflag";
	public String MASTERDATA_SPECIFIC_STATUS_FLAG = "masterdata.specificstatusflag";
	public String MASTERDATA_IS_BLACKLISTED = "masterdata.isBlacklisted";
	public String MASTERDATA_GET_FLAGNAME = "masterdata.getFlagName";
	public String MASTERDATA_FORMEDBY_LOANOFFICERS = "masterdata.formedByLoanOfficers";
	public String IS_GROUP_NAME_EXIST = "isGroupNameExist";
	public String GET_CUSTOMER_MOVEMENT = "getCustomerMovement";
	public String GET_CUSTOMER_HIERARCHY = "getCustomerHierarcy";
	public String COUNT_ACTIVE_CUSTOMERS_FOR_PARENT = "countActiveCustomersForParent";
	public String FIND_CUSTOMER_BY_SYSTEM_ID = "findCustomerBySystemId";
	public String GET_CUSTOMER_MASTER_BY_SEARCH_ID = "getCustomerMasterBySearchId";
	public String GET_ALLSAVINGS_PRODUCTS = "productOffering.getAllSavingsProducts";

	public String GET_ALL_ACTIVE_SAVINGS_PRODUCTS = "productOffering.getAllActiveSavingsProducts";

	public String IS_CENTER_ACTIVE = "isCenterActive";
	//for group transfer
	public String MASTERDATA_CENTERS_FOR_BRANCH = "masterdata.centerForBranch";
	public String GET_CUSTOMER_MASTER = "getCustomerMaster";
	//for notes
	public String GET_NOTES = "notes.getNotes";
	public String GET_PERSONNEL_NOTES = "notes.getPersonnelNotes";
	public String GET_ACCOUNT_NOTES = "notes.getAccountNotes";
	public String GETALLACCOUNTNOTES = "notes.getAllAccountNotes";
	//for historical data
	public String GET_HISTORICAL_DATA = "getHistoricalData";

	//for configuration
	public String GET_PARENT_LEVEL = "getParentLevel";

	//for roles and permission
	public String GETROLES = "getRoles";
	public String GETROLE = "getRole";
	public String GETACTIVITIES = "getActivities";
	public String GETROLEWITHID = "getRoleWithId";
	public String GETLOOKUPVALUELOCALE = "lookUpValueLocaleEntity.getByLocaleAndLookUpId";
	public String GETACTIVITYENTITY = "activityEntity.getActivityEntityByLookUpValueEntity";
	//for security
	public String GETACTIVITYROLES = "getActivityRoles";
	public String GETPERSONROLES = "getPersonRoles";
	public String GETPERSON = "getPerson";
	public String GETOFFICESEARCH = "getOfficeSearch";
	public String GETOFFICESEARCHLIST = "getOfficeSearchList";

	//for office
	public String GETNUMBEROFACTIVECHILDERN = "getNoOfActiveChildren";
	public String GETNOOFCHILDREN = "getNoOfChildren";
	public String CHECKFORNAMEANDSHORTNAMEUNIQUENESS = "checkForNameAndShortNameUniqueNess";
	public String GETACTIVEPERSONNEL = "getActivePersonnel";
	public String GETACTIVEOFFICEWITHLEVEL = "getActiveOfficeWithLevel";
	public String CHECKNAMEUNIQUENESS = "checkForNameUniqueNess";
	public String CHECKSHORTNAMEUNIQUENESS = "checkForShortNameUniqueNess";
	public String GETOLDOFFICEHIERARCHY = "getOldHierarchy";
	public String GETALLLEVEL = "getLevels";
	public String GETHEADOFFICE = "getHeadOffice";
	public String GETLEVEL = "getlevel";

	public String GETLEVELCHILDREN = "getLevelchildren";
	public String ISINVALIDVALIDOFFICEPRESENT = "isInvalidValidOfficePresent";
	public String ISLEVELCONFIGURED = "isLevelConfigured";
	public String ISOFFICEACTIVE = "isOfficeActive";

	public final String OFFICELEVELMASTER = "getofficelevel";
	public final String OFFICECODEMASTER = "getofficelcode";
	public final String OFFICEPARENTMASTER = "getParentOffice";
	public final String OFFICESTATUSMASTER = "getofficestatus";

	public final String GETPARENTOFFICEACTIVE = "getParentOfficeActive";


	public final String GETOFFICESUBOBJECT = "getOfficeSubObject";
	public final String GETALLOFFICELEVELS = "getallofficelevel";
	public final String GETLEVELCHILDERNACTIVE = "getLevelchildrenActive";
	public String MASTERDATA_BRANCH_PARENTS = "masterdata.branchParentOffice";
	public String MASTERDATA_BRANCH_OFFICES = "masterdata.branchOffice";

	public String MASTERDATA_BRANCH_PARENTS_ACTIVE = "masterdata.branchParentOfficeActive";
	public String MASTERDATA_BRANCH_OFFICES_ACTIVE = "masterdata.branchOfficeActive";

	public String GETMAXID = "getMaxId";
	/**Center Named queries**/
	public String DOES_CENTER_NAME_EXIST = "doesCenterNameExist";
	public String GET_CENTER_COUNT = "getCenterCount";
	public String GET_CUSTOMER_COUNT_INOFFICE = "getCustomerCountForOffice";
	public String GET_OFFICE_SEARCHID = "getOfficeSearchId";


	//end for office
	//product type
	public String PRDTYPE = "product.prdtype";

	//Product Category
	public String PRODUCTCATEGORIES_COUNT_CREATE = "product.createduplcount";
	public String PRODUCTCATEGORIES_COUNT_UPDATE = "product.updateduplcount";
	public String PRODUCTCATEGORIES_SEARCH = "product.searchcategories";
	public String PRODUCTCATEGORIES_MAX = "product.maxprdcatid";
	public String PRODUCTCATEGORIESALL = "product.getallcategories";
	//Product Configuration
	public String PRDCONFIGURATION_SEARCH = "product.searchprdconf";

	//Product Offering
	public String PRD_CREATE_NAME_COUNT = "product.createduplnamecount";
	public String PRD_UPDATE_NAME_COUNT = "product.updateduplnamecount";
	public String PRD_CREATE_SHORTNAME_COUNT = "product.createduplshortnamecount";
	public String PRD_UPDATE_SHORTNAME_COUNT = "product.updateduplshortnamecount";
	public String LOAD_PRODUCTS_OFFERING_MIX = "product.retrieveProductOfferingMix";

	public String PRD_BYTYPE = "product.bytype";
	public String ALLOWED_PRD_OFFERING_BYTYPE = "product.allowedproductofferingbytype";
	public String NOT_ALLOWED_PRD_OFFERING_BYTYPE = "product.notallowedproductofferingbytype";
	public String NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.notallowedproductformixproduct";
	public String ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.allowedproductformixproduct";


	public String PRD_BYID = "product.byid";
	public String PRD_MIX_BYID = "product.prdmixbyid";


	//Loan Products
	public String PRDLOAN_CATEGORIES = "product.getloancategories";
	public String PRDAPPLFORLOAN = "product.getprdapplforloan";
	public String GRACEPERIODTYPE = "product.getgraceperiodtype";
	public String YESNOMASTER = "product.getyesnomaster";
	public String INTERESTTYPES = "product.getinteresttypes";
	public String INTERESTCACLRULES = "product.getinterestcaclrules";
	public String PENALTYTYPES = "product.getpenaltytypes";

	public String LOANPRODUCT_SEARCH = "product.searchloanproducts";
	public String MAXLOANPRODUCTID = "product.maxloanprdid";
	//Savings Product
	public String SAVINGSTYPES = "product.getsavingstypes";
	public String INTCALCTYPESTYPES = "product.getintcalctypes";
	public String RECAMNTUNITS = "product.getrecamtunits";
	public String SAVINGSPRODUCT_SEARCH = "product.searchsavingsproducts";
	public String MAXSAVINGSPRODUCTID = "product.maxsavingsprdid";
	public String PRDSTATUS = "product.prdstatus";
	public String PRDCATEGORYSTATUS = "product.prdcategorystatus";
	public String PRDAPPLFOR = "product.getprdapplfor";
	public String PRDSRCFUNDS = "product.srcfund";
	public String PRDLOANFEES = "product.loanfees";
	public String SAVINGSPRDRECURRENCETYPES = "product.savingsrecurrencetypes";

	// for Accounts	
	public String APPLICABLE_SAVINGS_PRODUCT_OFFERINGS = "accounts.applicablesavingsproductofferings";
	public String APPLICABLEPRODUCTOFFERINGS = "accounts.applicableproductofferings";
	public String MAXACCOUNTID = "accounts.maxaccountid";
	public String ACCOUNTSTATESINUSE = "accounts.getMandatoryStates";
	public String ALLACCOUNTSTATES = "accounts.getAllAccountStates";
	public String LOANACCOUNTFEES = "accounts.loanFees";
	public String FINDBYGLOBALACCNUM = "accounts.findByGloabalAccNum";
	public String RETRIEVECUSTOMERMASTER = "Customer.RetrieveCustomerMaster";
	public String GETCUSTOMERMEETING = "customer.getCustomerMeeting";
	public String RECENTACCACTIVITY = "accountTrxn.recentAccountActivity";
	public String ACCOUNTFLAGFORGIVENACCOUNT = "accounts.getAccountFlag";
	
	// for Loan accounts
	public String GET_COSIGNING_CLIENTS_FOR_GLIM = "loan.getCosigningLoansOfClientsForGlim";

	//for account status
	public String ACCOUNT_FLAGS = "masterdata.accountstatusflag";
	public String ACCOUNT_STATUS = "masterdata.accountstatus";
	public String ACCOUNT_STATE_CHANGE_HISTORY = "accounts.getStateChangeHistory";
	public String ACCOUNT_CHECKLIST = "account.checklist";
	public String RETRIEVEALLACCOUNTSTATES = "accounts.retrieveAllAccountStates";
	public String RETRIEVEALLACTIVEACCOUNTSTATES = "accounts.retrieveAllActiveAccountStates";
	public String STATUSCHECKLIST = "account.statusChecklist";
	public String ACCOUNT_GETALLLOANBYCUSTOMER = "accounts.GetAllLoanByCustomer";


	//for meeting 
	public String GETWEEKDAYS = "getWeekDays";
	public String GETRANK = "getrank";
	public String GETWORKINGWEEKDAYS = "getWorkingWeekDays";

	//for personnel
	public String OFFICE_MASTER_BRANCH_OFFICES = "masterdata.officeMasterForBranchOffices";
	public String OFFICE_MASTER = "masterdata.officeMaster";
	public String GET_SUPPORTED_LOCALES = "masterdata.supportedlocales";
	public String GET_PERSONNEL_BY_SYSTEM_ID = "getPersonnelBySystemId";
	public String GET_MAX_PERSONNEL_ID = "getMaxPersonnelId";
	public String IS_BRANCH_ACTIVE = "isBranchActive";
	public String GET_PERSONNEL_BY_USERNAME = "getPersonnelByUserName";
	public String GET_ACTIVE_CENTER_COUNT = "getActiveCenterCountForLO";
	public String GET_ACTIVE_CUSTOMER_COUNT = "getActiveCustomerCountForLO";
	public String GET_CUSTOMER_COUNT = "getCustomerCountForLO";

	public String GET_PERSONNEL_BY_GOVTID = "getPersonnelByGovtId";
	public String GET_PERSONNEL_BY_NAME_AND_DOB = "getPersonnelByNameAndDOB";
	//for personnel Status
	public String MASTERDATA_PERSONNEL_STATUS = "masterdata.personnelStatus";

	//for Customer Search
	public String CUSTOMERGETCUSTOMERS = "Customer.getloanofficercustomers";
	public String GETLOANOFFICERCENTERS = "Customer.getloanofficercenters";
	public String LOANOFFICERGROUPS = "Customer.getloanofficergroups";
	public String CUSTOMERGETLOANOFFICERS = "Customer.getloanofficers";
	public String CUSTOMERGETACTIVELOANOFFICERS = "Customer.getactiveloanofficers";
	public String GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveClientsCountUnderOffice";
	public String GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveOrHoldClientsCountUnderOffice";	
	public String GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorActiveOrHoldClientsCountUnderOffice";	
	public String GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorClientsCountUnderOffice";
	public String GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getActiveBorrowersCountUnderOffice";
	public String GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorBorrowersCountUnderOffice";
	public String GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getActiveSaversCountUnderOffice";
	public String GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorActiveSaversCountUnderOffice";
	public String GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE = "Customers.getReplacementsCountUnderOffice";
	public String GET_VERY_POOR_CLIENTS_UNDER_OFFICE = "Customers.getVeryPoorClientsUnderOffice";
	public String GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountByLoanAccountForOffice";
	public String GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountBySavingAccountForOffice";
	public String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountByLoanAccountForOffice";
	public String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountBySavingAccountForOffice";
	public String GET_VERY_POOR_CLIENTS_TO_CONSIDER_FOR_DORMANT_COUNT = "Customers.getVeryPoorClientsToConsiderForDormantCount";
	public String GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getDropOutClientsCountUnderOffice";
	public String GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorDropOutClientsCountUnderOffice";
	public String GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getOnHoldClientsCountUnderOffice";
	public String GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorOnHoldClientsCountUnderOffice";
	public String CUSTOMERGETOFFICES = "Customer.getOffices";
	public String CUSTOMERGETACTIVEOFFICES = "Customer.getActiveOffices";
	public String GET_CHILDREN_OTHER_THAN_CLOSED_CANCELLED = "getClientOtherThanClosedOrCancelled";
	public String RETRIEVE_INSTALLMENT_NUM = "Account.GetInstallmentId";
	public String RETRIEVE_INSTALLMENTS = "Account.GetInstallments";

	//Disburse loan
	public String RETRIEVE_DUE_INTALLMENTS = "Account.GetIdForDueInstallments";
	public String RETRIEVE_NEXT_INTALLMENT = "Account.GetIdForNextInstallment";
	public String RETRIEVE_MAX_INTALLMENTID = "Account.GetMaxInstallmentId";
	public String IS_CURRENTDAY_DUEDATE = "Account.isCurrentDayDueDate";

	//Apply Adjustment 
	public String RETRIEVE_MAX_ACTIONID = "accountTrxn.maxActionId";
	public String RETRIEVE_TOTAL_AMOUNT = "accountTrxn.totalAmount";
	public String RETRIEVE_MAX_ACCOUNTPAYMENT = "accountPayment.maxAccountPayment";
	public String RETRIEVE_MAX_ACCPAYMENT = "accountPayment.maxAccPayment";
	public String RETRIEVE_ALL_ACCPAYMENT = "accountPayment.allAccPayment";
	public String RETRIEVE_ACCOUNTTRANSACTRANSACTIONS = "accountTrxn.AccountTrxns";
	public String RETRIEVE_ACCOUNTTRANSAMOUNTTOTAL = "accountTrxn.AccountTrxnsTotalAmount";

	//For client closedacc, changelog,fee details
	public String VIEWALLCLIENTCLOSEDACCOUNTS = "accounts.viewallclosedaccounts";
	public String VIEWALLSAVINGSCLOSEDACCOUNTS = "accounts.viewallsavingsclosedaccounts";
	public String VIEWALLCLOSEDACCOUNTS = "customer.viewallclosedaccounts";
	public String GETCLIENTCHARGES = "Client.GetClientCharges";
	public String GETSUMCLIENTDUECHARGES = "Client.GetSumClientDueCharges";
	public String GETSUMCLIENTOVERDUECHARGES = "Client.GetSumClientOverDueCharges";
	public String GETSUMCLIENTCHANGELOG = "Client.getClientChangeLog";
	public String GETRECURRENCEFEESCHARGES = "Client.GetRecurrenceFeesDetails";
	public String GETUPCOMINGCHARGESDATE = "Client.GetUpcomingChargesDate";
	public String UPDATE_CHILD_PERSONNEL = "updateChildPersonnel";

	public String SEARCH_PERSONNEL = "searchPersonnel";
	public String COUNT_SEARCH_PERSONNEL = "count_searchPersonnel";

	public String SEARCH_CENTERS = "searchCenters";
	public String COUNT_SEARCH_CENTERS = "count_searchCenters";

	public String GROUP_SEARCH_WITH_CENTER = "groupSearchWithCenter";
	public String COUNT_GROUP_SEARCH_WITH_CENTER = "count_groupSearchWithCenter";
	public String GROUP_SEARCH_WITHOUT_CENTER = "group_SearchWithoutCenter";
	public String GROUP_SEARCH_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithoutCenterForAddingGroupMember";
	public String COUNT_GROUP_SEARCH_WITHOUT_CENTER = "count_groupSearchWithoutCenter";

	public String ACCOUNTSEARCH = "accounts.accountSearch";
	public String COUNT_ACCOUNTSEARCH = "accounts.count_accountSearch";
	public String ACCOUNTSEARCH_NOCLIENTS = "accounts.accountSearch_noclients";
	public String COUNT_ACCOUNTSEARCH_NOCLIENTS = "accounts.count_accountSearch_noclients";
	public String LEVEL_ACCOUNTSEARCH = "accounts.levelaccountSearch";
	public String LEVEL_ACCOUNTSEARCH_NOCLIENTS = "accounts.levelaccountSearch_noclients";
	public String LEVEL_COUNT_ACCOUNTSEARCH = "accounts.count_levelaccountSearch";
	public String LEVEL_COUNT_ACCOUNTSEARCH_NOCLIENTS = "accounts.count_levelaccountSearch_noclients";
	public String ACCOUNTIDSEARCH = "accounts.accountIdSearch";
	public String ACCOUNTIDSEARCH_WITHOUTOFFICE = "accounts.accountIdSearch_withoutoffice";

	public String CUSTOMERSEARCH = "Customer.search";
	public String COUNT_CUSTOMERSEARCH = "Customer.count_search";

	public String CUSTOMERSEARCH_NOOFFICEID = "Customer.search_noofficeid";
	public String COUNT_CUSTOMERSEARCH_NOCLIENTS = "Customer.count_search_noclients";
	public String CUSTOMERSEARCH_NOCLIENTS = "Customer.search_noclients";

	public String COUNT_CUSTOMERSEARCH_NOOFFICEID = "Customer.count_search_noofficeid";
	public String CUSTOMER_IDSEARCH = "Customer.idsearch";
	public String CUSTOMER_IDSEARCH_WITHOUTOFFICE = "Customer.idsearch_withoutoffice";
	public String CUSTOMERSEARCH_NOOFFICEID_NOCLIENTS = "Customer.search_noofficeid_noclients";
	public String COUNT_CUSTOMERSEARCH_NOOFFICEID_NOCLIENTS = "Customer.count_search_noofficeid_noclients";
	// for collection sheet

	public String COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.customersWithSpecifiedMeetingDateAsSql";
	public String CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE = "CollectionSheetCustomer.customersWithSpecifiedDisbursalDate";
	public String COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.loansWithSpecifiedMeetingDateAsSql";

	public String COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL= "CollectionSheetCustomer.savingssWithSpecifiedMeetingDateAsSql";

	public String MASTERDATA_ACTIVE_BRANCHES = "masterdata.activeBranches";
	public String GET_FIELD_TYPE = "getFieldType";

	public static final String COLLECTION_SHEET_FOR_SPECIFIED_CUSTOMER = "collectionSheet.getCollectionSheetForCenterWithSpecifiedMeetingDate";
	public static final String COLLECTION_SHEETS_FOR_MEETING_DATE = "collectionSheet.getCollectionSheetForMeetingDate";
	public static final String COLLECTION_SHEET_CUSTOMERS_ON_MEETING_DATE_FOR_LOAN_OFFICER = "CollectionSheetCustomer.collectionSheetForSpecifiedMeetingDateAndLoanOfficer";
	public static final String COLLECTION_SHEET_CUSTOMERS_ON_MEETING_DATE = "CollectionSheetCustomer.collectionSheetForSpecifiedMeetingDate";
	public static final String COLLECTION_SHEETS_OF_CHILDREN_ON_MEETING_DATE = "CollectionSheetCustomer.collectionSheetOfCustomersUnderAParentForSpecifiedMeetingDate";
	public static final String GET_ACTIVE_BRANCHES_AS_SELECTION_ITEM = "SelectionItem.get_active_offices";
	public static final String GET_ACTIVE_LOAN_OFFICERS_UNDER_OFFICE_AS_SELECTION_ITEM = "SelectionItem.get_active_loanofficers_under_office";
	public static final String GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM = "SelectionItem.get_active_customers_under_loanofficers";
	public static final String GET_MEETING_DATES_AS_SELECTION_ITEM = "DateSelectionItem.get_meeting_dates";

	/**M2 queries*/
	/**Personnel Queries*/
	public String MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH = "masterdata.activeloanofficers";
	/*Customer Queries*/
	public String GET_PARENTCUSTOMERS_FOR_LOANOFFICER = "Customer.getParentCustomersForLoanOfficer";
	public String GET_ACTIVE_CHILDREN_FORPARENT = "Customer.getActiveChildrenForParent";
	public String GET_CHILDREN_OTHER_THAN_CLOSED = "Customer.getChildrenOtherThanClosed";
	public String GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED = "Customer.getChildrenOtherThanClosedAndCancelled";
	public String GET_ALL_CHILDREN = "Customer.getAllChildren";
	public String GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL = "Customer.getAllChildrenForCustomerLevel";
	public String GET_ACTIVE_AND_ONHOLD_CHILDREN = "Customer.getActiveAndOnHoldChildren";
	public String GET_CHILDREN_FOR_PARENT = "Customer.getChildrenForParent";
	public String GET_ALL_CUSTOMERS = "Customer.getAllCustomers";
	public String GET_CUSTOMER_COUNT_FOR_OFFICE = "Customer.getCustomerCountForOffice";
	public String GET_SQL_CUSTOMER_COUNT_FOR_OFFICE = "Customer.Sql.getCustomerCountForOffice";
	public String GET_SQL_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getCustomerCountBasedOnStatusForOffice";
	public String GET_SQL_VERY_POOR_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getVeryPoorCustomerCountBasedOnStatusForOffice";
	public String GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getActiveAccountUserCountForOffice";
	public String GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorActiveBorrowersCountForOffice";
	public String GET_SQL_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getReplacementCountForOffice";
	public String GET_SQL_VERY_POOR_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorReplacementCountForOffice";
	public String GET_TOTAL_AMOUNT_FOR_GROUP="Customer.getTotalAmountForGroup";
	public String GET_TOTAL_AMOUNT_FOR_ALL_CLIENTS_OF_GROUP="Customer.getTotalAmountForAllClientsOfGroup";
	public String GET_ALL_BASIC_GROUP_INFO="Customer.getAllBasicGroupInfo";
	
	
	/*Office Queries*/
	public String OFFICE_GET_SEARCHID = "office.getOfficeSearchId";
	public String OFFICE_GET_HEADOFFICE = "office.getHeadOffice";

	/**Account*/
	public String GET_CUSTOMER_STATE_CHECKLIST = "customer.checklist";
	public String BULKENTRYPRODUCTS = "Customer.getLoanProductForCustomer";
	public String GET_LAST_MEETINGDATE_FOR_CUSTOMER = "accounts.getLastMeetingDateforCustomer";

	public String ACCOUNT_GETNEXTINSTALLMENTIDS = "account.GetNextInstallmentIds";
	public String ACCOUNT_GETACTIVEFEES = "Account.getActiveFees";

	public String GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS = "accounts.getApplicableSavingsProductOfferings";
	public String RETRIEVE_SAVINGS_ACCCOUNT = "accounts.retrieveSavingsAccounts";
	public String RETRIEVE_ACCCOUNTS_FOR_INT_CALC = "accounts.retrieveSavingsAccountsIntCalc";
	public String RETRIEVE_ACCCOUNTS_FOR_INT_POST = "accounts.retrieveSavingsAccountsIntPost";
	public String RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER = "accounts.retrieveSavingsAccountsForCustomer";
	public String RETRIEVE_ACCCOUNTS_FOR_CUSTOMER = "accounts.retrieveAccountsForCustomer";
	public String GET_MISSED_DEPOSITS_COUNT = "accounts.countOfMissedDeposits";
	//accounts
	public String GET_MAX_ACCOUNT_ID="accounts.getMaxAccountId";
	public String FIND_ACCOUNT_BY_SYSTEM_ID="accounts.findBySystemId";
	public String FIND_LOAN_ACCOUNT_BY_SYSTEM_ID="accounts.findLoanBySystemId";
	public String FIND_INDIVIDUAL_LOANS="accounts.findIndividualLoans";
	public String RETRIEVE_LAST_TRXN="accounts.retrieveLastTrxn";
	public String RETRIEVE_FIRST_TRXN="accounts.retrieveFirstTrxn";
	
	/*Savings Search Queries*/
	public String CUSTOMERSFORSAVINGSACCOUNT = "accounts.customersForSavingsAccount";
	public String COUNT_CUSTOMERSFORSAVINGSACCOUNT = "accounts.count_customersForSavingsAccount";
	public String CUSTOMERSFORSAVINGSACCOUNT_NOCLIENTS = "accounts.customersForSavingsAccount_noclients";
	public String COUNT_CUSTOMERSFORSAVINGSACCOUNT_NOCLIENTS = "accounts.count_customersForSavingsAccount_noclients";
	public String CUSTOMERSFORSAVINGSACCOUNTNONLO = "accounts.customersForSavingsAccountNonLO";
	public String COUNT_CUSTOMERSFORSAVINGSACCOUNTNONLO = "accounts.count_customersForSavingsAccountNonLO";
	public String CUSTOMERSFORSAVINGSACCOUNTNONLO_NOCLIENTS = "accounts.customersForSavingsAccount_noclientsNonLO";
	public String COUNT_CUSTOMERSFORSAVINGSACCOUNTNONLO_NOCLIENTS = "accounts.count_customersForSavingsAccount_noclientsNonLO";

	/*Configuration Queries*/
	public String GET_DEFAULT_CURRENCY = "getDefaultCurrency";
	public String ACCOUNT_ISCURRENTDATEGREATERTHENFIRSTINSTALLMENTDATE = "Account.isCurrentDateGreaterThenFirstInstallmentDate";
	public String GET_ENTITIES = "entities";
	public String GET_LOOKUPVALUES = "lookupvalues";
	public String SUPPORTED_LOCALE_LIST = "supportedlocales";
	public String GET_CURRENCY = "getCurrency";

	//	BulkEntry 
	public String BULKENTRYSAVINGSPRODUCTS = "Customer.getSavingsProductForCustomer";
	public String GET_FEE_AMOUNT_AT_DISBURSEMENT = "accounts.getFeeAmountAtDisbursement";

	/*custom fields*/
	public String RETRIEVE_CUSTOM_FIELDS = "retrieveCustomFields";
	public String RETRIEVE_ALL_CUSTOM_FIELDS = "retrieveAllCustomFields";
	public String RETRIEVE_ONE_CUSTOM_FIELD = "retrieveOneCustomField";

	public String LISTOFSAVINGSANDLOANACCOUNTS = "accounts.listOfAccountsForLoanAccountIdSearch";
	public String GETPOTENTIAL_DISBDATE = "accounts.getPotentialDisbDate";

	public String FETCH_PRODUCT_NAMES_FOR_GROUP = "Customer.fetchProductNamesForGroup";
	public String FETCH_PRODUCT_NAMES_FOR_CLIENT = "Customer.fetchProductNamesForClient";

	//Customer Accounts Quries
	public String CUSTOMER_ACTIVE_LOAN_ACCOUNTS = "Customer.getActiveLoanAccounts";
	public String CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS = "Customer.getActiveSavingsAccounts";
	public String CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID = "customer.findBySystemId";
	public String CUSTOMER_FIND_COUNT_BY_SYSTEM_ID = "customer.findCountBySystemId";
	public String GET_CUSTOMER_STATUS_LIST = "customer.getStatusForCustomer";
	public String GET_CENTER_BY_SYSTEMID = "customer.findCenterSystemId";
	public String GET_GROUP_BY_SYSTEMID = "customer.findGroupSystemId";
	public String GET_CLIENT_BY_SYSTEMID = "customer.findClientSystemId";
	public String GETACTIVELOANOFFICER = "customer.findActiveLoanOfficef";


	//number of meetings attended and missed
	public String NUMBEROFMEETINGSATTENDED = "numberOfMeetingsAttended";
	public String NUMBEROFMEETINGSMISSED = "numberOfMeetingsMissed";
	public String GETLASTLOANAMOUNT = "accounts.getLastLoanAmount";


	// configuration
	public String GET_MFI_LOCALE = "getMFILocale";
	public final String GET_CONFIGURATION_KEYVALUE_BY_KEY = "getConfigurationKeyValueByKey";
	public final String GET_ALL_CONFIGURATION_VALUES = "getAllConfigurationValues";


	//Fething fees with formula
	public String GET_FEES_WITH_FORMULA_FOR_LOAN = "getFeesWithFormmulaForLoan";

	//Fetch approved state entity for loan account
	public String GET_APPROVED_ACCOUNT_STATE = "accounts.getApprovedAccountState";

	public String GET_LASTPAIDINSTALLMNENT_ON_CURRENTDATE = "account.GetLastPaidInstallmentOnCurrentDate";

	//To get customer account
	public String CUSTOMER_ACCOUNT_ACTIONS_DATE = "accounts.getCustomerAccountActionDates";

	public String GET_LOAN_ACOUNTS_IN_ARREARS = "accounts.GetLoanArrears";
	public String GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING = "accounts.GetLoanArrearsInGoodStanding";
	public String GET_LATENESS_FOR_LOANS = "productdefenition.GetLatenessDaysForLoans";
	public String GET_DORMANCY_DAYS = "productdefenition.getDormancyDays";
	public String GET_ACCOUNT_STATES = "accounts.getStates";
	public String GET_CUSTOMER_STATES = "customer.getStates";
	public String GET_ALL_OFFICES = "office.getAllOffices";
	public final String GET_OFFICES_TILL_BRANCHOFFICE = "office.getOfficesTillBranchOffice";
	public final String GET_BRANCH_OFFICES = "office.getBranchOffices";

	public String GET_MANDATORY_FIELD_List = "getMandatoryFieldList";
	public String GET_FIELD_LIST = "getFieldList";
	public String GET_ENTITY_MASTER = "getEntityMaster";

	public String GET_PAYMENT_TYPES = "getPaymentTypes";
	public String GET_YESTERDAYS_INSTALLMENT_FOR_ACTIVE_CUSTOMERS = "getYesterdaysInstallmentForActiveCustomers";
	public String CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS = "customerScheduleEntity.getScheduleForIds";
	public String GET_UPDATED_CUSTOMER_MEETINGS = "getUpdatedMeetings";
	public String GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE = "accounts.countOfMissedDepositsPaidAfterDueDate";
	public String GET_CENTER_COUNT_BY_NAME = "Customer.getCenterCount";
	public String GET_GROUP_COUNT_BY_NAME = "Customer.getGroupCountByGroupNameAndOffice";

	//fee related m2
	public String GET_UPDATED_FEES_FOR_CUSTOMERS = "getUpdatedFeesForCustomers";
	public String GET_CUSTOMER_ACCOUNTS_FOR_FEE = "getCustomerAccountsForFee";

	// Seems not to be used by anything
	public String GET_FEE_UPDATETYPE = "getFeeUpdateType";

	public String GET_ACTIVE_CUSTOMER__AND_SAVINGS_ACCOUNTS = "getActiveCustomerAndSavingsAccounts";

	public String GET_LASTINSTALLMENT = "getLastInstallment";

	public String ACTIVE_CUSTOMERS_UNDER_PARENT = "Customer.getActiveCustomerUnderParent";
	public String RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE = "retrieveCustomerFeesByCategoryType";
	public String RETRIEVE_CUSTOMER_FEES = "retrieveCustomerFees";
	public String RETRIEVE_PRODUCT_FEES = "retrieveProductFees";
	public String ACTIVE_CUSTOMERS_WITH_SEARCH_ID = "Customer.getActiveCustomerWithSearchId";

	public String ALL_LOAN_SCHEDULE_DETAILS = "account.getAllInstallmentsForAllLoanAcounts";
	public String ALL_SAVINGS_SCHEDULE_DETAILS = "account.getAllInstallmentsForAllSavingsAccounts";
	public String ALL_CUSTOMER_SCHEDULE_DETAILS = "account.getAllInstallmentsForAllCustomerAcounts";
	public String CUSTOMER_SCHEDULE_DETAILS = "account.getAllInstallmentsForCustomerAcount";

	public String ALL_LOAN_FEE_SCHEDULE_DETAILS = "account.getAllAccountFeeForAllInstallmentsForAllLoanAcounts";
	public String ALL_CUSTOMER_FEE_SCHEDULE_DETAILS = "account.getAllAccountFeeForAllInstallmentsForAllCustomerAccounts";
	public String CUSTOMER_FEE_SCHEDULE_DETAILS = "account.getAllAccountFeeForAllInstallmentsForCustomerAccount";

	public String APPLICABLE_LOAN_OFFERINGS = "loanOffering.getApplicableProduts";

	public String GET_ALL_APPLICABLE_CUSTOMER_FEE = "getAllApplicableFeesForCustomer";
	public String GET_ALL_APPLICABLE_LOAN_FEE = "getAllApplicableFeesForLoan";

	public String GETALLCUSTOMERNOTES = "notes.getAllCustomerNotes";


	public String GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION = "getAllApplicableFeesForLoanCreation";
	public String MASTERDATA_MIFOS_ENTITY_VALUE = "masterdata.mifosEntityValue";

	public String FORMEDBY_LOANOFFICERS_LIST = "personnel.formedByLoanOfficers";
	public String GETALLPERSONNELNOTES = "personnel.getAllPersonnelNotes";
	//M2 office
	public String GETMAXOFFICEID="office.getMaxId";
	public String GETCHILDCOUNT="office.getChlidCount";
	public String CHECKOFFICENAMEUNIQUENESS="office.getOfficeWithName";
	public String CHECKOFFICESHORTNAMEUNIQUENESS="office.getOfficeWithShortName";
	public String GET_NON_CLOSED_CLIENT_BASED_ON_NAME_DOB="Customer.getNonClosedClientBasedOnNameAndDateOfBirth";
	public String GET_CLOSED_CLIENT_BASED_ON_NAME_DOB="Customer.getClosedClientBasedOnNameAndDateOfBirth";
	public String GET_ACTIVE_OFFERINGS_FOR_CUSTOMER="product.getActiveOfferingsForCustomer";
	public String GET_NON_CLOSED_CLIENT_BASEDON_GOVTID="Customer.getNonClosedClientBasedOnGovtId";
	public String GET_CLOSED_CLIENT_BASEDON_GOVTID="Customer.getClosedClientBasedOnGovtId";
	public String GETOFFICEACTIVEPERSONNEL="getCountActivePersonnel";
	public String GETCOUNTOFACTIVECHILDERN="getCountOfActiveChildren";
	public String GETACTIVEPARENTS="masterdata.activeParents";
	public String GETACTIVELEVELS="masterdata.activeLevels";
	public String GETOFFICESTATUS="masterdata.officestatus";
	public String GETCHILDERN="getChlidren";
	public String GET_CUSTOMER_PICTURE="Customer.getPicture";
	public String GETOFFICEINACTIVE="getCountInactiveOffice";
	public String GET_PRD_TYPES="productdefenition.getProductTypes";
	public String GET_PRD_TYPES_BY_ID="productmix.getProductTypesByID";	
	public String GET_PRODUCTCATEGORY="productdefenition.getProductCategory";
	public String GET_PRDCATEGORYSTATUS="productdefenition.prdcategorystatus";
	public String GET_OFFICES_TILL_BRANCH="office.getOfficesTillBranchOfficeActive";
	public String GET_BRANCH_PARENTS="office.getBranchParents";
	
	//M2 center
	public String GET_LO_FOR_CUSTOMER = "Customer.getLOForCustomer";
	public String GET_OFFICE_LEVELS = "officeLevel.getOfficeLevels";
	//Change Account Status
	public String GET_ALL_BRANCHES = "office.getAllBranches";
	public String GET_SEARCH_RESULTS = "account.getSearchResults";

	//M2 personnel
	public String GET_PERSONNEL_WITH_NAME = "getCountByName";
	public String GETPERSONNELBYNAME = "getPersonnelByName";
	public String GETPERSONNELBYDISPLAYNAME = "getPersonnelByDisplayName";
	public String GET_PERSONNEL_WITH_GOVERNMENTID = "getCountByGovernmentId";
	public String GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME = "getCountByDobAndDisplayName";
	public String GET_ACTIVE_CUSTOMERS_FOR_LO = "Customer.getActiveCustomersForLO";
	public String GET_ALL_CUSTOMERS_FOR_LO = "Customer.getAllCustomersForLO";
	public String GETOFFICE_CHILDREN = "office.getAllChildOffices";
	public String PERSONNEL_BY_SYSTEM_ID = "getPersonBySystemId";
	public String PRODUCTOFFERING_MAX = "product.maxprdofferingid";
	public String PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT = "product.createofferingnamecount";
	public String PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT = "product.createofferingshortnamecount";

	public String PRDAPPLICABLE_CATEGORIES = "product.createapplicableproductcat";
	public String SAVINGS_APPL_RECURRENCETYPES = "product.savingsapplicablerecurrencetypes";

	public String GET_ROLE_FOR_GIVEN_NAME = "getRoleForGivenName";
	public String GET_ALL_ACTIVITIES = "getAllActivities";
	public String GET_ALL_ROLES = "getAllRoles";
	public String GET_PERSONNEL_ROLE_COUNT = "getPersonnelRoleCount";

	public String PRODUCT_STATUS = "product.status";
	public String ALL_PRD_STATES = "product.getAllPrdStates";
	public String PRODUCT_ALL_LOAN_PRODUCTS = "product.getAllLoanProducts";
	public String PRODUCT_NOTMIXED_LOAN_PRODUCTS = "product.getLoanOfferingsNotMixed";
	public String PRODUCT_ALL_ACTIVE_LOAN_PRODUCTS = "product.getAllActiveLoanProducts";

	public String PRODUCT_NOTMIXED_SAVING_PRODUCTS = "product.getSavingOfferingsNotMixed";

	//m2 search quaries 
	public String CUSTOMER_SEARCH_COUNT_FIRST_AND_LAST_NAME = "Customer.cust_count_search_first_and_last_name";
	public String CUSTOMER_SEARCH_COUNT = "Customer.cust_count_search";

	public String CUSTOMER_SEARCH_FIRST_AND_LAST_NAME = "Customer.cust_search_first_and_last_name";
	public String CUSTOMER_SEARCH = "Customer.cust_search";
	public String CUSTOMER_SEARCH_NOOFFICEID = "Customer.cust_search_noofficeid";
	public String CUSTOMER_SEARCH_COUNT_NOOFFICEID = "Customer.cust_count_search_noofficeid";

	public String CUSTOMER_SEARCH_NOOFFICEID_FIRST_AND_LAST_NAME = "Customer.cust_search_noofficeid_first_and_last_name";
	public String CUSTOMER_SEARCH_COUNT_NOOFFICEID_FIRST_AND_LAST_NAME = "Customer.cust_count_search_noofficeid_first_and_last_name";


	public String CUSTOMER_ID_SEARCH_NOOFFICEID = "Customer.cust_idsearch_withoutoffice";
	public String CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_idsearch_withoutoffice_count";

	public String CUSTOMER_ID_SEARCH = "Customer.cust_idsearch";
	public String CUSTOMER_ID_SEARCH_COUNT = "Customer.cust_idsearch_count";

	public String CUSTOMER_ID_SEARCH_NONLO = "Customer.cust_idsearch_nonLo";
	public String CUSTOMER_ID_SEARCH_COUNT_NONLO = "Customer.cust_idsearch_count_nonLo";

	public String ACCOUNT_ID_SEARCH = "accounts.account_IdSearch";
	public String ACCOUNT_ID_SEARCH_NOOFFICEID = "accounts.account_IdSearch_withoutoffice";
	public String ACCOUNT_ID_SEARCH_COUNT = "accounts.account_IdSearch_count";
	public String ACCOUNT_ID_SEARCH_NOOFFICEID_COUNT = "accounts.account_IdSearch_withoutoffice_count";

	public String ACCOUNT_LIST_ID_SEARCH = "accounts.account_list_IdSearch";
	public String PERSONNEL_SEARCH_COUNT = "count_search_Personnel";
	public String PERSONNEL_SEARCH = "search_Personnel";

	public String PERSONNEL_SEARCH_COUNT_FIRST_NAME_AND_LAST_NAME = "count_search_Personnel_first_and_last_name";
	public String PERSONNEL_SEARCH_FIRST_NAME_AND_LAST_NAME = "search_Personnel_first_and_last_name";

	public String CENTER_SEARCH = "search_Centers";
	public String CENTER_SEARCH_COUNT = "count_search_Centers";
	public String GROUP_SEARCHWITH_CENTER = "group_SearchWithCenter";
	public String GROUP_SEARCHWITH_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithCenterForAddingGroupMember";
	public String GROUP_SEARCH_COUNT_WITH_CENTER = "count_group_SearchWithCenter";
	public String GROUP_SEARCH_COUNT_WITH_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithCenterForAddingGroupMember";
	public String GROUP_SEARCH_COUNT_WITHOUT_CENTER = "count_group_SearchWithoutCenter";
	public String GROUP_SEARCH_COUNT_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithoutCenterForAddingGroupMember";
	public String GROUP_SEARCHWITHOUT_CENTER = "group_SearchWithoutCenter";
	public String SEARCH_GROUP_CLIENT_COUNT_LO = "Customer.count_cust_for_account";
	public String SEARCH_GROUP_CLIENT_LO = "Customer.cust_for_account";
	public String SEARCH_GROUP_CLIENT_COUNT = "Customer.cust_count_account_Search";
	public String SEARCH_GROUP_CLIENT = "Customer.cust_account_Search";
	public String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT = "Customer.count_customersForSavingsAccount";
	public String SEARCH_CUSTOMER_FOR_SAVINGS = "Customer.customersForSavingsAccount";
	public String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO = "Customer.count_customersForSavingsAccountNonLO";
	public String SEARCH_CUSTOMER_FOR_SAVINGS_NOLO = "Customer.customersForSavingsAccountNonLO";
	public String SEARCH_CENTERS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_centers";
	public String SEARCH_GROUPS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_groups";
	public String GET_ACTIVE_LOAN_OFFICER_UNDER_USER = "get_active_loanofficers_under_office";
	public String GET_ACTIVE_BRANCHES = "get_active_offices";

	public String RETRIEVE_AUDIT_LOG_RECORD = "retrieveAuditLogRecords";

	//	check List M2
	public String MASTERDATA_CUSTOMER_CHECKLIST = "masterdata.customer_checklist";
	public String MASTERDATA_PRODUCT_CHECKLIST = "masterdata.product_checklist";
	public String CUSTOMER_STATES_LIST = "customer_states_list";
	public String CUSTOMER_VALIDATESTATE = "customer.validateState";
	public String PRODUCT_VALIDATESTATE = "product.validateState";
	public String LOAD_ALL_CUSTOMER_CHECKLISTS = "checklist.loadAllCustomerCheckLists";
	public String LOAD_ALL_ACCOUNT_CHECKLISTS = "checklist.loadAllAccountCheckLists";
	public String CHECKLIST_GET_VALID_CUSTOMER_STATES = "checklist.getStatusForCustomer";
	public String CHECKLIST_GET_VALID_ACCOUNT_STATES = "checklist.getStatusForAccount";
	public String FETCH_ALL_RECURRENCE_TYPES = "meeting.fetchAllReccurenceTypes";

	public String ACTIVE_CLIENTS_UNDER_PARENT = "Customer.getActiveClientsUnderParent";
	public String ACTIVE_CLIENTS_UNDER_GROUP = "Customer.getActiveClientsUnderGroup";
	public String LAST_LOAN_AMOUNT_CUSTOMER = "account.lastLoanAmountForCustomer";

	//  holiday handling
	public String GET_HOLIDAYS = "holiday.getHolidays";
	public String GET_REPAYMENT_RULE_TYPES = "holiday.getRepaymentRuleLabels";
	public String GET_REPAYMENT_RULE="holiday.getRepaymentRule";
	public String ALL_LOAN_SCHEDULE = "account.getAllLoanSchedules";
	public String ALL_SAVING_SCHEDULE = "account.getAllSavingSchedules";
	public String SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS = "savingsScheduleEntity.getScheduleForIds";
	public String LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS = "loanScheduleEntity.getScheduleForIds";	
	public String GET_HOLIDAYS_BY_FLAG = "holiday.getHoildaysByFlag";

	public String GET_ALL_FIELD_CONFIGURATION_LIST = "getAllFieldConfigurationList";

	//	surveys
	public String SURVEYS_RETRIEVE_ALL = "surveys.retrieveAllSurveys";
	public String RESPONSES_RETRIEVE_ALL = "surveys.retrieveAllResponses";
	public String SURVEYS_RETRIEVE_BY_TYPE = "surveys.retrieveSurveysByType";
	public String SURVEYS_RETRIEVE_BY_TYPE_AND_STATE = "surveys.retrieveSurveysByTypeAndState";
	public String SURVEYS_RETRIEVE_BY_NAME = "surveys.retrieveSurveysByName";
	public String SURVEYS_RETRIEVE_BY_STATUS = "surveys.retrieveSurveysByStatus";
	public String SURVEYS_RETRIEVE_ACTIVE_PPI = "surveys.retrieveActivePPISurvey";
	public String SURVEYS_RETRIEVE_PPI_BY_COUNTRY = "surveys.retrievePPISurveyByCountry";
	public String SURVEYS_RETRIEVE_ALL_PPI = "surveys.retrieveAllPPISurveys";
	public String QUESTIONS_GET_NUM = "questions.getNum";
	public String QUESTIONS_RETRIEVE_ALL = "questions.retrieveAll";
	public String QUESTIONS_RETRIEVE_BY_STATE = "questions.retrieveByState";
	public String QUESTIONS_RETRIEVE_BY_TYPE = "questions.retrieveByAnswerType";
	public String QUESTIONS_RETRIEVE_BY_NAME = "questions.retrieveByName";
	public String SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES = "surveys.retrieveCustomersSurveys";
	public String SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES = "surveys.retrieveAccountsSurveys";
	public String SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER = "surveys.retrieveInstancesByCustomer";
	public String SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT = "surveys.retrieveInstancesByAccount";
	public String SURVEYINSTANCE_RETRIEVE_BY_SURVEY = "surveys.retrieveInstancesBySurvey";
	public String RESPONSES_RETRIEVE_BY_INSTANCE = "surveys.retrieveResponsesByInstance";

	// products mix
	public String LOAD_ALL_DEFINED_PRODUCTS_MIX = "productsmix.retrieveAll";
	public String LOAD_NOT_ALLOWED_PRODUCTS = "productsmix.loadnotallowedproducts";
	public String LOAD_DEFINED_PRODUCTS_MIX_BY_PRDOFFERING_ID = "productsmix.retrieveByProductID";
 
	public String GET_TOP_LEVEL_ACCOUNT = "COABO.getTopLevelAccount";
	//	 accepted payment type
	public String GET_ACCEPTED_PAYMENT_TYPE = "acceptedpaymenttype.getAcceptedPaymentType";
	public String GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION = "acceptedpaymenttype.getAcceptedPaymentTypesForATransaction";

	public String GET_TOP_LEVEL_ACCOUNTS = "COABO.getTopLevelAccounts";
	public String GET_ACCOUNT_ID_FOR_GL_CODE = "COABO.getAccountIdForGLCode";
	public String GET_ALL_COA = "COABO.getAllCoa";

	// Administrative documents

	public String GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllActiveAdministrativeDocument";
	public String GET_MIX_BY_ADMINISTRATIVE_DOCUMENT = "admindocument.getMixByAdministrativeDocument";
	public String GET_ALL_MIXED_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllMixedAdministrativeDocument";

 	// new queries for issue 1601 to allow backfilling of custom fields
 	// on existing database objects like customers, personnel, loans, etc.
 	public String GET_CUSTOMERS_BY_LEVELID="Customer.getCustomersByLevelId";
 	public String GET_ALL_OFFICES_FOR_CUSTOM_FIELD="office.getAllOfficesForCustomField";
 	public String GET_ALL_PERSONNEL="personnel.getAllPersonnel";
 	public String GET_ALL_SAVINGS_ACCOUNTS="accounts.getAllSavingsAccounts";
 	public String GET_ALL_LOAN_ACCOUNTS="accounts.getAllLoanAccounts";

	public String GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportForDateAndBranch";
	public String GET_BRANCH_REPORT_FOR_DATE = "branchReport.getBranchReportForDate";
	public String GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportClientSummaryForDateAndBranch";
	public String GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsAgingForDateAndBranch";
	public String GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsProfileForDateAndBranch";
	public String GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffSummaryForDateAndBranch";
	public String GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffingLevelSummaryForDateAndBranch";
	public String GET_ACTIVE_BRANCH_MANAGER_UNDER_OFFICE = "personnel.getActiveBranchManagerUnderOffice";
	public String GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanDetailsForDateAndBranch";

	// AGING ARREARS FOR BRANCH REPORT
	public String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD = "branchReport.extractBranchReportLoanArrears";

	// STAFF SUMMARY FOR BRANCH REPORT
	public String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS = "branchReport.extractStaffSummaryActiveBorrowersLoans";
	public String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT = "branchReport.extractStaffSummaryCenterAndClientCount";
	public String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_NEW_GROUP_JOINED_COUNT = "branchReport.extractGroupJoinedInCurrentMonth";
	public String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING = "branchReport.extractLoanAmountOutstanding";
	public String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR = "branchReport.extractPortfolioAtRisk";
	public String EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR = "branchReport.extractPortfolioAtRiskForBranch";
	public String EXTRACT_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY = "branchReport.extractStaffingLevelSummaryForBranch";
	public String EXTRACT_BRANCH_REPORT_TOTAL_STAFFING_LEVEL_SUMMARY = "branchReport.extractTotalStaffingLevelSummaryForBranch";
	public String EXTRACT_BRANCH_REPORT_LOAN_DETAILS = "branchReport.extractLoanDetailsForBranch";
	public String EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS = "branchReport.extractLoansInArrearsForBranch";
	public String EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK = "branchReport.extractLoansAndOutstandingAmountsAtRiskForBranch";
	public String EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK = "branchReport.extractLoanArrearsProfileClientsAtRiskForBranch";
	public String EXTRACT_BRANCH_REPORT_TOTAL_CLIENTS_ENROLLED_BY_PERSONNEL = "branchReport.extractTotalClientsFormedByPersonnel";
	public String EXTRACT_BRANCH_REPORT_CLIENTS_ENROLLED_BY_PERSONNEL_THIS_MONTH = "branchReport.extractTotalClientsFormedByPersonnelThisMonth";
	public String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_AMOUNT_FOR_PERSONNEL = "branchReport.extractLoanArrearsAmountForPersonnel";

	// Branch Cash Confirmation Report Queries
	public String EXTRACT_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.extractCenterRecovery";
	public String EXTRACT_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.extractDisbursement";

	public String GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.getCenterRecoveries";
	public String GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES = "branchCashConfirmationReport.getCenterIssues";
	public String GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.getDisbursements";

	public String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE = "branchCashConfirmationReport.getReportForDate";
	public String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH = "branchCashConfirmationReport.getReportForDateAndBranch";
	public String COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA = "collectionSheet.extractCollectionSheetReportData";
	
	public String SCHEDULED_TASK_GET_LATEST_TASK="scheduledTasks.getLatestTask";
	public String SCHEDULED_TASK_GET_SUCCESSFUL_TASK="scheduledTasks.getSuccessfulTask";
}
