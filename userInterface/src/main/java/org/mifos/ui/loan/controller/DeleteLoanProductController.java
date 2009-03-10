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
import javax.servlet.http.HttpServletResponse;

import org.mifos.core.MifosException;
import org.mifos.loan.service.LoanProductDto;
import org.mifos.loan.service.LoanProductService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class DeleteLoanProductController extends SimpleFormController {

    private LoanProductService loanProductService;
    
    @Override
        protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)  {
            Map<String, Object> model = errors.getModel();
            Integer loanProductId = Integer.valueOf(request.getParameter("id"));
            LoanProductDto loanProduct = loanProductService.getLoanProduct(loanProductId);
            model.put("loanProduct", loanProduct);
            return new ModelAndView("deleteLoanProduct", model);
        }
        
       @Override
       @SuppressWarnings("PMD.SignatureDeclareThrowsException") // signature of superclass method
       protected Map referenceData(HttpServletRequest request) throws Exception {      
            Map<String, Object> referenceData = new HashMap<String, Object>();
            referenceData.put("deleteLoanProduct", new DeleteLoanProductDto());
            return referenceData;
       }
       
       @Override
       @SuppressWarnings("PMD.SignatureDeclareThrowsException") // signature of superclass method
       protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
            DeleteLoanProductDto deleteLoanProductDto = (DeleteLoanProductDto) command;
            ModelAndView result = null;
            Map<String, Object> model = errors.getModel();
            if ("Delete".equals(deleteLoanProductDto.getAction())) { 
                    LoanProductDto loanProductDto = loanProductService.getLoanProduct(((DeleteLoanProductDto) command).getLoanProductId());
                    model.put("loanProduct", loanProductDto);
                    try {
                       loanProductService.deleteLoanProduct(loanProductDto);
                       result = new ModelAndView("deleteLoanProductSuccess", "model", model);
                    } catch (MifosException e) {
                        result = new ModelAndView("deleteLoanProductFailure", "model", model);
                    }
            } else {
                result = new ModelAndView("adminHome");
            }
            return result;
        }

        @SuppressWarnings("PMD.SignatureDeclareThrowsException") // signature of superclass method
        protected Object formBackingObject(HttpServletRequest request) throws Exception {
            return new DeleteLoanProductDto();
        }
        
    public void setLoanProductService(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }
    
}
