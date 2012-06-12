/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounting.struts.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.JournalVoucherActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.AccountingDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalVoucherAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(JournalVoucherAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		List<AccountingDto> accountingDtos = null;

		accountingDtos = accountingServiceFacade.loadDebitAccounts();

		java.util.Date voucherDate = DateUtils.getCurrentDateWithoutTimeStamp();
		actionForm.setVoucherDate(voucherDate);
		request.getSession().setAttribute("status", "");
		storingSession(request, "DebitAccountGlCodes", accountingDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadOffices(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		List<OfficeGlobalDto> officeDetailsDtos = null;
		if (actionForm.getOfficeHierarchy().equals("")) {
			officeDetailsDtos = null;
		} else if (actionForm.getOfficeHierarchy().equals("6")) {
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("3"));
		} else if (actionForm.getOfficeHierarchy().equals("7")) {
			officeDetailsDtos = accountingServiceFacade
					.loadCustomerForLevel(new Short("2"));
		} else {
			officeDetailsDtos = accountingServiceFacade
					.loadOfficesForLevel(Short.valueOf(actionForm
							.getOfficeHierarchy()));
		}

		storingSession(request, "JVOfficesOnHierarchy", officeDetailsDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadCreditAccount(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		List<AccountingDto> accountingDtos = null;
		accountingDtos = accountingServiceFacade.loadCreditAccounts(actionForm
				.getDebitAccountHead());
		storingSession(request, "CreditAccounts", accountingDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		storingSession(request, "JournalVoucherActionForm", actionForm);
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

	public ActionForward submit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		List<String> amountActionList = getAmountAction(actionForm);
		List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
				amountActionList);
		//
		GlMasterBO glMasterBO = new GlMasterBO();
		glMasterBO.setTransactionDate(DateUtils.getDate(actionForm
				.getVoucherDate()));
		glMasterBO.setTransactionType(actionForm.getTrxnType());
		glMasterBO.setFromOfficeLevel(new Integer(actionForm
				.getOfficeHierarchy()));
		glMasterBO.setFromOfficeId(actionForm.getOffice());
		glMasterBO
				.setToOfficeLevel(new Integer(actionForm.getOfficeHierarchy()));
		glMasterBO.setToOfficeId(actionForm.getOffice());
		glMasterBO.setMainAccount(actionForm.getDebitAccountHead());
		glMasterBO.setTransactionAmount(new BigDecimal(actionForm.getAmount()));
		glMasterBO.setAmountAction(amountActionList.get(0));
		glMasterBO.setTransactionNarration(actionForm.getVoucherNotes());
		glMasterBO.setGlDetailBOList(glDetailBOList);
		glMasterBO.setStatus("");
		glMasterBO.setTransactionBy(0);
		glMasterBO.setCreatedBy(getUserContext(request).getId());
		glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
		if (accountingServiceFacade.savingAccountingTransactions(glMasterBO))
			storingSession(request, "status", "success");

		return mapping.findForward("submit_success");
	}

	List<GlDetailBO> getGlDetailBOList(JournalVoucherActionForm actionForm,
			List<String> amountActionList) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
		glDetailBOList.add(new GlDetailBO(actionForm.getCreditAccountHead(),
				new BigDecimal(actionForm.getAmount()),
				amountActionList.get(1), null, null, null, null));
		return glDetailBOList;

	}

	public List<String> getAmountAction(JournalVoucherActionForm actionForm) {
		List<String> amountActionList = new ArrayList<String>();

		amountActionList.add("debit");// for MainAccount amountAction
		amountActionList.add("credit");// for SubAccount amountAction

		return amountActionList;
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}

}