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
		var curAmountValue = parseFloat(document.forms["loanAccountActionForm"].elements["clientDetails["+i+"].loanAmount"].value);
		if ((document.forms["loanAccountActionForm"].elements["clients["+i+"]"].checked==true) && (!isNaN(curAmountValue)))
		{
			curAmountValue+=parseFloat(document.forms["loanAccountActionForm"].elements["loanAmount"].value);
			document.forms["loanAccountActionForm"].elements["loanAmount"].value = round(curAmountValue);
		}
	}
}

/* float is an approximate representation of a real number.
 * Simple addition of floats like 1.44 + 1.34 will result 2.7800000000000002
 * http://en.wikipedia.org/wiki/Floating_point
 * this is workaround so that 1.44 + 1.34 with this method will give 2.78
 * see http://mifosforge.jira.com/browse/MIFOS-2792
 * 
 * Amount stored in database is up to precision 4, 100000 (5 digit) is enough
 * for rounding purpose
 */
function round(n){
	return Math.round(n*100000)/100000;
}