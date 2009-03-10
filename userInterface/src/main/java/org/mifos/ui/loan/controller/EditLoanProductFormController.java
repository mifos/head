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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mifos.loan.service.LoanProductDto;
import org.mifos.loan.service.LoanProductService;
import org.mifos.ui.loan.command.LoanProductFormBean;
import org.springframework.web.servlet.ModelAndView;

public class EditLoanProductFormController extends AbstractLoanProductFormController {
    
    private static final Log LOG = LogFactory.getLog(EditLoanProductFormController.class);
    
    private LoanProductService loanProductService;
    
    public LoanProductService getLoanProductService() {
        return loanProductService;
    }

    public void setLoanProductService(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"NP_UNWRITTEN_FIELD"}, justification="set by Spring dependency injection")
    protected ModelAndView onSubmit(Object command) throws Exception {
        
        LOG.debug ("entered EditLoanProductController.onSubmit()");
        
        return createModelAndView(loanProductService.updateLoanProduct((LoanProductDto) command),
                                  "loanProductEditSuccess");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new LoanProductFormBean(loanProductService.getLoanProduct(new Integer(request.getParameter("id"))));
    }
}
