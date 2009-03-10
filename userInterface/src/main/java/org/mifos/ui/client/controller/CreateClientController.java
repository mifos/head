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

package org.mifos.ui.client.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.mifos.client.service.ClientDto;
import org.mifos.client.service.ClientService;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.support.RequestContextUtils;

public class CreateClientController extends SimpleFormController {
    
    private ClientService clientService;
    private LocalDateEditor localDateEditor;
    
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

   @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)  {
       Map<String, Object> model = errors.getModel();
       model.put("datePattern", localDateEditor.getDatePattern());
       model.put("client", new ClientDto());
       return new ModelAndView("createClient", model);
    }
	
   @Override
   @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
   protected Map referenceData(HttpServletRequest request) throws Exception {      
    Map<String, Object> referenceData = new HashMap<String, Object>();
       referenceData.put("client", new ClientDto());
       return referenceData;
   }
   
	@Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
	protected ModelAndView onSubmit(Object command) throws Exception {
		ClientDto clientDto = clientService.createClient((ClientDto) command);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("client", clientDto);
        model.put("datePattern", localDateEditor.getDatePattern());
		return new ModelAndView("createClientSuccess", "model", model);
	}

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
    	return new ClientDto();
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
    throws ServletException {
        Locale locale = RequestContextUtils.getLocale(request);
        localDateEditor = new LocalDateEditor(locale);
        binder.registerCustomEditor(LocalDate.class, localDateEditor);
    }

}
