
/**

 * Fees.js    version: 1

 

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
function onPageLoad() {
    selectAdminBox();
    showFrequency();
    showMeetingFrequency();
    showTimeOfCharges();
    showRateOrAmoount();
}
function selectAdminBox() {
    if (document.getElementsByName("categoryType")[0].value == document.getElementsByName("loanCategoryId")[0].value) {
        document.getElementsByName("customerDefaultFee")[0].disabled = true;
        document.getElementsByName("customerDefaultFee")[0].checked = false;
    } else {
        document.getElementsByName("customerDefaultFee")[0].disabled = false;
    }
}
function showFrequency() {
    var freqTypes = document.getElementsByName("feeFrequencyType");
    var recurTypes = document.getElementsByName("feeRecurrenceType");
    var freqPeriodic = (freqTypes[0].value=="1") ? freqTypes[0] : freqTypes[1];
    var freqOneTime = (freqTypes[0].value=="2") ? freqTypes[0] : freqTypes[1];
    
    
    if (freqOneTime.checked == false && freqPeriodic.checked == false) {
    	freqOneTime.checked = true;
    }
    
    if (freqPeriodic.checked == true) {
        document.getElementById("scheduleDIV").style.display = "block";
        document.getElementById("timeofchargeDiv").style.display = "none";
        if (recurTypes[0].checked == false && recurTypes[1].checked == false) {
            recurTypes[0].checked = true;
        }
    } else {
        if (freqOneTime.checked == true) {
            document.getElementById("scheduleDIV").style.display = "none";
            document.getElementById("timeofchargeDiv").style.display = "block";
        }
    }
}
function showMeetingFrequency() {
    var recurTypes = document.getElementsByName("feeRecurrenceType");
    if (recurTypes[0].checked == true) {
        document.getElementById("weekDIV").style.display = "block";
        document.getElementById("monthDIV").style.display = "none";
    } else {
        if (recurTypes[1].checked == true) {
            document.getElementById("weekDIV").style.display = "none";
            document.getElementById("monthDIV").style.display = "block";
        }
    }
}
function showTimeOfCharges() {
    if (document.getElementsByName("categoryType")[0].value == document.getElementsByName("loanCategoryId")[0].value) {
        document.getElementById("loanTimeOfChargeDiv").style.display = "block";
        document.getElementById("customerTimeOfChargeDiv").style.display = "none";
        document.getElementsByName("customerCharge")[0].selectedIndex = 0;
    } else {
        document.getElementById("loanTimeOfChargeDiv").style.display = "none";
        document.getElementById("customerTimeOfChargeDiv").style.display = "block";
        document.getElementsByName("loanCharge")[0].selectedIndex = 0;
    }
}
function showRateOrAmoount() {
    if (document.getElementsByName("categoryType")[0].value == document.getElementsByName("loanCategoryId")[0].value) {
        document.getElementById("rateDiv").style.display = "block";
        document.getElementById("rateDivHeading").style.display = "block";
        if (document.getElementById("currencyDiv") != null) {
            document.getElementById("currencyDiv").style.display = "block";
            document.getElementById("currencyDivHeading").style.display = "block";
        }
    } else {
        document.getElementsByName("rate")[0].value = "";
        document.getElementsByName("feeFormula")[0].selectedIndex = 0;
        document.getElementById("rateDiv").style.display = "none";
        document.getElementById("rateDivHeading").style.display = "none";
        if (document.getElementById("currencyDiv") != null) {
            document.getElementById("currencyDiv").style.display = "none";
            document.getElementById("currencyDivHeading").style.display = "none";
        }
    }
}
function fnCancel(form) {
    form.action = "feeaction.do?method=cancelCreate";
    form.submit();
}
function fnEdit(form) {
    form.action = "feeaction.do?method=previous";
    form.submit();
}

function fnOnEditCancel(Id) {
    document.feeactionform.action = "feeaction.do?method=cancelEdit&feeIdTemp="+Id;
    document.feeactionform.submit();
}

function fnOnEditPreviousFeeInformation(form) {
    form.action = "feeaction.do?method=editPrevious";
    form.submit();
}
