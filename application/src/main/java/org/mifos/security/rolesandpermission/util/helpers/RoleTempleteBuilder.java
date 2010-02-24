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

/**
 * 
 */
package org.mifos.security.rolesandpermission.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class build the templete for the activities in the system so that user
 * can create of see what all activities are associated with the given role
 */
public class RoleTempleteBuilder {

    /** ***********************************Fields******************** */
    /**
     * This would hold the index map of activity id to index in activities list
     */
    private HashMap<Short, Short> indexMap = new HashMap<Short, Short>();

    Short localeId;
    /**
     * This would hold the activites of currently selected activity
     */

    private Set<Short> currentActivites = new HashSet<Short>();

    /**
     * This would hold the any place holder activities if needs to be checked in
     * ui
     */

    private Set<Short> checkedLinks = new HashSet<Short>();

    /**
     * This would hold the childern associated with the given activity
     */

    private HashMap<Short, List<ActivityEntity>> childMap = new HashMap<Short, List<ActivityEntity>>();

    /**
     * Helper varibale used to give name to the checkboxes in the ui
     */
    private int nameHelper = 0;

    private Integer maxDepth = 0;

    /**
     * Default constructor
     */
    public RoleTempleteBuilder() {
    }

    /**
     * This Function returns the currentActivites
     * 
     * @return Returns the currentActivites.
     */
    public Set<Short> getCurrentActivites() {
        return currentActivites;
    }

    /**
     * This function set the currentActivites
     * 
     * @param currentActivites
     *            The currentActivites to set.
     */
    public void setCurrentActivites(Set<Short> currentActivites) {
        this.currentActivites = currentActivites;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    /**
     * This is internal helpher function used to build the templete this give
     * the childern associated with the current id
     * 
     * @param activities
     *            List of all Activity object in the system
     * @param id
     *            activity id whose childern we are finding
     * @return List of childern associated with the current activity
     */
    private List<ActivityEntity> getChildren(List<ActivityEntity> activities, Short id) {
        List<ActivityEntity> l = new ArrayList<ActivityEntity>();

        for (int i = 0; i < activities.size(); i++) {

            // if id=0 then we are looking for top level activities
            ActivityEntity parent = activities.get(i).getParent();
            if (id.shortValue() == 0) {

                if (null == parent) {
                    l.add(activities.get(i));
                }

            } else {

                if (null != parent) {
                    if (parent.getId().shortValue() == id.shortValue()) {
                        l.add(activities.get(i));
                    }
                }

            }
        }

        return l;
    }

    /**
     * This function is recursive function helps in making the tree table
     * 
     * @param l
     *            List of all activities in the system
     * @param aid
     *            current activity id
     * @param buff
     *            StringBuffer which would hold the table
     * @param level
     *            level of recursion
     * @param name
     *            name to be given to control
     */

    private void makeTable(List<ActivityEntity> l, Short aid, StringBuilder buff, int level, String name) {
        List<ActivityEntity> lst = childMap.get(aid);
        if (lst.size() > 0) {

            for (int i = 0; i < lst.size(); i++) {
                buff.append("<tr >");
                String name1 = name + "_" + Integer.toString(i);
                for (int j = 0; j < level; j++) {
                    buff.append("<td bgcolor=\"#FFFFFF\" class=\"paddingleft05BottomBorder\">&nbsp; </td> ");

                }
                buff.append("<td width=\"3%\" bgcolor=\"#FFFFFF\" class=\"paddingleft05BottomBorder\"><input name=\""
                        + "activity(" + nameHelper++ + ")\"" + "  id=\"" + name1 + "\" type=\"checkbox\" ");
                // check whether it is leaf

                List<ActivityEntity> li = childMap.get(lst.get(i).getId());
                Short index = getIndex(lst.get(i).getId());
                if (li.size() == 0) {
                    buff.append("value=\"" + lst.get(i).getId().shortValue() + "\"");

                } else {
                    buff.append(" value=\"checkbox\"");
                }

                // check whether checked or not
                if (currentActivites.contains(lst.get(i).getId())) {
                    buff.append("  checked=true ");
                } else if (checkedLinks.contains(lst.get(i).getId())) {
                    buff.append("  checked=true ");
                }

                buff.append(" onclick=\"doCheck(this)\" ></td>");
                buff.append("<td colspan=\"");
                // adding colspan for proper formatting
                buff.append(maxDepth - level + "\"  ");

                buff.append("bgcolor=\"#FFFFFF\" class=\"paddingleft05BottomBorder\"  ><span class=\"fontnormal\">");

                buff.append(l.get(index).getActivityName());
                buff.append("</span></td>");
                buff.append("<td width=\"50%\" bgcolor=\"#FFFFFF\" class=\"paddingleft05BottomLeftBorder\"  >"
                        + "<span class=\"fontnormal\">");

                if (null == l.get(index).getDescription()) {
                    buff.append(" &nbsp; ");
                } else {

                    buff.append(l.get(index).getDescription());
                }

                buff.append("</span></td></tr>");
                makeTable(l, lst.get(i).getId(), buff, level + 1, name1);
            }

        }

    }

    /**
     * This is the main function which would build the table tree for ui
     * 
     * @param l
     *            List if all the activities in the system
     * @return stringbuffer representatiuon of the tree
     */
    public StringBuilder getRolesTemplete(List<ActivityEntity> l) {
        nameHelper = 0;
        // StringBuffer buff = new StringBuffer();
        StringBuilder buff = new StringBuilder();
        // first build the map
        for (short k = 0; k < l.size(); k++) {

            indexMap.put(l.get(k).getId(), k);
        }

        // make checkmap

        buildCheckedItems(l);
        // by this time child map has been built
        short start = 0;
        List<ActivityEntity> li = childMap.get(Short.valueOf(start));

        buff
                .append("<script language=\"javascript\" src=\"pages/application/rolesandpermission/js/checkBoxLogic.js\"  type=\"text/javascript\">   </script>");

        for (int i = 0; i < li.size(); i++) {
            String name = Integer.toString(i);
            Short index = getIndex(li.get(i).getId());

            buff
                    .append("<table width=\"95%\" border=\"0\" cellpadding=\"2\" cellspacing=\"0\" class=\"bluetableborder\">");
            buff.append("<tr class=\"bluetablehead05\">");
            buff.append("<td width=\"3%\"  ><input name=\"" + "activity(" + nameHelper++ + ")\""

            + " id=\"" + name + "\" type=\"checkbox\" value=\"checkbox\" ");

            if (checkedLinks.contains(l.get(index).getId())) {
                buff.append("  checked=true ");
            }

            buff.append("onclick=\"doCheck(this)\" > </td>");
            buff.append("<td colspan=\"");
            buff.append(maxDepth + 2 + "\"  >");
            buff.append("<span class=\"fontnormalbold\">");

            buff.append(l.get(index).getActivityName());
            buff.append("</span></td></tr>");
            makeTable(l, li.get(i).getId(), buff, 1, name);
            buff.append("</table><br>");
        }
        return buff;
    }

    /**
     * This is internal helper function which would return the index of current
     * activity ijn the activities list
     * 
     * @param id
     *            of the activity
     * @return index of the activity in the list
     */
    private short getIndex(Short id) {
        return indexMap.get(id);
    }

    /**
     * This is main internal helper function which would build the map of
     * checked item in the templete
     * 
     * @param l
     *            List of activities in the system
     */

    private void buildCheckedItems(List<ActivityEntity> l) {

        MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER).debug("size of list is" + l.size());

        List<ActivityEntity> li = getChildren(l, Short.valueOf("0"));
        MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER).debug("child list size " + li.size());

