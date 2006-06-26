package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.struts.tags.MifosPropertyMessageResources;
import org.mifos.framework.struts.tags.MifosPropertyMessageResourcesFactory;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;

public class LoanRepaymentTableTag extends BodyTagSupport {

	Locale locale = null;

	public int doStartTag() throws JspException {
		boolean twoTables = false;
		StringBuilder builder = new StringBuilder();
		Money totalPrincipal = new Money();
		Money totalInterest = new Money();
		Money totalFees = new Money();

		try {
			Object object = pageContext.getRequest().getAttribute(
					AccountConstants.ACCOUNT_GETINSTALLMENTS);
			if (object != null) {

				// MifosPropertyMessageResources resources =
				// (MifosPropertyMessageResources)TagUtils.getInstance().retrieveMessageResources(pageContext,
				// "loanUIResources", false);
				locale =((UserContext)pageContext.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();

				// topmost table
				builder
						.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

				// Left side header
				StringBuilder builderHeader1 = new StringBuilder();
				builderHeader1
						.append(
								"<tr><td width=\"6%\" class=\"drawtablerowbold\">No.</td>")
						.append(
								"<td width=\"18%\" class=\"drawtablerowbold\">Due Date</td>")
						.append(
								"<td width=\"18%\" class=\"drawtablerowbold\">Date Paid</td>")
						.append(
								"<td width=\"15%\" align=\"right\" class=\"drawtablerowbold\">Principal</td>");
				
				builderHeader1.append(
								"<td width=\"14%\" align=\"right\" class=\"drawtablerowbold\">"+MifosConfiguration.getInstance().getLabel(ConfigurationConstants.INTEREST,locale)+"</td>");
				
				
						builderHeader1.append(
								"<td width=\"14%\" align=\"right\" class=\"drawtablerowbold\">Fees</td>")
						.append(
								"<td width=\"15%\" align=\"right\" class=\"drawtablerowbold\">Total</td></tr>");

				List<AccountActionDate> list = (List<AccountActionDate>) object;
				// iterate once through the list to get the totals. Else will
				// have to query the database again
				for (AccountActionDate acctDate : list) {
					totalPrincipal=totalPrincipal.add(acctDate.getPrincipal());
					totalInterest=totalInterest.add(acctDate.getInterest());
					totalFees=totalFees.add(acctDate.getTotalFeesAmountWithMisc());
				}

				// check if at least the first installment is paid
				AccountActionDate firstInstallment = list.get(0);

				StringBuilder builder1 = new StringBuilder();
				StringBuilder builder2 = new StringBuilder();

				StringBuilder builderHeader2 = new StringBuilder();

				if (firstInstallment.getPaymentStatus() == Constants.YES) {
					twoTables = true;
					// installments paid and running balance table is required
					builderHeader2
							.append(
									"<tr><td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">Principal</td>")
							.append(
									"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">Interest</td>")
							.append(
									"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">Fees</td>")
							.append(
									"<td width=\"25%\" align=\"right\" class=\"drawtablerowbold\">Total</td></tr>");

				}

				builder1
						.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">&nbsp;</tr>");
				builder2
						.append("<tr><td colspan=\"4\" class=\"drawtablerowbold\">Running Balance</tr>");

				builder1.append(builderHeader1.toString());
				builder2.append(builderHeader2.toString());

				if (twoTables){
					builder1
							.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">Installments Paid</tr>");
					builder2
					.append("<tr><td colspan=\"4\" class=\"drawtablerowbold\">&nbsp;</tr>");
				}

				int index = 0;
				AccountActionDate installment = list.get(index);
				while (index <=list.size() - 1
						&& installment.getPaymentStatus() == Constants.YES) {
					index++;
					if(index!=list.size())
						builder1.append(createInstallmentRow(installment));
					builder2.append(createRunningBalanceRow(installment, totalPrincipal, totalInterest, totalFees));
					totalPrincipal = totalPrincipal.subtract(installment.getPrincipal());
					totalInterest = totalInterest.subtract(installment.getInterest());
					totalFees = totalFees.subtract(installment.getTotalFeesAmountWithMisc());
					if(index!=list.size())
						installment = list.get(index);
				}

				boolean dueInstallments = false;
				if (installment.getPaymentStatus() == Constants.NO
						&& installment.getActionDate().getTime() <= new java.util.Date()
								.getTime())
					dueInstallments = true;

				if (dueInstallments) {
					builder1
							.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">Installments Due</tr>");
					while (index < list.size() - 1
							&& installment.getPaymentStatus() == Constants.NO
							&& installment.getActionDate().getTime() <= new java.util.Date()
									.getTime()) {
						index++;
						builder1.append(createInstallmentRow(installment));
						installment = list.get(index);
					}
				}

				boolean futureInstallments = false;
				if (installment.getPaymentStatus() == Constants.NO
						&& installment.getActionDate().getTime() > new java.util.Date()
								.getTime())
					futureInstallments = true;
				if(futureInstallments){
					builder1
							.append("<tr><td colspan=\"7\" class=\"drawtablerowbold\">Future Installments</tr>");
					while (index < list.size() - 1) {
						index++;
						builder1.append(createInstallmentRow(installment));
						installment = list.get(index);
					}
				}
				//append the last transaction
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

			pageContext.getOut().write(builder.toString());
		} catch (Exception e) {
			// ignore. Do nothing
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	private String createInstallmentRow(AccountActionDate installment) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<tr><td width=\"6%\" class=\"drawtablerow\">")
				.append(installment.getInstallmentId())
				.append("</td>")
				.append("<td width=\"15%\" class=\"drawtablerow\">")
				.append(DateHelper.getDBtoUserFormatString(installment.getActionDate(),locale))
				.append("</td>")
				.append("<td width=\"15%\" class=\"drawtablerow\">")
				.append((installment.getPaymentDate()== null ? "-" : DateHelper.getDBtoUserFormatString(installment.getPaymentDate(),locale)))
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
				.append(installment.getTotalFeesAmountWithMisc())
				.append("</td>")
				.append(
						"<td width=\"18%\" align=\"right\" class=\"drawtablerow\">")
				.append(installment.getPrincipal().add(installment.getInterest()).add(installment.getTotalFeesAmountWithMisc()))
				.append("</td></tr>");
		return builder.toString();
	}

	private String createRunningBalanceRow(AccountActionDate installment, Money totalPrincipal, Money totalInterest, Money totalFees) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<tr><td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalPrincipal.subtract(installment.getPrincipal()))
				.append("</td>")
				.append("<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalInterest.subtract(installment.getInterest()))
				.append("</td>")
				.append("<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(totalFees.subtract(installment.getTotalFeesAmountWithMisc()))
				.append("</td>")
				.append(
						"<td width=\"25%\" align=\"right\" class=\"drawtablerow\">")
				.append(
						totalPrincipal.add(totalInterest).add(totalFees).subtract(installment.getPrincipal()).subtract(installment.getInterest()).subtract(installment.getTotalFeesAmountWithMisc()))
				.append("</td></tr>");

		return builder.toString();

	}


}