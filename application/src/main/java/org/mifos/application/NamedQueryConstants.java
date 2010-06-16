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

package org.mifos.application;

/**
 * This class holds the names of named queries which are used for master data retrieval.
 */
public interface NamedQueryConstants {

    public static final String GL_CODE_BY_ID = "GLCode.findById";
    // fund related
    public static final String CHECK_FUND_NAME_EXIST = "fund.checkFundNameExist";
    public static final String GET_FUND_CODES = "fund.getFundCodes";
    public static final String GET_FUND_FOR_GIVEN_NAME = "fund.getFundForGivenName";
    public static final String GET_OFFICE_COUNT = "office.getOfficeCountForLevel";
    // for group status
    public static final String GET_ALLSAVINGS_PRODUCTS = "productOffering.getAllSavingsProducts";

    public static final String GET_ALL_ACTIVE_SAVINGS_PRODUCTS = "productOffering.getAllActiveSavingsProducts";

    // for notes
    public static final String GETALLACCOUNTNOTES = "notes.getAllAccountNotes";

    // for roles and permission
    public static final String GETLOOKUPVALUELOCALE = "lookUpValueLocaleEntity.getByLocaleAndLookUpId";
    public static final String GETACTIVITYENTITY = "activityEntity.getActivityEntityByLookUpValueEntity";
    // for security
    public static final String GETACTIVITYROLES = "getActivityRoles";
    public static final String GETPERSONROLES = "getPersonRoles";
    public static final String GETOFFICESEARCH = "getOfficeSearch";
    public static final String GETOFFICESEARCHLIST = "getOfficeSearchList";

    // Product Category
    public static final String PRODUCTCATEGORIES_COUNT_CREATE = "product.createduplcount";
    public static final String PRODUCTCATEGORIES_COUNT_UPDATE = "product.updateduplcount";
    public static final String PRODUCTCATEGORIES_SEARCH = "product.searchcategories";
    public static final String PRODUCTCATEGORIES_MAX = "product.maxprdcatid";

    // Product Offering
    public static final String LOAD_PRODUCTS_OFFERING_MIX = "product.retrieveProductOfferingMix";

    public static final String PRD_BYTYPE = "product.bytype";
    public static final String ALLOWED_PRD_OFFERING_BYTYPE = "product.allowedproductofferingbytype";
    public static final String NOT_ALLOWED_PRD_OFFERING_BYTYPE = "product.notallowedproductofferingbytype";
    public static final String NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.notallowedproductformixproduct";
    public static final String ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.allowedproductformixproduct";

    public static final String PRD_BYID = "product.byid";
    public static final String PRD_MIX_BYID = "product.prdmixbyid";

    // Savings Product

    public static final String PRDSRCFUNDS = "product.srcfund";

    // for Loan accounts
    public static final String GET_COSIGNING_CLIENTS_FOR_GLIM = "loan.getCosigningLoansOfClientsForGlim";
    public static final String RETRIEVE_TOTAL_LOAN_FOR_CUSTOMER = "loan.retrieveTotalLoanForCustomer";

    // for Savings accounts
    public static final String RETRIEVE_TOTAL_SAVINGS_FOR_CUSTOMER = "savings.retrieveTotalSavingsForCustomer";

    // for account status
    public static final String RETRIEVEALLACCOUNTSTATES = "accounts.retrieveAllAccountStates";
    public static final String RETRIEVEALLACTIVEACCOUNTSTATES = "accounts.retrieveAllActiveAccountStates";
    public static final String STATUSCHECKLIST = "account.statusChecklist";
    public static final String ACCOUNT_GETALLLOANBYCUSTOMER = "accounts.GetAllLoanByCustomer";

