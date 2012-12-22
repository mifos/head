package org.mifos.accounting.struts.actionform;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;

public class ConsolidatedTransactionActionForm  extends BaseActionForm {
	
	
	private String branch;
	private String trxnDate;
	private String officeLevelId;
	private String CrBrTotal;
	private String CpBpTotal;
	private String [] transactionNo;
	private String[] amount;
	private String[] subAccountType;
	private String[] transactionCpBpNo;
	private String[] CpBpamount;
	private String[] subAccountCpBpType;
	
	public static final int approveStage = 1;
	public static final int rejectStage = -1;

	private String stageTransactionNo;
	private String stageTrxnDate;
	private String stageTrxnId;
	private String stageTrxnType;
	private String stageMainAccount;
	private String stageOfficeHierarchy;
	private String stageOffice;
	private String stageAccountHead;
	private String stageAmount;
	private String stageNotes;

	// staging
	private String stage;
	private String transactionDetailID;

	// chequeDetails
	private String stageChequeNo;
	private String chequeDate;
	private String stageBankName;
	private String stageankBranch;

	// Audit
	private String audit;
	private String auditComments;
	private String toTrxnDate;
	private String fromTrxnDate;
	private String transactiondate;
	
	private String lastProcessDateDD;
	private String lastProcessDateMM;
	private String lastProcessDateYY;
	

	public String getLastProcessDateDD() {
		return lastProcessDateDD;
	}

	public String getLastProcessDateMM() {
		return lastProcessDateMM;
	}

	public String getLastProcessDateYY() {
		return lastProcessDateYY;
	}

	public void setLastProcessDateDD(String lastProcessDateDD) {
		this.lastProcessDateDD = lastProcessDateDD;
	}

	public void setLastProcessDateMM(String lastProcessDateMM) {
		this.lastProcessDateMM = lastProcessDateMM;
	}

	public void setLastProcessDateYY(String lastProcessDateYY) {
		this.lastProcessDateYY = lastProcessDateYY;
	}

	public String getTransactiondate() {
		return transactiondate;
	}

	public void setTransactiondate(String transactiondate) {
		this.transactiondate = transactiondate;
	}

	public String getStageTransactionNo() {
		return stageTransactionNo;
	}

	public String getStageTrxnDate() {
		return stageTrxnDate;
	}

	public String getStageTrxnId() {
		return stageTrxnId;
	}

	public String getStageTrxnType() {
		return stageTrxnType;
	}

	public String getStageMainAccount() {
		return stageMainAccount;
	}

	public String getStageOfficeHierarchy() {
		return stageOfficeHierarchy;
	}

	public String getStageOffice() {
		return stageOffice;
	}

	public String getStageAccountHead() {
		return stageAccountHead;
	}

	public String getStageAmount() {
		return stageAmount;
	}

	public String getStageNotes() {
		return stageNotes;
	}

	public String getStage() {
		return stage;
	}

	public String getTransactionDetailID() {
		return transactionDetailID;
	}

	public String getStageChequeNo() {
		return stageChequeNo;
	}

	public String getStageBankName() {
		return stageBankName;
	}

	public String getStageankBranch() {
		return stageankBranch;
	}

	public String getAudit() {
		return audit;
	}

	public String getAuditComments() {
		return auditComments;
	}

	public String getToTrxnDate() {
		return toTrxnDate;
	}

	public String getFromTrxnDate() {
		return fromTrxnDate;
	}

	public void setStageTransactionNo(String stageTransactionNo) {
		this.stageTransactionNo = stageTransactionNo;
	}

	public void setStageTrxnDate(String stageTrxnDate) {
		this.stageTrxnDate = stageTrxnDate;
	}

	public void setStageTrxnId(String stageTrxnId) {
		this.stageTrxnId = stageTrxnId;
	}

	public void setStageTrxnType(String stageTrxnType) {
		this.stageTrxnType = stageTrxnType;
	}

	public void setStageMainAccount(String stageMainAccount) {
		this.stageMainAccount = stageMainAccount;
	}

	public void setStageOfficeHierarchy(String stageOfficeHierarchy) {
		this.stageOfficeHierarchy = stageOfficeHierarchy;
	}

	public void setStageOffice(String stageOffice) {
		this.stageOffice = stageOffice;
	}

