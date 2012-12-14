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

package org.mifos.application;

/**
 * This class holds the names of named queries which are used for master data
 * retrieval.
 */
public interface NamedQueryConstants {

    String GL_CODE_BY_ID = "GLCode.findById";
    // fund related
    String CHECK_FUND_NAME_EXIST = "fund.checkFundNameExist";
    String GET_FUND_CODES = "fund.getFundCodes";
    String GET_FUND_FOR_GIVEN_NAME = "fund.getFundForGivenName";
    String GET_OFFICE_COUNT = "office.getOfficeCountForLevel";
    // for group status
    String GET_ALLSAVINGS_PRODUCTS = "productOffering.getAllSavingsProducts";

    String GET_ALL_ACTIVE_SAVINGS_PRODUCTS = "productOffering.getAllActiveSavingsProducts";

    // for notes
    String GETALLACCOUNTNOTES = "notes.getAllAccountNotes";

    // for roles and permission
    String GETLOOKUPVALUELOCALE = "lookUpValueLocaleEntity.getByLocaleAndLookUpId";
    String GETACTIVITYENTITY = "activityEntity.getActivityEntityByLookUpValueEntity";
    // for security
    String GETACTIVITYROLES = "getActivityRoles";
    String GETPERSONROLES = "getPersonRoles";
    String GETOFFICESEARCH = "getOfficeSearch";
    String GETOFFICESEARCHLIST = "getOfficeSearchList";

    // Product Category
    String PRODUCTCATEGORIES_COUNT_CREATE = "product.createduplcount";
    String PRODUCTCATEGORIES_COUNT_UPDATE = "product.updateduplcount";
    String PRODUCTCATEGORIES_SEARCH = "product.searchcategories";
    String PRODUCTCATEGORIES_MAX = "product.maxprdcatid";

    // Product Offering
    String LOAD_PRODUCTS_OFFERING_MIX = "product.retrieveProductOfferingMix";

    String PRD_BYTYPE = "product.bytype";
    String ALLOWED_PRD_OFFERING_BYTYPE = "product.allowedproductofferingbytype";
    String NOT_ALLOWED_PRD_OFFERING_BYTYPE = "product.notallowedproductofferingbytype";
    String NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.notallowedproductformixproduct";
    String ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT = "product.allowedproductformixproduct";

    String PRD_BYID = "product.byid";
    String PRD_MIX_BYID = "product.prdmixbyid";

    // Savings Product

    String PRDSRCFUNDS = "product.srcfund";

    // for Loan accounts
    String GET_ALL_LOANS_TO_BE_PAID_CURRENT_WEEK ="loan.getAllLoansToBePaidCurrentWeek";
    String GET_LOANS_TO_BE_PAID_CURRENT_WEEK_UNDER_LOANOFF ="loan.getLoansToBePaidCurrentWeekUnderLoanOfficer";
    String GET_COSIGNING_CLIENTS_FOR_GLIM = "loan.getCosigningLoansOfClientsForGlim";
    String GET_ALL_WAITING_FOR_APPROVAL_LOANS = "loan.getAllWaitingForApprovalLoans";
    String GET_ALL_BAD_STANDING_LOANS = "loan.getAllBadStandingLoans";
    String GET_WAITING_FOR_APPROVAL_LOANS_UNDER_LOANOFF = "loan.getWaitingForApprovalLoansUnderLoanOfficer";
    String GET_BAD_STANDING_LOANS_UNDER_LOANOFF = "loan.getBadStandingLoansUnderLoanOfficer";
    String RETRIEVE_TOTAL_LOAN_FOR_CUSTOMER = "loan.retrieveTotalLoanForCustomer";

    String COUNT_ALL_LOANS_TO_BE_PAID_CURRENT_WEEK ="loan.countAllLoansToBePaidCurrentWeek";
    String COUNT_ALL_WAITING_FOR_APPROVAL_LOANS = "loan.countAllWaitingForApprovalLoans";
    String COUNT_ALL_BAD_STANDING_LOANS = "loan.countAllBadStandingLoans";
    String COUNT_LOANS_TO_BE_PAID_CURRENT_WEEK_UNDER_LOANOFF ="loan.countLoansToBePaidCurrentWeekLoansUnderLoanOfficer";
    String COUNT_WAITING_FOR_APPROVAL_LOANS_UNDER_LOANOFF = "loan.countWaitingForApprovalLoansLoansUnderLoanOfficer";
    String COUNT_BAD_STANDING_LOANS_UNDER_LOANOFF = "loan.countBadStandingLoansUnderLoanOfficer";
    
    // for Savings accounts
    String RETRIEVE_TOTAL_SAVINGS_FOR_CUSTOMER = "savings.retrieveTotalSavingsForCustomer";

    // for account status
    String RETRIEVEALLACCOUNTSTATES = "accounts.retrieveAllAccountStates";
    String RETRIEVEALLACTIVEACCOUNTSTATES = "accounts.retrieveAllActiveAccountStates";
    String STATUSCHECKLIST = "account.statusChecklist";
    String ACCOUNT_GETALLLOANBYCUSTOMER = "accounts.GetAllLoanByCustomer";

