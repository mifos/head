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
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewStageTransactionActionForm extends BaseActionForm {

	private static final Logger logger = LoggerFactory
			.getLogger(GeneralLedgerActionForm.class);
	// stage values
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

	public String getToTrxnDate() {
		return toTrxnDate;
	}

	public void setToTrxnDate(java.util.Date date) {
		this.toTrxnDate = DateUtils.format(date);
	}

	public String getFromTrxnDate() {
		return fromTrxnDate;
	}

	public void setFromTrxnDate(String fromTrxnDate) {
		this.fromTrxnDate = fromTrxnDate;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getAudit() {
		return audit;
	}

	public void setAudit(String audit) {
		this.audit = audit;
	}

	public String getAuditComments() {
		return auditComments;
	}

	public void setAuditComments(String auditComments) {
		this.auditComments = auditComments;
	}

	public String getTransactionDetailID() {
		return transactionDetailID;
	}

	public void setTransactionDetailID(String transactionDetailID) {
		this.transactionDetailID = transactionDetailID;
	}

	public String getStageTransactionNo() {
		return stageTransactionNo;
	}

	public void setStageTransactionNo(String stageTransactionNo) {
		this.stageTransactionNo = stageTransactionNo;
	}

	public String getStageTrxnDate() {
		return stageTrxnDate;
	}

	public void setStageTrxnDate(String stageTrxnDate) {
		this.stageTrxnDate = stageTrxnDate;
	}

	public String getStageTrxnId() {
		return stageTrxnId;
	}

	public void setStageTrxnId(String stageTrxnId) {
		this.stageTrxnId = stageTrxnId;
	}

	public String getStageTrxnType() {
		return stageTrxnType;
	}

	public void setStageTrxnType(String stageTrxnType) {
		this.stageTrxnType = stageTrxnType;
	}

	public String getStageMainAccount() {
		return stageMainAccount;
	}

	public void setStageMainAccount(String stageMainAccount) {
		this.stageMainAccount = stageMainAccount;
	}

	public String getStageOfficeHierarchy() {
		return stageOfficeHierarchy;
	}

	public void setStageOfficeHierarchy(String stageOfficeHierarchy) {
		this.stageOfficeHierarchy = stageOfficeHierarchy;
	}

	public String getStageOffice() {
		return stageOffice;
	}

	public void setStageOffice(String stageTOffice) {
		this.stageOffice = stageTOffice;
	}

	public String getStageAccountHead() {
		return stageAccountHead;
	}

	public void setStageAccountHead(String stageAccountHead) {
		this.stageAccountHead = stageAccountHead;
	}

	public String getStageAmount() {
		return stageAmount;
	}

	public void setStageAmount(String stageTamount) {
		this.stageAmount = stageTamount;
	}

	public String getStageNotes() {
		return stageNotes;
	}

	public void setStageNotes(String stageNotes) {
		this.stageNotes = stageNotes;
	}

	public String getStageChequeNo() {
		return stageChequeNo;
	}

	public void setStageChequeNo(String stageChequeNo) {
		this.stageChequeNo = stageChequeNo;
	}

	public String getStageBankName() {
		return stageBankName;
	}

	public void setStageBankName(String stageBankName) {
		this.stageBankName = stageBankName;
	}

	public String getStageankBranch() {
		return stageankBranch;
	}

	public void setStageankBranch(String stageankBranch) {
		this.stageankBranch = stageankBranch;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("reset method called");
		if (request.getParameter(SimpleAccountingConstants.METHOD).equals(
				"load")) {
			this.stageTrxnDate = null;
			this.stageOffice = null;
			this.stageOfficeHierarchy = null;
			this.stageTrxnType = null;
			this.stageMainAccount = null;
			this.stageAccountHead = null;
			this.stageAmount = null;
			this.stageChequeNo = null;
			this.stageBankName = null;
			this.stageankBranch = null;
			this.stageNotes = null;
			this.chequeDate = null;
		}

		this.audit = null;
		this.auditComments = null;

	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("GeneralLedgerActionForm.validate");
		// request.setAttribute(Constants.CURRENTFLOWKEY,
		// request.getParameter(Constants.CURRENTFLOWKEY));
		ActionErrors errors = new ActionErrors();

		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PREVIEW)) {
			return mandatoryCheck(getUserContext(request));
		}
		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PROCESS)) {
			return mandatoryCheckForAudit(getUserContext(request));
		}
		if (request.getParameter(SimpleAccountingConstants.METHOD)
				.equalsIgnoreCase(SimpleAccountingConstants.PICKDATE)) {
			System.out.println("inside pick date: 1");
			return mandotoryCheckForPickDate(getUserContext(request));
		}

		return errors;
	}


	private ActionErrors trxnDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getStageTrxnDate())
				&& !DateUtils.isValidDate(getStageTrxnDate())) {
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

	private ActionErrors chequeDateValidate(ActionErrors errors, Locale locale) {
		if (StringUtils.isNotBlank(getChequeDate())
				&& !DateUtils.isValidDate(getChequeDate())) {
			ResourceBundle resources = ResourceBundle.getBundle(
					FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
			String chequeDate = resources
					.getString(SimpleAccountingConstants.CHEQUE_DATE);
			errors.add(SimpleAccountingConstants.INVALIDDATE,
					new ActionMessage(SimpleAccountingConstants.INVALIDDATE,
							chequeDate));
		}
		return errors;
	}

	private ActionErrors mandatoryCheckForAudit(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
		String audit = resources.getString(SimpleAccountingConstants.AUDIT);
		String audit_comments = resources
				.getString(SimpleAccountingConstants.AUDIT_COMMENTS);

		ActionErrors errors = new ActionErrors();
		if (getAudit() == null || "".equals(getAudit())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							audit));
		} else if (getAuditComments() == null || "".equals(getAuditComments())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							audit_comments));
		}
		return errors;
	}

	private ActionErrors mandatoryCheck(UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle(
				FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

		String trxn_Date = resources
				.getString(SimpleAccountingConstants.TRXNDATE);
		String trxn_Type = resources
				.getString(SimpleAccountingConstants.TRXNTYPE);
		String office_Hierarchy = resources
				.getString(SimpleAccountingConstants.OFFICE_HIERARCHY);
		String officeId = resources.getString(SimpleAccountingConstants.OFFICE);
		String main_Account = resources
				.getString(SimpleAccountingConstants.MAIN_ACCOUNT);
		String subAccount = resources
				.getString(SimpleAccountingConstants.ACCOUNT_HEAD);
		String Amount = resources.getString(SimpleAccountingConstants.AMOUNT);
		String Notes = resources
				.getString(SimpleAccountingConstants.TRXN_NOTES);
		String cheque_date = resources
				.getString(SimpleAccountingConstants.CHEQUE_DATE);

		ActionErrors errors = new ActionErrors();
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

		if (getStageTrxnDate() == null || "".equals(getStageTrxnDate())) {
			errors.add(SimpleAccountingConstants.MANDATORYENTER,
					new ActionMessage(SimpleAccountingConstants.MANDATORYENTER,
							trxn_Date));
		} else if (getStageTrxnDate() != null && !getStageTrxnDate().equals("")
				&& !DateUtils.isValidDate(getStageTrxnDate())) {
			errors = trxnDateValidate(errors, locale);
		} else if (DateUtils.isValidDate(getStageTrxnDate())) {
			try {
				trxnDate = DateUtils
						.getDateAsSentFromBrowser(getStageTrxnDate());
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

		if (stageTrxnType == null || "".equals(stageTrxnType.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							trxn_Type));
		}

		if (stageOfficeHierarchy == null
				|| "".equals(stageOfficeHierarchy.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							office_Hierarchy));
		}

		if (stageOffice == null || "".equals(stageOffice.trim())) {
			errors.add(
					SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, officeId));
		}

		if (stageMainAccount == null || "".equals(stageMainAccount.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							main_Account));
		}

		if (stageAccountHead == null || "".equals(stageAccountHead.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS,
							subAccount));
		}

		if (stageAmount == null || "".equals(stageAmount.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Amount));
		}

		if (StringUtils.isNotBlank(getStageAmount())) {
			DoubleConversionResult conversionResult = validateAmount(
					getStageAmount(), Amount, errors);
			if (conversionResult.getErrors().size() == 0
					&& !(conversionResult.getDoubleValue() > 0.0)) {
				addError(
						errors,
						SimpleAccountingConstants.AMOUNT,
						SimpleAccountingConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
						Amount);
			}
		}

		if (stageNotes == null || "".equals(stageNotes.trim())) {
			errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
					new ActionMessage(
							SimpleAccountingConstants.MANDATORYFIELDS, Notes));
		}

		java.sql.Date chkDate = null;

		if (getChequeDate() != null && !getChequeDate().equals("")
				&& !DateUtils.isValidDate(getChequeDate())) {
			errors = trxnDateValidate(errors, locale);
		} else if (DateUtils.isValidDate(getChequeDate())) {
			try {
				chkDate = DateUtils.getDateAsSentFromBrowser(getChequeDate());
			} catch (InvalidDateException ide) {
				errors.add(SimpleAccountingConstants.MANDATORYFIELDS,
						new ActionMessage(
								SimpleAccountingConstants.INVALID_CHEQUE_DATE,
								cheque_date));
			}

		}

		if (getChequeDate() != null && !getChequeDate().equals("")
				&& !DateUtils.isValidDate(getChequeDate()))
			errors = chequeDateValidate(errors, locale);
		return errors;
	}

	protected DoubleConversionResult validateAmount(String amountString,
			MifosCurrency currency, String fieldPropertyKey,
			ActionErrors errors, String installmentNo) {
		String fieldName = fieldPropertyKey;
		DoubleConversionResult conversionResult = parseDoubleForMoney(
				amountString, currency);
		for (ConversionError error : conversionResult.getErrors()) {
			String errorText = error.toLocalizedMessage(currency);
			addError(errors, fieldName, "errors.generic", fieldName, errorText);
		}
		return conversionResult;
	}

	protected DoubleConversionResult validateAmount(String amountString,
			String fieldPropertyKey, ActionErrors errors) {
		return validateAmount(amountString, null, fieldPropertyKey, errors, "");
	}

	   private ActionErrors toTrxnDateValidate(ActionErrors errors, Locale locale) {
	        if (StringUtils.isNotBlank(getToTrxnDate()) && !DateUtils.isValidDate(getToTrxnDate())) {
	            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
	            String trxnDate = resources.getString(SimpleAccountingConstants.TO_TRXNDATE);
	            errors.add(SimpleAccountingConstants.INVALID_TRXN_DATE, new ActionMessage(
				SimpleAccountingConstants.INVALID_TRXN_DATE, trxnDate));
	        }
	        return errors;
	    }

	   private ActionErrors fromTrxnDateValidate(ActionErrors errors, Locale locale) {
	        if (StringUtils.isNotBlank(getFromTrxnDate()) && !DateUtils.isValidDate(getFromTrxnDate())) {
	            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
	            String trxnDate = resources.getString(SimpleAccountingConstants.FROM_TRXNDATE);
	            errors.add(SimpleAccountingConstants.INVALID_TRXN_DATE, new ActionMessage(
				SimpleAccountingConstants.INVALID_TRXN_DATE, trxnDate));
	        }
	        return errors;
	    }

	   private ActionErrors mandotoryCheckForPickDate(UserContext userContext) {
	        Locale locale = userContext.getPreferredLocale();
	        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

	        String to_Trxn_Date = resources.getString(SimpleAccountingConstants.TO_TRXNDATE);
	        String from_Trxn_Date = resources.getString(SimpleAccountingConstants.FROM_TRXNDATE);

	        ActionErrors errors = new ActionErrors();
	        java.sql.Date currentDate = null;
	        System.out.println("inside pick date: 2");
	        try {
	            currentDate = DateUtils.getLocaleDate(userContext.getPreferredLocale(), DateUtils
	                    .getCurrentDate(userContext.getPreferredLocale()));
	        } catch (InvalidDateException ide) {
	            errors.add(SimpleAccountingConstants.INVALIDDATE, new ActionMessage(
				SimpleAccountingConstants.INVALIDDATE));
	        }

	        java.sql.Date trxnDate = null;

	        if (getToTrxnDate() ==null || "".equals(getToTrxnDate())) {
			 errors.add(SimpleAccountingConstants.MANDATORYENTER, new ActionMessage(
					 SimpleAccountingConstants.MANDATORYENTER, to_Trxn_Date));
	        }
	        else if(getToTrxnDate() != null && !getToTrxnDate().equals("") && !DateUtils.isValidDate(getToTrxnDate())){
			 errors=toTrxnDateValidate(errors, locale);
	        }
	        else if(DateUtils.isValidDate(getToTrxnDate())){
			 try {
		                trxnDate = DateUtils.getDateAsSentFromBrowser(getToTrxnDate());
		            } catch (InvalidDateException ide) {
		                errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(SimpleAccountingConstants.INVALID_TRXN_DATE,to_Trxn_Date));
		            }
			 if(trxnDate.compareTo(currentDate)>0){
				 errors.add(SimpleAccountingConstants.INVALID_FUTURE, new ActionMessage(
						 SimpleAccountingConstants.INVALID_FUTURE, to_Trxn_Date));
			 }
	        }

	        if (getFromTrxnDate() ==null || "".equals(getFromTrxnDate())) {
				 errors.add(SimpleAccountingConstants.MANDATORYENTER, new ActionMessage(
						 SimpleAccountingConstants.MANDATORYENTER, from_Trxn_Date));
		        }
	        else if(getFromTrxnDate() != null && !getFromTrxnDate().equals("") && !DateUtils.isValidDate(getFromTrxnDate())){
				 errors=fromTrxnDateValidate(errors, locale);
		        }
	        else if(DateUtils.isValidDate(getFromTrxnDate())){
				 try {
			                trxnDate = DateUtils.getDateAsSentFromBrowser(getFromTrxnDate());
			            } catch (InvalidDateException ide) {
			                errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(SimpleAccountingConstants.INVALID_TRXN_DATE,from_Trxn_Date));
			            }
				 if(trxnDate.compareTo(currentDate)>0){
					 errors.add(SimpleAccountingConstants.INVALID_FUTURE, new ActionMessage(
							 SimpleAccountingConstants.INVALID_FUTURE, from_Trxn_Date));
				 }
		        }

	        return errors;
	    }

}
