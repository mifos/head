package org.mifos.application.checklist.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.business.service.CheckListBusinessService;
import org.mifos.application.checklist.struts.actionforms.ChkListActionForm;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ChkListAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return new CheckListBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<CustomerStatusEntity> statesData = null;
		List<String> details = null;
		request.getSession().setAttribute("ChkListActionForm", null);
		List<CheckListMasterView> masterData = ((CheckListBusinessService) getService())
				.getCheckListMasterData(getUserContext(request));
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request);
		SessionUtils.setAttribute(CheckListConstants.STATES, statesData,
				request);
		SessionUtils.setAttribute(CheckListConstants.CHECKLIST_MASTERDATA,
				masterData, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getStates(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<CheckListStatesView> states = null;
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		List<String> details = chkListActionForm.getDetailsList();
		if (chkListActionForm.getIsCustomer()) {
			states = ((CheckListBusinessService) getService())
					.getCustomerStates(getShortValue(chkListActionForm
							.getMasterTypeId()), getUserContext(request)
							.getLocaleId());
		} else {
			states = ((CheckListBusinessService) getService())
					.getAccountStates(getShortValue(chkListActionForm
							.getMasterTypeId()), getUserContext(request)
							.getLocaleId());
		}
		SessionUtils.setAttribute(CheckListConstants.STATES, states, request);
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		List<String> details = chkListActionForm.getDetailsList();
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request);
		isValidCheckListState(
				getShortValue(chkListActionForm.getMasterTypeId()),
				getShortValue(chkListActionForm.getStateId()),
				chkListActionForm.getIsCustomer());

		return mapping.findForward(ActionForwards.preview_success.toString());

	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		if (chkListActionForm.getIsCustomer()) {
			CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(
					CustomerLevel.getLevel(getShortValue(chkListActionForm
							.getMasterTypeId())));
			CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(
					getShortValue(chkListActionForm.getStateId()));
			CustomerCheckListBO customerCheckListBO = new CustomerCheckListBO(
					customerLevelEntity, customerStatusEntity,
					chkListActionForm.getChecklistName(),
					CheckListConstants.STATUS_ACTIVE, chkListActionForm
							.getDetailsList(), getUserContext(request)
							.getLocaleId(), getUserContext(request).getId());
			customerCheckListBO.save();
		} else {
			ProductTypeEntity productTypeEntity = null;
			for (ProductTypeEntity prdTypeEntity : new ProductCategoryBusinessService()
					.getProductTypes()) {
				if (chkListActionForm.getMasterTypeId().equals(
						getStringValue(prdTypeEntity.getProductTypeID()))) {
					productTypeEntity = prdTypeEntity;
					break;
				}
			}
			AccountStateEntity accountStateEntity = new AccountStateEntity(
					AccountState.getStatus(getShortValue(chkListActionForm
							.getStateId())));
			AccountCheckListBO accountCheckListBO = new AccountCheckListBO(
					productTypeEntity, accountStateEntity, chkListActionForm
							.getChecklistName(),
					CheckListConstants.STATUS_ACTIVE, chkListActionForm
							.getDetailsList(), getUserContext(request)
							.getLocaleId(), getUserContext(request).getId());
			accountCheckListBO.save();
		}
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");

		if (method.equalsIgnoreCase(Methods.preview.toString()))
			return mapping.findForward(ActionForwards.load_success.toString());
		if (method.equalsIgnoreCase(Methods.create.toString()))
			return mapping.findForward(ActionForwards.preview_success
					.toString());
		return null;
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancelCreate_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		List<String> details = chkListActionForm.getDetailsList();
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request
				.getSession());
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		List<String> details = chkListActionForm.getDetailsList();
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request
				.getSession());
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		List<String> details = chkListActionForm.getDetailsList();
		SessionUtils.setAttribute(CheckListConstants.DETAILS, details, request
				.getSession());
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	public ActionForward cancelEdit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping
				.findForward(ActionForwards.editcancel_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward loadAllChecklist(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.getSession().setAttribute("ChkListActionForm", null);
		List<CustomerCheckListBO> customerCheckLists = ((CheckListBusinessService) getService())
				.retreiveAllCustomerCheckLists();
		List<AccountCheckListBO> accountCheckLists = ((CheckListBusinessService) getService())
				.retreiveAllAccountCheckLists();
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setAttribute(CheckListConstants.CENTER_CHECKLIST,
				getCustomerCheckLists(customerCheckLists, CustomerLevel.CENTER,
						localeId), request);
		SessionUtils.setAttribute(CheckListConstants.GROUP_CHECKLIST,
				getCustomerCheckLists(customerCheckLists, CustomerLevel.GROUP,
						localeId), request);
		SessionUtils.setAttribute(CheckListConstants.CLIENT_CHECKLIST,
				getCustomerCheckLists(customerCheckLists, CustomerLevel.CLIENT,
						localeId), request);

		SessionUtils.setAttribute(CheckListConstants.LOAN_CHECKLIST,
				getAccountCheckLists(accountCheckLists, ProductType.LOAN,
						localeId), request);
		SessionUtils.setAttribute(CheckListConstants.SAVINGS_CHECKLIST,
				getAccountCheckLists(accountCheckLists, ProductType.SAVINGS,
						localeId), request);
		return mapping.findForward(ActionForwards.loadAllChecklist_success
				.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ChkListActionForm chkListActionForm = (ChkListActionForm) form;
		Short localeId = getUserContext(request).getLocaleId();
		CheckListBO checkList = ((CheckListBusinessService) getService())
				.getCheckList(getShortValue(chkListActionForm.getCheckListId()));
		if (checkList.getCheckListType().equals(
				CheckListType.CUSTOMER_CHECKLIST)) {
			CustomerCheckListBO customerCheckList = (CustomerCheckListBO) checkList;
			customerCheckList.getCustomerStatus().setLocaleId(localeId);
			customerCheckList.getCustomerLevel().setLocaleId(localeId);
			SessionUtils.setAttribute(Constants.BUSINESS_KEY,
					customerCheckList, request);
			SessionUtils.setAttribute(CheckListConstants.TYPE,
					CheckListType.CUSTOMER_CHECKLIST.getValue(), request);
		} else {
			AccountCheckListBO accountCheckList = (AccountCheckListBO) checkList;
			accountCheckList.getAccountStateEntity().setLocaleId(localeId);
			accountCheckList.getProductTypeEntity().setUserContext(
					getUserContext(request));
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountCheckList,
					request);
			SessionUtils.setAttribute(CheckListConstants.TYPE,
					CheckListType.ACCOUNT_CHECKLIST.getValue(), request);
		}
		SessionUtils.setAttribute(CheckListConstants.CREATED_BY_NAME,
				new PersonnelBusinessService().getPersonnel(
						checkList.getCreatedBy()).getDisplayName(), request);

		return mapping.findForward(ActionForwards.get_success.toString());
	}

	private List<CustomerCheckListBO> getCustomerCheckLists(
			List<CustomerCheckListBO> checkLists, CustomerLevel level,
			Short localeId) {
		List<CustomerCheckListBO> customerCheckLists = new ArrayList<CustomerCheckListBO>();
		if (checkLists != null && checkLists.size() > 0) {
			for (CustomerCheckListBO checkList : checkLists) {
				if (checkList.getCustomerLevel().getId().equals(
						level.getValue())) {
					checkList.getCustomerStatus().setLocaleId(localeId);
					customerCheckLists.add(checkList);
				}
			}
		}
		return customerCheckLists;
	}

	private List<AccountCheckListBO> getAccountCheckLists(
			List<AccountCheckListBO> checkLists, ProductType productType,
			Short localeId) {
		List<AccountCheckListBO> accountCheckLists = new ArrayList<AccountCheckListBO>();
		if (checkLists != null && checkLists.size() > 0) {
			for (AccountCheckListBO checkList : checkLists) {
				if (checkList.getProductTypeEntity().getProductTypeID().equals(
						productType.getValue())) {
					checkList.getAccountStateEntity().setLocaleId(localeId);
					accountCheckLists.add(checkList);
				}
			}
		}
		return accountCheckLists;
	}

	private void isValidCheckListState(Short levelId, Short customerState,
			boolean isCustomer) throws Exception {
		((CheckListBusinessService) getService()).isValidCheckListState(
				levelId, customerState, isCustomer);
	}
}
