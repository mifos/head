package org.mifos.application.productdefinition.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.application.productdefinition.struts.actionforms.PrdCategoryActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PrdCategoryAction extends BaseAction {

	@Override
	protected BusinessService getService() {
		return getBusinessService();
	}

	@TransactionDemarcate (saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request, form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
				getProductTypes(userContext), request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward createPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward createPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate (validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		PrdCategoryActionForm productCategoryActionForm = (PrdCategoryActionForm) form;
		ProductCategoryBO productCategoryBO = new ProductCategoryBO(
				userContext, getProductType(
						(List<ProductTypeEntity>) SessionUtils.getAttribute(
								ProductDefinitionConstants.PRODUCTTYPELIST,
								request), Short
								.valueOf(productCategoryActionForm
										.getProductType())),
				productCategoryActionForm.getProductCategoryName(),
				productCategoryActionForm.getProductCategoryDesc());
		productCategoryBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, productCategoryBO, request);
		productCategoryActionForm.setGlobalPrdCategoryNum(productCategoryBO.getGlobalPrdCategoryNum());

		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate (saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request, form);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
				getProductTypes(userContext), request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, getBusinessService()
				.findByGlobalNum(request.getParameter("globalPrdCategoryNum")),
				request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProductCategoryBO productCategoryBO = (ProductCategoryBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		populateForm(form, productCategoryBO);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils
				.setCollectionAttribute(ProductDefinitionConstants.PRDCATEGORYSTATUSLIST,
						getProductCategoryStatusList(userContext), request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward managePreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward managePrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}

	@TransactionDemarcate (validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProductCategoryBO productCategoryBO = (ProductCategoryBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		PrdCategoryActionForm prdCategoryActionForm = (PrdCategoryActionForm) form;
		SessionUtils.getAttribute(
				ProductDefinitionConstants.PRDCATEGORYSTATUSLIST, request);
		productCategoryBO.updateProductCategory(prdCategoryActionForm
				.getProductCategoryName(), prdCategoryActionForm
				.getProductCategoryDesc(), getProductCategoryStatus(
				(List<PrdCategoryStatusEntity>) SessionUtils.getAttribute(
						ProductDefinitionConstants.PRDCATEGORYSTATUSLIST,
						request), Short
						.valueOf(prdCategoryActionForm
								.getProductCategoryStatus())));
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate (saveToken = true)
	public ActionForward getAllCategories(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.PRODUCTCATEGORYLIST,
				getAllCategories(userContext), request);
		return mapping.findForward(ActionForwards.search_success.toString());
	}

	@TransactionDemarcate (joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(ProductDefinitionConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.createPreview.toString()))
				forward = ActionForwards.preview_failure.toString();
			else if (method.equals(Methods.managePreview.toString()))
				forward = ActionForwards.managepreview_failure.toString();
			else if (method.equals(Methods.create.toString()))
				forward = ActionForwards.create_failure.toString();
			else if (method.equals(Methods.update.toString()))
				forward = ActionForwards.update_failure.toString();
		}
		return mapping.findForward(forward);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	private ProductCategoryBusinessService getBusinessService() {
		return new ProductCategoryBusinessService();
	}

	private List<ProductTypeEntity> getProductTypes(UserContext userContext)
			throws Exception {
		List<ProductTypeEntity> productCategoryList = getBusinessService()
				.getProductTypes();
		for (ProductTypeEntity productTypeEntity : productCategoryList)
			productTypeEntity.setUserContext(userContext);
		return productCategoryList;
	}

	private List<PrdCategoryStatusEntity> getProductCategoryStatusList(
			UserContext userContext) throws Exception {
		List<PrdCategoryStatusEntity> productCategoryStatusList = getBusinessService()
				.getProductCategoryStatusList();
		for (PrdCategoryStatusEntity prdCategoryStatusEntity : productCategoryStatusList)
			prdCategoryStatusEntity.setLocaleId(userContext.getLocaleId());
		return productCategoryStatusList;
	}

	private ProductTypeEntity getProductType(
			List<ProductTypeEntity> productCategoryList, Short prdTypeId)
			throws Exception {
		for (ProductTypeEntity productTypeEntity : productCategoryList)
			if (productTypeEntity.getProductTypeID().equals(prdTypeId))
				return productTypeEntity;
		return null;
	}

	private List<ProductCategoryBO> getAllCategories(UserContext userContext)
			throws Exception {
		List<ProductCategoryBO> productCategoryList = getBusinessService()
				.getAllCategories();
		if (productCategoryList != null) {
			for (ProductCategoryBO productCategoryBO : productCategoryList) {
				productCategoryBO.getPrdCategoryStatus().setLocaleId(
						userContext.getLocaleId());
				productCategoryBO.getProductType().setUserContext(userContext);
			}
		}
		return productCategoryList;
	}

	private PrdCategoryStatusEntity getProductCategoryStatus(
			List<PrdCategoryStatusEntity> prdCategoryStatusList, Short statusId) {
		for (PrdCategoryStatusEntity prdCategoryStatusEntity : prdCategoryStatusList)
			if (prdCategoryStatusEntity.getId().equals(statusId))
				return prdCategoryStatusEntity;
		return null;
	}

	private void doCleanUp(HttpServletRequest request, ActionForm form)
	throws Exception {
		PrdCategoryActionForm prdCategoryActionForm = (PrdCategoryActionForm) form;
		prdCategoryActionForm.setProductType(null);
		prdCategoryActionForm.setProductCategoryName(null);
		prdCategoryActionForm.setProductCategoryDesc(null);
		prdCategoryActionForm.setProductCategoryStatus(null);
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
	}

	private void populateForm(ActionForm form,
			ProductCategoryBO productCategoryBO) {
		PrdCategoryActionForm prdCategoryActionForm = (PrdCategoryActionForm) form;
		prdCategoryActionForm.setProductType(productCategoryBO.getProductType()
				.getProductTypeID().toString());
		prdCategoryActionForm.setProductCategoryName(productCategoryBO
				.getProductCategoryName());
		prdCategoryActionForm.setProductCategoryDesc(productCategoryBO
				.getProductCategoryDesc());
		prdCategoryActionForm.setProductCategoryStatus(productCategoryBO
				.getPrdCategoryStatus().getId().toString());
	}

}
