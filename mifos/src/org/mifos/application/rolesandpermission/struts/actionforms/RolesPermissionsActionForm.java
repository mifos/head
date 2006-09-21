package org.mifos.application.rolesandpermission.struts.actionforms;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class RolesPermissionsActionForm extends BaseActionForm {
	
	private String id;
	
	private String name;
	
	private Map<String, String> activities = new HashMap<String, String>();

	public Map<String, String> getActivities() {
		return activities;
	}

	public void setActivities(Map<String, String> activities) {
		this.activities = activities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setActivity(String key, String value) {
		this.activities.put(key, value);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
