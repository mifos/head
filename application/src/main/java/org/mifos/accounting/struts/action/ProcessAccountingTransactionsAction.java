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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.GeneralLedgerActionForm;
import org.mifos.accounting.struts.actionform.ProcessAccountingTransactionsActionForm;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.DynamicOfficeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.OfficeHierarchy;
import org.mifos.dto.domain.OfficesList;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
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
		UserContext context = getUserContext(request);
		actionForm.setOfficeLevelId(String.valueOf(context.getOfficeLevelId()));
		List<OfficesList> offices = new ArrayList<OfficesList>();
		List<DynamicOfficeDto> listOfOffices = null;
		listOfOffices = accountingServiceFacade.getOfficeDetails(
				String.valueOf(context.getBranchId()),
				String.valueOf(context.getOfficeLevelId()));
		OfficesList officesList = null;
		OfficeGlobalDto officeGlobalDto = null;
		for (DynamicOfficeDto officeDto : listOfOffices) {
			
			if (officeDto.getOfficeLevelId() == 5) {
				officesList = new OfficesList(officeDto.getOfficeId(),
						officeDto.displayName, officeDto.officeLevelId,
						officeDto.globalOfficeNumber);
				offices.add(officesList);
			}

		}
		if("1".equals(String.valueOf(getUserContext(request).getOfficeLevelId()))){
			storingSession(request, "officeLevelId", actionForm.getOfficeLevelId());
			storingSession(request, "DynamicOfficesOnHierarchy", offices);
		}else if("5".equals(String.valueOf(getUserContext(request).getOfficeLevelId()))){
			if(offices.size() > 0 && (offices.get(0)!=null)){
				officeGlobalDto = new OfficeGlobalDto(offices.get(0).globalOfficeNumber, offices.get(0).displayName);
			}
			actionForm.setOffice(offices.get(0).globalOfficeNumber);
			storingSession(request, "officeLevelId", actionForm.getOfficeLevelId());
			storingSession(request, "DynamicOfficesOnHierarchy", officeGlobalDto);
			actionForm.setLastProcessDate(accountingServiceFacade
					.getLastProcessUpdatedDate(getUserContext(request)
							.getBranchGlobalNum()));
		}else{
			storingSession(request, "DynamicOfficesOnHierarchy", offices);
		}

		/*
		 * actionForm.setLastProcessDate(accountingServiceFacade
		 * .getLastProcessDate());
		 */


		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadLastUpdatedDate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		ProcessAccountingTransactionsActionForm actionForm = (ProcessAccountingTransactionsActionForm) form;
		actionForm.setLastProcessDate(accountingServiceFacade.getLastProcessUpdatedDate(actionForm.getOffice()));

		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward process(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ProcessAccountingTransactionsActionForm actionForm = (ProcessAccountingTransactionsActionForm) form;
		UserContext userContext = getUserContext(request);

		monthClosingServiceFacade.validateTransactionDate(DateUtils
				.getDate(actionForm.getProcessTillDate()));

		accountingServiceFacade.processMisPostings(DateUtils.getDate(actionForm
				.getLastProcessDate()), DateUtils.getDate(actionForm
				.getProcessTillDate()), getUserContext(request).getId(),
				actionForm.getOffice());
		/*
		 * actionForm.setLastProcessDate(accountingServiceFacade
		 * .getLastProcessDate());
		 */
		actionForm.setLastProcessDate(accountingServiceFacade
				.getLastProcessUpdatedDate(actionForm.getOffice()));
		storingSession(request, "ProcessAccountingTransactionsActionForm",
				actionForm);
		return mapping.findForward("submit_success");
	}

	public void storingSession(HttpServletRequest httpServletRequest, String s,
			Object o) {
		httpServletRequest.getSession().setAttribute(s, o);
	}

}