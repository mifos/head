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
 * This class holds the names of named queries which are used for master data
 * retrieval.
 */
public interface NamedQueryConstants {

    public final String GL_CODE_BY_ID = "GLCode.findById";
    // fund related
    public final String CHECK_FUND_NAME_EXIST = "fund.checkFundNameExist";
    public final String GET_FUND_CODES = "fund.getFundCodes";
    public final String GET_FUND_FOR_GIVEN_NAME = "fund.getFundForGivenName";
    public final String GET_OFFICE_COUNT = "office.getOfficeCountForLevel";
    // for group status
    public final String GET_ALLSAVINGS_PRODUCTS = "productOffering.getAllSavingsProducts";

    public final String GET_ALL_ACTIVE_SAVINGS_PRODUCTS = "productOffering.getAllActiveSavingsProducts";

    // for notes
    public final String GETALLACCOUNTNOTES = "notes.getAllAccountNotes";

    // for roles and permission
    public final String GETLOOKUPVALUELOCALE = "lookUpValueLocaleEntity.getByLocaleAndLookUpId";
    public final String GETACTIVITYENTITY = "activityEntity.getActivityEntityByLookUpValueEntity";
    // for security
    public final String GETACTIVITYROLES = "getActivityRoles";
    public final String GETPERSONROLES = "getPersonRoles";
    public final String GETOFFICESEARCH = "getOfficeSearch";
    public final String GETOFFICESEARCHLIST = "getOfficeSearchList";


    // Product Category
    public final String PRODUCTCATEGORIES_COUNT_CREATE = "product.createduplcount";
    public final String PRODUCTCATEGORIES_COUNT_UPDATE = "product.updateduplcount";
    public final String PRODUCTCATEGORIES_SEARCH = "product.searchcategories";
    public final String PRODUCTCATEGORIES_MAX = "product.maxprdcatid";

    // Product Offering
    public final String LOAD_PRODUCTS_OFFERING_MIX = "product.retrieveProductOfferingMix";

    public final String PRD_BYTYPE = "product.bytype";
    public final String ALLOWED_PRD_OFFERING_BYTYPE = "product.allowedproductofferingbytype";
    public final String NOT_ALLOWED_PRD_OFFERING_BYTYPE = "product.notallowedproductofferingbytype";
    public final String NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.notallowedproductformixproduct";
    public final String ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.allowedproductformixproduct";

    public final String PRD_BYID = "product.byid";
    public final String PRD_MIX_BYID = "product.prdmixbyid";

    // Savings Product

    public final String PRDSRCFUNDS = "product.srcfund";

    // for Loan accounts
    public final String GET_COSIGNING_CLIENTS_FOR_GLIM = "loan.getCosigningLoansOfClientsForGlim";
    public final String RETRIEVE_TOTAL_LOAN_FOR_CUSTOMER = "loan.retrieveTotalLoanForCustomer";

    // for Savings accounts
    public final String RETRIEVE_TOTAL_SAVINGS_FOR_CUSTOMER = "savings.retrieveTotalSavingsForCustomer";



    // for account status
    public final String RETRIEVEALLACCOUNTSTATES = "accounts.retrieveAllAccountStates";
    public final String RETRIEVEALLACTIVEACCOUNTSTATES = "accounts.retrieveAllActiveAccountStates";
    public final String STATUSCHECKLIST = "account.statusChecklist";
    public final String ACCOUNT_GETALLLOANBYCUSTOMER = "accounts.GetAllLoanByCustomer";

    // for Customer Search
    public final String GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveClientsCountUnderOffice";
    public final String GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveOrHoldClientsCountUnderOffice";
    public final String GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorActiveOrHoldClientsCountUnderOffice";
    public final String GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorClientsCountUnderOffice";
    public final String GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getActiveBorrowersCountUnderOffice";
    public final String GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorBorrowersCountUnderOffice";
    public final String GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getActiveSaversCountUnderOffice";
    public final String GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorActiveSaversCountUnderOffice";
    public final String GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE = "Customers.getReplacementsCountUnderOffice";
    public final String GET_VERY_POOR_CLIENTS_UNDER_OFFICE = "Customers.getVeryPoorClientsUnderOffice";
    public final String GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountByLoanAccountForOffice";
    public final String GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountBySavingAccountForOffice";
    public final String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountByLoanAccountForOffice";
    public final String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountBySavingAccountForOffice";
    public final String GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getDropOutClientsCountUnderOffice";
    public final String GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorDropOutClientsCountUnderOffice";
    public final String GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getOnHoldClientsCountUnderOffice";
    public final String GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorOnHoldClientsCountUnderOffice";


