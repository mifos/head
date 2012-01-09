package org.mifos.application.collectionsheet.struts.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.servicefacade.AccountPayment;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDto;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDtoDecorator;
import org.mifos.application.servicefacade.CollectionSheetFormEnteredDataDto;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.application.servicefacade.FormEnteredDataAssembler;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;

public class CollectionSheetPrintChildAction extends BaseAction {
	private final CollectionSheetServiceFacade collectionSheetServiceFacade = ApplicationContextProvider.getBean(CollectionSheetServiceFacade.class);
	
	public ActionForward memberPrintDetails(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
		final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto =(CollectionSheetEntryFormDto)request.getSession().getAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO);//By Prudhvi
		String loanOfficerName="";
		String branchOfficerName="";
		String groupOfficerName="";
		String memberOfficerName="";
		for(OfficeDetailsDto officeDetails:previousCollectionSheetEntryFormDto.getActiveBranchesList()){
			if(officeDetails.getOfficeId().equals(Short.valueOf(bulkEntryActionForm.getOfficeId())))
				branchOfficerName=officeDetails.getOfficeName();
		}
		
		for(PersonnelDto personnelDto:previousCollectionSheetEntryFormDto.getLoanOfficerList()){
			if(personnelDto.getPersonnelId().equals(Short.valueOf(bulkEntryActionForm.getLoanOfficerId())))
				loanOfficerName=personnelDto.getDisplayName();
		}
		
		for(org.mifos.dto.domain.CustomerDto customerDetailsDto:previousCollectionSheetEntryFormDto.getGroupsList()){
			if(customerDetailsDto.getCustomerId().equals(Integer.valueOf(bulkEntryActionForm.getGroupId())))
				groupOfficerName=customerDetailsDto.getDisplayName();
		}
		
		for(org.mifos.dto.domain.CustomerDto customerDetailsDto:previousCollectionSheetEntryFormDto.getMembersList()){
			if(customerDetailsDto.getCustomerId().equals(Integer.valueOf(bulkEntryActionForm.getMemberId())))
				memberOfficerName=customerDetailsDto.getDisplayName();
		}
		
		Date transactionDate = new DateTimeService().getCurrentJavaSqlDate();
		
		final List<AccountPayment> accountPayments = collectionSheetServiceFacade.customerPrintDetails(Integer.parseInt(bulkEntryActionForm.getMemberId()),transactionDate);
		
		List<BigDecimal> totals=getTotalDetails(accountPayments);
		
        storeOnSessionCustomerPrintDetils(accountPayments,request,transactionDate,bulkEntryActionForm,loanOfficerName,branchOfficerName,groupOfficerName,memberOfficerName,totals);
        return mapping.findForward(CollectionSheetEntryConstants.PRINT_SUCCESS);
    }
	
	public void storeOnSessionCustomerPrintDetils(final List<AccountPayment> accountPayments,final HttpServletRequest request,Date transactionDate, BulkEntryActionForm bulkEntryActionForm,String loanOfficerName,String branchOfficerName,String groupOfficerName,String memberOfficerName,List<BigDecimal> totals){
		String memberId=request.getParameter("memberId");
		
		request.getSession().setAttribute("membername",memberOfficerName);
		request.getSession().setAttribute("transactionDate", transactionDate);
		request.getSession().setAttribute("accountPayements", accountPayments);
		request.getSession().setAttribute("customerId", Integer.parseInt(memberId));
		request.getSession().setAttribute("branchId", branchOfficerName);
		request.getSession().setAttribute("groupId", groupOfficerName);
		request.getSession().setAttribute("loanoffId", loanOfficerName);
		
		if(totals!=null && totals.size()<=3){
		request.getSession().setAttribute("principalSum", totals.get(0));
		request.getSession().setAttribute("interestSum", totals.get(1));
		request.getSession().setAttribute("amountTotal", totals.get(2));
		}
		
	}
	public List<BigDecimal> getTotalDetails(List<AccountPayment> accountPayments){
		List<BigDecimal> list=new ArrayList<BigDecimal>();
		BigDecimal principalSum=BigDecimal.ZERO;
		BigDecimal interestSum=BigDecimal.ZERO;
		BigDecimal amountSum=BigDecimal.ZERO;
		for(AccountPayment accountPayment:accountPayments)
			principalSum=principalSum.add(accountPayment.getPrincipal());
				for(AccountPayment accountPayment:accountPayments)
					interestSum=interestSum.add(accountPayment.getInterest());
						for(AccountPayment accountPayment:accountPayments)
							amountSum=amountSum.add(accountPayment.getAmount());
		list.add(principalSum);
		list.add(interestSum);
		list.add(amountSum);
		return list;
		
	}
	
}
