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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.OpenBalanceActionForm;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenBalanceAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenBalanceAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		OpenBalanceActionForm actionForm = (OpenBalanceActionForm) form;
		FinancialYearBO financialYearBO = accountingServiceFacade
				.getFinancialYear();
		actionForm.setFinancialYear(DateUtils.format(financialYearBO
				.getFinancialYearStartDate())
				+ "-"
				+ DateUtils.format(financialYearBO.getFinancialYearEndDate()));

		actionForm.setFinancialYearId(new Integer(financialYearBO
				.getFinancialYearId()).toString());
		List<GLCodeDto> accountingDtos = accountingServiceFacade
				.findTotalGlAccounts();

		storingSession(request, "TotalGlCodes", accountingDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadOffices(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		OpenBalanceActionForm actionForm = (OpenBalanceActionForm) form;
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

		storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward loadOpenBalance(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		OpenBalanceActionForm actionForm = (OpenBalanceActionForm) form;
		GlBalancesBO balancesBO=accountingServiceFacade.loadExistedGlBalancesBO(new Integer(actionForm.getOfficeHierarchy()), actionForm.getOffice(), actionForm.getCoaName());
		if(balancesBO!=null){
			BigDecimal openBalance=balancesBO.getOpeningBalance();
			if(openBalance.compareTo(new BigDecimal(0.0))<0)
				actionForm.setAmountAction("-");
			else
			actionForm.setAmountAction("+");
			
			actionForm.setOpenBalance(openBalance.abs().toString());
		}
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		OpenBalanceActionForm actionForm = (OpenBalanceActionForm) form;
		storingSession(request, "OpenBalanceActionForm", actionForm);
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

		OpenBalanceActionForm actionForm = (OpenBalanceActionForm) form;
		UserContext context = getUserContext(request);
		//
		GlBalancesBO glBalancesBO = new GlBalancesBO();
		String finacialYearId = (String) (actionForm.getFinancialYearId() == "" ? "0"
				: actionForm.getFinancialYearId());
		glBalancesBO.setFinancialYearBO(accountingServiceFacade
				.getFinancialYearBO(new Integer(finacialYearId)));
		glBalancesBO.setCreatedBy(context.getId());
		glBalancesBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
		glBalancesBO
				.setOfficeLevel(new Integer(actionForm.getOfficeHierarchy()));
		glBalancesBO.setOfficeId(actionForm.getOffice());
		glBalancesBO.setGlCodeValue(actionForm.getCoaName());
		glBalancesBO.setOpeningBalance(new BigDecimal(actionForm
				.getAmountAction() + actionForm.getOpenBalance()));
		glBalancesBO.setClosingBalance(new BigDecimal(actionForm
				.getAmountAction() + actionForm.getOpenBalance()));
		glBalancesBO.setTransactionDebitSum(new BigDecimal(0.0));
		glBalancesBO.setTransactionCreditSum(new BigDecimal(0.0));
		accountingServiceFacade.savingOpeningBalances(glBalancesBO);
		return mapping.findForward("submit_success");
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}
}