package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.LabelTagUtils;

public class LoanActivityTag extends BodyTagSupport{
	Locale locale = null;
	
	@Override
	public int doStartTag() throws JspException {
		StringBuilder builder = new StringBuilder();
		try {
			String currentFlowKey = (String) pageContext.getRequest()
			.getAttribute(Constants.CURRENTFLOWKEY);
			HttpSession session =  pageContext.getSession();
			FlowManager flowManager = (FlowManager) session
					.getAttribute(Constants.FLOWMANAGER);
			Object object = flowManager.getFromFlow(currentFlowKey, LoanConstants.LOAN_ALL_ACTIVITY_VIEW);
			if(null != object) {
				locale = ((UserContext)pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY)).getPreferredLocale();
				builder.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				builder
						.append("<tr>")
						.append("<td colspan=\"8\">&nbsp;</td>")
						.append("<td colspan=\"4\" class=\"drawtablerowboldnoline\">"+getLabel("loan.running_bal",locale)+"</td>")
						.append("</tr>")
						.append("<tr class=\"drawtablerowbold\">")
						.append("<td width=\"9%\" class=\"drawtablerowbold\">"+getLabel("loan.date",locale)+"</td>")
						.append("<td width=\"19%\" class=\"drawtablerowbold\">"+getLabel("loan.activity",locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.principal",locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+MifosConfiguration.getInstance().getLabel(ConfigurationConstants.INTEREST,locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.fees",locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.penalty",locale)+"</td>")
						.append("<td width=\"6%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.total",locale)+"</td>")
						.append("<td width=\"4\" align=\"right\" class=\"fontnormalbold\">&nbsp;</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.principal",locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+MifosConfiguration.getInstance().getLabel(ConfigurationConstants.INTEREST,locale)+"</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.fees",locale)+"</td>")
						.append("<td width=\"6%\" align=\"right\" class=\"drawtablerowbold\">"+getLabel("loan.total",locale)+"</td>")
						.append("</tr>");
				
				List<LoanActivityView> loanRecentActivityViewSet = (List<LoanActivityView>) object;
				Iterator<LoanActivityView> it = loanRecentActivityViewSet.iterator();
				while(it.hasNext()) {
					LoanActivityView loanActivityView = it.next();
					builder.append("<tr valign=\"top\">");
					builder.append(buildLeftHeaderRows(loanActivityView));
					builder.append(buildRightHeaderRows(loanActivityView));
					builder.append("</tr>");
				}
				builder.append("</table>");
			}
			pageContext.getOut().write(builder.toString());
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	
	 String buildLeftHeaderRows(LoanActivityView loanRecentActivityView) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
		.append("<td class=\"drawtablerow\">")
		.append(DateUtils.getUserLocaleDate(locale, loanRecentActivityView.getActionDate().toString())).append("</td>")
		.append("<td class=\"drawtablerow\">")
		.append(loanRecentActivityView.getActivity()).append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getPrincipal()).append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getInterest()).append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getFees()).append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getPenalty()).append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getTotal()).append("</td>")
		.append("<td align=\"right\" class=\"fontnormalbold\"><br></td>");
		return stringBuilder.toString();
	}
	
	 String buildRightHeaderRows(LoanActivityView loanRecentActivityView) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getRunningBalancePrinciple())
		.append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getRunningBalanceInterest())
		.append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getRunningBalanceFees())
		.append("</td>")
		.append("<td align=\"right\" class=\"drawtablerow\">")
		.append(loanRecentActivityView.getRunningBalancePrinciple().add(loanRecentActivityView.getRunningBalanceInterest()).add(loanRecentActivityView.getRunningBalanceFees()))
		.append("</td>")
		.append("<td align=\"right\" class=\"fontnormalbold\"><br></td>");
		return stringBuilder.toString();
	}

	private String  getLabel(String key,Locale locale) throws JspException{
		return LabelTagUtils.getInstance().getLabel(pageContext,"loanUIResources",locale,key,null);
	}
}
