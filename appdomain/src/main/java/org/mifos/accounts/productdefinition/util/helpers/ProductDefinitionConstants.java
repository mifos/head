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

package org.mifos.accounts.productdefinition.util.helpers;

/**
 * This public interface contains all the definition of all constants required for
 * product definition
 */
public interface ProductDefinitionConstants {

    String METHOD = "method";
    String PREVIEWMETHOD = "preview";
    String LOADMETHOD = "load";
    String CANCELMETHOD = "cancel";
    String GETMETHOD = "get";
    String SEARCHMETHOD = "search";
    String MANAGEMETHOD = "manage";
    String CREATEMETHOD = "create";
    String UPDATEMETHOD = "update";
    String INPUT = "input";
    String INPUTADMIN = "admin";
    String INPUTDETAILS = "details";
    String CHANGELOG = "ChangeLog";

    String LOAD = "load_success";
    String MANAGE = "manage_success";
    String CREATEPREVIEW = "create_preview";
    String MANAGEPREVIEW = "manage_preview";
    String CREATE = "create_success";
    String UPDATE = "update_success";
    String ADMINCANCELFORWARD = "cancel_admin";
    String DETAILSCANCELFORWARD = "cancel_details";
    String DETAILSPREVIOUSFORWARD = "previous_details";
    String ADMINPREVIOUSFORWARD = "previous_admin";
    String GET = "get_success";
    String SEARCH = "search_success";
    String SEARCHLOG = "search_log_success";
    String CANCEL = "cancel_success";

    // Product Category
    String GETPATHPRODUCTCATEGORY = "ProductCategory";
    String PRODUCTCATEGORYNAME = "productCategoryName";
    String PRODUCTCATEGORYID = "productCategoryID";
    Short DEFAULTMAX = 0;
    String DEFAULTPRINDUELASTINSTFLAG = "2";
    String DEFAULTINTDEDDISBURSEMENTFLAG = "2";
    String DEFAULTRECURFREQUENCY = "1";
    String DEFAULTMINNOINSTALLMENTS = "1";
    String MIFOSPRDDEFACTIONFORM = "mifosproddefactionform";
    String METHODCALLED = "methodCalled";

    // States for Product Category
    Short ACTIVE = 1;
    Short INACTIVE = 0;

    // payment types
    String GETPATHPMTTYPES = "PaymentTypes";

    // process flow
    String GETPATHPROCESSFLOW = "AccountProcess";

    // product type for business processor
    String PRODUCTTYPE = "ProductType";
    String PRODUCTID = "prdOfferingId";
    String NOTALLOWEDPRODUCTID = "notAllowedPrdOfferingId";
    String PRODUCTCATEGORYLIST = "ProductCategoryList";
    String LOCALEID = "localeId";
    String STATUS = "status";
    String PRDCATEGORYSTATUSLIST = "PrdCategoryStatusList";

    // Product Configuration(Lateness Definition)
    // Product Mix
    String GETPATHPRDCONF = "ProductConfiguration";
    String PRODUCTTYPELIST = "ProductTypeList";
    String PRODUCTINSTANCELIST = "ProductInstanceList";
    String ALLOWEDPRODUCTLIST = "AllowedProductList";
    String SELECTEDACCOUNTSTATUS = "SelectedAccountStatus";
    String AVAILABLEACCOUNTSTATUS = "AvailableAccountStatus";
    String NOTSELECTEDACCOUNTSTATUS = "NotSelectedAccountStatus";
    String STATUS_LIST = "statusList";

    String NOTALLOWEDPRODUCTLIST = "NotAllowedProductList";
    String OLDNOTALLOWEDPRODUCTLIST = "OldNotAllowedProductList";

    String TABALLOWED = "tabAllowed";
    String SHOWMESSAGE = "showMessage";

    String PRODUCTMIXLIST = "ProductMixList";

