/**

 * GenerateTextBox.js    version: 1

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
	/*
			function fun_return(form)
					{
						form.action="loanAction.do?method=get&globalAccountNum=${requestScope.loan.globalAccountNum}";
						form.submit();
					}*/

	/**
	* This function is being used for generating text box dynamically according to the selection in select box. 
	*/					
			function generateTextBox()
				{
					if(document.getElementById("paymentType.paymentTypeId").value=="2")
						{
							document.getElementById("3").style.display="block";
							document.getElementById("1").style.display="none";
							document.getElementById("2").style.display="none";
							document.getElementById("4").style.display="none";
						}
					else if(document.getElementById("paymentType.paymentTypeId").value=="4")
						{
							document.getElementById("4").style.display="block";
							document.getElementById("1").style.display="none";
							document.getElementById("2").style.display="none";
							document.getElementById("3").style.display="none";
						}
					else if(document.getElementById("paymentType.paymentTypeId").value=="3")
						{
							document.getElementById("1").style.display="block";
							document.getElementById("2").style.display="block";
							document.getElementById("3").style.display="none";
							document.getElementById("4").style.display="none";
						}
					else 
						{
							document.getElementById("1").style.display="none";
							document.getElementById("2").style.display="none";
							document.getElementById("3").style.display="none";
							document.getElementById("4").style.display="none";
						}
					document.getElementById("receiptNumber").style.display="block";
				}
