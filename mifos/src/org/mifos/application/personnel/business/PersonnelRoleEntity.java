

package org.mifos.application.personnel.business;

import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.business.PersistentObject;

public class PersonnelRoleEntity extends PersistentObject {

	private final Integer personnelRoleId;

	private final Role role;

	private final PersonnelBO personnel;

	protected PersonnelRoleEntity() {
		super();
		this.personnelRoleId=null;
		this.personnel=null;
		this.role=null;

	}
	public PersonnelRoleEntity(Role role, PersonnelBO personnel) {
		super();
		this.role = role;
		this.personnel = personnel;
		this.personnelRoleId=null;
	}
	public Integer getPersonnelRoleId() {
		return personnelRoleId;
	}
	public Role getRole() {
		return role;
	}
	public PersonnelBO getPersonnel() {
		return personnel;
	}

}
