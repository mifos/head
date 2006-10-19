package org.mifos.application.productdefinition.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.ProductCategoryBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.productdefinition.struts.actionforms.PrdConfActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PrdConfAction extends BaseAction {

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() {
		return new LoanPrdBusinessService();
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionUtils.setAttribute(ProductDefinitionConstants.LATENESS_DAYS,
				((LoanPrdBusinessService) getService())
						.retrieveLatenessForPrd(), request);
		SessionUtils
				.setAttribute(ProductDefinitionConstants.DORMANCY_DAYS,
						new SavingsPrdBusinessService().retrieveDormancyDays(),
						request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PrdConfActionForm prdConfActionForm = (PrdConfActionForm) form;
		List<ProductTypeEntity> productTypes = new ProductCategoryBusinessService()
				.getProductTypes();
		for (ProductTypeEntity productType : productTypes) {
			if (productType.getProductTypeID().equals(
					ProductType.LOAN.getValue()))
				productType.update(getShortValue(prdConfActionForm
						.getLatenessDays()));
			if (productType.getProductTypeID().equals(
					ProductType.SAVINGS.getValue()))
				productType.update(getShortValue(prdConfActionForm
						.getDormancyDays()));
		}
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String method = (String) request.getAttribute("methodCalled");
		if (method.equalsIgnoreCase(Methods.update.toString()))
			return mapping
					.findForward(ActionForwards.update_failure.toString());
		return null;
	}
}
