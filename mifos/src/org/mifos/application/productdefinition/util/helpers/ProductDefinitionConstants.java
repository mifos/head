/**

 * ProductDefinitionConstants.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.productdefinition.util.helpers;

/**
 * This interface contains all the definition of all constants required for 
 * product definition
 */
public interface ProductDefinitionConstants {
	
	public String METHOD="method";
	public String PREVIEWMETHOD="preview";
	public String LOADMETHOD="load";
	public String CANCELMETHOD="cancel";
	public String GETMETHOD="get";
	public String SEARCHMETHOD="search";
	public String MANAGEMETHOD="manage";
	public String CREATEMETHOD="create";
	public String UPDATEMETHOD="update";
	public String INPUT="input";
	public String INPUTADMIN="admin";
	public String INPUTDETAILS="details";
	public String CHANGELOG="ChangeLog";
	
	public String LOAD="load_success";
	public String MANAGE="manage_success";
	public String CREATEPREVIEW="create_preview";
	public String MANAGEPREVIEW="manage_preview";
	public String CREATE="create_success";
	public String UPDATE="update_success";
	public String ADMINCANCELFORWARD="cancel_admin";
	public String DETAILSCANCELFORWARD="cancel_details";
	public String DETAILSPREVIOUSFORWARD="previous_details";
	public String ADMINPREVIOUSFORWARD="previous_admin";
	public String GET="get_success";
	public String SEARCH="search_success";
	public String SEARCHLOG="search_log_success";
	public String CANCEL="cancel_success";
	
	//Product Category
	public String GETPATHPRODUCTCATEGORY="ProductCategory";
	public String PRODUCTCATEGORYNAME="productCategoryName";
	public String PRODUCTCATEGORYID="productCategoryID";
	public Short DEFAULTMAX=0;
	public String DEFAULTPRINDUELASTINSTFLAG="2";
	public String DEFAULTINTDEDDISBURSEMENTFLAG="2";
	public String DEFAULTRECURFREQUENCY="1";
	public String DEFAULTMINNOINSTALLMENTS="1";
	public String MIFOSPRDDEFACTIONFORM="mifosproddefactionform";
	public String METHODCALLED="methodCalled";
	
	//States for Product Category
	public Short ACTIVE=1;
	public Short INACTIVE=0;
	
	//payment types
	public String GETPATHPMTTYPES="PaymentTypes";

	//process flow
	public String GETPATHPROCESSFLOW="AccountProcess";
	
	//product type for business processor
	public String PRODUCTTYPE="ProductType";
	public String PRODUCTID="prdOfferingId";
	public String NOTALLOWEDPRODUCTID="notAllowedPrdOfferingId";
	public String PRODUCTCATEGORYLIST="ProductCategoryList";
	public String LOCALEID="localeId";
	public String STATUS="status";
	public String PRDCATEGORYSTATUSLIST="PrdCategoryStatusList";
	
	//Product Configuration(Lateness Definition)
	//Product Mix
	public String GETPATHPRDCONF="ProductConfiguration";
	public String PRODUCTTYPELIST="ProductTypeList";
	public String PRODUCTINSTANCELIST="ProductInstanceList";
	public String ALLOWEDPRODUCTLIST="AllowedProductList";
	public String SELECTEDACCOUNTSTATUS="SelectedAccountStatus";
	public String NOTSELECTEDACCOUNTSTATUS="NotSelectedAccountStatus";
	public String STATUS_LIST="statusList";
		
	public String NOTALLOWEDPRODUCTLIST="NotAllowedProductList";
	public String OLDNOTALLOWEDPRODUCTLIST="OldNotAllowedProductList";
	
	public String TABALLOWED="tabAllowed";
	public String SHOWMESSAGE="showMessage";
	
	public String PRODUCTMIXLIST="ProductMixList";

	public String PRODUCTTYPEID="productTypeID";
	public String PRODUCTCATEGORYSTATUSID="prdCategoryStatusId";
	public String LOANPRODUCTID="loanId";
	public String SAVINGSPRODUCTID="savingsId";
	
	//Product Offering
	public String PRDOFFERINGNAME="prdOfferingName";
	public String PRDOFFERINGSHORTNAME="prdOfferingShortName";
	public String PRODUCTOFFERINGLIST="productOfferingList";
	public String OLDPRODUCTOFFERINGLIST="oldProductOfferingList";
	public String NOTALLOWEDPRODUCTOFFERINGLIST="notAllowedproductOfferingList";
	
	//Loan Product

