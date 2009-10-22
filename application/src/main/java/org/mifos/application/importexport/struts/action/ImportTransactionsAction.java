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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mifos.api.accounts.UserReferenceDto;
import org.mifos.application.importexport.struts.actionforms.ImportTransactionsActionForm;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.UnicodeUtil;
import org.mifos.spi.ParseResultDto;
import org.mifos.spi.TransactionImport;

/**
 * This class takes the {@link ImportTransactionsActionForm} and retrieves file
 * with details transactions received from a third party (mostly a bank) and
 * import the details to collection sheet entry module
 */

public class ImportTransactionsAction extends BaseAction {
    private static final MifosLogger logger = MifosLogManager.getLogger(ImportTransactionsAction.class.getName());

    static final String IMPORT_TEMPORARY_FILENAME = "importTemporaryFilename";
    static final String IMPORT_PLUGIN_CLASSNAME = "importPluginClassname";

    public static ActionSecurity getSecurity() {
        final ActionSecurity security = new ActionSecurity("manageImportAction");
        security.allow("load", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("upload", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("confirm", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        return security;
    }

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        clearOurSessionVariables(request.getSession());
        final List<ListItem<String>> importPlugins = new ArrayList<ListItem<String>>();
        for (TransactionImport ti : new PluginManager().loadImportPlugins()) {
            importPlugins.add(new ListItem<String>(ti.getClass().getName(), ti.getDisplayName()));
        }
        request.setAttribute("importPlugins", importPlugins);
        return mapping.findForward("import_load");
    }

    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final ImportTransactionsActionForm importTransactionsForm = (ImportTransactionsActionForm) form;

        final FormFile importTransactionsFile = importTransactionsForm.getImportTransactionsFile();
        final String importPluginClassname = importTransactionsForm.getImportPluginName();
        request.getSession().setAttribute(IMPORT_PLUGIN_CLASSNAME, importPluginClassname);

        final InputStream stream = importTransactionsFile.getInputStream();
        final String tempFilename = saveImportAsTemporaryFile(importTransactionsFile.getInputStream());
        request.getSession().setAttribute(IMPORT_TEMPORARY_FILENAME, tempFilename);
        importTransactionsForm.setImportTransactionsFileName(importTransactionsFile.getFileName());

        final TransactionImport ti = getInitializedImportPlugin(importPluginClassname, getUserContext(request).getId());
        final ParseResultDto importResult = ti.parse(UnicodeUtil.getUnicodeAwareBufferedReader(tempFilename));

        final List<String> errorsForDisplay = new ArrayList<String>();
        if (!importResult.parseErrors.isEmpty()) {
            errorsForDisplay.addAll(importResult.parseErrors);
            importTransactionsForm.setImportTransactionsErrors(errorsForDisplay);
        }
        request.setAttribute("importTransactionsErrors", errorsForDisplay);
        request.setAttribute("numSuccessfulRows", importResult.successfullyParsedRows.size());

        stream.close();

        importTransactionsFile.destroy();

        return mapping.findForward("import_results");
    }

    private TransactionImport getInitializedImportPlugin(String importPluginClassname, Short userId) {
        final TransactionImport ti = new PluginManager().getImportPlugin(importPluginClassname);
        final UserReferenceDto userReferenceDTO = new UserReferenceDto(userId);
        ti.setUserReferenceDto(userReferenceDTO);
        return ti;
    }

    /**
     * This will fail if we ever cluster Mifos dynamic Web requests.
     */
    private String saveImportAsTemporaryFile(InputStream input) throws IOException {
        File tempFile = File.createTempFile(this.getClass().getSimpleName(), null);
        FileOutputStream out = new FileOutputStream(tempFile);
        BufferedInputStream in = new BufferedInputStream(input);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return tempFile.getCanonicalPath();
    }

    public ActionForward confirm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String tempFilename = (String) request.getSession().getAttribute(IMPORT_TEMPORARY_FILENAME);
        final String importPluginClassname = (String) request.getSession().getAttribute(IMPORT_PLUGIN_CLASSNAME);
        clearOurSessionVariables(request.getSession());
        final TransactionImport ti = getInitializedImportPlugin(importPluginClassname, getUserContext(request).getId());

        BufferedReader in = UnicodeUtil.getUnicodeAwareBufferedReader(tempFilename);
        final ParseResultDto importResult = ti.parse(in);
        in.close();

        in = UnicodeUtil.getUnicodeAwareBufferedReader(tempFilename);
        ti.store(in);
        in.close();

        logger.info(importResult.successfullyParsedRows.size() + " transactions imported.");

        new File(tempFilename).delete();
        return mapping.findForward("import_confirm");
    }

    /**
     * Used to remove our (this action's) temporaries after use to keep them
     * from accumulating in the session. This probably breaks the "back" button.
     * Using hidden form fields to persist temporaries might be more
     * user-friendly.
     */
    private void clearOurSessionVariables(HttpSession session) {
        session.removeAttribute(IMPORT_TEMPORARY_FILENAME);
        session.removeAttribute(IMPORT_PLUGIN_CLASSNAME);
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