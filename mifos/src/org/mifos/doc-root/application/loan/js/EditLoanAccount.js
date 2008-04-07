/**

 * EditLoanAccount.js    version: 1

 

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
			 			 		document.getElementsByName("intDedDisb")[0].disabled=true	;
			 			 		document.getElementsByName("gracePeriod")[0].disabled=true;
			 		} 		
			 }	
			 
		 	function setIntrestAtDisb() {
		 		if(document.getElementsByName("gracePeriodTypeId")[0].value!="1") {
					if(document.getElementsByName("intDedDisbursement")[0].value==1){
						document.getElementsByName("intDedDisb")[0].checked=true;
						document.getElementsByName("gracePeriod")[0].disabled=true;
					}	
					else{
						document.getElementsByName("intDedDisb")[0].checked=false;
						document.getElementsByName("gracePeriod")[0].disabled=false;
					}	
					document.getElementsByName("gracePeriod")[0].value=document.getElementsByName("gracePeriodDuration")[0].value;
				}else {
					document.getElementsByName("gracePeriodDuration")[0].value=0;
					document.getElementsByName("gracePeriod")[0].value=0;
					document.getElementsByName("gracePeriod")[0].disabled=true;
					if(document.getElementsByName("intDedDisbursement")[0].value==1){
						document.getElementsByName("intDedDisb")[0].checked=true;
					}	
					else{
						document.getElementsByName("intDedDisb")[0].checked=false;
					}
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