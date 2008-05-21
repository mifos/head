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
	form.action="custSearchAction.do?method=loadMainSearch";
	form.submit();
}
function fun_submit(form)
{
	if(document.getElementsByName("selectedPrdOfferingId")[0].value=="") 
	{
		return false;
	}
	else
	{
		loanActionForm.action="loanAction.do?method=load";
		func_disableSubmitBtn("continueBtn");
		return true;
	}
}
function CalculateTotalLoanAmount(length)
{
	document.forms["loanAccountActionForm"].elements["loanAmount"].value="0.0";	
	for(var i=0,l=length; i<l; i++)
	{		
		var curAmountValue = parseInt(document.forms["loanAccountActionForm"].elements["clientDetails["+i+"].loanAmount"].value);
		if ((document.forms["loanAccountActionForm"].elements["clients["+i+"]"].checked==true) && (!isNaN(curAmountValue)))
		{
			document.forms["loanAccountActionForm"].elements["loanAmount"].value=curAmountValue+parseInt(document.forms["loanAccountActionForm"].elements["loanAmount"].value);
		}
	}
}