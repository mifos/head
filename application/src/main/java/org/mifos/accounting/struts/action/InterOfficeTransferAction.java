package org.mifos.accounting.struts.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.MultipleGeneralLedgerActionForm;
import org.mifos.accounting.struts.actionform.InterOfficeTransferActionForm;
import org.mifos.accounting.struts.actionform.JournalVoucherActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterOfficeTransferAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(InterOfficeTransferAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		java.util.Date trxnDate = DateUtils.getCurrentDateWithoutTimeStamp();
		actionForm.setTrxnDate(trxnDate);
		List<GLCodeDto> interOfficeAccountingDtos = null;
		List<GLCodeDto> accountingDtos = null;
		interOfficeAccountingDtos = accountingServiceFacade
				.loadInterOfficeDebitAccounts();
		storingSession(request, "InterOfficeDebitAccountGlCodes",
				interOfficeAccountingDtos);
		storingSession(request, "IOFromOfficeHierarchy", null);
		storingSession(request, "IOToOfficeHierarchy", null);

		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadFromOffices(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		List<OfficeGlobalDto> fromOfficeDetailsDtos = null;
		if (actionForm.getFromOfficeHierarchy().equals("")) {
			fromOfficeDetailsDtos = null;
		} else if (actionForm.getFromOfficeHierarchy().equals("6")) { // to
																		// recognize
																		// center
			fromOfficeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("3"));
		} else if (actionForm.getFromOfficeHierarchy().equals("7")) { // to
																		// recognize
																		// group
			fromOfficeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("2"));
		} else {

			fromOfficeDetailsDtos = accountingServiceFacade
					.loadOfficesForLevel(Short.valueOf(actionForm.getFromOfficeHierarchy()));
		}

		storingSession(request, "IOFromOfficeHierarchy", fromOfficeDetailsDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadToOffices(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		List<OfficeGlobalDto> toOfficeDetailsDtos = null;
		actionForm.getFromOfficeHierarchy();
		actionForm.getFromOffice();

		if (actionForm.getFromOfficeHierarchy().equals(
				actionForm.getToOfficeHierarchy())) {

			if (actionForm.getFromOfficeHierarchy().equals("")) {
				toOfficeDetailsDtos = null;
			} else if (actionForm.getToOfficeHierarchy().equals("6")) { // to
																		// recognize
																		// center
				toOfficeDetailsDtos = accountingServiceFacade
						.loadCustomerForLevel(new Short("3"),actionForm.getFromOffice());
			} else if (actionForm.getToOfficeHierarchy().equals("7")) { // to
																		// recognize
																		// group
				toOfficeDetailsDtos = accountingServiceFacade
						.loadCustomerForLevel(new Short("2"),actionForm.getFromOffice());
			} else {
				toOfficeDetailsDtos = accountingServiceFacade
						.loadOfficesForLevel(Short.valueOf(actionForm
								.getToOfficeHierarchy()),actionForm.getFromOffice());
			}


		} else {
			if (actionForm.getFromOfficeHierarchy().equals("")) {
				toOfficeDetailsDtos = null;
			} else if (actionForm.getToOfficeHierarchy().equals("6")) { // to
																		// recognize
																		// center
				toOfficeDetailsDtos = accountingServiceFacade
						.loadCustomerForLevel(new Short("3"));
			} else if (actionForm.getToOfficeHierarchy().equals("7")) { // to
																		// recognize
																		// group
				toOfficeDetailsDtos = accountingServiceFacade
						.loadCustomerForLevel(new Short("2"));
			} else {
				toOfficeDetailsDtos = accountingServiceFacade
						.loadOfficesForLevel(Short.valueOf(actionForm
								.getToOfficeHierarchy()));
			}
		}

		storingSession(request, "IOToOfficeHierarchy", toOfficeDetailsDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadCreditAccount(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		List<GLCodeDto> accountingDtos = null;
		/*
		 * if( (actionForm.getFromOfficeHierarchy().equals(actionForm.
		 * getToOfficeHierarchy())) &&
		 * (actionForm.getFromOffice().equals(actionForm.getToOffice()))){

		 * accountingDtos =
		 * accountingServiceFacade.loadCreditAccounts(actionForm
		 * .getDebitAccountHead());
		 *
		 * }else{ accountingDtos =
		 * accountingServiceFacade.loadCreditAccounts(actionForm
		 * .getDebitAccountHead()); }
		 */
		accountingDtos = accountingServiceFacade.loadCreditAccounts(actionForm
				.getDebitAccountHead());
		storingSession(request, "CreditAccounts", accountingDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward submit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		int stage = 1;
		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		submitSaveAndStage(actionForm, request, stage);

		return mapping.findForward("submit_success");

	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;
		storingSession(request, "InterOfficeTransferActionForm", actionForm);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());

	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(final ActionMapping mapping,
			final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		String forward = null;
		String methodCalled = request
				.getParameter(SimpleAccountingConstants.METHOD);
		String input = request.getParameter("input");
		if (null != methodCalled) {
			if ("load".equals(input)) {
				forward = SimpleAccountingConstants.LOADSUCCESS;
			} else if ("submit".equals(input)) {
				forward = SimpleAccountingConstants.LOADSUCCESS;
			}
		}
		if (null != forward) {
			return mapping.findForward(forward);
		}
		return null;
	}

	List<GlDetailBO> getGlDetailBOList(
			InterOfficeTransferActionForm actionForm,
			List<String> amountActionList) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
		glDetailBOList
				.add(new GlDetailBO("11301", new BigDecimal(actionForm
						.getAmount()), amountActionList.get(1), null, null,
						null, null,null));
		return glDetailBOList;
	}

	List<GlDetailBO> getInterOfficeGlDetailBOList(
			InterOfficeTransferActionForm actionForm,
			List<String> amountActionList) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();

		glDetailBOList.add(new GlDetailBO(actionForm.getCreditAccountHead(),
				new BigDecimal(actionForm.getAmount()),
				amountActionList.get(1), null, null, null, null,null));
		return glDetailBOList;
	}

	public List<String> getAmountAction(InterOfficeTransferActionForm actionForm) {
		List<String> amountActionList = new ArrayList<String>();

		amountActionList.add("debit");// for MainAccount amountAction
		amountActionList.add("credit");// for SubAccount amountAction

		return amountActionList;
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}

	public ActionForward saveStageSubmit(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		InterOfficeTransferActionForm actionForm = (InterOfficeTransferActionForm) form;

		int stage = 0;
		submitSaveAndStage(actionForm, request, stage);
		return mapping.findForward("submit_success");
	}

	public void submitSaveAndStage(InterOfficeTransferActionForm actionForm,
			HttpServletRequest request, int stage) {

		List<String> amountActionList = getAmountAction(actionForm);
		List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
				amountActionList);

		GlMasterBO glMasterBO = new GlMasterBO();
		glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
				.getTrxnDate()));
		glMasterBO.setTransactionType(actionForm.getTrxnType());
		glMasterBO.setFromOfficeLevel(new Integer(actionForm
				.getFromOfficeHierarchy()));
		glMasterBO.setFromOfficeId(actionForm.getFromOffice());
		glMasterBO.setToOfficeLevel(new Integer(actionForm
				.getToOfficeHierarchy()));
		glMasterBO.setToOfficeId(actionForm.getFromOffice());
		glMasterBO.setMainAccount(actionForm.getDebitAccountHead());
		glMasterBO.setTransactionAmount(new BigDecimal(actionForm.getAmount()));
		glMasterBO.setAmountAction(amountActionList.get(0));
		glMasterBO.setTransactionNarration(actionForm.getNotes());
		glMasterBO.setGlDetailBOList(glDetailBOList);
		glMasterBO.setStatus("");// default value
		glMasterBO.setTransactionBy(0); // default value
		glMasterBO.setCreatedBy(getUserContext(request).getId());
		glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());

		conditionForSaving(glMasterBO, stage);

		List<GlDetailBO> interOfficeGLDetailBOList = getInterOfficeGlDetailBOList(
				actionForm, amountActionList);

		GlMasterBO interOfficeGlMasterBO = new GlMasterBO();
		interOfficeGlMasterBO.setTransactionDate(DateUtils.getDate(actionForm
				.getTrxnDate()));
		interOfficeGlMasterBO.setTransactionType(actionForm.getTrxnType());
		interOfficeGlMasterBO.setFromOfficeLevel(new Integer(actionForm
				.getToOfficeHierarchy()));
		interOfficeGlMasterBO.setFromOfficeId(actionForm.getToOffice());
		interOfficeGlMasterBO.setToOfficeLevel(new Integer(actionForm
				.getFromOfficeHierarchy()));
		interOfficeGlMasterBO.setToOfficeId(actionForm.getToOffice());
		interOfficeGlMasterBO.setMainAccount("11301");
		interOfficeGlMasterBO.setTransactionAmount(new BigDecimal(actionForm
				.getAmount()));
		interOfficeGlMasterBO.setAmountAction(amountActionList.get(0));
		interOfficeGlMasterBO.setTransactionNarration(actionForm.getNotes());
		interOfficeGlMasterBO.setGlDetailBOList(interOfficeGLDetailBOList);
		interOfficeGlMasterBO.setStatus("");// default value
		interOfficeGlMasterBO.setTransactionBy(0); // default value
		interOfficeGlMasterBO.setCreatedBy(getUserContext(request).getId());
		interOfficeGlMasterBO.setCreatedDate(DateUtils
				.getCurrentDateWithoutTimeStamp());

		conditionForSaving(interOfficeGlMasterBO, stage);
	}

	public void conditionForSaving(GlMasterBO glMasterBO, int stage) {

		if (stage == 0) {
			accountingServiceFacade
					.savingStageAccountingTransactions(glMasterBO);
		}
		if (stage == 1) {
			accountingServiceFacade.savingAccountingTransactions(glMasterBO);
		}
	}

}