    // Apply Adjustment
    public final String RETRIEVE_MAX_ACCPAYMENT = "accountPayment.maxAccPayment";
    public final String RETRIEVE_ALL_ACCPAYMENT = "accountPayment.allAccPayment";

    // For client closedacc, changelog,fee details
    public final String VIEWALLSAVINGSCLOSEDACCOUNTS = "accounts.viewallsavingsclosedaccounts";
    public final String VIEWALLCLOSEDACCOUNTS = "customer.viewallclosedaccounts";

    public final String GROUP_SEARCH_WITHOUT_CENTER = "group_SearchWithoutCenter";
    public final String GROUP_SEARCH_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithoutCenterForAddingGroupMember";

    // for collection sheet
    public final String COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.customersWithSpecifiedMeetingDateAsSql";
    public final String CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE = "CollectionSheetCustomer.customersWithSpecifiedDisbursalDate";
    public final String COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.loansWithSpecifiedMeetingDateAsSql";

    public final String COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.savingssWithSpecifiedMeetingDateAsSql";

    public final String MASTERDATA_ACTIVE_BRANCHES = "masterdata.activeBranches";

    /** M2 queries */
    /** Personnel Queries */
    public final String MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH = "masterdata.activeloanofficers";
    /* Customer Queries */
    public final String GET_PARENTCUSTOMERS_FOR_LOANOFFICER = "Customer.getParentCustomersForLoanOfficer";
    public final String GET_ACTIVE_CHILDREN_FORPARENT = "Customer.getActiveChildrenForParent";
    public final String GET_CHILDREN_OTHER_THAN_CLOSED = "Customer.getChildrenOtherThanClosed";
    public final String GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED = "Customer.getChildrenOtherThanClosedAndCancelled";
    public final String GET_ALL_CHILDREN = "Customer.getAllChildren";
    public final String GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL = "Customer.getAllChildrenForCustomerLevel";
    public final String GET_ACTIVE_AND_ONHOLD_CHILDREN = "Customer.getActiveAndOnHoldChildren";
    public final String GET_ACTIVE_AND_ONHOLD_CHILDREN_COUNT = "Customer.getActiveAndOnHoldChildrenCount";
    public final String GET_CUSTOMER_COUNT_FOR_OFFICE = "Customer.getCustomerCountForOffice";
    public final String GET_SEARCH_IDS_FOR_OFFICE = "Customer.getSearchIdsForOffice";
    public final String GET_SQL_CUSTOMER_COUNT_FOR_OFFICE = "Customer.Sql.getCustomerCountForOffice";
    public final String GET_SQL_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getCustomerCountBasedOnStatusForOffice";
    public final String GET_SQL_VERY_POOR_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getVeryPoorCustomerCountBasedOnStatusForOffice";
    public final String GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getActiveAccountUserCountForOffice";
    public final String GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorActiveBorrowersCountForOffice";
    public final String GET_SQL_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getReplacementCountForOffice";
    public final String GET_SQL_VERY_POOR_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorReplacementCountForOffice";
    public final String GET_TOTAL_AMOUNT_FOR_GROUP = "Customer.getTotalAmountForGroup";
    public final String GET_LOAN_SUMMARY_CURRENCIES_FOR_ALL_CLIENTS_OF_GROUP= "Customer.getLoanSummaryCurrenciesForAllClientsOfGroup";
    public final String GET_LOAN_SUMMARY_CURRENCIES_FOR_GROUP = "Customer.getLoanSummaryCurrenciesForGroup";
    public final String GET_TOTAL_AMOUNT_FOR_ALL_CLIENTS_OF_GROUP = "Customer.getTotalAmountForAllClientsOfGroup";
    public final String GET_ALL_BASIC_GROUP_INFO = "Customer.getAllBasicGroupInfo";

