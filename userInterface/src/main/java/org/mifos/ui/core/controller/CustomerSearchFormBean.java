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

package org.mifos.ui.core.controller;

import java.io.Serializable;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_NO_SERIALVERSIONID", justification = "required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
/**
 * @deprecated by CreateSavingsAccountFormBean
 */
public class CustomerSearchFormBean implements Serializable {

    @NotEmpty
    private String searchString;
    
    private String selectedDateOption;
    
    private Short officeId = 0;
    
    private Map<String, String> offices;
    
    private String officeName;
        
    private boolean clientSearch;
    
    private boolean groupSearch;
    
    private boolean centerSearch;

    @Autowired
    private transient MifosBeanValidator validator;

    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public String getSelectedDateOption() {
		return selectedDateOption;
	}

	public void setSelectedDateOption(String selectedDateOption) {
		this.selectedDateOption = selectedDateOption;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}	
	public Map<String, String> getOffices() {
        return offices;
    }

    public void setOffices(Map<String, String> offices) {
        this.offices = offices;
    }
    
    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public boolean isClientSearch() {
        return clientSearch;
    }

    public void setClientSearch(boolean clientSearch) {
        this.clientSearch = clientSearch;
    }

    public boolean isGroupSearch() {
        return groupSearch;
    }

    public void setGroupSearch(boolean groupSearch) {
        this.groupSearch = groupSearch;
    }
    

    public boolean isCenterSearch() {
        return centerSearch;
    }

    public void setCenterSearch(boolean centerSearch) {
        this.centerSearch = centerSearch;
    }

    /**
     * Validation method that Spring webflow calls on state transition out of
     * customerSearchStep.
     */
    public void validateCustomerSearchStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages);
    }

    /**
     * Validation method that Spring webflow calls on state transition out of
     * selectCustomerStep.
     */
    public void validateSelectCustomerStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        validator.validate(this, messages);
    }

}
