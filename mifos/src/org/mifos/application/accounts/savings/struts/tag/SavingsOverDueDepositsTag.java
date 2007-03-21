package org.mifos.application.accounts.savings.struts.tag;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
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
										.getPreferredLocale()).toString());
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
		SavingsScheduleEntity installment = null;
		Date actionDate = null;
	
		Collections.sort(installmentsInArrears, new Comparator<AccountActionDateEntity>() {
			public int compare(AccountActionDateEntity actionDate1, AccountActionDateEntity actionDate2) {
				return actionDate1.getActionDate().compareTo(actionDate2.getActionDate());
			}
		});		
		
		for (int i = 0; i < installmentsInArrears.size();) {
			actionDate = installmentsInArrears.get(i).getActionDate();
			Money totalAmount = new Money();
			do{
				installment = (SavingsScheduleEntity) installmentsInArrears.get(i);	
				if(!(installment.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue()) 
						&& 	installment.getCustomer().getStatus().equals(CustomerStatus.CLIENT_CLOSED))){
					totalAmount = totalAmount.add(installment.getTotalDepositDue());
				}
				i++;
			} while(i < installmentsInArrears.size() && actionDate.equals(installmentsInArrears.get(i).getActionDate()));
			if(totalAmount.getAmountDoubleValue()>0)
				builder.append(buildDepositDueUIRow(locale, actionDate,
						totalAmount));
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