    /* Office Queries */
    public final String OFFICE_GET_SEARCHID = "office.getOfficeSearchId";
    public final String OFFICE_GET_HEADOFFICE = "office.getHeadOffice";
    public final String GET_TOP_LEVEL_OFFICE_NAMES = "office.topLevelOfficeNames";
    public final String GET_ACCOUNT_ID_OFFICE_ID_MAP = "account.AccountOfficeMap";
    /** Account */
    public final String GET_CUSTOMER_STATE_CHECKLIST = "customer.checklist";
    public final String GET_LAST_MEETINGDATE_FOR_CUSTOMER = "accounts.getLastMeetingDateforCustomer";

    public final String GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS = "accounts.getApplicableSavingsProductOfferings";
    public final String RETRIEVE_SAVINGS_ACCCOUNT = "accounts.retrieveSavingsAccounts";
    public final String RETRIEVE_ACCCOUNTS_FOR_INT_CALC = "accounts.retrieveSavingsAccountsIntCalc";
    public final String RETRIEVE_ACCCOUNTS_FOR_INT_POST = "accounts.retrieveSavingsAccountsIntPost";
    public final String RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER = "accounts.retrieveSavingsAccountsForCustomer";
    public final String RETRIEVE_ACCCOUNTS_FOR_CUSTOMER = "accounts.retrieveAccountsForCustomer";
    public final String GET_MISSED_DEPOSITS_COUNT = "accounts.countOfMissedDeposits";
    // accounts
    public final String GET_MAX_ACCOUNT_ID = "accounts.getMaxAccountId";
    public final String FIND_ACCOUNT_BY_SYSTEM_ID = "accounts.findBySystemId";
    public final String FIND_LOAN_ACCOUNT_BY_SYSTEM_ID = "accounts.findLoanBySystemId";
    public final String FIND_LOAN_ACCOUNT_BY_EXTERNAL_ID = "accounts.findLoanByExternalId";
    public final String FIND_LOAN_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findLoanByClientGovernmentIdAndProductShortName";
    public final String FIND_SAVINGS_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findSavingsByClientGovernmentIdAndProductShortName";
    public final String FIND_INDIVIDUAL_LOANS = "accounts.findIndividualLoans";
    public final String RETRIEVE_LAST_TRXN = "accounts.retrieveLastTrxn";
    public final String RETRIEVE_FIRST_TRXN = "accounts.retrieveFirstTrxn";


    public final String GET_ENTITIES = "entities";
    public final String GET_LOOKUPVALUES = "lookupvalues";
    public final String SUPPORTED_LOCALE_LIST = "supportedlocales";
    public final String AVAILABLE_LANGUAGES = "availableLanguages";
    public final String GET_CURRENCY = "getCurrency";

    // BulkEntry
    public final String GET_FEE_AMOUNT_AT_DISBURSEMENT = "accounts.getFeeAmountAtDisbursement";

    /* custom fields */
    public final String RETRIEVE_CUSTOM_FIELDS = "retrieveCustomFields";
    public final String RETRIEVE_ALL_CUSTOM_FIELDS = "retrieveAllCustomFields";
    public final String RETRIEVE_ONE_CUSTOM_FIELD = "retrieveOneCustomField";

    public final String FETCH_PRODUCT_NAMES_FOR_GROUP = "Customer.fetchProductNamesForGroup";
    public final String FETCH_PRODUCT_NAMES_FOR_CLIENT = "Customer.fetchProductNamesForClient";

    // Customer Accounts Quries
    public final String CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID = "customer.findBySystemId";
    public final String CUSTOMER_FIND_COUNT_BY_SYSTEM_ID = "customer.findCountBySystemId";
    public final String CUSTOMER_FIND_COUNT_BY_GOVERNMENT_ID = "customer.findCountByGovernmentId";
    public final String GET_CUSTOMER_STATUS_LIST = "customer.getStatusForCustomer";
    public final String GET_CENTER_BY_SYSTEMID = "customer.findCenterSystemId";
    public final String GET_GROUP_BY_SYSTEMID = "customer.findGroupSystemId";
    public final String GET_CLIENT_BY_SYSTEMID = "customer.findClientSystemId";

