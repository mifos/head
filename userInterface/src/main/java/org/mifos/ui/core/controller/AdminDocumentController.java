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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.dto.domain.AdminDocumentDto;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("formBean")
public class AdminDocumentController {

    private final static String LEGACY_BIRT_ADMIN_DOCUMENT_LOAD_PATH = "reportsUserParamsAction.do?method=loadAdminReport&admindocId=%s&globalAccountNum=%s";
    private final static Integer PENTAHO_OUTPUT_TYPE_HTML_ID = 3;
    
    @Autowired
    private PentahoReportsServiceFacade pentahoReportsService;
    
    @RequestMapping(value = "/executeAdminDocument.ftl", method = RequestMethod.GET)
    public ModelAndView executeAdminDocument(final HttpServletRequest request, HttpServletResponse response,
            @RequestParam Integer adminDocumentId, @RequestParam String entityId, @RequestParam Integer outputTypeId) throws IOException{
        ModelAndView mav = null;
        String fileName = pentahoReportsService.getAdminReportFileName(adminDocumentId);
        
        if (fileName.endsWith(".rptdesign")) {
            response.sendRedirect(String.format(LEGACY_BIRT_ADMIN_DOCUMENT_LOAD_PATH, adminDocumentId.toString(), entityId));
        } else {
            Map<String, AbstractPentahoParameter> params = new HashMap<String, AbstractPentahoParameter>();
            PentahoInputParameter entityIdParameter = new PentahoInputParameter();
            entityIdParameter.setParamName("entity_id");
            entityIdParameter.setValue(entityId);
            params.put("entity_id", entityIdParameter);
            
            PentahoReport report = pentahoReportsService.getAdminReport(adminDocumentId, outputTypeId, params);
            
            if (!outputTypeId.equals(PENTAHO_OUTPUT_TYPE_HTML_ID)) {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getFilename() + "\"");
            }
            response.setContentType(report.getContentType());
            response.setContentLength(report.getContentSize());

            response.getOutputStream().write(report.getContent());
        }
        
        return mav;
    }
    
    @RequestMapping(value="/viewAdminDocs", method = RequestMethod.GET)
    @ModelAttribute("listofadministrativedocuments")
    public List<AdminDocumentDto> showAllDocuments() {
        return createStub();
    }

    @RequestMapping(value = "/editAdminDocs", method = RequestMethod.GET)
    public ModelAndView showDocument(HttpServletRequest request) {
        Integer id = Integer.parseInt( request.getParameter("id"));
        AdminDocumentDto document = findByDocId( id);

        /*
         * Since the DocumentDto is an immutable object. We would like to have
         *  a bean bound to the form that updates the document info. Basically don't want to
         *  change the DocumentDto object.
         *
         *  TODO: Need to understand where AdminDocumentFormBean gets all it's data
         *        Some of it comes from AdminDocumentDto, but not all variables.
         *
         *  Work In Progress.
         */
        ModelAndView mav = new ModelAndView("editAdminDocs");

        // Form Backed Object
        AdminDocumentFormBean formBean = new AdminDocumentFormBean();
        formBean.setAccountType("loan");
        formBean.setName(document.getName());
        formBean.setId(document.getId());

        Map<String, String> accountType = accountTypeMap();

        Map<String, String> showStatus = accountStatusMap(formBean.getAccountType());

        mav.addObject("status", showStatus);
        mav.addObject("accountType", accountType);
        mav.addObject("formBean", formBean);

        return mav;
    }

    @SuppressWarnings("PMD")
    private Map<String, String> accountStatusMap(String accountType) {

        // need to define resource file once in the beginning!
        ResourceBundle resource = ResourceBundle.getBundle("org.mifos.ui.localizedProperties.messages");

        // used to populate UI listbox
        Map<String, String> showStatus = new LinkedHashMap<String, String>();
        showStatus.put("pendingApproval", resource.getString("manageReports.options.pendingApproval"));
        showStatus.put("appApproved", resource.getString("manageReports.options.appApproved"));

        if (accountType.equals("loan")) {
            showStatus.put("activeGoodStanding", resource.getString("manageReports.options.activeGoodStanding"));
            showStatus.put("activeBadStanding", resource.getString("manageReports.options.activeBadStanding"));
            showStatus.put("closedObligationMet", resource.getString("manageReports.options.closedObligationMet"));
            showStatus.put("closedWrittenOff", resource.getString("manageReports.options.closedWrittenOff"));
            showStatus.put("closedRescheduled", resource.getString("manageReports.options.closedRescheduled"));
            showStatus.put("custmrAccountActive", resource.getString("manageReports.options.custmrAccountActive"));
            showStatus.put("custmrAccountInactive", resource.getString("manageReports.options.custmrAccountInactive"));
            showStatus.put("cancel", resource.getString("manageReports.cancel"));
        } else {
            // accountType can only be "loan" or "savings"
            showStatus.put("active", resource.getString("manageReports.options.active"));
            showStatus.put("closed", resource.getString("manageReports.options.closed"));
            showStatus.put("inactive", resource.getString("manageReports.options.inactive"));
            showStatus.put("cancel", resource.getString("manageReports.cancel"));
        }

        return showStatus;
    }

    @SuppressWarnings("PMD")
    private Map<String, String> accountTypeMap() {
        // Linked list for use with formSingleSelect
        // want to pull this from messages.properties
        // need to define resource file once in the beginning!
        ResourceBundle resource = ResourceBundle.getBundle("org.mifos.ui.localizedProperties.messages");

        Map<String, String> accountType = new LinkedHashMap<String, String>();
        accountType.put("select", resource.getString("manageReports.select"));
        accountType.put("loan", resource.getString("manageReports.loanaccount"));
        accountType.put("savings", resource.getString("manageReports.savingsaccount"));
        return accountType;
    }

    @RequestMapping(value="/updateAdminDoc", method=RequestMethod.POST)
    public ModelAndView postDocument(@ModelAttribute AdminDocumentFormBean formBean, BindingResult result) {

//        if( result.hasErrors()) {
//            System.out.println("BindingResult has errors!");
//        } else {
//            System.out.println("No BindingResult errors!");
//        }

        ModelAndView mav = new ModelAndView("updateAdminDoc");
        mav.addObject("formBean", formBean);
        return mav;
    }

    private AdminDocumentDto findByDocId(Integer id) {
        List<AdminDocumentDto> docs = createStub();
        return docs.get(id);
    }

    @SuppressWarnings("PMD")
    private List<AdminDocumentDto> createStub(){
        List<AdminDocumentDto> docs = new ArrayList<AdminDocumentDto>();

        Integer[] docid = {0, 1, 2, 3};
        Boolean[] isactive = {true, false, true, false};
        String[] name = {"doc 1","doc 2","doc 3","doc 4"};
        String[] identifier   = {"id 1","id 2","id 3","id 4"};

        for (int i = 0; i < name.length; i++)
        {
            AdminDocumentDto adminDoc = new AdminDocumentDto(docid[i], name[i], identifier[i], isactive[i]);
            docs.add( i, adminDoc);
        }
        return docs;
    }
}