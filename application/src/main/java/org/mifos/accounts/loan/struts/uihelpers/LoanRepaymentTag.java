/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.AccountingRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ConversionUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.LabelTagUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class LoanRepaymentTag extends BodyTagSupport {
    Locale locale = null;

    private String memberGlobalNum;

    public String getMemberGlobalNum() {
        return memberGlobalNum;
    }

    public void setMemberGlobalNum(String memberGlobalNum) {
        this.memberGlobalNum = memberGlobalNum;
    }

    @Override
    public int doStartTag() throws JspException {
        boolean twoTables = false;
        XmlBuilder html = new XmlBuilder();
        LoanBO loanBO = null;

        try {
            String currentFlowKey = (String) pageContext.getRequest().getAttribute(Constants.CURRENTFLOWKEY);
            HttpSession session = pageContext.getSession();
            FlowManager flowManager = (FlowManager) session.getAttribute(Constants.FLOWMANAGER);

            loanBO = (LoanBO) flowManager.getFromFlow(currentFlowKey, Constants.BUSINESS_KEY);
            if (StringUtils.isNotBlank(memberGlobalNum)) {
                loanBO = loanBO.findMemberByGlobalNum(memberGlobalNum);
            }

            Date viewDate = getViewDate(currentFlowKey, flowManager);
            Money totalPrincipal = new Money(loanBO.getCurrency(), "0");
            Money totalInterest = new Money(loanBO.getCurrency(), "0");
            Money totalFees = new Money(loanBO.getCurrency(), "0");
            Money totalPenalties = new Money(loanBO.getCurrency(), "0");

            /*
             * LoanBO loanBO = (LoanBO) pageContext.getRequest().getAttribute( Constants.BUSINESS_KEY);
             */
            List<AccountActionDateEntity> list = new ArrayList<AccountActionDateEntity>();
            list.addAll(loanBO.getAccountActionDates());
            UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY);
            locale = userContext.getPreferredLocale();
            if (list.size() != 0) {
                // topmost table
                html.startTag("table", "width", "100%", "border", "0", "cellspacing", "0", "cellpadding", "0");
                // Left side header
                XmlBuilder htmlHeader1 = new XmlBuilder();
                htmlHeader1.startTag("tr");
                htmlHeader1.startTag("td", "width", "6%", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.no", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "18%", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.due_date", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "18%", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.date_paid", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "12%", "align", "right", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.principal", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "12%", "align", "right", "class", "drawtablerowbold");
                htmlHeader1.text(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.INTEREST));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "10%", "align", "right", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.fees", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "12%", "align", "right", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.penalty", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.startTag("td", "width", "12%", "align", "right", "class", "drawtablerowbold");
                htmlHeader1.text(getLabel("loan.total", locale));
                htmlHeader1.endTag("td");
                htmlHeader1.endTag("tr");

                for (AccountActionDateEntity acctDate : list) {
                    LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) acctDate;
                    totalPrincipal = totalPrincipal.add(loanScheduleEntity.getPrincipal());
                    totalInterest = totalInterest.add(loanScheduleEntity.getEffectiveInterest());
                    totalFees = totalFees.add(loanScheduleEntity.getTotalScheduledFeeAmountWithMiscFee());
                    totalPenalties = totalPenalties.add(loanScheduleEntity.getTotalPenalty());
                }

                // check if at least the first installment is paid
                LoanScheduleEntity firstInstallment = (LoanScheduleEntity) list.get(0);
                XmlBuilder html1 = new XmlBuilder();
                XmlBuilder html2 = new XmlBuilder();
                XmlBuilder htmlHeader2 = new XmlBuilder();
                if (!firstInstallment.getTotalDueWithFees().equals(firstInstallment.getTotalScheduleAmountWithFees())) {
                    twoTables = true;
                    // installments paid and running balance table is
                    // required
                    htmlHeader2.startTag("tr");
                    htmlHeader2.startTag("td", "width", "20%", "align", "right", "class", "drawtablerowbold");
                    htmlHeader2.text(getLabel("loan.principal", locale));
                    htmlHeader2.endTag("td");
                    htmlHeader2.startTag("td", "width", "20%", "align", "right", "class", "drawtablerowbold");
                    htmlHeader2.text(ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.INTEREST));
                    htmlHeader2.endTag("td");
                    htmlHeader2.startTag("td", "width", "20%", "align", "right", "class", "drawtablerowbold");
                    htmlHeader2.text(getLabel("loan.fees", locale));
                    htmlHeader2.endTag("td");
                    htmlHeader2.startTag("td", "width", "20%", "align", "right", "class", "drawtablerowbold");
                    htmlHeader2.text(getLabel("loan.penalty", locale));
                    htmlHeader2.endTag("td");
                    htmlHeader2.startTag("td", "width", "20%", "align", "right", "class", "drawtablerowbold");
                    htmlHeader2.text(getLabel("loan.total", locale));
                    htmlHeader2.endTag("td");
                    htmlHeader2.endTag("tr");

                }

                html1.startTag("tr");
                html1.startTag("td", "colspan", "8", "class", "drawtablerowbold");
                html1.nonBreakingSpace();
                html1.endTag("td");
                html1.endTag("tr");
                html2.startTag("tr");
                html2.startTag("td", "colspan", "5", "class", "drawtablerowbold");
                html2.text(getLabel("loan.running_bal", locale));
                html2.endTag("td");
                html2.endTag("tr");

                html1.append(htmlHeader1);
                html2.append(htmlHeader2);

                if (twoTables) {
                    html1.startTag("tr");
                    html1.startTag("td", "colspan", "8", "class", "drawtablerowbold");
                    html1.text(getLabel("loan.instt_paid", locale));
                    html1.endTag("td");
                    html1.endTag("tr");
                    html2.startTag("tr");
                    html2.startTag("td", "colspan", "5", "class", "drawtablerowbold");
                    html2.nonBreakingSpace();
                    html2.endTag("td");
                    html2.endTag("tr");
                }

                int index = 0;
                boolean toContinue = true;
                LoanScheduleEntity installment = (LoanScheduleEntity) list.get(index);
                while (index <= list.size() - 1 && toContinue
                        && !installment.getTotalDueWithFees().equals(installment.getTotalScheduleAmountWithFees())) {

                    html1.append(createInstallmentRow(installment, true));
                    html2.append(createRunningBalanceRow(installment, totalPrincipal, totalInterest, totalFees, totalPenalties));
                    totalPrincipal = totalPrincipal.subtract(installment.getPrincipalPaid());
                    totalInterest = totalInterest.subtract(installment.getEffectiveInterestPaid());
                    totalFees = totalFees.subtract(installment.getTotalFeeAmountPaidWithMiscFee());
                    totalPenalties = totalPenalties.subtract(installment.getPenaltyPaid());
                    if (index != list.size() - 1 && installment.isPaid()) {
                        index++;
                        installment = (LoanScheduleEntity) list.get(index);

                    } else {
                        toContinue = false;
                    }
                }

                boolean dueInstallments = false;
                if (!installment.isPaid()
                        && installment.getActionDate().getTime() <= viewDate.getTime()) {
                    dueInstallments = true;
                }

                if (dueInstallments) {
                    html1.startTag("tr");
                    html1.startTag("td", "colspan", "8", "class", "drawtablerowbold");
                    html1.text(getLabel("loan.instt_due", locale));
                    html1.endTag("td");
                    html1.endTag("tr");
                    while (index < list.size() - 1
                            && !installment.isPaid()
                            && installment.getActionDate().getTime() <= viewDate.getTime()) {
                        index++;
                        html1.append(createInstallmentRow(installment, false));
                        installment = (LoanScheduleEntity) list.get(index);
                    }
                }

                boolean futureInstallments = false;
                if (!installment.isPaid()
                        && installment.getActionDate().getTime() > viewDate.getTime()) {
                    futureInstallments = true;
                }
                if (futureInstallments) {
                    html1.startTag("tr");
                    html1.startTag("td", "colspan", "8", "class", "drawtablerowbold");
                    html1.text(getLabel("loan.future_install", locale));
                    html1.endTag("td");
                    html1.endTag("tr");
                    while (index < list.size() - 1) {
                        index++;
                        html1.append(createInstallmentRow(installment, false));
                        installment = (LoanScheduleEntity) list.get(index);
                    }
                }
                // append the last transaction
                if (!installment.isPaid()) {
                    html1.append(createInstallmentRow(installment, false));
                }

                if (twoTables) {
                    // add a tr with 2 td for each of the 2 tables
                    html.startTag("tr");
                    html.startTag("td", "width", "70%");
                    html.startTag("table", "width", "95%", "border", "0", "cellspacing", "0", "cellpadding", "5", "id", "repaymentScheduleTable");
                    html.append(html1);
                    html.endTag("table");
                    html.endTag("td");
                    html.startTag("td", "width", "25%", "valign", "top");
                    html.startTag("table", "width", "95%", "border", "0", "cellspacing", "0", "cellpadding", "5", "id", "runningBalanceTable");
                    html.append(html2);
                    html.endTag("table");
                    html.endTag("td");
                    html.endTag("tr");
                    html.endTag("table");
                } else {
                    html.startTag("tr");
                    html.startTag("td", "width", "100%");
                    html.startTag("table", "id", "installments", "width", "95%", "border", "0", "cellspacing", "0", "cellpadding", "5");
                    html.append(html1);
                    html.endTag("table");
                    html.endTag("td");
                    html.endTag("tr");
                    html.endTag("table");

                }
            }
            pageContext.getOut().write(html.toString());
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

    private Date getViewDate(String currentFlowKey, FlowManager flowManager) throws PageExpiredException {
        Date viewDate = (Date) flowManager.getFromFlow(currentFlowKey, Constants.VIEW_DATE);
        viewDate = (viewDate == null) ? DateUtils.currentDate() : viewDate;
        return new DateTime(viewDate).withTime(23, 59, 59, 0).toDate();
    }

    XmlBuilder createInstallmentRow(LoanScheduleEntity installment, boolean isPaymentMade) {
        XmlBuilder html = new XmlBuilder();
        html.startTag("tr");

        html.startTag("td", "width", "6%", "class", "drawtablerow");
        html.text(installment.getInstallmentId().toString());
        html.endTag("td");

        html.startTag("td", "width", "18%", "class", "drawtablerow");
        html.text(DateUtils.getDBtoUserFormatString(installment.getActionDate(), locale).toString());
        html.endTag("td");

        html.startTag("td", "width", "18%", "class", "drawtablerow");
        html.text((isPaymentMade && installment.getPaymentDate() != null ? DateUtils.getDBtoUserFormatString(
                installment.getPaymentDate(), locale) : "-").toString());
        html.endTag("td");

        html.startTag("td", "width", "12%", "align", "right", "class", "drawtablerow");
        html.text((isPaymentMade ? ConversionUtil.formatNumber(installment.getPrincipalPaid().toString()) : ConversionUtil.formatNumber(installment.getPrincipalDue().toString())));
        html.endTag("td");

        html.startTag("td", "width", "12%", "align", "right", "class", "drawtablerow");
        html.text((isPaymentMade ? ConversionUtil.formatNumber(installment.getEffectiveInterestPaid().toString()) :
                ConversionUtil.formatNumber(installment.getEffectiveInterestDue().toString()) + ( 
                AccountingRules.isOverdueInterestPaidFirst() ? " ("+ConversionUtil.formatNumber((installment.getInterestPaid().add(installment.getExtraInterestPaid())).toString())+")" : "")));
        html.endTag("td");

        html.startTag("td", "width", "10%", "align", "right", "class", "drawtablerow");
        html.text((isPaymentMade ? ConversionUtil.formatNumber(installment.getTotalFeeAmountPaidWithMiscFee().toString()) : ConversionUtil.formatNumber(installment
                .getTotalFeeDueWithMiscFeeDue().toString())));
        html.endTag("td");
        
        html.startTag("td", "width", "12%", "align", "right", "class", "drawtablerow");
        html.text((isPaymentMade ? ConversionUtil.formatNumber(installment.getTotalPenaltyPaid().toString()) :
                ConversionUtil.formatNumber(installment.getPenaltyDue().toString())));
        html.endTag("td");

        html.startTag("td", "width", "12%", "align", "right", "class", "drawtablerow");
        html.text((isPaymentMade ? ConversionUtil.formatNumber(String.valueOf(installment.getPrincipalPaid().add(installment.getEffectiveInterestPaid()).add(
                installment.getTotalFeeAmountPaidWithMiscFee()).add(installment.getTotalPenaltyPaid()))) : ConversionUtil.formatNumber(String.valueOf(installment.getPrincipalDue().add(
                installment.getEffectiveInterestDue()).add(installment.getTotalFeeDueWithMiscFeeDue()).add(
                installment.getPenaltyDue())))));
        html.endTag("td");

        html.endTag("tr");
        return html;
    }

    XmlBuilder createRunningBalanceRow(LoanScheduleEntity installment, Money totalPrincipal, Money totalInterest,
            Money totalFees, Money totalPenalties) {
        XmlBuilder html = new XmlBuilder();
        html.startTag("tr");
        html.startTag("td", "width", "20%", "align", "right", "class", "drawtablerow");
        html.text(ConversionUtil.formatNumber(totalPrincipal.subtract(installment.getPrincipalPaid()).toString()));
        html.endTag("td");
        html.startTag("td", "width", "20%", "align", "right", "class", "drawtablerow");
        html.text(ConversionUtil.formatNumber(totalInterest.subtract(installment.getEffectiveInterestPaid()).toString()));
        html.endTag("td");
        html.startTag("td", "width", "20%", "align", "right", "class", "drawtablerow");
        html.text(ConversionUtil.formatNumber(totalFees.subtract(installment.getTotalFeeAmountPaidWithMiscFee()).toString()));
        html.endTag("td");
        html.startTag("td", "width", "20%", "align", "right", "class", "drawtablerow");
        html.text(ConversionUtil.formatNumber(totalPenalties.subtract(installment.getTotalPenaltyPaid()).toString()));
        html.endTag("td");
        html.startTag("td", "width", "20%", "align", "right", "class", "drawtablerow");
        
        html.text((ConversionUtil.formatNumber(String.valueOf(totalPrincipal.add(totalInterest).add(totalFees).add(totalPenalties).subtract(
                installment.getPrincipalPaid()).subtract(installment.getEffectiveInterestPaid()).subtract(
                installment.getTotalFeeAmountPaidWithMiscFee()).subtract(
                installment.getTotalPenaltyPaid())))).toString());
        html.endTag("td");
        html.endTag("tr");
        return html;

    }

    private String getLabel(String key, Locale locale) throws JspException {
        return LabelTagUtils.getInstance().getLabel(pageContext, "loanUIResources", locale, key, null);
    }
}
