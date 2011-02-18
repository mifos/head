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

function fun_cancel(form)
		{
			form.action="loanAccountAction.do?method=cancel";
			form.submit();
		}
		
function setGracePeriodDurationValue() {
	document.getElementsByName("gracePeriodDuration")[0].value=document.getElementsByName("gracePeriod")[0].value;
}

function disableFields(){
	 	accountState = document.getElementsByName("accountStateId")[0].value;
 		if(accountState == "3" ||
 			accountState == "4" ||
	 			accountState == "5" ||
 					accountState == "6" ||
 					 accountState == "7" || 
 			 			accountState == "8" || 
 			 				accountState == "9" || 
 			 					accountState == "10"){
 			 			
 			 		document.getElementsByName("loanAmount")[0].disabled=true	;
 			 		document.getElementsByName("interestRate")[0].disabled=true	;
 			 		document.getElementsByName("noOfInstallments")[0].disabled=true	;
 			 		document.getElementsByName("disbursementDateDD")[0].disabled=true	;
 			 		document.getElementsByName("disbursementDateMM")[0].disabled=true	;
 			 		document.getElementsByName("disbursementDateYY")[0].disabled=true	;
 			 		document.getElementsByName("gracePeriod")[0].disabled=true;
 		} 		
 }

function checkGracePeriod() {
    if(document.getElementsByName("gracePeriodTypeId")[0].value!="1") {
        document.getElementsByName("gracePeriod")[0].disabled=false;
        document.getElementsByName("gracePeriod")[0].value=document.getElementsByName("gracePeriodDuration")[0].value;
    }else {
        document.getElementsByName("gracePeriodDuration")[0].value=0;
        document.getElementsByName("gracePeriod")[0].value=0;
        document.getElementsByName("gracePeriod")[0].disabled=true;
    }
}

function setGracePeriod() {
	var inheritedGracePeriodDurationValue = document.getElementsByName("inheritedGracePeriodDuration")[0].value;
	if(inheritedGracePeriodDurationValue==""){
		inheritedGracePeriodDurationValue=0;
	}
	if(document.getElementsByName("intDedDisb")[0].checked==true) {
		document.getElementsByName("intDedDisbursement")[0].value=1;
		if(document.getElementsByName("gracePeriodTypeId")[0].value!="1") {
			document.getElementsByName("gracePeriodDuration")[0].value=0;
			document.getElementsByName("gracePeriod")[0].value=0;
			document.getElementsByName("gracePeriod")[0].disabled=true;
		}
	}else {
		document.getElementsByName("intDedDisbursement")[0].value=0;
		if(document.getElementsByName("gracePeriodTypeId")[0].value!="1") {
			document.getElementsByName("gracePeriodDuration")[0].value=inheritedGracePeriodDurationValue;
			document.getElementsByName("gracePeriod")[0].value=inheritedGracePeriodDurationValue;
			document.getElementsByName("gracePeriod")[0].disabled=false;
		}
	}
}

function CalculateTotalLoanAmount(length){
    var curForm = document.forms["loanAccountActionForm"];
	var totalLoanAmount = document.getElementById("sumLoanAmount");
	totalLoanAmount.value = "0.0";
	for(var i=0;i<length;i++)	{		
		var curAmountValue = parseFloat(curForm.elements["clientDetails["+i+"].loanAmount"].value);
		if ((curForm.elements["clients["+i+"]"].checked==true) && (!isNaN(curAmountValue)))
		{
			curAmountValue += parseFloat(totalLoanAmount.value);
		    totalLoanAmount.value = round(curAmountValue);
		}
	}
}

/* float is an approximate representation of a real number.
 * Simple addition of floats like 1.44 + 1.34 will result 2.7800000000000002
 * http://en.wikipedia.org/wiki/Floating_point
 * this is workaround so that 1.44 + 1.34 with this method will give 2.78
 * see http://mifosforge.jira.com/browse/MIFOS-2792
 * 
 * Amount stored in database is up to precision 4, 100000 (5 digit) is enough
 * for rounding purpose
 */
function round(n){
	return Math.round(n*100000)/100000;
}
