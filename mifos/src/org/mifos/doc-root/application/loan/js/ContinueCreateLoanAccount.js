/**

 * ContinueCreateLoanAccount.js    version: 1

 

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
						form.method.value="load";
						form.action="CustomerSearchAction.do";
						form.submit();
					}

	/**
	* This function is being used to go to the next page. 
	* @param form form currently selected. 
	*/					
				function fun_next(form)
					{
						form.method.value="next";
						form.action="loanAction.do";
						
					}

	/**
	* This function will be called when we will select some different value of the select box on the create page and
	* it will reload the page.
	* @param form form currently selected. 
	* @param index index value of the select box. 
	*/					
				function fun_refresh(form)
					{
						if(document.getElementsByName("selectedPrdOfferingId")[0].value=="")
						{
								//alert("Please select loan instance name");
								return false;
						}
						else
						{
							form.method.value="load";
							form.action="loanAction.do";
							form.submit();
						}	
					}
					
	/**
	* This function is being used for the check box.
	* @param form form currently selected. 
	* @param checkbox property of the check box.
	*/					
		/**		function fn_setIntrestAtDisbursement(checkBox,form){
					if(checkBox.checked==true){
						form.intrestAtDisbursement.value="1";
											
					}else{
						form.intrestAtDisbursement.value="0";			
					}
					return true;
				}
			*/	
			function fn_setIntrestAtDisbursement(){
				var inheritedGracePeriodDurationValue = document.getElementsByName("inheritedGracePeriodDuration")[0].value;
				//alert("inheritedGracePeriodDurationValue value is " + inheritedGracePeriodDurationValue);
				if(inheritedGracePeriodDurationValue==""){
					inheritedGracePeriodDurationValue=0;
				}
				//alert("inheritedGracePeriodDurationValue value is " + inheritedGracePeriodDurationValue);
					if(document.getElementsByName("loanOffering.intDedDisbursementFlag")[0].checked==true) {
						document.getElementsByName("intrestAtDisbursement")[0].value="1";
						document.getElementsByName("gracePeriodDuration")[0].value=0;
						document.getElementsByName("gracePeriodDuration")[0].disabled=true;
						//alert("checked inside load method the value for hidden field is " + document.getElementById("intrestAtDisbursement").value);
					}else{
						document.getElementsByName("intrestAtDisbursement")[0].value="0";
						document.getElementsByName("gracePeriodDuration")[0].value=inheritedGracePeriodDurationValue;
						document.getElementsByName("gracePeriodDuration")[0].disabled=false;						
						//alert("unchecked inside load method the value for hidden field is " + document.getElementById("intrestAtDisbursement").value);
					}
				}
				/**
				This function is used to set values in the hidden field for the check box intrestAtDisbursement.
				This function cannot directly set values in the hidden field because there are two elements in the jsp with 
				the same name as 'intrestAtDisbursement' one is the check box and another one is the hidden field.
				Hence it iterates over all the fields in the form and for both the fields either sets the values to
				 '0' or '1' based on the check box selection.
				*/
				function fn_setIntrestAtDisb() {
				var index = 0;
				var hiddenVarName = "";
				
				var inheritedGracePeriodDurationValue = document.getElementsByName("inheritedGracePeriodDuration")[0].value;
				//alert("inheritedGracePeriodDurationValue value is " + inheritedGracePeriodDurationValue);
				if(inheritedGracePeriodDurationValue==""){
					inheritedGracePeriodDurationValue=0;
				}
				//alert("inheritedGracePeriodDurationValue value is " + inheritedGracePeriodDurationValue);
				
					if(document.getElementsByName("intrestAtDisbursement")[0].checked==true) {
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="1";
	 						//	alert(" inside prev method the value for hidden field is " + document.loanActionForm.elements[index].value);
							 }
							index ++;	
						}	
						document.getElementsByName("gracePeriodDuration")[0].value=0;
						document.getElementsByName("gracePeriodDuration")[0].disabled=true;

					}else{
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="0";
	 					//		alert(" inside prev method the value for hidden field is " + document.loanActionForm.elements[index].value);
							 }
							index ++;	
						}	
						document.getElementsByName("gracePeriodDuration")[0].value=inheritedGracePeriodDurationValue;
						document.getElementsByName("gracePeriodDuration")[0].disabled=false;						
						
					}
				}
		/**
	* This function is being used for the check box only when grace period is none.
	* @param form form currently selected. 
	* @param checkbox property of the check box.
	*/	
			function fun_setDisbursementdateDisable() {
			
				if(document.getElementsByName("loanOffering.intDedDisbursementFlag")[0].checked==true) {
						
						document.getElementsByName("intrestAtDisbursement")[0].value="1";
					}else{
						
						document.getElementsByName("intrestAtDisbursement")[0].value="0";
						
					}
			}	
			function fun_setDisbursedateDis() {
				var index = 0;
				var hiddenVarName = "";
				if(document.getElementsByName("intrestAtDisbursement")[0].checked==true) {
						
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="1";
	 						//	alert(" inside prev method the value for hidden field is " + document.loanActionForm.elements[index].value);
							 }
							index ++;	
						}
					}else{
						while(index < document.loanActionForm.elements.length){
							 hiddenVarName = document.loanActionForm.elements[index].name;
							 if("intrestAtDisbursement"==hiddenVarName){
							 	document.loanActionForm.elements[index].value="0";
	 					//		alert(" inside prev method the value for hidden field is " + document.loanActionForm.elements[index].value);
							 }
							index ++;	
						}
						
					}
			}
			
	/**
	* This function is being used at the time of page submit.
	*/
				function fun_onPageSubmit() {
					var gracePeriodTypeID=document.getElementsByName("gracePeriodTypeId")[0].value;
					if(gracePeriodTypeID==1) {
						if(document.getElementsByName("loanOffering.intDedDisbursementFlag")[0]==undefined) {
							fun_setDisbursedateDis();
						}else {
							fun_setDisbursementdateDisable();
						}
											
					}
					else if(document.getElementsByName("loanOffering.intDedDisbursementFlag")[0]==undefined) {
							fn_setIntrestAtDisb();
					}
					else {
							fn_setIntrestAtDisbursement();
					}
				}
	/**
	* This function sets the name , amount and versionNo in the hidden fields based on the 
	* fees selected by the user in the drop down.	
	* @param targetVersionNoField  - the versionNo hidden field which is used to populate versionNo for the map property of the action form.
	* @param listBox - - the list box which is being edited.
	* @param targetAmntField - the versionNo hidden field which is used to populate amount for the map property of the action form.
	* @param targetNameField  - the versionNo hidden field which is used to populate feeName for the map property of the action form.
	*/				
				function setFeeAmnt(listBox,targetAmntField,targetNameField,targetVersionNoField){
					var amountField = document.getElementsByName(targetAmntField)[0];
					var nameField = document.getElementsByName(targetNameField)[0];
					var versionNoField = document.getElementsByName(targetVersionNoField)[0];
					
					if(listBox.selectedIndex==0)
						amountField.value = "";
					else{
							selectedIndexNo = listBox.selectedIndex;
							// this would indicate that there is only one drop down and hence instead of an array there would be a single field.
							//alert(" account fee amnt value is " + loanActionForm.accountFeeAmnt[selectedIndexNo-1]);
							//alert(eval(document.getElementById(loanActionForm.accountFeeAmnt[1]) != undefined ))
							var amount=loanActionForm.accountFeeAmnt[selectedIndexNo-1];
							//alert(eval(amount) != undefined );
							if(amount != undefined ){
															
								amountField.value=loanActionForm.accountFeeAmnt[selectedIndexNo-1].value;
								nameField.value = loanActionForm.feeName[selectedIndexNo-1].value;
								versionNoField.value = loanActionForm.feeVersionNo[selectedIndexNo-1].value;
							}else{
								
								amountField.value=loanActionForm.accountFeeAmnt.value;
								nameField.value = loanActionForm.feeName.value;
								versionNoField.value = loanActionForm.feeVersionNo.value;
							}
						}
					//	alert(" the amount set is " + amountField.value);
					//	alert(" the name set is " + nameField.value);
					//	alert(" the versionNo set is " + versionNoField.value);
				}
	/**
		This function checks if the grace period duration is less than no of installments.
		This check is done only if interest deducted at disbursement checkbox is unchecked
		which can be figured out based on the value of the associated hidden field.
	*/
 				function gracePeriodDurationRangeCheck(){
 					if(document.getElementsByName("intrestAtDisbursement")[0].value=="0"){
 					//alert("grace period duration value is " + document.getElementById("gracePeriodDuration").value);
 						if(parseInt(document.getElementsByName("gracePeriodDuration")[0].value) < parseInt(document.getElementsByName("noOfInstallments")[0].value)){
	 					//alert(" returning true");
 						return true;
 						}else{
 						alert(" Grace period duration should be less than the total number of installments");	
 						//alert(" returning false");
 						return false;
 						}
 					}else{
 						return true;
 					}
 									
 				}
 				
 	/**
		This function is called when the form is submitted.It checks if the date is valid and also checks that
		grace period duration is less than no of installments.
	*/		
 			function fn_submit(){
				//return gracePeriodDurationRangeCheck();
				return true;
; 			}	
 			
 			/**
 			* This function sets the value of the hidden field checkToRemove based on the user selection or otherwise.
 			* The parameter passed is the name of the check box itself.It iterates over the elements in the form
 			*  because there are two fields with the same name , one is a check box and another is a hidden field,
 			* this is because if the check box is unchecked its value is not posted hence we need to have the hidden field.
 			*/
 			function fn_setCheckToRemove(checkBoxName){
 				//alert(" inside fn_setCheckToRemove method the checkbox is checked = " + document.getElementById(checkBoxName).checked);
 				var index = 0;
				var hiddenVarName = "";
				
				while(index < document.loanActionForm.elements.length){

					hiddenVarName = document.loanActionForm.elements[index].name;
					if(document.getElementsByName(checkBoxName)[0].checked==true){
					    if(checkBoxName==hiddenVarName ){
						 	document.loanActionForm.elements[index].value="1";
	 						//alert(" since true inside fn_setCheckToRemove method the value for hidden field is " + document.loanActionForm.elements[index].value);
						 }
					 }else{
					 	  if(checkBoxName==hiddenVarName ){
						 	document.loanActionForm.elements[index].value="0";
	 						//alert(" since false inside fn_setCheckToRemove method the value for hidden field is " + document.loanActionForm.elements[index].value);
						 }
					 }
					index ++;	
				}	
 			}
