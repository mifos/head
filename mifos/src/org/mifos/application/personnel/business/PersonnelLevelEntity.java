package org.mifos.application.personnel.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;

/**
 * See also {@link PersonnelLevel}.
 */
public class PersonnelLevelEntity extends MasterDataEntity {

	private Short interactionFlag;

	private final PersonnelLevelEntity parent;

	public PersonnelLevelEntity(PersonnelLevel level) {
		super(level.getValue());
		this.parent = null;
	}

	protected PersonnelLevelEntity() {
		super();
		this.parent = null;

	}

	public boolean isInteractionFlag() {
		return this.interactionFlag > 0;
	}

	public PersonnelLevelEntity getParent() {
		return parent;
	}
}
