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

package org.mifos.application.collectionsheet.struts.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.struts.uihelpers.BulkEntryDisplayHelper;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;

public class BulkEntryTag extends BodyTagSupport {

    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);

    @SuppressWarnings("unchecked")
    @Override
    public int doStartTag() throws JspException {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter out = pageContext.getOut();

        StringBuilder builder = new StringBuilder();
        CollectionSheetEntryGridDto bulkEntry = null;
        try {
            bulkEntry = (CollectionSheetEntryGridDto) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY,
                    request);
        } catch (PageExpiredException e) {
            logger.error("Page expired getting BulkEntryBO.");
        }
        if (null != bulkEntry) {
            List<ProductDto> loanProducts = bulkEntry.getLoanProducts();
            List<ProductDto> savingsProducts = bulkEntry.getSavingProducts();
            try {
                final List<CustomValueListElement> custAttTypes = (List<CustomValueListElement>) SessionUtils
                        .getAttribute(
                        CollectionSheetEntryConstants.CUSTOMERATTENDANCETYPES, request);

                String method = request.getParameter(CollectionSheetEntryConstants.METHOD);

                generateTagData(bulkEntry, loanProducts, savingsProducts, custAttTypes, method, builder);
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

    private void generateTagData(final CollectionSheetEntryGridDto bulkEntry, final List<ProductDto> loanProducts,
            final List<ProductDto> savingsProducts, final List<CustomValueListElement> custAttTypes,
            final String method, final StringBuilder builder)
            throws SystemException {
        UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USERCONTEXT);
        BulkEntryDisplayHelper bulkEntryDisplayHelper = new BulkEntryDisplayHelper();

        builder.append(bulkEntryDisplayHelper.buildTableHeadings(loanProducts, savingsProducts, userContext
                .getPreferredLocale()));
        CollectionSheetEntryView bulkEntryParentView = bulkEntry.getBulkEntryParent();
        Double[] totals = null;

        boolean centerHierachyExists = ClientRules.getCenterHierarchyExists();
        if (centerHierachyExists) {
            totals = bulkEntryDisplayHelper.buildForCenter(bulkEntryParentView, loanProducts, savingsProducts,
                    custAttTypes, builder, method, userContext);
        } else {
            totals = bulkEntryDisplayHelper.buildForGroup(bulkEntryParentView, loanProducts, savingsProducts,
                    custAttTypes, builder, method, userContext);

        }
        int columnSize = 2 * (loanProducts.size() + savingsProducts.size()) + 7;
        builder.append(bulkEntryDisplayHelper.getEndTable(columnSize));
        builder.append(bulkEntryDisplayHelper.buildTotals(bulkEntryParentView.getCurrency(), totals, loanProducts.size(), savingsProducts.size(), method,
                userContext));
    }

}
