/**

 * FeesAction.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.fees.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.fees.struts.actionforms.FeesActionForm;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;

public class FeesAction extends MifosBaseAction {

	public FeesAction() {
		super();
	}

	@Override
	protected String getPath() {
		return "Fees";
	}

	public ActionForward customLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.getSession().setAttribute("FeesActionForm", null);
		return mapping.findForward(FeeConstants.CREATEFEES);
	}

	public ActionForward customGet(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults("feesId", request.getParameter("feeIdTemp"));
		return mapping.findForward(FeeConstants.FEEDETAILS);
	}

	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String input = request.getParameter("input");
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Fees fees = (Fees) context.getValueObject();
		FeesActionForm feesActionForm = (FeesActionForm) form;

		if (feesActionForm.getRateFlatFalg().equals(
				RateAmountFlag.AMOUNT.getValue().toString())) {
			Money amount = new Money(Configuration.getInstance()
					.getSystemConfig().getCurrency(), fees.getRateOrAmount());
			feesActionForm.setAmount(String.valueOf(amount
					.getAmountDoubleValue()));
			fees.setRateOrAmount(amount.getAmountDoubleValue());
		}

		if (input.equalsIgnoreCase(FeeConstants.EDITFEEDETAILS)) {
			return mapping.findForward(FeeConstants.PREVIEWFEEDETAILS);
		} else {
			return mapping.findForward(FeeConstants.CREATEFEESPREVIEW);
		}
	}

	public ActionForward customCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(FeeConstants.CREATEFEESCONFIRMATION);

	}

	public ActionForward customPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String input = request.getParameter("input");
		if (input.equalsIgnoreCase(FeeConstants.PREVIEWFEEDETAILS)) {
			return mapping.findForward(FeeConstants.EDITFEEDETAILS);
		} else {
			return mapping.findForward(FeeConstants.CREATEFEES);
		}

	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(FeeConstants.ADMIN);
	}

	public ActionForward customManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(FeeConstants.EDITFEEDETAILS);

	}

	public ActionForward customSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(FeeConstants.VIEWEDITFEES);

	}

	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String input = request.getParameter("input");
		if (input.equalsIgnoreCase(FeeConstants.EDITFEEDETAILS)) {
			return mapping.findForward(FeeConstants.EDITFEEDETAILS);
		} else {
			return mapping.findForward(FeeConstants.CREATEFEES);
		}
	}

}
