package org.mifos.application.rolesandpermission.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;

public class RoleBO extends BusinessObject {

	private final Short id=null;
	private String name;
	private final Set<RoleActivityEntity> activities=new HashSet<RoleActivityEntity>();
	
	MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ROLEANDPERMISSIONLOGGER);
	
	protected RoleBO(){
	}

	public Short getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ActivityEntity> getActivities() {
		List<ActivityEntity> activityList=new ArrayList<ActivityEntity>();
		for(RoleActivityEntity roleActivityEntity :  activities){
			activityList.add(roleActivityEntity.getActivity());
		}
		return activityList;
	}

	
	
	
}
