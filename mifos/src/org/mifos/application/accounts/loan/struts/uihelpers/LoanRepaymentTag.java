package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.LabelTagUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanRepaymentTag extends BodyTagSupport {
	Locale locale = null;

	public int doStartTag() throws JspException {
		boolean twoTables = false;
		StringBuilder builder = new StringBuilder();
		Money totalPrincipal = new Money();
		Money totalInterest = new Money();
		Money totalFees = new Money();

		try {
			LoanBO loanBO = (LoanBO) pageContext.getSession().getAttribute(
					Constants.BUSINESS_KEY);
			if (loanBO != null) {
				List<AccountActionDateEntity> list = new ArrayList<AccountActionDateEntity>();
				list.addAll(loanBO.getAccountActionDates());
				locale = ((UserContext) pageContext.getSession().getAttribute(
						LoginConstants.USERCONTEXT)).getPereferedLocale();
				if (list != null && list.size() != 0) {
					// topmost table
					builder
							.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
	
					// Left side header
					StringBuilder builderHeader1 = new StringBuilder();
					builderHeader1.append(
							"<tr><td width=\"6%\" class=\"drawtablerowbold\">"
									+ getLabel("loan.no", locale) + "</td>")
							.append(
									"<td width=\"18%\" class=\"drawtablerowbold\">"
											+ getLabel("loan.due_date", locale)
											+ "</td>").append(
									"<td width=\"18%\" class=\"drawtablerowbold\">"
											+ getLabel("loan.date_paid", locale)
											+ "</td>").append(
									"<td width=\"15%\" align=\"right\" class=\"drawtablerowbold\">"
											+ getLabel("loan.principal", locale)
											+ "</td>");
	
					builderHeader1
							.append("<td width=\"14%\" align=\"right\" class=\"drawtablerowbold\">"
									+ MifosConfiguration
											.getInstance()
											.getLabel(
													ConfigurationConstants.INTEREST,
													locale) + "</td>");
	
					builderHeader1.append(
							"<td width=\"14%\" align=\"right\" class=\"drawtablerowbold\">"
									+ getLabel("loan.fees", locale) + "</td>")
							.append(
									"<td width=\"15%\" align=\"right\" class=\"drawtablerowbold\">"
											+ getLabel("loan.total", locale)
											+ "</td></tr>");
	
					for (AccountActionDateEntity acctDate : list) {
						LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) acctDate;
						totalPrincipal = totalPrincipal.add(loanScheduleEntity
								.getPrincipal());
						totalInterest = totalInterest.add(loanScheduleEntity
								.getInterest());
						totalFees = totalFees
								.add(loanScheduleEntity.getTotalFees());
					}
	
					// check if at least the first installment is paid
					AccountActionDateEntity firstInstallment = list.get(0);
	
					StringBuilder builder1 = new StringBuilder();
					StringBuilder builder2 = new StringBuilder();
	
					StringBuilder builderHeader2 = new StringBuilder();
	
					if (firstInstallment.getPaymentStatus() == YesNoFlag.YES
							.getValue()) {
						twoTables = true;
						// installments paid and running balance table is required
						builderHeader2
								.append(
										"<tr><td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">"
												+ getLabel("loan.principal", locale)
												+ "</td>")
								.append(
										"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">"
												+ MifosConfiguration
														.getInstance()
														.getLabel(
																ConfigurationConstants.INTEREST,
																locale) + "</td>")
								.append(
										"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">"
												+ getLabel("loan.fees", locale)
												+ "</td>").append(
										"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">"
												+ getLabel("loan.total", locale)
												+ "</td></tr>");
	
					}
	
					builder1
							.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">&nbsp;</tr>");
					builder2
							.append("<tr><td colspan=\"4\" class=\"drawtablerowbold\">"
									+ getLabel("loan.running_bal", locale)
									+ "</tr>");
	
					builder1.append(builderHeader1.toString());
					builder2.append(builderHeader2.toString());
	
					if (twoTables) {
						builder1
								.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">"
										+ getLabel("loan.instt_paid", locale)
										+ "</tr>");
						builder2
								.append("<tr><td colspan=\"4\" class=\"drawtablerowbold\">&nbsp;</tr>");
					}
	
					int index = 0;
					LoanScheduleEntity installment = (LoanScheduleEntity) list
							.get(index);
					while (index <= list.size() - 1
							&& installment.getPaymentStatus() == YesNoFlag.YES
									.getValue()) {
						index++;
						if (index != list.size())
							builder1.append(createInstallmentRow(installment));
						builder2.append(createRunningBalanceRow(installment,
								totalPrincipal, totalInterest, totalFees));
						totalPrincipal = totalPrincipal.subtract(installment
								.getPrincipal());
						totalInterest = totalInterest.subtract(installment
								.getInterest());
						totalFees = totalFees.subtract(installment.getTotalFees());
						if (index != list.size())
							installment = (LoanScheduleEntity) list.get(index);
					}
	
					boolean dueInstallments = false;
					if (installment.getPaymentStatus() == YesNoFlag.NO.getValue()
							&& installment.getActionDate().getTime() <= new java.util.Date()
									.getTime())
						dueInstallments = true;
	
					if (dueInstallments) {
						builder1
								.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">"
										+ getLabel("loan.instt_due", locale)
										+ "</tr>");
						while (index < list.size() - 1
								&& installment.getPaymentStatus() == YesNoFlag.NO
										.getValue()
								&& installment.getActionDate().getTime() <= new java.util.Date()
										.getTime()) {
							index++;
							builder1.append(createInstallmentRow(installment));
							installment = (LoanScheduleEntity) list.get(index);
						}
					}
	
					boolean futureInstallments = false;
					if (installment.getPaymentStatus() == YesNoFlag.NO.getValue()
							&& installment.getActionDate().getTime() > new java.util.Date()
									.getTime())
						futureInstallments = true;
					if (futureInstallments) {
						builder1
								.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">"
										+ getLabel("loan.future_install", locale)
										+ "</tr>");
						while (index < list.size() - 1) {
							index++;
							builder1.append(createInstallmentRow(installment));
							installment = (LoanScheduleEntity) list.get(index);
						}
					}
					// append the last transaction
					builder1.append(createInstallmentRow(installment));
	
					if (twoTables) {
						// add a tr with 2 td for each of the 2 tables
						builder
								.append("<tr>")
								.append("<td width=\"70%\">")
								.append(
										"<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\">")
								.append(builder1.toString())
								.append("</table>")
								.append("</td>")
								.append("<td width=\"25%\" valign=\"top\">")
								.append(
										"<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\">")
								.append(builder2.toString()).append("</table>")
								.append("</td>").append("</tr></table>");
					} else {
						builder
								.append("<tr>")
								.append("<td width=\"100%\">")
								.append(
										"<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\">")
								.append(builder1.toString()).append("</table>")
								.append("</td>").append("</tr></table>");
	
					}
					// // System.out.println(builder.toString());
				}
			}

			pageContext.getOut().write(builder.toString());
		} catch (Exception e) {
			// ignore. Do nothing
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	private String createInstallmentRow(LoanScheduleEntity installment) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<tr><td width=\"6%\" class=\"drawtablerow\">")
				.append(installment.getInstallmentId())
				.append("</td>")
				.append("<td width=\"15%\" class=\"drawtablerow\">")
				.append(
						DateHelper.getDBtoUserFormatString(installment
								.getActionDate(), locale))
				.append("</td>")
				.append("<td width=\"15%\" class=\"drawtablerow\">")
				.append(
						(installment.getPaymentDate() == null ? "-"
								: DateHelper.getDBtoUserFormatString(
										installment.getPaymentDate(), locale)))
				.append("</td>")
				.append(
						"<td width=\"16%\" align=\"right\" class=\"drawtablerow\">")
				.append(installment.getPrincipal())
				.append("</td>")
				.append(
						"<td width=\"15%\" align=\"right\" class=\"drawtablerow\">")
				.append(installment.getInterest())
				.append("</td>")
				.append(
						"<td width=\"15%\" align=\"right\" class=\"drawtablerow\">")
				.append(installment.getTotalFees())
				.append("</td>")
				.append(
						"<td width=\"18%\" align=\"right\" class=\"drawtablerow\">")
				.append(
						installment.getPrincipal().add(
								installment.getInterest()).add(
								installment.getTotalFees())).append(
						"</td></tr>");
		return builder.toString();
	}

	private String createRunningBalanceRow(LoanScheduleEntity installment,
			Money totalPrincipal, Money totalInterest, Money totalFees) {
		StringBuilder builder = new StringBuilder();
		builder
				.append(
						"<tr><td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalPrincipal.subtract(installment.getPrincipal()))
				.append("</td>")
				.append(
						"<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalInterest.subtract(installment.getInterest()))
				.append("</td>")
				.append(
						"<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalFees.subtract(installment.getTotalFees()))
				.append("</td>")
				.append(
						"<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(
						totalPrincipal.add(totalInterest).add(totalFees)
								.subtract(installment.getPrincipal()).subtract(
										installment.getInterest()).subtract(
										installment.getTotalFees())).append(
						"</td></tr>");

		return builder.toString();

	}

	private String getLabel(String key, Locale locale) throws JspException {
		return LabelTagUtils.getInstance().getLabel(pageContext,
				"loanUIResources", locale, key, null);
	}
}