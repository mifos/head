package org.mifos.framework.components.audit.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TransactionDemarcate;

/**
 * @author krishankg
 *
 */
public class AuditAction extends BaseAction {

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadChangeLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Short entityType = EntityType.getEntityValue(request.getParameter("entityType"));
		Integer entityId = Integer.valueOf(request.getParameter("entityId"));
		request.getSession().setAttribute(AuditConstants.AUDITLOGRECORDS,getAuditBusinessService().getAuditLogRecords(entityType,entityId));
		return mapping.findForward("view"+request.getParameter("entityType")+"ChangeLog");
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward cancelChangeLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("cancel"+request.getParameter("entityType")+"ChangeLog");
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return getAuditBusinessService();
	}

	private AuditBusinessService getAuditBusinessService()
			throws ServiceException {
		return (AuditBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.AuditLog);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	
	
	

}
