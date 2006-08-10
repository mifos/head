package org.mifos.framework.struts.action;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.HibernateException;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.IllegalStateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SearchObjectNotCreatedException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SearchObject;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.ValueObjectUtil;

public abstract class BaseAction extends DispatchAction {

	protected abstract BusinessService getService() throws ServiceException;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		TransactionDemarcate annotation = getTransaction(form, request);
		preExecute(form, request, annotation);
		ActionForward forward = super.execute(mapping, form, request, response);
		postExecute(request, annotation);
		return forward;
	}

	protected void preExecute(ActionForm actionForm,
			HttpServletRequest request, TransactionDemarcate annotation)
			throws SystemException, ApplicationException {
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

	protected void preHandleTransaction(HttpServletRequest request,
			TransactionDemarcate annotation) throws PageExpiredException {
		if (null != annotation && annotation.saveToken()) {
			resetToken(request);
			saveToken(request);
		} else if (null != annotation && annotation.validateAndResetToken()) {
			if (!isTokenValid(request)) {
				throw new PageExpiredException(
						ExceptionConstants.PAGEEXPIREDEXCEPTION);
			}
		}

	}

	protected void postHandleTransaction(HttpServletRequest request,
			TransactionDemarcate annotation) throws SystemException,
			ApplicationException {
		if (null != annotation && annotation.validateAndResetToken()) {
			resetToken(request);
		}
	}

	protected void postExecute(HttpServletRequest request,
			TransactionDemarcate annotation) throws ApplicationException,
			SystemException {
		// do cleanup here

		if (startSession()) {
			try {
				HibernateUtil.commitTransaction();
				postHandleTransaction(request, annotation);
			} catch (HibernateException he) {
				HibernateUtil.rollbackTransaction();
				throw new ApplicationException(he);
			} catch (IllegalStateException ise) {
				HibernateUtil.rollbackTransaction();
				throw new ApplicationException(ise);
			}
		} else {
			postHandleTransaction(request, annotation);
		}
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

	private Locale getLocale(UserContext userContext) {
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
}// :~
