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
import org.mifos.accounting.struts.actionform.GeneralLedgerActionForm;
import org.mifos.accounting.struts.actionform.JournalVoucherActionForm;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.application.servicefacade.AccountingServiceFacadeWebTier;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.dto.domain.DynamicOfficeDto;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.OfficeHierarchy;
import org.mifos.dto.domain.OfficesList;
import org.mifos.dto.domain.RolesActivityDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
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
		List<GLCodeDto> accountingDtos = null;
		List<RolesActivityDto> rolesactivitydto=null;
		accountingDtos = accountingServiceFacade.loadDebitAccounts();
		java.util.Date voucherDate = DateUtils.getCurrentDateWithoutTimeStamp();
		actionForm.setVoucherDate(voucherDate);
		rolesactivitydto=accountingServiceFacade.glloadRolesActivity();
		UserContext context = getUserContext(request);
		actionForm.setOfficeLevelId(String.valueOf(context.getOfficeLevelId()));
		List listOfOfficeHierarchyObject = getOfficeLevels(actionForm);
		boolean journalVoucherSave=rolesactivitydto.isEmpty();
		storingSession(request, "listOfOffices", listOfOfficeHierarchyObject);
		storingSession(request, "jvsave", journalVoucherSave);
		storingSession(request, "DebitAccountGlCodes", accountingDtos);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadOffices(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		/*
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

		storingSession(request, "JVOfficesOnHierarchy", officeDetailsDtos);*/

		System.out.println("Office Hierarchy from the form : "+actionForm.getOfficeHierarchy());
		UserContext userContext = getUserContext(request);

		System.out.println("Office Level Id : "+userContext.getOfficeLevelId());

		//List<OfficeGlobalDto> officeDetailsDtos = null;
		List<OfficeGlobalDto> dynamicOfficeDetailsDtos = null;
		List<OfficesList> offices = new ArrayList<OfficesList>();
		// list of offices for a single parent office
		List<DynamicOfficeDto> listOfOffices = null;

		/*listOfOffices = accountingServiceFacade.getOfficeDetails(String.valueOf(userContext.getBranchId()),String.valueOf(userContext.getOfficeLevelId()));
		OfficesList officesList = null;

		for(DynamicOfficeDto officeDto :listOfOffices){
			System.out.println("Office Hierarchy "+actionForm.getOfficeHierarchy());
			System.out.println("compare Hierarchy "+String.valueOf(officeDto.getOfficeLevelId()));
			if(actionForm.getOfficeHierarchy().equals(String.valueOf(officeDto.getOfficeLevelId()))){

				officesList = new OfficesList(officeDto.getOfficeId(), officeDto.displayName, officeDto.officeLevelId, officeDto.globalOfficeNumber);
				offices.add(officesList);
			}

		}*/

		listOfOffices = accountingServiceFacade.getOfficeDetails(String.valueOf(userContext.getBranchId()),String.valueOf(userContext.getOfficeLevelId()));
		OfficesList officesList = null;
		for(DynamicOfficeDto officeDto :listOfOffices){
			System.out.println("Office Hierarchy "+actionForm.getOfficeHierarchy());
			System.out.println("compare Hierarchy "+String.valueOf(officeDto.getOfficeLevelId()));

		if (actionForm.getOfficeHierarchy().equals("")) {
			offices = null;
		// to recognise center and group
		} else if (actionForm.getOfficeHierarchy().equals("6") || actionForm.getOfficeHierarchy().equals("7") ) {

			if(actionForm.getOfficeHierarchy().equals(String.valueOf(officeDto.getOfficeLevelId()))){
				System.out.println(officeDto.globalCustomerNumber);
				officesList = new OfficesList(officeDto.getCustomerId(), officeDto.getDisplayName(), officeDto.getCustomerLevelId(), officeDto.getGlobalCustomerNumber());
				offices.add(officesList);
			}

//			officeDetailsDtos = accountingServiceFacade
//					.loadCustomerForLevel(new Short("3"));
//		} else if (actionForm.getOfficeHierarchy().equals("7")) { // to
//																	// recognize
//																	// group
//			officeDetailsDtos = accountingServiceFacade
//					.loadCustomerForLevel(new Short("2"));

		} else {
			/*officeDetailsDtos = accountingServiceFacade
					.loadOfficesForLevel(Short.valueOf(actionForm
							.getOfficeHierarchy()));*/

				if(actionForm.getOfficeHierarchy().equals(String.valueOf(officeDto.getOfficeLevelId()))){

					officesList = new OfficesList(officeDto.getOfficeId(), officeDto.getDisplayName(), officeDto.getOfficeLevelId(), officeDto.getGlobalOfficeNumber());
					offices.add(officesList);
				}

			}

		}

//		storingSession(request, "OfficesOnHierarchy", officeDetailsDtos);
		storingSession(request, "DynamicOfficesOnHierarchy", offices);

		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadCreditAccount(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {
		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;
		List<GLCodeDto> accountingDtos = null;
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
		monthClosingServiceFacade.validateTransactionDate(DateUtils.getDate(actionForm.getVoucherDate()));
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
		int stage=1;
		insertionSaveAndStage(actionForm,request,stage);
		return mapping.findForward("submit_success");
	}

	public ActionForward saveStageSubmit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request,
			@SuppressWarnings("unused") HttpServletResponse response)
			throws Exception {

		JournalVoucherActionForm actionForm = (JournalVoucherActionForm) form;

		int stage=0;
		//
		insertionSaveAndStage(actionForm,request,stage);
		return mapping.findForward("submit_success");
	}


	public void insertionSaveAndStage(JournalVoucherActionForm actionForm,HttpServletRequest request,int stage)
    {
		List<String> amountActionList = getAmountAction(actionForm);
		List<GlDetailBO> glDetailBOList = getGlDetailBOList(actionForm,
				amountActionList);
		//
		GlMasterBO glMasterBO = new GlMasterBO();
		glMasterBO.setTransactionDate(DateUtils.getDate(actionForm.getVoucherDate()));
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
		if(stage==0)
		{
			accountingServiceFacade.savingStageAccountingTransactions(glMasterBO);
		}else{
		accountingServiceFacade.savingAccountingTransactions(glMasterBO);}
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

public List getOfficeLevels(JournalVoucherActionForm actionForm){

		List listOfOffices = new ArrayList();
		OfficeHierarchy officeHierarchy = null;

		 switch (Integer.parseInt(actionForm.getOfficeLevelId())){
		  case 1:

			    officeHierarchy = new OfficeHierarchy("1","Head Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("2","Regional Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("3","Divisional Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("4","Area Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("5","Branch Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("6","Center");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("7","Group");
			    listOfOffices.add(officeHierarchy);

				System.out.println(listOfOffices.size());
		 break;
		  case 2:

			    officeHierarchy = new OfficeHierarchy("2","Regional Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("3","Divisional Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("4","Area Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("5","Branch Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("6","Center");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("7","Group");
			    listOfOffices.add(officeHierarchy);

			    System.out.println(listOfOffices.size());
		  break;
		  case 3:

			    officeHierarchy = new OfficeHierarchy("3","Divisional Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("4","Area Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("5","Branch Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("6","Center");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("7","Group");
			    listOfOffices.add(officeHierarchy);

			    System.out.println(listOfOffices.size());
		  break;
		  case 4:


			    officeHierarchy = new OfficeHierarchy("4","Area Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("5","Branch Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("6","Center");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("7","Group");
			    listOfOffices.add(officeHierarchy);

			    System.out.println(listOfOffices.size());
		  break;

		  case 5:
			    officeHierarchy = new OfficeHierarchy("5","Branch Office");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("6","Center");
			    listOfOffices.add(officeHierarchy);
			    officeHierarchy = new OfficeHierarchy("7","Group");
			    listOfOffices.add(officeHierarchy);

			    System.out.println(listOfOffices.size());
	      break;


		  }

		return listOfOffices;
	}

}