    // for Customer Search
    String GET_ALL_BORROWERS = "Customer.getAllBorrowers";
    String GET_ALL_BORROWERS_GROUP = "Customer.getAllBorrowersGroup";
    String GET_ALL_ACTIVE_CLIENTS = "Customer.getAllActiveClients";
    String GET_ALL_ACTIVE_GROUPS = "Customer.getAllActiveGroups";
    String GET_ALL_ACTIVE_CENTERS = "Customer.getAllActiveCenters";
    String GET_ACTIVE_CLIENTS_UNDER_LOANOFF = "Customer.getActiveClientsUnderLoanOff";
    String GET_ACTIVE_GROUPS_UNDER_LOANOFF = "Customer.getActiveGroupsUnderLoanOff";
    String GET_ACTIVE_CENTERS_UNDER_LOANOFF = "Customer.getActiveCentersUnderLoanOff";
    String COUNT_ALL_BORROWERS = "Customer.countAllBorrowers";
    String COUNT_ALL_BORROWERS_GROUP = "Customer.countAllBorrowersGroup";
    String GET_BORROWERS_UNDER_LOANOFF = "Customer.getBorrowersUnderLoanOfficerID";
    String GET_BORROWERS_GROUP_UNDER_LOANOFF = "Customer.getBorrowersGroupUnderLoanOfficerID";
    String COUNT_BORROWERS_UNDER_LOANOFF = "Customer.countBorrowersUnderLoanOfficerID";
    String COUNT_BORROWERS_GROUP_UNDER_LOANOFF = "Customer.countBorrowersGroupUnderLoanOfficerID";
    String GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveClientsCountUnderOffice";
    String GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getActiveOrHoldClientsCountUnderOffice";
    String GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorActiveOrHoldClientsCountUnderOffice";
    String GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE = "Customer.getVeryPoorClientsCountUnderOffice";
    String GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getActiveBorrowersCountUnderOffice";
    String GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorBorrowersCountUnderOffice";
    String GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getActiveSaversCountUnderOffice";
    String GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorActiveSaversCountUnderOffice";
    String GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE = "Customers.getReplacementsCountUnderOffice";
    String GET_VERY_POOR_CLIENTS_UNDER_OFFICE = "Customers.getVeryPoorClientsUnderOffice";
    String GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountByLoanAccountForOffice";
    String GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getDormantClientsCountBySavingAccountForOffice";
    String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountByLoanAccountForOffice";
    String GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE = "Customers.getVeryPoorDormantClientsCountBySavingAccountForOffice";
    String GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getDropOutClientsCountUnderOffice";
    String GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorDropOutClientsCountUnderOffice";
    String GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getOnHoldClientsCountUnderOffice";
    String GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE = "Customers.getVeryPoorOnHoldClientsCountUnderOffice";
    String GET_ACTION_DATES_FOR_CUSTOMER = "Customer.getActionDatesForCustomer";
    
    // Apply Adjustment
    String RETRIEVE_MAX_ACCPAYMENT = "accountPayment.maxAccPayment";
    String RETRIEVE_ALL_ACCPAYMENT = "accountPayment.allAccPayment";

    // Payments
    String FIND_PAYMENT_BY_ID = "accountPayment.findPaymentById";

    // For client closedacc, changelog,fee details
    String VIEWALLSAVINGSCLOSEDACCOUNTS = "accounts.viewallsavingsclosedaccounts";
    String GROUP_SEARCH_WITHOUT_CENTER = "group_SearchWithoutCenter";
    String GROUP_SEARCH_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithoutCenterForAddingGroupMember";

    // for collection sheet
    String COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.customersWithSpecifiedMeetingDateAsSql";
    String CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE = "CollectionSheetCustomer.customersWithSpecifiedDisbursalDate";
    String COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.loansWithSpecifiedMeetingDateAsSql";

    String COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL = "CollectionSheetCustomer.savingssWithSpecifiedMeetingDateAsSql";

    String MASTERDATA_ACTIVE_BRANCHES = "masterdata.activeBranches";

