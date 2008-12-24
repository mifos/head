package org.mifos.framework.formulaic;

import java.math.BigDecimal;

public class NumberValidator extends IsInstanceValidator {
	
	public NumberValidator() {
		super(String.class);
	}
	
	public NumberValidator(BigDecimal min, BigDecimal max) {
		this();
		this.min = min;
		this.max = max;
	}
	
	private BigDecimal min;
	
	private BigDecimal max;
	
	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	@Override
	public BigDecimal validate(Object input) throws ValidationError {
		String inputString = (String) super.validate(input);
		try {
			BigDecimal value = new BigDecimal(inputString);
			if (max != null && max.compareTo(value) < 0) {
				throw makeError(input, ErrorType.NUMBER_OUT_OF_RANGE);
			}
			if (min != null && min.compareTo(value) > 0) {
				throw makeError(input, ErrorType.NUMBER_OUT_OF_RANGE);
			}
			return value;
		}
		catch (NumberFormatException e) {
			throw makeError(input, ErrorType.INVALID_NUMBER);
		}
	}

}
