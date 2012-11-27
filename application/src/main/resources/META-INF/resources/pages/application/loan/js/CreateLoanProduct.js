/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
$(document).ready(function() {
    showCashFlowInputs();
    fnIntDesbr();
    fnGracePeriod();
    checkRow();
    checkType();
    showLoanAmountType();
    showInstallType();
    showVariableInstallmentInputs();
});

function showMeetingFrequency(){
    if (document.getElementsByName("freqOfInstallments")[1].checked == true){
        document.getElementById("week").style.display = "none";
        document.getElementById("day").style.display = "none";
        document.getElementById("month").style.display = "block";
    } else if (document.getElementsByName("freqOfInstallments")[2].checked == true) {
        document.getElementById("week").style.display = "none";
        document.getElementById("day").style.display = "block";
        document.getElementById("month").style.display = "none";
    } else {
        document.getElementById("week").style.display = "block";
        document.getElementById("day").style.display = "none";
        document.getElementsByName("freqOfInstallments")[0].checked = true;
        document.getElementById("month").style.display = "none";
    }
}
function fnCancel(form) {
    form.method.value="cancelCreate";
    form.action="loanproductaction.do";
    form.submit();
}

function fnIntDesbr() {
    if(document.getElementsByName("intDedDisbursementFlag")[0].checked==true) {
        document.getElementsByName("gracePeriodType")[0].disabled=true;
        document.getElementsByName("gracePeriodType")[0].selectedIndex=0;
        document.getElementsByName("gracePeriodDuration")[0].value="";
        document.getElementsByName("gracePeriodDuration")[0].disabled=true;
    }
    else {
        document.getElementsByName("gracePeriodType")[0].disabled=false;
        document.getElementsByName("gracePeriodDuration")[0].disabled=false;
    }
}
function fnGracePeriod() {
    if(document.getElementsByName("gracePeriodType")[0].selectedIndex==0 ||
        document.getElementsByName("gracePeriodType")[0].value==1) {
        document.getElementsByName("gracePeriodDuration")[0].value="";
        document.getElementsByName("gracePeriodDuration")[0].disabled=true;
    }else {
        document.getElementsByName("gracePeriodDuration")[0].disabled=false;
    }
}

function checkRow(){
    if (document.loanproductactionform.loanAmtCalcType[0].checked == true){
        document.getElementById("option0").style.display = "block";
        document.getElementById("option1").style.display = "none";
        document.getElementById("option2").style.display = "none";
        }
    else if (document.loanproductactionform.loanAmtCalcType[1].checked == true){
        document.getElementById("option0").style.display = "none";
        document.getElementById("option1").style.display = "block";
        document.getElementById("option2").style.display = "none";
        }

    else if (document.loanproductactionform.loanAmtCalcType[2].checked == true){
        document.getElementById("option0").style.display = "none";
        document.getElementById("option1").style.display = "none";
        document.getElementById("option2").style.display = "block";
        }
}

function checkType(){
    if (document.loanproductactionform.calcInstallmentType[0].checked == true){
        document.getElementById("install0").style.display = "block";
        document.getElementById("install1").style.display = "none";
        document.getElementById("install2").style.display = "none";
        }
    else if (document.loanproductactionform.calcInstallmentType[1].checked == true){
        document.getElementById("install0").style.display = "none";
        document.getElementById("install1").style.display = "block";
        document.getElementById("install2").style.display = "none";
        }
    else if (document.loanproductactionform.calcInstallmentType[2].checked == true){
        document.getElementById("install0").style.display = "none";
        document.getElementById("install1").style.display = "none";
        document.getElementById("install2").style.display = "block";
        }
}

function changeValue(event, editbox, endvalue, rownum)
{

    if(!(endvalue=="")){
    if (FnCheckNumber(event,'','',editbox) == false)
        return false;
    for(var i=rownum;i<rownum+1;i++)
        {
            eval("document.loanproductactionform.startRangeLoanAmt"+(i+1)).value = parseFloat(endvalue) +1;
        }
        }
}

function changeInstallmentValue(event, editbox, endvalue, rownum)
{

    if(!(endvalue=="")){
    if (FnCheckNumber(event,'','',editbox) == false)
        return false;
    for(var i=rownum;i<rownum+1;i++)
        {
            eval("document.loanproductactionform.startInstallmentRange"+(i+1)).value = parseFloat(endvalue) +1;

        }
        }
}

function showLoanAmountType() {
    var loanAmtCalcType = document.getElementsByName("loanAmtCalcType");
    if (loanAmtCalcType[1].checked == false && loanAmtCalcType[2].checked == false) {
        loanAmtCalcType[0].checked = true;
    }
}
function showInstallType() {
    var calcInstallmentType = document.getElementsByName("calcInstallmentType");
    if (calcInstallmentType[1].checked == false && calcInstallmentType[2].checked == false) {
        calcInstallmentType[0].checked = true;
    }
}

function showVariableInstallmentInputs() {
    var isVariableInstallmentType = document.getElementById("createLoanProduct.checkbox.canConfigureVariableInstallments");
    if (isVariableInstallmentType.checked == true) {
        document.getElementById("minimumGapBetweenInstallmentsLabelDiv").style.display = "block";
        document.getElementById("maximumGapBetweenInstallmentsLabelDiv").style.display = "block";
        document.getElementById("minimumInstallmentAmountLabelDiv").style.display = "block";

        document.getElementById("minimumGapBetweenInstallmentsInputDiv").style.display = "block";
        document.getElementById("maximumGapBetweenInstallmentsInputDiv").style.display = "block";
        document.getElementById("minimumInstallmentAmountInputDiv").style.display = "block";

    } else {
        document.getElementById("minimumGapBetweenInstallmentsLabelDiv").style.display = "none";
        document.getElementById("maximumGapBetweenInstallmentsLabelDiv").style.display = "none";
        document.getElementById("minimumInstallmentAmountLabelDiv").style.display = "none";

        document.getElementById("minimumGapBetweenInstallmentsInputDiv").style.display = "none";
        document.getElementById("maximumGapBetweenInstallmentsInputDiv").style.display = "none";
        document.getElementById("minimumInstallmentAmountInputDiv").style.display = "none";
   }
}

function showCashFlowInputs() {
    var isVariableInstallmentType = document.getElementById("createLoanProduct.checkbox.cashFlowValidation");
    if (isVariableInstallmentType.checked == true || isVariableInstallmentType.checked == "checked") {
        document.getElementById("cashFlowThresholdDiv").style.display = "block";
        document.getElementById("cashFlowIndebtednessRatioDiv").style.display = "block";
        document.getElementById("cashFlowRepaymentCapacityDiv").style.display = "block";
        document.getElementById("cashFlowThresholdInputDiv").style.display = "block";
        document.getElementById("cashFlowIndebtednessRatioInputDiv").style.display = "block";
        document.getElementById("cashFlowRepaymentCapacityInputDiv").style.display = "block";
    } else {
        document.getElementById("cashFlowThresholdDiv").style.display = "none";
        document.getElementById("cashFlowIndebtednessRatioDiv").style.display = "none";
        document.getElementById("cashFlowRepaymentCapacityDiv").style.display = "none";
        document.getElementById("cashFlowThresholdInputDiv").style.display = "none";
        document.getElementById("cashFlowIndebtednessRatioInputDiv").style.display = "none";
        document.getElementById("cashFlowRepaymentCapacityInputDiv").style.display = "none";
   }
}
