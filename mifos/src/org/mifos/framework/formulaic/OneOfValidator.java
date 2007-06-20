package org.mifos.framework.formulaic;

import java.util.Collection;
import java.util.LinkedList;

public class OneOfValidator extends BaseValidator {
	
	LinkedList foo;
	
	public static final String NOT_A_CHOICE_ERROR = "errors.formulaic.OneOfValidator.notachoice";
	private Collection choices;
	private OneOfValidatorSource source;
	
	public OneOfValidator(Collection choices) {
		assert choices != null;
		this.choices = choices;
	}
	
	public OneOfValidator(Object[] choices) {
		
		this.choices = new LinkedList();
		for (Object choice : choices) {
			System.out.println("choice: " + choice);
			this.choices.add(choice);
		}
	}
	
	public OneOfValidator(OneOfValidatorSource source) {
		assert source != null;
		this.source = source;
	}
	
	@Override
	public Object validate(Object value) throws ValidationError {
		checkNull(value);
		Collection items = source == null ? choices : source.getItems();
		if (!items.contains(value)) {
			throw new ValidationError(value, NOT_A_CHOICE_ERROR);
		}
		else {
			return value;
		}
	}

}