    // number of meetings attended and missed
    public final String NUMBEROFMEETINGSATTENDED = "numberOfMeetingsAttended";
    public final String NUMBEROFMEETINGSMISSED = "numberOfMeetingsMissed";

    // configuration
    public final String GET_CONFIGURATION_KEYVALUE_BY_KEY = "getConfigurationKeyValueByKey";
    public final String GET_ALL_CONFIGURATION_VALUES = "getAllConfigurationValues";

    // To get customer account
    public final String CUSTOMER_ACCOUNT_ACTIONS_DATE = "accounts.getCustomerAccountActionDates";

    public final String GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING = "accounts.GetLoanArrearsInGoodStanding";
    public final String GET_LATENESS_FOR_LOANS = "productdefenition.GetLatenessDaysForLoans";
    public final String GET_DORMANCY_DAYS = "productdefenition.getDormancyDays";
    public final String GET_ACCOUNT_STATES = "accounts.getStates";
    public final String GET_CUSTOMER_STATES = "customer.getStates";
    public final String GET_ALL_OFFICES = "office.getAllOffices";
    public final String GET_OFFICES_TILL_BRANCHOFFICE = "office.getOfficesTillBranchOffice";
    public final String GET_BRANCH_OFFICES = "office.getBranchOffices";

    public final String GET_FIELD_LIST = "getFieldList";
    public final String GET_ENTITY_MASTER = "getEntityMaster";

    public final String CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS = "customerScheduleEntity.getScheduleForIds";
    public final String GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE = "accounts.countOfMissedDepositsPaidAfterDueDate";
    public final String GET_CENTER_COUNT_BY_NAME = "Customer.getCenterCount";
    public final String GET_GROUP_COUNT_BY_NAME = "Customer.getGroupCountByGroupNameAndOffice";

    // fee related m2
    public final String GET_CUSTOMER_ACCOUNTS_FOR_FEE = "getCustomerAccountsForFee";

    // Seems not to be used by anything
    public final String GET_FEE_UPDATETYPE = "getFeeUpdateType";

    public final String RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE = "retrieveCustomerFeesByCategoryType";
    public final String RETRIEVE_CUSTOMER_FEES = "retrieveCustomerFees";
    public final String RETRIEVE_PRODUCT_FEES = "retrieveProductFees";

    public final String APPLICABLE_LOAN_PRODUCTS = "loanOffering.getApplicableProducts";

    public final String GET_ALL_APPLICABLE_CUSTOMER_FEE = "getAllApplicableFeesForCustomer";
    public final String GET_ALL_APPLICABLE_LOAN_FEE = "getAllApplicableFeesForLoan";

    public final String GETALLCUSTOMERNOTES = "notes.getAllCustomerNotes";

    public final String GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION = "getAllApplicableFeesForLoanCreation";
    public final String MASTERDATA_MIFOS_ENTITY_VALUE = "masterdata.mifosEntityValue";

    public final String FORMEDBY_LOANOFFICERS_LIST = "personnel.formedByLoanOfficers";
    public final String GETALLPERSONNELNOTES = "personnel.getAllPersonnelNotes";
    // M2 office
    public final String GETMAXOFFICEID = "office.getMaxId";
    public final String GETCHILDCOUNT = "office.getChlidCount";
    public final String CHECKOFFICENAMEUNIQUENESS = "office.getOfficeWithName";
    public final String CHECKOFFICESHORTNAMEUNIQUENESS = "office.getOfficeWithShortName";
    public final String GET_CLOSED_CLIENT_BASED_ON_NAME_DOB = "Customer.getClosedClientBasedOnNameAndDateOfBirth";
    public final String GET_ACTIVE_OFFERINGS_FOR_CUSTOMER = "product.getActiveOfferingsForCustomer";
    public final String GET_CLOSED_CLIENT_BASEDON_GOVTID = "Customer.getClosedClientBasedOnGovtId";
    public final String GETOFFICEACTIVEPERSONNEL = "getCountActivePersonnel";
    public final String GETCOUNTOFACTIVECHILDERN = "getCountOfActiveChildren";
    public final String GETACTIVEPARENTS = "masterdata.activeParents";
    public final String GETACTIVELEVELS = "masterdata.activeLevels";
    public final String GETOFFICESTATUS = "masterdata.officestatus";
    public final String GETCHILDERN = "getChlidren";
    public final String GET_CUSTOMER_PICTURE = "Customer.getPicture";
    public final String GETOFFICEINACTIVE = "getCountInactiveOffice";
    public final String GET_PRD_TYPES = "productdefenition.getProductTypes";
    public final String GET_PRODUCTCATEGORY = "productdefenition.getProductCategory";
    public final String GET_PRDCATEGORYSTATUS = "productdefenition.prdcategorystatus";
    public final String GET_OFFICES_TILL_BRANCH = "office.getOfficesTillBranchOfficeActive";
    public final String GET_BRANCH_PARENTS = "office.getBranchParents";