	public String GETPATHLOANPRODUCT="LoanProduct";
	public String LOANPRODUCTCATEGORYLIST="LoanProductCategoryList";
	public String LOANAPPLFORLIST="LoanApplForList";
	public String LOANGRACEPERIODTYPELIST="LoanGracePeriodTypeList";
	public String YESNOMASTERLIST="YesNoMasterList";
	public String INTERESTTYPESLIST="InterestTypesList";
	public String INTERESTCALCRULELIST="InterestCalcRuleList";
	public String PENALTYTYPESLIST="PenaltyTypesList";
	public String LOANPRDSTATUSLIST="LoanPrdStatusList";
	public String LOANPRODUCTLIST="LoanProductList";
	public String LOANPRODUCTNAME="loanProductName";
	public String SRCFUNDSLIST="SrcFundsList";
	public String LOANFEESLIST="LoanFeesList";
	public String LOANCATEGORYID="loanCategoryId";
	public String LOANFEESSTATUS="feeStatus";
	public String OFFERINGAPPLICENTERSID="offerinApplicableCentersId";
	public String LOANPRDACTIONFORM="loanprdactionform";
	public String LOANPRDACTIONDEFAMOUNT="Default amount";
	public String LOANPRDACTIONMAXAMOUNT="max amount";
	public String LOANPRDACTIONDEFINTERESTRATE="Default interest rate";
	public String LOANPRDACTIONMAXINTERESTRATE="max interest rate";
	public String LOANAMOUNTTYPE = "loanAmountType";
	public String INSTALLTYPE = "installType";
	public String PRDOFFERINGBO = "prdOfferingBO";
	
	public Short LOANFRQINSTID =1;
	public Short LOANFEESINACTIVEID=1;
	public Short LOANCATEGORYIDVALUE=5;
	
	public Short LOANAMOUNTTYPE_UNKNOWN =0;
	public Short LOANAMOUNTSAMEFORALLLOAN =1;
	public Short LOANAMOUNTFROMLASTLOAN = 2;
	public Short LOANAMOUNTFROMLOANCYCLE =3;
	
	public Short NOOFINSTALL_UNKNOWN =0;
	public Short NOOFINSTALLSAMEFORALLLOAN =1;
	public Short NOOFINSTALLFROMLASTLOAN = 2;
	public Short NOOFINSTALLFROMLOANCYCLLE =3;
	//Savings Product

	public String GETPATHSAVINGSPRODUCT="SavingsProduct";
	public String SAVINGSPRODUCTCATEGORYLIST="SavingsProductCategoryList";
	public String SAVINGSAPPLFORLIST="SavingsApplForList";
	public String SAVINGSTYPELIST="SavingsTypesList";
	public String RECAMNTUNITLIST="RecAmntUnitList";
	public String INTCALCTYPESLIST="IntCalcTypesList";
	public String SAVINGSPRODUCTNAME="savingsProductName";
	public String SAVINGSPRODUCTLIST="SavingsProductList";
	public String PRDOFFERINGID="prdOfferingId";
	public String SAVINGSPRDSTATUSLIST="SavingsPrdStatusList";
	public String SAVINGSPRDACTIONFORM="savingsprdactionform";
	public String SAVINGSRECURRENCETYPELIST="SavingsRecurrenceTypeList";
	
	//Change Log
	public Short LOANENTITYTYPEID=2;
	public Short SAVINGSENTITYTYPEID=3;
	public String LOANCHANGELOGLIST="LoanChangeLogList";
	public String SAVINGSCHANGELOGLIST="SavingsChangeLogList";
	
	//Exceptions 
	public String ERRORMAXMINLOANAMOUNT="errors.maxminLoanAmount";
	public String ERRORMAXMINNOOFINSTALL="errors.maxminnoofinstall";
	public String ERRORDEFLOANAMOUNT="errors.defLoanAmount";
	public String ERRORMAXMININTRATE="errors.maxminIntRate";
	public String ERRORDEFINTRATE="errors.defIntRate";
	public String ERRORINTRATE="errors.intRateValue";
	public String PRDINVALID="errors.invalid";
	public String DUPLICATE_CATEGORY_NAME="errors.duplcategoryname";
	public String DUPLPRDINSTNAME="errors.duplprdinstname";
	public String DUPLPRDINSTSHORTNAME="errors.duplprdinstshortname";
	public String INVALIDSTARTDATE="errors.startdateexception";
	public String INVALIDENDDATE="errors.enddateexception";
	public String STARTDATEUPDATEEXCEPTION="errors.startdateupdateexception";
	public String ERRORMAXPENALTYRATE="errors.maxpenaltyrate";
	public String ERRORFEEFREQUENCY="errors.feefrequency";
	public String ERRORMANDAMOUNT="errors.mandAmount";
	public String INVALIDFIELD="exceptions.application.productDef.invalidfield";
	public String ERROR_MANDATORY = "errors.mandatory";
	public String DECLINEINTERESTDISBURSEMENTDEDUCTION =
		"exceptions.declineinterestdisbursementdeduction";
	public String ERROR_SELECT = "errors.select";
	public String ERROR_CREATE = "errors.create";
	public String ERRORSSELECTCONFIG="errors.selectconfig";
	public String ERRORSENTERCONFIG="errors.mandatoryconfig";
	public String ERRORSDEFMINMAXCONFIG="errors.defMinMaxconfig";
	public String ERRORSMINMAXINTCONFIG="errors.maxminIntRateconfig";
	public String ERRORSDEFINTCONFIG="errors.defIntRateconfig";
	public String ERRORS_RANGE="errors.defMinMax";

