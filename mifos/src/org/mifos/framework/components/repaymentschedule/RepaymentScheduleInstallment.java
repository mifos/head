/**
 * RepaymentScheduleInstallment.java version:1.0
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
package org.mifos.framework.components.repaymentschedule;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

/**
 * 
 * This class is the object that holds the repayment schedule installments
 */
public class RepaymentScheduleInstallment {

	private Integer installment = 0;

	private java.util.Date dueDate = null;

	private Money principal = new Money();

	private Money interest = new Money();

	private Money fees = new Money();

	private Money miscFees = new Money();

	private Money miscPenalty = new Money();

	private FeeInstallment feeInstallment = null;

	// This field has been added to display the date in the user locale on the
	// ui
	// value for this field would be set from the tag.
	private Locale locale = null;

	public RepaymentScheduleInstallment(int installment, Date dueDate,
			Money principal, Money interest, Money fees, Money miscFees,
			Money miscPenalty) {
		this.installment = installment;
		this.dueDate = dueDate;
		this.principal = principal;
		this.interest = interest;
		this.fees = fees;
		this.miscFees = miscFees;
		this.miscPenalty = miscPenalty;
	}
	
	public RepaymentScheduleInstallment(){}

	public void setInstallment(Integer installment) {
		this.installment = installment;
	}

	public void setDueDate(java.util.Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setPrincipal(Money principal) {
		this.principal = principal;
	}

	public void setInterest(Money interest) {
		this.interest = interest;
	}

	public void setFees(Money fees) {
		this.fees = this.fees.add(fees);
	}

	public Integer getInstallment() {
		return installment;
	}

	public java.util.Date getDueDate() {
		return dueDate;
	}

	public Money getPrincipal() {
		return principal;
	}

	public Money getInterest() {
		return interest;
	}

	public Money getFees() {
		return fees;
	}

	public Money getTotal() {
		return principal.add(interest).add(fees).add(miscFees).add(miscPenalty);
	}

	public void setFeeInstallment(FeeInstallment feeInstallment) {
		this.feeInstallment = feeInstallment;
	}

	public FeeInstallment getFeeInstallment() {
		return feeInstallment;
	}

	/**
	 * This method has been overridden to return date is user locale.
	 * 
	 * @return
	 */
	public String getDueDateInUserLocale() {
		// return DateHelper.getUserLocaleDate(getLocale(), new
		// java.util.Date(getDueDate().getYear(),getDueDate().getMonth(),getDueDate().getDate()).toString());
		return DateHelper.getDBtoUserFormatString(getDueDate(), getLocale());

	}

	/**
	 * @return Returns the locale}.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            The locale to set.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Money getMiscFees() {
		return miscFees;
	}

	public void setMiscFees(Money miscFees) {
		this.miscFees = miscFees;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	public void setMiscPenalty(Money miscPenalty) {
		this.miscPenalty = miscPenalty;
	}

}