    // for Customer Search
    public static final String GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveClientsCountUnderOffice";
    public static final String GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveOrHoldClientsCountUnderOffice";
    public static final String GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorActiveOrHoldClientsCountUnderOffice";
    public static final String GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorClientsCountUnderOffice";
    public static final String GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getActiveBorrowersCountUnderOffice";
    public static final String GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorBorrowersCountUnderOffice";
    public static final String GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getActiveSaversCountUnderOffice";
    public static final String GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorActiveSaversCountUnderOffice";
    public static final String GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE = "Customers.getReplacementsCountUnderOffice";
    public static final String GET_VERY_POOR_CLIENTS_UNDER_OFFICE = "Customers.getVeryPoorClientsUnderOffice";
    public static final String GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountByLoanAccountForOffice";
    public static final String GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountBySavingAccountForOffice";
    public static final String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountByLoanAccountForOffice";
    public static final String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountBySavingAccountForOffice";
    public static final String GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getDropOutClientsCountUnderOffice";
    public static final String GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorDropOutClientsCountUnderOffice";
    public static final String GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getOnHoldClientsCountUnderOffice";
    public static final String GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorOnHoldClientsCountUnderOffice";

    // Apply Adjustment
    public static final String RETRIEVE_MAX_ACCPAYMENT = "accountPayment.maxAccPayment";
    public static final String RETRIEVE_ALL_ACCPAYMENT = "accountPayment.allAccPayment";

    // For client closedacc, changelog,fee details
    public static final String VIEWALLSAVINGSCLOSEDACCOUNTS = "accounts.viewallsavingsclosedaccounts";
    public static final String VIEWALLCLOSEDACCOUNTS = "customer.viewallclosedaccounts";

    public static final String GROUP_SEARCH_WITHOUT_CENTER = "group_SearchWithoutCenter";
    public static final String GROUP_SEARCH_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithoutCenterForAddingGroupMember";

    // for collection sheet
    public static final String COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.customersWithSpecifiedMeetingDateAsSql";
    public static final String CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE = "CollectionSheetCustomer.customersWithSpecifiedDisbursalDate";
    public static final String COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.loansWithSpecifiedMeetingDateAsSql";

    public static final String COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.savingssWithSpecifiedMeetingDateAsSql";

    public static final String MASTERDATA_ACTIVE_BRANCHES = "masterdata.activeBranches";

    /** M2 queries */
    /** Personnel Queries */
    public static final String MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH = "masterdata.activeloanofficers";
    /* Customer Queries */
    public static final String GET_PARENTCUSTOMERS_FOR_LOANOFFICER = "Customer.getParentCustomersForLoanOfficer";
    public static final String GET_ACTIVE_CHILDREN_FORPARENT = "Customer.getActiveChildrenForParent";
    public static final String GET_CHILDREN_OTHER_THAN_CLOSED = "Customer.getChildrenOtherThanClosed";
    public static final String GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED = "Customer.getChildrenOtherThanClosedAndCancelled";
    public static final String GET_ALL_CHILDREN = "Customer.getAllChildren";
    public static final String GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL = "Customer.getAllChildrenForCustomerLevel";
    public static final String GET_ACTIVE_AND_ONHOLD_CHILDREN = "Customer.getActiveAndOnHoldChildren";
    public static final String GET_ACTIVE_AND_ONHOLD_CHILDREN_COUNT = "Customer.getActiveAndOnHoldChildrenCount";
    public static final String GET_CUSTOMER_COUNT_FOR_OFFICE = "Customer.getCustomerCountForOffice";
    public static final String GET_SEARCH_IDS_FOR_OFFICE = "Customer.getSearchIdsForOffice";
    public static final String GET_SQL_CUSTOMER_COUNT_FOR_OFFICE = "Customer.Sql.getCustomerCountForOffice";
    public static final String GET_SQL_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getCustomerCountBasedOnStatusForOffice";
    public static final String GET_SQL_VERY_POOR_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getVeryPoorCustomerCountBasedOnStatusForOffice";
    public static final String GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getActiveAccountUserCountForOffice";
    public static final String GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorActiveBorrowersCountForOffice";
    public static final String GET_SQL_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getReplacementCountForOffice";
    public static final String GET_SQL_VERY_POOR_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorReplacementCountForOffice";
    public static final String GET_TOTAL_AMOUNT_FOR_GROUP = "Customer.getTotalAmountForGroup";
    public static final String GET_LOAN_SUMMARY_CURRENCIES_FOR_ALL_CLIENTS_OF_GROUP = "Customer.getLoanSummaryCurrenciesForAllClientsOfGroup";
    public static final String GET_LOAN_SUMMARY_CURRENCIES_FOR_GROUP = "Customer.getLoanSummaryCurrenciesForGroup";
    public static final String GET_TOTAL_AMOUNT_FOR_ALL_CLIENTS_OF_GROUP = "Customer.getTotalAmountForAllClientsOfGroup";
    public static final String GET_ALL_BASIC_GROUP_INFO = "Customer.getAllBasicGroupInfo";