    /** M2 queries */
    /** Personnel Queries */
    String MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH = "masterdata.activeloanofficers";
    /* Customer Queries */
    String GET_PARENTCUSTOMERS_FOR_LOANOFFICER = "Customer.getParentCustomersForLoanOfficer";
    String GET_ACTIVE_CHILDREN_FORPARENT = "Customer.getActiveChildrenForParent";
    String GET_CHILDREN_OTHER_THAN_CLOSED = "Customer.getChildrenOtherThanClosed";
    String GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED = "Customer.getChildrenOtherThanClosedAndCancelled";
    String GET_ALL_CHILDREN = "Customer.getAllChildren";
    String GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL = "Customer.getAllChildrenForCustomerLevel";
    String GET_ACTIVE_AND_ONHOLD_CHILDREN = "Customer.getActiveAndOnHoldChildren";
    String GET_ACTIVE_AND_ONHOLD_CHILDREN_COUNT = "Customer.getActiveAndOnHoldChildrenCount";
    String GET_CUSTOMER_COUNT_FOR_OFFICE = "Customer.getCustomerCountForOffice";
    String GET_SEARCH_IDS_FOR_OFFICE = "Customer.getSearchIdsForOffice";
    String GET_SQL_CUSTOMER_COUNT_FOR_OFFICE = "Customer.Sql.getCustomerCountForOffice";
    String GET_SQL_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getCustomerCountBasedOnStatusForOffice";
    String GET_SQL_VERY_POOR_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE = "Customer.Sql.getVeryPoorCustomerCountBasedOnStatusForOffice";
    String GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getActiveAccountUserCountForOffice";
    String GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorActiveBorrowersCountForOffice";
    String GET_SQL_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getReplacementCountForOffice";
    String GET_SQL_VERY_POOR_REPLACEMENT_COUNT_FOR_OFFICE = "Customer.Sql.getVeryPoorReplacementCountForOffice";
    String GET_TOTAL_AMOUNT_FOR_GROUP = "Customer.getTotalAmountForGroup";
    String GET_LOAN_SUMMARY_CURRENCIES_FOR_ALL_CLIENTS_OF_GROUP = "Customer.getLoanSummaryCurrenciesForAllClientsOfGroup";
    String GET_LOAN_SUMMARY_CURRENCIES_FOR_GROUP = "Customer.getLoanSummaryCurrenciesForGroup";
    String GET_TOTAL_AMOUNT_FOR_ALL_CLIENTS_OF_GROUP = "Customer.getTotalAmountForAllClientsOfGroup";
    String GET_ALL_BASIC_GROUP_INFO = "Customer.getAllBasicGroupInfo";
    String GET_UPLOADED_FILE = "Customer.getUploadedFile";
    String GET_CLIENT_ALL_UPLOADED_FILES = "Customer.getClientAllUploadedFiles";
    String GET_CLIENT_UPLOADED_FILE_BY_NAME = "Customer.getClientUploadedFileByName";
    String GET_LOAN_ALL_UPLOADED_FILES = "Loan.getLoanAllUploadedFiles";
    String GET_LOAN_UPLOADED_FILE_BY_NAME = "Loan.getLoanUploadedFileByName";

    /* Office Queries */
    String OFFICE_GET_SEARCHID = "office.getOfficeSearchId";
    String OFFICE_GET_HEADOFFICE = "office.getHeadOffice";

    /** Account */
    String GET_CUSTOMER_STATE_CHECKLIST = "customer.checklist";
    String GET_LAST_MEETINGDATE_FOR_CUSTOMER = "accounts.getLastMeetingDateforCustomer";
    String GET_FIRST_MEETINGDATE_FOR_CUSTOMER = "accounts.getFirstMeetingDateForCustomer";

    String RETRIEVE_SAVINGS_ACCCOUNT = "accounts.retrieveSavingsAccounts";
    String RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER = "accounts.retrieveSavingsAccountsForCustomer";
    String RETRIEVE_ACCCOUNTS_FOR_CUSTOMER = "accounts.retrieveAccountsForCustomer";
    String GET_MISSED_DEPOSITS_COUNT = "accounts.countOfMissedDeposits";
    // accounts
    String GET_MAX_ACCOUNT_ID = "accounts.getMaxAccountId";
    String FIND_ACCOUNT_BY_SYSTEM_ID = "accounts.findBySystemId";
    String FIND_LOAN_ACCOUNT_BY_SYSTEM_ID = "accounts.findLoanBySystemId";
    String FIND_LOAN_ACCOUNT_BY_EXTERNAL_ID = "accounts.findLoanByExternalId";
    String FIND_LOAN_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findLoanByClientGovernmentIdAndProductShortName";
    String FIND_SAVINGS_ACCOUNT_BY_CLIENT_GOVERNMENT_ID_AND_PRODUCT_SHORT_NAME = "accounts.findSavingsByClientGovernmentIdAndProductShortName";
    String FIND_LOAN_ACCOUNT_BY_CLIENT_PHONE_NUMBER_AND_PRODUCT_SHORT_NAME = "accounts.findLoanByClientPhoneNumberAndProductShortName";
    String FIND_APPROVED_LOANS_FOR_CLIENT_WITH_PHONE_NUMBER = "accounts.findApprovedLoansForClientWithPhoneNumber";
    String FIND_SAVINGS_ACCOUNT_BY_CLIENT_PHONE_NUMBER_AND_PRODUCT_SHORT_NAME = "accounts.findSavingsByClientPhoneNumberAndProductShortName";
    String FIND_INDIVIDUAL_LOANS = "accounts.findIndividualLoans";
    String RETRIEVE_LAST_TRXN = "accounts.retrieveLastTrxn";
    String RETRIEVE_FIRST_TRXN = "accounts.retrieveFirstTrxn";

    String FIND_OVERPAYMENT_BY_ID = "accountOverpayment.findOverpaymentById";

