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

package org.mifos.application.productdefinition.business;

import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;

public class LoanOfferingTestUtils {

    public static void setStatus(LoanOfferingBO loanOffering, PrdStatusEntity prdStatus) {
        loanOffering.setPrdStatus(prdStatus);
    }

    public static void setGracePeriodType(LoanOfferingBO loanOffering, GracePeriodTypeEntity gracePeriodType) {
        loanOffering.setGracePeriodType(gracePeriodType);
    }

    public static LoanPrdActionForm populateLoanAmountFromLastLoanAmount(String loanAmtCalcType, Integer startRangeLoanAmt1,
            Integer endRangeLoanAmt1, Integer startRangeLoanAmt2, Integer endRangeLoanAmt2, Integer startRangeLoanAmt3,
            Integer endRangeLoanAmt3, Integer startRangeLoanAmt4, Integer endRangeLoanAmt4, Integer startRangeLoanAmt5,
            Integer endRangeLoanAmt5, Integer startRangeLoanAmt6, Integer endRangeLoanAmt6, Double lastLoanMinLoanAmt1,
            Double lastLoanMaxLoanAmt1, Double lastLoanDefaultLoanAmt1, Double lastLoanMinLoanAmt2,
            Double lastLoanMaxLoanAmt2, Double lastLoanDefaultLoanAmt2, Double lastLoanMinLoanAmt3,
            Double lastLoanMaxLoanAmt3, Double lastLoanDefaultLoanAmt3, Double lastLoanMinLoanAmt4,
            Double lastLoanMaxLoanAmt4, Double lastLoanDefaultLoanAmt4, Double lastLoanMinLoanAmt5,
            Double lastLoanMaxLoanAmt5, Double lastLoanDefaultLoanAmt5, Double lastLoanMinLoanAmt6,
            Double lastLoanMaxLoanAmt6, Double lastLoanDefaultLoanAmt6, LoanPrdActionForm loanPrdActionForm) {

        if (loanAmtCalcType.equals("2")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setStartRangeLoanAmt1(startRangeLoanAmt1);
            loanPrdActionForm.setStartRangeLoanAmt2(startRangeLoanAmt2);
            loanPrdActionForm.setStartRangeLoanAmt3(startRangeLoanAmt3);
            loanPrdActionForm.setStartRangeLoanAmt4(startRangeLoanAmt4);
            loanPrdActionForm.setStartRangeLoanAmt5(startRangeLoanAmt5);
            loanPrdActionForm.setStartRangeLoanAmt6(startRangeLoanAmt6);
            loanPrdActionForm.setEndRangeLoanAmt1(endRangeLoanAmt1);
            loanPrdActionForm.setEndRangeLoanAmt2(endRangeLoanAmt2);
            loanPrdActionForm.setEndRangeLoanAmt3(endRangeLoanAmt3);
            loanPrdActionForm.setEndRangeLoanAmt4(endRangeLoanAmt4);
            loanPrdActionForm.setEndRangeLoanAmt5(endRangeLoanAmt5);
            loanPrdActionForm.setEndRangeLoanAmt6(endRangeLoanAmt6);
            loanPrdActionForm.setLastLoanMaxLoanAmt1(lastLoanMaxLoanAmt1.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt2(lastLoanMaxLoanAmt2.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt3(lastLoanMaxLoanAmt3.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt4(lastLoanMaxLoanAmt4.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt5(lastLoanMaxLoanAmt5.toString());
            loanPrdActionForm.setLastLoanMaxLoanAmt6(lastLoanMaxLoanAmt6.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt1(lastLoanMinLoanAmt1.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt2(lastLoanMinLoanAmt2.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt3(lastLoanMinLoanAmt3.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt4(lastLoanMinLoanAmt4.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt5(lastLoanMinLoanAmt5.toString());
            loanPrdActionForm.setLastLoanMinLoanAmt6(lastLoanMinLoanAmt6.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt1(lastLoanDefaultLoanAmt1.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt2(lastLoanDefaultLoanAmt2.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt3(lastLoanDefaultLoanAmt3.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt4(lastLoanDefaultLoanAmt4.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt5(lastLoanDefaultLoanAmt5.toString());
            loanPrdActionForm.setLastLoanDefaultLoanAmt6(lastLoanDefaultLoanAmt6.toString());

        }
        return loanPrdActionForm;
    }

    public static LoanPrdActionForm populateNoOfInstallFromLastLoanAmount(String calcInstallmentType,
            Integer startInstallmentRange1, Integer endInstallmentRange1, Integer startInstallmentRange2,
            Integer endInstallmentRange2, Integer startInstallmentRange3, Integer endInstallmentRange3,
            Integer startInstallmentRange4, Integer endInstallmentRange4, Integer startInstallmentRange5,
            Integer endInstallmentRange5, Integer startInstallmentRange6, Integer endInstallmentRange6,
            String minLoanInstallment1, String maxLoanInstallment1, String defLoanInstallment1,
            String minLoanInstallment2, String maxLoanInstallment2, String defLoanInstallment2,
            String minLoanInstallment3, String maxLoanInstallment3, String defLoanInstallment3,
            String minLoanInstallment4, String maxLoanInstallment4, String defLoanInstallment4,
            String minLoanInstallment5, String maxLoanInstallment5, String defLoanInstallment5,
            String minLoanInstallment6, String maxLoanInstallment6, String defLoanInstallment6,
            LoanPrdActionForm loanPrdActionForm) {

        if (calcInstallmentType.equals("2")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setStartInstallmentRange1(startInstallmentRange1);
            loanPrdActionForm.setStartInstallmentRange2(startInstallmentRange2);
            loanPrdActionForm.setStartInstallmentRange3(startInstallmentRange3);
            loanPrdActionForm.setStartInstallmentRange4(startInstallmentRange4);
            loanPrdActionForm.setStartInstallmentRange5(startInstallmentRange5);
            loanPrdActionForm.setStartInstallmentRange6(startInstallmentRange6);
            loanPrdActionForm.setEndInstallmentRange1(endInstallmentRange1);
            loanPrdActionForm.setEndInstallmentRange2(endInstallmentRange2);
            loanPrdActionForm.setEndInstallmentRange3(endInstallmentRange3);
            loanPrdActionForm.setEndInstallmentRange4(endInstallmentRange4);
            loanPrdActionForm.setEndInstallmentRange5(endInstallmentRange5);
            loanPrdActionForm.setEndInstallmentRange6(endInstallmentRange6);
            loanPrdActionForm.setMaxLoanInstallment1(maxLoanInstallment1);
            loanPrdActionForm.setMaxLoanInstallment2(maxLoanInstallment2);
            loanPrdActionForm.setMaxLoanInstallment3(maxLoanInstallment3);
            loanPrdActionForm.setMaxLoanInstallment4(maxLoanInstallment4);
            loanPrdActionForm.setMaxLoanInstallment5(maxLoanInstallment5);
            loanPrdActionForm.setMaxLoanInstallment6(maxLoanInstallment6);
            loanPrdActionForm.setMinLoanInstallment1(minLoanInstallment1);
            loanPrdActionForm.setMinLoanInstallment2(minLoanInstallment2);
            loanPrdActionForm.setMinLoanInstallment3(minLoanInstallment3);
            loanPrdActionForm.setMinLoanInstallment4(minLoanInstallment4);
            loanPrdActionForm.setMinLoanInstallment5(minLoanInstallment5);
            loanPrdActionForm.setMinLoanInstallment6(minLoanInstallment6);
            loanPrdActionForm.setDefLoanInstallment1(defLoanInstallment1);
            loanPrdActionForm.setDefLoanInstallment2(defLoanInstallment2);
            loanPrdActionForm.setDefLoanInstallment3(defLoanInstallment3);
            loanPrdActionForm.setDefLoanInstallment4(defLoanInstallment4);
            loanPrdActionForm.setDefLoanInstallment5(defLoanInstallment5);
            loanPrdActionForm.setDefLoanInstallment6(defLoanInstallment6);
        }
        return loanPrdActionForm;
    }

    public static LoanPrdActionForm populateLoanAmountFromLoanCycle(String loanAmtCalcType, Double cycleLoanMinLoanAmt1,
            Double cycleLoanMaxLoanAmt1, Double cycleLoanDefaultLoanAmt1, Double cycleLoanMinLoanAmt2,
            Double cycleLoanMaxLoanAmt2, Double cycleLoanDefaultLoanAmt2, Double cycleLoanMinLoanAmt3,
            Double cycleLoanMaxLoanAmt3, Double cycleLoanDefaultLoanAmt3, Double cycleLoanMinLoanAmt4,
            Double cycleLoanMaxLoanAmt4, Double cycleLoanDefaultLoanAmt4, Double cycleLoanMinLoanAmt5,
            Double cycleLoanMaxLoanAmt5, Double cycleLoanDefaultLoanAmt5, Double cycleLoanMinLoanAmt6,
            Double cycleLoanMaxLoanAmt6, Double cycleLoanDefaultLoanAmt6, LoanPrdActionForm loanPrdActionForm) {
        if (loanAmtCalcType.equals("3")) {
            loanPrdActionForm.setLoanAmtCalcType(loanAmtCalcType);
            loanPrdActionForm.setCycleLoanMaxLoanAmt1(cycleLoanMaxLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt2(cycleLoanMaxLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt3(cycleLoanMaxLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt4(cycleLoanMaxLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt5(cycleLoanMaxLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanMaxLoanAmt6(cycleLoanMaxLoanAmt6.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt1(cycleLoanMinLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt2(cycleLoanMinLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt3(cycleLoanMinLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt4(cycleLoanMinLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt5(cycleLoanMinLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanMinLoanAmt6(cycleLoanMinLoanAmt6.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt1(cycleLoanDefaultLoanAmt1.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt2(cycleLoanDefaultLoanAmt2.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt3(cycleLoanDefaultLoanAmt3.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt4(cycleLoanDefaultLoanAmt4.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt5(cycleLoanDefaultLoanAmt5.toString());
            loanPrdActionForm.setCycleLoanDefaultLoanAmt6(cycleLoanDefaultLoanAmt6.toString());
        }
        return loanPrdActionForm;
    }

    public static LoanPrdActionForm populateNoOfInstallFromLoanCycle(String calcInstallmentType, String minCycleInstallment1,
            String maxCycleInstallment1, String defCycleInstallment1, String minCycleInstallment2,
            String maxCycleInstallment2, String defCycleInstallment2, String minCycleInstallment3,
            String maxCycleInstallment3, String defCycleInstallment3, String minCycleInstallment4,
            String maxCycleInstallment4, String defCycleInstallment4, String minCycleInstallment5,
            String maxCycleInstallment5, String defCycleInstallment5, String minCycleInstallment6,
            String maxCycleInstallment6, String defCycleInstallment6, LoanPrdActionForm loanPrdActionForm) {

        if (calcInstallmentType.equals("3")) {
            loanPrdActionForm.setCalcInstallmentType(calcInstallmentType);
            loanPrdActionForm.setMaxCycleInstallment1(maxCycleInstallment1);
            loanPrdActionForm.setMaxCycleInstallment2(maxCycleInstallment2);
            loanPrdActionForm.setMaxCycleInstallment3(maxCycleInstallment3);
            loanPrdActionForm.setMaxCycleInstallment4(maxCycleInstallment4);
            loanPrdActionForm.setMaxCycleInstallment5(maxCycleInstallment5);
            loanPrdActionForm.setMaxCycleInstallment6(maxCycleInstallment6);
            loanPrdActionForm.setMinCycleInstallment1(minCycleInstallment1);
            loanPrdActionForm.setMinCycleInstallment2(minCycleInstallment2);
            loanPrdActionForm.setMinCycleInstallment3(minCycleInstallment3);
            loanPrdActionForm.setMinCycleInstallment4(minCycleInstallment4);
            loanPrdActionForm.setMinCycleInstallment5(minCycleInstallment5);
            loanPrdActionForm.setMinCycleInstallment6(minCycleInstallment6);
            loanPrdActionForm.setDefCycleInstallment1(defCycleInstallment1);
            loanPrdActionForm.setDefCycleInstallment2(defCycleInstallment2);
            loanPrdActionForm.setDefCycleInstallment3(defCycleInstallment3);
            loanPrdActionForm.setDefCycleInstallment4(defCycleInstallment4);
            loanPrdActionForm.setDefCycleInstallment5(defCycleInstallment5);
            loanPrdActionForm.setDefCycleInstallment6(defCycleInstallment6);

        }
        return loanPrdActionForm;

    }
}
