package org.mifos.application.collectionsheet.struts.action;

import java.sql.Date;
import java.util.List;

import java.util.ListIterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDto;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this class is used to load members for bulkentry.jsp for client label 
 *
 */
public class CollectionSheetPrintAction extends BaseAction {
	private final CollectionSheetServiceFacade collectionSheetServiceFacade = ApplicationContextProvider.getBean(CollectionSheetServiceFacade.class);


	
	@TransactionDemarcate(saveToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        // clean up
        request.getSession().setAttribute(CollectionSheetEntryConstants.BULKENTRYACTIONFORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);

        final UserContext userContext = getUserContext(request);
        final CollectionSheetEntryFormDto collectionSheetForm = collectionSheetServiceFacade
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        // settings for action
        request.setAttribute(CollectionSheetEntryConstants.REFRESH, collectionSheetForm.getReloadFormAutomatically());

        storeOnRequestCollectionSheetEntryFormDto(request, collectionSheetForm);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadLoanOfficers(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        final BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        final Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        final UserContext userContext = getUserContext(request);
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);

        final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadLoanOfficersForBranch(officeId, userContext, previousCollectionSheetEntryFormDto);

        // add reference data for view
        storeOnRequestCollectionSheetEntryFormDto(request, latestCollectionSheetEntryFormDto);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadCustomerList(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        final Short personnelId = Short.valueOf(bulkEntryActionForm.getLoanOfficerId());
        final Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);

        final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadCustomersForBranchAndLoanOfficer(personnelId, officeId, previousCollectionSheetEntryFormDto);

        // add reference data for view
        storeOnRequestCollectionSheetEntryFormDto(request, latestCollectionSheetEntryFormDto);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    /**
     * This method retrieves the last meeting date for the chosen customer. This meeting date is put as the default date
     * for the transaction date in the search criteria
     *
     */
    @TransactionDemarcate(joinToken = true)
    public ActionForward getLastMeetingDateForCustomer(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final BulkEntryActionForm actionForm = (BulkEntryActionForm) form;

		final Integer customerId = Integer.valueOf(actionForm.getCustomerId());
		
		
		
		final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);

		//By Prudhvi:HugoTechnologies        
		final CollectionSheetEntryFormDto groupsCollectionFormDto = collectionSheetServiceFacade.loadGroupsForCustomer(customerId,previousCollectionSheetEntryFormDto);
		//By Sivaji : Hugo Technologies
		final CollectionSheetEntryFormDto collectionSheetEntryFormDto=new CollectionSheetEntryFormDto(groupsCollectionFormDto.getActiveBranchesList(),
				groupsCollectionFormDto.getPaymentTypesList(), groupsCollectionFormDto.getLoanOfficerList(), groupsCollectionFormDto.getCustomerList(),
				groupsCollectionFormDto.getReloadFormAutomatically(), groupsCollectionFormDto.getCenterHierarchyExists(), 
				groupsCollectionFormDto.getBackDatedTransactionAllowed(),new DateTimeService().getCurrentJavaSqlDate(),
				groupsCollectionFormDto.getGroupsList(),groupsCollectionFormDto.getMembersList());
		
		actionForm.setTransactionDate(collectionSheetEntryFormDto.getMeetingDate());
		storeOnRequestCollectionSheetEntryFormDto(request, groupsCollectionFormDto);
		request.getSession().setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO, groupsCollectionFormDto);//By Prudhvi : Hugo Technologies
		return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }
    
	 private void storeOnRequestCollectionSheetEntryFormDto(final HttpServletRequest request,
	            final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto) throws PageExpiredException {

	        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
	                latestCollectionSheetEntryFormDto, request);

	        // support old way of managing reference data for now.
	        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, latestCollectionSheetEntryFormDto
	                .getActiveBranchesList(), request);
	        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.PAYMENT_TYPES_LIST,
	                latestCollectionSheetEntryFormDto.getPaymentTypesList(), request);
	        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, latestCollectionSheetEntryFormDto
	                .getLoanOfficerList(), request);
	        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST,
	                latestCollectionSheetEntryFormDto.getCustomerList(), request);
	        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS,
	                latestCollectionSheetEntryFormDto.getCenterHierarchyExists(), request);
	        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED,
	                latestCollectionSheetEntryFormDto.getBackDatedTransactionAllowed(), request);
	        SessionUtils.setAttribute("LastMeetingDate", latestCollectionSheetEntryFormDto.getMeetingDate(), request);
	      //by Prudhvi : Hugo Technologies
	      		SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.GROUPSLIST,
	      				latestCollectionSheetEntryFormDto.getGroupsList(), request);		
	      		SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.MEMBERSLIST,
	      				latestCollectionSheetEntryFormDto.getMembersList(), request);
	    }
	   private CollectionSheetEntryFormDto retrieveFromRequestCollectionSheetEntryFormDto(final HttpServletRequest request)
	            throws PageExpiredException {
	        return (CollectionSheetEntryFormDto) SessionUtils.getAttribute(
	                CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO, request);
	    }

}