    String GET_ENTITIES = "entities";
    String GET_LOOKUPVALUES = "lookupvalues";
    String SUPPORTED_LOCALE_LIST = "supportedlocales";
    String AVAILABLE_LANGUAGES = "availableLanguages";
    String GET_CURRENCY = "getCurrency";

    // BulkEntry
    String GET_FEE_AMOUNT_AT_DISBURSEMENT = "accounts.getFeeAmountAtDisbursement";

    String FETCH_PRODUCT_NAMES_FOR_GROUP = "Customer.fetchProductNamesForGroup";
    String FETCH_PRODUCT_NAMES_FOR_CLIENT = "Customer.fetchProductNamesForClient";

    // Customer Accounts Quries
    String CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID = "customer.findBySystemId";
    String CUSTOMER_FIND_COUNT_BY_SYSTEM_ID = "customer.findCountBySystemId";
    String CUSTOMER_FIND_COUNT_BY_GOVERNMENT_ID = "customer.findCountByGovernmentId";
    String GET_CUSTOMER_STATUS_LIST = "customer.getStatusForCustomer";
    String GET_CENTER_BY_SYSTEMID = "customer.findCenterSystemId";
    String GET_GROUP_BY_SYSTEMID = "customer.findGroupSystemId";
    String GET_CLIENT_BY_SYSTEMID = "customer.findClientSystemId";

    // number of meetings attended and missed
    String NUMBEROFMEETINGSATTENDED = "numberOfMeetingsAttended";
    String NUMBEROFMEETINGSMISSED = "numberOfMeetingsMissed";

    // configuration
    String GET_CONFIGURATION_KEYVALUE_BY_KEY = "getConfigurationKeyValueByKey";
    String GET_ALL_CONFIGURATION_VALUES = "getAllConfigurationValues";

    // To get customer account
    String CUSTOMER_ACCOUNT_ACTIONS_DATE = "accounts.getCustomerAccountActionDates";

    String RETRIEVE_TOTAL_AMOUNT_IN_ARREARS = "customers.retrieveTotalAmountInArrears";

    String GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING = "accounts.GetLoanArrearsInGoodStanding";
    String GET_LATENESS_FOR_LOANS = "productdefenition.GetLatenessDaysForLoans";
    String GET_DORMANCY_DAYS = "productdefenition.getDormancyDays";
    String GET_ACCOUNT_STATES = "accounts.getStates";
    String GET_CUSTOMER_STATES = "customer.getStates";
    String GET_ALL_OFFICES = "office.getAllOffices";
    String GET_OFFICES_TILL_BRANCHOFFICE = "office.getOfficesTillBranchOffice";
    String GET_BRANCH_OFFICES = "office.getBranchOffices";

    String GET_FIELD_LIST = "getFieldList";
    String GET_ENTITY_MASTER = "getEntityMaster";

    String CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS = "customerScheduleEntity.getScheduleForIds";
    String GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE = "accounts.countOfMissedDepositsPaidAfterDueDate";
    String GET_CENTER_COUNT_BY_NAME = "Customer.getCenterCount";
    String GET_GROUP_COUNT_BY_NAME = "Customer.getGroupCountByGroupNameAndOffice";

    // fee related m2
    String GET_CUSTOMER_ACCOUNTS_FOR_FEE = "getCustomerAccountsForFee";

    // Seems not to be used by anything
    String GET_FEE_UPDATETYPE = "getFeeUpdateType";

    String RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE = "retrieveCustomerFeesByCategoryType";
    String RETRIEVE_CUSTOMER_FEES = "retrieveCustomerFees";
    String RETRIEVE_PRODUCT_FEES = "retrieveProductFees";

    String APPLICABLE_LOAN_PRODUCTS = "loanOffering.getApplicableProducts";

    String GET_ALL_APPLICABLE_CUSTOMER_FEE = "getAllApplicableFeesForCustomer";
    String GET_ALL_APPLICABLE_LOAN_FEE = "getAllApplicableFeesForLoan";

    String GETALLCUSTOMERNOTES = "notes.getAllCustomerNotes";

    String GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION = "getAllApplicableFeesForLoanCreation";
    String MASTERDATA_MIFOS_ENTITY_VALUE = "masterdata.mifosEntityValue";

