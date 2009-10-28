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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
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

    private String importTransactionsFileName;
    
    private List<String> importTransactionsErrors;
    
    private String importTransactionsStatus;

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


    public String getImportTransactionsFileName() {
        return this.importTransactionsFileName;
    }


    public void setImportTransactionsFileName(String importTransactionsFileName) {
        this.importTransactionsFileName = importTransactionsFileName;
    }


    public List<String> getImportTransactionsErrors() {
        return this.importTransactionsErrors;
    }


    public void setImportTransactionsErrors(List<String> importTransactionsErrors) {
        this.importTransactionsErrors = importTransactionsErrors;
    }


    public String getImportTransactionsStatus() {
        return this.importTransactionsStatus;
    }


    public void setImportTransactionsStatus(String importTransactionsStatus) {
        this.importTransactionsStatus = importTransactionsStatus;
    }


    /**
     * Check to make sure the client hasn't exceeded the maximum allowed upload
     * size inside of this validate method.
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        
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
        
        /*FIXME Enable after testing
         * ImportedFilesServiceFacade importedFilesServiceFacade = new WebTierImportedFilesServiceFacede();
        try {
            if (importedFilesServiceFacade.isImportTransactionFileNamePermitted(importTransactionsFileName)) {
                errors.add("importTransactionsFile", new ActionMessage("errors.importexport.already_submitted"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
        return errors.isEmpty() ? null : errors;

    }
}