    // M2 center
    public final String GET_LO_FOR_CUSTOMER = "Customer.getLOForCustomer";
    public final String GET_OFFICE_LEVELS = "officeLevel.getOfficeLevels";
    // Change Account Status
    public final String GET_SEARCH_RESULTS = "account.getSearchResults";

    // M2 personnel
    public final String GET_PERSONNEL_WITH_NAME = "getCountByName";
    public final String GETPERSONNELBYNAME = "getPersonnelByName";
    public final String GETPERSONNELBYDISPLAYNAME = "getPersonnelByDisplayName";
    public final String GET_PERSONNEL_WITH_GOVERNMENTID = "getCountByGovernmentId";
    public final String GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME = "getCountByDobAndDisplayName";
    public final String GET_ACTIVE_CUSTOMERS_FOR_LO = "Customer.getActiveCustomersForLO";
    public final String GET_ALL_CUSTOMERS_FOR_LO = "Customer.getAllCustomersForLO";
    public final String GETOFFICE_CHILDREN = "office.getAllChildOffices";
    public final String PERSONNEL_BY_SYSTEM_ID = "getPersonBySystemId";
    public final String PRODUCTOFFERING_MAX = "product.maxprdofferingid";
    public final String PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT = "product.createofferingnamecount";
    public final String PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT = "product.createofferingshortnamecount";

    public final String PRDAPPLICABLE_CATEGORIES = "product.createapplicableproductcat";
    public final String SAVINGS_APPL_RECURRENCETYPES = "product.savingsapplicablerecurrencetypes";

    public final String GET_ROLE_FOR_GIVEN_NAME = "getRoleForGivenName";
    public final String GET_ALL_ACTIVITIES = "getAllActivities";
    public final String GET_ALL_ROLES = "getAllRoles";
    public final String GET_PERSONNEL_ROLE_COUNT = "getPersonnelRoleCount";

    public final String PRODUCT_STATUS = "product.status";
    public final String ALL_PRD_STATES = "product.getAllPrdStates";
    public final String PRODUCT_ALL_LOAN_PRODUCTS = "product.getAllLoanProducts";
    public final String PRODUCT_NOTMIXED_LOAN_PRODUCTS = "product.getLoanOfferingsNotMixed";
    public final String PRODUCT_ALL_ACTIVE_LOAN_PRODUCTS = "product.getAllActiveLoanProducts";

    public final String PRODUCT_NOTMIXED_SAVING_PRODUCTS = "product.getSavingOfferingsNotMixed";

    // m2 search quaries
    public final String CUSTOMER_SEARCH_COUNT = "Customer.cust_count_search";

    public final String CUSTOMER_SEARCH = "Customer.cust_search";
    public final String CUSTOMER_SEARCH_NOOFFICEID = "Customer.cust_search_noofficeid";
    public final String CUSTOMER_SEARCH_COUNT_NOOFFICEID = "Customer.cust_count_search_noofficeid";

    public final String CUSTOMER_ID_SEARCH_NOOFFICEID = "Customer.cust_idsearch_withoutoffice";
    public final String CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_idsearch_withoutoffice_count";

    public final String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID = "Customer.cust_govidsearch_withoutoffice";
    public final String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_govidsearch_withoutoffice_count";

    public final String CUSTOMER_ID_SEARCH = "Customer.cust_idsearch";
    public final String CUSTOMER_ID_SEARCH_COUNT = "Customer.cust_idsearch_count";

