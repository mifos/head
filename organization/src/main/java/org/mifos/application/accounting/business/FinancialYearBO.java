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

package org.mifos.application.accounting.business;

import java.util.Date;

import org.mifos.framework.business.AbstractBusinessObject;

public class FinancialYearBO extends AbstractBusinessObject{

   private int financialYearId;
   private Date financialYearStartDate;
   private Date financialYearEndDate;
   private String status;

public Date getCreatedDate() {
	return createdDate;
}

public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
}

public Short getCreatedBy() {
	return createdBy;
}

public void setCreatedBy(Short createdBy) {
	this.createdBy = createdBy;
}

public FinancialYearBO() {

}

public int getFinancialYearId() {
	return financialYearId;
}

public void setFinancialYearId(int financialYearId) {
	this.financialYearId = financialYearId;
}

public Date getFinancialYearStartDate() {
	return financialYearStartDate;
}

public void setFinancialYearStartDate(Date financialYearStartDate) {
	this.financialYearStartDate = financialYearStartDate;
}

public Date getFinancialYearEndDate() {
	return financialYearEndDate;
}

public void setFinancialYearEndDate(Date financialYearEndDate) {
	this.financialYearEndDate = financialYearEndDate;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

}
