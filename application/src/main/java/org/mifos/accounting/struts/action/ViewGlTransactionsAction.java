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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounting.struts.actionform.ViewGlTransactionsActionForm;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.ViewGlTransactionPaginaitonVariablesDto;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewGlTransactionsAction extends BaseAction {

	private static final Logger logger = LoggerFactory
			.getLogger(ViewGlTransactionsAction.class);
	private AccountingServiceFacade accountingServiceFacade = new AccountingServiceFacadeWebTier();

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewGlTransactionsActionForm actionForm = (ViewGlTransactionsActionForm) form;
		java.util.Date trxnDate = DateUtils.getCurrentDateWithoutTimeStamp();
		actionForm.setToTrxnDate(trxnDate);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward submit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		ViewGlTransactionsActionForm actionForm = (ViewGlTransactionsActionForm) form;
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

		List<ViewTransactionsDto> viewTransactionsDtos = accountingServiceFacade
				.getAccountingTransactions(
						DateUtils.getDate(actionForm.getToTrxnDate()),DateUtils.getDate(actionForm.getFromTrxnDate()), iPageNo,
						noOfRecordsPerPage);
		storingSession(request, "ViewTransactionsDtos", viewTransactionsDtos);

		// // this will count total number of rows

		totalNoOfRowsForPagination = accountingServiceFacade
				.getNumberOfTransactions(DateUtils.getDate(actionForm
						.getToTrxnDate()),DateUtils.getDate(actionForm.getFromTrxnDate()));

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

		return mapping.findForward("submit_success");
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
}