    String PRODUCTTYPEID = "productTypeID";
    String PRODUCTCATEGORYSTATUSID = "prdCategoryStatusId";
    String LOANPRODUCTID = "loanId";
    String SAVINGSPRODUCTID = "savingsId";

    // Product Offering
    String PRDOFFERINGNAME = "prdOfferingName";
    String PRDOFFERINGSHORTNAME = "prdOfferingShortName";
    String PRODUCTOFFERINGLIST = "productOfferingList";
    String OLDPRODUCTOFFERINGLIST = "oldProductOfferingList";
    String NOTALLOWEDPRODUCTOFFERINGLIST = "notAllowedproductOfferingList";

    // Loan Product

    String GETPATHLOANPRODUCT = "LoanProduct";
    String LOANPRODUCTCATEGORYLIST = "LoanProductCategoryList";
    String LOANAPPLFORLIST = "LoanApplForList";
    String LOANGRACEPERIODTYPELIST = "LoanGracePeriodTypeList";
    String YESNOMASTERLIST = "YesNoMasterList";
    String INTERESTTYPESLIST = "InterestTypesList";
    String INTERESTCALCRULELIST = "InterestCalcRuleList";
    String PENALTYTYPESLIST = "PenaltyTypesList";
    String LOANPRDSTATUSLIST = "LoanPrdStatusList";
    String LOANPRODUCTLIST = "LoanProductList";
    String LOANPRODUCTNAME = "loanProductName";
    String SRCFUNDSLIST = "SrcFundsList";
    String LOANFEESLIST = "LoanFeesList";
    String LOANPENALTIESLIST = "LoanPenaltiesList";
    String LOANCATEGORYID = "loanCategoryId";
    String LOANFEESSTATUS = "feeStatus";
    String OFFERINGAPPLICENTERSID = "offerinApplicableCentersId";
    String LOANPRDACTIONFORM = "loanprdactionform";
    String LOANPRDACTIONDEFAMOUNT = "Default amount";
    String LOANPRDACTIONMAXAMOUNT = "max amount";
    String LOANPRDACTIONDEFINTERESTRATE = "Default interest rate";
    String LOANPRDACTIONMAXINTERESTRATE = "max interest rate";
    String LOANAMOUNTTYPE = "loanAmountType";
    String INSTALLTYPE = "installType";
    String PRDOFFERINGBO = "prdOfferingBO";

    Short LOANFRQINSTID = 1;
    Short LOANFEESINACTIVEID = 1;
    Short LOANCATEGORYIDVALUE = 5;

    Short LOANAMOUNTTYPE_UNKNOWN = 0;
    Short LOANAMOUNTSAMEFORALLLOAN = 1;
    Short LOANAMOUNTFROMLASTLOAN = 2;
    Short LOANAMOUNTFROMLOANCYCLE = 3;

    Short NOOFINSTALL_UNKNOWN = 0;
    Short NOOFINSTALLSAMEFORALLLOAN = 1;
    Short NOOFINSTALLFROMLASTLOAN = 2;
    Short NOOFINSTALLFROMLOANCYCLLE = 3;
    // Savings Product

    String GETPATHSAVINGSPRODUCT = "SavingsProduct";
    String SAVINGSPRODUCTCATEGORYLIST = "SavingsProductCategoryList";
    String SAVINGSAPPLFORLIST = "SavingsApplForList";
    String SAVINGSTYPELIST = "SavingsTypesList";
    String RECAMNTUNITLIST = "RecAmntUnitList";
    String INTCALCTYPESLIST = "IntCalcTypesList";
    String SAVINGSPRODUCTNAME = "savingsProductName";
    String SAVINGSPRODUCTLIST = "SavingsProductList";
    String PRDOFFERINGID = "prdOfferingId";
    String SAVINGSPRDSTATUSLIST = "SavingsPrdStatusList";
    String SAVINGSPRDACTIONFORM = "savingsprdactionform";
    String SAVINGSRECURRENCETYPELIST = "SavingsRecurrenceTypeList";