    String FORMEDBY_LOANOFFICERS_LIST = "personnel.formedByLoanOfficers";
    String GETALLPERSONNELNOTES = "personnel.getAllPersonnelNotes";
    // M2 office
    String GETMAXOFFICEID = "office.getMaxId";
    String GETCHILDCOUNT = "office.getChlidCount";
    String CHECKOFFICENAMEUNIQUENESS = "office.getOfficeWithName";
    String CHECKOFFICESHORTNAMEUNIQUENESS = "office.getOfficeWithShortName";
    String GET_CLOSED_CLIENT_BASED_ON_NAME_DOB = "Customer.getClosedClientBasedOnNameAndDateOfBirth";
    String GET_BLACKLISTED_CLIENT_BASED_ON_NAME_DOB = "Customer.getBlackListedClientBasedOnNameAndDateOfBirth";
    String GET_ACTIVE_OFFERINGS_FOR_CUSTOMER = "product.getActiveOfferingsForCustomer";
    String GET_CLOSED_CLIENT_BASED_ON_GOVT_ID = "Customer.getClosedClientBasedOnGovtId";
    String GETOFFICEACTIVEPERSONNEL = "getCountActivePersonnel";
    String GETCOUNTOFACTIVECHILDERN = "getCountOfActiveChildren";
    String GETACTIVEPARENTS = "masterdata.activeParents";
    String GETACTIVELEVELS = "masterdata.activeLevels";
    String GETOFFICESTATUS = "masterdata.officestatus";
    String GETCHILDERN = "getChlidren";
    String GET_CUSTOMER_PICTURE = "Customer.getPicture";
    String GETOFFICEINACTIVE = "getCountInactiveOffice";
    String GET_PRD_TYPES = "productdefenition.getProductTypes";
    String GET_PRODUCTCATEGORY = "productdefenition.getProductCategory";
    String GET_PRDCATEGORYSTATUS = "productdefenition.prdcategorystatus";
    String GET_OFFICES_TILL_BRANCH = "office.getOfficesTillBranchOfficeActive";
    String GET_BRANCH_PARENTS = "office.getBranchParents";

    // M2 center
    String GET_LO_FOR_CUSTOMER = "Customer.getLOForCustomer";
    String GET_OFFICE_LEVELS = "officeLevel.getOfficeLevels";
    // Change Account Status
    String GET_SEARCH_RESULTS = "account.getSearchResults";

    // M2 personnel
    String GET_PERSONNEL_WITH_NAME = "getCountByName";
    String GETPERSONNELBYNAME = "getPersonnelByName";
    String GETPERSONNELBYDISPLAYNAME = "getPersonnelByDisplayName";
    String GET_PERSONNEL_WITH_GOVERNMENTID = "getCountByGovernmentId";
    String GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME = "getCountByDobAndDisplayName";
    String GET_ACTIVE_CUSTOMERS_FOR_LO = "Customer.getActiveCustomersForLO";
    String GET_ALL_CUSTOMERS_FOR_LO = "Customer.getAllCustomersForLO";
    String GETOFFICE_CHILDREN = "office.getAllChildOffices";
    String PERSONNEL_BY_SYSTEM_ID = "getPersonBySystemId";
    String PRODUCTOFFERING_MAX = "product.maxprdofferingid";
    String PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT = "product.createofferingnamecount";
    String PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT = "product.createofferingshortnamecount";

    String PRDAPPLICABLE_CATEGORIES = "product.createapplicableproductcat";
    String SAVINGS_APPL_RECURRENCETYPES = "product.savingsapplicablerecurrencetypes";

    String GET_ROLE_FOR_GIVEN_NAME = "getRoleForGivenName";
    String GET_ALL_ACTIVITIES = "getAllActivities";
    String GET_ACTIVITY_BY_ID = "getActivityById";
    String GET_ALL_ROLES = "getAllRoles";
    String GET_PERSONNEL_ROLE_COUNT = "getPersonnelRoleCount";

    String GET_ACTIVITY_RESTRICTION_FOR_GIVEN_ID = "getActivityRestrictionById";
    String GET_ROLE_ACTIVITY_RESTRICTION_FOR_GIVEN_TYPEID = "getRoleActivityRestrictionForGivenTypeId";
    String GET_ROLE_ACTIVITIES_RESTRICTIONS = "getRoleActivitiesRestrictions";
    String GET_ALL_ACTIVITY_RESTRICTION_TYPES = "getAllActivityRestrictionTypes";
    String GET_ACTIVITY_RESTRICTION_TYPE_BY_ID = "getActivityRestrictionTypeById";
    
    String PRODUCT_STATUS = "product.status";
    String ALL_PRD_STATES = "product.getAllPrdStates";
    String PRODUCT_ALL_LOAN_PRODUCTS = "product.getAllLoanProducts";
    String PRODUCT_NOTMIXED_LOAN_PRODUCTS = "product.getLoanOfferingsNotMixed";
    String PRODUCT_ALL_ACTIVE_LOAN_PRODUCTS = "product.getAllActiveLoanProducts";

    String PRODUCT_NOTMIXED_SAVING_PRODUCTS = "product.getSavingOfferingsNotMixed";

    // m2 search quaries
    String CUSTOMER_SEARCH_COUNT = "Customer.cust_count_search";

    String CUSTOMER_SEARCH = "Customer.cust_search";
    String CUSTOMER_SEARCH_NOOFFICEID = "Customer.cust_search_noofficeid";
    String CUSTOMER_SEARCH_COUNT_NOOFFICEID = "Customer.cust_count_search_noofficeid";

    String CUSTOMER_ID_SEARCH_NOOFFICEID = "Customer.cust_idsearch_withoutoffice";
    String CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_idsearch_withoutoffice_count";

    String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID = "Customer.cust_govidsearch_withoutoffice";
    String CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID_COUNT = "Customer.cust_govidsearch_withoutoffice_count";

