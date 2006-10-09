package org.mifos.application.customer.struts.action;

import java.sql.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHistoricalDataEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.struts.actionforms.CustHistoricalDataActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustHistoricalDataAction extends BaseAction {

	@Override
	protected BusinessService getService() {
		return getCustomerBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustomerBO customerBO = new CustomerBusinessService()
				.findBySystemId(request
						.getParameter(CustomerConstants.GLOBAL_CUST_NUM));
		customerBO.setUserContext(getUserContext(request));
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request
				.getSession());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request
				.getSession());
		setTypeForGet(customerBO, form);
		CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO
				.getHistoricalData();
		if (customerHistoricalDataEntity == null)
			customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
					customerBO);
		String currentDate = DateHelper.getCurrentDate(getUserContext(request)
				.getPereferedLocale());
		SessionUtils
				.setAttribute(
						CustomerConstants.MFIJOININGDATE,
						(customerHistoricalDataEntity.getMfiJoiningDate() == null ? DateHelper
								.getLocaleDate(getUserContext(request)
										.getPereferedLocale(), currentDate)
								: new Date(customerHistoricalDataEntity
										.getMfiJoiningDate().getTime())),
						request.getSession());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA,
				customerHistoricalDataEntity, request.getSession());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
		CustomerHistoricalDataEntity customerHistoricalDataEntity = (CustomerHistoricalDataEntity) SessionUtils
				.getAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA,
						request.getSession());
		setFormAttributes(request, historicalActionForm,
				customerHistoricalDataEntity);
		historicalActionForm.setMfiJoiningDate(getMfiJoiningDate(
				getUserContext(request).getPereferedLocale(), request
						.getSession()));
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
		CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CustomerBO customerBO = getCustomerBusinessService().getCustomer(customerBOInSession.getCustomerId());
		customerBO.setVersionNo(customerBOInSession.getVersionNo());
		customerBO.setUserContext(getUserContext(request));
		customerBOInSession = null;
		setInitialObjectForAuditLogging(customerBO);
		CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO
				.getHistoricalData();
		Integer oldLoanCycleNo = 0;
		if (customerHistoricalDataEntity == null) {
			customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
					customerBO);
			customerHistoricalDataEntity.setCreatedBy(customerBO
					.getUserContext().getId());
			customerHistoricalDataEntity.setCreatedDate(new java.util.Date());
		} else {
			oldLoanCycleNo = customerHistoricalDataEntity.getLoanCycleNumber();
			customerHistoricalDataEntity.setUpdatedDate(new java.util.Date());
			customerHistoricalDataEntity.setUpdatedBy(customerBO
					.getUserContext().getId());
		}
		setCustomerHistoricalDataEntity(customerBO, historicalActionForm,
				customerHistoricalDataEntity);
		customerBO.updateHistoricalData(customerHistoricalDataEntity,oldLoanCycleNo);
		customerBO.update();
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(getDetailAccountPage(form));
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward getHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CustomerBO customerBO = new CustomerBusinessService()
				.findBySystemId(request
						.getParameter(CustomerConstants.GLOBAL_CUST_NUM));
		customerBO.setUserContext(getUserContext(request));
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);
		setTypeForGet(customerBO, form);
		CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO
				.getHistoricalData();
		if (customerHistoricalDataEntity == null)
			customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
					customerBO);
		String currentDate = DateHelper.getCurrentDate(getUserContext(request)
				.getPereferedLocale());
		SessionUtils
				.setAttribute(
						CustomerConstants.MFIJOININGDATE,
						(customerHistoricalDataEntity.getMfiJoiningDate() == null ? DateHelper
								.getLocaleDate(getUserContext(request)
										.getPereferedLocale(), currentDate)
								: new Date(customerHistoricalDataEntity
										.getMfiJoiningDate().getTime())),
						request);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA,
				customerHistoricalDataEntity, request);
		return mapping.findForward(ActionForwards.getHistoricalData_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
		CustomerHistoricalDataEntity customerHistoricalDataEntity = (CustomerHistoricalDataEntity) SessionUtils
				.getAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA,
						request);
		setFormAttributes(request, historicalActionForm,
				customerHistoricalDataEntity);
		historicalActionForm.setMfiJoiningDate(getMfiJoiningDate(
				getUserContext(request).getPereferedLocale(), request));
		return mapping.findForward(ActionForwards.loadHistoricalData_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.previewHistoricalData_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previousHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping
				.findForward(ActionForwards.previousHistoricalData_success
						.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward updateHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
		CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		CustomerBO customerBO = getCustomerBusinessService().getCustomer(customerBOInSession.getCustomerId());
		customerBO.setVersionNo(customerBOInSession.getVersionNo());
		customerBO.setUserContext(getUserContext(request));
		customerBOInSession = null;
		setInitialObjectForAuditLogging(customerBO);
		CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO
				.getHistoricalData();
		Integer oldLoanCycleNo = 0;
		if (customerHistoricalDataEntity == null) {
			customerHistoricalDataEntity = new CustomerHistoricalDataEntity(
					customerBO);
			customerHistoricalDataEntity.setCreatedBy(customerBO
					.getUserContext().getId());
			customerHistoricalDataEntity.setCreatedDate(new java.util.Date());
		} else {
			oldLoanCycleNo = customerHistoricalDataEntity.getLoanCycleNumber();
			customerHistoricalDataEntity.setUpdatedDate(new java.util.Date());
			customerHistoricalDataEntity.setUpdatedBy(customerBO
					.getUserContext().getId());
		}
		setCustomerHistoricalDataEntity(customerBO, historicalActionForm,
				customerHistoricalDataEntity);
		customerBO.updateHistoricalData(customerHistoricalDataEntity,oldLoanCycleNo);
		customerBO.update();
		return mapping.findForward(ActionForwards.updateHistoricalData_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(getDetailAccountPage(form));
	}

	private void setTypeForGet(CustomerBO customerBO, ActionForm form) {
		CustHistoricalDataActionForm actionForm = (CustHistoricalDataActionForm) form;
		if (customerBO instanceof ClientBO)
			actionForm.setType("Client");
		else if (customerBO instanceof GroupBO)
			actionForm.setType("Group");
	}

	private String getDetailAccountPage(ActionForm form) {
		CustHistoricalDataActionForm actionForm = (CustHistoricalDataActionForm) form;
		String type = actionForm.getType();
		String forward = null;
		if (type.equals("Group"))
			forward = ActionForwards.group_detail_page.toString();
		else if (type.equals("Client"))
			forward = ActionForwards.client_detail_page.toString();
		return forward;
	}

	private void setFormAttributes(HttpServletRequest request,
			CustHistoricalDataActionForm historicalActionForm,
			CustomerHistoricalDataEntity historicalDataEntity) {
		historicalActionForm
				.setInterestPaid(getStringValue(historicalDataEntity
						.getInterestPaid()));
		historicalActionForm.setLoanAmount(getStringValue(historicalDataEntity
				.getLoanAmount()));
		historicalActionForm
				.setLoanCycleNumber(getStringValue(historicalDataEntity
						.getLoanCycleNumber()));
		historicalActionForm
				.setMissedPaymentsCount(getStringValue(historicalDataEntity
						.getMissedPaymentsCount()));
		historicalActionForm.setCommentNotes(historicalDataEntity.getNotes());
		historicalActionForm.setProductName(historicalDataEntity
				.getProductName());
		historicalActionForm
				.setTotalAmountPaid(getStringValue(historicalDataEntity
						.getTotalAmountPaid()));
		historicalActionForm
				.setTotalPaymentsCount(getStringValue(historicalDataEntity
						.getTotalPaymentsCount()));
	}

	private String getMfiJoiningDate(Locale locale, HttpSession session) {
		return DateHelper.getUserLocaleDate(locale, session.getAttribute(
				CustomerConstants.MFIJOININGDATE).toString());
	}

	private String getMfiJoiningDate(Locale locale, HttpServletRequest request)
			throws ApplicationException {
		return DateHelper.getUserLocaleDate(locale, SessionUtils.getAttribute(
				CustomerConstants.MFIJOININGDATE, request).toString());
	}

	private void setCustomerHistoricalDataEntity(CustomerBO customerBO,
			CustHistoricalDataActionForm historicalActionForm,
			CustomerHistoricalDataEntity historicalDataEntity) {
		historicalDataEntity.setInterestPaid(historicalActionForm
				.getInterestPaidValue());
		historicalDataEntity.setLoanAmount(historicalActionForm
				.getLoanAmountValue());
		historicalDataEntity.setLoanCycleNumber(historicalActionForm
				.getLoanCycleNumberValue());
		historicalDataEntity.setMissedPaymentsCount(historicalActionForm
				.getMissedPaymentsCountValue());
		historicalDataEntity.setNotes(historicalActionForm.getCommentNotes());
		historicalDataEntity.setProductName(historicalActionForm
				.getProductName());
		historicalDataEntity.setTotalAmountPaid(historicalActionForm
				.getTotalAmountPaidValue());
		historicalDataEntity.setTotalPaymentsCount(historicalActionForm
				.getTotalPaymentsCountValue());
		historicalDataEntity.setMfiJoiningDate(getDateFromString(
				historicalActionForm.getMfiJoiningDate(), customerBO
						.getUserContext().getPereferedLocale()));
	}
	
	private CustomerBusinessService getCustomerBusinessService() {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}
}
