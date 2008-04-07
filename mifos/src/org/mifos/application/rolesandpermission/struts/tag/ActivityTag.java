package org.mifos.application.rolesandpermission.struts.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.application.rolesandpermission.util.helpers.RoleTempleteBuilder;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class ActivityTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute(Constants.USER_CONTEXT_KEY);
		RoleTempleteBuilder builder = new RoleTempleteBuilder();
		List<ActivityEntity> activities;
		try {
			activities = (List<ActivityEntity>) SessionUtils.getAttribute(
					RolesAndPermissionConstants.ACTIVITYLIST,
					(HttpServletRequest) pageContext.getRequest());
			populateLocaleID(activities, userContext.getLocaleId());
			RoleBO role = (RoleBO) SessionUtils.getAttribute(
					Constants.BUSINESS_KEY, (HttpServletRequest) pageContext
							.getRequest());
			RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) pageContext
					.getSession().getAttribute("rolesPermissionsActionForm");
			if (rolesPermissionsActionForm != null
					&& rolesPermissionsActionForm.getActivities().size() > 0) {
				Set activitySet = convertToIdSet(getActivities(activities,
						rolesPermissionsActionForm.getActivities()));
				builder.setCurrentActivites(activitySet);
			} else if (role != null) {
				Set activitySet = convertToIdSet(role.getActivities());
				builder.setCurrentActivites(activitySet);
			}
			SessionUtils.getAttribute(Constants.BUSINESS_KEY,
					(HttpServletRequest) pageContext.getRequest());
			StringBuilder sb = builder.getRolesTemplete(activities);
			pageContext.getOut().print(sb.toString());
		} catch (IOException e) {
			throw new JspException(e);
		} catch (PageExpiredException e1) {
			throw new JspException(e1);
		}
		return EVAL_PAGE;
	}

	 void populateLocaleID(List<ActivityEntity> activities,
			Short localeId) {
		for (ActivityEntity activityEntity : activities) {
			activityEntity.setLocaleId(localeId);
		}
	}

	 static Set<Short> convertToIdSet(List<ActivityEntity> activityList) {
		Set<Short> activities = new HashSet<Short>();
		for (Iterator<ActivityEntity> iter = activityList.iterator(); iter
				.hasNext();) {
			ActivityEntity activityEntity = iter.next();
			activities.add(activityEntity.getId());
		}
		return activities;
	}

	 List<ActivityEntity> getActivities(
			List<ActivityEntity> activityList, Map<String, String> activities) {
		List<ActivityEntity> newActivityList = new ArrayList<ActivityEntity>();
		List<Short> ids = new ArrayList<Short>();
		Set<String> keys = activities.keySet();
		for (String string : keys) {
			/*
			 * We need to collect the id's of all the checked activities when we
			 * created the ui. We have given unique name to leaf activities and
			 * "chekbox" to non leaf activities .Now we are trying to get the
			 * id's of checked leafs only
			 */
			if (!activities.get(string).equalsIgnoreCase("checkbox")
					&& !activities.get(string).equalsIgnoreCase("")) {
				Short activityId = Short.parseShort(activities.get(string));
				ids.add(activityId);
			}
		}
		for (ActivityEntity activityEntity : activityList) {
			if (ids.contains(activityEntity.getId()))
				newActivityList.add(activityEntity);
		}
		return newActivityList;
	}

}
