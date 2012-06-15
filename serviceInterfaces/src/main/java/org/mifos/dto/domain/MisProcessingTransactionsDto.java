/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.dto.domain;

import java.math.BigDecimal;
import java.util.Date;
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class MisProcessingTransactionsDto {
	private int officeLevel;
	private String globalOfficeNum;
	private String voucherType;
	private Date postedDate;
	private Date voucherDate;
	private String glDebit;
	private String glCredit;
	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getOfficeLevel() {
		return officeLevel;
	}

	public void setOfficeLevel(int officeLevel) {
		this.officeLevel = officeLevel;
	}

	public String getGlobalOfficeNum() {
		return globalOfficeNum;
	}

	public void setGlobalOfficeNum(String globalOfficeNum) {
		this.globalOfficeNum = globalOfficeNum;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getGlDebit() {
		return glDebit;
	}

	public void setGlDebit(String glDebit) {
		this.glDebit = glDebit;
	}

	public String getGlCredit() {
		return glCredit;
	}

	public void setGlCredit(String glCredit) {
		this.glCredit = glCredit;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public Date getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(Date voucherDate) {
		this.voucherDate = voucherDate;
	}

}