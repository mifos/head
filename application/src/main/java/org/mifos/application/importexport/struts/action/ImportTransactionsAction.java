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

package org.mifos.application.importexport.struts.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mifos.application.importexport.struts.actionforms.ImportTransactionsActionForm;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;

/**
 * This class takes the {@link ImportTransactionsActionForm} and retrieves file
 * with details transactions received from a third party (mostly a bank) and
 * import the details to collection sheet entry module
 */

public class ImportTransactionsAction extends BaseAction {

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward("import_load");
    }

    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // this line is here for when the input page is upload-utf8.jsp,
        // it sets the correct character encoding for the response
        String encoding = request.getCharacterEncoding();
        if ((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
            response.setContentType("text/html; charset=utf-8");
        }

        ImportTransactionsActionForm importTransactionsForm = (ImportTransactionsActionForm) form;

        // retrieve the file representation
        FormFile importTransationsFile = importTransactionsForm.getImportTransactionsFile();

        // Use this to apply specific parsing implementation
        String importTransactionsType = importTransactionsForm.getImportTransactionsType();

        try {
            // retrieve the file data
            InputStream stream = importTransationsFile.getInputStream();
            importTransactionsForm.setImportTransactionsFileName(importTransationsFile.getFileName());

            // TODO Do import processing with the input stream
            // PARSE the data
            // Required for UI

            // // Generate Import Status Message
            String importTransactionsStatus = "It should tell how many row can be imported or appear to be parsed correctly";
            importTransactionsForm.setImportTransactionsStatus(importTransactionsStatus);

            // // Generate error List
            List<String> importTransactionsErrors = new ArrayList<String>();
            // FIXME dummy code
            boolean errorListIsNotEmpty = true;
            
            if (errorListIsNotEmpty) {
                String errorHeading = "The following rows contains errors and will not be imported.";
                importTransactionsErrors.add(errorHeading);
                importTransactionsErrors.add("- Row 13 is missing data.");
                importTransactionsErrors.add("- Serial value of Row 26 does not follow expected format.");
                importTransactionsForm.setImportTransactionsErrors(importTransactionsErrors);
            }
            request.setAttribute("importTransactionsErrors", importTransactionsErrors);
            // // close the stream
            stream.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // destroy the temporary file created
        importTransationsFile.destroy();

        return mapping.findForward("import_results");
    }

    public ActionForward confirm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward("import_confirm");
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        // TODO Auto-generated method stub
        return new DummyImportTransactionService();
    }

    class DummyImportTransactionService implements BusinessService {

        @Override
        public BusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }
    }
}