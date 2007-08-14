package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.LabelTagUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanRepaymentTag extends BodyTagSupport {
	Locale locale = null;

	@Override
	public int doStartTag() throws JspException {
		boolean twoTables = false;
		XmlBuilder html = new XmlBuilder();
		Money totalPrincipal = new Money();
		Money totalInterest = new Money();
		Money totalFees = new Money();
		LoanBO loanBO = null;

		try {
			String currentFlowKey = (String) pageContext.getRequest()
			.getAttribute(Constants.CURRENTFLOWKEY);
			HttpSession session =  pageContext.getSession();
			FlowManager flowManager = (FlowManager) session
					.getAttribute(Constants.FLOWMANAGER);
			 loanBO =(LoanBO) flowManager.getFromFlow(currentFlowKey, Constants.BUSINESS_KEY);
					
			/*LoanBO loanBO = (LoanBO) pageContext.getRequest().getAttribute(
					Constants.BUSINESS_KEY);*/
			if (loanBO != null) {
				List<AccountActionDateEntity> list = new ArrayList<AccountActionDateEntity>();
				list.addAll(loanBO.getAccountActionDates());
				locale = ((UserContext) pageContext.getSession().getAttribute(
						LoginConstants.USERCONTEXT)).getPreferredLocale();
				if (list != null && list.size() != 0) {
					// topmost table
					html.startTag("table", "width","100%","border","0","cellspacing","0","cellpadding","0");
					// Left side header
					XmlBuilder htmlHeader1 =  new XmlBuilder();
					htmlHeader1.startTag("tr");
					htmlHeader1.startTag("td", "width" ,"6%","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.no", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"18%","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.due_date", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"18%","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.date_paid", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"15%","align","right","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.principal", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"14%","align","right","class","drawtablerowbold");
					htmlHeader1.text(MifosConfiguration
							.getInstance()
							.getLabel(
									ConfigurationConstants.INTEREST,
									locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"14%","align","right","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.fees", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.startTag("td", "width" ,"15%","align","right","class","drawtablerowbold");
					htmlHeader1.text(getLabel("loan.total", locale));
					htmlHeader1.endTag("td");
					htmlHeader1.endTag("tr");

					for (AccountActionDateEntity acctDate : list) {
						LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) acctDate;
						totalPrincipal = totalPrincipal.add(loanScheduleEntity
								.getPrincipal());
						totalInterest = totalInterest.add(loanScheduleEntity
								.getInterest());
						totalFees = totalFees.add(loanScheduleEntity
								.getTotalScheduledFeeAmountWithMiscFee());
					}

					// check if at least the first installment is paid
					LoanScheduleEntity firstInstallment = (LoanScheduleEntity) list
							.get(0);
					XmlBuilder html1 = new XmlBuilder();
					XmlBuilder html2 = new XmlBuilder();
					XmlBuilder htmlHeader2 = new XmlBuilder();
					if (firstInstallment.getTotalDueWithFees()
							.getAmountDoubleValue() != firstInstallment
							.getTotalScheduleAmountWithFees()
							.getAmountDoubleValue()) {
						twoTables = true;
						// installments paid and running balance table is
						// required
						htmlHeader2.startTag("tr");
						htmlHeader2.startTag("td", "width","25%","align","right","class","drawtablerowbold");
						htmlHeader2.text(getLabel("loan.principal",
								locale));
						htmlHeader2.endTag("td");
						htmlHeader2.startTag("td", "width" ,"25%","align","right","class","drawtablerowbold");
						htmlHeader2.text(MifosConfiguration
								.getInstance()
								.getLabel(
										ConfigurationConstants.INTEREST,
										locale));
						htmlHeader2.endTag("td");
						htmlHeader2.startTag("td", "width" ,"25%","align","right","class","drawtablerowbold");
						htmlHeader2.text(getLabel("loan.fees", locale));
						htmlHeader2.endTag("td");
						htmlHeader2.startTag("td", "width" ,"25%","align","right","class","drawtablerowbold");
						htmlHeader2.text(getLabel("loan.total", locale));
						htmlHeader2.endTag("td");
						htmlHeader2.endTag("tr");

					}

					html1.startTag("tr");
					html1.startTag("td", "colspan" ,"7","class","drawtablerowbold");
					html1.nonBreakingSpace();
					html1.endTag("td");
					html1.endTag("tr");
					html2.startTag("tr");
					html2.startTag("td", "colspan" ,"4","class","drawtablerowbold");
					html2.text(getLabel("loan.running_bal", locale));
					html2.endTag("td");
					html2.endTag("tr");

					html1.append(htmlHeader1);
					html2.append(htmlHeader2);

					if (twoTables) {
						html1.startTag("tr");
						html1.startTag("td", "colspan" ,"7","class","drawtablerowbold");
						html1.text(getLabel("loan.instt_paid", locale));
						html1.endTag("td");
						html1.endTag("tr");
						html2.startTag("tr");
						html2.startTag("td", "colspan" ,"4","class","drawtablerowbold");
						html2.nonBreakingSpace();
						html2.endTag("td");
						html2.endTag("tr");
					}

					int index = 0;
					boolean toContinue = true;
					LoanScheduleEntity installment = (LoanScheduleEntity) list
							.get(index);
					while (index <= list.size() - 1
							&& toContinue
							&& installment.getTotalDueWithFees()
									.getAmountDoubleValue() != installment
									.getTotalScheduleAmountWithFees()
									.getAmountDoubleValue()) {
						
						html1.append(createInstallmentRow(installment,
								true));
						html2.append(createRunningBalanceRow(installment,
								totalPrincipal, totalInterest, totalFees));
						totalPrincipal = totalPrincipal.subtract(installment
								.getPrincipalPaid());
						totalInterest = totalInterest.subtract(installment
								.getInterestPaid());
						totalFees = totalFees.subtract(installment
								.getTotalFeeAmountPaidWithMiscFee());
						if (index != list.size()-1
								&& installment.isPaid()) {
							index++;
							installment = (LoanScheduleEntity) list.get(index);

						} else {
							toContinue = false;
						}
					}

					boolean dueInstallments = false;
					if (!installment.isPaid()
							&& installment.getActionDate().getTime() <= new java.util.Date()
									.getTime())
						dueInstallments = true;

					if (dueInstallments) {
						html1.startTag("tr");
						html1.startTag("td", "colspan" ,"7","class","drawtablerowbold");
						html1.text(getLabel("loan.instt_due", locale));
						html1.endTag("td");
						html1.endTag("tr");
						while (index < list.size() - 1
								&& !installment.isPaid()
								&& installment.getActionDate().getTime() <= new java.util.Date()
										.getTime()) {
							index++;
							html1.append(createInstallmentRow(installment,
									false));
							installment = (LoanScheduleEntity) list.get(index);
						}
					}

					boolean futureInstallments = false;
					if (!installment.isPaid()
							&& installment.getActionDate().getTime() > new java.util.Date()
									.getTime())
						futureInstallments = true;
					if (futureInstallments) {
						html1.startTag("tr");
						html1.startTag("td", "colspan" ,"7","class","drawtablerowbold");
						html1.text(getLabel("loan.future_install", locale));
						html1.endTag("td");
						html1.endTag("tr");
						while (index < list.size() - 1) {
							index++;
							html1.append(createInstallmentRow(installment,
									false));
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
						html.startTag("td", "width" ,"70%");
						html.startTag("table", "width" ,"95%","border","0","cellspacing","0","cellpadding","5");
						html.append(html1);
						html.endTag("table");
						html.endTag("td");
						html.startTag("td", "width" ,"25%","valign","top");
						html.startTag("table", "width" ,"95%","border","0","cellspacing","0","cellpadding","5");
						html.append(html2);
						html.endTag("table");
						html.endTag("td");
						html.endTag("tr");
						html.endTag("table");
					} else {
						html.startTag("tr");
						html.startTag("td", "width" ,"100%");
						html.startTag("table", "width" ,"95%","border","0","cellspacing","0","cellpadding","5");
						html.append(html1);
						html.endTag("table");
						html.endTag("td");
						html.endTag("tr");
						html.endTag("table");

					}
				}
			}

			pageContext.getOut().write(html.toString());
		} catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	 XmlBuilder createInstallmentRow(LoanScheduleEntity installment,
			boolean isPaymentMade) {
		 	XmlBuilder html = new XmlBuilder();
		 	html.startTag("tr");
			
		 	html.startTag("td", "width" ,"6%","class","drawtablerow");
		 	html.text(installment.getInstallmentId().toString());
		 	html.endTag("td");
			
		 	html.startTag("td", "width" ,"15%","class","drawtablerow");
		 	html.text(DateUtils.getDBtoUserFormatString(installment
					.getActionDate(), locale).toString());
		 	html.endTag("td");
			
		 	html.startTag("td", "width" ,"15%","class","drawtablerow");
		 	html.text((isPaymentMade && installment.getPaymentDate() != null ? DateUtils.getDBtoUserFormatString(installment
					.getPaymentDate(), locale)
					: "-").toString());
		 	html.endTag("td");

		 	html.startTag("td", "width" ,"16%","align","right","class","drawtablerow");
		 	html.text((isPaymentMade ? installment.getPrincipalPaid()
					: installment.getPrincipalDue()).toString());
		 	html.endTag("td");
			
		 	html.startTag("td", "width" ,"15%","align","right","class","drawtablerow");
		 	html.text((isPaymentMade ? installment.getInterestPaid()
					: installment.getInterestDue()).toString());
		 	html.endTag("td");
			
		 	html.startTag("td", "width" ,"15%","align","right","class","drawtablerow");
		 	html.text((isPaymentMade ? installment
					.getTotalFeeAmountPaidWithMiscFee()
					: installment.getTotalFeeDueWithMiscFeeDue()).toString());
		 	html.endTag("td");
			
		 	html.startTag("td", "width" ,"18%","align","right","class","drawtablerow");
		 	html.text((isPaymentMade ? installment.getPrincipalPaid().add(
					installment.getInterestPaid()).add(
							installment.getTotalFeeAmountPaidWithMiscFee())
							: installment
									.getPrincipalDue()
									.add(installment.getInterestDue())
									.add(
											installment
													.getTotalFeeDueWithMiscFeeDue())).toString());
		 	html.endTag("td");
			
		 	html.endTag("tr");
		return html;
	}

	 XmlBuilder createRunningBalanceRow(LoanScheduleEntity installment,
			Money totalPrincipal, Money totalInterest, Money totalFees) {
		 	XmlBuilder html = new XmlBuilder();
		 	html.startTag("tr");
		 	html.startTag("td", "width" ,"25%","align","right","class","drawtablerow");
		 	html.text(totalPrincipal.subtract(installment.getPrincipalPaid()).toString());
		 	html.endTag("td");
		 	html.startTag("td", "width" ,"25%","align","right","class","drawtablerow");
		 	html.text(totalInterest.subtract(installment.getInterestPaid()).toString());
		 	html.endTag("td");
		 	html.startTag("td", "width" ,"25%","align","right","class","drawtablerow");
		 	html.text(totalFees.subtract(installment
					.getTotalFeeAmountPaidWithMiscFee()).toString());
		 	html.endTag("td");
		 	html.startTag("td", "width" ,"25%","align","right","class","drawtablerow");
		 	html.text(totalPrincipal
					.add(totalInterest)
					.add(totalFees)
					.subtract(installment.getPrincipalPaid())
					.subtract(installment.getInterestPaid())
					.subtract(
							installment
									.getTotalFeeAmountPaidWithMiscFee()).toString());
		 	html.endTag("td");
		 	html.endTag("tr");
			return html;

	}

	private String getLabel(String key, Locale locale) throws JspException {
		return LabelTagUtils.getInstance().getLabel(pageContext,
				"loanUIResources", locale, key, null);
	}
}
