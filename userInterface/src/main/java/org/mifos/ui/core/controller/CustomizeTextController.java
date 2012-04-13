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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.admin.servicefacade.CustomizedTextDto;
import org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class CustomizeTextController {

    @Autowired
    private CustomizedTextServiceFacade customizedTextServiceFacade;
    
    protected CustomizeTextController() {
        // default contructor for spring autowiring
    }

    public CustomizeTextController(final CustomizedTextServiceFacade customizedTextServiceFacade) {
        this.customizedTextServiceFacade = customizedTextServiceFacade;
    }

    public List<BreadCrumbsLinks> showBreadCrumbsForView() {
        return new AdminBreadcrumbBuilder().withAdminLink("done").withLink("customizeTextView.customizedTextList", "unused")
        	.build();
    }    
    
    public List<BreadCrumbsLinks> showBreadCrumbsForAdd() {
        return new AdminBreadcrumbBuilder().withAdminLink("done")
        .withLink("customizeTextView.customizedTextList", "cancel")
        .withLink("customizeTextAdd.title", "unused")
    	.build();
   }

    public List<BreadCrumbsLinks> showBreadCrumbsForEdit() {
        return new AdminBreadcrumbBuilder().withAdminLink("done")
        .withLink("customizeTextView.customizedTextList", "cancel")
        .withLink("customizeTextEdit.title", "unused")
    	.build();    }
    
    public Map<String,String> retrieveCustomMessages() {
    	return customizedTextServiceFacade.retrieveCustomizedText();
    }

    public Map<String,String> retrieveCustomizedTextMap() {
    	Map<String,String> rawMessageMap = customizedTextServiceFacade.retrieveCustomizedText();
    	Map<String,String> ftlMessageMap = new LinkedHashMap<String,String>();
    	
        for (Map.Entry<String, String> entry : rawMessageMap.entrySet()) { 
        	ftlMessageMap.put(entry.getKey(), entry.getKey() + " > " + entry.getValue());
        }    
        return ftlMessageMap;
    }

    public CustomizedTextFormBean getCustomizedText(CustomizedTextSelectFormBean customizedTextSelectFormBean) {
    	CustomizedTextDto customizedTextDto = customizedTextServiceFacade.getCustomizedTextDto(customizedTextSelectFormBean.getMessage());
    	CustomizedTextFormBean customizedTextFormBean = new CustomizedTextFormBean();
    	customizedTextFormBean.setOriginalText(customizedTextDto.getOriginalText());
    	customizedTextFormBean.setCustomText(customizedTextDto.getCustomText());
    	return customizedTextFormBean;
    }
    
    public void addOrUpdateCustomizedText(CustomizedTextFormBean customizedTextFormBean) {
    	customizedTextServiceFacade.addOrUpdateCustomizedText(customizedTextFormBean.getOriginalText(), 
    			customizedTextFormBean.getCustomText());
    }
    
    public void removeCustomizedText(CustomizedTextSelectFormBean customMessageFormBean) {
    	if (customMessageFormBean.getMessage() == null) {
    		return;
    	}
    	customizedTextServiceFacade.removeCustomizedText(customMessageFormBean.getMessage());
    }    

}