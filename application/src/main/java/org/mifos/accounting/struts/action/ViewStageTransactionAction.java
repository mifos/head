package org.mifos.accounting.struts.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.LocalDate;
import org.mifos.accounting.struts.actionform.MultipleGeneralLedgerActionForm;
import org.mifos.accounting.struts.actionform.ViewGlTransactionsActionForm;
import org.mifos.accounting.struts.actionform.ViewStageTransactionActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlDetailDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.ViewGlTransactionPaginaitonVariablesDto;
import org.mifos.dto.domain.ViewStageTransactionsDto;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewStageTransactionAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(ViewStageTransactionAction.class);

	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;

		int noOfRecordsPerPage = 10; // Number of records show on per page
		int noOfPagesIndex = 10; // Number of pages index shown
		/*
		 * this program displays the pagination concept as view page displaying
		 * limited number of page links(number of page links value carrying with
		 * noOfPagesIndex)
		 */

		int totalNoOfRowsForPagination = nullIntconv(request
				.getParameter("totalNoOfRowsForPagination"));
		int iTotalPages = nullIntconv(request.getParameter("iTotalPages"));
		int iPageNo = nullIntconv(request.getParameter("iPageNo"));
		int cPageNo = nullIntconv(request.getParameter("cPageNo"));

		int startRecordCurrentPage = 0;
		int endRecordCurrentPage = 0;

		if (iPageNo == 0) {
			iPageNo = 0;
		} else {
			iPageNo = Math.abs((iPageNo - 1) * noOfRecordsPerPage);
		}

		List<ViewStageTransactionsDto> viewStageTransactionsDtos = accountingServiceFacade
				.getStageAccountingTransactions(null, iPageNo,
						noOfRecordsPerPage);

		storingSession(request, "ViewStageTransactionsDtos", viewStageTransactionsDtos);

		// this will count total number of rows

		totalNoOfRowsForPagination = accountingServiceFacade
				.getNumberOfStageTransactions();

		// // calculate next record start record and end record
		if (totalNoOfRowsForPagination < (iPageNo + noOfRecordsPerPage)) {
			endRecordCurrentPage = totalNoOfRowsForPagination;
		} else {
			endRecordCurrentPage = (iPageNo + noOfRecordsPerPage);
		}

		startRecordCurrentPage = (iPageNo + 1);
		iTotalPages = ((int) (Math.ceil((double) totalNoOfRowsForPagination
				/ noOfRecordsPerPage)));

		// // index of pages

		int cPage = 0;
		cPage = ((int) (Math.ceil((double) endRecordCurrentPage
				/ (noOfPagesIndex * noOfRecordsPerPage))));
		int prePageNo = (cPage * noOfPagesIndex)
				- ((noOfPagesIndex - 1) + noOfPagesIndex); // we can say it as
															// pre cPage
		int i = (cPage * noOfPagesIndex) + 1;
		ViewGlTransactionPaginaitonVariablesDto dto = new ViewGlTransactionPaginaitonVariablesDto();

		dto.setcPageNo(cPageNo);
		dto.setI(i);
		dto.setcPage(cPage);
		dto.setPrePageNo(prePageNo);
		dto.setNoOfPagesIndex(noOfPagesIndex);
		dto.setiPageNo(iPageNo);
		dto.setNoOfRecordsPerPage(noOfRecordsPerPage);
		dto.setiTotalPages(iTotalPages);
		dto.setStartRecordCurrentPage(startRecordCurrentPage);
		dto.setEndRecordCurrentPage(endRecordCurrentPage);
		dto.setTotalNoOfRowsForPagination(totalNoOfRowsForPagination);

		storingSession(request, "ViewGlTransactionPaginaitonVariablesDto", dto);

		return mapping.findForward("load_success");

	}

	public ActionForward reject(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		int transactionNo = Integer.parseInt(request.getParameter("txnNo"));
		accountingServiceFacade.approveStageAccountingTransactions(
				transactionNo, ViewStageTransactionActionForm.rejectStage);

		return mapping.findForward("approve_success");

	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;

		String stageTransactionNo = request.getParameter("txnNo");

		ViewStageTransactionsDto viewStageTransactionsDto = accountingServiceFacade
				.getstagedAccountingTransactions(stageTransactionNo);

		actionForm.setStageTrxnDate(changeDateFormat(viewStageTransactionsDto
				.getTransactionDate().toString()));
		actionForm.setStageOfficeHierarchy(this
				.getOfficeHierarchy(viewStageTransactionsDto.getOfficeLevel()));

		actionForm.setStageTrxnType(getTranType(viewStageTransactionsDto
				.getTransactionType()));

		actionForm.setStageMainAccount(viewStageTransactionsDto
				.getMainAccount());
		viewStageTransactionsDto.getSubAccount();
		actionForm
				.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
		actionForm.setStageOffice(viewStageTransactionsDto.getFromOfficeId());

		short s = new Integer(actionForm.getStageOfficeHierarchy())
				.shortValue();

		GlDetailDto glDetailDto = accountingServiceFacade
				.getChequeDetails(stageTransactionNo);

		if (glDetailDto != null) {
			actionForm.setStageChequeNo(glDetailDto.getChequeNo());
			if (glDetailDto.getChequeDate() != null) {
				actionForm.setChequeDate(changeDateFormat(glDetailDto
						.getChequeDate().toString()));
			}
			actionForm.setStageBankName(glDetailDto.getBankName());
			actionForm.setStageankBranch(glDetailDto.getBankBranch());
		}

		// load offices
		List<OfficeGlobalDto> officeDetailsDtos = null;

		if (actionForm.getStageOfficeHierarchy() == "0") {
			officeDetailsDtos = null;
			// To recognize center
		} else if (actionForm.getStageOfficeHierarchy() == "6") {
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("3"));
			// to recognize group
		} else if (actionForm.getStageOfficeHierarchy() == "7") {
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("2"));
		} else {
			officeDetailsDtos = accountingServiceFacade.loadOfficesForLevel(s);
		}

		storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
		// load main accounts
		List<GLCodeDto> accountingDtos = null;
		if (actionForm.getStageTrxnType().equals("CR")
				|| actionForm.getStageTrxnType().equals("CP")
				|| actionForm.getStageTrxnType().equals("BR")
				|| actionForm.getStageTrxnType().equals("BP")
				|| actionForm.getStageTrxnType().equals("JV")) {
			accountingDtos = accountingServiceFacade.auditAccountHeads();
		}

		storingSession(request, "MainAccountGlCodes", accountingDtos);
		storingSession(request, "stageTransactionNo", stageTransactionNo);

		actionForm.setTransactionDetailID(new Integer(viewStageTransactionsDto
				.getTransactionID()).toString());

		actionForm.setStageMainAccount(viewStageTransactionsDto
				.getMainAccount());
		actionForm
				.setStageAccountHead(viewStageTransactionsDto.getSubAccount());
		actionForm.setStageNotes(viewStageTransactionsDto.getNarration());
		actionForm.setStageAmount(String.format("%.2f", Double.parseDouble(viewStageTransactionsDto.getTransactionAmount())));
		storingSession(request, "ViewStageTransactionsDto",
				viewStageTransactionsDto);
		List<GLCodeDto> accountingGlDtos = null;
		accountingGlDtos = accountingServiceFacade.accountHead(actionForm
				.getStageMainAccount());
		storingSession(request, "AccountHeadGlCodes", accountingGlDtos);

		return mapping.findForward("edit_success");

	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}

	public int nullIntconv(String str) {
		int conv = 0;
		if (str == null) {
			str = "0";
		} else if ((str.trim()).equals("null")) {
			str = "0";
		} else if (str.equals("")) {
			str = "0";
		}
		try {
			conv = Integer.parseInt(str);
		} catch (Exception e) {

		}
		return conv;
	}

	public String getOfficeHierarchy(String office) {

		String returnValue = "1";

		if (office.equals("Head Office")) {
			returnValue = "1";
		}
		if (office.equals("Regional Office")) {
			returnValue = "2";
		}
		if (office.equals("Divisional Office")) {
			returnValue = "3";
		}
		if (office.equals("Area Office")) {
			returnValue = "4";
		}
		if (office.equals("Branch Office")) {
			returnValue = "5";
		}
		if (office.equals("Center")) {
			returnValue = "6";
		}
		if (office.equals("Group")) {
			returnValue = "7";
		}

		return returnValue;
	}

	public String getTranType(String tranxnType) {
		String returntranxn = null;
		if (tranxnType.equals("Cash Receipt")) {
			returntranxn = "CR";
		}
		if (tranxnType.equals("Cash Payment")) {
			returntranxn = "CP";
		}
		if (tranxnType.equals("Bank Receipt")) {
			returntranxn = "BR";
		}
		if (tranxnType.equals("Bank Payment")) {
			returntranxn = "BP";
		}
		if (tranxnType.equals("Journal Voucher")) {
			returntranxn = "JV";
		}
		return returntranxn;
	}

	public String getAccountType(String accountType) {
		String mainAccountType = null;
		if (mainAccountType.equals("cash 1")) {
			mainAccountType = "11101";
		}
		if (mainAccountType.equals("cash 2")) {
			mainAccountType = "11102";
		}
		return mainAccountType;
	}

	public ActionForward loadOffices(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;
		List<OfficeGlobalDto> officeDetailsDtos = null;
		if (actionForm.getStageOfficeHierarchy().equals("")) {
			officeDetailsDtos = null;
		} else if (actionForm.getStageOfficeHierarchy().equals("6")) { // to
																		// recognize
																		// center
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("3"));
		} else if (actionForm.getStageOfficeHierarchy().equals("7")) { // to
																		// recognize
																		// group
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("2"));
		} else {
			officeDetailsDtos = accountingServiceFacade
					.loadOfficesForLevel(Short.valueOf(actionForm
							.getStageOfficeHierarchy()));
		}

		storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
		return mapping.findForward("load_stage_success");
	}

	public ActionForward loadMainAccounts(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;
		List<GLCodeDto> accountingDtos = null;
		if (actionForm.getStageTrxnType().equals("CR")
				|| actionForm.getStageTrxnType().equals("CP")) {
			accountingDtos = accountingServiceFacade.mainAccountForCash();
		} else if (actionForm.getStageTrxnType().equals("BR")
				|| actionForm.getStageTrxnType().equals("BP")) {
			accountingDtos = accountingServiceFacade.mainAccountForBank();
		}

		storingSession(request, "MainAccountGlCodes", accountingDtos);
		return mapping.findForward("load_stage_success");
	}

	public ActionForward loadAccountHeads(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;

		List<GLCodeDto> accountingDtos = null;

		accountingDtos = accountingServiceFacade.accountHead(actionForm
				.getStageMainAccount());
		storingSession(request, "AccountHeadGlCodes", accountingDtos);
		return mapping.findForward("load_stage_success");
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;

		if(actionForm.getStageTrxnType().equals("CP")||actionForm.getStageTrxnType().equals("CR")||actionForm.getStageTrxnType().equals("JV")){
			actionForm.setChequeDate(null);
			actionForm.setStageChequeNo(null);
			actionForm.setStageBankName(null);
			actionForm.setStageankBranch(null);
		}
		storingSession(request, "viewtransactionstageactionform", actionForm);
		return mapping.findForward("preview_success");
	}

	public List<String> getAmountAction(
			ViewStageTransactionActionForm actionForm) {
		List<String> amountActionList = new ArrayList<String>();

		if (actionForm.getStageTrxnType().equals("CR")
				|| actionForm.getStageTrxnType().equals("BR")) {
			amountActionList.add("debit");// for MainAccount amountAction
			amountActionList.add("credit");// for SubAccount amountAction
		} else if (actionForm.getStageTrxnType().equals("CP")
				|| actionForm.getStageTrxnType().equals("BP")) {
			amountActionList.add("credit");// for MainAccount amountAction
			amountActionList.add("debit");// for SubAccount amountAction
		} else if (actionForm.getStageTrxnType().equals("JV")) {
			amountActionList.add("debit");// for MainAccount amountAction
			amountActionList.add("credit");// for SubAccount amountAction
		}

		return amountActionList;
	}

	List<GlDetailBO> getGlDetailBOList(
			ViewStageTransactionActionForm actionForm,
			List<String> amountActionList, int transactionDetailID) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();

		glDetailBOList.add(new GlDetailBO(transactionDetailID, actionForm
				.getStageAccountHead(), new BigDecimal(actionForm
				.getStageAmount()), amountActionList.get(1), actionForm
				.getStageChequeNo(), DateUtils.getDate(actionForm
				.getChequeDate()), actionForm.getStageBankName(), actionForm
				.getStageankBranch()));
		return glDetailBOList;
	}

	public ActionForward submit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession(true);

		ViewStageTransactionActionForm actionForm = (ViewStageTransactionActionForm) form;

		GlMasterBO glMasterBO = new GlMasterBO();
		List<String> amountActionList = getAmountAction(actionForm);
		List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
				amountActionList,
				Integer.parseInt(actionForm.getTransactionDetailID()));
		glMasterBO.setTransactionMasterId(Integer.parseInt(actionForm
				.getStageTransactionNo()));
		glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
				.getStageTrxnDate()));
		glMasterBO.setTransactionType(actionForm.getStageTrxnType());
		glMasterBO.setFromOfficeLevel(new Integer(actionForm
				.getStageOfficeHierarchy()));
		glMasterBO.setFromOfficeId(actionForm.getStageOffice());
		glMasterBO.setToOfficeLevel(new Integer(actionForm
				.getStageOfficeHierarchy()));
		glMasterBO.setToOfficeId(actionForm.getStageOffice());
		glMasterBO.setMainAccount(actionForm.getStageMainAccount());
		glMasterBO.setTransactionAmount(new BigDecimal(actionForm
				.getStageAmount()));
		glMasterBO.setAmountAction(amountActionList.get(0));
		glMasterBO.setTransactionNarration(actionForm.getStageNotes());
		glMasterBO.setGlDetailBOList(glDetailBOList);
		glMasterBO.setStatus("");// default value
		glMasterBO.setTransactionBy(0); // default value
		glMasterBO.setCreatedBy(getUserContext(request).getId());
		glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
		accountingServiceFacade.savingStageAccountingTransactions(glMasterBO);
		return mapping.findForward("submit_success");
	}

	public String changeDateFormat(String date) {
		String[] yymmdds = date.split("-");

		return yymmdds[2] + "/" + yymmdds[1] + "/" + yymmdds[0];
	}

	public String bigdecimalToInt(BigDecimal amount) {
		return new Integer(amount.intValue()).toString();
	}

}