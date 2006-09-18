package org.mifos.application.rolesandpermission.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class RolesPermissionsActionForm extends BaseActionForm {
	
	private String name;
	
	private Map<String, String> activities = new HashMap<String, String>();

	private Map<String, String> getActivities() {
		return activities;
	}

	private void setActivities(Map<String, String> activities) {
		this.activities = activities;
	}

	private String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public void setActivity(String key, String value) {
		this.activities.put(key, value);
	}

	@Override
	public void reset(ActionMapping mapping, ServletRequest request) {
		name = null;
		activities.clear();
		activities = null;
		super.reset(mapping, request);
	}

}
