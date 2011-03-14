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
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class CustomizeMessagesController {

    @Autowired
    private CustomizedTextServiceFacade customizedTextServiceFacade;
    
    protected CustomizeMessagesController() {
        // default contructor for spring autowiring
    }

    public CustomizeMessagesController(final CustomizedTextServiceFacade customizedTextServiceFacade) {
        this.customizedTextServiceFacade = customizedTextServiceFacade;
    }
/*
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.customizeMessages", "customizeMessages.ftl").build();
    }
    */
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("customMessagesView.messageList", "viewCustomMessages.ftl").build();
    }    
    
    public Map<String,String> retrieveCustomMessages() {
    	return customizedTextServiceFacade.retrieveCustomizedText();
    }

    public Map<String,String> retrieveCustomMessagesMap() {
    	Map<String,String> rawMessageMap = customizedTextServiceFacade.retrieveCustomizedText();
    	Map<String,String> ftlMessageMap = new LinkedHashMap<String,String>();
    	
        for (Map.Entry<String, String> entry : rawMessageMap.entrySet()) { 
        	ftlMessageMap.put(entry.getKey(), entry.getKey() + " > " + entry.getValue());
        }    
        return ftlMessageMap;
    }

    public CustomMessageFormBean getCustomMessage(CustomMessageSelectFormBean customMessageSelectFormBean) {
    	CustomizedTextDto customizedTextDto = customizedTextServiceFacade.getCustomizedTextDto(customMessageSelectFormBean.getMessage());
    	CustomMessageFormBean customMessageFormBean = new CustomMessageFormBean();
    	customMessageFormBean.setOldMessage(customizedTextDto.getOriginalText());
    	customMessageFormBean.setNewMessage(customizedTextDto.getCustomText());
    	return customMessageFormBean;
    }
    
    public void addOrUpdateCustomMessage(CustomMessageFormBean customMessageFormBean) {
    	customizedTextServiceFacade.addOrUpdateCustomizedText(customMessageFormBean.getOldMessage(), 
    			customMessageFormBean.getNewMessage());
    }
    
    public void removeCustomMessage(CustomMessageSelectFormBean customMessageFormBean) {
    	if (customMessageFormBean.getMessage() == null) {
    		return;
    	}
    	customizedTextServiceFacade.removeCustomizedText(customMessageFormBean.getMessage());
    }    

}