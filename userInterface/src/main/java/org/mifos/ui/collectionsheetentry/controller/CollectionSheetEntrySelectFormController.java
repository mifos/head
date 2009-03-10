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

package org.mifos.ui.collectionsheetentry.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mifos.ui.collectionsheetentry.command.CollectionSheetEntrySelectFormBean;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class CollectionSheetEntrySelectFormController extends SimpleFormController {

    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return new CollectionSheetEntrySelectFormBean();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
    @Override
    protected Map referenceData (HttpServletRequest request) throws Exception {
        return getTestReferenceData();
    }

    private Map getTestReferenceData () {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("branchOffices", getTestBranchOffices());
        model.put("loanOfficers", getTestLoanOfficers());
        model.put("centers", getTestCenters());
        model.put("paymentModes", getPaymentModes());
        return model;
    }
    
    private List<String> getPaymentModes() {
        List<String> paymentModes = new ArrayList<String>();
        paymentModes.add("-- Select --");
        paymentModes.add("CASH");
        paymentModes.add("VOUCHER");
        return paymentModes;
    }
    
    private List<String> getTestBranchOffices() {
        List<String> branchOffices = new ArrayList<String>();
        branchOffices.add("-- Select --");
        branchOffices.add("Branch1");
        branchOffices.add("BranchOffice2");
        return branchOffices;
    }
    
    private List<String> getTestLoanOfficers() {
        List<String> loanOfficers = new ArrayList<String>();
        loanOfficers.add("-- Select --");
        loanOfficers.add("Loan Officer1");
        loanOfficers.add("Loan Officer2");
        return loanOfficers;
    }
    
    private Map<Integer, String> getTestCenters() {
        return new HashMap<Integer, String>();
    }
}
