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
	form.method.value="loadMainSearch";
	form.action="custSearchAction.do";
	form.submit();
}
function fun_next(form)
{
	form.method.value="next";
	form.action="loanAction.do";
}
function fun_refresh(form)
{
	if(document.getElementsByName("selectedPrdOfferingId")[0].value=="")
	{
		return false;
	}
	else
	{
		form.method.value="load";
		form.action="loanAction.do";
		form.submit();
	}	
}
function fn_setIntrestAtDisbursement(){
	var inheritedGracePeriodDurationValue = document.getElementsByName("inheritedGracePeriodDuration")[0].value;
	if(inheritedGracePeriodDurationValue==""){
		inheritedGracePeriodDurationValue=0;
	}
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
function fn_setIntrestAtDisb() {
	var index = 0;
	var hiddenVarName = "";
	
	var inheritedGracePeriodDurationValue = document.getElementsByName("inheritedGracePeriodDuration")[0].value;
	if(inheritedGracePeriodDurationValue==""){
		inheritedGracePeriodDurationValue=0;
	}
	
	if(document.getElementsByName("intrestAtDisbursement")[0].checked==true) {
		while(index < document.loanActionForm.elements.length){
			 hiddenVarName = document.loanActionForm.elements[index].name;
			 if("intrestAtDisbursement"==hiddenVarName){
			 	document.loanActionForm.elements[index].value="1";
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
			 }
			index ++;	
		}	
		document.getElementsByName("gracePeriodDuration")[0].value=inheritedGracePeriodDurationValue;
		document.getElementsByName("gracePeriodDuration")[0].disabled=false;						
		
	}
}
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
function setFeeAmnt(listBox,targetAmntField,targetNameField,targetVersionNoField){
	var amountField = document.getElementsByName(targetAmntField)[0];
	var nameField = document.getElementsByName(targetNameField)[0];
	var versionNoField = document.getElementsByName(targetVersionNoField)[0];
	
	if(listBox.selectedIndex==0)
		amountField.value = "";
	else{
			selectedIndexNo = listBox.selectedIndex;
			var amount=loanActionForm.accountFeeAmnt[selectedIndexNo-1];
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
}
function gracePeriodDurationRangeCheck(){
	if(document.getElementsByName("intrestAtDisbursement")[0].value=="0"){
		if(parseInt(document.getElementsByName("gracePeriodDuration")[0].value) < parseInt(document.getElementsByName("noOfInstallments")[0].value)){
			return true;
			}else{
			alert(" Grace period duration should be less than the total number of installments");	
			return false;
		}
	}else{
		return true;
	}
}
function fn_submit(){
	return true;
}	
function fn_setCheckToRemove(checkBoxName){
	var index = 0;
	var hiddenVarName = "";
	while(index < document.loanActionForm.elements.length){
		hiddenVarName = document.loanActionForm.elements[index].name;
		if(document.getElementsByName(checkBoxName)[0].checked==true){
		    if(checkBoxName==hiddenVarName ){
			 	document.loanActionForm.elements[index].value="1";
			 }
		 }else{
		 	  if(checkBoxName==hiddenVarName ){
			 	document.loanActionForm.elements[index].value="0";
			 }
		 }
		index ++;	
	}	
}
