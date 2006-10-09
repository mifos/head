/**

 * CreateLoanAccount.js    version: 1

 

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
	* This function is being used for submit button.If select is selected then 
	* it will throw a message to select some value otherwise it will call load method to go to the next page.
	* @param form form currently selected. 
	* @param selectbox property of the select box. 
	*/				
				
				function fun_submit(form)
					{
						if(document.getElementsByName("selectedPrdOfferingId")[0].value=="") 
							{
								//alert("Please select loan instance name");
								return false;
							}
						else
						{
						
							loanActionForm.action="loanAction.do";
							loanActionForm.method.value="load";
							
							func_disableSubmitBtn("continueBtn");
							
							return true;
						}
						
								
					}
					
				

 
