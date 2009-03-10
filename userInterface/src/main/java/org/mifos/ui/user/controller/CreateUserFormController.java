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

import org.mifos.core.MifosValidationException;
import org.mifos.security.domain.SecurityRoles;
import org.mifos.ui.user.command.UserFormBean;
import org.mifos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class CreateUserFormController extends SimpleFormController {
    
    private UserService userService;
    private SecurityRoles securityRoles;
    
    public UserService getUserService() {
        return userService;
    }

    public SecurityRoles getSecurityRoles() {
        return securityRoles;
    }

    @Autowired
    public void setSecurityRoles(SecurityRoles securityRoles) {
        this.securityRoles = securityRoles;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    
    @Override
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.OnlyOneReturn"}) 
        //rationale: This is the signature of the superclass's method that we're overriding
        //rationale: Forcing a single return, in code this simple, will make it more complex
        //and harder to understand
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        
        UserFormBean userForm = (UserFormBean)command;        
        try {
            userService.createUser(userForm);
        } catch (MifosValidationException e) {
            errors.addAllErrors(e.getErrors());
            return showForm(request, response, errors);                   
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", command);
        return new ModelAndView("userCreateSuccess", "model", model);       
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") 
        //rationale: This is the signature of the superclass's method that we're overriding
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new UserFormBean();
    }
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") 
        //rationale: This is the signature of the superclass's method that we're overriding
    protected ModelAndView showForm(HttpServletRequest request,
            HttpServletResponse response, BindException errors)
            throws Exception {
        return super.showForm(request, response, errors);
    }
    

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") 
        //rationale: This is the signature of the superclass's method that we're overriding
    protected Map referenceData (HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("availableRoles", securityRoles.getRoles());
        return model;
    }

}

