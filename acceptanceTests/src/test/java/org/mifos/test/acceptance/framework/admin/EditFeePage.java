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

package org.mifos.test.acceptance.framework.admin;


import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage.SubmitFormParameters;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class EditFeePage extends MifosPage {

    public EditFeePage(Selenium selenium) {
        super(selenium);
        verifyPage("feesedit");
    }
    
    private void submit() {
        selenium.click("previewBtn");
        waitForPageToLoad();
    }

    public static class SubmitFormParameters {
    	public static final int ACTIVE = 1;
        public static final int INACTIVE = 2;
        
    	private String amount;
    	private String rate;
    	private int status;
    	private boolean remove;
    	
		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}
		public String getRate() {
			return rate;
		}
		public void setRate(String rate) {
			this.rate = rate;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public boolean isRemove() {
			return remove;
		}
		public void setRemove(boolean remove) {
			this.remove = remove;
		}
    	
    }
    
    public EditFeePreviewPage fillFormAndNavigateToEditFeePreviewPage(SubmitFormParameters parameters) {
        if(selenium.isElementPresent("amount") ) {
        	typeTextIfNotEmpty("amount", parameters.getAmount());
        }
        if(selenium.isElementPresent("rate") ) {
        	typeTextIfNotEmpty("rate", parameters.getRate());
        }
        
        selenium.select("feeStatus", "value=" + parameters.getStatus());

        if(parameters.isRemove()){
        	selenium.check("toRemove");
        } else {
        	selenium.uncheck("toRemove");
        }
        submit();
        return new EditFeePreviewPage(selenium);
    }
    
    public EditFeePage verifyElementsArePresentAndEditable(String[] elements ) {
        for(String element : elements) {
            Assert.assertTrue(selenium.isElementPresent(element));
            Assert.assertTrue(selenium.isEditable(element));
        }
        return this;
    }
    
}
