

package org.mifos.application.personnel.business;

import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.business.PersistentObject;

public class PersonnelRoleEntity extends PersistentObject {

	private final Integer personnelRoleId;

	private final RoleBO role;

	private final PersonnelBO personnel;

	protected PersonnelRoleEntity() {
		super();
		this.personnelRoleId=null;
		this.personnel=null;
		this.role=null;

	}
	public PersonnelRoleEntity(RoleBO role, PersonnelBO personnel) {
		super();
		this.role = role;
		this.personnel = personnel;
		this.personnelRoleId=null;
	}
	public Integer getPersonnelRoleId() {
		return personnelRoleId;
	}
	public RoleBO getRole() {
		return role;
	}
	public PersonnelBO getPersonnel() {
		return personnel;
	}

}
