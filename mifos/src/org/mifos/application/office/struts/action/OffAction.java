package org.mifos.application.office.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.struts.actionforms.OffActionForm;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class OffAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Office);
	}
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OffActionForm actionForm = (OffActionForm) form;
		loadParents(request,actionForm);
		actionForm.clear();


		SessionUtils.setAttribute(OfficeConstants.OFFICELEVELLIST,
				((OfficeBusinessService) getService())
						.getConfiguredLevels(getUserContext(request)
								.getLocaleId()), request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadParent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loadParents(request,(OffActionForm)form);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loadParents(request,(OffActionForm)form);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loadParents(request,(OffActionForm)form);
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	
	private void loadParents(HttpServletRequest request,OffActionForm form) throws Exception {
		String officeLevel = (String) request.getParameter("officeLevel");
		if (!StringUtils.isNullOrEmpty(officeLevel)) {
			form.setOfficeLevel(officeLevel);
			OfficeLevel Level = OfficeLevel.getOfficeLevel(Short
					.valueOf(officeLevel));
			SessionUtils.setAttribute(OfficeConstants.PARENTS,
					((OfficeBusinessService) getService()).getActiveParents(
							Level, getUserContext(request).getLocaleId()),
					request.getSession());
		}
	}
}
