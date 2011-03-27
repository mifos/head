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

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

/**
 * An object to hold information collected in create savings account process.
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class CustomizedTextFormBean implements Serializable {

    @Size(max = 50)
    @NotEmpty
    private String originalText;

    @Size(max = 50)
    @NotEmpty
    private String customText;

    @Autowired
    private transient MifosBeanValidator validator;

    public void clear() {
    	originalText = "";
    	customText = "";
    }
    
    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

	public MifosBeanValidator getValidator() {
		return validator;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}

    /**
     * Validation method that Spring webflow calls on state transition out of
     * addCustomizedTextStep.
     */
    public void validateAddCustomizedTextStep(ValidationContext context) {
        doValidation(context);
    }	

    /**
     * Validation method that Spring webflow calls on state transition out of
     * editCustomizedTextStep.
     */
    public void validateEditCustomizedTextStep(ValidationContext context) {
        doValidation(context);
    }	
    
    private void doValidation(ValidationContext context) {
        MessageContext messages = context.getMessageContext();

        validator.validate(this, messages);
    }	
    
}