    /* Office Queries */
    public static final String OFFICE_GET_SEARCHID = "office.getOfficeSearchId";
    public static final String OFFICE_GET_HEADOFFICE = "office.getHeadOffice";

    /** Account */
    public static final String GET_CUSTOMER_STATE_CHECKLIST = "customer.checklist";
    public static final String GET_LAST_MEETINGDATE_FOR_CUSTOMER = "accounts.getLastMeetingDateforCustomer";

    public static final String GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS = "accounts.getApplicableSavingsProductOfferings";
    public static final String RETRIEVE_SAVINGS_ACCCOUNT = "accounts.retrieveSavingsAccounts";
    public static final String RETRIEVE_ACCCOUNTS_FOR_INT_CALC = "accounts.retrieveSavingsAccountsIntCalc";
    public static final String RETRIEVE_ACCCOUNTS_FOR_INT_POST = "accounts.retrieveSavingsAccountsIntPost";
    public static final String RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER = "accounts.retrieveSavingsAccountsForCustomer";
    public static final String RETRIEVE_ACCCOUNTS_FOR_CUSTOMER = "accounts.retrieveAccountsForCustomer";
    public static final String GET_MISSED_DEPOSITS_COUNT = "accounts.countOfMissedDeposits";
    // accounts
    public static final String GET_MAX_ACCOUNT_ID = "accounts.getMaxAccountId";
    public static final String FIND_ACCOUNT_BY_SYSTEM_ID = "accounts.findBySystemId";
    public static final String FIND_LOAN_ACCOUNT_BY_SYSTEM_ID = "accounts.findLoanBySystemId";
    public static final String FIND_LOAN_ACCOUNT_BY_EXTERNAL_ID = "accounts.findLoanByExternalId";
    public static final String FIND_LOAN_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findLoanByClientGovernmentIdAndProductShortName";
    public static final String FIND_SAVINGS_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findSavingsByClientGovernmentIdAndProductShortName";
    public static final String FIND_INDIVIDUAL_LOANS = "accounts.findIndividualLoans";
    public static final String RETRIEVE_LAST_TRXN = "accounts.retrieveLastTrxn";
    public static final String RETRIEVE_FIRST_TRXN = "accounts.retrieveFirstTrxn";

    public static final String GET_ENTITIES = "entities";
    public static final String GET_LOOKUPVALUES = "lookupvalues";
    public static final String SUPPORTED_LOCALE_LIST = "supportedlocales";
    public static final String AVAILABLE_LANGUAGES = "availableLanguages";
    public static final String GET_CURRENCY = "getCurrency";

    // BulkEntry
    public static final String GET_FEE_AMOUNT_AT_DISBURSEMENT = "accounts.getFeeAmountAtDisbursement";

    /* custom fields */
    public static final String RETRIEVE_CUSTOM_FIELDS = "retrieveCustomFields";
    public static final String RETRIEVE_ALL_CUSTOM_FIELDS = "retrieveAllCustomFields";
    public static final String RETRIEVE_ONE_CUSTOM_FIELD = "retrieveOneCustomField";

    public static final String FETCH_PRODUCT_NAMES_FOR_GROUP = "Customer.fetchProductNamesForGroup";
    public static final String FETCH_PRODUCT_NAMES_FOR_CLIENT = "Customer.fetchProductNamesForClient";

    // Customer Accounts Quries
    public static final String CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID = "customer.findBySystemId";
    public static final String CUSTOMER_FIND_COUNT_BY_SYSTEM_ID = "customer.findCountBySystemId";
    public static final String CUSTOMER_FIND_COUNT_BY_GOVERNMENT_ID = "customer.findCountByGovernmentId";
    public static final String GET_CUSTOMER_STATUS_LIST = "customer.getStatusForCustomer";
    public static final String GET_CENTER_BY_SYSTEMID = "customer.findCenterSystemId";
    public static final String GET_GROUP_BY_SYSTEMID = "customer.findGroupSystemId";
    public static final String GET_CLIENT_BY_SYSTEMID = "customer.findClientSystemId";

