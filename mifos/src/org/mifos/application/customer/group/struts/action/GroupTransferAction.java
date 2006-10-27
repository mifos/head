package org.mifos.application.customer.group.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
import org.mifos.application.customer.group.struts.actionforms.GroupTransferActionForm;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class GroupTransferAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return getGroupBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadBranches(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loadBranches_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewBranchTransfer(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.previewBranchTransfer_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward transferToBranch(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
		OfficeBO officeToTransfer = getOfficeBusinessService().getOffice(
				actionForm.getOfficeIdValue());
		GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		GroupBO group = getGroupBusinessService().getGroup(
				groupInSession.getCustomerId());
		checkVersionMismatch(groupInSession.getVersionNo(),group.getVersionNo());
		group.setVersionNo(groupInSession.getVersionNo());
		group.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(group);
		group.transferToBranch(officeToTransfer);
		groupInSession = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadParents(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterSearchInput centerSearchInput = new CenterSearchInput();
		centerSearchInput.setOfficeId(getUserContext(request).getBranchId());
		centerSearchInput.setGroupInput(GroupConstants.GROUP_TRANSFER_INPUT);
		SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT,
				centerSearchInput, request.getSession());
		return mapping.findForward(ActionForwards.loadParents_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewParentTransfer(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.previewParentTransfer_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward transferToCenter(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
		CenterBO transferToCenter = (CenterBO)getCenterBusinessService().findBySystemId(actionForm.getCenterSystemId());
		transferToCenter.setUserContext(getUserContext(request));
		
		GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		GroupBO group = getGroupBusinessService().getGroup(
				groupInSession.getCustomerId());
		checkVersionMismatch(groupInSession.getVersionNo(),group.getVersionNo());
		group.setUserContext(getUserContext(request));
		group.setVersionNo(groupInSession.getVersionNo());
		setInitialObjectForAuditLogging(group);
		group.transferToCenter(transferToCenter);
		groupInSession = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private OfficeBusinessService getOfficeBusinessService()
			throws ServiceException {
		return (OfficeBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Office);
	}

	private GroupBusinessService getGroupBusinessService(){
		return (GroupBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Group);
	}
	
	private CenterBusinessService getCenterBusinessService(){
		return (CenterBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Center);
	}
}