    // Change Log
    Short LOANENTITYTYPEID = 2;
    Short SAVINGSENTITYTYPEID = 3;
    String LOANCHANGELOGLIST = "LoanChangeLogList";
    String SAVINGSCHANGELOGLIST = "SavingsChangeLogList";

    // Exceptions
    String ERRORMAXMINLOANAMOUNT = "errors.maxminLoanAmount";
    String ERRORMAXMINNOOFINSTALL = "errors.maxminnoofinstall";
    String ERRORDEFLOANAMOUNT = "errors.defLoanAmount";
    String ERRORMAXMININTRATE = "errors.maxminIntRate";
    String ERRORDEFINTRATE = "errors.defIntRate";
    String ERRORINTRATE = "errors.intRateValue";
    String PRDINVALID = "errors.invalid";
    String DUPLICATE_CATEGORY_NAME = "errors.duplcategoryname";
    String DUPLPRDINSTNAME = "errors.duplprdinstname";
    String DUPLPRDINSTSHORTNAME = "errors.duplprdinstshortname";
    String INVALIDSTARTDATE = "errors.startdateexception";
    String INVALIDSTARTDATEWITHBACKDATE = "errors.startdatewithbackdateexception";
    String INVALIDENDDATE = "errors.enddateexception";
    String STARTDATEUPDATEEXCEPTION = "errors.startdateupdateexception";
    String ERRORMAXPENALTYRATE = "errors.maxpenaltyrate";
    String ERRORFEEFREQUENCY = "errors.feefrequency";
    String ERROR_FEE_CURRENCY_MATCH = "errors.feeCurrencyMatch";
    String ERRORMANDAMOUNT = "errors.mandAmount";
    String INVALIDFIELD = "exceptions.application.productDef.invalidfield";
    String ERROR_MANDATORY = "errors.mandatory";
    String DECLINEINTERESTDISBURSEMENTDEDUCTION = "exceptions.declineinterestdisbursementdeduction";
    String ERROR_SELECT = "errors.select";
    String ERROR_CREATE = "errors.create";
    String ERRORSSELECTCONFIG = "errors.selectconfig";
    String ERRORS_MANDATORY_MIN_INTEREST = "errors.mandatoryMinInterestRate";
    String ERRORS_MANDATORY_MAX_INTEREST = "errors.mandatoryMaxInterestRate";
    String ERRORS_MANDATORY_DEFAULT_INTEREST = "errors.mandatoryDefaultInterestRate";
    String ERRORSDEFMINMAXCONFIG = "errors.defMinMaxconfig";
    String ERRORSMINMAXINTCONFIG = "errors.maxminIntRateconfig";
    String ERRORSDEFINTCONFIG = "errors.defIntRateconfig";
    String ERRORS_RANGE = "errors.defMinMax";

    String BALANCE_INTEREST = "Balance used for ";
    String CALCULATION = "calculation";
    String TIME_PERIOD = "Time period for ";
    String FREQUENCY = "Frequency of ";
    String POSTING_ACCOUNTS = "posting to accounts";
    String RECUR_AFTER = "Recur every";
    String GLCODE_FOR = "GL code for ";