	public String BALANCE_INTEREST = "Balance used for ";
	public String CALCULATION = "calculation";
	public String TIME_PERIOD = "Time period for ";
	public String FREQUENCY = "Frequency of ";
	public String POSTING_ACCOUNTS = "posting to accounts";
	public String RECUR_AFTER = "Recur every";
	public String GLCODE_FOR = "GL code for ";
	
	//GLcodes
	public String PRICIPALGLCODE="principal";
	public String INTERESTGLCODE="interest";
	public String PENALTYGLCODE="penalty";
	public String DEPOSITGLCODE="deposit";
	public String SAVINGSPRODUCTACTIONFORM="savingsproductactionform";
	public String SAVINGSDEPOSITGLCODELIST="depositGLCodes";
	public String SAVINGSINTERESTGLCODELIST="interestGLCodes";
	public String SAVINGSPRDGLOBALOFFERINGNUM="savingsprdglobalofferingnum";
	public String LOANPRICIPALGLCODELIST="principalGLCodes";
	public String LOANINTERESTGLCODELIST="interestGLCodes";
	public String LOANPRODUCTACTIONFORM="loanproductactionform";
	public String LOANPRDGLOBALOFFERINGNUM="loanprdglobalofferingnum";
	public String LOANPRDFEESELECTEDLIST="loanprdfeeselectedlist";
	public String LOANPRDFUNDSELECTEDLIST="loanprdfundselectedlist";
	public String LOANPRDFEE="loanprdfee";
	public String RATETYPE="rate type";
	public String RATE="rate";
	public String MAX="Max";
	public String MIN="Min";
	public String DEFAULT="Default ";
	public String LOANPRDSTARTDATE="LoanPrdstartDate";
	//product configuration
	public String LATENESS_DAYS = "lateness_days";
	public String DORMANCY_DAYS = "dormancy_days";
	public String DORMANCYDAYS ="dormancy days";
	public String LATENESSDAYS = "lateness days";

	public String MAX_DAYS="product.maxDays";
	public String ERROR_MAX_DAYS="errors.maxValue";
	
	public String PRODUCT_TYPE="product type";
	public String PRODUCT_INSTANCE_NAME = "product instance name";
	
	public String ERRORMINIMUMLOANAMOUNT="errors.minloanamount";
	public String ERRORMAXIMUMLOANAMOUNT="errors.maxloanamount";
	public String ERRORDEFAULTLOANAMOUNT="errors.defaultloanamount";
	public String ERRORSTARTRANGELOANAMOUNT="errors.startloanamount";
	public String ERRORENDLOANAMOUNT="errors.endloanamount";
	public String ERRORCALCLOANAMOUNTTYPE="errors.calcloanamounttype";
	public String ERRORSTARTENDLOANAMOUNT="errors.startendrangeloanamount";	
	
	public String ERRORMINIMUMINSTALLMENT="errors.mininstallments";
	public String ERRORMAXIMUMINSTALLMENT="errors.maxinstallments";
	public String ERRORDEFAULTINSTALLMENT="errors.definstallments";
	public String ERRORSTARTRANGEINSTALLMENT="errors.startinstallment";
	public String ERRORENDINSTALLMENT="errors.endinstallment";
	public String ERRORCALCINSTALLMENTTYPE="errors.calcinstallmenttype";
	public String ERRORSTARTENDINSTALLMENT="errors.startendinstallment";
	public String ERRORMINMAXDEFINSTALLMENT="errors.defaultinstallments";
	public static final String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE="errors.principallast_invalidgrace";
	public String SAMEFORALLLOANS="product.sameforallloans";
	public String FORBYLASTLOANATROW="product.forbylastloanatrow";
	public String FORBYLOANCYCLEATROW="product.forbyloancycleatrow";
	public String INVALIDRATE="errors.intRate";
	public String FORNUMBEROFLASTLOLANINSTALLMENTATROW="product.fornumberoflastloaninstallmentatrow";
	
	public String ERRORMINIMUMLOANAMOUNTINVALIDFORMAT="errors.minloanamountinvalidformat";
	public String ERRORMAXIMUMLOANAMOUNTINVALIDFORMAT="errors.maxloanamountinvalidformat";
	public String ERRORDEFAULTLOANAMOUNTINVALIDFORMAT="errors.defaultloanamountinvalidformat";
	
	public String ERRORMININTERESTINVALIDFORMAT="errors.mininterestinvalidformat";
	public String ERRORMAXINTERESTINVALIDFORMAT="errors.maxinterestinvalidformat";
	public String ERRORDEFINTERESTINVALIDFORMAT="errors.definterestinvalidformat";
	
	public String EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR="EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR";
	public String EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR="EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR";
	public String NOT_ALL_NUMBER="NOT_ALL_NUMBER";
	public String CONVERSION_ERROR="CONVERSION_ERROR";
}