    public final String CUSTOMER_GOVERNMENT_ID_SEARCH = "Customer.cust_govidsearch";
    public final String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT = "Customer.cust_govidsearch_count";

    public final String CUSTOMER_ID_SEARCH_NONLO = "Customer.cust_idsearch_nonLo";
    public final String CUSTOMER_ID_SEARCH_COUNT_NONLO = "Customer.cust_idsearch_count_nonLo";

    public final String CUSTOMER_GOVERNMENT_ID_SEARCH_NONLO = "Customer.cust_govidsearch_nonLo";
    public final String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT_NONLO = "Customer.cust_govidsearch_count_nonLo";

    public final String ACCOUNT_ID_SEARCH = "accounts.account_IdSearch";
    public final String ACCOUNT_ID_SEARCH_NOOFFICEID = "accounts.account_IdSearch_withoutoffice";
    public final String ACCOUNT_ID_SEARCH_COUNT = "accounts.account_IdSearch_count";
    public final String ACCOUNT_ID_SEARCH_NOOFFICEID_COUNT = "accounts.account_IdSearch_withoutoffice_count";

    public final String ACCOUNT_LIST_ID_SEARCH = "accounts.account_list_IdSearch";
    public final String PERSONNEL_SEARCH_COUNT = "count_search_Personnel";
    public final String PERSONNEL_SEARCH = "search_Personnel";

    public final String CENTER_SEARCH = "search_Centers";
    public final String CENTER_SEARCH_COUNT = "count_search_Centers";
    public final String GROUP_SEARCHWITH_CENTER = "group_SearchWithCenter";
    public final String GROUP_SEARCHWITH_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithCenterForAddingGroupMember";
    public final String GROUP_SEARCH_COUNT_WITH_CENTER = "count_group_SearchWithCenter";
    public final String GROUP_SEARCH_COUNT_WITH_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithCenterForAddingGroupMember";
    public final String GROUP_SEARCH_COUNT_WITHOUT_CENTER = "count_group_SearchWithoutCenter";
    public final String GROUP_SEARCH_COUNT_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithoutCenterForAddingGroupMember";
    public final String SEARCH_GROUP_CLIENT_COUNT_LO = "Customer.count_cust_for_account";
    public final String SEARCH_GROUP_CLIENT_LO = "Customer.cust_for_account";
    public final String SEARCH_GROUP_CLIENT_COUNT = "Customer.cust_count_account_Search";
    public final String SEARCH_GROUP_CLIENT = "Customer.cust_account_Search";
    public final String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT = "Customer.count_customersForSavingsAccount";
    public final String SEARCH_CUSTOMER_FOR_SAVINGS = "Customer.customersForSavingsAccount";
    public final String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO = "Customer.count_customersForSavingsAccountNonLO";
    public final String SEARCH_CUSTOMER_FOR_SAVINGS_NOLO = "Customer.customersForSavingsAccountNonLO";
    public final String SEARCH_CENTERS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_centers";
    public final String SEARCH_GROUPS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_groups";
    public final String GET_ACTIVE_LOAN_OFFICER_UNDER_USER = "get_active_loanofficers_under_office";
    public final String GET_ACTIVE_BRANCHES = "get_active_offices";

    public final String RETRIEVE_AUDIT_LOG_RECORD = "retrieveAuditLogRecords";

    // check List M2
    public final String MASTERDATA_CUSTOMER_CHECKLIST = "masterdata.customer_checklist";
    public final String MASTERDATA_PRODUCT_CHECKLIST = "masterdata.product_checklist";
    public final String CUSTOMER_VALIDATESTATE = "customer.validateState";
    public final String PRODUCT_VALIDATESTATE = "product.validateState";
    public final String LOAD_ALL_CUSTOMER_CHECKLISTS = "checklist.loadAllCustomerCheckLists";
    public final String LOAD_ALL_ACCOUNT_CHECKLISTS = "checklist.loadAllAccountCheckLists";
    public final String CHECKLIST_GET_VALID_CUSTOMER_STATES = "checklist.getStatusForCustomer";
    public final String CHECKLIST_GET_VALID_ACCOUNT_STATES = "checklist.getStatusForAccount";
    public final String FETCH_ALL_RECURRENCE_TYPES = "meeting.fetchAllReccurenceTypes";