    // number of meetings attended and missed
    public static final String NUMBEROFMEETINGSATTENDED = "numberOfMeetingsAttended";
    public static final String NUMBEROFMEETINGSMISSED = "numberOfMeetingsMissed";

    // configuration
    public static final String GET_CONFIGURATION_KEYVALUE_BY_KEY = "getConfigurationKeyValueByKey";
    public static final String GET_ALL_CONFIGURATION_VALUES = "getAllConfigurationValues";

    // To get customer account
    public static final String CUSTOMER_ACCOUNT_ACTIONS_DATE = "accounts.getCustomerAccountActionDates";

    public static final String GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING = "accounts.GetLoanArrearsInGoodStanding";
    public static final String GET_LATENESS_FOR_LOANS = "productdefenition.GetLatenessDaysForLoans";
    public static final String GET_DORMANCY_DAYS = "productdefenition.getDormancyDays";
    public static final String GET_ACCOUNT_STATES = "accounts.getStates";
    public static final String GET_CUSTOMER_STATES = "customer.getStates";
    public static final String GET_ALL_OFFICES = "office.getAllOffices";
    public static final String GET_OFFICES_TILL_BRANCHOFFICE = "office.getOfficesTillBranchOffice";
    public static final String GET_BRANCH_OFFICES = "office.getBranchOffices";

    public static final String GET_FIELD_LIST = "getFieldList";
    public static final String GET_ENTITY_MASTER = "getEntityMaster";

    public static final String CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS = "customerScheduleEntity.getScheduleForIds";
    public static final String GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE = "accounts.countOfMissedDepositsPaidAfterDueDate";
    public static final String GET_CENTER_COUNT_BY_NAME = "Customer.getCenterCount";
    public static final String GET_GROUP_COUNT_BY_NAME = "Customer.getGroupCountByGroupNameAndOffice";

    // fee related m2
    public static final String GET_CUSTOMER_ACCOUNTS_FOR_FEE = "getCustomerAccountsForFee";

    public static final String RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE = "retrieveCustomerFeesByCategoryType";

    public static final String APPLICABLE_LOAN_PRODUCTS = "loanOffering.getApplicableProducts";

    public static final String GET_ALL_APPLICABLE_CUSTOMER_FEE = "getAllApplicableFeesForCustomer";
    public static final String GET_ALL_APPLICABLE_LOAN_FEE = "getAllApplicableFeesForLoan";

    public static final String GETALLCUSTOMERNOTES = "notes.getAllCustomerNotes";

    public static final String GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION = "getAllApplicableFeesForLoanCreation";
    public static final String MASTERDATA_MIFOS_ENTITY_VALUE = "masterdata.mifosEntityValue";

    public static final String FORMEDBY_LOANOFFICERS_LIST = "personnel.formedByLoanOfficers";
    public static final String GETALLPERSONNELNOTES = "personnel.getAllPersonnelNotes";
    // M2 office
    public static final String GETMAXOFFICEID = "office.getMaxId";
    public static final String GETCHILDCOUNT = "office.getChlidCount";
    public static final String CHECKOFFICENAMEUNIQUENESS = "office.getOfficeWithName";
    public static final String CHECKOFFICESHORTNAMEUNIQUENESS = "office.getOfficeWithShortName";
    public static final String GET_CLOSED_CLIENT_BASED_ON_NAME_DOB = "Customer.getClosedClientBasedOnNameAndDateOfBirth";
    public static final String GET_BLACKLISTED_CLIENT_BASED_ON_NAME_DOB = "Customer.getBlackListedClientBasedOnNameAndDateOfBirth";
    public static final String GET_ACTIVE_OFFERINGS_FOR_CUSTOMER = "product.getActiveOfferingsForCustomer";
    public static final String GET_CLOSED_CLIENT_BASEDON_GOVTID = "Customer.getClosedClientBasedOnGovtId";
    public static final String GETOFFICEACTIVEPERSONNEL = "getCountActivePersonnel";
    public static final String GETCOUNTOFACTIVECHILDERN = "getCountOfActiveChildren";
    public static final String GETACTIVEPARENTS = "masterdata.activeParents";
    public static final String GETACTIVELEVELS = "masterdata.activeLevels";
    public static final String GETOFFICESTATUS = "masterdata.officestatus";
    public static final String GETCHILDERN = "getChlidren";
    public static final String GET_CUSTOMER_PICTURE = "Customer.getPicture";
    public static final String GETOFFICEINACTIVE = "getCountInactiveOffice";
    public static final String GET_PRD_TYPES = "productdefenition.getProductTypes";
    public static final String GET_PRODUCTCATEGORY = "productdefenition.getProductCategory";
    public static final String GET_PRDCATEGORYSTATUS = "productdefenition.prdcategorystatus";
    public static final String GET_OFFICES_TILL_BRANCH = "office.getOfficesTillBranchOfficeActive";
    public static final String GET_BRANCH_PARENTS = "office.getBranchParents";

