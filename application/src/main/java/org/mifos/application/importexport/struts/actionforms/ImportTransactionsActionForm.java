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

package org.mifos.application.importexport.struts.actionforms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.mifos.application.importexport.servicefacade.WebTierImportTransactionsServiceFacade;
import org.mifos.framework.struts.actionforms.BaseActionForm;

/**
 * This class is a placeholder for form values. In a multipart request, files
 * are represented by set and get methods that use the class
 * org.apache.struts.upload.FormFile, an interface with basic methods to
 * retrieve file information. The actual structure of the FormFile is dependent
 * on the underlying implementation of multipart request handling. The default
 * implementation that struts uses is
 * org.apache.struts.upload.CommonsMultipartRequestHandler.
 *
 */
public class ImportTransactionsActionForm extends BaseActionForm {

    private String importPluginName;

    private FormFile importTransactionsFile;

    public String getImportPluginName() {
        return this.importPluginName;
    }


    public void setImportPluginName(String importPluginName) {
        this.importPluginName = importPluginName;
    }


    public FormFile getImportTransactionsFile() {
        return this.importTransactionsFile;
    }


    public void setImportTransactionsFile(FormFile importTransactionsFile) {
        this.importTransactionsFile = importTransactionsFile;
    }


    public void clear() {
        this.importPluginName = null;
        this.importTransactionsFile = null;
    }

    /**
     * Check to make sure the client hasn't exceeded the maximum allowed upload
     * size inside of this validate method.
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        // Do not validate if validation is not for upload
        if (!request.getParameter("method").equals("upload")) {
            // request.getAttribute("methodCalled") is a way to track which method was called earlier current method
            // we check if methodCalled was upload then set ERROR_KEY and not directly return (ActionErrors) errors.
            // otherwise loal -> validate -> load will be held a cycle
            // In some action class this is done using a seperate validate method
            // Using validate in Action classes creates confusion.
            if (request.getAttribute("methodCalled")!=null && request.getAttribute("methodCalled").equals("upload")) {
                request.setAttribute(Globals.ERROR_KEY, request.getAttribute("uploadErrors"));
            }
            return null;
        }

        request.setAttribute("methodCalled", request.getParameter("method"));
        request.setAttribute("uploadErrors", errors);

        if((importPluginName != null) && (importPluginName.length() < 1)){
            errors.add("importPluginName", new ActionMessage("errors.importexport.mandatory_selectbox"));
        }

        // has the maximum length been exceeded?
        Boolean maxLengthExceeded = (Boolean) request.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);

        if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.importexport.toobig"));
        } else if((importTransactionsFile != null) && (importTransactionsFile.getFileName().length() < 1)){
            errors.add("importTransactionsFile", new ActionMessage("errors.importexport.mandatory_file"));
        }


        ImportTransactionsServiceFacade importedFilesServiceFacade = new WebTierImportTransactionsServiceFacade();
        try {

            if (importTransactionsFile.getFileName() != null
                    && importedFilesServiceFacade.isAlreadyImported(importTransactionsFile.getFileName())) {
                errors.add("importTransactionsFile", new ActionMessage("errors.importexport.already_submitted"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return errors;

    }
}
