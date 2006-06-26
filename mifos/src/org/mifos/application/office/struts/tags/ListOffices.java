package org.mifos.application.office.struts.tags;

import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.util.valueobjects.OfficeLevelChildren;
import org.mifos.application.master.util.valueobjects.OfficeLevelMaster;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.BranchOffice;
import org.mifos.application.office.util.valueobjects.BranchParentOffice;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.SearchResults;

public class ListOffices extends BodyTagSupport {

	private static final long serialVersionUID = 9889767667l;

	private String actionName;

	private String methodName;

	private String onlyBranchOffices;

	/**
	 * Construct a new instance of this tag.
	 */
	public ListOffices() {
		super();
	}

	/**
	 * Render the Label element
	 * 
	 * @return EVAL_PAGE
	 * @exception JspException
	 *                if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		TagUtils.getInstance().write(pageContext, getOfficeList());
		return EVAL_PAGE;
	}

	/**
	 * This function returns the other offices list 
	 * @return
	 * @throws JspException
	 */
	protected String getOfficeList() throws JspException {
		StringBuilder result = new StringBuilder();
		try {
			HttpSession session = pageContext.getSession();
			UserContext uc = (UserContext) session
					.getAttribute(Constants.USERCONTEXT);
			String searchId = HierarchyManager.getInstance().getSearchId(
					uc.getBranchId());
			Short officeLevel = ((UserContext) pageContext.getSession()
					.getAttribute(Constants.USERCONTEXT)).getOfficeLevelId();
			OfficeDAO officeDAO = new OfficeDAO();
			if (null == onlyBranchOffices
					|| !"yes".equalsIgnoreCase(onlyBranchOffices)) {
				List<OfficeLevelMaster> officeList = officeDAO
						.getTillBranchOfficeActive(searchId, officeLevel);
				getAboveBranches(result, officeList);
			}
			if (null == onlyBranchOffices
					|| "yes".equalsIgnoreCase(onlyBranchOffices)) {
				List<BranchParentOffice> branchList = officeDAO
						.getBranchOfficeActive(searchId);
				SearchResults sr = officeDAO.getAllOfficeLevel();
				List<OfficeLevelMaster> levelList = (List<OfficeLevelMaster>) sr
						.getValue();
				getBranchOffices(result, branchList, levelList,uc);
			}
		} catch (ApplicationException ae) {
			throw new JspException(ae);
		} catch (SystemException se) {
			throw new JspException(se);
		}
		return result.toString();
	}

	/**
	 * This function make the list of all the branch offices 
	 * @param result
	 * @param officeList
	 * @param levelList
	 * @param uc
	 */
	private void getBranchOffices(StringBuilder result,
			List<BranchParentOffice> officeList,
			List<OfficeLevelMaster> levelList,UserContext uc) {

		if (null != officeList) {
			
			MifosTagUtils tagUtils = MifosTagUtils.getInstance();
			
			if (officeList.size() > 0) {

				
				for(OfficeLevelMaster officeLevelMaster : levelList){
					if (officeLevelMaster.getLevelId().shortValue() == OfficeConstants.BRANCHOFFICE) {
						result.append("<br><span class=\"fontnormalBold\">");
						String ln = tagUtils.filter(officeLevelMaster.getLevelName());
						result.append(ln);
						result.append("</span><br>");
					}
				}

			}
			else
			{
				//In case of only branch office we still nedd to show the label with message
				if (null != onlyBranchOffices
						|| "yes".equalsIgnoreCase(onlyBranchOffices))
				{
					
					String levelname = "";
					for(OfficeLevelMaster officeLevelMaster : levelList){
						if (officeLevelMaster.getLevelId().shortValue() == OfficeConstants.BRANCHOFFICE) {
							result.append("<br><span class=\"fontnormalBold\">");
							String ln = tagUtils.filter(officeLevelMaster.getLevelName());
							result.append(ln);
							levelname=ln;
							result.append("</span><br>");

						}
					}
					
					//add a message also
					ResourceBundle resources = ResourceBundle.getBundle ("org.mifos.application.office.util.resources.Office", uc.getPereferedLocale());
					
					
					result.append("<span class=\"fontnormal\">");
					result.append(resources.getString("office.labelNo"));
					result.append(" ");
					result.append(levelname.toLowerCase());
					result.append(" ");
					result.append(resources.getString("office.labelPresent"));
					result.append("</span>");

				}
			}

			for (int i = 0; i < officeList.size(); i++) {
				BranchParentOffice officeParent = officeList.get(i);
				if (officeParent.getNoOfChildren() > 0) {

					if (i > 0) {
						result.append("<br>");
					}
					result.append("<span class=\"fontnormal\">");
					result.append(tagUtils.filter(officeParent.getOfficeName()));
					result.append("</span>");
					List<BranchOffice> branchList = officeParent
							.getBranchOffice();
					if (null != branchList) {
						result
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						for (int j = 0; j < branchList.size(); j++) {
							BranchOffice branchOffice = branchList.get(j);
							result.append("<tr class=\"fontnormal\">");
							result
									.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
							result.append("<td width=\"99%\">");
							result.append(getLink(branchOffice.getOfficeId(),
									tagUtils.filter(branchOffice.getOfficeName()) ));
							result.append("</td>");
							result.append("</tr>");
						}
						result.append("</table>");
					}
				}

			}
		}
	}

	private void getAboveBranches(StringBuilder result,
			List<OfficeLevelMaster> officeList) {
		if (null != officeList) {
			MifosTagUtils tagUtils = MifosTagUtils.getInstance();

			for (int i = 0; i < officeList.size(); i++) {
				OfficeLevelMaster office = officeList.get(i);
				if (office.getLevelId() == OfficeConstants.HEADOFFICE) {
					result.append("<br><span class=\"fontnormalbold\">");
					List olm = office.getOfficeLevelChildren();

					if (null != olm && olm.size() > 0) {
						// this will have only one childern as this is head
						// office
						OfficeLevelChildren olc = (OfficeLevelChildren) olm
								.get(0);
						// olc.getOfficeId()
						result.append(getLink(olc.getOfficeId(),tagUtils.filter( olc
								.getOfficeName())));

					}
					// result.append(getLink(office.getLevelId(),
					// office.getLevelName()));
					result.append("<br></span>");
				} else if (office.getLevelId() == OfficeConstants.REGIONALOFFICE
						|| office.getLevelId() == OfficeConstants.SUBREGIONALOFFICE
						|| office.getLevelId() == OfficeConstants.AREAOFFICE) {

					// ------------children for office level
					List<OfficeLevelChildren> officeChildren = office
							.getOfficeLevelChildren();

					if (null != officeChildren && officeChildren.size() > 0) {
						result
								.append("<br><table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						result.append("<tr>");
						result.append("<td><span class=\"fontnormalbold\">");
						result.append(tagUtils.filter(office.getLevelName()));
						result.append("</span></td>");
						result.append("</table>");
						result
								.append("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
						for (int j = 0; j < officeChildren.size(); j++) {
							OfficeLevelChildren officeChild = officeChildren
									.get(j);
							result.append("<tr class=\"fontnormal\">");
							result
									.append("<td width=\"1%\"><img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\"></td>");
							result.append("<td width=\"99%\">");
							result.append(getLink(officeChild.getOfficeId(),
									tagUtils.filter(officeChild.getOfficeName())));
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
				+ "&amp;office.officeName=" + newOfficeName + ">" + officeName
				+ "</a>";
		return str;
	}

	public String replaceSpaces(String officeName) {
		String replacedString = "";
		replacedString = officeName.trim().replaceAll(" ", "%20");
		return replacedString;
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
}