        childMap.put(Short.valueOf("0"), li);
        int[] currentDepth = { 0 };
        for (int i = 0; i < li.size(); i++) {
            currentDepth[0] = 0;
            makeCheckTable(l, li.get(i).getId(), currentDepth);
            if (currentDepth[0] > maxDepth.intValue()) {
                maxDepth = currentDepth[0];
            }
        }

    }

    /**
     * This is internal recursive helper function used to build the map of the
     * checked items if some activity is selected
     * 
     * @param l
     *            list of all activities in the system
     * @param id
     *            Activity id
     * @return whether checked or not
     */
    private boolean makeCheckTable(List<ActivityEntity> l, Short id, int[] currentDepth) {
        // increment current depth by one
        currentDepth[0]++;
        boolean checked = true;
        List<ActivityEntity> lst = getChildren(l, id);
        childMap.put(id, lst);
        for (int i = 0; i < lst.size(); i++) {
            // check whether it is leaf
            List<ActivityEntity> li = getChildren(l, lst.get(i).getId());
            // Short index = getIndex(lst.get(i).getId());
            if (li.size() == 0) {
                // check whether checked or not
                if (!currentActivites.contains(lst.get(i).getId())) {
                    checked = false;
                }

                childMap.put(lst.get(i).getId(), li);
            } else {
                if (!makeCheckTable(l, lst.get(i).getId(), currentDepth)) {
                    checked = false;
                }
            }
        }
        if (checked) {
            MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER).debug(
                    "item with id= " + id + "is checked");

            checkedLinks.add(id);
        }

        return checked;
    }

}