    // GLcodes
    String PRICIPALGLCODE = "principal";
    String INTERESTGLCODE = "interest";
    String PENALTYGLCODE = "penalty";
    String DEPOSITGLCODE = "deposit";
    String SAVINGSPRODUCTACTIONFORM = "savingsproductactionform";
    String SAVINGSDEPOSITGLCODELIST = "depositGLCodes";
    String SAVINGSINTERESTGLCODELIST = "interestGLCodes";
    String SAVINGSPRDGLOBALOFFERINGNUM = "savingsprdglobalofferingnum";
    String LOANPRICIPALGLCODELIST = "principalGLCodes";
    String LOANINTERESTGLCODELIST = "interestGLCodes";
    String LOANPRODUCTACTIONFORM = "loanproductactionform";
    String LOANPRDGLOBALOFFERINGNUM = "loanprdglobalofferingnum";
    String LOANPRDFEESELECTEDLIST = "loanprdfeeselectedlist";
    String LOANPRDPENALTYSELECTEDLIST = "loanprdpenaltyselectedlist";
    String LOANPRDFUNDSELECTEDLIST = "loanprdfundselectedlist";
    String LOANPRDFEE = "loanprdfee";
    String LOANPRDPENALTY = "loanprdpenalty";
    String RATETYPE = "rate type";
    String RATE = "rate";
    String MAX = "Max";
    String MIN = "Min";
    String DEFAULT = "Default ";
    String LOANPRDSTARTDATE = "LoanPrdstartDate";
    // product configuration
    String LATENESS_DAYS = "lateness_days";
    String DORMANCY_DAYS = "dormancy_days";
    String DORMANCYDAYS = "dormancy days";
    String LATENESSDAYS = "lateness days";

    String MAX_DAYS = "product.maxDays";
    String ERROR_MAX_DAYS = "errors.maxValue";

    String PRODUCT_TYPE = "product type";
    String PRODUCT_INSTANCE_NAME = "product instance name";

    String ERRORMINIMUMLOANAMOUNT = "errors.minloanamount";
    String ERRORMAXIMUMLOANAMOUNT = "errors.maxloanamount";
    String ERRORDEFAULTLOANAMOUNT = "errors.defaultloanamount";
    String ERRORSTARTRANGELOANAMOUNT = "errors.startloanamount";
    String ERRORENDLOANAMOUNT = "errors.endloanamount";
    String ERRORCALCLOANAMOUNTTYPE = "errors.calcloanamounttype";
    String ERRORSTARTENDLOANAMOUNT = "errors.startendrangeloanamount";

    String ERRORMINIMUMINSTALLMENT = "errors.mininstallments";
    String ERRORMAXIMUMINSTALLMENT = "errors.maxinstallments";
    String ERRORDEFAULTINSTALLMENT = "errors.definstallments";
    String ERRORSTARTRANGEINSTALLMENT = "errors.startinstallment";
    String ERRORENDINSTALLMENT = "errors.endinstallment";
    String ERRORCALCINSTALLMENTTYPE = "errors.calcinstallmenttype";
    String ERRORSTARTENDINSTALLMENT = "errors.startendinstallment";
    String ERRORMINMAXDEFINSTALLMENT = "errors.defaultinstallments";
    String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE = "errors.principallast_invalidgrace";
    String SAMEFORALLLOANS = "product.sameforallloans";
    String FORBYLASTLOANATROW = "product.forbylastloanatrow";
    String FORBYLOANCYCLEATROW = "product.forbyloancycleatrow";
    String INVALIDRATE = "errors.intRate";
    String FORNUMBEROFLASTLOLANINSTALLMENTATROW = "product.fornumberoflastloaninstallmentatrow";
    String DEFAULTNOOFINSTALLMENTS = "product.definst";
    String MINIMUMNOOFINSTALLMENTS = "product.mininst";
    String MAXIMUMNOOFINSTALLMENTS = "product.maxinst";

    String ERRORMINIMUMLOANAMOUNTINVALIDFORMAT = "errors.minloanamountinvalidformat";
    String ERRORMAXIMUMLOANAMOUNTINVALIDFORMAT = "errors.maxloanamountinvalidformat";
    String ERRORDEFAULTLOANAMOUNTINVALIDFORMAT = "errors.defaultloanamountinvalidformat";

    String ERRORMININTERESTINVALIDFORMAT = "errors.mininterestinvalidformat";
    String ERRORMAXINTERESTINVALIDFORMAT = "errors.maxinterestinvalidformat";
    String ERRORDEFINTERESTINVALIDFORMAT = "errors.definterestinvalidformat";

