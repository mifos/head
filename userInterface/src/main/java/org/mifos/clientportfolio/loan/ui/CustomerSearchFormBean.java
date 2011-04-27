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

package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class CustomerSearchFormBean implements Serializable {

    @Autowired
    private transient MifosBeanValidator validator;
    
	@NotEmpty
	private String searchString;
	
	private boolean redoLoanAccount = false;
	
    private int page = 1;
	private int pageSize = 10;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
    public boolean isRedoLoanAccount() {
        return redoLoanAccount;
    }

    public void setRedoLoanAccount(boolean redoLoanAccount) {
        this.redoLoanAccount = redoLoanAccount;
    }
	
	/**
     * validateXXXX is invoked on transition from state 
     */
    public void validateCustomerSearchStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages);
    }
    
    public void validateSelectCustomerStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages);
    }
}