/**

 * BulkEntryTag.java    version: 1.0

 

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

package org.mifos.application.bulkentry.struts.tags;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.struts.uihelpers.BulkEntryDisplayHelper;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.config.ClientRules;

public class BulkEntryTag extends BodyTagSupport {

	@Override
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		JspWriter out = pageContext.getOut();

		StringBuilder builder = new StringBuilder();
		BulkEntryBO bulkEntry = null;
		try {
			bulkEntry = (BulkEntryBO) SessionUtils.getAttribute(
					BulkEntryConstants.BULKENTRY, request);
		} catch (PageExpiredException e) {
		}
		if (null != bulkEntry) {
			List<PrdOfferingBO> loanProducts = bulkEntry.getLoanProducts();
			List<PrdOfferingBO> savingsProducts = bulkEntry
					.getSavingsProducts();
			try {
				List<CustomValueListElement> custAttTypes = (List<CustomValueListElement>) SessionUtils
						.getAttribute(
								BulkEntryConstants.CUSTOMERATTENDANCETYPES,
								request);
				String method = request.getParameter(BulkEntryConstants.METHOD);
				generateTagData(bulkEntry, loanProducts, savingsProducts,
						custAttTypes, method, builder);
			} catch (ApplicationException ae) {
				throw new JspException(ae);
			} catch (SystemException se) {
				throw new JspException(se);
			}
		}
		try {
			out.write(builder.toString());
		} catch (IOException ioe) {
			throw new JspException(ioe);
		}
		return SKIP_BODY;
	}

	private void generateTagData(BulkEntryBO bulkEntry,
			List<PrdOfferingBO> loanProducts,
			List<PrdOfferingBO> savingsProducts,
			List<CustomValueListElement> custAttTypes, String method,
			StringBuilder builder) throws ApplicationException,
			SystemException, JspException {
		BulkEntryDisplayHelper bulkEntryDisplayHelper = new BulkEntryDisplayHelper();
		builder.append(bulkEntryDisplayHelper.buildTableHeadings(loanProducts,
				savingsProducts));
		BulkEntryView bulkEntryParentView = bulkEntry.getBulkEntryParent();
		Double[] totals = null;
		UserContext userContext = ((UserContext) pageContext.getSession()
				.getAttribute(Constants.USERCONTEXT));
		boolean centerHierachyExists = ClientRules.getCenterHierarchyExists();
		if (centerHierachyExists) {
			totals = bulkEntryDisplayHelper.buildForCenter(bulkEntryParentView,
					loanProducts, savingsProducts, custAttTypes, builder,
					method, userContext, bulkEntry.getOffice().getOfficeId());
		} else {
			totals = bulkEntryDisplayHelper.buildForGroup(bulkEntryParentView,
					loanProducts, savingsProducts, custAttTypes, builder,
					method, userContext, bulkEntry.getOffice().getOfficeId());

		}
		int columnSize = (2 * (loanProducts.size() + savingsProducts.size())) + 7;
		builder.append(bulkEntryDisplayHelper.getEndTable(columnSize));
		builder.append(bulkEntryDisplayHelper.buildTotals(totals, loanProducts
				.size(), savingsProducts.size(), method, userContext));
	}

}
