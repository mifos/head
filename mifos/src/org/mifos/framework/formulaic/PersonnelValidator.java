package org.mifos.framework.formulaic;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class PersonnelValidator extends BaseValidator {
	
	private PersonnelPersistence persistence;
	
	public static final String INVALID_PERSONNEL = "errors.formulaic.PersonnelValidator.invalidpersonnel";
	
	public PersonnelValidator() {
		persistence = new PersonnelPersistence();
	}

	@Override
	public PersonnelBO validate(Object value) throws ValidationError {
		if (value == null || ((String)value).trim().equals(""))
			return null;
		
		PersonnelBO personnel = null;
		try {
			String inputValue = (String) value;
			personnel = persistence.getPersonnel(inputValue);
			if (personnel == null) // input not valid username
				personnel = persistence.getPersonnelByGlobalPersonnelNum(inputValue);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
		if (personnel == null) {
			throw makeError(value, ErrorType.INVALID_PERSONNEL);
		} else
			return personnel;
	}

}
