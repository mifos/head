package org.mifos.application.accounts.savings.struts.tag;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;

public class SavingsOverDueDepositsTag extends BodyTagSupport {

	public SavingsOverDueDepositsTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		FlowManager flowManager = (FlowManager) pageContext.getSession()
				.getAttribute(Constants.FLOWMANAGER);
		try {
			Object obj = flowManager.getFromFlow((String) pageContext
					.getRequest().getAttribute(Constants.CURRENTFLOWKEY),
					Constants.BUSINESS_KEY);
			if (null != obj) {
				pageContext.getOut().write(
						buildUI(
								((SavingsBO) obj)
										.getDetailsOfInstallmentsInArrears(),
								((UserContext) pageContext.getSession()
										.getAttribute(Constants.USERCONTEXT))
										.getPereferedLocale()).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SKIP_BODY;
	}

	StringBuilder buildUI(
			List<AccountActionDateEntity> installmentsInArrears, Locale locale) {
		StringBuilder builder = new StringBuilder();
		Date actionDate = null;
		Money totalAmount = new Money();
		int currentSize = 0;
		int listSize = installmentsInArrears.size();

		for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
			SavingsScheduleEntity installment = (SavingsScheduleEntity) accountActionDate;
			currentSize++;
			if (actionDate == null)
				actionDate = installment.getActionDate();
			if (actionDate.compareTo(installment.getActionDate()) != 0
					|| currentSize == listSize) {
				if (currentSize == listSize
						&& actionDate.compareTo(installment.getActionDate()) == 0) {
					builder.append(buildDepositDueUIRow(locale, actionDate,
							totalAmount.add(installment.getTotalDepositDue())));
				} else if (actionDate.compareTo(installment.getActionDate()) != 0
						&& currentSize != listSize) {
					builder.append(buildDepositDueUIRow(locale, actionDate,
							totalAmount));
				} else {
					builder.append(buildDepositDueUIRow(locale, actionDate,
							totalAmount));
					builder
							.append(buildDepositDueUIRow(locale, installment
									.getActionDate(), installment
									.getTotalDepositDue()));
				}
				totalAmount = installment.getTotalDepositDue();
				actionDate = installment.getActionDate();
			} else
				totalAmount = totalAmount.add(installment.getTotalDepositDue());
		}

		return builder;

	}

	StringBuilder buildDateUI(Locale locale, Date actionDate) {
		StringBuilder builder = new StringBuilder();
		builder
				.append("<td class=\"drawtablerow\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		builder.append(DateHelper.getUserLocaleDate(locale, actionDate
				.toString()));
		builder.append("</td>");
		return builder;
	}

	StringBuilder buildAmountUI(Money amount) {
		StringBuilder builder = new StringBuilder();
		builder.append("<td align=\"right\" class=\"drawtablerow\">");
		builder.append(amount);
		builder.append("</td>");
		builder.append("<td align=\"right\" class=\"drawtablerow\">");
		builder.append("&nbsp;");
		builder.append("</td>");
		return builder;
	}

	StringBuilder buildDepositDueUIRow(Locale locale, Date actionDate,
			Money dueAmount) {
		StringBuilder builder = new StringBuilder();
		builder.append("<tr>");
		builder.append(buildDateUI(locale, actionDate));
		builder.append(buildAmountUI(dueAmount));
		builder.append("</tr>");
		return builder;
	}

}
