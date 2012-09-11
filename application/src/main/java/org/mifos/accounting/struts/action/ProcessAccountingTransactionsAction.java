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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.ProcessAccountingTransactionsActionForm;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessAccountingTransactionsAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(ProcessAccountingTransactionsAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ProcessAccountingTransactionsActionForm actionForm = (ProcessAccountingTransactionsActionForm) form;
		actionForm.setLastProcessDate(accountingServiceFacade
				.getLastProcessDate());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward process(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ProcessAccountingTransactionsActionForm actionForm = (ProcessAccountingTransactionsActionForm) form;
		accountingServiceFacade.processMisPostings(
				DateUtils.getDate(actionForm.getLastProcessDate()),
				DateUtils.getDate(actionForm.getProcessTillDate()),getUserContext(request).getId());
		actionForm.setLastProcessDate(accountingServiceFacade
				.getLastProcessDate());
		storingSession(request, "ProcessAccountingTransactionsActionForm", actionForm);
		return mapping.findForward("submit_success");
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}
}