    // M2 center
    public static final String GET_LO_FOR_CUSTOMER = "Customer.getLOForCustomer";
    public static final String GET_OFFICE_LEVELS = "officeLevel.getOfficeLevels";
    // Change Account Status
    public static final String GET_SEARCH_RESULTS = "account.getSearchResults";

    // M2 personnel
    public static final String GET_PERSONNEL_WITH_NAME = "getCountByName";
    public static final String GETPERSONNELBYNAME = "getPersonnelByName";
    public static final String GETPERSONNELBYDISPLAYNAME = "getPersonnelByDisplayName";
    public static final String GET_PERSONNEL_WITH_GOVERNMENTID = "getCountByGovernmentId";
    public static final String GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME = "getCountByDobAndDisplayName";
    public static final String GET_ACTIVE_CUSTOMERS_FOR_LO = "Customer.getActiveCustomersForLO";
    public static final String GET_ALL_CUSTOMERS_FOR_LO = "Customer.getAllCustomersForLO";
    public static final String GETOFFICE_CHILDREN = "office.getAllChildOffices";
    public static final String PERSONNEL_BY_SYSTEM_ID = "getPersonBySystemId";
    public static final String PRODUCTOFFERING_MAX = "product.maxprdofferingid";
    public static final String PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT = "product.createofferingnamecount";
    public static final String PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT = "product.createofferingshortnamecount";

    public static final String PRDAPPLICABLE_CATEGORIES = "product.createapplicableproductcat";
    public static final String SAVINGS_APPL_RECURRENCETYPES = "product.savingsapplicablerecurrencetypes";

    public static final String GET_ROLE_FOR_GIVEN_NAME = "getRoleForGivenName";
    public static final String GET_ALL_ACTIVITIES = "getAllActivities";
    public static final String GET_ALL_ROLES = "getAllRoles";
    public static final String GET_PERSONNEL_ROLE_COUNT = "getPersonnelRoleCount";

    public static final String PRODUCT_STATUS = "product.status";
    public static final String ALL_PRD_STATES = "product.getAllPrdStates";
    public static final String PRODUCT_ALL_LOAN_PRODUCTS = "product.getAllLoanProducts";
    public static final String PRODUCT_NOTMIXED_LOAN_PRODUCTS = "product.getLoanOfferingsNotMixed";
    public static final String PRODUCT_ALL_ACTIVE_LOAN_PRODUCTS = "product.getAllActiveLoanProducts";

    public static final String PRODUCT_NOTMIXED_SAVING_PRODUCTS = "product.getSavingOfferingsNotMixed";

    // m2 search quaries
    public static final String CUSTOMER_SEARCH_COUNT = "Customer.cust_count_search";

    public static final String CUSTOMER_SEARCH = "Customer.cust_search";
    public static final String CUSTOMER_SEARCH_NOOFFICEID = "Customer.cust_search_noofficeid";
    public static final String CUSTOMER_SEARCH_COUNT_NOOFFICEID = "Customer.cust_count_search_noofficeid";

    public static final String CUSTOMER_ID_SEARCH_NOOFFICEID = "Customer.cust_idsearch_withoutoffice";
    public static final String CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_idsearch_withoutoffice_count";

    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID = "Customer.cust_govidsearch_withoutoffice";
    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_govidsearch_withoutoffice_count";

