/**

 * LoanPrdActionForm.java    version: 1.0

 

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
package org.mifos.application.productdefinition.struts.actionforms;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class LoanPrdActionForm extends BaseActionForm {
	private final MifosLogger logger;

	private String prdOfferingId;

	private String prdOfferingName;

	private String prdOfferingShortName;

	private String description;

	private String prdCategory;

	private String startDate;

	private String endDate;

	private String prdApplicableMaster;

	private String loanCounter;

	private Double minLoanAmount;

	private Double maxLoanAmount;

	private Double defaultLoanAmount;

	private String interestTypes;

	private String maxInterestRate;

	private String minInterestRate;

	private String defInterestRate;

	private String freqOfInstallments;

	private String recurAfter;

	private String maxNoInstallments;

	private String minNoInstallments;

	private String defNoInstallments;

	private String intDedDisbursementFlag;

	private String prinDueLastInstFlag;

	private String gracePeriodType;

	private String gracePeriodDuration;

	private String[] prdOfferinFees;

	private String[] loanOfferingFunds;

	private String principalGLCode;

	private String interestGLCode;

	private String prdStatus;

	private String loanAmtCalcType;

 	private Double  lastLoanMinLoanAmt1;
	private Double  lastLoanMinLoanAmt2;
	private Double  lastLoanMinLoanAmt3;
	private Double  lastLoanMinLoanAmt4;
	private Double  lastLoanMinLoanAmt5;
	private Double  lastLoanMinLoanAmt6;	

	private Double  lastLoanMaxLoanAmt1;
	private Double  lastLoanMaxLoanAmt2;
	private Double  lastLoanMaxLoanAmt3;
	private Double  lastLoanMaxLoanAmt4;
	private Double  lastLoanMaxLoanAmt5;
	private Double  lastLoanMaxLoanAmt6;

	private Double  lastLoanDefaultLoanAmt1;
	private Double  lastLoanDefaultLoanAmt2;
	private Double  lastLoanDefaultLoanAmt3;
	private Double  lastLoanDefaultLoanAmt4;
	private Double  lastLoanDefaultLoanAmt5;
	private Double  lastLoanDefaultLoanAmt6;

	private Double  cycleLoanMinLoanAmt1;
	private Double  cycleLoanMinLoanAmt2;
	private Double  cycleLoanMinLoanAmt3;
	private Double  cycleLoanMinLoanAmt4;
	private Double  cycleLoanMinLoanAmt5;
	private Double  cycleLoanMinLoanAmt6;

	private Double  cycleLoanMaxLoanAmt1; 
	private Double  cycleLoanMaxLoanAmt2;
	private Double  cycleLoanMaxLoanAmt3;
	private Double  cycleLoanMaxLoanAmt4;
	private Double  cycleLoanMaxLoanAmt5;
	private Double  cycleLoanMaxLoanAmt6;

	private Double  cycleLoanDefaultLoanAmt1;
	private Double  cycleLoanDefaultLoanAmt2;
	private Double  cycleLoanDefaultLoanAmt3;
	private Double  cycleLoanDefaultLoanAmt4;
	private Double  cycleLoanDefaultLoanAmt5;
	private Double  cycleLoanDefaultLoanAmt6;

	private Double  startRangeLoanAmt1;
	private Double  startRangeLoanAmt2;
	private Double  startRangeLoanAmt3 ;
	private Double  startRangeLoanAmt4 ;
	private Double  startRangeLoanAmt5 ;
	private Double  startRangeLoanAmt6 ;

	private Double  endRangeLoanAmt1 ;
	private Double  endRangeLoanAmt2 ;
	private Double  endRangeLoanAmt3 ;
	private Double  endRangeLoanAmt4 ;
	private Double  endRangeLoanAmt5 ;
	private Double  endRangeLoanAmt6 ;	

	private String minLoanInstallment1;
	private String minLoanInstallment2;
	private String minLoanInstallment3;
	private String minLoanInstallment4;
	private String minLoanInstallment5;
	private String minLoanInstallment6;

	private String maxLoanInstallment1;
	private String maxLoanInstallment2;
	private String maxLoanInstallment3;
	private String maxLoanInstallment4;
	private String maxLoanInstallment5;
	private String maxLoanInstallment6;

	private String defLoanInstallment1;
	private String defLoanInstallment2;
	private String defLoanInstallment3;
	private String defLoanInstallment4;
	private String defLoanInstallment5;
	private String defLoanInstallment6;

	private String minCycleInstallment1;
	private String minCycleInstallment2;
	private String minCycleInstallment3;
	private String minCycleInstallment4;
	private String minCycleInstallment5;
	private String minCycleInstallment6;

	private String maxCycleInstallment1;
	private String maxCycleInstallment2;
	private String maxCycleInstallment3;
	private String maxCycleInstallment4;
	private String maxCycleInstallment5;
	private String maxCycleInstallment6;

	private String defCycleInstallment1;
	private String defCycleInstallment2;
	private String defCycleInstallment3;
	private String defCycleInstallment4;
	private String defCycleInstallment5;
	private String defCycleInstallment6;

	private Double startInstallmentRange1;
	private Double startInstallmentRange2;
	private Double startInstallmentRange3;
	private Double startInstallmentRange4;
	private Double startInstallmentRange5;
	private Double startInstallmentRange6;

	private Double endInstallmentRange1;
	private Double endInstallmentRange2;
	private Double endInstallmentRange3;
	private Double endInstallmentRange4;
	private Double endInstallmentRange5;
	private Double endInstallmentRange6;

	private String calcInstallmentType;

	public String getCalcInstallmentType() {
		return calcInstallmentType;
	}

	public void setCalcInstallmentType(String calcInstallmentType) {
		this.calcInstallmentType = calcInstallmentType;
	}

	public Double getCycleLoanDefaultLoanAmt1() {
		return cycleLoanDefaultLoanAmt1;
	}

	public void setCycleLoanDefaultLoanAmt1(Double cycleLoanDefaultLoanAmt1) {
		this.cycleLoanDefaultLoanAmt1 = cycleLoanDefaultLoanAmt1;
	}

	public Double getCycleLoanDefaultLoanAmt2() {
		return cycleLoanDefaultLoanAmt2;
	}

	public void setCycleLoanDefaultLoanAmt2(Double cycleLoanDefaultLoanAmt2) {
		this.cycleLoanDefaultLoanAmt2 = cycleLoanDefaultLoanAmt2;
	}

	public Double getCycleLoanDefaultLoanAmt3() {
		return cycleLoanDefaultLoanAmt3;
	}

	public void setCycleLoanDefaultLoanAmt3(Double cycleLoanDefaultLoanAmt3) {
		this.cycleLoanDefaultLoanAmt3 = cycleLoanDefaultLoanAmt3;
	}

	public Double getCycleLoanDefaultLoanAmt4() {
		return cycleLoanDefaultLoanAmt4;
	}

	public void setCycleLoanDefaultLoanAmt4(Double cycleLoanDefaultLoanAmt4) {
		this.cycleLoanDefaultLoanAmt4 = cycleLoanDefaultLoanAmt4;
	}

	public Double getCycleLoanDefaultLoanAmt5() {
		return cycleLoanDefaultLoanAmt5;
	}

	public void setCycleLoanDefaultLoanAmt5(Double cycleLoanDefaultLoanAmt5) {
		this.cycleLoanDefaultLoanAmt5 = cycleLoanDefaultLoanAmt5;
	}

	public Double getCycleLoanDefaultLoanAmt6() {
		return cycleLoanDefaultLoanAmt6;
	}

	public void setCycleLoanDefaultLoanAmt6(Double cycleLoanDefaultLoanAmt6) {
		this.cycleLoanDefaultLoanAmt6 = cycleLoanDefaultLoanAmt6;
	}

	public Double getCycleLoanMaxLoanAmt1() {
		return cycleLoanMaxLoanAmt1;
	}

	public void setCycleLoanMaxLoanAmt1(Double cycleLoanMaxLoanAmt1) {
		this.cycleLoanMaxLoanAmt1 = cycleLoanMaxLoanAmt1;
	}

	public Double getCycleLoanMaxLoanAmt2() {
		return cycleLoanMaxLoanAmt2;
	}

	public void setCycleLoanMaxLoanAmt2(Double cycleLoanMaxLoanAmt2) {
		this.cycleLoanMaxLoanAmt2 = cycleLoanMaxLoanAmt2;
	}

	public Double getCycleLoanMaxLoanAmt3() {
		return cycleLoanMaxLoanAmt3;
	}

	public void setCycleLoanMaxLoanAmt3(Double cycleLoanMaxLoanAmt3) {
		this.cycleLoanMaxLoanAmt3 = cycleLoanMaxLoanAmt3;
	}

	public Double getCycleLoanMaxLoanAmt4() {
		return cycleLoanMaxLoanAmt4;
	}

	public void setCycleLoanMaxLoanAmt4(Double cycleLoanMaxLoanAmt4) {
		this.cycleLoanMaxLoanAmt4 = cycleLoanMaxLoanAmt4;
	}

	public Double getCycleLoanMaxLoanAmt5() {
		return cycleLoanMaxLoanAmt5;
	}

	public void setCycleLoanMaxLoanAmt5(Double cycleLoanMaxLoanAmt5) {
		this.cycleLoanMaxLoanAmt5 = cycleLoanMaxLoanAmt5;
	}

	public Double getCycleLoanMaxLoanAmt6() {
		return cycleLoanMaxLoanAmt6;
	}

	public void setCycleLoanMaxLoanAmt6(Double cycleLoanMaxLoanAmt6) {
		this.cycleLoanMaxLoanAmt6 = cycleLoanMaxLoanAmt6;
	}

	public Double getCycleLoanMinLoanAmt1() {
		return cycleLoanMinLoanAmt1;
	}

	public void setCycleLoanMinLoanAmt1(Double cycleLoanMinLoanAmt1) {
		this.cycleLoanMinLoanAmt1 = cycleLoanMinLoanAmt1;
	}

	public Double getCycleLoanMinLoanAmt2() {
		return cycleLoanMinLoanAmt2;
	}

	public void setCycleLoanMinLoanAmt2(Double cycleLoanMinLoanAmt2) {
		this.cycleLoanMinLoanAmt2 = cycleLoanMinLoanAmt2;
	}

	public Double getCycleLoanMinLoanAmt3() {
		return cycleLoanMinLoanAmt3;
	}

	public void setCycleLoanMinLoanAmt3(Double cycleLoanMinLoanAmt3) {
		this.cycleLoanMinLoanAmt3 = cycleLoanMinLoanAmt3;
	}

	public Double getCycleLoanMinLoanAmt4() {
		return cycleLoanMinLoanAmt4;
	}

	public void setCycleLoanMinLoanAmt4(Double cycleLoanMinLoanAmt4) {
		this.cycleLoanMinLoanAmt4 = cycleLoanMinLoanAmt4;
	}

	public Double getCycleLoanMinLoanAmt5() {
		return cycleLoanMinLoanAmt5;
	}

	public void setCycleLoanMinLoanAmt5(Double cycleLoanMinLoanAmt5) {
		this.cycleLoanMinLoanAmt5 = cycleLoanMinLoanAmt5;
	}

	public Double getCycleLoanMinLoanAmt6() {
		return cycleLoanMinLoanAmt6;
	}

	public void setCycleLoanMinLoanAmt6(Double cycleLoanMinLoanAmt6) {
		this.cycleLoanMinLoanAmt6 = cycleLoanMinLoanAmt6;
	}

	public String getDefCycleInstallment1() {
		return defCycleInstallment1;
	}

	public void setDefCycleInstallment1(String defCycleInstallment1) {
		this.defCycleInstallment1 = defCycleInstallment1;
	}

	public String getDefCycleInstallment2() {
		return defCycleInstallment2;
	}

	public void setDefCycleInstallment2(String defCycleInstallment2) {
		this.defCycleInstallment2 = defCycleInstallment2;
	}

	public String getDefCycleInstallment3() {
		return defCycleInstallment3;
	}

	public void setDefCycleInstallment3(String defCycleInstallment3) {
		this.defCycleInstallment3 = defCycleInstallment3;
	}

	public String getDefCycleInstallment4() {
		return defCycleInstallment4;
	}

	public void setDefCycleInstallment4(String defCycleInstallment4) {
		this.defCycleInstallment4 = defCycleInstallment4;
	}

	public String getDefCycleInstallment5() {
		return defCycleInstallment5;
	}

	public void setDefCycleInstallment5(String defCycleInstallment5) {
		this.defCycleInstallment5 = defCycleInstallment5;
	}

	public String getDefCycleInstallment6() {
		return defCycleInstallment6;
	}

	public void setDefCycleInstallment6(String defCycleInstallment6) {
		this.defCycleInstallment6 = defCycleInstallment6;
	}

	public String getDefLoanInstallment1() {
		return defLoanInstallment1;
	}

	public void setDefLoanInstallment1(String defLoanInstallment1) {
		this.defLoanInstallment1 = defLoanInstallment1;
	}

	public String getDefLoanInstallment2() {
		return defLoanInstallment2;
	}

	public void setDefLoanInstallment2(String defLoanInstallment2) {
		this.defLoanInstallment2 = defLoanInstallment2;
	}

	public String getDefLoanInstallment3() {
		return defLoanInstallment3;
	}

	public void setDefLoanInstallment3(String defLoanInstallment3) {
		this.defLoanInstallment3 = defLoanInstallment3;
	}

	public String getDefLoanInstallment4() {
		return defLoanInstallment4;
	}

	public void setDefLoanInstallment4(String defLoanInstallment4) {
		this.defLoanInstallment4 = defLoanInstallment4;
	}

	public String getDefLoanInstallment5() {
		return defLoanInstallment5;
	}

	public void setDefLoanInstallment5(String defLoanInstallment5) {
		this.defLoanInstallment5 = defLoanInstallment5;
	}

	public String getDefLoanInstallment6() {
		return defLoanInstallment6;
	}

	public void setDefLoanInstallment6(String defLoanInstallment6) {
		this.defLoanInstallment6 = defLoanInstallment6;
	}

	public Double getEndInstallmentRange1() {
		return endInstallmentRange1;
	}

	public void setEndInstallmentRange1(Double endInstallmentRange1) {
		this.endInstallmentRange1 = endInstallmentRange1;
	}

	public Double getEndInstallmentRange2() {
		return endInstallmentRange2;
	}

	public void setEndInstallmentRange2(Double endInstallmentRange2) {
		this.endInstallmentRange2 = endInstallmentRange2;
	}

	public Double getEndInstallmentRange3() {
		return endInstallmentRange3;
	}

	public void setEndInstallmentRange3(Double endInstallmentRange3) {
		this.endInstallmentRange3 = endInstallmentRange3;
	}

	public Double getEndInstallmentRange4() {
		return endInstallmentRange4;
	}

	public void setEndInstallmentRange4(Double endInstallmentRange4) {
		this.endInstallmentRange4 = endInstallmentRange4;
	}

	public Double getEndInstallmentRange5() {
		return endInstallmentRange5;
	}

	public void setEndInstallmentRange5(Double endInstallmentRange5) {
		this.endInstallmentRange5 = endInstallmentRange5;
	}

	public Double getEndInstallmentRange6() {
		return endInstallmentRange6;
	}

	public void setEndInstallmentRange6(Double endInstallmentRange6) {
		this.endInstallmentRange6 = endInstallmentRange6;
	}

	public Double getEndRangeLoanAmt1() {
		return endRangeLoanAmt1;
	}

	public void setEndRangeLoanAmt1(Double endRangeLoanAmt1) {
		this.endRangeLoanAmt1 = endRangeLoanAmt1;
	}

	public Double getEndRangeLoanAmt2() {
		return endRangeLoanAmt2;
	}

	public void setEndRangeLoanAmt2(Double endRangeLoanAmt2) {
		this.endRangeLoanAmt2 = endRangeLoanAmt2;
	}

	public Double getEndRangeLoanAmt3() {
		return endRangeLoanAmt3;
	}

	public void setEndRangeLoanAmt3(Double endRangeLoanAmt3) {
		this.endRangeLoanAmt3 = endRangeLoanAmt3;
	}

	public Double getEndRangeLoanAmt4() {
		return endRangeLoanAmt4;
	}

	public void setEndRangeLoanAmt4(Double endRangeLoanAmt4) {
		this.endRangeLoanAmt4 = endRangeLoanAmt4;
	}

	public Double getEndRangeLoanAmt5() {
		return endRangeLoanAmt5;
	}

	public void setEndRangeLoanAmt5(Double endRangeLoanAmt5) {
		this.endRangeLoanAmt5 = endRangeLoanAmt5;
	}

	public Double getEndRangeLoanAmt6() {
		return endRangeLoanAmt6;
	}

	public void setEndRangeLoanAmt6(Double endRangeLoanAmt6) {
		this.endRangeLoanAmt6 = endRangeLoanAmt6;
	}

	public Double getLastLoanDefaultLoanAmt1() {
		return lastLoanDefaultLoanAmt1;
	}

	public void setLastLoanDefaultLoanAmt1(Double lastLoanDefaultLoanAmt1) {
		this.lastLoanDefaultLoanAmt1 = lastLoanDefaultLoanAmt1;
	}

	public Double getLastLoanDefaultLoanAmt2() {
		return lastLoanDefaultLoanAmt2;
	}

	public void setLastLoanDefaultLoanAmt2(Double lastLoanDefaultLoanAmt2) {
		this.lastLoanDefaultLoanAmt2 = lastLoanDefaultLoanAmt2;
	}

	public Double getLastLoanDefaultLoanAmt3() {
		return lastLoanDefaultLoanAmt3;
	}

	public void setLastLoanDefaultLoanAmt3(Double lastLoanDefaultLoanAmt3) {
		this.lastLoanDefaultLoanAmt3 = lastLoanDefaultLoanAmt3;
	}

	public Double getLastLoanDefaultLoanAmt4() {
		return lastLoanDefaultLoanAmt4;
	}

	public void setLastLoanDefaultLoanAmt4(Double lastLoanDefaultLoanAmt4) {
		this.lastLoanDefaultLoanAmt4 = lastLoanDefaultLoanAmt4;
	}

	public Double getLastLoanDefaultLoanAmt5() {
		return lastLoanDefaultLoanAmt5;
	}

	public void setLastLoanDefaultLoanAmt5(Double lastLoanDefaultLoanAmt5) {
		this.lastLoanDefaultLoanAmt5 = lastLoanDefaultLoanAmt5;
	}

	public Double getLastLoanDefaultLoanAmt6() {
		return lastLoanDefaultLoanAmt6;
	}

	public void setLastLoanDefaultLoanAmt6(Double lastLoanDefaultLoanAmt6) {
		this.lastLoanDefaultLoanAmt6 = lastLoanDefaultLoanAmt6;
	}

	public Double getLastLoanMaxLoanAmt1() {
		return lastLoanMaxLoanAmt1;
	}

	public void setLastLoanMaxLoanAmt1(Double lastLoanMaxLoanAmt1) {
		this.lastLoanMaxLoanAmt1 = lastLoanMaxLoanAmt1;
	}

	public Double getLastLoanMaxLoanAmt2() {
		return lastLoanMaxLoanAmt2;
	}

	public void setLastLoanMaxLoanAmt2(Double lastLoanMaxLoanAmt2) {
		this.lastLoanMaxLoanAmt2 = lastLoanMaxLoanAmt2;
	}

	public Double getLastLoanMaxLoanAmt3() {
		return lastLoanMaxLoanAmt3;
	}

	public void setLastLoanMaxLoanAmt3(Double lastLoanMaxLoanAmt3) {
		this.lastLoanMaxLoanAmt3 = lastLoanMaxLoanAmt3;
	}

	public Double getLastLoanMaxLoanAmt4() {
		return lastLoanMaxLoanAmt4;
	}

	public void setLastLoanMaxLoanAmt4(Double lastLoanMaxLoanAmt4) {
		this.lastLoanMaxLoanAmt4 = lastLoanMaxLoanAmt4;
	}

	public Double getLastLoanMaxLoanAmt5() {
		return lastLoanMaxLoanAmt5;
	}

	public void setLastLoanMaxLoanAmt5(Double lastLoanMaxLoanAmt5) {
		this.lastLoanMaxLoanAmt5 = lastLoanMaxLoanAmt5;
	}

	public Double getLastLoanMaxLoanAmt6() {
		return lastLoanMaxLoanAmt6;
	}

	public void setLastLoanMaxLoanAmt6(Double lastLoanMaxLoanAmt6) {
		this.lastLoanMaxLoanAmt6 = lastLoanMaxLoanAmt6;
	}

	public Double getLastLoanMinLoanAmt1() {
		return lastLoanMinLoanAmt1;
	}

	public void setLastLoanMinLoanAmt1(Double lastLoanMinLoanAmt1) {
		this.lastLoanMinLoanAmt1 = lastLoanMinLoanAmt1;
	}

	public Double getLastLoanMinLoanAmt2() {
		return lastLoanMinLoanAmt2;
	}

	public void setLastLoanMinLoanAmt2(Double lastLoanMinLoanAmt2) {
		this.lastLoanMinLoanAmt2 = lastLoanMinLoanAmt2;
	}

	public Double getLastLoanMinLoanAmt3() {
		return lastLoanMinLoanAmt3;
	}

	public void setLastLoanMinLoanAmt3(Double lastLoanMinLoanAmt3) {
		this.lastLoanMinLoanAmt3 = lastLoanMinLoanAmt3;
	}

	public Double getLastLoanMinLoanAmt4() {
		return lastLoanMinLoanAmt4;
	}

	public void setLastLoanMinLoanAmt4(Double lastLoanMinLoanAmt4) {
		this.lastLoanMinLoanAmt4 = lastLoanMinLoanAmt4;
	}

	public Double getLastLoanMinLoanAmt5() {
		return lastLoanMinLoanAmt5;
	}

	public void setLastLoanMinLoanAmt5(Double lastLoanMinLoanAmt5) {
		this.lastLoanMinLoanAmt5 = lastLoanMinLoanAmt5;
	}

	public Double getLastLoanMinLoanAmt6() {
		return lastLoanMinLoanAmt6;
	}

	public void setLastLoanMinLoanAmt6(Double lastLoanMinLoanAmt6) {
		this.lastLoanMinLoanAmt6 = lastLoanMinLoanAmt6;
	}

	public String getMaxCycleInstallment1() {
		return maxCycleInstallment1;
	}

	public void setMaxCycleInstallment1(String maxCycleInstallment1) {
		this.maxCycleInstallment1 = maxCycleInstallment1;
	}

	public String getMaxCycleInstallment2() {
		return maxCycleInstallment2;
	}

	public void setMaxCycleInstallment2(String maxCycleInstallment2) {
		this.maxCycleInstallment2 = maxCycleInstallment2;
	}

	public String getMaxCycleInstallment3() {
		return maxCycleInstallment3;
	}

	public void setMaxCycleInstallment3(String maxCycleInstallment3) {
		this.maxCycleInstallment3 = maxCycleInstallment3;
	}

	public String getMaxCycleInstallment4() {
		return maxCycleInstallment4;
	}

	public void setMaxCycleInstallment4(String maxCycleInstallment4) {
		this.maxCycleInstallment4 = maxCycleInstallment4;
	}

	public String getMaxCycleInstallment5() {
		return maxCycleInstallment5;
	}

	public void setMaxCycleInstallment5(String maxCycleInstallment5) {
		this.maxCycleInstallment5 = maxCycleInstallment5;
	}

	public String getMaxCycleInstallment6() {
		return maxCycleInstallment6;
	}

	public void setMaxCycleInstallment6(String maxCycleInstallment6) {
		this.maxCycleInstallment6 = maxCycleInstallment6;
	}

	public String getMaxLoanInstallment1() {
		return maxLoanInstallment1;
	}

	public void setMaxLoanInstallment1(String maxLoanInstallment1) {
		this.maxLoanInstallment1 = maxLoanInstallment1;
	}

	public String getMaxLoanInstallment2() {
		return maxLoanInstallment2;
	}

	public void setMaxLoanInstallment2(String maxLoanInstallment2) {
		this.maxLoanInstallment2 = maxLoanInstallment2;
	}

	public String getMaxLoanInstallment3() {
		return maxLoanInstallment3;
	}

	public void setMaxLoanInstallment3(String maxLoanInstallment3) {
		this.maxLoanInstallment3 = maxLoanInstallment3;
	}

	public String getMaxLoanInstallment4() {
		return maxLoanInstallment4;
	}

	public void setMaxLoanInstallment4(String maxLoanInstallment4) {
		this.maxLoanInstallment4 = maxLoanInstallment4;
	}

	public String getMaxLoanInstallment5() {
		return maxLoanInstallment5;
	}

	public void setMaxLoanInstallment5(String maxLoanInstallment5) {
		this.maxLoanInstallment5 = maxLoanInstallment5;
	}

	public String getMaxLoanInstallment6() {
		return maxLoanInstallment6;
	}

	public void setMaxLoanInstallment6(String maxLoanInstallment6) {
		this.maxLoanInstallment6 = maxLoanInstallment6;
	}

	public String getMinCycleInstallment1() {
		return minCycleInstallment1;
	}

	public void setMinCycleInstallment1(String minCycleInstallment1) {
		this.minCycleInstallment1 = minCycleInstallment1;
	}

	public String getMinCycleInstallment2() {
		return minCycleInstallment2;
	}

	public void setMinCycleInstallment2(String minCycleInstallment2) {
		this.minCycleInstallment2 = minCycleInstallment2;
	}

	public String getMinCycleInstallment3() {
		return minCycleInstallment3;
	}

	public void setMinCycleInstallment3(String minCycleInstallment3) {
		this.minCycleInstallment3 = minCycleInstallment3;
	}

	public String getMinCycleInstallment4() {
		return minCycleInstallment4;
	}

	public void setMinCycleInstallment4(String minCycleInstallment4) {
		this.minCycleInstallment4 = minCycleInstallment4;
	}

	public String getMinCycleInstallment5() {
		return minCycleInstallment5;
	}

	public void setMinCycleInstallment5(String minCycleInstallment5) {
		this.minCycleInstallment5 = minCycleInstallment5;
	}

	public String getMinCycleInstallment6() {
		return minCycleInstallment6;
	}

	public void setMinCycleInstallment6(String minCycleInstallment6) {
		this.minCycleInstallment6 = minCycleInstallment6;
	}

	public String getMinLoanInstallment1() {
		return minLoanInstallment1;
	}

	public void setMinLoanInstallment1(String minLoanInstallment1) {
		this.minLoanInstallment1 = minLoanInstallment1;
	}

	public String getMinLoanInstallment2() {
		return minLoanInstallment2;
	}

	public void setMinLoanInstallment2(String minLoanInstallment2) {
		this.minLoanInstallment2 = minLoanInstallment2;
	}

	public String getMinLoanInstallment3() {
		return minLoanInstallment3;
	}

	public void setMinLoanInstallment3(String minLoanInstallment3) {
		this.minLoanInstallment3 = minLoanInstallment3;
	}

	public String getMinLoanInstallment4() {
		return minLoanInstallment4;
	}

	public void setMinLoanInstallment4(String minLoanInstallment4) {
		this.minLoanInstallment4 = minLoanInstallment4;
	}

	public String getMinLoanInstallment5() {
		return minLoanInstallment5;
	}

	public void setMinLoanInstallment5(String minLoanInstallment5) {
		this.minLoanInstallment5 = minLoanInstallment5;
	}

	public String getMinLoanInstallment6() {
		return minLoanInstallment6;
	}

	public void setMinLoanInstallment6(String minLoanInstallment6) {
		this.minLoanInstallment6 = minLoanInstallment6;
	}

	public Double getStartInstallmentRange1() {
		return startInstallmentRange1;
	}

	public void setStartInstallmentRange1(Double startInstallmentRange1) {
		this.startInstallmentRange1 = startInstallmentRange1;
	}

	public Double getStartInstallmentRange2() {
		return startInstallmentRange2;
	}

	public void setStartInstallmentRange2(Double startInstallmentRange2) {
		this.startInstallmentRange2 = startInstallmentRange2;
	}

	public Double getStartInstallmentRange3() {
		return startInstallmentRange3;
	}

	public void setStartInstallmentRange3(Double startInstallmentRange3) {
		this.startInstallmentRange3 = startInstallmentRange3;
	}

	public Double getStartInstallmentRange4() {
		return startInstallmentRange4;
	}

	public void setStartInstallmentRange4(Double startInstallmentRange4) {
		this.startInstallmentRange4 = startInstallmentRange4;
	}

	public Double getStartInstallmentRange5() {
		return startInstallmentRange5;
	}

	public void setStartInstallmentRange5(Double startInstallmentRange5) {
		this.startInstallmentRange5 = startInstallmentRange5;
	}

	public Double getStartInstallmentRange6() {
		return startInstallmentRange6;
	}

	public void setStartInstallmentRange6(Double startInstallmentRange6) {
		this.startInstallmentRange6 = startInstallmentRange6;
	}

	public Double getStartRangeLoanAmt1() {
		return startRangeLoanAmt1;
	}

	public void setStartRangeLoanAmt1(Double startRangeLoanAmt1) {
		this.startRangeLoanAmt1 = startRangeLoanAmt1;
	}

	public Double getStartRangeLoanAmt2() {
		return startRangeLoanAmt2;
	}

	public void setStartRangeLoanAmt2(Double startRangeLoanAmt2) {
		this.startRangeLoanAmt2 = startRangeLoanAmt2;
	}

	public Double getStartRangeLoanAmt3() {
		return startRangeLoanAmt3;
	}

	public void setStartRangeLoanAmt3(Double startRangeLoanAmt3) {
		this.startRangeLoanAmt3 = startRangeLoanAmt3;
	}

	public Double getStartRangeLoanAmt4() {
		return startRangeLoanAmt4;
	}

	public void setStartRangeLoanAmt4(Double startRangeLoanAmt4) {
		this.startRangeLoanAmt4 = startRangeLoanAmt4;
	}

	public Double getStartRangeLoanAmt5() {
		return startRangeLoanAmt5;
	}

	public void setStartRangeLoanAmt5(Double startRangeLoanAmt5) {
		this.startRangeLoanAmt5 = startRangeLoanAmt5;
	}

	public Double getStartRangeLoanAmt6() {
		return startRangeLoanAmt6;
	}

	public void setStartRangeLoanAmt6(Double startRangeLoanAmt6) {
		this.startRangeLoanAmt6 = startRangeLoanAmt6;
	}
	public LoanPrdActionForm() {
		this(MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER));
	}

	public LoanPrdActionForm(MifosLogger logger) {
		super();
		prdOfferinFees = null;
		loanOfferingFunds = null;
		this.logger = logger;
	}

	public Double getDefaultLoanAmount() {
		return defaultLoanAmount;
	}

	public void setDefaultLoanAmount(Double defaultLoanAmount) {
		this.defaultLoanAmount = defaultLoanAmount;
	}

	public String getDefInterestRate() {
		return defInterestRate;
	}

	public void setDefInterestRate(String defInterestRate) {
		this.defInterestRate = defInterestRate;
	}

	public String getDefNoInstallments() {
		return defNoInstallments;
	}

	public void setDefNoInstallments(String defNoInstallments) {
		this.defNoInstallments = defNoInstallments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFreqOfInstallments() {
		return freqOfInstallments;
	}

	public void setFreqOfInstallments(String freqOfInstallments) {
		this.freqOfInstallments = freqOfInstallments;
	}

	public String getGracePeriodDuration() {
		return gracePeriodDuration;
	}

	public void setGracePeriodDuration(String gracePeriodDuration) {
		this.gracePeriodDuration = gracePeriodDuration;
	}

	public String getGracePeriodType() {
		return gracePeriodType;
	}

	public void setGracePeriodType(String gracePeriodType) {
		this.gracePeriodType = gracePeriodType;
	}

	public String getIntDedDisbursementFlag() {
		return intDedDisbursementFlag;
	}

	public void setIntDedDisbursementFlag(String intDedDisbursementFlag) {
		this.intDedDisbursementFlag = intDedDisbursementFlag;
	}

	public String getInterestGLCode() {
		return interestGLCode;
	}

	public void setInterestGLCode(String interestGLCode) {
		this.interestGLCode = interestGLCode;
	}

	public String getInterestTypes() {
		return interestTypes;
	}

	public void setInterestTypes(String interestTypes) {
		this.interestTypes = interestTypes;
	}

	public String getLoanCounter() {
		return loanCounter;
	}

	public void setLoanCounter(String loanCounter) {
		this.loanCounter = loanCounter;
	}

	public String[] getLoanOfferingFunds() {
		return loanOfferingFunds;
	}

	public void setLoanOfferingFunds(String[] loanOfferingFunds) {
		this.loanOfferingFunds = loanOfferingFunds;
	}

	public String getMaxInterestRate() {
		return maxInterestRate;
	}

	public void setMaxInterestRate(String maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
	}

	public Double getMaxLoanAmount() {
		return maxLoanAmount;
	}

	public void setMaxLoanAmount(Double maxLoanAmount) {
		this.maxLoanAmount = maxLoanAmount;
	}

	public String getMaxNoInstallments() {
		return maxNoInstallments;
	}

	public void setMaxNoInstallments(String maxNoInstallments) {
		this.maxNoInstallments = maxNoInstallments;
	}

	public String getMinInterestRate() {
		return minInterestRate;
	}

	public void setMinInterestRate(String minInterestRate) {
		this.minInterestRate = minInterestRate;
	}

	public Double getMinLoanAmount() {
		return minLoanAmount;
	}

	public void setMinLoanAmount(Double minLoanAmount) {
		this.minLoanAmount = minLoanAmount;
	}

	public String getMinNoInstallments() {
		return minNoInstallments;
	}

	public void setMinNoInstallments(String minNoInstallments) {
		this.minNoInstallments = minNoInstallments;
	}

	/** Called via reflection from jsp's (I think).
	 * Most/all java code should instead call 
	 * {@link #getPrdApplicableMasterEnum()}
	 */
	public String getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public ApplicableTo getPrdApplicableMasterEnum() {
		return ApplicableTo.fromInt(
			Integer.parseInt(prdApplicableMaster));
	}

	/** Called via reflection from jsp's (I think).
	 * Most/all java code should instead call
	 * {@link #setPrdApplicableMaster(ApplicableTo)}
	 */
	public void setPrdApplicableMaster(String prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	public void setPrdApplicableMaster(ApplicableTo applicableTo) {
		this.prdApplicableMaster = "" + applicableTo.getValue();
	}

	public String getPrdCategory() {
		return prdCategory;
	}

	public void setPrdCategory(String prdCategory) {
		this.prdCategory = prdCategory;
	}

	public String[] getPrdOfferinFees() {
		return prdOfferinFees;
	}

	public void setPrdOfferinFees(String[] prdOfferinFees) {
		this.prdOfferinFees = prdOfferinFees;
	}

	public String getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(String prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	public void setPrdOfferingShortName(String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	public String getPrdStatus() {
		return prdStatus;
	}

	public void setPrdStatus(String prdStatus) {
		this.prdStatus = prdStatus;
	}

	public String getPrincipalGLCode() {
		return principalGLCode;
	}

	public void setPrincipalGLCode(String principalGLCode) {
		this.principalGLCode = principalGLCode;
	}

	public String getPrinDueLastInstFlag() {
		return prinDueLastInstFlag;
	}

	public void setPrinDueLastInstFlag(String prinDueLastInstFlag) {
		this.prinDueLastInstFlag = prinDueLastInstFlag;
	}

	public String getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(String recurAfter) {
		this.recurAfter = recurAfter;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Date getStartDateValue(Locale locale) {
		return DateUtils.getLocaleDate(locale, getStartDate());
	}

	public Date getEndDateValue(Locale locale) {
		return DateUtils.getLocaleDate(locale, getEndDate());
	}

	public Short getPrdCategoryValue() {
		return getShortValue(getPrdCategory());
	}

	public Short getGracePeriodTypeValue() {
		return getShortValue(getGracePeriodType());
	}

	public Short getGracePeriodDurationValue() {
		return getShortValue(getGracePeriodDuration());
	}

	public Short getInterestTypesValue() {
		return getShortValue(getInterestTypes());
	}

	/*public Money getMaxLoanAmountValue() {
		return getMoney(getMaxLoanAmount());
	}

	public Money getMinLoanAmountValue() {
		return getMoney(getMinLoanAmount());
	}

	public Money getDefaultLoanAmountValue() {
		return (StringUtils.isNullAndEmptySafe(getDefaultLoanAmount()) && !getDefaultLoanAmount()
				.trim().equals(".")) ? new Money(getDefaultLoanAmount()) : null;
	}*/

	public Double getMaxInterestRateValue() {
		return getDoubleValue(getMaxInterestRate());
	}

	public Double getMinInterestRateValue() {
		return getDoubleValue(getMinInterestRate());
	}

	public Double getDefInterestRateValue() {
		return getDoubleValue(getDefInterestRate());
	}

	public Short getMaxNoInstallmentsValue() {
		return getShortValue(getMaxNoInstallments());
	}

	public Short getMinNoInstallmentsValue() {
		return getShortValue(getMinNoInstallments());
	}

	public Short getDefNoInstallmentsValue() {
		return getShortValue(getDefNoInstallments());
	}

	public boolean isLoanCounterValue() {
		return getBooleanValue(getLoanCounter());
	}

	public boolean isIntDedAtDisbValue() {
		return getBooleanValue(getIntDedDisbursementFlag());
	}

	public boolean isPrinDueLastInstValue() {
		return getBooleanValue(getPrinDueLastInstFlag());
	}

	public Short getRecurAfterValue() {
		return getShortValue(getRecurAfter());
	}

	public Short getFreqOfInstallmentsValue() {
		return getShortValue(getFreqOfInstallments());
	}

	public Short getPrdStatusValue() {
		return getShortValue(getPrdStatus());
	}

	public Short getPrdOfferingIdValue() {
		return getShortValue(getPrdOfferingId());
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		logger
				.debug("start reset method of Savings Product Action form method :"
						+ method);
		if (method != null && method.equals(Methods.load.toString())) {
			startDate = DateUtils.getCurrentDate(getUserContext(request)
			.getPreferredLocale());
			recurAfter = "1";
			minNoInstallments = "1";
		}
		if (method != null
				&& (method.equals(Methods.preview.toString()) || method
						.equals(Methods.editPreview.toString()))) {
			intDedDisbursementFlag = null;
			prinDueLastInstFlag = null;
			loanCounter = null;
			prdOfferinFees = null;
			loanOfferingFunds = null;
			gracePeriodType = null;
			gracePeriodDuration = null;
		}
		logger
				.debug("reset method of Savings Product Action form method called ");
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		logger
				.debug("validate method of Savings Product Action form method called :"
						+ method);
		if (method != null && method.equals(Methods.preview.toString())) {
			errors.add(super.validate(mapping, request));
			validateForPreview(request, errors);
		}
		if (method != null && method.equals(Methods.editPreview.toString())) {
			errors.add(super.validate(mapping, request));
			validateForEditPreview(request, errors);
		}
		if (method != null && !method.equals(Methods.validate.toString())) {
			request.setAttribute(ProductDefinitionConstants.METHODCALLED,
					method);
		}
		logger
				.debug("validate method of Savings Product Action form called and error size:"
						+ errors.size());
		return errors;
	}

	public void clear() {
		logger
				.debug("start clear method of Loan Product Action form method :"
						+ prdOfferingId);
		this.prdOfferingId = null;
		this.prdOfferingName = null;
		this.prdOfferingShortName = null;
		this.description = null;
		this.prdCategory = null;
		this.startDate = null;
		this.endDate = null;
		this.prdApplicableMaster = null;
		this.loanCounter = null;
		this.minLoanAmount = null;
		this.maxLoanAmount = null;
		this.defaultLoanAmount = null;
		this.interestTypes = null;
		this.maxInterestRate = null;
		this.minInterestRate = null;
		this.defInterestRate = null;
		this.freqOfInstallments = null;
		this.recurAfter = null;
		this.maxNoInstallments = null;
		this.minNoInstallments = null;
		this.defNoInstallments = null;
		this.intDedDisbursementFlag = null;
		this.prinDueLastInstFlag = null;
		this.gracePeriodType = null;
		this.gracePeriodDuration = null;
		this.prdOfferinFees = null;
		this.loanOfferingFunds = null;
		this.principalGLCode = null;
		this.interestGLCode = null;
		this.prdStatus = null;
		logger
				.debug("clear method of Loan Product Action form method called :"
						+ prdOfferingId);
	}

	private void validateForPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateForPreview method of Loan Product Action form method :"
						+ prdOfferingName);
		validateStartDate(request, errors);
 		validateEndDate(request, errors);
		validateLoanAmmount(errors);
		validateLoanInstallments(errors);
		if (StringUtils.isNullOrEmpty(getInterestTypes()))
			addError(errors, "interestTypes",
					ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
							ConfigurationConstants.INTEREST, request),
					ProductDefinitionConstants.RATETYPE);
		validateMinMaxDefInterestRates(errors, request);
		vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors,
				request);
		validatePrincDueOnLastInstAndPrincGraceType(errors);
		setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
		validateInterestGLCode(request,errors);
		logger
				.debug("validateForPreview method of Loan Product Action form method called :"
						+ prdOfferingName);
	}

	private void validateForEditPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateForEditPreview method of Loan Product Action form method :"
						+ prdOfferingName);
		validateStartDateForEditPreview(request, errors);
		validateEndDate(request, errors);
		validateLoanAmmount(errors);
		validateLoanInstallments(errors);
		if (StringUtils.isNullOrEmpty(getInterestTypes()))
			addError(errors, "interestTypes",
					ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
							ConfigurationConstants.INTEREST, request),
					ProductDefinitionConstants.RATETYPE);
		if (StringUtils.isNullOrEmpty(getPrdStatus()))
			addError(errors, "prdStatus",
					ProductDefinitionConstants.ERROR_SELECT,
					ProductDefinitionConstants.STATUS);
		validateMinMaxDefInterestRates(errors, request);
		vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors,
				request);
		validatePrincDueOnLastInstAndPrincGraceType(errors);
		setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
		validateInterestGLCode(request,errors);
		logger
				.debug("validateForEditPreview method of Loan Product Action form method called :"
						+ prdOfferingName);
	}

	private void validateStartDateForEditPreview(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateStartDateForEditPreview method of Loan Product Action form method :"
						+ startDate);
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		java.util.Date oldStartDate = null;
		try {
			oldStartDate = (java.util.Date) SessionUtils.getAttribute(
					ProductDefinitionConstants.LOANPRDSTARTDATE, request);
		} catch (PageExpiredException e) {
		}
		Date changedStartDate = getStartDateValue(getUserContext(request)
				.getPreferredLocale());
		if (oldStartDate != null && changedStartDate != null) {
			if (DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime())
					.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0
					&& (changedStartDate != null && DateUtils
							.getDateWithoutTimeStamp(oldStartDate.getTime())
							.compareTo(
									DateUtils
											.getDateWithoutTimeStamp(changedStartDate
													.getTime())) != 0)) {
				addError(errors, "startDate",
						ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
			}
		} else if (changedStartDate != null
				&& DateUtils
						.getDateWithoutTimeStamp(changedStartDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
			validateStartDate(request, errors);

		}
		logger
				.debug("validateStartDateForEditPreview method of Loan Product Action form method called :"
						+ startDate + "---" + oldStartDate);
	}

	private void validateStartDate(HttpServletRequest request,
			ActionErrors errors) {
		logger
				.debug("start validateStartDate method of Loan Product Action form method :"
						+ startDate);
		Date startingDate = getStartDateValue(getUserContext(request)
				.getPreferredLocale());
		if (startingDate != null
				&& ((DateUtils.getDateWithoutTimeStamp(startingDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) || (DateUtils
						.getDateWithoutTimeStamp(startingDate.getTime())
						.compareTo(
								DateUtils
										.getCurrentDateOfNextYearWithOutTimeStamp()) > 0)))
			addError(errors, "startDate",
					ProductDefinitionConstants.INVALIDSTARTDATE);
		logger
				.debug("validateStartDate method of Loan Product Action form method called :"
						+ startDate);
	}

	private void validateEndDate(HttpServletRequest request, ActionErrors errors) {
		logger
				.debug("start validateEndDate method of Loan Product Action form method :"
						+ startDate + "---" + endDate);
		Date startingDate = getStartDateValue(getUserContext(request)
				.getPreferredLocale());
		Date endingDate = getEndDateValue(getUserContext(request)
				.getPreferredLocale());
		if (startingDate != null && endingDate != null
				&& startingDate.compareTo(endingDate) >= 0)
			addError(errors, "endDate",
					ProductDefinitionConstants.INVALIDENDDATE);
		logger
				.debug("validateEndDate method of Loan Product Action form method called :"
						+ startDate + "---" + endDate);
	}

	private void setSelectedFeesAndFundsAndValidateForFrequency(
			HttpServletRequest request, ActionErrors errors) {
		logger
				.debug("start setSelectedFeesAndFundsAndValidateForFrequency method "
						+ "of Loan Product Action form method :");
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		List<FeeView> feeViews = new ArrayList<FeeView>();
		try {
			if (getPrdOfferinFees() != null && getPrdOfferinFees().length > 0) {

				List<FeeBO> fees = (List<FeeBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDFEE, request);
				for (String selectedFee : getPrdOfferinFees()) {
					FeeBO fee = getFeeFromList(fees, selectedFee);
					if (fee != null) {
						isFrequencyMatchingOfferingFrequency(fee, errors);
						feeViews.add(new FeeView(getUserContext(request), fee));

					}
				}
			}
			SessionUtils.setCollectionAttribute(
					ProductDefinitionConstants.LOANPRDFEESELECTEDLIST,
					feeViews, request);
		} catch (PageExpiredException e) {
		}
		List<FundBO> selectedFunds = new ArrayList<FundBO>();
		try {
			if (getLoanOfferingFunds() != null
					&& getLoanOfferingFunds().length > 0) {

				List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.SRCFUNDSLIST, request);
				for (String selectedFund : getLoanOfferingFunds()) {
					FundBO fund = getFundFromList(funds, selectedFund);
					if (fund != null)
						selectedFunds.add(fund);
				}
			}
			SessionUtils.setCollectionAttribute(
					ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST,
					selectedFunds, request);
		} catch (PageExpiredException e) {
		}
		logger
				.debug("setSelectedFeesAndFundsAndValidateForFrequency method "
						+ "of Loan Product Action form method called :");
	}

	private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
		logger
				.debug("getFeeFromList method of Loan Product Action form method called :"
						+ feeSelected);
		for (FeeBO fee : fees)
			if (fee.getFeeId().equals(getShortValue(feeSelected)))
				return fee;
		return null;
	}

	private FundBO getFundFromList(List<FundBO> funds, String fundSelected) {
		logger
				.debug("getFundFromList method of Loan Product Action form method called :"
						+ fundSelected);
		for (FundBO fund : funds)
			if (fund.getFundId().equals(getShortValue(fundSelected)))
				return fund;
		return null;
	}

	private void isFrequencyMatchingOfferingFrequency(FeeBO fee,
			ActionErrors errors) {
		logger
				.debug("start Loan prd Action Form isFrequencyMatchingOfferingFrequency - fee:"
						+ fee);
		if (getFreqOfInstallmentsValue() != null
				&& fee.isPeriodic()
				&& !(fee.getFeeFrequency().getFeeMeetingFrequency()
						.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(getFreqOfInstallmentsValue())))
			addError(errors, "Fee",
					ProductDefinitionConstants.ERRORFEEFREQUENCY, fee
							.getFeeName());
		logger
				.debug("Loan prd Action Form isFrequencyMatchingOfferingFrequency called - fee:"
						+ fee);
	}

	private void validateMinMaxDefInterestRates(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form validateMinMaxDefInterestRates :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
		validateForMand(getMaxInterestRate(), errors, "maxInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.MAX, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForMand(getMinInterestRate(), errors, "minInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.MIN, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForMand(getDefInterestRate(), errors, "defInterestRate",
				ProductDefinitionConstants.ERRORSENTERCONFIG,
				ProductDefinitionConstants.DEFAULT, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE);
		validateForRange(getMaxInterestRate(), 0, 999, errors,
				"maxInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.MAX, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		validateForRange(getMinInterestRate(), 0, 999, errors,
				"minInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.MIN, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		validateForRange(getDefInterestRate(), 0, 999, errors,
				"defInterestRate",
				ProductDefinitionConstants.ERRORSDEFMINMAXCONFIG,
				ProductDefinitionConstants.DEFAULT, getLabel(
						ConfigurationConstants.INTEREST, request),
				ProductDefinitionConstants.RATE, "0", "999");
		vaildateMinMaxInterestRate(errors, request);
		vaildateDefMinMaxInterestRate(errors, request);
		logger
				.debug("Loan prd Action Form validateMinMaxDefInterestRates called:"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
	}

	private void validateForMand(String value, ActionErrors errors,
			String property, String key, String... arg) {
		logger.debug("Start Loan prd Action Form validateForMand :"
				+ value);
		if (StringUtils.isNullOrEmpty(value))
			addError(errors, property, key, arg);
		logger.debug("Loan prd Action Form validateForMand called:"
				+ value);
	}

	private void validateForRange(String value, double min, double max,
			ActionErrors errors, String property, String key, String... arg) {
		logger.debug("start Loan prd Action Form validateForRange :"
				+ value);
		if (StringUtils.isNullAndEmptySafe(value)) {
			double valueToBeChecked = getDoubleValue(value);
			if (valueToBeChecked < min || valueToBeChecked > max) {
				addError(errors, property, key, arg);
			}
		}
		logger.debug("Loan prd Action Form validateForRange called:"
				+ value);
	}

	private void vaildateMinMaxInterestRate(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateMinMaxInterestRate :"
						+ maxInterestRate + "---" + minInterestRate);
		if (StringUtils.isNullAndEmptySafe(getMaxInterestRate())
				&& StringUtils.isNullAndEmptySafe(getMinInterestRate())) {
			double maximumInterestRate = getDoubleValue(getMaxInterestRate());
			double minimumInterestRate = getDoubleValue(getMinInterestRate());
			if (maximumInterestRate <= 999.0 && minimumInterestRate <= 999.0
					&& maximumInterestRate < minimumInterestRate)
				addError(errors, "MinMaxInterestRate",
						ProductDefinitionConstants.ERRORSMINMAXINTCONFIG,
						ProductDefinitionConstants.MAX, getLabel(
								ConfigurationConstants.INTEREST, request),
						ProductDefinitionConstants.RATE,
						ProductDefinitionConstants.MIN);
		}
		logger
				.debug("Loan prd Action Form vaildateMinMaxInterestRate called:"
						+ maxInterestRate + "---" + minInterestRate);
	}

	private void vaildateDefMinMaxInterestRate(ActionErrors errors,
			HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateDefMinMaxInterestRate :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
		if (StringUtils.isNullAndEmptySafe(getMaxInterestRate())
				&& StringUtils.isNullAndEmptySafe(getMinInterestRate())
				&& StringUtils.isNullAndEmptySafe(getDefInterestRate())) {
			double maximumInterestRate = getDoubleValue(getMaxInterestRate());
			double minimumInterestRate = getDoubleValue(getMinInterestRate());
			double defaultInterestRate = getDoubleValue(getDefInterestRate());
			if (maximumInterestRate <= 999.0 && minimumInterestRate <= 999.0
					&& defaultInterestRate <= 999.0) {
				if (defaultInterestRate < minimumInterestRate
						|| defaultInterestRate > maximumInterestRate)
					addError(errors, "DefInterestRate",
							ProductDefinitionConstants.ERRORSDEFINTCONFIG,
							ProductDefinitionConstants.DEFAULT, getLabel(
									ConfigurationConstants.INTEREST, request),
							ProductDefinitionConstants.RATE,
							ProductDefinitionConstants.MIN,
							ProductDefinitionConstants.MAX);
			}

		}
		logger
				.debug("Loan prd Action Form vaildateDefMinMaxInterestRate called :"
						+ maxInterestRate
						+ "---"
						+ minInterestRate
						+ "---"
						+ defInterestRate);
	}

	private void vaildateDecliningInterestSvcChargeDeductedAtDisbursement(
			ActionErrors errors, HttpServletRequest request) {
		logger
				.debug("start Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement :"
						+ getInterestTypes()
						+ "---"
						+ getIntDedDisbursementFlag());

		if (getInterestTypes() != null
				&& getInterestTypes().equals(
						InterestType.DECLINING.getValue()
								.toString())) {

			if (null != getIntDedDisbursementFlag()
					&& getIntDedDisbursementFlag().equals("1")) {
				errors
						.add(
								ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION,
								new ActionMessage(
										ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION));
			}
		}

		logger
				.debug("Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement called ");
	}

	private void validatePrincDueOnLastInstAndPrincGraceType(ActionErrors errors) {
		if (getGracePeriodTypeValue() != null
				&& getGracePeriodTypeValue().equals(
						GraceType.PRINCIPALONLYGRACE.getValue())
				&& isPrinDueLastInstValue()) {
			addError(
					errors,
					ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE,
					ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
		}
	}
	
	private void validateInterestGLCode(HttpServletRequest request,
			ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getInterestGLCode()))
			addError(
					errors,
					ProductDefinitionConstants.INTERESTGLCODE,
					ProductDefinitionConstants.ERROR_SELECT,
					ProductDefinitionConstants.GLCODE_FOR
							+ getLabel(ConfigurationConstants.INTEREST, request));
	}

	public String getLoanAmtCalcType() {
		return loanAmtCalcType;
	}

	public void setLoanAmtCalcType(String loanAmtCalcType) {
		this.loanAmtCalcType = loanAmtCalcType;
	}

	private void validateLoanAmmount(ActionErrors errors){
		String calctype = getLoanAmtCalcType();
		Double minLoanAmt;
		Double maxLoanAmt;
		Double defLoanAmt;	
		Double startRange;
		Double endRange;
		if (!StringUtils.isNullAndEmptySafe(calctype)){
			addError(errors,ProductDefinitionConstants.ERRORCALCLOANAMOUNTTYPE,
					ProductDefinitionConstants.ERRORCALCLOANAMOUNTTYPE);
		}
		else{
			if(calctype.equals("1"))
			{
				// same for all loans			
				minLoanAmt = (getMinLoanAmount()==null ||getMinLoanAmount().equals(""))?null:Double.valueOf(getMinLoanAmount());
				maxLoanAmt = (getMaxLoanAmount()==null ||getMaxLoanAmount().equals(""))?null:Double.valueOf(getMaxLoanAmount());
				defLoanAmt = (getDefaultLoanAmount()==null ||getDefaultLoanAmount().equals(""))?null:Double.valueOf(getDefaultLoanAmount());
				validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"Same for all loans");
			}
			else if(calctype.equals("2"))
				{
					// by last loan amount
					// First Row
					startRange = getStartRangeLoanAmt1();
					endRange = getEndRangeLoanAmt1();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 1");
					minLoanAmt = getLastLoanMinLoanAmt1();
					maxLoanAmt = getLastLoanMaxLoanAmt1();
					defLoanAmt = getLastLoanDefaultLoanAmt1();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 1");

					//Second Row
					startRange = getStartRangeLoanAmt2();
					endRange = getEndRangeLoanAmt2();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 2");
					minLoanAmt = getLastLoanMinLoanAmt2();
					maxLoanAmt = getLastLoanMaxLoanAmt2();
					defLoanAmt = getLastLoanDefaultLoanAmt2();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 2");

					// Third Row
					startRange = getStartRangeLoanAmt3();
					endRange = getEndRangeLoanAmt3();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 3");
				
					minLoanAmt = getLastLoanMinLoanAmt3();
					maxLoanAmt = getLastLoanMaxLoanAmt3();
					defLoanAmt = getLastLoanDefaultLoanAmt3();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 3");

					//Fourth Row
					startRange = getStartRangeLoanAmt4();
					endRange = getEndRangeLoanAmt4();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 4");
				
					minLoanAmt = getLastLoanMinLoanAmt4();
					maxLoanAmt = getLastLoanMaxLoanAmt4();
					defLoanAmt = getLastLoanDefaultLoanAmt4();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 4");

					//Fifth Row
					startRange = getStartRangeLoanAmt5();
					endRange = getEndRangeLoanAmt5();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 5");
				
					minLoanAmt = getLastLoanMinLoanAmt5();
					maxLoanAmt = getLastLoanMaxLoanAmt5();
					defLoanAmt = getLastLoanDefaultLoanAmt5();					
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 5");					

					//Six Row
					startRange = getStartRangeLoanAmt6();
					endRange = getEndRangeLoanAmt6();										
					validateStartEndRangeLoanAmounts(errors,startRange,endRange,"for by last loan at row 6");
				
					minLoanAmt = getLastLoanMinLoanAmt6();
					maxLoanAmt = getLastLoanMaxLoanAmt6();
					defLoanAmt = getLastLoanDefaultLoanAmt6();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by last loan at row 6");							

				}	
				else if(calctype.equals("3"))			
				{
					// by loan cycle
					// first row
					minLoanAmt = getCycleLoanMinLoanAmt1();
					maxLoanAmt = getCycleLoanMaxLoanAmt1();
					defLoanAmt = getCycleLoanDefaultLoanAmt1();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 1");

					//second row
					minLoanAmt = getCycleLoanMinLoanAmt2();
					maxLoanAmt = getCycleLoanMaxLoanAmt2();
					defLoanAmt = getCycleLoanDefaultLoanAmt2();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 2");

					//third row
					minLoanAmt = getCycleLoanMinLoanAmt3();
					maxLoanAmt = getCycleLoanMaxLoanAmt3();
					defLoanAmt = getCycleLoanDefaultLoanAmt3();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 3");

					//fourth row
					minLoanAmt = getCycleLoanMinLoanAmt4();
					maxLoanAmt = getCycleLoanMaxLoanAmt4();
					defLoanAmt = getCycleLoanDefaultLoanAmt4();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 4");

					//fifth row
					minLoanAmt = getCycleLoanMinLoanAmt5();
					maxLoanAmt = getCycleLoanMaxLoanAmt5();
					defLoanAmt = getCycleLoanDefaultLoanAmt5();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 5");

					//six row(>5)
					minLoanAmt = getCycleLoanMinLoanAmt6();
					maxLoanAmt = getCycleLoanMaxLoanAmt6();
					defLoanAmt = getCycleLoanDefaultLoanAmt6();
					validateMinMaxDefLoanAmounts(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 6");			
				}
		}
	}
	private void validateMinMaxDefLoanAmounts(ActionErrors errors,Double maxLoanAmount, Double minLoanAmount, Double DefLoanAmount,String rownum) {

		String minlnamt = minLoanAmount==null?null:minLoanAmount.toString();
		String maxlnamt = maxLoanAmount==null?null:maxLoanAmount.toString();
		String deflnamt = DefLoanAmount==null?null:DefLoanAmount.toString();

		if(!StringUtils.isNullAndEmptySafe(minlnamt))
		{
			addError(errors,
					ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNT,
					ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNT,rownum);	
		}
		if(!StringUtils.isNullAndEmptySafe(maxlnamt))
		{
			addError(errors,ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNT,
					ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNT,rownum);	
		}		

		if(StringUtils.isNullAndEmptySafe(maxlnamt)&& StringUtils.isNullAndEmptySafe(minlnamt)) {
			if (getDoubleValue(minlnamt) >getDoubleValue(maxlnamt))
				addError(errors,ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT,
						ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT,rownum);				
		}		
		if (StringUtils.isNullAndEmptySafe(deflnamt) && StringUtils.isNullAndEmptySafe(maxlnamt)&& StringUtils.isNullAndEmptySafe(minlnamt)) {				
			if (getDoubleValue(deflnamt) < getDoubleValue(minlnamt) || getDoubleValue(deflnamt) > getDoubleValue(maxlnamt)) {
				addError(errors,ProductDefinitionConstants.ERRORDEFLOANAMOUNT,
						ProductDefinitionConstants.ERRORDEFLOANAMOUNT,rownum);
			}
		}
		else
		{
			addError(errors,ProductDefinitionConstants.ERRORDEFAULTLOANAMOUNT,
					ProductDefinitionConstants.ERRORDEFAULTLOANAMOUNT,rownum);
		}			
	}
	
	private void validateLoanInstallments(ActionErrors errors){
		String calcinsttype = getCalcInstallmentType();
		String minLoanAmt;
		String maxLoanAmt;
		String defLoanAmt;	
		Double startRange;
		Double endRange;
		
		if(!StringUtils.isNullAndEmptySafe(calcinsttype))
		{
			addError(errors,ProductDefinitionConstants.ERRORCALCINSTALLMENTTYPE,
					ProductDefinitionConstants.ERRORCALCINSTALLMENTTYPE);			
		}
		else
		{
			if(calcinsttype.equals("1"))
			{
				// same for all loans 	
				minLoanAmt = (getMinNoInstallments()==null ||getMinNoInstallments().equals(""))?null:getMinNoInstallments();
				maxLoanAmt = (getMaxNoInstallments()==null ||getMaxNoInstallments().equals(""))?null:getMaxNoInstallments();
				defLoanAmt = (getDefNoInstallments()==null ||getDefNoInstallments().equals(""))?null:getDefNoInstallments();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"Same for all loans");
			}
			else
			if(calcinsttype.equals("2"))
			{
				// number of installments  by last loan amount
				// first row
				
				startRange = getStartInstallmentRange1();
				endRange  = getEndInstallmentRange1();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 1");
				minLoanAmt = getMinLoanInstallment1();
				maxLoanAmt = getMaxLoanInstallment1();
				defLoanAmt = getDefLoanInstallment1();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 1");
				
				// second row
				startRange = getStartInstallmentRange2();
				endRange  = getEndInstallmentRange2();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 2");
				
				minLoanAmt = getMinLoanInstallment2();
				maxLoanAmt = getMaxLoanInstallment2();
				defLoanAmt = getDefLoanInstallment2();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 2");
				
				//third row
				startRange = getStartInstallmentRange3();
				endRange  = getEndInstallmentRange3();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 3");
				
				minLoanAmt = getMinLoanInstallment3();
				maxLoanAmt = getMaxLoanInstallment3();
				defLoanAmt = getDefLoanInstallment3();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 3");
				
				//four row
				startRange = getStartInstallmentRange4();
				endRange  = getEndInstallmentRange4();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 4");
				
				minLoanAmt = getMinLoanInstallment4();
				maxLoanAmt = getMaxLoanInstallment4();
				defLoanAmt = getDefLoanInstallment4();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 4");

				// fifth row
				startRange = getStartInstallmentRange5();
				endRange  = getEndInstallmentRange5();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 5");
				
				minLoanAmt = getMinLoanInstallment5();
				maxLoanAmt = getMaxLoanInstallment5();
				defLoanAmt = getDefLoanInstallment5();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 5");
				
				//six row
				startRange = getStartInstallmentRange6();
				endRange  = getEndInstallmentRange6();
				validateStartEndRangeInstallment(errors, startRange, endRange, "for by last loan at row 6");
				
				minLoanAmt = getMinLoanInstallment6();
				maxLoanAmt = getMaxLoanInstallment6();
				defLoanAmt = getDefLoanInstallment6();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for # of last loan Installments at row 6");				
				
			}
			else
			if(calcinsttype.equals("3"))
			{
				// by loan cycle
				//first row 
				minLoanAmt = getMinCycleInstallment1();
				maxLoanAmt = getMaxCycleInstallment1();
				defLoanAmt = getDefCycleInstallment1();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 1");
				
				//Second row
				minLoanAmt = getMinCycleInstallment2();
				maxLoanAmt = getMaxCycleInstallment2();
				defLoanAmt = getDefCycleInstallment2();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 2");
				
				//third row
				minLoanAmt = getMinCycleInstallment3();
				maxLoanAmt = getMaxCycleInstallment3();
				defLoanAmt = getDefCycleInstallment3();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 3");
				
				//fourth row
				minLoanAmt = getMinCycleInstallment4();
				maxLoanAmt = getMaxCycleInstallment4();
				defLoanAmt = getDefCycleInstallment4();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 4");
				
				//fifth row
				minLoanAmt = getMinCycleInstallment5();
				maxLoanAmt = getMaxCycleInstallment5();
				defLoanAmt = getDefCycleInstallment5();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 5");
				
				//six row
				minLoanAmt = getMinCycleInstallment6();
				maxLoanAmt = getMaxCycleInstallment6();
				defLoanAmt = getDefCycleInstallment6();
				validateMinMaxDefInstallments(errors,maxLoanAmt,minLoanAmt,defLoanAmt,"for by loan cycle at row 6");				
			}	
		}	
		
	}
	
	private void validateMinMaxDefInstallments(ActionErrors errors,String maxinst, String mininst, String definst,String rownum) {
		
		String maxNoOfInstall = maxinst==null?null:maxinst.toString();
		String minNoOfInstall = mininst==null?null:mininst.toString();
		String defNoOfInstall = definst==null?null:definst.toString();
		
		if(!StringUtils.isNullAndEmptySafe(minNoOfInstall))
		{
			addError(errors,
					ProductDefinitionConstants.ERRORMINIMUMINSTALLMENT,
					ProductDefinitionConstants.ERRORMINIMUMINSTALLMENT,rownum);	
		}
		if(!StringUtils.isNullAndEmptySafe(maxNoOfInstall))
		{
			addError(errors,ProductDefinitionConstants.ERRORMAXIMUMINSTALLMENT,
							ProductDefinitionConstants.ERRORMAXIMUMINSTALLMENT,rownum);	
		}		
		
		if(StringUtils.isNullAndEmptySafe(maxNoOfInstall)&& StringUtils.isNullAndEmptySafe(minNoOfInstall)) {
			if (getDoubleValue(minNoOfInstall) > getDoubleValue(maxNoOfInstall))
				addError(errors,ProductDefinitionConstants.ERRORMAXMINNOOFINSTALL,
								ProductDefinitionConstants.ERRORMAXMINNOOFINSTALL,rownum);				
			}		
			if (StringUtils.isNullAndEmptySafe(defNoOfInstall) && StringUtils.isNullAndEmptySafe(maxNoOfInstall)&& StringUtils.isNullAndEmptySafe(minNoOfInstall)) {				
				if (getDoubleValue(defNoOfInstall) < getDoubleValue(minNoOfInstall) || getDoubleValue(defNoOfInstall) > getDoubleValue(maxNoOfInstall)) {
					addError(errors,ProductDefinitionConstants.ERRORMINMAXDEFINSTALLMENT,
									ProductDefinitionConstants.ERRORMINMAXDEFINSTALLMENT,rownum);
				}
			}
			else
			{
				addError(errors,ProductDefinitionConstants.ERRORDEFAULTINSTALLMENT,
								ProductDefinitionConstants.ERRORDEFAULTINSTALLMENT,rownum);
			}			
	  }
	private void validateStartEndRangeLoanAmounts(ActionErrors errors,Double StartLoanAmount, Double EndLoanAmnount,String rownum){
		
		String S_StartLoanAmount=StartLoanAmount==null?null:StartLoanAmount.toString();
		String S_EndLoanAmount=EndLoanAmnount==null?null:EndLoanAmnount.toString();
		
		if(!StringUtils.isNullAndEmptySafe(S_StartLoanAmount))
		{
			addError(errors,ProductDefinitionConstants.ERRORSTARTRANGELOANAMOUNT,ProductDefinitionConstants.ERRORSTARTRANGELOANAMOUNT,rownum);	
		}
		if(!StringUtils.isNullAndEmptySafe(S_EndLoanAmount))
		{
			addError(errors,ProductDefinitionConstants.ERRORENDLOANAMOUNT,ProductDefinitionConstants.ERRORENDLOANAMOUNT,rownum);	
		}
		if(StringUtils.isNullAndEmptySafe(S_StartLoanAmount)&& StringUtils.isNullAndEmptySafe(S_EndLoanAmount)) 
		{
			if (Double.valueOf(StartLoanAmount) > Double.valueOf(EndLoanAmnount))
				addError(errors,ProductDefinitionConstants.ERRORSTARTENDLOANAMOUNT,ProductDefinitionConstants.ERRORSTARTENDLOANAMOUNT,rownum);				
		}		
	}
	
	private void validateStartEndRangeInstallment(ActionErrors errors,Double StartInstallmentno, Double EndInstallmentno,String rownum){
		
		String S_StartInstallmentno = StartInstallmentno==null?null:StartInstallmentno.toString();
		String S_EndInstallmentno = EndInstallmentno==null?null:EndInstallmentno.toString();
		if(!StringUtils.isNullAndEmptySafe(S_StartInstallmentno))
		{
			addError(errors,ProductDefinitionConstants.ERRORSTARTRANGEINSTALLMENT,ProductDefinitionConstants.ERRORSTARTRANGEINSTALLMENT,rownum);	
		}
		if(!StringUtils.isNullAndEmptySafe(S_EndInstallmentno))
		{
			addError(errors,ProductDefinitionConstants.ERRORENDINSTALLMENT,ProductDefinitionConstants.ERRORENDINSTALLMENT,rownum);	
		}
		if(StringUtils.isNullAndEmptySafe(S_StartInstallmentno)&& StringUtils.isNullAndEmptySafe(S_EndInstallmentno)) 
		{
			if (Double.valueOf(StartInstallmentno) > Double.valueOf(EndInstallmentno))
				addError(errors,ProductDefinitionConstants.ERRORSTARTENDINSTALLMENT,ProductDefinitionConstants.ERRORSTARTENDINSTALLMENT,rownum);				
		}		
	}
}
