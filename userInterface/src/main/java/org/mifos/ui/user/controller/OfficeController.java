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

package org.mifos.ui.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.user.service.OfficeService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class OfficeController extends MultiActionController {

	private OfficeService officeService;
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
	public ModelAndView viewOffices(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {
		
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("offices", getOfficeService().getAll());
        model.put("headOffice", getOfficeService().getHeadOffice());
        
        return new ModelAndView("viewOffices", "model", model);
	}
	
    public OfficeService getOfficeService() {
        return officeService;
    }

    public void setOfficeService(OfficeService officeService) {
        this.officeService = officeService;
    }

}
