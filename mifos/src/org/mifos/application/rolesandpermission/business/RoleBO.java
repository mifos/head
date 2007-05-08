package org.mifos.application.rolesandpermission.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.StringUtils;

public class RoleBO extends BusinessObject {

	private Short id = null;

	private String name;

	private final Set<RoleActivityEntity> activities = 
		new HashSet<RoleActivityEntity>();

	MifosLogger logger;

	private RolesPermissionsPersistence rolesPermissionPersistence = 
		new RolesPermissionsPersistence();

	protected RoleBO() {
		logger = MifosLogManager.getLogger(
				LoggerConstants.ROLEANDPERMISSIONLOGGER);
	}
	
	RoleBO(MifosLogger logger, int id) {
		this.logger = logger;
		this.id = (short) id;
	}

	public RoleBO(UserContext userContext, String roleName,
			List<ActivityEntity> activityList)
			throws RolesPermissionException {
		super(userContext);
		logger = MifosLogManager.getLogger(
			LoggerConstants.ROLEANDPERMISSIONLOGGER);
		logger.info("Creating a new role");
		validateRoleName(roleName);
		validateActivities(activityList);
		name = roleName;
		createRoleActivites(activityList);
		setCreateDetails();
		logger.info("New role created");
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
		List<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
		for (RoleActivityEntity roleActivityEntity : activities) {
			activityList.add(roleActivityEntity.getActivity());
		}
		return activityList;
	}
	
	public List<Short> getActivityIds() {
		List<Short> ids = new ArrayList<Short>();
		for (RoleActivityEntity roleActivityEntity : activities) {
			ids.add(roleActivityEntity.getActivity().getId());
		}
		return ids;
	}

	public void save() throws RolesPermissionException {
		logger.info("Saving role");
		try {
			rolesPermissionPersistence.createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new RolesPermissionException(e);
		}
		logger.info("Role saved");
	}
	
	public void delete() throws RolesPermissionException{
		logger.info("Deleting role");
		validateIfRoleAssignedToPersonnel();
		try {
			rolesPermissionPersistence.delete(this);
		} catch (PersistenceException e) {
			throw new RolesPermissionException(e);
		}
		logger.info("Role deleted");
	}
	
	public void update(Short perosnnelId, String roleName,
			List<ActivityEntity> activityList) throws RolesPermissionException {
		logger.info("Updating role");
		validateRoleName(roleName);
		validateActivities(activityList);
		name = roleName;
		updateRoleActivities(activityList);
		setUpdateDetails(perosnnelId);
		try {
			rolesPermissionPersistence.createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new RolesPermissionException(e);
		}
		logger.info("Role updated");
	}
	
	private void updateRoleActivities(List<ActivityEntity> activityList) {
		//Removing activities
		List<Short> activityIds=getActivityIds(activityList);
		for (Iterator<RoleActivityEntity> iter = activities.iterator(); iter.hasNext();) {
			RoleActivityEntity roleActivityEntity = iter.next();
			if(!activityIds.contains(roleActivityEntity.getActivity().getId()))
				iter.remove();
			
		}
		//Adding activities
		activityIds=getActivityIds(getActivities());
		for(ActivityEntity activityEntity :  activityList){
			if(!activityIds.contains(activityEntity.getId())){
				RoleActivityEntity roleActivityEntity = new RoleActivityEntity(
						this, activityEntity);
				activities.add(roleActivityEntity);
			}
		}
	}
	
	private List<Short> getActivityIds(List<ActivityEntity> activityList){
		List<Short> activityIds=new ArrayList<Short>();
		for(ActivityEntity activityEntity : activityList)
			activityIds.add(activityEntity.getId());
		return activityIds;
	}
	
	private void createRoleActivites(List<ActivityEntity> activityList) {
		for (ActivityEntity activityEntity : activityList) {
			RoleActivityEntity roleActivityEntity = new RoleActivityEntity(
					this, activityEntity);
			activities.add(roleActivityEntity);
		}
	}

	private void validateRoleName(String roleName)
			throws RolesPermissionException {
		logger.info("Checking rolename for empty or null");
		if (!StringUtils.isNullAndEmptySafe(roleName))
			throw new RolesPermissionException(
					RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED);
		logger.info("Checking for duplicate rolename");
		try {
			if (name == null || !name.trim().equalsIgnoreCase(roleName.trim()))
				if (rolesPermissionPersistence.getRole(roleName.trim()) != null)
					throw new RolesPermissionException(
							RolesAndPermissionConstants.KEYROLEALREADYEXIST);
		} catch (PersistenceException e) {
			throw new RolesPermissionException(e);
		}
		logger.info("Checking rolename done");
	}

	private void validateActivities(List<ActivityEntity> activityList)
			throws RolesPermissionException {
		logger.info("Validating activities");
		if (null == activityList || activityList.size() == 0)
			throw new RolesPermissionException(
					RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES);
		logger.info("Activities validated");
	}
	
	private void validateIfRoleAssignedToPersonnel() throws RolesPermissionException{
		logger.info("Validating if role is assigned to personnel");
		if(isRoleAssignedToPersonnel())
			throw new RolesPermissionException(
					RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL);
		logger.info("Validation done for role assigned to personnel");
	}
	
	private boolean isRoleAssignedToPersonnel() throws RolesPermissionException{
		Integer count;
		try {
			count = new PersonnelPersistence().getPersonnelRoleCount(id);
		} catch (PersistenceException e) {
			throw new RolesPermissionException(e);
		}
		return (null != count && count > 0) ? true : false; 
	}


}