    String EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_CASHFLOW_THRESHOLD = "EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_CASHFLOW_THRESHOLD";
    String EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_CASHFLOW_THRESHOLD = "EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_CASHFLOW_THRESHOLD";

    String EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST = "EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST";
    String EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST = "EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST";
    String NOT_ALL_NUMBER = "NOT_ALL_NUMBER";
    String CONVERSION_ERROR = "CONVERSION_ERROR";

    //Create Saving Products
    String MANDATORY_AMOUNT_FOR_DEPOSIT_KEY = "product.mandamntdep";
    String RECOMMENDED_AMOUNT_FOR_DEPOSIT_KEY = "product.recamtdep";
    String MAX_AMOUNT_WITHDRAWL_KEY = "product.maxamtwid";
    String MIN_BALANCE_FOR_CALC_KEY = "product.minBalForCalc";
    String ERROR_MUST_NOT_BE_NEGATIVE = "errors.mustNotBeNegative";
    String SRCQGLIST = "SrcQGList";
    String SELECTEDQGLIST = "SelectedQGList";
    String MIN_GAP_MORE_THAN_MAX_GAP_FOR_VARIABLE_INSTALLMENT_PRODUCT = "errors.minGapMoreThanMaxGapForVariableInstallmentProduct";
    String VARIABLE_INSTALLMENT_MIN_GAP_NEGATIVE_OR_ZERO = "errors.variableInstallmentMinGapNegativeOrZero";
    String VARIABLE_INSTALLMENT_MAX_GAP_NEGATIVE_OR_ZERO = "errors.variableInstallmentMaxGapNegativeOrZero";
    String VARIABLE_INSTALLMENT_MIN_GAP_NOT_PROVIDED = "errors.variableInstallmentMinGapNotProvided";
    String VARIABLE_INSTALLMENT_MIN_GAP_MORE_THAN_ALLOWED = "errors.variableInstallmentMinGapMoreThanAllowed";
    String VARIABLE_INSTALLMENT_MAX_GAP_MORE_THAN_ALLOWED = "errors.variableInstallmentMaxGapMoreThanAllowed";
    String VARIABLE_INSTALLMENT_MIN_AMOUNT_INVALID_FORMAT = "errors.minInstallmentAmountInvalidFormat";
    int MAX_ALLOWED_INSTALLMENT_GAP = 999;
    String CASHFLOW_THRESHOLD_INVALID = "errors.cashFlowThresholdInvalid";
    String CASHFLOW_WARNING_THRESHOLD_INVALID_FORMAT = "errors.cashflowwarninginvalidformat";
    String INDEBTEDNESS_RATIO_INVALID_FORMAT = "errors.indebtednessRatioInvalidFormat";
    String INDEBTEDNESS_RATIO_INVALID = "errors.indebtednessRatioInvalid";
    String REPAYMENT_CAPACITY_INVALID_FORMAT = "errors.repaymentCapacityInvalidFormat";
    String REPAYMENT_CAPACITY_INVALID = "errors.repaymentCapacityInvalid";
    String INVALID_INTEREST_TYPE_FOR_VARIABLE_INSTALLMENT = "errors.interestTypeInvalidForVariableInstallment";
    String PERIODIC_FEE_NOT_APPLICABLE = "errors.periodicFeeNotApplicableForVariableInstallment";
    String MULTIPLE_ONE_TIME_FEES_NOT_ALLOWED = "errors.multipleInstancesOfTheSameOneTimeFeeNotAllowed";    
    String FEE_WITH_PERCENT_INTEREST_NOT_APPLICABLE = "errors.feeCaliculatedAsPercentOfInterestCannotBeAppliedToVariableInstallmentLoan";
    String INVALID_INTEREST_TYPE_FOR_GRACE_PERIODS = "errors.interestTypeInvalidForGracePeriods";
}