	public void setStageAccountHead(String stageAccountHead) {
		this.stageAccountHead = stageAccountHead;
	}

	public void setStageAmount(String stageAmount) {
		this.stageAmount = stageAmount;
	}

	public void setStageNotes(String stageNotes) {
		this.stageNotes = stageNotes;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public void setTransactionDetailID(String transactionDetailID) {
		this.transactionDetailID = transactionDetailID;
	}

	public void setStageChequeNo(String stageChequeNo) {
		this.stageChequeNo = stageChequeNo;
	}

	public void setStageBankName(String stageBankName) {
		this.stageBankName = stageBankName;
	}

	public void setStageankBranch(String stageankBranch) {
		this.stageankBranch = stageankBranch;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public void setAuditComments(String auditComments) {
		this.auditComments = auditComments;
	}

	public void setToTrxnDate(String toTrxnDate) {
		this.toTrxnDate = toTrxnDate;
	}

	public void setFromTrxnDate(String fromTrxnDate) {
		this.fromTrxnDate = fromTrxnDate;
	}

	public String[] getTransactionNo() {
		return transactionNo;
	}

	public String[] getAmount() {
		return amount;
	}

	public String[] getSubAccountType() {
		return subAccountType;
	}

	public String[] getTransactionCpBpNo() {
		return transactionCpBpNo;
	}

	public String[] getCpBpamount() {
		return CpBpamount;
	}

	public String[] getSubAccountCpBpType() {
		return subAccountCpBpType;
	}

	public void setTransactionNo(String[] transactionNo) {
		this.transactionNo = transactionNo;
	}

	public void setAmount(String[] amount) {
		this.amount = amount;
	}

	public void setSubAccountType(String[] subAccountType) {
		this.subAccountType = subAccountType;
	}

	public void setTransactionCpBpNo(String[] transactionCpBpNo) {
		this.transactionCpBpNo = transactionCpBpNo;
	}

	public void setCpBpamount(String[] cpBpamount) {
		CpBpamount = cpBpamount;
	}

	public void setSubAccountCpBpType(String[] subAccountCpBpType) {
		this.subAccountCpBpType = subAccountCpBpType;
	}

	public String getOfficeLevelId() {
		return officeLevelId;
	}

	public String getCrBrTotal() {
		return CrBrTotal;
	}

	public String getCpBpTotal() {
		return CpBpTotal;
	}

	public void setCrBrTotal(String crBrTotal) {
		CrBrTotal = crBrTotal;
	}

	public void setCpBpTotal(String cpBpTotal) {
		CpBpTotal = cpBpTotal;
	}

	public void setOfficeLevelId(String officeLevelId) {
		this.officeLevelId = officeLevelId;
	}
	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
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

	private String chequeDateDD;
	private String chequeDateMM;
	private String chequeDateYY;
	public void setChequeDate(String s) {
		setChequeDate(DateUtils.getDate(s));
	}

	public void setTrxnDate(String trxnDate) {
		this.trxnDate = trxnDate;
	}

	public void setTrxnDate(java.util.Date date) {
		this.trxnDate = DateUtils.format(date);
	}

	public String getTrxnDate() {
		return trxnDate;
	}
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load")) {
			this.trxnDate = null;
			this.branch=null;
			this.toTrxnDate=null;
		}

	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.APPROVE)) {

			return mandatoryCheck(getUserContext(request));
		}
		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.REJECT)) {

			return mandatoryCheck(getUserContext(request));
		}
		return errors;
	}
	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

		String trxn_Date = resources
				.getString(SimpleAccountingConstants.TRXNDATE);
		String officeId = resources.getString(SimpleAccountingConstants.OFFICE);
		

		ActionErrors errors = new ActionErrors();
		if (branch == null || "".equals(branch.trim())) {
			errors.add(
					SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, officeId));
		}

		return errors;
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
	public void setLastProcessDate(String s) {
		setLastProcessDate(DateUtils.getDate(s));
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
	public String getChequeDate() {
		if (StringUtils.isBlank(chequeDateDD)
				|| StringUtils.isBlank(chequeDateMM)
				|| StringUtils.isBlank(chequeDateYY)) {
			return null;
		}
		return chequeDateDD + "/" + chequeDateMM + "/" + chequeDateYY;
	}
	
	
	
	
	
}
