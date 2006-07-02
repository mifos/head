/**

 * CreateSavingsAccount.js    version: 1.0

 

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
		function disableButtons(){
			func_disableSubmitBtn("saveForLaterButton");
			func_disableSubmitBtn("approvedButton");
		}  
 		function fun_createCancel(form)
		{
			form.action="CustomerSearchAction.do?method=load";
			form.submit();
		}
		function fun_editCancel(form)
		{
			form.action="savingsAction.do?method=get";
			form.submit();
		}		
		function setAccountState(form, state){
			disableButtons();
			form.stateSelected.value=state;
			form.submit();
		}			
		
		function fnCreateEdit(form){
			form.action="savingsAction.do?method=previous";
			form.submit();
		}
		
		function fnEdit(form){
			form.action="savingsAction.do?method=editPrevious";
			form.submit();
		}
		
		function fnUpdate(form){
			form.action="savingsAction.do?method=update";
			func_disableSubmitBtn("submitButton");
			form.submit();
		}
		
		function fun_refresh(form)
		{
			form.action="savingsAction.do?method=reLoad";
			form.submit();
		}
	
