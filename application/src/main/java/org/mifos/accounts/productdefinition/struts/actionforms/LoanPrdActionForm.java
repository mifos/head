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

package org.mifos.accounts.productdefinition.struts.actionforms;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;

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
    
    private Short currencyId;

    private String minLoanAmount;

    private String maxLoanAmount;

    private String defaultLoanAmount;

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

    // FIXME: this could be done more cleanly with a few ordered sets, perhaps?
    // at any rate, something more terse and maintainable should be attempted.
    private String lastLoanMinLoanAmt1;
    private String lastLoanMinLoanAmt2;
    private String lastLoanMinLoanAmt3;
    private String lastLoanMinLoanAmt4;
    private String lastLoanMinLoanAmt5;
    private String lastLoanMinLoanAmt6;

    private String lastLoanMaxLoanAmt1;
    private String lastLoanMaxLoanAmt2;
    private String lastLoanMaxLoanAmt3;
    private String lastLoanMaxLoanAmt4;
    private String lastLoanMaxLoanAmt5;
    private String lastLoanMaxLoanAmt6;

    private String lastLoanDefaultLoanAmt1;
    private String lastLoanDefaultLoanAmt2;
    private String lastLoanDefaultLoanAmt3;
    private String lastLoanDefaultLoanAmt4;
    private String lastLoanDefaultLoanAmt5;
    private String lastLoanDefaultLoanAmt6;

    private String cycleLoanMinLoanAmt1;
    private String cycleLoanMinLoanAmt2;
    private String cycleLoanMinLoanAmt3;
    private String cycleLoanMinLoanAmt4;
    private String cycleLoanMinLoanAmt5;
    private String cycleLoanMinLoanAmt6;

    private String cycleLoanMaxLoanAmt1;
    private String cycleLoanMaxLoanAmt2;
    private String cycleLoanMaxLoanAmt3;
    private String cycleLoanMaxLoanAmt4;
    private String cycleLoanMaxLoanAmt5;
    private String cycleLoanMaxLoanAmt6;

    private String cycleLoanDefaultLoanAmt1;
    private String cycleLoanDefaultLoanAmt2;
    private String cycleLoanDefaultLoanAmt3;
    private String cycleLoanDefaultLoanAmt4;
    private String cycleLoanDefaultLoanAmt5;
    private String cycleLoanDefaultLoanAmt6;

    private Integer startRangeLoanAmt1;
    private Integer startRangeLoanAmt2;
    private Integer startRangeLoanAmt3;
    private Integer startRangeLoanAmt4;
    private Integer startRangeLoanAmt5;
    private Integer startRangeLoanAmt6;

    private Integer endRangeLoanAmt1;
    private Integer endRangeLoanAmt2;
    private Integer endRangeLoanAmt3;
    private Integer endRangeLoanAmt4;
    private Integer endRangeLoanAmt5;
    private Integer endRangeLoanAmt6;

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

    private Integer startInstallmentRange1;
    private Integer startInstallmentRange2;
    private Integer startInstallmentRange3;
    private Integer startInstallmentRange4;
    private Integer startInstallmentRange5;
    private Integer startInstallmentRange6;

    private Integer endInstallmentRange1;
    private Integer endInstallmentRange2;
    private Integer endInstallmentRange3;
    private Integer endInstallmentRange4;
    private Integer endInstallmentRange5;
    private Integer endInstallmentRange6;

    private String calcInstallmentType;

    private Double minLoanAmountValue;
    private Double maxLoanAmountValue;
    private Double defaultLoanAmountValue;

    private Double lastLoanMinLoanAmt1Value;
    private Double lastLoanMinLoanAmt2Value;
    private Double lastLoanMinLoanAmt3Value;
    private Double lastLoanMinLoanAmt4Value;
    private Double lastLoanMinLoanAmt5Value;
    private Double lastLoanMinLoanAmt6Value;

    private Double lastLoanMaxLoanAmt1Value;
    private Double lastLoanMaxLoanAmt2Value;
    private Double lastLoanMaxLoanAmt3Value;
    private Double lastLoanMaxLoanAmt4Value;
    private Double lastLoanMaxLoanAmt5Value;
    private Double lastLoanMaxLoanAmt6Value;

    private Double lastLoanDefaultLoanAmt1Value;
    private Double lastLoanDefaultLoanAmt2Value;
    private Double lastLoanDefaultLoanAmt3Value;
    private Double lastLoanDefaultLoanAmt4Value;
    private Double lastLoanDefaultLoanAmt5Value;
    private Double lastLoanDefaultLoanAmt6Value;

    private Double cycleLoanMinLoanAmt1Value;
    private Double cycleLoanMinLoanAmt2Value;
    private Double cycleLoanMinLoanAmt3Value;
    private Double cycleLoanMinLoanAmt4Value;
    private Double cycleLoanMinLoanAmt5Value;
    private Double cycleLoanMinLoanAmt6Value;

    private Double cycleLoanMaxLoanAmt1Value;
    private Double cycleLoanMaxLoanAmt2Value;
    private Double cycleLoanMaxLoanAmt3Value;
    private Double cycleLoanMaxLoanAmt4Value;
    private Double cycleLoanMaxLoanAmt5Value;
    private Double cycleLoanMaxLoanAmt6Value;

    private Double cycleLoanDefaultLoanAmt1Value;
    private Double cycleLoanDefaultLoanAmt2Value;
    private Double cycleLoanDefaultLoanAmt3Value;
    private Double cycleLoanDefaultLoanAmt4Value;
    private Double cycleLoanDefaultLoanAmt5Value;
    private Double cycleLoanDefaultLoanAmt6Value;

    private Double maxInterestRateValue;
    private Double minInterestRateValue;
    private Double defInterestRateValue;

    public Double getLastLoanDefaultLoanAmt1Value() {
        if (lastLoanDefaultLoanAmt1Value != null)
            return lastLoanDefaultLoanAmt1Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt1);
    }

    public Double getLastLoanDefaultLoanAmt2Value() {
        if (lastLoanDefaultLoanAmt2Value != null)
            return lastLoanDefaultLoanAmt2Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt2);
    }

    public Double getLastLoanDefaultLoanAmt3Value() {
        if (lastLoanDefaultLoanAmt3Value != null)
            return lastLoanDefaultLoanAmt3Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt3);
    }

    public Double getLastLoanDefaultLoanAmt4Value() {
        if (lastLoanDefaultLoanAmt4Value != null)
            return lastLoanDefaultLoanAmt4Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt4);
    }

    public Double getLastLoanDefaultLoanAmt5Value() {
        if (lastLoanDefaultLoanAmt5Value != null)
            return lastLoanDefaultLoanAmt5Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt5);
    }

    public Double getLastLoanDefaultLoanAmt6Value() {
        if (lastLoanDefaultLoanAmt6Value != null)
            return lastLoanDefaultLoanAmt6Value;
        return getDoubleValueForMoney(lastLoanDefaultLoanAmt6);
    }

    public Double getMinLoanAmountValue() {
        if (minLoanAmountValue != null)
            return minLoanAmountValue;
        return getDoubleValueForMoney(minLoanAmount);
    }

    public Double getMaxLoanAmountValue() {
        if (maxLoanAmountValue != null)
            return maxLoanAmountValue;
        return getDoubleValueForMoney(maxLoanAmount);
    }

    public Double getDefaultLoanAmountValue() {
        if (defaultLoanAmountValue != null)
            return defaultLoanAmountValue;
        return getDoubleValueForMoney(defaultLoanAmount);
    }

    public Double getLastLoanMinLoanAmt1Value() {
        if (lastLoanMinLoanAmt1Value != null)
            return lastLoanMinLoanAmt1Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt1);
    }

    public Double getLastLoanMinLoanAmt2Value() {
        if (lastLoanMinLoanAmt2Value != null)
            return lastLoanMinLoanAmt2Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt2);
    }

    public Double getLastLoanMinLoanAmt3Value() {
        if (lastLoanMinLoanAmt3Value != null)
            return lastLoanMinLoanAmt3Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt3);
    }

    public Double getLastLoanMinLoanAmt4Value() {
        if (lastLoanMinLoanAmt4Value != null)
            return lastLoanMinLoanAmt4Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt4);
    }

    public Double getLastLoanMinLoanAmt5Value() {
        if (lastLoanMinLoanAmt5Value != null)
            return lastLoanMinLoanAmt5Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt5);
    }

    public Double getLastLoanMinLoanAmt6Value() {
        if (lastLoanMinLoanAmt6Value != null)
            return lastLoanMinLoanAmt6Value;
        return getDoubleValueForMoney(lastLoanMinLoanAmt6);
    }

    public Double getLastLoanMaxLoanAmt1Value() {
        if (lastLoanMaxLoanAmt1Value != null)
            return lastLoanMaxLoanAmt1Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt1);
    }

    public Double getLastLoanMaxLoanAmt2Value() {
        if (lastLoanMaxLoanAmt2Value != null)
            return lastLoanMaxLoanAmt2Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt2);
    }

    public Double getLastLoanMaxLoanAmt3Value() {
        if (lastLoanMaxLoanAmt3Value != null)
            return lastLoanMaxLoanAmt3Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt3);
    }

    public Double getLastLoanMaxLoanAmt4Value() {
        if (lastLoanMaxLoanAmt4Value != null)
            return lastLoanMaxLoanAmt4Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt4);
    }

    public Double getLastLoanMaxLoanAmt5Value() {
        if (lastLoanMaxLoanAmt5Value != null)
            return lastLoanMaxLoanAmt5Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt5);
    }

    public Double getLastLoanMaxLoanAmt6Value() {
        if (lastLoanMaxLoanAmt6Value != null)
            return lastLoanMaxLoanAmt6Value;
        return getDoubleValueForMoney(lastLoanMaxLoanAmt6);
    }

    public Double getCycleLoanMinLoanAmt1Value() {
        if (cycleLoanMinLoanAmt1Value != null)
            return cycleLoanMinLoanAmt1Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt1);
    }

    public Double getCycleLoanMinLoanAmt2Value() {
        if (cycleLoanMinLoanAmt2Value != null)
            return cycleLoanMinLoanAmt2Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt2);
    }

    public Double getCycleLoanMinLoanAmt3Value() {
        if (cycleLoanMinLoanAmt3Value != null)
            return cycleLoanMinLoanAmt3Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt3);
    }

    public Double getCycleLoanMinLoanAmt4Value() {
        if (cycleLoanMinLoanAmt4Value != null)
            return cycleLoanMinLoanAmt4Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt4);
    }

    public Double getCycleLoanMinLoanAmt5Value() {
        if (cycleLoanMinLoanAmt5Value != null)
            return cycleLoanMinLoanAmt5Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt5);
    }

    public Double getCycleLoanMinLoanAmt6Value() {
        if (cycleLoanMinLoanAmt6Value != null)
            return cycleLoanMinLoanAmt6Value;
        return getDoubleValueForMoney(cycleLoanMinLoanAmt6);
    }

    public Double getCycleLoanMaxLoanAmt1Value() {
        if (cycleLoanMaxLoanAmt1Value != null)
            return cycleLoanMaxLoanAmt1Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt1);
    }

    public Double getCycleLoanMaxLoanAmt2Value() {
        if (cycleLoanMaxLoanAmt2Value != null)
            return cycleLoanMaxLoanAmt2Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt2);
    }

    public Double getCycleLoanMaxLoanAmt3Value() {
        if (cycleLoanMaxLoanAmt3Value != null)
            return cycleLoanMaxLoanAmt3Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt3);
    }

    public Double getCycleLoanMaxLoanAmt4Value() {
        if (cycleLoanMaxLoanAmt4Value != null)
            return cycleLoanMaxLoanAmt4Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt4);
    }

    public Double getCycleLoanMaxLoanAmt5Value() {
        if (cycleLoanMaxLoanAmt5Value != null)
            return cycleLoanMaxLoanAmt5Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt5);
    }

    public Double getCycleLoanMaxLoanAmt6Value() {
        if (cycleLoanMaxLoanAmt6Value != null)
            return cycleLoanMaxLoanAmt6Value;
        return getDoubleValueForMoney(cycleLoanMaxLoanAmt6);
    }

    public Double getCycleLoanDefaultLoanAmt1Value() {
        if (cycleLoanDefaultLoanAmt1Value != null)
            return cycleLoanDefaultLoanAmt1Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt1);
    }

    public Double getCycleLoanDefaultLoanAmt2Value() {
        if (cycleLoanDefaultLoanAmt2Value != null)
            return cycleLoanDefaultLoanAmt2Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt2);
    }

    public Double getCycleLoanDefaultLoanAmt3Value() {
        if (cycleLoanDefaultLoanAmt3Value != null)
            return cycleLoanDefaultLoanAmt3Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt3);
    }

    public Double getCycleLoanDefaultLoanAmt4Value() {
        if (cycleLoanDefaultLoanAmt4Value != null)
            return cycleLoanDefaultLoanAmt4Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt4);
    }

    public Double getCycleLoanDefaultLoanAmt5Value() {
        if (cycleLoanDefaultLoanAmt5Value != null)
            return cycleLoanDefaultLoanAmt5Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt5);
    }

    public Double getCycleLoanDefaultLoanAmt6Value() {
        if (cycleLoanDefaultLoanAmt6Value != null)
            return cycleLoanDefaultLoanAmt6Value;
        return getDoubleValueForMoney(cycleLoanDefaultLoanAmt6);
    }

    public String getCalcInstallmentType() {
        return calcInstallmentType;
    }

    public void setCalcInstallmentType(String calcInstallmentType) {
        this.calcInstallmentType = calcInstallmentType;
    }

    public String getCycleLoanDefaultLoanAmt1() {
        return cycleLoanDefaultLoanAmt1;
    }

    public void setCycleLoanDefaultLoanAmt1(String cycleLoanDefaultLoanAmt1) {
        this.cycleLoanDefaultLoanAmt1 = cycleLoanDefaultLoanAmt1;
    }

    public String getCycleLoanDefaultLoanAmt2() {
        return cycleLoanDefaultLoanAmt2;
    }

    public void setCycleLoanDefaultLoanAmt2(String cycleLoanDefaultLoanAmt2) {
        this.cycleLoanDefaultLoanAmt2 = cycleLoanDefaultLoanAmt2;
    }

    public String getCycleLoanDefaultLoanAmt3() {
        return cycleLoanDefaultLoanAmt3;
    }

    public void setCycleLoanDefaultLoanAmt3(String cycleLoanDefaultLoanAmt3) {
        this.cycleLoanDefaultLoanAmt3 = cycleLoanDefaultLoanAmt3;
    }

    public String getCycleLoanDefaultLoanAmt4() {
        return cycleLoanDefaultLoanAmt4;
    }

    public void setCycleLoanDefaultLoanAmt4(String cycleLoanDefaultLoanAmt4) {
        this.cycleLoanDefaultLoanAmt4 = cycleLoanDefaultLoanAmt4;
    }

    public String getCycleLoanDefaultLoanAmt5() {
        return cycleLoanDefaultLoanAmt5;
    }

    public void setCycleLoanDefaultLoanAmt5(String cycleLoanDefaultLoanAmt5) {
        this.cycleLoanDefaultLoanAmt5 = cycleLoanDefaultLoanAmt5;
    }

    public String getCycleLoanDefaultLoanAmt6() {
        return cycleLoanDefaultLoanAmt6;
    }

    public void setCycleLoanDefaultLoanAmt6(String cycleLoanDefaultLoanAmt6) {
        this.cycleLoanDefaultLoanAmt6 = cycleLoanDefaultLoanAmt6;
    }

    public String getCycleLoanMaxLoanAmt1() {
        return cycleLoanMaxLoanAmt1;
    }

    public void setCycleLoanMaxLoanAmt1(String cycleLoanMaxLoanAmt1) {
        this.cycleLoanMaxLoanAmt1 = cycleLoanMaxLoanAmt1;
    }

    public String getCycleLoanMaxLoanAmt2() {
        return cycleLoanMaxLoanAmt2;
    }

    public void setCycleLoanMaxLoanAmt2(String cycleLoanMaxLoanAmt2) {
        this.cycleLoanMaxLoanAmt2 = cycleLoanMaxLoanAmt2;
    }

    public String getCycleLoanMaxLoanAmt3() {
        return cycleLoanMaxLoanAmt3;
    }

    public void setCycleLoanMaxLoanAmt3(String cycleLoanMaxLoanAmt3) {
        this.cycleLoanMaxLoanAmt3 = cycleLoanMaxLoanAmt3;
    }

    public String getCycleLoanMaxLoanAmt4() {
        return cycleLoanMaxLoanAmt4;
    }

    public void setCycleLoanMaxLoanAmt4(String cycleLoanMaxLoanAmt4) {
        this.cycleLoanMaxLoanAmt4 = cycleLoanMaxLoanAmt4;
    }

    public String getCycleLoanMaxLoanAmt5() {
        return cycleLoanMaxLoanAmt5;
    }

    public void setCycleLoanMaxLoanAmt5(String cycleLoanMaxLoanAmt5) {
        this.cycleLoanMaxLoanAmt5 = cycleLoanMaxLoanAmt5;
    }

    public String getCycleLoanMaxLoanAmt6() {
        return cycleLoanMaxLoanAmt6;
    }

    public void setCycleLoanMaxLoanAmt6(String cycleLoanMaxLoanAmt6) {
        this.cycleLoanMaxLoanAmt6 = cycleLoanMaxLoanAmt6;
    }

    public String getCycleLoanMinLoanAmt1() {
        return cycleLoanMinLoanAmt1;
    }

    public void setCycleLoanMinLoanAmt1(String cycleLoanMinLoanAmt1) {
        this.cycleLoanMinLoanAmt1 = cycleLoanMinLoanAmt1;
    }

    public String getCycleLoanMinLoanAmt2() {
        return cycleLoanMinLoanAmt2;
    }

    public void setCycleLoanMinLoanAmt2(String cycleLoanMinLoanAmt2) {
        this.cycleLoanMinLoanAmt2 = cycleLoanMinLoanAmt2;
    }

    public String getCycleLoanMinLoanAmt3() {
        return cycleLoanMinLoanAmt3;
    }

    public void setCycleLoanMinLoanAmt3(String cycleLoanMinLoanAmt3) {
        this.cycleLoanMinLoanAmt3 = cycleLoanMinLoanAmt3;
    }

    public String getCycleLoanMinLoanAmt4() {
        return cycleLoanMinLoanAmt4;
    }

    public void setCycleLoanMinLoanAmt4(String cycleLoanMinLoanAmt4) {
        this.cycleLoanMinLoanAmt4 = cycleLoanMinLoanAmt4;
    }

    public String getCycleLoanMinLoanAmt5() {
        return cycleLoanMinLoanAmt5;
    }

    public void setCycleLoanMinLoanAmt5(String cycleLoanMinLoanAmt5) {
        this.cycleLoanMinLoanAmt5 = cycleLoanMinLoanAmt5;
    }

    public String getCycleLoanMinLoanAmt6() {
        return cycleLoanMinLoanAmt6;
    }

    public void setCycleLoanMinLoanAmt6(String cycleLoanMinLoanAmt6) {
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

    public Integer getEndInstallmentRange1() {
        return endInstallmentRange1;
    }

    public void setEndInstallmentRange1(Integer endInstallmentRange1) {
        this.endInstallmentRange1 = endInstallmentRange1;
    }

    public Integer getEndInstallmentRange2() {
        return endInstallmentRange2;
    }

    public void setEndInstallmentRange2(Integer endInstallmentRange2) {
        this.endInstallmentRange2 = endInstallmentRange2;
    }

    public Integer getEndInstallmentRange3() {
        return endInstallmentRange3;
    }

    public void setEndInstallmentRange3(Integer endInstallmentRange3) {
        this.endInstallmentRange3 = endInstallmentRange3;
    }

    public Integer getEndInstallmentRange4() {
        return endInstallmentRange4;
    }

    public void setEndInstallmentRange4(Integer endInstallmentRange4) {
        this.endInstallmentRange4 = endInstallmentRange4;
    }

    public Integer getEndInstallmentRange5() {
        return endInstallmentRange5;
    }

    public void setEndInstallmentRange5(Integer endInstallmentRange5) {
        this.endInstallmentRange5 = endInstallmentRange5;
    }

    public Integer getEndInstallmentRange6() {
        return endInstallmentRange6;
    }

    public void setEndInstallmentRange6(Integer endInstallmentRange6) {
        this.endInstallmentRange6 = endInstallmentRange6;
    }

    public Integer getEndRangeLoanAmt1() {
        return endRangeLoanAmt1;
    }

    public void setEndRangeLoanAmt1(Integer endRangeLoanAmt1) {
        this.endRangeLoanAmt1 = endRangeLoanAmt1;
    }

    public Integer getEndRangeLoanAmt2() {
        return endRangeLoanAmt2;
    }

    public void setEndRangeLoanAmt2(Integer endRangeLoanAmt2) {
        this.endRangeLoanAmt2 = endRangeLoanAmt2;
    }

    public Integer getEndRangeLoanAmt3() {
        return endRangeLoanAmt3;
    }

    public void setEndRangeLoanAmt3(Integer endRangeLoanAmt3) {
        this.endRangeLoanAmt3 = endRangeLoanAmt3;
    }

    public Integer getEndRangeLoanAmt4() {
        return endRangeLoanAmt4;
    }

    public void setEndRangeLoanAmt4(Integer endRangeLoanAmt4) {
        this.endRangeLoanAmt4 = endRangeLoanAmt4;
    }

    public Integer getEndRangeLoanAmt5() {
        return endRangeLoanAmt5;
    }

    public void setEndRangeLoanAmt5(Integer endRangeLoanAmt5) {
        this.endRangeLoanAmt5 = endRangeLoanAmt5;
    }

    public Integer getEndRangeLoanAmt6() {
        return endRangeLoanAmt6;
    }

    public void setEndRangeLoanAmt6(Integer endRangeLoanAmt6) {
        this.endRangeLoanAmt6 = endRangeLoanAmt6;
    }

    public String getLastLoanDefaultLoanAmt1() {
        return lastLoanDefaultLoanAmt1;
    }

    public void setLastLoanDefaultLoanAmt1(String lastLoanDefaultLoanAmt1) {
        this.lastLoanDefaultLoanAmt1 = lastLoanDefaultLoanAmt1;
    }

    public String getLastLoanDefaultLoanAmt2() {
        return lastLoanDefaultLoanAmt2;
    }

    public void setLastLoanDefaultLoanAmt2(String lastLoanDefaultLoanAmt2) {
        this.lastLoanDefaultLoanAmt2 = lastLoanDefaultLoanAmt2;
    }

    public String getLastLoanDefaultLoanAmt3() {
        return lastLoanDefaultLoanAmt3;
    }

    public void setLastLoanDefaultLoanAmt3(String lastLoanDefaultLoanAmt3) {
        this.lastLoanDefaultLoanAmt3 = lastLoanDefaultLoanAmt3;
    }

    public String getLastLoanDefaultLoanAmt4() {
        return lastLoanDefaultLoanAmt4;
    }

    public void setLastLoanDefaultLoanAmt4(String lastLoanDefaultLoanAmt4) {
        this.lastLoanDefaultLoanAmt4 = lastLoanDefaultLoanAmt4;
    }

    public String getLastLoanDefaultLoanAmt5() {
        return lastLoanDefaultLoanAmt5;
    }

    public void setLastLoanDefaultLoanAmt5(String lastLoanDefaultLoanAmt5) {
        this.lastLoanDefaultLoanAmt5 = lastLoanDefaultLoanAmt5;
    }

    public String getLastLoanDefaultLoanAmt6() {
        return lastLoanDefaultLoanAmt6;
    }

    public void setLastLoanDefaultLoanAmt6(String lastLoanDefaultLoanAmt6) {
        this.lastLoanDefaultLoanAmt6 = lastLoanDefaultLoanAmt6;
    }

    public String getLastLoanMaxLoanAmt1() {
        return lastLoanMaxLoanAmt1;
    }

    public void setLastLoanMaxLoanAmt1(String lastLoanMaxLoanAmt1) {
        this.lastLoanMaxLoanAmt1 = lastLoanMaxLoanAmt1;
    }

    public String getLastLoanMaxLoanAmt2() {
        return lastLoanMaxLoanAmt2;
    }

    public void setLastLoanMaxLoanAmt2(String lastLoanMaxLoanAmt2) {
        this.lastLoanMaxLoanAmt2 = lastLoanMaxLoanAmt2;
    }

    public String getLastLoanMaxLoanAmt3() {
        return lastLoanMaxLoanAmt3;
    }

    public void setLastLoanMaxLoanAmt3(String lastLoanMaxLoanAmt3) {
        this.lastLoanMaxLoanAmt3 = lastLoanMaxLoanAmt3;
    }

    public String getLastLoanMaxLoanAmt4() {
        return lastLoanMaxLoanAmt4;
    }

    public void setLastLoanMaxLoanAmt4(String lastLoanMaxLoanAmt4) {
        this.lastLoanMaxLoanAmt4 = lastLoanMaxLoanAmt4;
    }

    public String getLastLoanMaxLoanAmt5() {
        return lastLoanMaxLoanAmt5;
    }

    public void setLastLoanMaxLoanAmt5(String lastLoanMaxLoanAmt5) {
        this.lastLoanMaxLoanAmt5 = lastLoanMaxLoanAmt5;
    }

    public String getLastLoanMaxLoanAmt6() {
        return lastLoanMaxLoanAmt6;
    }

    public void setLastLoanMaxLoanAmt6(String lastLoanMaxLoanAmt6) {
        this.lastLoanMaxLoanAmt6 = lastLoanMaxLoanAmt6;
    }

    public String getLastLoanMinLoanAmt1() {
        return lastLoanMinLoanAmt1;
    }

    public void setLastLoanMinLoanAmt1(String lastLoanMinLoanAmt1) {
        this.lastLoanMinLoanAmt1 = lastLoanMinLoanAmt1;
    }

    public String getLastLoanMinLoanAmt2() {
        return lastLoanMinLoanAmt2;
    }

    public void setLastLoanMinLoanAmt2(String lastLoanMinLoanAmt2) {
        this.lastLoanMinLoanAmt2 = lastLoanMinLoanAmt2;
    }

    public String getLastLoanMinLoanAmt3() {
        return lastLoanMinLoanAmt3;
    }

    public void setLastLoanMinLoanAmt3(String lastLoanMinLoanAmt3) {
        this.lastLoanMinLoanAmt3 = lastLoanMinLoanAmt3;
    }

    public String getLastLoanMinLoanAmt4() {
        return lastLoanMinLoanAmt4;
    }

    public void setLastLoanMinLoanAmt4(String lastLoanMinLoanAmt4) {
        this.lastLoanMinLoanAmt4 = lastLoanMinLoanAmt4;
    }

    public String getLastLoanMinLoanAmt5() {
        return lastLoanMinLoanAmt5;
    }

    public void setLastLoanMinLoanAmt5(String lastLoanMinLoanAmt5) {
        this.lastLoanMinLoanAmt5 = lastLoanMinLoanAmt5;
    }

    public String getLastLoanMinLoanAmt6() {
        return lastLoanMinLoanAmt6;
    }

    public void setLastLoanMinLoanAmt6(String lastLoanMinLoanAmt6) {
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

    public Integer getStartInstallmentRange1() {
        return startInstallmentRange1;
    }

    public void setStartInstallmentRange1(Integer startInstallmentRange1) {
        this.startInstallmentRange1 = startInstallmentRange1;
    }

    public Integer getStartInstallmentRange2() {
        return startInstallmentRange2;
    }

    public void setStartInstallmentRange2(Integer startInstallmentRange2) {
        this.startInstallmentRange2 = startInstallmentRange2;
    }

    public Integer getStartInstallmentRange3() {
        return startInstallmentRange3;
    }

    public void setStartInstallmentRange3(Integer startInstallmentRange3) {
        this.startInstallmentRange3 = startInstallmentRange3;
    }

    public Integer getStartInstallmentRange4() {
        return startInstallmentRange4;
    }

    public void setStartInstallmentRange4(Integer startInstallmentRange4) {
        this.startInstallmentRange4 = startInstallmentRange4;
    }

    public Integer getStartInstallmentRange5() {
        return startInstallmentRange5;
    }

    public void setStartInstallmentRange5(Integer startInstallmentRange5) {
        this.startInstallmentRange5 = startInstallmentRange5;
    }

    public Integer getStartInstallmentRange6() {
        return startInstallmentRange6;
    }

    public void setStartInstallmentRange6(Integer startInstallmentRange6) {
        this.startInstallmentRange6 = startInstallmentRange6;
    }

    public Integer getStartRangeLoanAmt1() {
        return startRangeLoanAmt1;
    }

    public void setStartRangeLoanAmt1(Integer startRangeLoanAmt1) {
        this.startRangeLoanAmt1 = startRangeLoanAmt1;
    }

    public Integer getStartRangeLoanAmt2() {
        return startRangeLoanAmt2;
    }

    public void setStartRangeLoanAmt2(Integer startRangeLoanAmt2) {
        this.startRangeLoanAmt2 = startRangeLoanAmt2;
    }

    public Integer getStartRangeLoanAmt3() {
        return startRangeLoanAmt3;
    }

    public void setStartRangeLoanAmt3(Integer startRangeLoanAmt3) {
        this.startRangeLoanAmt3 = startRangeLoanAmt3;
    }

    public Integer getStartRangeLoanAmt4() {
        return startRangeLoanAmt4;
    }

    public void setStartRangeLoanAmt4(Integer startRangeLoanAmt4) {
        this.startRangeLoanAmt4 = startRangeLoanAmt4;
    }

    public Integer getStartRangeLoanAmt5() {
        return startRangeLoanAmt5;
    }

    public void setStartRangeLoanAmt5(Integer startRangeLoanAmt5) {
        this.startRangeLoanAmt5 = startRangeLoanAmt5;
    }

    public Integer getStartRangeLoanAmt6() {
        return startRangeLoanAmt6;
    }

    public void setStartRangeLoanAmt6(Integer startRangeLoanAmt6) {
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

    public String getDefaultLoanAmount() {
        return defaultLoanAmount;
    }

    public void setDefaultLoanAmount(String defaultLoanAmount) {
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

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
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

    public String getMaxLoanAmount() {
        return maxLoanAmount;
    }

    public void setMaxLoanAmount(String maxLoanAmount) {
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

    public String getMinLoanAmount() {
        return minLoanAmount;
    }

    public void setMinLoanAmount(String minLoanAmount) {
        this.minLoanAmount = minLoanAmount;
    }

    public String getMinNoInstallments() {
        return minNoInstallments;
    }

    public void setMinNoInstallments(String minNoInstallments) {
        this.minNoInstallments = minNoInstallments;
    }

    /**
     * Called via reflection from jsp's (I think). Most/all java code should
     * instead call {@link #getPrdApplicableMasterEnum()}
     */
    public String getPrdApplicableMaster() {
        return prdApplicableMaster;
    }

    public ApplicableTo getPrdApplicableMasterEnum() {
        return ApplicableTo.fromInt(Integer.parseInt(prdApplicableMaster));
    }

    /**
     * Called via reflection from jsp's (I think). Most/all java code should
     * instead call {@link #setPrdApplicableMaster(ApplicableTo)}
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

    public Date getStartDateValue(Locale locale) throws InvalidDateException {
        return DateUtils.getLocaleDate(locale, getStartDate());
    }

    public Date getEndDateValue(Locale locale) throws InvalidDateException {
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

    public Double getMaxInterestRateValue() {
        if (maxInterestRateValue != null)
            return maxInterestRateValue;
        return getDoubleValueForInterest(maxInterestRate);
    }

    public Double getMinInterestRateValue() {
        if (minInterestRateValue != null)
            return minInterestRateValue;
        return getDoubleValueForInterest(minInterestRate);
    }

    public Double getDefInterestRateValue() {
        if (defInterestRateValue != null)
            return defInterestRateValue;
        return getDoubleValueForInterest(defInterestRate);
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
        logger.debug("start reset method of Savings Product Action form method :" + method);
        if (method != null && method.equals(Methods.load.toString())) {
            try {
                startDate = DateUtils.getCurrentDate(getUserContext(request).getPreferredLocale());
            } catch (InvalidDateException ide) {
                throw new MifosRuntimeException(ide);
            }
            recurAfter = "1";
            minNoInstallments = "1";
        }
        if (method != null
                && (method.equals(Methods.preview.toString()) || method.equals(Methods.editPreview.toString()))) {
            intDedDisbursementFlag = null;
            prinDueLastInstFlag = null;
            loanCounter = null;
            prdOfferinFees = null;
            loanOfferingFunds = null;
            gracePeriodType = null;
            gracePeriodDuration = null;
        }
        logger.debug("reset method of Savings Product Action form method called ");
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        Locale locale = getUserContext(request).getPreferredLocale();
        String method = request.getParameter(ProductDefinitionConstants.METHOD);
        logger.debug("validate method of Savings Product Action form method called :" + method);
        if (method != null && method.equals(Methods.preview.toString())) {
            errors.add(super.validate(mapping, request));
            validateForPreview(request, errors, locale);
        }
        if (method != null && method.equals(Methods.editPreview.toString())) {
            errors.add(super.validate(mapping, request));
            validateForEditPreview(request, errors, locale);
        }
        if (method != null && !method.equals(Methods.validate.toString())) {
            request.setAttribute(ProductDefinitionConstants.METHODCALLED, method);
        }
        logger.debug("validate method of Savings Product Action form called and error size:" + errors.size());
        return errors;
    }

    private Double getDoubleValueForMoney(String doubleStr) {
        DoubleConversionResult result = parseDoubleForMoney(doubleStr);
        if (result != null)
            return result.getDoubleValue();
        return null;
    }

    private Double getDoubleValueForInterest(String doubleStr) {
        DoubleConversionResult result = parseDoubleForInterest(doubleStr);
        if (result != null)
            return result.getDoubleValue();
        return null;
    }

    public void clear() {
        logger.debug("start clear method of Loan Product Action form method :" + prdOfferingId);
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
        logger.debug("clear method of Loan Product Action form method called :" + prdOfferingId);
    }

    private void validateForPreview(HttpServletRequest request, ActionErrors errors, Locale locale) {
        logger.debug("start validateForPreview method of Loan Product Action form method :" + prdOfferingName);
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                locale);
        String sameForAllLoans = resources.getString(ProductDefinitionConstants.SAMEFORALLLOANS);
        String forByLastLoanAtRow = resources.getString(ProductDefinitionConstants.FORBYLASTLOANATROW);
        String forByLoanCycleAtRow = resources.getString(ProductDefinitionConstants.FORBYLOANCYCLEATROW);
        String forNumberOfLastLoanInstallmentAtRow = resources
                .getString(ProductDefinitionConstants.FORNUMBEROFLASTLOLANINSTALLMENTATROW);
        String rateType = resources.getString("product.ratetype");

        validateStartDate(request, errors);
        validateEndDate(request, errors);
        validateLoanAmount(errors, locale, sameForAllLoans, forByLastLoanAtRow, forByLoanCycleAtRow);
        validateLoanInstallments(errors, sameForAllLoans, forByLastLoanAtRow, forByLoanCycleAtRow,
                forNumberOfLastLoanInstallmentAtRow);
        if (StringUtils.isBlank(getInterestTypes()))
            addError(errors, "interestTypes", ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
                    ConfigurationConstants.INTEREST, request), rateType);
        validateMinMaxDefInterestRates(errors, locale, request);
        vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors, request);
        validatePrincDueOnLastInstAndPrincGraceType(errors);
        setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
        validateInterestGLCode(request, errors);
        logger.debug("validateForPreview method of Loan Product Action form method called :" + prdOfferingName);
    }

    private void validateForEditPreview(HttpServletRequest request, ActionErrors errors, Locale locale) {
        logger.debug("start validateForEditPreview method of Loan Product Action form method :" + prdOfferingName);
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                locale);
        String sameForAllLoans = resources.getString(ProductDefinitionConstants.SAMEFORALLLOANS);
        String forByLastLoanAtRow = resources.getString(ProductDefinitionConstants.FORBYLASTLOANATROW);
        String forByLoanCycleAtRow = resources.getString(ProductDefinitionConstants.FORBYLOANCYCLEATROW);
        String forNumberOfLastLoanInstallmentAtRow = resources
                .getString(ProductDefinitionConstants.FORNUMBEROFLASTLOLANINSTALLMENTATROW);
        String rateType = resources.getString("product.ratetype");
        String status = resources.getString("product.status");
        validateStartDateForEditPreview(request, errors);
        validateEndDate(request, errors);
        validateLoanAmount(errors, locale, sameForAllLoans, forByLastLoanAtRow, forByLoanCycleAtRow);
        validateLoanInstallments(errors, sameForAllLoans, forByLastLoanAtRow, forByLoanCycleAtRow,
                forNumberOfLastLoanInstallmentAtRow);
        if (StringUtils.isBlank(getInterestTypes()))
            addError(errors, "interestTypes", ProductDefinitionConstants.ERRORSSELECTCONFIG, getLabel(
                    ConfigurationConstants.INTEREST, request), rateType);
        if (StringUtils.isBlank(getPrdStatus()))
            addError(errors, "prdStatus", ProductDefinitionConstants.ERROR_SELECT, status);
        validateMinMaxDefInterestRates(errors, locale, request);
        vaildateDecliningInterestSvcChargeDeductedAtDisbursement(errors, request);
        validatePrincDueOnLastInstAndPrincGraceType(errors);
        setSelectedFeesAndFundsAndValidateForFrequency(request, errors);
        validateInterestGLCode(request, errors);
        logger.debug("validateForEditPreview method of Loan Product Action form method called :" + prdOfferingName);
    }

    private void validateStartDateForEditPreview(HttpServletRequest request, ActionErrors errors) {
        logger.debug("start validateStartDateForEditPreview method of Loan Product Action form method :" + startDate);
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        java.util.Date oldStartDate = null;
        Date changedStartDate = null;
        try {
            oldStartDate = (java.util.Date) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
                    request);
        } catch (PageExpiredException e) {
        }
        try {
            changedStartDate = getStartDateValue(getUserContext(request).getPreferredLocale());
        } catch (InvalidDateException ide) {
            addError(errors, "startdate", ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
        }
        if (oldStartDate != null && changedStartDate != null) {
            if (DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime()).compareTo(
                    DateUtils.getCurrentDateWithoutTimeStamp()) <= 0
                    && (changedStartDate != null && DateUtils.getDateWithoutTimeStamp(oldStartDate.getTime())
                            .compareTo(DateUtils.getDateWithoutTimeStamp(changedStartDate.getTime())) != 0)) {
                addError(errors, "startDate", ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION);
            }
        } else if (changedStartDate != null
                && DateUtils.getDateWithoutTimeStamp(changedStartDate.getTime()).compareTo(
                        DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
            validateStartDate(request, errors);

        }
        logger.debug("validateStartDateForEditPreview method of Loan Product Action form method called :" + startDate
                + "---" + oldStartDate);
    }

    private void validateStartDate(HttpServletRequest request, ActionErrors errors) {
        logger.debug("start validateStartDate method of Loan Product Action form method :" + startDate);
        Date startingDate = null;
        try {
            startingDate = getStartDateValue(getUserContext(request).getPreferredLocale());
        } catch (InvalidDateException ide) {
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        }
        if (startingDate != null
                && ((DateUtils.getDateWithoutTimeStamp(startingDate.getTime()).compareTo(
                        DateUtils.getCurrentDateWithoutTimeStamp()) < 0) || (DateUtils.getDateWithoutTimeStamp(
                        startingDate.getTime()).compareTo(DateUtils.getCurrentDateOfNextYearWithOutTimeStamp()) > 0)))
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        logger.debug("validateStartDate method of Loan Product Action form method called :" + startDate);
    }

    private void validateEndDate(HttpServletRequest request, ActionErrors errors) {
        logger.debug("start validateEndDate method of Loan Product Action form method :" + startDate + "---" + endDate);
        Date startingDate = null;
        Date endingDate = null;
        try {
            startingDate = getStartDateValue(getUserContext(request).getPreferredLocale());
        } catch (InvalidDateException ide) {
            addError(errors, "startDate", ProductDefinitionConstants.INVALIDSTARTDATE);
        }
        try {
            endingDate = getEndDateValue(getUserContext(request).getPreferredLocale());
        } catch (InvalidDateException ide) {
            addError(errors, "endDate", ProductDefinitionConstants.INVALIDENDDATE);
        }
        if (startingDate != null && endingDate != null && startingDate.compareTo(endingDate) >= 0)
            addError(errors, "endDate", ProductDefinitionConstants.INVALIDENDDATE);
        logger
                .debug("validateEndDate method of Loan Product Action form method called :" + startDate + "---"
                        + endDate);
    }

    private void setSelectedFeesAndFundsAndValidateForFrequency(HttpServletRequest request, ActionErrors errors) {
        logger.debug("start setSelectedFeesAndFundsAndValidateForFrequency method "
                + "of Loan Product Action form method :");
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        List<FeeView> feeViews = new ArrayList<FeeView>();
        try {
            if (getPrdOfferinFees() != null && getPrdOfferinFees().length > 0) {

                List<FeeBO> fees = (List<FeeBO>) SessionUtils.getAttribute(ProductDefinitionConstants.LOANPRDFEE,
                        request);
                for (String selectedFee : getPrdOfferinFees()) {
                    FeeBO fee = getFeeFromList(fees, selectedFee);
                    if (fee != null) {
                        isFrequencyMatchingOfferingFrequency(fee, errors);
                        if (AccountingRules.isMultiCurrencyEnabled()) {
                            isValidForCurrency(fee, errors, request);
                        }
                        feeViews.add(new FeeView(getUserContext(request), fee));
                    }
                }
            }
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFEESELECTEDLIST, feeViews, request);
        } catch (PageExpiredException e) {
        }
        List<FundBO> selectedFunds = new ArrayList<FundBO>();
        try {
            if (getLoanOfferingFunds() != null && getLoanOfferingFunds().length > 0) {

                List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(ProductDefinitionConstants.SRCFUNDSLIST,
                        request);
                for (String selectedFund : getLoanOfferingFunds()) {
                    FundBO fund = getFundFromList(funds, selectedFund);
                    if (fund != null)
                        selectedFunds.add(fund);
                }
            }
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST, selectedFunds,
                    request);
        } catch (PageExpiredException e) {
        }
        logger.debug("setSelectedFeesAndFundsAndValidateForFrequency method "
                + "of Loan Product Action form method called :");
    }

    private void isValidForCurrency(FeeBO fee, ActionErrors errors, HttpServletRequest request)
            throws PageExpiredException {
        if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
            boolean isLoanCurrencyAndFeeCurrencySame = ((AmountFeeBO) fee).getFeeAmount().getCurrency().getCurrencyId()
                    .equals(getCurrencyId());
            if (getCurrencyId() == null) {
                LoanOfferingBO loanOffering = (LoanOfferingBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
                        request);
                isLoanCurrencyAndFeeCurrencySame = loanOffering.getCurrency().equals(
                        ((AmountFeeBO) fee).getFeeAmount().getCurrency());
            }
            if (!isLoanCurrencyAndFeeCurrencySame) {
                addError(errors, "Fee", ProductDefinitionConstants.ERROR_FEE_CURRENCY_MATCH, fee.getFeeName());
            }
        }
    }

    private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
        logger.debug("getFeeFromList method of Loan Product Action form method called :" + feeSelected);
        for (FeeBO fee : fees)
            if (fee.getFeeId().equals(getShortValue(feeSelected)))
                return fee;
        return null;
    }

    private FundBO getFundFromList(List<FundBO> funds, String fundSelected) {
        logger.debug("getFundFromList method of Loan Product Action form method called :" + fundSelected);
        for (FundBO fund : funds)
            if (fund.getFundId().equals(getShortValue(fundSelected)))
                return fund;
        return null;
    }

    private void isFrequencyMatchingOfferingFrequency(FeeBO fee, ActionErrors errors) {
        logger.debug("start Loan prd Action Form isFrequencyMatchingOfferingFrequency - fee:" + fee);
        if (getFreqOfInstallmentsValue() != null
                && fee.isPeriodic()
                && !(fee.getFeeFrequency().getFeeMeetingFrequency().getMeetingDetails().getRecurrenceType()
                        .getRecurrenceId().equals(getFreqOfInstallmentsValue())))
            addError(errors, "Fee", ProductDefinitionConstants.ERRORFEEFREQUENCY, fee.getFeeName());
        logger.debug("Loan prd Action Form isFrequencyMatchingOfferingFrequency called - fee:" + fee);
    }

    private void vaildateDecliningInterestSvcChargeDeductedAtDisbursement(ActionErrors errors,
            HttpServletRequest request) {
        logger.debug("start Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement :"
                + getInterestTypes() + "---" + getIntDedDisbursementFlag());

        if (getInterestTypes() != null
                && (getInterestTypes().equals(InterestType.DECLINING.getValue().toString()) || getInterestTypes()
                        .equals(InterestType.DECLINING_EPI.getValue().toString()))) {

            if (null != getIntDedDisbursementFlag() && getIntDedDisbursementFlag().equals("1")) {
                errors.add(ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION, new ActionMessage(
                        ProductDefinitionConstants.DECLINEINTERESTDISBURSEMENTDEDUCTION));
            }
        }

        logger.debug("Loan prd Action Form vaildateDecliningInterestSvcChargeDeductedAtDisbursement called ");
    }

    private void validatePrincDueOnLastInstAndPrincGraceType(ActionErrors errors) {
        if (getGracePeriodTypeValue() != null
                && getGracePeriodTypeValue().equals(GraceType.PRINCIPALONLYGRACE.getValue())
                && isPrinDueLastInstValue()) {
            addError(errors, ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE,
                    ProductDefinitionConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
        }
    }

    private void validateInterestGLCode(HttpServletRequest request, ActionErrors errors) {
        if (StringUtils.isBlank(getInterestGLCode())) {
            UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
            Locale locale = userContext.getPreferredLocale();
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                    locale);
            String glCodeFor = resources.getString("product.glCodeFor");
            addError(errors, ProductDefinitionConstants.INTERESTGLCODE, ProductDefinitionConstants.ERROR_SELECT,
                    glCodeFor + getLabel(ConfigurationConstants.INTEREST, request));
        }
    }

    public String getLoanAmtCalcType() {
        return loanAmtCalcType;
    }

    public void setLoanAmtCalcType(String loanAmtCalcType) {
        this.loanAmtCalcType = loanAmtCalcType;
    }

    private void validateLoanAmount(ActionErrors errors, Locale locale, String sameForAllLoans,
            String forByLastLoanAtRow, String forByLoanCycleAtRow) {
        Integer startRange;
        Integer endRange;
        String minLoanAmt = null;
        String maxLoanAmt = null;
        String defLoanAmt = null;

        if (!StringUtils.isNotBlank(getLoanAmtCalcType())) {
            addError(errors, ProductDefinitionConstants.ERRORCALCLOANAMOUNTTYPE,
                    ProductDefinitionConstants.ERRORCALCLOANAMOUNTTYPE);
        }

        if (errors.isEmpty()) {
            short calctype = Short.parseShort(getLoanAmtCalcType());
            if (calctype == ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN) {
                // same for all loans
                validateMinMaxDefLoanAmounts(errors, getMaxLoanAmount(), getMinLoanAmount(), getDefaultLoanAmount(),
                        sameForAllLoans, "", locale);

            } else if (calctype == ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN) {
                // by last loan amount
                // First Row
                startRange = getStartRangeLoanAmt1();
                endRange = getEndRangeLoanAmt1();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "1");
                minLoanAmt = getLastLoanMinLoanAmt1();
                maxLoanAmt = getLastLoanMaxLoanAmt1();
                defLoanAmt = getLastLoanDefaultLoanAmt1();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "1",
                        locale);

                // Second Row
                startRange = getStartRangeLoanAmt2();
                endRange = getEndRangeLoanAmt2();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "2");
                minLoanAmt = getLastLoanMinLoanAmt2();
                maxLoanAmt = getLastLoanMaxLoanAmt2();
                defLoanAmt = getLastLoanDefaultLoanAmt2();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "2",
                        locale);

                // Third Row
                startRange = getStartRangeLoanAmt3();
                endRange = getEndRangeLoanAmt3();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "3");

                minLoanAmt = getLastLoanMinLoanAmt3();
                maxLoanAmt = getLastLoanMaxLoanAmt3();
                defLoanAmt = getLastLoanDefaultLoanAmt3();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "3",
                        locale);

                // Fourth Row
                startRange = getStartRangeLoanAmt4();
                endRange = getEndRangeLoanAmt4();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "4");

                minLoanAmt = getLastLoanMinLoanAmt4();
                maxLoanAmt = getLastLoanMaxLoanAmt4();
                defLoanAmt = getLastLoanDefaultLoanAmt4();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "4",
                        locale);

                // Fifth Row
                startRange = getStartRangeLoanAmt5();
                endRange = getEndRangeLoanAmt5();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "5");

                minLoanAmt = getLastLoanMinLoanAmt5();
                maxLoanAmt = getLastLoanMaxLoanAmt5();
                defLoanAmt = getLastLoanDefaultLoanAmt5();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "5",
                        locale);

                // Six Row
                startRange = getStartRangeLoanAmt6();
                endRange = getEndRangeLoanAmt6();
                validateStartEndRangeLoanAmounts(errors, startRange, endRange, forByLastLoanAtRow, "6");

                minLoanAmt = getLastLoanMinLoanAmt6();
                maxLoanAmt = getLastLoanMaxLoanAmt6();
                defLoanAmt = getLastLoanDefaultLoanAmt6();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLastLoanAtRow, "6",
                        locale);

            } else if (calctype == ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE) {
                // by loan cycle
                // first row
                minLoanAmt = getCycleLoanMinLoanAmt1();
                maxLoanAmt = getCycleLoanMaxLoanAmt1();
                defLoanAmt = getCycleLoanDefaultLoanAmt1();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "1",
                        locale);

                // second row
                minLoanAmt = getCycleLoanMinLoanAmt2();
                maxLoanAmt = getCycleLoanMaxLoanAmt2();
                defLoanAmt = getCycleLoanDefaultLoanAmt2();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "2",
                        locale);

                // third row
                minLoanAmt = getCycleLoanMinLoanAmt3();
                maxLoanAmt = getCycleLoanMaxLoanAmt3();
                defLoanAmt = getCycleLoanDefaultLoanAmt3();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "3",
                        locale);

                // fourth row
                minLoanAmt = getCycleLoanMinLoanAmt4();
                maxLoanAmt = getCycleLoanMaxLoanAmt4();
                defLoanAmt = getCycleLoanDefaultLoanAmt4();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "4",
                        locale);

                // fifth row
                minLoanAmt = getCycleLoanMinLoanAmt5();
                maxLoanAmt = getCycleLoanMaxLoanAmt5();
                defLoanAmt = getCycleLoanDefaultLoanAmt5();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "5",
                        locale);

                // six row(>5)
                minLoanAmt = getCycleLoanMinLoanAmt6();
                maxLoanAmt = getCycleLoanMaxLoanAmt6();
                defLoanAmt = getCycleLoanDefaultLoanAmt6();
                validateMinMaxDefLoanAmounts(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "6",
                        locale);
            }
        }
    }

    private void setLoanAmounts(Double minLoanAmt, Double maxLoanAmt, Double defLoanAmt, String rownum) {
        short calctype = Short.parseShort(getLoanAmtCalcType());

        if (calctype == ProductDefinitionConstants.LOANAMOUNTSAMEFORALLLOAN) {
            minLoanAmountValue = minLoanAmt;
            maxLoanAmountValue = maxLoanAmt;
            defaultLoanAmountValue = defLoanAmt;
        } else if (calctype == ProductDefinitionConstants.LOANAMOUNTFROMLASTLOAN) {
            if (rownum.equals("1")) {
                lastLoanMinLoanAmt1Value = minLoanAmt;
                lastLoanMaxLoanAmt1Value = maxLoanAmt;
                lastLoanDefaultLoanAmt1Value = defLoanAmt;
            } else if (rownum.equals("2")) {
                lastLoanMinLoanAmt2Value = minLoanAmt;
                lastLoanMaxLoanAmt2Value = maxLoanAmt;
                lastLoanDefaultLoanAmt2Value = defLoanAmt;
            } else if (rownum.equals("3")) {
                lastLoanMinLoanAmt3Value = minLoanAmt;
                lastLoanMaxLoanAmt3Value = maxLoanAmt;
                lastLoanDefaultLoanAmt3Value = defLoanAmt;
            } else if (rownum.equals("4")) {
                lastLoanMinLoanAmt4Value = minLoanAmt;
                lastLoanMaxLoanAmt4Value = maxLoanAmt;
                lastLoanDefaultLoanAmt4Value = defLoanAmt;
            } else if (rownum.equals("5")) {
                lastLoanMinLoanAmt5Value = minLoanAmt;
                lastLoanMaxLoanAmt5Value = maxLoanAmt;
                lastLoanDefaultLoanAmt5Value = defLoanAmt;
            } else if (rownum.equals("6")) {
                lastLoanMinLoanAmt6Value = minLoanAmt;
                lastLoanMaxLoanAmt6Value = maxLoanAmt;
                lastLoanDefaultLoanAmt6Value = defLoanAmt;
            }
        } else if (calctype == ProductDefinitionConstants.LOANAMOUNTFROMLOANCYCLE) {
            if (rownum.equals("1")) {
                cycleLoanMinLoanAmt1Value = minLoanAmt;
                cycleLoanMaxLoanAmt1Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt1Value = defLoanAmt;
            } else if (rownum.equals("2")) {
                cycleLoanMinLoanAmt2Value = minLoanAmt;
                cycleLoanMaxLoanAmt2Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt2Value = defLoanAmt;
            } else if (rownum.equals("3")) {
                cycleLoanMinLoanAmt3Value = minLoanAmt;
                cycleLoanMaxLoanAmt3Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt3Value = defLoanAmt;
            } else if (rownum.equals("4")) {
                cycleLoanMinLoanAmt4Value = minLoanAmt;
                cycleLoanMaxLoanAmt4Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt4Value = defLoanAmt;
            } else if (rownum.equals("5")) {
                cycleLoanMinLoanAmt5Value = minLoanAmt;
                cycleLoanMaxLoanAmt5Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt5Value = defLoanAmt;
            } else if (rownum.equals("6")) {
                cycleLoanMinLoanAmt6Value = minLoanAmt;
                cycleLoanMaxLoanAmt6Value = maxLoanAmt;
                cycleLoanDefaultLoanAmt6Value = defLoanAmt;
            }
        }
    }

    private void validateMinMaxDefInterestRates(ActionErrors errors, Locale locale, HttpServletRequest request) {

        DoubleConversionResult minInterestResult = null;
        DoubleConversionResult maxInterestResult = null;
        DoubleConversionResult defInterestResult = null;
        List<ConversionError> errorList = null;
        Double maxInterest = null;
        Double minInterest = null;
        Double defInterest = null;
        String label = getLabel(ConfigurationConstants.INTEREST, request);
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                locale);
        String prdrate = resources.getString("product.prdrate");
        String min = resources.getString("product.min");
        String max = resources.getString("product.max");
        String defaultString = resources.getString("product.default");
        if (!StringUtils.isNotBlank(minInterestRate)) {
            addError(errors, "minInterestRate", ProductDefinitionConstants.ERRORSENTERCONFIG, min, label, prdrate);
        } else {
            minInterestResult = parseDoubleForInterest(minInterestRate);
            errorList = minInterestResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORMININTERESTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORMININTERESTINVALIDFORMAT, getConversionErrorText(errorList
                                    .get(i), locale));
            } else {
                minInterest = minInterestResult.getDoubleValue();
            }
        }

        if (!StringUtils.isNotBlank(maxInterestRate)) {
            addError(errors, "maxInterestRate", ProductDefinitionConstants.ERRORSENTERCONFIG, max, label, prdrate);
        } else {
            maxInterestResult = parseDoubleForInterest(maxInterestRate);
            errorList = maxInterestResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORMAXINTERESTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORMAXINTERESTINVALIDFORMAT, getConversionErrorText(errorList
                                    .get(i), locale));
            } else
                maxInterest = maxInterestResult.getDoubleValue();
        }

        if (!StringUtils.isNotBlank(defInterestRate)) {
            addError(errors, "defInterestRate", ProductDefinitionConstants.ERRORSENTERCONFIG, defaultString, label,
                    prdrate);
        } else {
            defInterestResult = parseDoubleForInterest(defInterestRate);
            errorList = defInterestResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORDEFINTERESTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORDEFINTERESTINVALIDFORMAT, getConversionErrorText(errorList
                                    .get(i), locale));
            } else
                defInterest = defInterestResult.getDoubleValue();

        }
        if ((minInterest != null) && (maxInterest != null)) {
            if (minInterest > maxInterest) {
                addError(errors, "MinMaxInterestRate", ProductDefinitionConstants.ERRORSMINMAXINTCONFIG, max, label,
                        prdrate, min);
            }
            if (defInterest != null) {
                if ((defInterest < minInterest) || (defInterest > maxInterest)) {
                    addError(errors, "DefInterestRate", ProductDefinitionConstants.ERRORSDEFINTCONFIG, defaultString,
                            label, prdrate, min, max);
                } else {
                    minInterestRateValue = minInterest;
                    maxInterestRateValue = maxInterest;
                    defInterestRateValue = defInterest;
                }

            }

        }

    }

    private void validateMinMaxDefLoanAmounts(ActionErrors errors, String maxLoanAmountStr, String minLoanAmountStr,
            String defLoanAmountStr, String error, String rownum, Locale locale) {

        DoubleConversionResult minLoanResult = null;
        DoubleConversionResult maxLoanResult = null;
        DoubleConversionResult defaultLoanResult = null;
        List<ConversionError> errorList = null;
        Double maxLoanAmt = null;
        Double minLoanAmt = null;
        Double defLoanAmt = null;

        if (!StringUtils.isNotBlank(minLoanAmountStr)) {
            addError(errors, ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNT,
                    ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNT, error, rownum);
        } else {
            minLoanResult = parseDoubleForMoney(minLoanAmountStr);
            errorList = minLoanResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORMINIMUMLOANAMOUNTINVALIDFORMAT, error, rownum,
                            getConversionErrorText(errorList.get(i), locale));
            } else
                minLoanAmt = minLoanResult.getDoubleValue();
        }

        if (!StringUtils.isNotBlank(maxLoanAmountStr)) {
            addError(errors, ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNT,
                    ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNT, error, rownum);
        } else {
            maxLoanResult = parseDoubleForMoney(maxLoanAmountStr);
            errorList = maxLoanResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORMAXIMUMLOANAMOUNTINVALIDFORMAT, error, rownum,
                            getConversionErrorText(errorList.get(i), locale));
            } else
                maxLoanAmt = maxLoanResult.getDoubleValue();
        }

        if (!StringUtils.isNotBlank(defLoanAmountStr)) {
            addError(errors, ProductDefinitionConstants.ERRORDEFLOANAMOUNT,
                    ProductDefinitionConstants.ERRORDEFLOANAMOUNT, error, rownum);
        } else {
            defaultLoanResult = parseDoubleForMoney(defLoanAmountStr);
            errorList = defaultLoanResult.getErrors();
            if (errorList.size() > 0) {
                for (int i = 0; i < errorList.size(); i++)
                    addError(errors, ProductDefinitionConstants.ERRORDEFAULTLOANAMOUNTINVALIDFORMAT,
                            ProductDefinitionConstants.ERRORDEFAULTLOANAMOUNTINVALIDFORMAT, error, rownum,
                            getConversionErrorText(errorList.get(i), locale));
            } else
                defLoanAmt = defaultLoanResult.getDoubleValue();

        }
        if ((minLoanAmt != null) && (maxLoanAmt != null)) {
            if (minLoanAmt > maxLoanAmt)
                addError(errors, ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT,
                        ProductDefinitionConstants.ERRORMAXMINLOANAMOUNT, error, rownum);
            if (defLoanAmt != null) {
                if ((defLoanAmt < minLoanAmt) || (defLoanAmt > maxLoanAmt))
                    addError(errors, ProductDefinitionConstants.ERRORDEFLOANAMOUNT,
                            ProductDefinitionConstants.ERRORDEFLOANAMOUNT, error, rownum);
                else
                    setLoanAmounts(minLoanAmt, maxLoanAmt, defLoanAmt, rownum);

            }
        }

    }

    private void validateLoanInstallments(ActionErrors errors, String sameForAllLoans, String forByLastLoanAtRow,
            String forByLoanCycleAtRow, String forNumberOfLastLoanInstallmentAtRow) {
        String minLoanAmt;
        String maxLoanAmt;
        String defLoanAmt;
        Integer startRange;
        Integer endRange;

        if (!StringUtils.isNotBlank(getCalcInstallmentType())) {
            addError(errors, ProductDefinitionConstants.ERRORCALCINSTALLMENTTYPE,
                    ProductDefinitionConstants.ERRORCALCINSTALLMENTTYPE);
        }

        if (errors.isEmpty()) {
            short calcinsttype = Short.parseShort(getCalcInstallmentType());
            if (calcinsttype == ProductDefinitionConstants.NOOFINSTALLSAMEFORALLLOAN) {
                // same for all loans
                minLoanAmt = (getMinNoInstallments() == null || getMinNoInstallments().equals("")) ? null
                        : getMinNoInstallments();
                maxLoanAmt = (getMaxNoInstallments() == null || getMaxNoInstallments().equals("")) ? null
                        : getMaxNoInstallments();
                defLoanAmt = (getDefNoInstallments() == null || getDefNoInstallments().equals("")) ? null
                        : getDefNoInstallments();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, sameForAllLoans, "");
            } else if (calcinsttype == ProductDefinitionConstants.NOOFINSTALLFROMLASTLOAN) {
                // number of installments by last loan amount
                // first row

                startRange = getStartInstallmentRange1();
                endRange = getEndInstallmentRange1();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "1");
                minLoanAmt = getMinLoanInstallment1();
                maxLoanAmt = getMaxLoanInstallment1();
                defLoanAmt = getDefLoanInstallment1();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "1");

                // second row
                startRange = getStartInstallmentRange2();
                endRange = getEndInstallmentRange2();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "2");

                minLoanAmt = getMinLoanInstallment2();
                maxLoanAmt = getMaxLoanInstallment2();
                defLoanAmt = getDefLoanInstallment2();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "2");

                // third row
                startRange = getStartInstallmentRange3();
                endRange = getEndInstallmentRange3();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "3");

                minLoanAmt = getMinLoanInstallment3();
                maxLoanAmt = getMaxLoanInstallment3();
                defLoanAmt = getDefLoanInstallment3();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "3");

                // four row
                startRange = getStartInstallmentRange4();
                endRange = getEndInstallmentRange4();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "4");

                minLoanAmt = getMinLoanInstallment4();
                maxLoanAmt = getMaxLoanInstallment4();
                defLoanAmt = getDefLoanInstallment4();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "4");

                // fifth row
                startRange = getStartInstallmentRange5();
                endRange = getEndInstallmentRange5();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "5");

                minLoanAmt = getMinLoanInstallment5();
                maxLoanAmt = getMaxLoanInstallment5();
                defLoanAmt = getDefLoanInstallment5();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "5");

                // six row
                startRange = getStartInstallmentRange6();
                endRange = getEndInstallmentRange6();
                validateStartEndRangeInstallment(errors, startRange, endRange, forByLastLoanAtRow, "6");

                minLoanAmt = getMinLoanInstallment6();
                maxLoanAmt = getMaxLoanInstallment6();
                defLoanAmt = getDefLoanInstallment6();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt,
                        forNumberOfLastLoanInstallmentAtRow, "6");

            } else if (calcinsttype == ProductDefinitionConstants.NOOFINSTALLFROMLOANCYCLLE) {
                // by loan cycle
                // first row
                minLoanAmt = getMinCycleInstallment1();
                maxLoanAmt = getMaxCycleInstallment1();
                defLoanAmt = getDefCycleInstallment1();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "1");

                // Second row
                minLoanAmt = getMinCycleInstallment2();
                maxLoanAmt = getMaxCycleInstallment2();
                defLoanAmt = getDefCycleInstallment2();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "2");

                // third row
                minLoanAmt = getMinCycleInstallment3();
                maxLoanAmt = getMaxCycleInstallment3();
                defLoanAmt = getDefCycleInstallment3();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "3");

                // fourth row
                minLoanAmt = getMinCycleInstallment4();
                maxLoanAmt = getMaxCycleInstallment4();
                defLoanAmt = getDefCycleInstallment4();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "4");

                // fifth row
                minLoanAmt = getMinCycleInstallment5();
                maxLoanAmt = getMaxCycleInstallment5();
                defLoanAmt = getDefCycleInstallment5();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "5");

                // six row
                minLoanAmt = getMinCycleInstallment6();
                maxLoanAmt = getMaxCycleInstallment6();
                defLoanAmt = getDefCycleInstallment6();
                validateMinMaxDefInstallments(errors, maxLoanAmt, minLoanAmt, defLoanAmt, forByLoanCycleAtRow, "6");
            }
        }

    }

    private void validateMinMaxDefInstallments(ActionErrors errors, String maxinst, String mininst, String definst,
            String error, String rownum) {

        String maxNoOfInstall = maxinst == null ? null : maxinst.toString();
        String minNoOfInstall = mininst == null ? null : mininst.toString();
        String defNoOfInstall = definst == null ? null : definst.toString();

        if (!StringUtils.isNotBlank(minNoOfInstall)) {
            addError(errors, ProductDefinitionConstants.ERRORMINIMUMINSTALLMENT,
                    ProductDefinitionConstants.ERRORMINIMUMINSTALLMENT, error, rownum);
        }
        if (!StringUtils.isNotBlank(maxNoOfInstall)) {
            addError(errors, ProductDefinitionConstants.ERRORMAXIMUMINSTALLMENT,
                    ProductDefinitionConstants.ERRORMAXIMUMINSTALLMENT, error, rownum);
        }
        if (!StringUtils.isNotBlank(defNoOfInstall)) {
            addError(errors, ProductDefinitionConstants.ERRORDEFAULTINSTALLMENT,
                    ProductDefinitionConstants.ERRORDEFAULTINSTALLMENT, error, rownum);
        }

        if (StringUtils.isNotBlank(maxNoOfInstall) && StringUtils.isNotBlank(minNoOfInstall)) {
            if (Integer.parseInt(minNoOfInstall) > Integer.parseInt(maxNoOfInstall))
                addError(errors, ProductDefinitionConstants.ERRORMAXMINNOOFINSTALL,
                        ProductDefinitionConstants.ERRORMAXMINNOOFINSTALL, error, rownum);
        }
        if (StringUtils.isNotBlank(defNoOfInstall) && StringUtils.isNotBlank(maxNoOfInstall)
                && StringUtils.isNotBlank(minNoOfInstall)) {
            if (Integer.parseInt(defNoOfInstall) < Integer.parseInt(minNoOfInstall)
                    || Integer.parseInt(defNoOfInstall) > Integer.parseInt(maxNoOfInstall)) {
                addError(errors, ProductDefinitionConstants.ERRORMINMAXDEFINSTALLMENT,
                        ProductDefinitionConstants.ERRORMINMAXDEFINSTALLMENT, error, rownum);
            }
        }
    }

    private void validateStartEndRangeLoanAmounts(ActionErrors errors, Integer StartLoanAmount, Integer EndLoanAmnount,
            String error, String rownum) {

        String S_StartLoanAmount = StartLoanAmount == null ? null : StartLoanAmount.toString();
        String S_EndLoanAmount = EndLoanAmnount == null ? null : EndLoanAmnount.toString();

        if (!StringUtils.isNotBlank(S_StartLoanAmount)) {
            addError(errors, ProductDefinitionConstants.ERRORSTARTRANGELOANAMOUNT,
                    ProductDefinitionConstants.ERRORSTARTRANGELOANAMOUNT, error, rownum);
        }
        if (!StringUtils.isNotBlank(S_EndLoanAmount)) {
            addError(errors, ProductDefinitionConstants.ERRORENDLOANAMOUNT,
                    ProductDefinitionConstants.ERRORENDLOANAMOUNT, error, rownum);
        }
        if (StringUtils.isNotBlank(S_StartLoanAmount) && StringUtils.isNotBlank(S_EndLoanAmount)) {
            if (StartLoanAmount > EndLoanAmnount)
                addError(errors, ProductDefinitionConstants.ERRORSTARTENDLOANAMOUNT,
                        ProductDefinitionConstants.ERRORSTARTENDLOANAMOUNT, error, rownum);
        }
    }

    private void validateStartEndRangeInstallment(ActionErrors errors, Integer StartInstallmentno,
            Integer EndInstallmentno, String error, String rownum) {

        String S_StartInstallmentno = StartInstallmentno == null ? null : StartInstallmentno.toString();
        String S_EndInstallmentno = EndInstallmentno == null ? null : EndInstallmentno.toString();
        if (!StringUtils.isNotBlank(S_StartInstallmentno)) {
            addError(errors, ProductDefinitionConstants.ERRORSTARTRANGEINSTALLMENT,
                    ProductDefinitionConstants.ERRORSTARTRANGEINSTALLMENT, error, rownum);
        }
        if (!StringUtils.isNotBlank(S_EndInstallmentno)) {
            addError(errors, ProductDefinitionConstants.ERRORENDINSTALLMENT,
                    ProductDefinitionConstants.ERRORENDINSTALLMENT, error, rownum);
        }
        if (StringUtils.isNotBlank(S_StartInstallmentno) && StringUtils.isNotBlank(S_EndInstallmentno)) {
            if (StartInstallmentno > EndInstallmentno)
                addError(errors, ProductDefinitionConstants.ERRORSTARTENDINSTALLMENT,
                        ProductDefinitionConstants.ERRORSTARTENDINSTALLMENT, error, rownum);
        }
    }
}
