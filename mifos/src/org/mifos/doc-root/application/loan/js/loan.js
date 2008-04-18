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
function selectAll(x) {
	for(var i=0,l=x.form.length; i<l; i++)
	{
		if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
			x.form[i].checked=x.checked
		}
	}
}
function selectAllCheck(x){
	var checked = true;
	for(var i=0,l=x.form.length; i<l; i++){
		if(x.form[i].type == 'checkbox' && x.form[i].name != 'selectAll1'){
			if(x.form[i].checked == false){
				checked = false;
			}
		}
	}
	for(var i=0,l=x.form.length; i<l; i++){
		if(x.form[i].type == 'checkbox' && x.form[i].name == 'selectAll1'){
			x.form[i].checked = checked;
		}
	}
}
function fun_saveForLater(form) {
	form.method.value="create";
	form.stateSelected.value="1";
	form.action="multipleloansaction.do";
	form.submit();
	func_disableSubmitBtn("saveForLaterButton");
}
function fun_submitForApproval(form) {
	form.method.value="create";
	form.stateSelected.value="2";
	form.action="multipleloansaction.do";
	form.submit();
	func_disableSubmitBtn("submitForApprovalButton");
	
}
function fun_approved(form) {
	form.method.value="create";
	form.stateSelected.value="3";
	form.action="multipleloansaction.do";
	form.submit();
	func_disableSubmitBtn("approvedButton");
	
}
function intDedAtDisb() {
	if(document.getElementsByName("gracePeriodTypeId")[0].value==1) {
		document.getElementsByName("gracePeriodDuration")[0].disabled=true;
	} else  {
		if(document.getElementsByName("intDedDisbursement")[0].checked==true) {
		    document.getElementsByName("gracePeriodDuration")[0].value="0";
			document.getElementsByName("gracePeriodDuration")[0].disabled=true;
		}else{
			document.getElementsByName("gracePeriodDuration")[0].disabled=false;
		}
	}
}
function displayAmount(listBox, textBox,index ){
	var comboBox = document.getElementsByName(listBox)[0];
	var amountField = document.getElementsByName(textBox)[0];
	if(comboBox.selectedIndex==0)
		amountField.value = "";
	else{
		var indexSelectedFee = comboBox.selectedIndex-1;
		var formula = "";
		if (loanAccountActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
			var amount=loanAccountActionForm.selectedFeeAmntList[indexSelectedFee].value;
			amountField.value = amount;
			formula = loanAccountActionForm.feeFormulaList[indexSelectedFee].value
		}
		else{
			amountField.value=loanAccountActionForm.selectedFeeAmntList.value;
			formula = loanAccountActionForm.feeFormulaList.value
		}
		var span = document.getElementsByName("feeFormulaSpan"+index)[0];
		span.innerHTML =formula;
	}
}  

function displayFormula(listBox, textBox,index ){
	var comboBox = document.getElementsByName(listBox)[0];
	var amountField = document.getElementsByName(textBox)[0];
	if(comboBox.selectedIndex==0)
		amountField.value = "";
	else{
		var indexSelectedFee = comboBox.selectedIndex-1;
		var formula = "";
		if (loanAccountActionForm.selectedFeeAmntList[indexSelectedFee]!= undefined){
			formula = loanAccountActionForm.feeFormulaList[indexSelectedFee].value
		}
		else{
			formula = loanAccountActionForm.feeFormulaList.value
		}
		var span = document.getElementsByName("feeFormulaSpan"+index)[0];
		span.innerHTML =formula;
	}
}
function fun_refresh(form)
	{
			form.action="loanAccountAction.do?method=load";
			form.submit();
	}
