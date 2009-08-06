/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.apache.commons.lang.StringUtils;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class DefineAdditionalFieldsPage extends MifosPage {

    public DefineAdditionalFieldsPage(Selenium selenium) {
        super(selenium);
    }
    
    public DefineAdditionalFieldsPage verifyPage() {
        verifyPage("define_additional_fields");
        return this;
    }

    public DefineAdditionalFieldPreviewPage defineAdditionalField(AdminPage adminPage, String category, String label, String dataType) {
        selectIfNotEmpty("categoryType", category);
        typeTextIfNotEmpty("define_additional_fields.input.labelName", label);
        if (! StringUtils.isEmpty(dataType)) {
            if ("Text".equals(dataType)) {
                selenium.select("dataType", "value=2");
            } else if ("Numeric".equals(dataType)) {
                selenium.select("dataType", "value=1");
            }
        }
        
        selenium.click("define_additional_fields.button.preview");
        waitForPageToLoad();
        return new DefineAdditionalFieldPreviewPage(selenium);       
    }


}
