package org.mifos.framework.formulaic;

import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class PersonnelValidator extends BaseValidator {
	
	private PersonnelPersistence persistence;
	
	public PersonnelValidator() {
		persistence = new PersonnelPersistence();
	}

	@Override
	public Object validate(Object value) throws ValidationError {
		Object personnel = null;
		try {
			String inputValue = (String) value;
			personnel = persistence.getPersonnel(inputValue);
			if (personnel == null) // input not valid username
				personnel = persistence.getPersonnelByGlobalPersonnelNum(inputValue);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
		if (personnel == null)
			throw makeError(value, ErrorType.INVALID_PERSONNEL);
		else
			return personnel;
	}

}