    String CUSTOMER_PHONE_SEARCH_NOOFFICEID = "Customer.cust_phonesearch_withoutoffice";
    String CUSTOMER_PHONE_SEARCH_NOOFFICEID_COUNT = "Customer.cust_phonesearch_withoutoffice_count";

    String CUSTOMER_ID_SEARCH = "Customer.cust_idsearch";
    String CUSTOMER_ID_SEARCH_COUNT = "Customer.cust_idsearch_count";

    String CUSTOMER_GOVERNMENT_ID_SEARCH = "Customer.cust_govidsearch";
    String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT = "Customer.cust_govidsearch_count";

    String CUSTOMER_PHONE_SEARCH = "Customer.cust_phonesearch";
    String CUSTOMER_PHONE_SEARCH_COUNT = "Customer.cust_phonesearch_count";

    String CUSTOMER_ID_SEARCH_NONLO = "Customer.cust_idsearch_nonLo";
    String CUSTOMER_ID_SEARCH_COUNT_NONLO = "Customer.cust_idsearch_count_nonLo";

    String CUSTOMER_GOVERNMENT_ID_SEARCH_NONLO = "Customer.cust_govidsearch_nonLo";
    String CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT_NONLO = "Customer.cust_govidsearch_count_nonLo";

    String CUSTOMER_PHONE_SEARCH_NONLO = "Customer.cust_phonesearch_nonLo";
    String CUSTOMER_PHONE_SEARCH_COUNT_NONLO = "Customer.cust_phonesearch_count_nonLo";

    String ACCOUNT_ID_SEARCH = "accounts.account_IdSearch";
    String ACCOUNT_ID_SEARCH_NOOFFICEID = "accounts.account_IdSearch_withoutoffice";
    String ACCOUNT_ID_SEARCH_COUNT = "accounts.account_IdSearch_count";
    String ACCOUNT_ID_SEARCH_NOOFFICEID_COUNT = "accounts.account_IdSearch_withoutoffice_count";

    String ACCOUNT_LIST_ID_SEARCH = "accounts.account_list_IdSearch";
    String PERSONNEL_SEARCH_COUNT = "count_search_Personnel";
    String PERSONNEL_SEARCH = "search_Personnel";

    String CENTER_SEARCH = "search_Centers";
    String CENTER_SEARCH_COUNT = "count_search_Centers";
    String GROUP_SEARCHWITH_CENTER = "group_SearchWithCenter";
    String GROUP_SEARCHWITH_CENTER_FOR_ADDING_GROUPMEMBER = "group_SearchWithCenterForAddingGroupMember";
    String GROUP_SEARCH_COUNT_WITH_CENTER = "count_group_SearchWithCenter";
    String GROUP_SEARCH_COUNT_WITH_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithCenterForAddingGroupMember";
    String GROUP_SEARCH_COUNT_WITHOUT_CENTER = "count_group_SearchWithoutCenter";
    String GROUP_SEARCH_COUNT_WITHOUT_CENTER_FOR_ADDING_GROUPMEMBER = "count_group_SearchWithoutCenterForAddingGroupMember";
    String SEARCH_GROUP_CLIENT_COUNT_LO = "Customer.count_cust_for_account";
    String SEARCH_GROUP_CLIENT_LO = "Customer.cust_for_account";
    String SEARCH_GROUP_CLIENT_COUNT = "Customer.cust_count_account_Search";
    String SEARCH_GROUP_CLIENT = "Customer.cust_account_Search";
    String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT = "Customer.count_customersForSavingsAccount";
    String SEARCH_CUSTOMER_FOR_SAVINGS = "Customer.customersForSavingsAccount";
    String SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO = "Customer.count_customersForSavingsAccountNonLO";
    String SEARCH_CUSTOMER_FOR_SAVINGS_NOLO = "Customer.customersForSavingsAccountNonLO";
    String SEARCH_CENTERS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_centers";
    String SEARCH_GROUPS_FOR_LOAN_OFFICER = "Customer.get_loanofficer_groups";
    String GET_ACTIVE_LOAN_OFFICER_UNDER_USER = "get_active_loanofficers_under_office";
    String GET_ACTIVE_BRANCHES = "get_active_offices";
    String SEARCH_GROUP_FOR_GROUP_LOAN_COUNT = "Customer.count_group_for_account";
    String SEARCH_GROUP_FOR_GROUP_LOAN = "Customer.group_account_Search";


    String RETRIEVE_AUDIT_LOG_RECORD = "retrieveAuditLogRecords";

    // check List M2
    String MASTERDATA_CUSTOMER_CHECKLIST = "masterdata.customer_checklist";
    String MASTERDATA_PRODUCT_CHECKLIST = "masterdata.product_checklist";
    String CUSTOMER_VALIDATESTATE = "customer.validateState";
    String PRODUCT_VALIDATESTATE = "product.validateState";
    String LOAD_ALL_CUSTOMER_CHECKLISTS = "checklist.loadAllCustomerCheckLists";
    String LOAD_ALL_ACCOUNT_CHECKLISTS = "checklist.loadAllAccountCheckLists";
    String CHECKLIST_GET_VALID_CUSTOMER_STATES = "checklist.getStatusForCustomer";
    String CHECKLIST_GET_VALID_ACCOUNT_STATES = "checklist.getStatusForAccount";
    String FETCH_ALL_RECURRENCE_TYPES = "meeting.fetchAllReccurenceTypes";

