package org.mifos.application.office.struts.tag;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.util.helpers.Constants;

public class OfficeListTag extends BodyTagSupport {
	private String actionName;

	private String methodName;
	
	private String flowKey;

	private String onlyBranchOffices;

	@Override
	public int doStartTag() throws JspException {
		try {
			TagUtils.getInstance().write(pageContext, getOfficeList());
		} catch (Exception e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getOnlyBranchOffices() {
		return onlyBranchOffices;
	}

	public void setOnlyBranchOffices(String onlyBranchOffices) {
		this.onlyBranchOffices = onlyBranchOffices;
	}

	private String getOfficeList() throws Exception {

		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute(Constants.USERCONTEXT);
		OfficePersistence officePersistence = new OfficePersistence();
		OfficeBO officeBO = officePersistence.getOffice(userContext
				.getBranchId());
		StringBuilder result = new StringBuilder();

		List<OfficeView> levels = officePersistence.getActiveLevels(userContext
				.getLocaleId());

		// let make all levels name as locle variables
		String brach = "", regional = "", subregional = "", area = "";
		for (OfficeView level : levels) {
			if (level.getLevelId().equals(OfficeLevel.BRANCHOFFICE.getValue()))
				brach = MifosTagUtils.xmlEscape(level.getLevelName());
			else if (level.getLevelId().equals(OfficeLevel.AREAOFFICE.getValue()))
				area = MifosTagUtils.xmlEscape(level.getLevelName());
			else if (level.getLevelId().equals(OfficeLevel.REGIONALOFFICE
					.getValue()))
				regional = MifosTagUtils.xmlEscape(level.getLevelName());
			else if (level.getLevelId().equals(OfficeLevel.SUBREGIONALOFFICE
					.getValue()))
				subregional = MifosTagUtils.xmlEscape(level.getLevelName());

		}

		if (onlyBranchOffices != null) {
			getBranchOffices(result, officePersistence
					.getBranchParents(officeBO.getSearchId()), userContext,
					brach);
		} else {

			getAboveBranches(result, officePersistence
					.getOfficesTillBranchOffice(officeBO.getSearchId()),
					regional, subregional, area);
			getBranchOffices(result, officePersistence
					.getBranchParents(officeBO.getSearchId()), userContext,
					brach);


		}

		return result.toString();
	}

	void getBranchOffices(StringBuilder result,
			List<OfficeBO> officeList, UserContext userContext,
			String branchName) {
		result.append("<br /><span class=\"fontnormalBold\">");
		result.append(MifosTagUtils.xmlEscape(branchName));
		result.append("</span><br />");
		if (officeList == null) {
			ResourceBundle resources = ResourceBundle
					.getBundle(
							"org.mifos.application.office.util.resources.OfficeUIResources",
							userContext.getPereferedLocale());
			result.append("<span class=\"fontnormal\">");
			result.append(resources.getString("Office.labelNo"));
			result.append(" ");
			result.append(branchName.toLowerCase());
			result.append(" ");
			result.append(resources.getString("Office.labelPresent"));
			result.append("</span>");

		} else {

			for (int i = 0; i < officeList.size(); i++) {
				OfficeBO officeParent = officeList.get(i);
				if (officeParent.getChildren().size() > 0) {

					if (i > 0) {
						result.append("<br />");
					}
					result.append("<span class=\"fontnormal\">");
					result.append(MifosTagUtils.xmlEscape(officeParent.getOfficeName()));
					result.append("</span>");
					Set<OfficeBO> branchList = officeParent.getChildren();

					if (null != branchList) {
						result
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						for (OfficeBO office : branchList) {

							result.append("<tr class=\"fontnormal\">");
							result
									.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
							result.append("<td width=\"99%\">");
							result.append(getLink(office.getOfficeId(),
									MifosTagUtils.xmlEscape(office.getOfficeName())));
							result.append("</td>");
							result.append("</tr>");
						}
						result.append("</table>");
					}
				}

			}

		}

	}

	private String getLink(Short officeId, String officeName) {
		String newOfficeName = replaceSpaces(officeName);
		String str = "<a href=" + actionName + "?method=" + methodName
				+ "&amp;office.officeId=" + officeId
				+ "&amp;office.officeName=" + newOfficeName + "&amp;officeId="
				+ officeId + "&amp;officeName=" + newOfficeName+ "&amp;currentFlowKey=" +flowKey  + ">"
				+ officeName +"</a>";
		return str;
	}

	public String replaceSpaces(String officeName) {
		String replacedString = "";
		replacedString = officeName.trim().replaceAll(" ", "%20");
		return replacedString;
	}

	private void getAboveBranches(StringBuilder result,
			List<OfficeBO> officeList, String regional, String subregional,
			String area) throws OfficeException {
		if (null != officeList) {

			boolean regionalLabel = false, subRegionalLabel = false, areaLabel = false;

			StringBuffer regionalBuffer = null, subregionalBuffer = null, areaBuffer = null;

			for (int i = 0; i < officeList.size(); i++) {
				OfficeBO office = officeList.get(i);
				if (office.getOfficeLevel() == OfficeLevel.HEADOFFICE) {
					result.append("<br><span class=\"fontnormalbold\">");
					result.append(getLink(office.getOfficeId(), MifosTagUtils.xmlEscape(office.getOfficeName())));
					result.append("<br></span>");
					// result.append("<br><table width=\"95%\" border=\"0\"
					// cellspacing=\"0\" cellpadding=\"0\">");
				}

				else if (office.getOfficeLevel() == OfficeLevel.REGIONALOFFICE) {
					if (regionalLabel == false) {
						regionalBuffer = new StringBuffer();
						regionalBuffer
								.append("<br><table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						regionalBuffer.append("<tr>");
						regionalBuffer
								.append("<td><span class=\"fontnormalbold\">");
						regionalBuffer.append(MifosTagUtils.xmlEscape(regional));
						regionalBuffer.append("</span></td>");
						regionalBuffer.append("</table>");
						regionalBuffer
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						regionalLabel = true;
					}

					regionalBuffer.append("<tr class=\"fontnormal\">");
					regionalBuffer
							.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
					regionalBuffer.append("<td width=\"99%\">");
					regionalBuffer.append(getLink(office.getOfficeId(),
							MifosTagUtils.xmlEscape(office.getOfficeName())));
					regionalBuffer.append("</td>");
					regionalBuffer.append("</tr>");

				} else if (office.getOfficeLevel() == OfficeLevel.SUBREGIONALOFFICE) {
					if (subRegionalLabel == false) {
						subregionalBuffer = new StringBuffer();
						subregionalBuffer
								.append("<br><table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						subregionalBuffer.append("<tr>");
						subregionalBuffer
								.append("<td><span class=\"fontnormalbold\">");
						subregionalBuffer.append(MifosTagUtils.xmlEscape(subregional));
						subregionalBuffer.append("</span></td>");
						subregionalBuffer.append("</table>");
						subregionalBuffer
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						subRegionalLabel = true;
					}

					
					subregionalBuffer.append("<tr class=\"fontnormal\">");
					subregionalBuffer
							.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
					subregionalBuffer.append("<td width=\"99%\">");
					subregionalBuffer.append(getLink(office.getOfficeId(),
							MifosTagUtils.xmlEscape(office.getOfficeName())));
					subregionalBuffer.append("</td>");
					subregionalBuffer.append("</tr>");

				} else if (office.getOfficeLevel() == OfficeLevel.AREAOFFICE) {
					if (areaLabel == false) {
						areaBuffer = new StringBuffer();
						areaBuffer
								.append("<br><table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						areaBuffer.append("<tr>");
						areaBuffer
								.append("<td><span class=\"fontnormalbold\">");
						areaBuffer.append(MifosTagUtils.xmlEscape(area));
						areaBuffer.append("</span></td>");
						areaBuffer.append("</table>");
						areaBuffer
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						areaLabel = true;
					}

					areaBuffer.append("<tr class=\"fontnormal\">");
					areaBuffer
							.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
					areaBuffer.append("<td width=\"99%\">");
					areaBuffer.append(getLink(office.getOfficeId(), MifosTagUtils.xmlEscape(office.getOfficeName())));
					areaBuffer.append("</td>");
					areaBuffer.append("</tr>");

				}
			}

			if (regionalBuffer != null) {
				result.append(regionalBuffer);
				result.append("</table>");
			}
			if (subregionalBuffer != null) {
				result.append(subregionalBuffer);
				result.append("</table>");
			}
			if (areaBuffer != null) {
				result.append(areaBuffer);
				result.append("</table>");
			}
		}
	}

	public String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(String flowKey) {
		this.flowKey = flowKey;
	}
}
