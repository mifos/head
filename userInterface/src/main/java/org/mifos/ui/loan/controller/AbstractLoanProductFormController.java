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

package org.mifos.ui.loan.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mifos.loan.service.LoanProductDto;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * Encapsulates behavior common to both CreateLoanProductFormController and EditLoanProductFormController.
 */
public class AbstractLoanProductFormController extends SimpleFormController {


    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    protected Map referenceData (HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("ACTIVE", "Active");
        aMap.put("INACTIVE", "Inactive");
        model.put("availableCategories", aMap);
        return model;
    }

    protected ModelAndView createModelAndView (LoanProductDto product, String view) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("loanProduct", product);
        return new ModelAndView(view, "model", model);

    }
}
