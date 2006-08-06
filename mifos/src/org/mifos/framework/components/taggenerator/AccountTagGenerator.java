/**
 
 * AccountTagGenerator.java    version: 1.0
 
 
 
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
package org.mifos.framework.components.taggenerator;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.business.BusinessObject;

public class AccountTagGenerator extends TagGenerator {
	
	public AccountTagGenerator(){
		setAssociatedGenerator(new CustomerTagGenerator());
	}	
	
	protected StringBuilder build(BusinessObject obj, boolean selfLinkRequired){
		AccountBO account = (AccountBO)obj;
		StringBuilder strBuilder = getAssociatedGenerator().build(account.getCustomer());
		strBuilder.append(" / ");
		if(selfLinkRequired){
			createAccountLink(strBuilder, account); //create self link
		}else{
			//TODO internationalize this
			strBuilder.append("<b>"+getAccountName(account)+"</b>"); //get Self Node Value
		}
		return strBuilder;
	}
	
	private void createAccountLink(StringBuilder strBuilder, AccountBO account){
		strBuilder.append("<a href=\"");
		strBuilder.append(getAction(account));
		//strBuilder.append(account.getAccountId());
		strBuilder.append("\">");
		//TODO internationalize this
		strBuilder.append(getAccountName(account));
		strBuilder.append("</a>");
	}
	
	private String getAccountName(AccountBO account){
		if(account.getAccountType().getAccountTypeId().equals(AccountTypes.SAVINGSACCOUNT.getValue())){
			return ((SavingsBO)account).getSavingsOffering().getPrdOfferingName();
		}else if(account.getAccountType().getAccountTypeId().equals(AccountTypes.LOANACCOUNT.getValue())){
			return ((LoanBO)account).getLoanOffering().getPrdOfferingName();
		}
		return null;
	}
	
	private String getAction(AccountBO account){
		if(account.getAccountType().getAccountTypeId().equals(AccountTypes.SAVINGSACCOUNT.getValue())){
			return "savingsAction.do?method=get&globalAccountNum="+account.getGlobalAccountNum();
		}else if(account.getAccountType().getAccountTypeId().equals(AccountTypes.LOANACCOUNT.getValue())){
			return "loanAccountAction.do?method=get&accountId="+account.getAccountId();
		}
		return "";
	}
}
