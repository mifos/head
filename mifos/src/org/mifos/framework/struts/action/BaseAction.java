package org.mifos.framework.struts.action;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.HibernateException;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SearchObjectNotCreatedException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SearchObject;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.ValueObjectUtil;

public abstract class BaseAction extends DispatchAction {

	protected abstract BusinessService getService() throws ServiceException;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(MifosTask.isCronJobRunning()){
			return logout(mapping, request);
		}
		TransactionDemarcate annotation = getTransaction(form, request);
		preExecute(form, request, annotation);
		ActionForward forward = super.execute(mapping, form, request, response);
		postExecute(request, annotation, isCloseSessionAnnotationPresent(form, request));
		return forward;
	}

	protected void preExecute(ActionForm actionForm,
			HttpServletRequest request, TransactionDemarcate annotation)
			throws SystemException, ApplicationException {
		if (null != request.getParameter(Constants.CURRENTFLOWKEY)) {
			request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));
		}

		preHandleTransaction(request, annotation);
		UserContext userContext = (UserContext) request.getSession()
				.getAttribute(Constants.USER_CONTEXT_KEY);
		Locale locale = getLocale(userContext);
		BusinessObject object = getBusinessObjectFromSession(request);
		if (!skipActionFormToBusinessObjectConversion((String) request
				.getParameter("method")))
			ValueObjectUtil.populateBusinessObject(actionForm, object, locale);
	}

	protected TransactionDemarcate getTransaction(ActionForm actionForm,
			HttpServletRequest request) {
		TransactionDemarcate annotation = null;
		try {
			String methodName = request
					.getParameter(MethodNameConstants.METHOD);
			Method methodToExecute = this.clazz
					.getMethod(methodName, new Class[] { ActionMapping.class,
							ActionForm.class, HttpServletRequest.class,
							HttpServletResponse.class });
			annotation = methodToExecute
					.getAnnotation(TransactionDemarcate.class);
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		return annotation;
	}

	protected boolean isCloseSessionAnnotationPresent(ActionForm actionForm,
			HttpServletRequest request) {
		boolean isAnnotationPresent = false;
		try {
			String methodName = request
					.getParameter(MethodNameConstants.METHOD);
			Method methodToExecute = this.clazz
					.getMethod(methodName, new Class[] { ActionMapping.class,
							ActionForm.class, HttpServletRequest.class,
							HttpServletResponse.class });
			isAnnotationPresent = methodToExecute.isAnnotationPresent(CloseSession.class);
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		return isAnnotationPresent;
	}

	protected void preHandleTransaction(HttpServletRequest request,
			TransactionDemarcate annotation) throws PageExpiredException {
		if (null != annotation && annotation.saveToken()) {
			createToken(request);
		} else if ((null != annotation && annotation.validateAndResetToken())
				|| (null != annotation && annotation.joinToken())) {
			joinToken(request);
		}
		else if (null != annotation && annotation.conditionToken()) {
			String flowKey = (String) request
			.getAttribute(Constants.CURRENTFLOWKEY);
			if ( flowKey==null)createToken(request);
			else joinToken(request);
			
		}
	}

	private void createToken(HttpServletRequest request) throws PageExpiredException{
		String flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = (FlowManager) request.getSession()
				.getAttribute(Constants.FLOWMANAGER);
		if(flowManager == null) {
			flowManager = new FlowManager();
			request.getSession(false).setAttribute(Constants.FLOWMANAGER,flowManager);
		}
		flowManager.addFLow(flowKey, new Flow());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

	}
	private void joinToken(HttpServletRequest request)
			throws PageExpiredException {
		String flowKey = (String) request
				.getAttribute(Constants.CURRENTFLOWKEY);
		FlowManager flowManager = (FlowManager) request.getSession()
				.getAttribute(Constants.FLOWMANAGER);
		if (flowKey == null || !flowManager.isFlowValid(flowKey)) {
			throw new PageExpiredException(
					ExceptionConstants.PAGEEXPIREDEXCEPTION);
		}

	}
	protected void postHandleTransaction(HttpServletRequest request,
			TransactionDemarcate annotation) throws SystemException,
			ApplicationException {
		if (null != annotation && annotation.validateAndResetToken()) {
			FlowManager flowManager = (FlowManager) request.getSession()
					.getAttribute(Constants.FLOWMANAGER);
			flowManager.removeFlow((String) request
					.getAttribute(Constants.CURRENTFLOWKEY));
			request.setAttribute(Constants.CURRENTFLOWKEY, null);
		}
	}

	protected void postExecute(HttpServletRequest request,
			TransactionDemarcate annotation, boolean closeSession) throws ApplicationException,
			SystemException {
		// do cleanup here
		if (startSession()) {
			try {
				HibernateUtil.commitTransaction();
				postHandleTransaction(request, annotation);
			} catch (HibernateException he) {
				HibernateUtil.rollbackTransaction();
				throw new ApplicationException(he);
			} 
		} else {
			postHandleTransaction(request, annotation);
		}

		if(closeSession)
			HibernateUtil.closeSession();
	}

	protected boolean isNewBizRequired(HttpServletRequest request)
			throws ServiceException {
		if (request.getSession().getAttribute(Constants.BUSINESS_KEY) != null) {
			if (getService().getBusinessObject(null) != null
					&& !(request.getSession().getAttribute(
							Constants.BUSINESS_KEY).getClass().getName()
							.equalsIgnoreCase(getService().getBusinessObject(
									null).getClass().getName()))) {
				return true;
			}
			return false;
		}
		return true;
	}

	private BusinessObject getBusinessObjectFromSession(
			HttpServletRequest request) throws ServiceException {
		BusinessObject object = null;
		if (isNewBizRequired(request)) {
			UserContext userContext = (UserContext) request.getSession()
					.getAttribute("UserContext");
			object = getService().getBusinessObject(userContext);
			request.getSession().setAttribute(Constants.BUSINESS_KEY, object);
		} else
			object = (BusinessObject) request.getSession().getAttribute(
					Constants.BUSINESS_KEY);
		return object;
	}

	protected Locale getLocale(UserContext userContext) {
		Locale locale = null;
		if (userContext != null)
			locale = userContext.getPereferedLocale();
		else
			locale = Configuration.getInstance().getSystemConfig()
					.getMFILocale();
		return locale;
	}

	protected boolean startSession() {
		return true;
	}

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return false;
	}

	protected SearchObject formSearchObject(ActionForm form,
			HttpServletRequest request) throws SearchObjectNotCreatedException {
		return ValueObjectUtil.getSearchObject(form);
	}

	protected UserContext getUserContext(HttpServletRequest request) {
		return (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
	}

	protected List<MasterDataEntity> getMasterEntities(Class clazz,
			Short localeId) throws ServiceException, PersistenceException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		return masterDataService.retrieveMasterEntities(clazz, localeId);
	}

	protected MasterDataEntity getMasterEntities(Short entityId,Class clazz,
			Short localeId) throws ServiceException, PersistenceException {
		return new MasterPersistence().retrieveMasterEntity(entityId,clazz, localeId);
	}

	protected Short getShortValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Short.valueOf(str) : null;
	}

	protected Integer getIntegerValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Integer.valueOf(str) : null;
	}

	protected Double getDoubleValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? Double.valueOf(str) : null;
	}

	protected Money getMoney(String str) {
		return (StringUtils.isNullAndEmptySafe(str) && !str.trim().equals(".")) ? new Money(
				str)
				: new Money();
	}

	protected String getStringValue(Double value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(Integer value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(Short value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(Money value) {
		return value != null ? String.valueOf(value) : null;
	}

	protected String getStringValue(boolean value) {
		if (value)
			return "1";
		return "0";

	}

	protected Date getDateFromString(String strDate, Locale locale) {
		Date date = null;
		if (StringUtils.isNullAndEmptySafe(strDate))
			date = new Date(DateHelper.getLocaleDate(locale, strDate).getTime());
		return date;
	}
	
	protected void setInitialObjectForAuditLogging(Object object){
		HibernateUtil.getSessionTL();
		HibernateUtil.getInterceptor().createInitialValueMap(object);
	}
	
	private ActionForward logout(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		request.getSession(false).invalidate();
		ActionErrors error = new ActionErrors();
		error.add(LoginConstants.CRON_JOB_RUNNING,new ActionMessage(LoginConstants.CRON_JOB_RUNNING));
		request.setAttribute(Globals.ERROR_KEY, error);
		return mapping.findForward(ActionForwards.load_main_page.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward loadChangeLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Short entityType = EntityType.getEntityValue(request.getParameter(AuditConstants.ENTITY_TYPE).toUpperCase());
		Integer entityId = Integer.valueOf(request.getParameter(AuditConstants.ENTITY_ID));
		AuditBusinessService auditBusinessService = (AuditBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.AuditLog);
		request.getSession().setAttribute(AuditConstants.AUDITLOGRECORDS,auditBusinessService.getAuditLogRecords(entityType,entityId));
		return mapping.findForward(AuditConstants.VIEW+request.getParameter(AuditConstants.ENTITY_TYPE)+AuditConstants.CHANGE_LOG);
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelChangeLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(AuditConstants.CANCEL+request.getParameter(AuditConstants.ENTITY_TYPE)+AuditConstants.CHANGE_LOG);
	}

}
