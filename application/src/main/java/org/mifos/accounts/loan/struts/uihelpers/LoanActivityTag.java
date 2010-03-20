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

package org.mifos.accounts.loan.struts.uihelpers;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.accounts.loan.business.LoanActivityView;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.LabelTagUtils;
import org.mifos.security.util.UserContext;

public class LoanActivityTag extends BodyTagSupport {
    Locale locale = null;

    @Override
    public int doStartTag() throws JspException {
        XmlBuilder xmlBuilder = new XmlBuilder();
        try {
            String currentFlowKey = (String) pageContext.getRequest().getAttribute(Constants.CURRENTFLOWKEY);
            HttpSession session = pageContext.getSession();
            FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);
            Object object = flowManager.getFromFlow(currentFlowKey, LoanConstants.LOAN_ALL_ACTIVITY_VIEW);
            if (null != object) {
                UserContext userContext = (UserContext) pageContext.getSession().getAttribute(
                        Constants.USER_CONTEXT_KEY);
                locale = userContext.getPreferredLocale();
                xmlBuilder.startTag("table", "width", "100%", "border", "0", "cellspacing", "0", "cellpadding", "0");
                xmlBuilder.startTag("tr");
                xmlBuilder.startTag("td", "colspan", "8");
                xmlBuilder.nonBreakingSpace();
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "colspan", "4", "class", "drawtablerowboldnoline");
                xmlBuilder.text(getLabel("loan.running_bal", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.endTag("tr");
                xmlBuilder.startTag("tr", "class", "drawtablerowbold");
                xmlBuilder.startTag("td", "width", "9%", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.date", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "19%", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.activity", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.principal", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(MessageLookup.getInstance().lookupLabel(ConfigurationConstants.INTEREST, userContext));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.fees", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.penalty", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "6%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.total", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "4", "align", "right", "class", "fontnormalbold");
                xmlBuilder.nonBreakingSpace();
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.principal", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(MessageLookup.getInstance().lookupLabel(ConfigurationConstants.INTEREST, userContext));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "8%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.fees", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.startTag("td", "width", "6%", "align", "right", "class", "drawtablerowbold");
                xmlBuilder.text(getLabel("loan.total", locale));
                xmlBuilder.endTag("td");
                xmlBuilder.endTag("tr");

                List<LoanActivityView> loanRecentActivityViewSet = (List<LoanActivityView>) object;
                Iterator<LoanActivityView> it = loanRecentActivityViewSet.iterator();
                while (it.hasNext()) {
                    LoanActivityView loanActivityView = it.next();
                    xmlBuilder.startTag("tr", "valign", "top");
                    xmlBuilder.append(buildLeftHeaderRows(loanActivityView));
                    xmlBuilder.append(buildRightHeaderRows(loanActivityView));
                    xmlBuilder.endTag("tr");
                }
                xmlBuilder.endTag("table");
            }
            pageContext.getOut().write(xmlBuilder.toString());

        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    XmlBuilder buildLeftHeaderRows(LoanActivityView loanRecentActivityView) {
        XmlBuilder xmlBuilder = new XmlBuilder();
        xmlBuilder.startTag("td", "class", "drawtablerow");
        xmlBuilder.text(DateUtils.getUserLocaleDate(locale, loanRecentActivityView.getActionDate().toString()));
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getActivity());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getPrincipal().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getInterest().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getFees().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getPenalty().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getTotal().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "fontnormalbold");
        xmlBuilder.singleTag("br");
        xmlBuilder.endTag("td");
        return xmlBuilder;
    }

    XmlBuilder buildRightHeaderRows(LoanActivityView loanRecentActivityView) {
        XmlBuilder xmlBuilder = new XmlBuilder();
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getRunningBalancePrinciple().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getRunningBalanceInterest().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getRunningBalanceFees().toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "drawtablerow");
        xmlBuilder.text(loanRecentActivityView.getRunningBalancePrinciple().add(
                loanRecentActivityView.getRunningBalanceInterest()).add(loanRecentActivityView.getRunningBalanceFees())
                .toString());
        xmlBuilder.endTag("td");
        xmlBuilder.startTag("td", "align", "right", "class", "fontnormalbold");
        xmlBuilder.singleTag("br");
        xmlBuilder.endTag("td");
        return xmlBuilder;
    }

    private String getLabel(String key, Locale locale) throws JspException {
        return LabelTagUtils.getInstance().getLabel(pageContext, "loanUIResources", locale, key, null);
    }
}
