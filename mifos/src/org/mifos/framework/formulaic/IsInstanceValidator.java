package org.mifos.framework.formulaic;

public class IsInstanceValidator extends BaseValidator {
	
	public static final String WRONG_TYPE_ERROR = "errors.formulaic.IsInstanceValidator.wrongtype";
	
	Class rightClass;
	
	public IsInstanceValidator(Class rightClass) {
		assert rightClass != null;
		this.rightClass = rightClass;
	}

	@Override
	public Object validate(Object value) throws ValidationError {
		checkNull(value);
		if (rightClass.isInstance(value)) {
			return value;
		}
		else {
			throw new ValidationError(value, WRONG_TYPE_ERROR);
		}
	}

}