    public static final String CUSTOMER_ID_SEARCH = "Customer.cust_idsearch";
    public static final String CUSTOMER_ID_SEARCH_COUNT = "Customer.cust_idsearch_count";

    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH = "Customer.cust_govidsearch";
    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT = "Customer.cust_govidsearch_count";

    public static final String CUSTOMER_ID_SEARCH_NONLO = "Customer.cust_idsearch_nonLo";
    public static final String CUSTOMER_ID_SEARCH_COUNT_NONLO = "Customer.cust_idsearch_count_nonLo";

    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH_NONLO = "Customer.cust_govidsearch_nonLo";
    public static final String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT_NONLO = "Customer.cust_govidsearch_count_nonLo";

    public static final String ACCOUNT_ID_SEARCH = "accounts.account_IdSearch";
    public static final String ACCOUNT_ID_SEARCH_NOOFFICEID = "accounts.account_IdSearch_withoutoffice";
    public static final String ACCOUNT_ID_SEARCH_COUNT = "accounts.account_IdSearch_count";
    public static final String ACCOUNT_ID_SEARCH_NOOFFICEID_COUNT = "accounts.account_IdSearch_withoutoffice_count";

    public static final String ACCOUNT_LIST_ID_SEARCH = "accounts.account_list_IdSearch";
    public static final String PERSONNEL_SEARCH_COUNT = "count_search_Personnel";
    public static final String PERSONNEL_SEARCH = "search_Personnel";

    public static final String CENTER_SEARCH = "search_Centers";
    public static final String CENTER_SEARCH_COUNT = "count_search_Centers";
    public static final String GROUP_SEARCHWITH_CENTER = "group_SearchWithCenter";
    public static final String GROUP_SEARCHWITH_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithCenterForAddingGroupMember";
    public static final String GROUP_SEARCH_COUNT_WITH_CENTER = "count_group_SearchWithCenter";
    public static final String GROUP_SEARCH_COUNT_WITH_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithCenterForAddingGroupMember";
    public static final String GROUP_SEARCH_COUNT_WITHOUT_CENTER = "count_group_SearchWithoutCenter";
    public static final String GROUP_SEARCH_COUNT_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithoutCenterForAddingGroupMember";
    public static final String SEARCH_GROUP_CLIENT_COUNT_LO = "Customer.count_cust_for_account";
    public static final String SEARCH_GROUP_CLIENT_LO = "Customer.cust_for_account";
    public static final String SEARCH_GROUP_CLIENT_COUNT = "Customer.cust_count_account_Search";
    public static final String SEARCH_GROUP_CLIENT = "Customer.cust_account_Search";
    public static final String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT = "Customer.count_customersForSavingsAccount";
    public static final String SEARCH_CUSTOMER_FOR_SAVINGS = "Customer.customersForSavingsAccount";
    public static final String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO = "Customer.count_customersForSavingsAccountNonLO";
    public static final String SEARCH_CUSTOMER_FOR_SAVINGS_NOLO = "Customer.customersForSavingsAccountNonLO";
    public static final String SEARCH_CENTERS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_centers";
    public static final String SEARCH_GROUPS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_groups";
    public static final String GET_ACTIVE_LOAN_OFFICER_UNDER_USER = "get_active_loanofficers_under_office";
    public static final String GET_ACTIVE_BRANCHES = "get_active_offices";

    public static final String RETRIEVE_AUDIT_LOG_RECORD = "retrieveAuditLogRecords";

    // check List M2
    public static final String MASTERDATA_CUSTOMER_CHECKLIST = "masterdata.customer_checklist";
    public static final String MASTERDATA_PRODUCT_CHECKLIST = "masterdata.product_checklist";
    public static final String CUSTOMER_VALIDATESTATE = "customer.validateState";
    public static final String PRODUCT_VALIDATESTATE = "product.validateState";
    public static final String LOAD_ALL_CUSTOMER_CHECKLISTS = "checklist.loadAllCustomerCheckLists";
    public static final String LOAD_ALL_ACCOUNT_CHECKLISTS = "checklist.loadAllAccountCheckLists";
    public static final String CHECKLIST_GET_VALID_CUSTOMER_STATES = "checklist.getStatusForCustomer";
    public static final String CHECKLIST_GET_VALID_ACCOUNT_STATES = "checklist.getStatusForAccount";
    public static final String FETCH_ALL_RECURRENCE_TYPES = "meeting.fetchAllReccurenceTypes";

