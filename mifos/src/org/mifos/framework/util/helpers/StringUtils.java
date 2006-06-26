/**

 * StringUtils.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.framework.util.helpers;

import java.util.Arrays;

/**
 * This class has got utility functions for string which would be required through out the project.
 * @author ashishsm
 *
 */
public class StringUtils {

	/**
	 * 
	 */
	public StringUtils() {
		super();
		
	}
	
	/**
	 * This method returns a padded string with the specified padding char.
	 * @param stringToBePadded - the string which is to be padded.
	 * @param paddingChar - the char with which the string is to be padded.
	 * @param finalLengthOfString - the expected final length of string.
	 * @return - returns the final padded string .
	 */
	public static String lpad(String stringToBePadded,char paddingChar,int finalLengthOfString){
		StringBuilder elevenDigitNumber = new StringBuilder();
		// get the current length of the stringToBePadded
		int currentLength = stringToBePadded.length();
		// convert the current number to an array of characters
		char [] runningNumberArray = stringToBePadded.toCharArray();
		// form a new array which holds eleven digits
		char[] totalArray = new char [finalLengthOfString];
		if(currentLength < finalLengthOfString){
			// padd the array with the padding character.
			Arrays.fill(totalArray, 0, finalLengthOfString-currentLength, paddingChar);
			
		}
		// fill the remaining part of the array with the character
		// array of the account running number.
		for(int index = 0 ; index< currentLength ; index ++){
			Arrays.fill(totalArray, finalLengthOfString-currentLength+index, finalLengthOfString-currentLength+index+1, runningNumberArray[index]);
		}
		
		return String.valueOf(totalArray);
	}

}