    public final String ACTIVE_CLIENTS_UNDER_PARENT = "Customer.getActiveClientsUnderParent";
    public final String ACTIVE_CLIENTS_UNDER_GROUP = "Customer.getActiveClientsUnderGroup";

    // holiday handling
    public final String GET_OFFICE_HOLIDAYS = "holiday.getOfficeHolidays";
    public final String GET_ALL_HOLIDAYS = "holiday.getAll";
    public final String GET_APPLICABLE_OFFICES_FOR_HOLIDAYS = "holiday.applicableOffices";
    public final String GET_REPAYMENT_RULE_TYPES = "holiday.getRepaymentRuleLabels";
    public final String GET_REPAYMENT_RULE = "holiday.getRepaymentRule";
    public final String SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS = "savingsScheduleEntity.getScheduleForIds";
    public final String LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS = "loanScheduleEntity.getScheduleForIds";
    public final String GET_HOLIDAYS_BY_FLAG = "holiday.getHoildaysByFlag";
    public final String GET_UNAPPLIED_OFFICE_HOLIDAYS = "holiday.unappliedOfficeHolidays";
    public final String GET_HOLIDAYS_FOR_OFFICES = "holiday.holidaysForOffices";

    public final String GET_ALL_FIELD_CONFIGURATION_LIST = "getAllFieldConfigurationList";

    // surveys
    public final String SURVEYS_RETRIEVE_ALL = "surveys.retrieveAllSurveys";
    public final String RESPONSES_RETRIEVE_ALL = "surveys.retrieveAllResponses";
    public final String SURVEYS_RETRIEVE_BY_TYPE = "surveys.retrieveSurveysByType";
    public final String SURVEYS_RETRIEVE_BY_TYPE_AND_STATE = "surveys.retrieveSurveysByTypeAndState";
    public final String SURVEYS_RETRIEVE_BY_STATUS = "surveys.retrieveSurveysByStatus";
    public final String SURVEYS_RETRIEVE_ACTIVE_PPI = "surveys.retrieveActivePPISurvey";
    public final String SURVEYS_RETRIEVE_PPI_BY_COUNTRY = "surveys.retrievePPISurveyByCountry";
    public final String SURVEYS_RETRIEVE_ALL_PPI = "surveys.retrieveAllPPISurveys";
    public final String QUESTIONS_GET_NUM = "questions.getNum";
    public final String QUESTIONS_RETRIEVE_ALL = "questions.retrieveAll";
    public final String QUESTIONS_RETRIEVE_BY_STATE = "questions.retrieveByState";
    public final String QUESTIONS_RETRIEVE_BY_TYPE = "questions.retrieveByAnswerType";
    public final String QUESTIONS_RETRIEVE_BY_NAME = "questions.retrieveByName";
    public final String SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES = "surveys.retrieveCustomersSurveys";
    public final String SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES = "surveys.retrieveAccountsSurveys";
    public final String SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER = "surveys.retrieveInstancesByCustomer";
    public final String SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT = "surveys.retrieveInstancesByAccount";
    public final String SURVEYINSTANCE_RETRIEVE_BY_SURVEY = "surveys.retrieveInstancesBySurvey";
    public final String RESPONSES_RETRIEVE_BY_INSTANCE = "surveys.retrieveResponsesByInstance";

    // products mix
    public final String LOAD_ALL_DEFINED_PRODUCTS_MIX = "productsmix.retrieveAll";
    public final String LOAD_NOT_ALLOWED_PRODUCTS = "productsmix.loadnotallowedproducts";

    public final String GET_TOP_LEVEL_ACCOUNT = "COABO.getTopLevelAccount";
    // accepted payment type
    public final String GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION = "acceptedpaymenttype.getAcceptedPaymentTypesForATransaction";

    public final String GET_TOP_LEVEL_ACCOUNTS = "COABO.getTopLevelAccounts";
    public final String GET_ACCOUNT_ID_FOR_GL_CODE = "COABO.getAccountIdForGLCode";
    public final String GET_ALL_COA = "COABO.getAllCoa";

    // Administrative documents

