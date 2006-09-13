package org.mifos.application.rolesandpermission.business;

import org.mifos.framework.business.PersistentObject;

public class RoleActivityEntity extends PersistentObject {

	private RoleBO role;

	private ActivityEntity activity;
	
	protected RoleActivityEntity(){
	}
	
	public RoleActivityEntity(RoleBO role, ActivityEntity activity){
		this.role=role;
		this.activity=activity;
	}

	public ActivityEntity getActivity() {
		return activity;
	}

	public RoleBO getRole() {
		return role;
	}

	@Override
	public boolean equals(Object arg0) {
		RoleActivityEntity roleActivityEntity = (RoleActivityEntity) arg0;
		if (this.getRole().getId().equals(roleActivityEntity.getRole().getId())
				&& this.getActivity().getId().equals(
						roleActivityEntity.getActivity().getId())) {
			return true;
		}
		return false;
	}
	
	

}
