package org.mifos.accounting.struts.actionform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.dto.domain.CoaNamesDto;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoucherBranchMappingActionForm extends BaseActionForm {
	private static final Logger logger = LoggerFactory
			.getLogger(VoucherBranchMappingActionForm.class);

	private String[] sno;
	private String[] coaname;
	private String[] amount;
	private String[] transactionnotes;
	private String total;
	private String branch;
	private String transactiontype;
	private String mainAccount;
	private String transactiondate;
	private String chequeNo;
	private String chequeDateDD;
	private String chequeDateMM;
	private String chequeDateYY;
	private String bankName;
	private String bankBranch;
	private String officeLevelId;
	private String officeHierarchy;
	private String office;
	private String processTillDate;
	private String lastProcessDateDD;
	private String lastProcessDateMM;
	private String lastProcessDateYY;

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}


	public String getProcessTillDate() {
		return processTillDate;
	}

	public void setProcessTillDate(String processTillDate) {
		this.processTillDate = processTillDate;
	}



	public String getLastProcessDateYY() {
		return lastProcessDateYY;
	}

	public void setLastProcessDateYY(String lastProcessDateYY) {
		this.lastProcessDateYY = lastProcessDateYY;
	}

	public String getLastProcessDateDD() {
		return lastProcessDateDD;
	}

	public void setLastProcessDateDD(String lastProcessDateDD) {
		this.lastProcessDateDD = lastProcessDateDD;
	}

	public String getLastProcessDateMM() {
		return lastProcessDateMM;
	}

	public void setLastProcessDateMM(String lastProcessDateMM) {
		this.lastProcessDateMM = lastProcessDateMM;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getOfficeHierarchy() {
		return officeHierarchy;
	}

	public void setOfficeHierarchy(String officeHierarchy) {
		this.officeHierarchy = officeHierarchy;
	}

	public String getOfficeLevelId() {
		return officeLevelId;
	}

	public void setOfficeLevelId(String officeLevelId) {
		this.officeLevelId = officeLevelId;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDateDD() {
		return chequeDateDD;
	}

	public void setChequeDateDD(String chequeDateDD) {
		this.chequeDateDD = chequeDateDD;
	}

	public String getChequeDateMM() {
		return chequeDateMM;
	}

	public void setChequeDateMM(String chequeDateMM) {
		this.chequeDateMM = chequeDateMM;
	}

	public String getChequeDateYY() {
		return chequeDateYY;
	}

	public void setChequeDateYY(String chequeDateYY) {
		this.chequeDateYY = chequeDateYY;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public void setTransactiondate(java.util.Date date) {

		this.transactiondate = DateUtils.format(date);
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getTransactiontype() {
		return transactiontype;
	}

	public void setTransactiontype(String transactiontype) {
		this.transactiontype = transactiontype;
	}

	public String getMainAccount() {
		return mainAccount;
	}

	public void setMainAccount(String mainAccount) {
		this.mainAccount = mainAccount;
	}

	public String getTransactiondate() {
		return transactiondate;
	}

	public void setTransactiondate(String transactiondate) {
		this.transactiondate = transactiondate;
	}

	public String[] getSno() {
		return sno;
	}

	public void setSno(String[] sno) {
		this.sno = sno;
	}

	public String[] getCoaname() {
		return coaname;
	}

	public void setCoaname(String[] coaname) {
		this.coaname = coaname;
	}

	public String[] getAmount() {
		return amount;
	}

	public void setAmount(String[] amount) {
		this.amount = amount;
	}

	public String[] getTransactionnotes() {
		return transactionnotes;
	}

	public void setTransactionnotes(String[] transactionnotes) {
		this.transactionnotes = transactionnotes;
	}

	public void setChequeDate(String s) {
		setChequeDate(DateUtils.getDate(s));
	}

	public void setLastProcessDate(String s) {
		setLastProcessDate(DateUtils.getDate(s));
	}

	public void setLastProcessDate(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// note that Calendar retrieves 0-based month, so increment month field
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		setLastProcessDateDD(Integer.toString(day));
		setLastProcessDateMM(Integer.toString(month));
		setLastProcessDateYY(Integer.toString(year));
	}

	public String getLastProcessDate() {
		if (StringUtils.isBlank(lastProcessDateDD)
				|| StringUtils.isBlank(lastProcessDateMM)
				|| StringUtils.isBlank(lastProcessDateYY)) {
			return null;
		}
		return lastProcessDateDD + "/" + lastProcessDateMM + "/"
				+ lastProcessDateYY;
	}
	public void setChequeDate(java.util.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// note that Calendar retrieves 0-based month, so increment month field
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH) + 1;
		int year = c.get(Calendar.YEAR);
		setChequeDateDD(Integer.toString(day));
		setChequeDateMM(Integer.toString(month));
		setChequeDateYY(Integer.toString(year));
	}

	public String getChequeDate() {
		if (StringUtils.isBlank(chequeDateDD)
				|| StringUtils.isBlank(chequeDateMM)
				|| StringUtils.isBlank(chequeDateYY)) {
			return null;
		}
		return chequeDateDD + "/" + chequeDateMM + "/" + chequeDateYY;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("VoucherBranchMappingActionForm.validate");
		// request.setAttribute(Constants.CURRENTFLOWKEY,
		// request.getParameter(Constants.CURRENTFLOWKEY));
		ActionErrors errors = new ActionErrors();

		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PREVIEW)) {
			return mandatoryCheck(getUserContext(request));
		}
		return errors;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load"))

		{
			this.sno = null;
			this.coaname = null;
			transactionnotes = new String[]{};
			amount = new String[]{};
			this.branch = null;
			this.mainAccount = null;
			this.transactiontype = null;


		}

	}
	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
		ActionErrors errors = new ActionErrors();
		String branchname = resources
				.getString(SimpleAccountingConstants.BRANCH_NAME);
		String Amount = resources.getString(SimpleAccountingConstants.AMOUNT);
		String trxn_Type = resources
				.getString(SimpleAccountingConstants.TRXNTYPE);
		String main_Account = resources
				.getString(SimpleAccountingConstants.MAIN_ACCOUNT);
		String Notes = resources
				.getString(SimpleAccountingConstants.TRXN_NOTES);
		String trxn_Date = resources
				.getString(SimpleAccountingConstants.TRXNDATE);
		String[] amts = getAmount();
		String[] trannotes = getTransactionnotes();
		// String[] canames=getCoaname();
		List<CoaNamesDto> coaNamesDtolist = new ArrayList<CoaNamesDto>();
		if (amts.length < 0 || "".equals(amts)) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							Amount));
		}
		for(int i=0;i<amts.length;i++)
		{
			if(amts[i].equalsIgnoreCase("0"))
			{
				errors.add(SimpleAccountingConstants.MANDATORYENTER,
						new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
								Amount));
			}
		}
		if (trannotes == null || "".equals(trannotes)) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Notes));
		}

		{
			for (int i = 0; i < getAmount().length; i++) {
				CoaNamesDto coanamesob = new CoaNamesDto();
				coanamesob.setAmount(amts[i]);
				coanamesob.setTransactionnotes(trannotes[i]);
				// coanamesob.setCoaName(canames[i]);
				coaNamesDtolist.add(coanamesob);
			}
		}

		java.sql.Date currentDate = null;
		try {
			currentDate = DateUtils.getLocaleDate(
					userContext.getPreferredLocale(),
					DateUtils.getCurrentDate(userContext.getPreferredLocale()));
		} catch (InvalidDateException ide) {
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE));
		}

		java.sql.Date trxnDate = null;

		if (getTransactiondate() == null || "".equals(getTransactiondate())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							trxn_Date));
		} else if (getTransactiondate() != null
				&& !getTransactiondate().equals("")
				&& !DateUtils.isValidDate(getTransactiondate())) {
			errors = trxnDateValidate(errors, locale);
		} else if (DateUtils.isValidDate(getTransactiondate())) {
			try {
				trxnDate = DateUtils
						.getDateAsSentFromBrowser(getTransactiondate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_TRXN_DATE,
								trxn_Date));
			}
			if (trxnDate.compareTo(currentDate) > 0) {
				errors.add(SimpleAccountingConstants.INVALID_FUTURE,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_FUTURE,
								trxn_Date));
			}
		}

		if (getBranch() == null || "".equals(getBranch())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							branchname));
		}

		
		if (transactiontype == null || "".equals(transactiontype.trim())) {
			 errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new
			  ActionMessage( SimpleAccountingConstants.MANDATORYFIELDS,
			 trxn_Type));
			 }
		if (mainAccount == null || "".equals(mainAccount.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							main_Account));
		}
		
		return errors;
	}

	
	private ActionErrors trxnDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getTransactiondate())
				&& !DateUtils.isValidDate(getTransactiondate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String trxnDate = resources
					.getString(SimpleAccountingConstants.TRXNDATE);
			errors.add(SimpleAccountingConstants.INVALID_TRXN_DATE,
					new ActionMessage(
							SimpleAccountingConstants.INVALID_TRXN_DATE,
							trxnDate));
		}
		return errors;
	}
}