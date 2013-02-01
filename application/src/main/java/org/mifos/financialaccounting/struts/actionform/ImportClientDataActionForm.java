package org.mifos.financialaccounting.struts.actionform;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.security.util.UserContext;

public class ImportClientDataActionForm extends BaseActionForm{
	private FormFile importTransactionsFile;

	public FormFile getImportTransactionsFile() {
		return importTransactionsFile;
	}

	public void setImportTransactionsFile(FormFile importTransactionsFile) {
		this.importTransactionsFile = importTransactionsFile;
	}


	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors actionErrors=new ActionErrors();
		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.UPLOADXLSDATA)) {
			return mandatoryCheck(getUserContext(request));
		}

		return actionErrors;
	   }

	private ActionErrors mandatoryCheck(UserContext userContext) {

		ActionErrors errors =new ActionErrors();
		if(getImportTransactionsFile()==null||importTransactionsFile.getFileSize() == 0)
		{
			errors.add(SimpleAccountingConstants.BROWSE_XLSHEET, new ActionMessage(SimpleAccountingConstants.BROWSE_XLSHEET));
		}
		else if (!importTransactionsFile.getContentType().equals("application/vnd.ms-excel")) {
	errors.add(SimpleAccountingConstants.VALID_XLSHEET, new ActionMessage(SimpleAccountingConstants.VALID_XLSHEET));
        }
		return errors;
	}


}
