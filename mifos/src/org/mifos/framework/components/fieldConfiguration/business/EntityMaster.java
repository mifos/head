package org.mifos.framework.components.fieldConfiguration.business;

import org.mifos.framework.persistence.Persistence;

public class EntityMaster extends Persistence {

	private Short id;

	private String entityType;

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

}
