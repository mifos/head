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
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.mifos.loan.service.LoanDto;
import org.mifos.loan.service.LoanService;
import org.mifos.ui.client.controller.LocalDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

public class DisburseLoanController extends SimpleFormController {
    
    private LoanService loanService;
    private LocalDateEditor localDateEditor;
 
    public LoanService getLoanService() {
        return loanService;
    }

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }
    
   @Override
   @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
   protected Map referenceData(HttpServletRequest request) throws Exception {      
       Integer loanId = Integer.valueOf(request.getParameter("id"));

       Map<String, Object> referenceData = new HashMap<String, Object>();
       LoanDto loanDto = getLoanService().getLoan(loanId);
       referenceData.put("loan", loanDto);
       referenceData.put("datePattern", localDateEditor.getDatePattern());
       return referenceData;
   }
   
	@Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
	protected ModelAndView onSubmit(Object command) throws Exception {
		LoanDto loanDto = (LoanDto) command;
        loanService.updateLoan(loanDto);
		return new ModelAndView(new RedirectView("loanDetail.ftl?id=" + loanDto.getId()));
	}

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        Integer loanId = Integer.valueOf(request.getParameter("id"));

        return getLoanService().getLoan(loanId);
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
    throws ServletException {
        Locale locale = RequestContextUtils.getLocale(request);
        localDateEditor = new LocalDateEditor(locale);
        binder.registerCustomEditor(LocalDate.class, localDateEditor);
    }

}
