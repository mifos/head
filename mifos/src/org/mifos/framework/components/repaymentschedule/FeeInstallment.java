
/**
 * FeeInstallment.java version:1.0
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
package org.mifos.framework.components.repaymentschedule;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.mifos.framework.util.helpers.Money;

/**
 *
 *  This class holds the fee installments
 */

public class FeeInstallment
{


	private int installmentId;
	private Money totalAccountFee = new Money() ;
	private Money accountFee;
	private List<AccountFeeInstallment> accountFeeInstallmentList = new ArrayList();
	private List<AccountFeeInstallment> summaryAccountFeeInstallmentList = new ArrayList();
	private Map<Short,AccountFeeInstallment> summaryFeeInstallmentMap = new HashMap<Short,AccountFeeInstallment>();


	public void setInstallmentId(int installmentId)
	{
		this.installmentId =installmentId;
	}

	public int getInstallmentId()
	{
		return installmentId;
	}
	
	public Map<Short,AccountFeeInstallment> getSummaryFeeInstallmentMap(){
		return summaryFeeInstallmentMap;
	}

	public void addAccountFeeInstallment(AccountFeeInstallment accountFeeInstallment)
	{
		accountFeeInstallmentList.add(accountFeeInstallment);
		addToSummaryFee(accountFeeInstallment);
		setTotalAccountFee(accountFeeInstallment.getAccountFeeAmount());
	}

	public List<AccountFeeInstallment> getAccountFeeInstallment()
	{
		return accountFeeInstallmentList;
	}

	private void setTotalAccountFee(Money accountFee)
	{
		setAccountFee(accountFee);
		totalAccountFee = totalAccountFee.add(accountFee);
	}

	public Money getTotalAccountFee()
	{
		return totalAccountFee;
	}

	public void setAccountFee(Money accountFee)
	{
		this.accountFee = accountFee;
	}

	public Money getAccountFee()
	{
		return accountFee;
	}

	private void addToSummaryFee(AccountFeeInstallment accountFeeInstallment)
	{
		Short feeIdInstallment = accountFeeInstallment.getFeeId();
		AccountFeeInstallment accountFeeInstallmentInMap = summaryFeeInstallmentMap.get(feeIdInstallment);
		if(accountFeeInstallmentInMap == null)
		{
			summaryFeeInstallmentMap.put(feeIdInstallment,accountFeeInstallment);
		}
		else
		{
			accountFeeInstallmentInMap.setAccountFeeAmount(accountFeeInstallment.getAccountFeeAmount());
		}

	}

	public List<AccountFeeInstallment> getSummaryAccountFeeInstallment()
	{
		Collection<AccountFeeInstallment> col =  summaryFeeInstallmentMap.values();
		Iterator<AccountFeeInstallment> iter = col.iterator();
		List<AccountFeeInstallment> list = new ArrayList();


		while(iter.hasNext())
		{
			AccountFeeInstallment acc = iter.next();
			list.add(acc);
		}

		return list;

	}

}
