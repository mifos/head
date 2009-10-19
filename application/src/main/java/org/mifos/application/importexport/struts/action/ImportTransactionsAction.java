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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mifos.api.accounts.UserReferenceDto;
import org.mifos.application.importexport.struts.actionforms.ImportTransactionsActionForm;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.spi.ParseResultDto;
import org.mifos.spi.TransactionImport;

/**
 * This class takes the {@link ImportTransactionsActionForm} and retrieves file
 * with details transactions received from a third party (mostly a bank) and
 * import the details to collection sheet entry module
 */

public class ImportTransactionsAction extends BaseAction {

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("manageImportAction");
        security.allow("load", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("upload", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("confirm", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        return security;
    }

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final List<ListItem<String>> importPlugins = new ArrayList<ListItem<String>>(); 
        for (TransactionImport ti : new PluginManager().loadImportPlugins()) {
            importPlugins.add(new ListItem<String>(ti.getClass().getName(), ti.getDisplayName()));
        }
        request.setAttribute("importPlugins", importPlugins);
        return mapping.findForward("import_load");
    }

    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // this line is here for when the input page is upload-utf8.jsp,
        // it sets the correct character encoding for the response
        final String encoding = request.getCharacterEncoding();
        if ((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
            response.setContentType("text/html; charset=utf-8");
        }

        final ImportTransactionsActionForm importTransactionsForm = (ImportTransactionsActionForm) form;

        // retrieve the file representation
        final FormFile importTransationsFile = importTransactionsForm.getImportTransactionsFile();

        // Use this to apply specific parsing implementation
        final String importPluginClassname = importTransactionsForm.getImportPluginName();

        try {
            // retrieve the file data
            InputStream stream = importTransationsFile.getInputStream();
            importTransactionsForm.setImportTransactionsFileName(importTransationsFile.getFileName());

            final TransactionImport ti = new PluginManager().getImportPlugin(importPluginClassname);
            final UserReferenceDto userReferenceDTO = new UserReferenceDto(getUserContext(request).getId());
            ti.setUserReferenceDto(userReferenceDTO);
            final ParseResultDto importResult = ti.parse(new BufferedReader(new InputStreamReader(stream)));

            final List<String> errorsForDisplay = new ArrayList<String>();
            if (!importResult.parseErrors.isEmpty()) {
                errorsForDisplay.addAll(importResult.parseErrors);
                importTransactionsForm.setImportTransactionsErrors(errorsForDisplay);
            }
            request.setAttribute("importTransactionsErrors", errorsForDisplay);
            request.setAttribute("numSuccessfulRows", importResult.successfullyParsedRows.size());

            stream.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // destroy the temporary file created
        /* TODO: or keep it around? or write a different temp file the session knows about? */
        importTransationsFile.destroy();

        return mapping.findForward("import_results");
    }

    public ActionForward confirm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        /* TODO: call store() */
        return mapping.findForward("import_confirm");
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return new DummyImportTransactionService();
    }

    class DummyImportTransactionService implements BusinessService {

        @Override
        public BusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }
    }
}