    public final String GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllActiveAdministrativeDocument";
    public final String GET_MIX_BY_ADMINISTRATIVE_DOCUMENT = "admindocument.getMixByAdministrativeDocument";
    public final String GET_ALL_MIXED_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllMixedAdministrativeDocument";

    // new queries for issue 1601 to allow backfilling of custom fields
    // on existing database objects like customers, personnel, loans, etc.
    public final String GET_CUSTOMERS_BY_LEVELID = "Customer.getCustomersByLevelId";
    public final String GET_ALL_OFFICES_FOR_CUSTOM_FIELD = "office.getAllOfficesForCustomField";
    public final String GET_ALL_PERSONNEL = "personnel.getAllPersonnel";
    public final String GET_ALL_SAVINGS_ACCOUNTS = "accounts.getAllSavingsAccounts";
    public final String GET_ALL_LOAN_ACCOUNTS = "accounts.getAllLoanAccounts";

    public final String GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportForDateAndBranch";
    public final String GET_BRANCH_REPORT_FOR_DATE = "branchReport.getBranchReportForDate";
    public final String GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportClientSummaryForDateAndBranch";
    public final String GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsAgingForDateAndBranch";
    public final String GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsProfileForDateAndBranch";
    public final String GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffSummaryForDateAndBranch";
    public final String GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffingLevelSummaryForDateAndBranch";
    public final String GET_ACTIVE_BRANCH_MANAGER_UNDER_OFFICE = "personnel.getActiveBranchManagerUnderOffice";
    public final String GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanDetailsForDateAndBranch";

    // AGING ARREARS FOR BRANCH REPORT
    public final String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD = "branchReport.extractBranchReportLoanArrears";

    // STAFF SUMMARY FOR BRANCH REPORT
    public final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS = "branchReport.extractStaffSummaryActiveBorrowersLoans";
    public final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT = "branchReport.extractStaffSummaryCenterAndClientCount";
    public final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING = "branchReport.extractLoanAmountOutstanding";
    public final String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR = "branchReport.extractPortfolioAtRisk";
    public final String EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR = "branchReport.extractPortfolioAtRiskForBranch";
    public final String EXTRACT_BRANCH_REPORT_LOAN_DETAILS = "branchReport.extractLoanDetailsForBranch";
    public final String EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS = "branchReport.extractLoansInArrearsForBranch";
    public final String EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK = "branchReport.extractLoansAndOutstandingAmountsAtRiskForBranch";
    public final String EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK = "branchReport.extractLoanArrearsProfileClientsAtRiskForBranch";
    public final String EXTRACT_BRANCH_REPORT_TOTAL_CLIENTS_ENROLLED_BY_PERSONNEL = "branchReport.extractTotalClientsFormedByPersonnel";
    public final String EXTRACT_BRANCH_REPORT_CLIENTS_ENROLLED_BY_PERSONNEL_THIS_MONTH = "branchReport.extractTotalClientsFormedByPersonnelThisMonth";
    public final String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_AMOUNT_FOR_PERSONNEL = "branchReport.extractLoanArrearsAmountForPersonnel";

    // Branch Cash Confirmation Report Queries
    public final String EXTRACT_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.extractCenterRecovery";
    public final String EXTRACT_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.extractDisbursement";

    public final String GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.getCenterRecoveries";
    public final String GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES = "branchCashConfirmationReport.getCenterIssues";
    public final String GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.getDisbursements";

    public final String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE = "branchCashConfirmationReport.getReportForDate";
    public final String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH = "branchCashConfirmationReport.getReportForDateAndBranch";
    public final String COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA = "collectionSheet.extractCollectionSheetReportData";

    public final String SCHEDULED_TASK_GET_LATEST_TASK = "scheduledTasks.getLatestTask";
    public final String SCHEDULED_TASK_GET_SUCCESSFUL_TASK = "scheduledTasks.getSuccessfulTask";

    // Imported transactions files
    public final String GET_IMPORTED_FILES_BY_NAME = "importfiles.getImportedFileByName";

    // Insert CustomerCustomFieldEntity entries
    public final String INSERT_CUSTOMER_CUSTOM_FIELD_ENTITY = "CustomerCustomFieldEntity.insertEntries";
}
