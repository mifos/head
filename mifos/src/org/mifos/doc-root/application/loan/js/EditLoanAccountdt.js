/**

 * EditLoanAccountdt.js    version: 1

 

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
 
	/**
	* This function is being used for cancel button.
	* @param form form currently selected 
	*/
 				function fun_cancel(form)
					{
						form.method.value="cancel";
						form.action="loanAction.do";
						form.submit();
					}
	/**
	* This function is being used for the check box.
	* @param form form currently selected. 
	* @param checkbox property of the check box.
	*/					 
				function fn_setIntrestAtDisbursement(){
					var inheritedGracePeriodDurationValue = document.getElementById("inheritedGracePeriodDuration").value;
					if(inheritedGracePeriodDurationValue==""){
						inheritedGracePeriodDurationValue=0;
					}
					if(document.getElementById("intrestAtDisbursement").checked==true) {
						document.getElementById("intrestAtDisbursement").value="1";
						document.getElementById("gracePeriodDuration").value=0;
						document.getElementById("gracePeriodDuration").disabled=true;
					}else{
						document.getElementById("intrestAtDisbursement").value="0";
						document.getElementById("gracePeriodDuration").value=document.getElementById("inheritedGracePeriodDuration").value;
						document.getElementById("gracePeriodDuration").disabled=false;						
					}
				}
				function fn_setIntrestAtDisb() {
					var index = 0;
					var hiddenVarName = "";
					var inheritedGracePeriodDurationValue = document.getElementById("inheritedGracePeriodDuration").value;
					if(inheritedGracePeriodDurationValue==""){
						inheritedGracePeriodDurationValue=0;
					}
					if(document.getElementById("intrestAtDisbursement").checked==true) {
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="1";
							 }
							index ++;	
						}	
						document.getElementById("gracePeriodDuration").value=0;
						document.getElementById("gracePeriodDuration").disabled=true;

					}else{
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="0";
							 }
							index ++;	
						}	
						document.getElementById("gracePeriodDuration").value=inheritedGracePeriodDurationValue;
						document.getElementById("gracePeriodDuration").disabled=false;						
						
					}
				}
	/** This function is being used for the check box only when grace period is none.
	* @param form form currently selected. 
	* @param checkbox property of the check box.
	*/	
		
			function fun_setDisbursementdateDisable() {
				//alert("in the fun_setDisbursementdateDisable method");
				if(document.getElementById("intrestAtDisbursement").checked==true) {
						document.getElementById("intrestAtDisbursement").value="1";
					}else{
						document.getElementById("intrestAtDisbursement").value="0";
						//alert("in the fun_setDisbursementdateDisable method value of intrestAtDisbursement is "+document.getElementById("intrestAtDisbursement").value);
					}
			}	
			
			function fun_setDisbursementdateDis() {
				var index = 0;
				var hiddenVarName = "";
				if(document.getElementById("intrestAtDisbursement").checked==true) {
						
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="1";
							 }
							index ++;	
						}
					}else{
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="0";
							 }
							index ++;	
						}
						
					}
			}
	/**
	* This function is being used at the time of page submit.
	*/
				function fun_onPageSubmit() {
					if(document.getElementById("intrestAtDisbursement").checked==true) {
						var gracePeriodTypeID=1;
					}
					else {
						var gracePeriodTypeID=document.getElementById("gracePeriodTypeId").value;
					}
						if(gracePeriodTypeID==1) {
								fun_setDisbursementdateDis();
								//alert("*********"+gracePeriodTypeID);
							}
							
						else {
							fn_setIntrestAtDisb();
							//alert("##########"+gracePeriodTypeID);
						}
				}		
	/**
		This function checks if the grace period duration is less than no of installments.
		This check is done only if interest deducted at disbursement checkbox is unchecked
		which can be figured out based on the value of the associated hidden field.
	*/
 				function gracePeriodDurationRangeCheck(){
 					if(document.getElementById("intrestAtDisbursement").value=="0"){
 						var graceperdur=document.getElementById("gracePeriodDuration").value;
 						var noofinst=document.getElementById("noOfInstallments").value;
 						if(parseInt(graceperdur)<parseInt(noofinst)){
 							func_disableSubmitBtn("editDetailsBtn");
 							return true;
 						}else{
 							//alert(" Grace period duration should be less than the total number of installments");
	 						return false;
 						}
 					}else{
 						func_disableSubmitBtn("editDetailsBtn");
 						return true;
 					}
 									
 				}
 				
 	/**
 	This function disables the fields which are not editable based on the accountStateId.
 	It is called onLoad of the jsp.
 	*/
 	
 	function disableFields(){
 		//alert(document.getElementById("accountStateId").value);
 	// disable the fields if the account is in any of the following states.
	 	accountState = document.getElementById("accountStateId").value;
 		if(accountState == "3" ||
 			accountState == "4" ||
	 			accountState == "5" ||
 					accountState == "6" ||
 					 accountState == "7" || 
 			 			accountState == "8" || 
 			 				accountState == "9" || 
 			 					accountState == "10"){
 			 			
 			 		document.getElementById("loanAmount").disabled=true	;
 			 		document.getElementById("interestRateAmount").disabled=true	;
 			 		document.getElementById("noOfInstallments").disabled=true	;
 			 		document.getElementById("disbursementDateDD").disabled=true	;
 			 		document.getElementById("disbursementDateMM").disabled=true	;
 			 		document.getElementById("disbursementDateYY").disabled=true	; 			 		
 			 		document.getElementById("intrestAtDisbursement").disabled=true	;
 			 		document.getElementById("gracePeriodDuration").disabled=true;	
 		}
 		/*if(accountState == "5")  {
 			document.getElementById("businessActivityId").disabled=true	;
 		}*/
 		
 	}			
