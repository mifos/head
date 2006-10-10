/**

 * CreateLoanAccount_logic.js    version: 1

 

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
						form.method.value="loadMainSearch";
						form.action="custSearchAction.do";
						form.submit();
					}

	/**
	* These function are being used for submit button.
	* @param form form currently selected.
	*/				
				
				function fnEdit(form) {
						form.method.value="previous";
						form.action="loanAccountAction.do";
						form.submit();
					}
					
				function fun_submitForApproval(form) {
						form.method.value="create";
						form.stateSelected.value="2";
						form.action="loanAccountAction.do";
						form.submit();
						func_disableSubmitBtn("submitForApprovalButton");
						
					}
					
				function fun_approved(form) {
						form.method.value="create";
						form.stateSelected.value="3";
						form.action="loanAccountAction.do";
						form.submit();
						func_disableSubmitBtn("approvedButton");
						
					}
					
				function fun_saveForLater(form) {
						form.method.value="create";
						form.stateSelected.value="1";
						form.action="loanAccountAction.do";
						form.submit();
						func_disableSubmitBtn("saveForLaterButton");
					}
