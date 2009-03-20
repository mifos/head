/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.util.helpers;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class has one method validate which validates the double value 
 * against the format passed. 
 */
public class DecimalFieldHelper {

	public DecimalFieldHelper() {
		super();
	}
	
	/**
	 * This method validates that the number passed as value is less than or equal to the number
	 * formed based on the format. E.g. if the format passed is (10,3) the method would return if 
	 * the number is less than or equal to 9999999.999 else it would return false.
	 * @param value -- value to be validated.
	 * @param format -- format against which the value is to be validated e.g. (10,3) 
	 */
	public static boolean validate(Double value, String format) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Format is " + format);
		// it gets the total possible digits by getting a substring from the format 
		// the substring is from '(' to ',' characters.
		int totalDigitsPossible = Integer.parseInt(format.substring(1, format.indexOf(",")));
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("totalDigitsPossible is " + totalDigitsPossible);
		int possibleDigitsPostDecimal = Integer.parseInt(format.substring(format.indexOf(",")+1, format.length()-1));
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("possibleDigitsPostDecimal are " + possibleDigitsPostDecimal);
		int possibleDigitsBeforeDecimal = totalDigitsPossible - possibleDigitsPostDecimal;
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("possibleDigitsBeforeDecimal are " + possibleDigitsBeforeDecimal);
		
		// form a max string based on the format
		StringBuilder maxAllowableString = new StringBuilder();
		// the number of digits before decimal 	
		for(int index =0 ; index < possibleDigitsBeforeDecimal ; index ++){
			maxAllowableString.append("9");
		
		}
		if(possibleDigitsPostDecimal > 0){
			maxAllowableString.append(".");
		}
		
		// the number of digits after decimal
		for(int index =0 ; index < possibleDigitsPostDecimal ; index ++){
			maxAllowableString.append("9");
		}
		Double maxAllowable = Double.valueOf(maxAllowableString.toString());
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The expected max is " + maxAllowable);
		
		if(value <= maxAllowable){
			return true;
		}
		return false;
	}

}