    String ACTIVE_CLIENTS_UNDER_PARENT = "Customer.getActiveClientsUnderParent";
    String ALL_EXCEPT_CANCELLED_CLOSED_CLIENTS_UNDER_PARENT = "Customer.getAllExceptClosedAndCancelledClientsUnderParent";
    String ACTIVE_CLIENTS_UNDER_GROUP = "Customer.getActiveClientsUnderGroup";
    String ALL_EXCEPT_CANCELLED_CLOSED_CLIENTS_WITHOUT_GROUP_FOR_LOAN_OFFICER = "Customer.getAllExceptClosedAndCancelledClientsWithoutGroupForLoanOfficer";

    // holiday handling
    String GET_OFFICE_HOLIDAYS = "holiday.getOfficeHolidays";
    String GET_ALL_HOLIDAYS = "holiday.getAll";
    String GET_APPLICABLE_OFFICES_FOR_HOLIDAYS = "holiday.applicableOffices";
    String GET_TOP_LEVEL_OFFICE_NAMES = "holiday.topLevelOfficeNames";
    String GET_REPAYMENT_RULE_TYPES = "holiday.getRepaymentRuleLabels";
    String GET_REPAYMENT_RULE = "holiday.getRepaymentRule";
    String SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS = "savingsScheduleEntity.getScheduleForIds";
    String SAVING_SCHEDULE_GET_ALL_CUSTOMER_SCHEDULES = "savingsScheduleEntity.getAllCustomerSchedules";
    String LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS = "loanScheduleEntity.getScheduleForIds";
    String GET_HOLIDAYS_BY_FLAG = "holiday.getHoildaysByFlag";

    String GET_ALL_FIELD_CONFIGURATION_LIST = "getAllFieldConfigurationList";

    // surveys
    String SURVEYS_RETRIEVE_ALL = "surveys.retrieveAllSurveys";
    String RESPONSES_RETRIEVE_ALL = "surveys.retrieveAllResponses";
    String SURVEYS_RETRIEVE_BY_TYPE = "surveys.retrieveSurveysByType";
    String SURVEYS_NON_PPI_RETRIEVE_BY_TYPE = "surveys.retrieveNonPPISurveysByType";
    String SURVEYS_RETRIEVE_BY_TYPE_AND_STATE = "surveys.retrieveSurveysByTypeAndState";
    String SURVEYS_RETRIEVE_BY_STATUS = "surveys.retrieveSurveysByStatus";
    String SURVEYS_RETRIEVE_ACTIVE_PPI = "surveys.retrieveActivePPISurvey";
    String SURVEYS_RETRIEVE_PPI_BY_COUNTRY = "surveys.retrievePPISurveyByCountry";
    String SURVEYS_RETRIEVE_ALL_PPI = "surveys.retrieveAllPPISurveys";
    String QUESTIONS_GET_NUM = "questions.getNum";
    String QUESTIONS_RETRIEVE_ALL = "questions.retrieveAll";
    String QUESTIONS_RETRIEVE_BY_STATE = "Question.retrieveByState";
    String QUESTIONS_RETRIEVE_BY_TYPE = "questions.retrieveByAnswerType";
    String QUESTIONS_RETRIEVE_BY_TEXT = "questions.retrieveByText";
    String SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES = "surveys.retrieveCustomersSurveys";
    String SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES = "surveys.retrieveAccountsSurveys";
    String SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER = "surveys.retrieveInstancesByCustomer";
    String SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT = "surveys.retrieveInstancesByAccount";
    String SURVEYINSTANCE_RETRIEVE_BY_SURVEY = "surveys.retrieveInstancesBySurvey";
    String RESPONSES_RETRIEVE_BY_INSTANCE = "surveys.retrieveResponsesByInstance";

    // products mix
    String LOAD_ALL_DEFINED_PRODUCTS_MIX = "productsmix.retrieveAll";
    String LOAD_NOT_ALLOWED_PRODUCTS = "productsmix.loadnotallowedproducts";

    String GET_TOP_LEVEL_ACCOUNT = "COABO.getTopLevelAccount";
    // accepted payment type
    String GET_ACCEPTED_PAYMENT_TYPES_FOR_A_TRANSACTION = "acceptedpaymenttype.getAcceptedPaymentTypesForATransaction";

    String GET_TOP_LEVEL_ACCOUNTS = "COABO.getTopLevelAccounts";
    String GET_ACCOUNT_ID_FOR_GL_CODE = "COABO.getAccountIdForGLCode";
    String GET_ALL_COA = "COABO.getAllCoa";

    // Administrative documents