    public static final String ACTIVE_CLIENTS_UNDER_PARENT = "Customer.getActiveClientsUnderParent";
    public static final String ACTIVE_CLIENTS_UNDER_GROUP = "Customer.getActiveClientsUnderGroup";

    // holiday handling
    public static final String GET_OFFICE_HOLIDAYS = "holiday.getOfficeHolidays";
    public static final String GET_ALL_HOLIDAYS = "holiday.getAll";
    public static final String GET_APPLICABLE_OFFICES_FOR_HOLIDAYS = "holiday.applicableOffices";
    public static final String GET_TOP_LEVEL_OFFICE_NAMES = "holiday.topLevelOfficeNames";
    public static final String GET_REPAYMENT_RULE_TYPES = "holiday.getRepaymentRuleLabels";
    public static final String GET_REPAYMENT_RULE = "holiday.getRepaymentRule";
    public static final String SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS = "savingsScheduleEntity.getScheduleForIds";
    public static final String LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS = "loanScheduleEntity.getScheduleForIds";
    public static final String GET_HOLIDAYS_BY_FLAG = "holiday.getHoildaysByFlag";

    public static final String GET_ALL_FIELD_CONFIGURATION_LIST = "getAllFieldConfigurationList";

    // surveys
    public static final String SURVEYS_RETRIEVE_ALL = "surveys.retrieveAllSurveys";
    public static final String RESPONSES_RETRIEVE_ALL = "surveys.retrieveAllResponses";
    public static final String SURVEYS_RETRIEVE_BY_TYPE = "surveys.retrieveSurveysByType";
    public static final String SURVEYS_RETRIEVE_BY_TYPE_AND_STATE = "surveys.retrieveSurveysByTypeAndState";
    public static final String SURVEYS_RETRIEVE_BY_STATUS = "surveys.retrieveSurveysByStatus";
    public static final String SURVEYS_RETRIEVE_ACTIVE_PPI = "surveys.retrieveActivePPISurvey";
    public static final String SURVEYS_RETRIEVE_PPI_BY_COUNTRY = "surveys.retrievePPISurveyByCountry";
    public static final String SURVEYS_RETRIEVE_ALL_PPI = "surveys.retrieveAllPPISurveys";
    public static final String QUESTIONS_GET_NUM = "questions.getNum";
    public static final String QUESTIONS_RETRIEVE_ALL = "questions.retrieveAll";
    public static final String QUESTIONS_RETRIEVE_BY_STATE = "questions.retrieveByState";
    public static final String QUESTIONS_RETRIEVE_BY_TYPE = "questions.retrieveByAnswerType";
    public static final String QUESTIONS_RETRIEVE_BY_NAME = "questions.retrieveByName";
    public static final String SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES = "surveys.retrieveCustomersSurveys";
    public static final String SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES = "surveys.retrieveAccountsSurveys";
    public static final String SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER = "surveys.retrieveInstancesByCustomer";
    public static final String SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT = "surveys.retrieveInstancesByAccount";
    public static final String SURVEYINSTANCE_RETRIEVE_BY_SURVEY = "surveys.retrieveInstancesBySurvey";
    public static final String RESPONSES_RETRIEVE_BY_INSTANCE = "surveys.retrieveResponsesByInstance";

    // products mix
    public static final String LOAD_ALL_DEFINED_PRODUCTS_MIX = "productsmix.retrieveAll";
    public static final String LOAD_NOT_ALLOWED_PRODUCTS = "productsmix.loadnotallowedproducts";

    public static final String GET_TOP_LEVEL_ACCOUNT = "COABO.getTopLevelAccount";
    // accepted payment type
    public static final String GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION = "acceptedpaymenttype.getAcceptedPaymentTypesForATransaction";

    public static final String GET_TOP_LEVEL_ACCOUNTS = "COABO.getTopLevelAccounts";
    public static final String GET_ACCOUNT_ID_FOR_GL_CODE = "COABO.getAccountIdForGLCode";
    public static final String GET_ALL_COA = "COABO.getAllCoa";

