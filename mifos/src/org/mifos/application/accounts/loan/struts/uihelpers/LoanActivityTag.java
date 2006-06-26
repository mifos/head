package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;

public class LoanActivityTag extends BodyTagSupport{
	Locale locale = null;
	public int doStartTag() throws JspException {
		StringBuilder builder = new StringBuilder();
		try {
			Object object = pageContext.getSession().getAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW);
			if(null != object) {
				locale = ((UserContext)pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY)).getPereferedLocale();
				builder.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				builder
						.append("<tr>")
						.append("<td colspan=\"8\">&nbsp;</td>")
						.append("<td colspan=\"4\" class=\"drawtablerowboldnoline\">Running balance</td>")
						.append("</tr>")
						.append("<tr class=\"drawtablerowbold\">")
						.append("<td width=\"9%\" class=\"drawtablerowbold\">Date</td>")
						.append("<td width=\"19%\" class=\"drawtablerowbold\">Activity</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Principal</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Interest</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Fees</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Penalty</td>")
						.append("<td width=\"6%\" align=\"right\" class=\"drawtablerowbold\">Total</td>")
						.append("<td width=\"4\" align=\"right\" class=\"fontnormalbold\">&nbsp;</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Principal</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Interest</td>")
						.append("<td width=\"8%\" align=\"right\" class=\"drawtablerowbold\">Fees</td>")
						.append("<td width=\"6%\" align=\"right\" class=\"drawtablerowbold\">Total</td>")
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
	
	private String buildLeftHeaderRows(LoanActivityView loanRecentActivityView) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
		.append("<td class=\"drawtablerow\">")
		.append(DateHelper.getUserLocaleDate(locale,loanRecentActivityView.getActionDate().toString())).append("</td>")
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
	
	private String buildRightHeaderRows(LoanActivityView loanRecentActivityView) {
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

}
