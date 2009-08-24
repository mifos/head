/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.office.struts.tag;

import java.util.HashSet;
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
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;

public class OfficeListTag extends BodyTagSupport {
    private String actionName;

    private String methodName;

    private String flowKey;

    /* null for false, anything else for true */
    private String onlyBranchOffices;

    public OfficeListTag() {
    }

    public OfficeListTag(String action, String method, String flow) {
        actionName = action;
        methodName = method;
        flowKey = flow;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USERCONTEXT);

            TagUtils.getInstance().write(pageContext, getOfficeList(userContext));

        } catch (Exception e) {
            /**
             * This turns into a (rather ugly) error 500. TODO: make it more
             * reasonable.
             */
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

    String getOfficeList(UserContext userContext) throws Exception {

        OfficePersistence officePersistence = new OfficePersistence();
        OfficeBO officeBO = officePersistence.getOffice(userContext.getBranchId());

        List<OfficeView> levels = officePersistence.getActiveLevels(userContext.getLocaleId());
        OfficeBO loggedInOffice = officePersistence.getOffice(userContext.getBranchId());

        List<OfficeBO> branchParents = officePersistence.getBranchParents(officeBO.getSearchId());
        List<OfficeBO> officesTillBranchOffice = officePersistence.getOfficesTillBranchOffice(officeBO.getSearchId());

        return getOfficeList(userContext, levels, loggedInOffice, branchParents, officesTillBranchOffice);
    }

    @Deprecated
    String getOfficeList(UserContext userContext, List<OfficeView> levels, OfficeBO loggedInOffice,
            List<OfficeBO> branchParents) throws Exception {

        OfficePersistence officePersistence = new OfficePersistence();
        OfficeBO officeBO = officePersistence.getOffice(userContext.getBranchId());

        List<OfficeBO> officesTillBranchOffice = officePersistence.getOfficesTillBranchOffice(officeBO.getSearchId());

        return getOfficeList(userContext, levels, loggedInOffice, branchParents, officesTillBranchOffice);
    }

    String getOfficeList(UserContext userContext, List<OfficeView> levels, OfficeBO loggedInOffice,
            List<OfficeBO> branchParents, List<OfficeBO> officesTillBranchOffice) throws OfficeException {
        String termForBranch = "";
        String regional = "";
        String subregional = "";
        String area = "";
        for (OfficeView level : levels) {
            if (level.getLevelId().equals(OfficeLevel.BRANCHOFFICE.getValue()))
                termForBranch = level.getLevelName();
            else if (level.getLevelId().equals(OfficeLevel.AREAOFFICE.getValue()))
                area = level.getLevelName();
            else if (level.getLevelId().equals(OfficeLevel.REGIONALOFFICE.getValue()))
                regional = level.getLevelName();
            else if (level.getLevelId().equals(OfficeLevel.SUBREGIONALOFFICE.getValue()))
                subregional = level.getLevelName();
        }

        XmlBuilder result = new XmlBuilder();
        if (onlyBranchOffices != null) {
            getBranchOffices(result, branchParents, userContext, loggedInOffice, termForBranch);
        } else {
            getAboveBranches(result, officesTillBranchOffice, regional, subregional, area);
            getBranchOffices(result, branchParents, userContext, loggedInOffice, termForBranch);
        }

        return result.getOutput();
    }

    void getBranchOffices(XmlBuilder html, List<OfficeBO> officeList, UserContext userContext, OfficeBO loggedInOffice,
            String branchName) throws OfficeException {
        html.singleTag("br");

        html.startTag("span", "class", "fontnormalBold");
        html.text(branchName);
        html.endTag("span");

        html.singleTag("br");
        if (officeList == null) {
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.OFFICERESOURCEPATH, userContext
                    .getPreferredLocale());
            html.startTag("span", "class", "fontnormal");
            html.text(resources.getString("Office.labelNo"));
            html.text(" ");
            html.text(branchName.toLowerCase());
            html.text(" ");
            html.text(resources.getString("Office.labelPresent"));
            html.endTag("span");
        } else {
            for (int i = 0; i < officeList.size(); i++) {
                OfficeBO officeParent = officeList.get(i);
                // Set<OfficeBO> branchList =
                // officeParent.getBranchOnlyChildren();
                Set<OfficeBO> branchList = retrieveDataScopeBranches(officeParent.getBranchOnlyChildren(),
                        loggedInOffice);
                if (branchList.size() > 0) {
                    if (i > 0) {
                        html.singleTag("br");
                    }

                    html.startTag("span", "class", "fontnormal");
                    html.text(officeParent.getOfficeName());
                    html.endTag("span");

                    if (null != branchList) {
                        html.startTag("table", "width", "90%", "border", "0", "cellspacing", "0", "cellpadding", "0");
                        for (OfficeBO office : branchList) {
                            html.startTag("tr", "class", "fontnormal");

                            bullet(html);

                            html.startTag("td", "width", "99%");
                            html.append(getLink(office.getOfficeId(), office.getOfficeName()));
                            html.endTag("td");
                            html.endTag("tr");
                        }
                        html.endTag("table");
                    }
                }
            }
        }
    }

    private Set<OfficeBO> retrieveDataScopeBranches(Set<OfficeBO> branchOnlyChildren, OfficeBO loggedInOffice)
            throws OfficeException {
        Set<OfficeBO> dataScopeBranches = new HashSet<OfficeBO>();

        if (branchOnlyChildren.size() > 0) {
            for (OfficeBO dataScopeBranch : branchOnlyChildren) {
                if (dataScopeBranch.getSearchId().startsWith(loggedInOffice.getSearchId())
                        && dataScopeBranch.isActive()) {
                    dataScopeBranches.add(dataScopeBranch);
                }
            }
        }
        return dataScopeBranches;
    }

    XmlBuilder getLink(Short officeId, String officeName) {
        String urlencodedOfficeName = replaceSpaces(officeName);
        XmlBuilder builder = new XmlBuilder();
        String url = (actionName + "?method=" + methodName + "&office.officeId=" + officeId + "&office.officeName="
                + urlencodedOfficeName + "&officeId=" + officeId + "&officeName=" + urlencodedOfficeName
                + "&currentFlowKey=" + flowKey);
        builder.startTag("a", "href", url);
        builder.text(officeName);
        builder.endTag("a");
        return builder;
    }

    public String replaceSpaces(String officeName) {
        return officeName.trim().replaceAll(" ", "%20");
    }

    void getAboveBranches(XmlBuilder html, List<OfficeBO> officeList, String regional, String subregional, String area)
            throws OfficeException {
        if (null != officeList) {

            XmlBuilder regionalHtml = null;
            XmlBuilder subregionalHtml = null;
            XmlBuilder areaHtml = null;

            for (int i = 0; i < officeList.size(); i++) {
                OfficeBO office = officeList.get(i);
                if (office.getOfficeLevel() == OfficeLevel.HEADOFFICE) {
                    html.singleTag("br");
                    html.startTag("span", "class", "fontnormalbold");
                    html.append(getLink(office.getOfficeId(), office.getOfficeName()));
                    html.singleTag("br");
                    html.endTag("span");
                } else if (office.getOfficeLevel() == OfficeLevel.REGIONALOFFICE) {
                    regionalHtml = processOffice(regionalHtml, office, regional);
                } else if (office.getOfficeLevel() == OfficeLevel.SUBREGIONALOFFICE) {
                    subregionalHtml = processOffice(subregionalHtml, office, subregional);
                } else if (office.getOfficeLevel() == OfficeLevel.AREAOFFICE) {
                    areaHtml = processOffice(areaHtml, office, area);
                }
            }

            outputLevel(html, regionalHtml);
            outputLevel(html, subregionalHtml);
            outputLevel(html, areaHtml);
        }
    }

    private void outputLevel(XmlBuilder result, XmlBuilder levelHtml) {
        if (levelHtml != null) {
            levelHtml.endTag("table");
            result.append(levelHtml);
        }
    }

    private XmlBuilder processOffice(XmlBuilder levelHtml, OfficeBO office, String levelName) {
        if (levelHtml == null) {
            levelHtml = new XmlBuilder();
            levelHtml.singleTag("br");
            levelHtml.startTag("table", "width", "95%", "border", "0", "cellspacing", "0", "cellpadding", "0");
            levelHtml.startTag("tr");

            levelHtml.startTag("td");
            levelHtml.startTag("span", "class", "fontnormalBold");
            levelHtml.text(levelName);
            levelHtml.endTag("span");
            levelHtml.endTag("td");

            levelHtml.endTag("tr");
            levelHtml.endTag("table");

            levelHtml.startTag("table", "width", "90%", "border", "0", "cellspacing", "0", "cellpadding", "0");
        }

        levelHtml.startTag("tr", "class", "fontnormal");

        bullet(levelHtml);

        levelHtml.startTag("td", "width", "99%");
        levelHtml.append(getLink(office.getOfficeId(), office.getOfficeName()));
        levelHtml.endTag("td");

        levelHtml.endTag("tr");
        return levelHtml;
    }

    private void bullet(XmlBuilder html) {
        html.startTag("td", "width", "1%");
        html.singleTag("img", "src", "pages/framework/images/bullet_circle.gif", "width", "9", "height", "11");
        html.endTag("td");
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }
}
