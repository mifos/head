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
	public String PRODUCTCATEGORYLIST="ProductCategoryList";
	public String LOCALEID="localeId";
	public String STATUS="status";
	public String PRDCATEGORYSTATUSLIST="PrdCategoryStatusList";
	
	//Product Configuration(Lateness Definition)
	public String GETPATHPRDCONF="ProductConfiguration";
	public Short LOANID=1;
	public Short SAVINGSID=2;
	public String PRODUCTTYPELIST="ProductTypeList";
	public String PRODUCTTYPEID="productTypeID";
	public String PRODUCTCATEGORYSTATUSID="prdCategoryStatusId";
	public String LOANPRODUCTID="loanId";
	public String SAVINGSPRODUCTID="savingsId";
	
	//Product Offering
	public String PRDOFFERINGNAME="prdOfferingName";
	public String PRDOFFERINGSHORTNAME="prdOfferingShortName";
	
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
	
	public Short LOANFRQINSTID =1;
	public Short DEFAULTLOANGRACEPERIODTYPE =1;
	public Short LOANFEESINACTIVEID=1;
	public Short LOANCATEGORYIDVALUE=5;
	public Short LOANACTIVE=1;
	public Short LOANINACTIVE=4;
	
	
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
	
	public Short SAVINGSACTIVE=2;
	public Short SAVINGSINACTIVE=5;
	// PRD APPLICABLE MASTER
	public String OFFERINGAPPLICABLETOCLIENTS = "1";
	public String OFFERINGAPPLICABLETOGROUPS = "2";
	public String OFFERINGAPPLICABLETOCENTERS = "3";
	public String OFFERINGAPPLICABLETOALLCUSTOMERS = "4";
	
	//Interest Types
	public Short DECLININGBALANCEINTEREST = 1;
	public Short FLATINTERST = 2;

	//Change Log
	public Short LOANENTITYTYPEID=2;
	public Short SAVINGSENTITYTYPEID=3;
	public String LOANCHANGELOGLIST="LoanChangeLogList";
	public String SAVINGSCHANGELOGLIST="SavingsChangeLogList";
	
	//Exceptions 
	public String ERRORMAXMINLOANAMOUNT="errors.maxminLoanAmount";
	public String ERRORDEFLOANAMOUNT="errors.defLoanAmount";
	public String ERRORMAXMININTRATE="errors.maxminIntRate";
	public String ERRORDEFINTRATE="errors.defIntRate";
	public String ERRORINTRATE="errors.intRate";
	public String PRDINVALID="errors.invalid";
	public String DUPLCATEGORYNAME="errors.duplcategoryname";
	public String DUPLPRDINSTNAME="errors.duplprdinstname";
	public String DUPLPRDINSTSHORTNAME="errors.duplprdinstshortname";
	public String INVALIDSTARTDATE="errors.startdateexception";
	public String INVALIDENDDATE="errors.enddateexception";
	public String STARTDATEUPDATEEXCEPTION="errors.startdateupdateexception";
	public String ERRORMAXPENALTYRATE="errors.maxpenaltyrate";
	public String ERRORFEEFREQUENCY="errors.feefrequency";
	public String ERRORMANDAMOUNT="errors.mandAmount";
	public String INVALIDFIELD="exceptions.application.productDef.invalidfield";
	
	//GLcodes
	public String PRICIPALGLCODE="principal";
	public String INTERESTGLCODE="interest";
	public String PENALTYGLCODE="penalty";
	public String DEPOSITGLCODE="deposit";
	
}

