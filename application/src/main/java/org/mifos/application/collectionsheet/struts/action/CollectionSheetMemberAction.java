package org.mifos.application.collectionsheet.struts.action;

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
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this class is used to load members for bulkentry.jsp for client label 
 *
 */
public class CollectionSheetMemberAction extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(CollectionSheetMemberAction.class);

	private final CollectionSheetServiceFacade collectionSheetServiceFacade = ApplicationContextProvider.getBean(CollectionSheetServiceFacade.class);

	public CollectionSheetMemberAction() {
	}

	@Override
	protected BusinessService getService() {
		return new DummyBusinessService();
	}

	@Override
	protected boolean startSession() {
		return false;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(final String method) {
		return method.equals(CollectionSheetEntryConstants.CREATEMETHOD);
	}

	/**
	 * This method is called before the load page for center is called It sets this information in session and
	 * context.This should be removed after center was successfully created.
	 */
	@TransactionDemarcate(saveToken = true)	
	public ActionForward loadMembers(final ActionMapping mapping, final ActionForm form,final HttpServletRequest request, final HttpServletResponse response) throws Exception {

			final BulkEntryActionForm actionForm = (BulkEntryActionForm) form;
			final Integer groupId = Integer.valueOf(actionForm.getGroupId()); 
			final Short personnelId = Short.valueOf(actionForm.getLoanOfficerId());
			final Short officeId = Short.valueOf(actionForm.getOfficeId());

			final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto =(CollectionSheetEntryFormDto)request.getSession().getAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO);//By Prudhvi

			final CollectionSheetEntryFormDto membersCollectionFormDto = collectionSheetServiceFacade.loadMembersByGroup(groupId,previousCollectionSheetEntryFormDto);
				storeMembersListOnRequestCollectionSheetEntryFormDto(request, membersCollectionFormDto);				
				return mapping.findForward(CollectionSheetEntryConstants.LOAD_MEMBER_SUCCESS);
	}
	
	
	/**
	 * By Hugo Technolgies
	 * @param request
	 * @param latestCollectionSheetEntryFormDto
	 * @throws PageExpiredException
	 */
	private void storeMembersListOnRequestCollectionSheetEntryFormDto(final HttpServletRequest request,
			final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto) throws PageExpiredException {

	
		//By Prudhvi
		request.getSession().setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO, latestCollectionSheetEntryFormDto);
		SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.MEMBERSLIST,latestCollectionSheetEntryFormDto.getMembersList(), request);
				
	  
		
	}
	
	private void getErrorString(final StringBuilder builder, final List<String> accountNums, final String message) {
		if (accountNums.size() != 0) {
			ListIterator<String> iter = accountNums.listIterator();
			builder.append("<br>");
			builder.append(message + "-    ");
			while (iter.hasNext()) {
				builder.append(iter.next());
				if (iter.hasNext()) {
					builder.append(", ");
				}
			}
		}
	}

	/**
	 * used by JSP functions in view.
	 */
	protected Locale getUserLocale(final HttpServletRequest request) {
		Locale locale = null;
		UserContext userContext = getUserContext(request);
		if (null != userContext) {
			locale = userContext.getCurrentLocale();
		}
		return locale;
	}

	private class DummyBusinessService implements BusinessService {

		@Override
		public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
			return null;
		}

	}

}