    // Administrative documents

    public static final String GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllActiveAdministrativeDocument";
    public static final String GET_MIX_BY_ADMINISTRATIVE_DOCUMENT = "admindocument.getMixByAdministrativeDocument";
    public static final String GET_ALL_MIXED_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllMixedAdministrativeDocument";

    // new queries for issue 1601 to allow backfilling of custom fields
    // on existing database objects like customers, personnel, loans, etc.
    public static final String GET_CUSTOMERS_BY_LEVELID = "Customer.getCustomersByLevelId";
    public static final String GET_ALL_OFFICES_FOR_CUSTOM_FIELD = "office.getAllOfficesForCustomField";
    public static final String GET_ALL_PERSONNEL = "personnel.getAllPersonnel";
    public static final String GET_ALL_SAVINGS_ACCOUNTS = "accounts.getAllSavingsAccounts";
    public static final String GET_ALL_LOAN_ACCOUNTS = "accounts.getAllLoanAccounts";

    public static final String GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportForDateAndBranch";
    public static final String GET_BRANCH_REPORT_FOR_DATE = "branchReport.getBranchReportForDate";
    public static final String GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportClientSummaryForDateAndBranch";
    public static final String GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsAgingForDateAndBranch";
    public static final String GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsProfileForDateAndBranch";
    public static final String GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffSummaryForDateAndBranch";
    public static final String GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffingLevelSummaryForDateAndBranch";
    public static final String GET_ACTIVE_BRANCH_MANAGER_UNDER_OFFICE = "personnel.getActiveBranchManagerUnderOffice";
    public static final String GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanDetailsForDateAndBranch";

    // AGING ARREARS FOR BRANCH REPORT
    public static final String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD = "branchReport.extractBranchReportLoanArrears";

    // STAFF SUMMARY FOR BRANCH REPORT
    public static final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS = "branchReport.extractStaffSummaryActiveBorrowersLoans";
    public static final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT = "branchReport.extractStaffSummaryCenterAndClientCount";
    public static final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING = "branchReport.extractLoanAmountOutstanding";
    public static final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR = "branchReport.extractPortfolioAtRisk";
    public static final String EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR = "branchReport.extractPortfolioAtRiskForBranch";
    public static final String EXTRACT_BRANCH_REPORT_LOAN_DETAILS = "branchReport.extractLoanDetailsForBranch";
    public static final String EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS = "branchReport.extractLoansInArrearsForBranch";
    public static final String EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK = "branchReport.extractLoansAndOutstandingAmountsAtRiskForBranch";
    public static final String EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK = "branchReport.extractLoanArrearsProfileClientsAtRiskForBranch";
    public static final String EXTRACT_BRANCH_REPORT_TOTAL_CLIENTS_ENROLLED_BY_PERSONNEL = "branchReport.extractTotalClientsFormedByPersonnel";
    public static final String EXTRACT_BRANCH_REPORT_CLIENTS_ENROLLED_BY_PERSONNEL_THIS_MONTH = "branchReport.extractTotalClientsFormedByPersonnelThisMonth";
    public static final String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_AMOUNT_FOR_PERSONNEL = "branchReport.extractLoanArrearsAmountForPersonnel";

    // Branch Cash Confirmation Report Queries
    public static final String GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.getCenterRecoveries";
    public static final String GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES = "branchCashConfirmationReport.getCenterIssues";
    public static final String GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.getDisbursements";

    public static final String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH = "branchCashConfirmationReport.getReportForDateAndBranch";
    public static final String COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA = "collectionSheet.extractCollectionSheetReportData";

    public static final String SCHEDULED_TASK_GET_LATEST_TASK = "scheduledTasks.getLatestTask";
    public static final String SCHEDULED_TASK_GET_SUCCESSFUL_TASK = "scheduledTasks.getSuccessfulTask";

    // Imported transactions files
    public static final String GET_IMPORTED_FILES_BY_NAME = "importfiles.getImportedFileByName";

    // Insert CustomerCustomFieldEntity entries
    public static final String INSERT_CUSTOMER_CUSTOM_FIELD_ENTITY = "CustomerCustomFieldEntity.insertEntries";
}
