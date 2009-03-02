/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.bulkentry.struts.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.CollectionSheetEntryView;
import org.mifos.application.bulkentry.struts.uihelpers.BulkEntryDisplayHelper;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class BulkEntryTag extends BodyTagSupport {

    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);
    
    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter out = pageContext.getOut();

        StringBuilder builder = new StringBuilder();
        BulkEntryBO bulkEntry = null;
        try {
            bulkEntry = (BulkEntryBO) SessionUtils.getAttribute(BulkEntryConstants.BULKENTRY, request);
        } catch (PageExpiredException e) {
            logger.error("Page expired getting BulkEntryBO.");
        }
        if (null != bulkEntry) {
            List<PrdOfferingBO> loanProducts = bulkEntry.getLoanProducts();
            List<PrdOfferingBO> savingsProducts = bulkEntry.getSavingsProducts();
            try {
                List<CustomValueListElement> custAttTypes = (List<CustomValueListElement>) SessionUtils.getAttribute(
                        BulkEntryConstants.CUSTOMERATTENDANCETYPES, request);
                String method = request.getParameter(BulkEntryConstants.METHOD);
                HashMap<Integer, ClientAttendanceDto> clientAttendance = (HashMap<Integer, ClientAttendanceDto>) SessionUtils.getAttribute(BulkEntryConstants.CLIENT_ATTENDANCE, request);
                generateTagData(bulkEntry, loanProducts, savingsProducts, clientAttendance, custAttTypes, method, builder);
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

    private void generateTagData(BulkEntryBO bulkEntry, List<PrdOfferingBO> loanProducts,
            List<PrdOfferingBO> savingsProducts, HashMap<Integer, ClientAttendanceDto> clientAttendance, List<CustomValueListElement> custAttTypes, String method,
            StringBuilder builder) throws ApplicationException, SystemException, JspException {
        UserContext userContext = ((UserContext) pageContext.getSession().getAttribute(Constants.USERCONTEXT));
        BulkEntryDisplayHelper bulkEntryDisplayHelper = new BulkEntryDisplayHelper();
        builder.append(bulkEntryDisplayHelper.buildTableHeadings(loanProducts, savingsProducts, userContext
                .getPreferredLocale()));
        CollectionSheetEntryView bulkEntryParentView = bulkEntry.getBulkEntryParent();
        Double[] totals = null;

        boolean centerHierachyExists = ClientRules.getCenterHierarchyExists();
        if (centerHierachyExists) {
            totals = bulkEntryDisplayHelper.buildForCenter(bulkEntryParentView, loanProducts, savingsProducts, clientAttendance,
                    custAttTypes, builder, method, userContext, bulkEntry.getOffice().getOfficeId());
        } else {
            totals = bulkEntryDisplayHelper.buildForGroup(bulkEntryParentView, loanProducts, savingsProducts, clientAttendance,
                    custAttTypes, builder, method, userContext, bulkEntry.getOffice().getOfficeId());

        }
        int columnSize = (2 * (loanProducts.size() + savingsProducts.size())) + 7;
        builder.append(bulkEntryDisplayHelper.getEndTable(columnSize));
        builder.append(bulkEntryDisplayHelper.buildTotals(totals, loanProducts.size(), savingsProducts.size(), method,
                userContext));
    }

}
