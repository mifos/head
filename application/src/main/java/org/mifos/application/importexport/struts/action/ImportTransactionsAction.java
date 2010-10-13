/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mifos.accounts.api.UserReferenceDto;
import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.mifos.application.importexport.servicefacade.WebTierImportTransactionsServiceFacade;
import org.mifos.application.importexport.struts.actionforms.ImportTransactionsActionForm;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.spi.ParseResultDto;
import org.mifos.spi.TransactionImport;

/**
 * This class takes the {@link ImportTransactionsActionForm} and retrieves file
 * with details transactions received from a third party (mostly a bank) and
 * import the details to collection sheet entry module
 */

public class ImportTransactionsAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ImportTransactionsAction.class);

    static final String IMPORT_TEMPORARY_FILENAME = "importTemporaryFilename";
    static final String IMPORT_PLUGIN_CLASSNAME = "importPluginClassname";

	public static final String SESSION_ATTRIBUTE_LOG = "importTransactionLog";
	public static final String SESSION_ATTRIBUTE_LOG_FILENAME = "importTransactionLogFilename";

    public static ActionSecurity getSecurity() {
        final ActionSecurity security = new ActionSecurity("manageImportAction");
        security.allow("load", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("upload", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("confirm", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
		security.allow("downloadLog", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        return security;
    }

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final ImportTransactionsActionForm importTransactionsForm = (ImportTransactionsActionForm) form;
        importTransactionsForm.clear();
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

        final TransactionImport ti = getInitializedImportPlugin(importPluginClassname, getUserContext(request).getId());
        final ParseResultDto importResult = ti.parse(importTransactionsFile.getInputStream());

        final List<String> errorsForDisplay = new ArrayList<String>();
        if (!importResult.getParseErrors().isEmpty()) {
            errorsForDisplay.addAll(importResult.getParseErrors());
        }


        int numberRowSuccessfullyParsed = ti.getSuccessfullyParsedRows();

        if(numberRowSuccessfullyParsed == -1) {
            numberRowSuccessfullyParsed = importResult.getSuccessfullyParsedPayments().size();
        }
        boolean submitButtonDisabled = false;
        if (numberRowSuccessfullyParsed == 0) {
            submitButtonDisabled = true;
        }

		if (importResult.isAmountInformationFilled() && importResult.isExtraRowInformationFilled()) {
			request.setAttribute("isExtraInformationFilled", true);
			request.setAttribute("numberOfErrorRows", importResult.getNumberOfErrorRows());
			request.setAttribute("numberOfIgnoredRows", importResult.getNumberOfIgnoredRows());
			request.setAttribute("numberOfReadRows", importResult.getNumberOfReadRows());
			request.setAttribute("totalAmountOfTransactionsImported", importResult.getTotalAmountOfTransactionsImported());
			request.setAttribute("totalAmountOfTransactionsWithError", importResult.getTotalAmountOfTransactionsWithError());

			request.getSession().setAttribute(SESSION_ATTRIBUTE_LOG, generateStatusLogfile(importResult, ti).getBytes());
			request.getSession().setAttribute(SESSION_ATTRIBUTE_LOG_FILENAME, statusLogfileName(importTransactionsFile.getFileName()));
		} else {
			request.setAttribute("isExtraInformationFilled", false);
		}

        request.setAttribute("importTransactionsErrors", errorsForDisplay);
        request.setAttribute("numSuccessfulRows", numberRowSuccessfullyParsed);
        request.setAttribute("submitButtonDisabled", submitButtonDisabled);

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
        final TransactionImport transactionImport = getInitializedImportPlugin(importPluginClassname, getUserContext(
                request).getId());

        final ParseResultDto importResult = transactionImport.parse(new FileInputStream(tempFilename));
        final ImportTransactionsActionForm importTransactionsForm = (ImportTransactionsActionForm) form;
        final String importTransactionsFileName = importTransactionsForm.getImportTransactionsFile().getFileName();

        if (null != importResult.getParseErrors() && !importResult.getParseErrors().isEmpty()) {
            for (String error : importResult.getParseErrors()) {
                logger.warn(importTransactionsFileName + ": " + error);
            }
        }

        transactionImport.store(new FileInputStream(tempFilename));

        final UserContext userContext = getUserContext(request);
        ImportTransactionsServiceFacade importedFilesServiceFacade = new WebTierImportTransactionsServiceFacade();
        importedFilesServiceFacade.saveImportedFileName(userContext, importTransactionsFileName);

        logger.info(transactionImport.getSuccessfullyParsedRows() + " transaction(s) imported from "
                + importTransactionsFileName + ".");

        new File(tempFilename).delete();
        return mapping.findForward("import_confirm");
    }

	public ActionForward downloadLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
		byte[] fileContents = (byte[]) request.getSession().getAttribute(ImportTransactionsAction.SESSION_ATTRIBUTE_LOG);
		String fileName = (String) request.getSession().getAttribute(ImportTransactionsAction.SESSION_ATTRIBUTE_LOG_FILENAME);
		response.setHeader("Content-disposition",
				"attachment; filename=" + fileName);
		response.setContentType("text/plain");
		IOUtils.copy(new ByteArrayInputStream(fileContents), response.getOutputStream());
		return null;
	}
	
	private static final String LOG_TEMPLATE =
			"%d rows were read.\n" +
			"\n" +
			"%d rows contained no errors and will be imported\n" +
			"%d rows will be ignored\n" +
			"%d rows contained errors and were not imported\n" +
			"\n" +
			"Total amount of transactions imported: %s\n" +
			"Total amount of transactions with error: %s\n" +
			"\n" +
			"%s";

	private String generateStatusLogfile(ParseResultDto result, TransactionImport transactionImport) {
		String rowErrors = "";
		if (!result.getParseErrors().isEmpty())
			rowErrors = StringUtils.join(result.getParseErrors(), System.getProperty("line.separator"));
		return String.format(LOG_TEMPLATE,
				result.getNumberOfReadRows(),
				transactionImport.getSuccessfullyParsedRows(),
				result.getNumberOfIgnoredRows(),
				result.getNumberOfErrorRows(),
				result.getTotalAmountOfTransactionsImported().toString(),
				result.getTotalAmountOfTransactionsWithError().toString(),
				rowErrors);
	}

	private String statusLogfileName(String uploadedFilename) {
		if (uploadedFilename.contains("."))
			return uploadedFilename.split("\\.")[0] + "-log.txt";
		return uploadedFilename + "-log.txt";
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
		session.removeAttribute(SESSION_ATTRIBUTE_LOG);
		session.removeAttribute(SESSION_ATTRIBUTE_LOG_FILENAME);
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return new DummyImportTransactionService();
    }

    class DummyImportTransactionService implements BusinessService {
        @Override
        public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }
    }
}