    String GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllActiveAdministrativeDocument";
    String GET_ALL_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllAdministrativeDocument";
    String GET_MIX_BY_ADMINISTRATIVE_DOCUMENT = "admindocument.getMixByAdministrativeDocument";
    String GET_ACC_ACTION_MIX_BY_ADMINISTRATIVE_DOCUMENT_ID = "admindocument.getAccActionMixByAdministrativeDocumentId";
    String GET_ALL_MIXED_ADMINISTRATIVE_DOCUMENT = "admindocument.getAllMixedAdministrativeDocument";
    String GET_ACTIVE_ADMIN_DOCUMENTS_BY_ACCOUNT_ACTION_ID = "admindocument.getActiveAdminDocumentsByAccountActionId";

    // new queries for issue 1601 to allow backfilling of custom fields
    // on existing database objects like customers, personnel, loans, etc.
    String GET_CUSTOMERS_BY_LEVELID = "Customer.getCustomersByLevelId";
    String GET_ALL_OFFICES_FOR_CUSTOM_FIELD = "office.getAllOfficesForCustomField";
    String GET_ALL_PERSONNEL = "personnel.getAllPersonnel";
    String GET_ALL_LOAN_ACCOUNTS = "accounts.getAllLoanAccounts";
    String GET_ALL_LOAN_ACCOUNTS_WITH_PENALTIES = "accounts.getAllLoanAccountsWithLateInstallments";

    String GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportForDateAndBranch";
    String GET_BRANCH_REPORT_FOR_DATE = "branchReport.getBranchReportForDate";
    String GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportClientSummaryForDateAndBranch";
    String GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsAgingForDateAndBranch";
    String GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanArrearsProfileForDateAndBranch";
    String GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffSummaryForDateAndBranch";
    String GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportStaffingLevelSummaryForDateAndBranch";
    String GET_ACTIVE_BRANCH_MANAGER_UNDER_OFFICE = "personnel.getActiveBranchManagerUnderOffice";
    String GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH = "branchReport.getBranchReportLoanDetailsForDateAndBranch";

    // AGING ARREARS FOR BRANCH REPORT
    String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD = "branchReport.extractBranchReportLoanArrears";

    // STAFF SUMMARY FOR BRANCH REPORT
    String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS = "branchReport.extractStaffSummaryActiveBorrowersLoans";
    String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT = "branchReport.extractStaffSummaryCenterAndClientCount";
    String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING = "branchReport.extractLoanAmountOutstanding";
    String EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR = "branchReport.extractPortfolioAtRisk";
    String EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR = "branchReport.extractPortfolioAtRiskForBranch";
    String EXTRACT_BRANCH_REPORT_LOAN_DETAILS = "branchReport.extractLoanDetailsForBranch";
    String EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS = "branchReport.extractLoansInArrearsForBranch";
    String EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK = "branchReport.extractLoansAndOutstandingAmountsAtRiskForBranch";
    String EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK = "branchReport.extractLoanArrearsProfileClientsAtRiskForBranch";
    String EXTRACT_BRANCH_REPORT_TOTAL_CLIENTS_ENROLLED_BY_PERSONNEL = "branchReport.extractTotalClientsFormedByPersonnel";
    String EXTRACT_BRANCH_REPORT_CLIENTS_ENROLLED_BY_PERSONNEL_THIS_MONTH = "branchReport.extractTotalClientsFormedByPersonnelThisMonth";
    String EXTRACT_BRANCH_REPORT_LOAN_ARREARS_AMOUNT_FOR_PERSONNEL = "branchReport.extractLoanArrearsAmountForPersonnel";

    // Branch Cash Confirmation Report Queries
    String GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES = "branchCashConfirmationReport.getCenterRecoveries";
    String GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES = "branchCashConfirmationReport.getCenterIssues";
    String GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS = "branchCashConfirmationReport.getDisbursements";

    String GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH = "branchCashConfirmationReport.getReportForDateAndBranch";
    String COLLECTION_SHEET_EXTRACT_COLLECTION_SHEET_REPORT_DATA = "collectionSheet.extractCollectionSheetReportData";

    String SCHEDULED_TASK_GET_LATEST_TASK = "scheduledTasks.getLatestTask";
    String SCHEDULED_TASK_GET_SUCCESSFUL_TASK = "scheduledTasks.getSuccessfulTask";

    String GET_ORIGINAL_SCHEDULE_BY_ACCOUNT_ID = "originalLoanScheduleEntity.getScheduleForLoan";
    
    // PENALTIES
    String GET_PENALTY_BY_ID = "penalty.getPenaltyById";
    String GET_LOAN_PENALTIES = "penalty.getLoanPenalties";
    String GET_SAVING_PENALTIES = "penalty.getSavingPenalties";
    String GET_ALL_APPLICABLE_PENALTY_FOR_LOAN_CREATION = "penalty.getAllApplicablePenaltiesForLoanCreation";
    String GET_ALL_APPLICABLE_LOAN_PENALTY = "penalty.getAllApplicablePenaltiesForLoan";

    // Payment types
    String GET_TRANSFER_PAYMENT_TYPE_ID = "paymentType.getTransferId";
}
