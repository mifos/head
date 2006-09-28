/**

 * StringUtils.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class has got utility functions for string which would be required
 * through out the project.
 * 
 */
public class StringUtils {
	public StringUtils() {
	}

	public static String lpad(String stringToBePadded, char paddingChar,
			int finalLengthOfString) {
		int currentLength = stringToBePadded.length();
		char[] runningNumberArray = stringToBePadded.toCharArray();
		char[] totalArray = new char[finalLengthOfString];
		if (currentLength < finalLengthOfString) {
			Arrays.fill(totalArray, 0, finalLengthOfString - currentLength,
					paddingChar);
		}
		for (int index = 0; index < currentLength; index++) {
			Arrays.fill(totalArray,
					finalLengthOfString - currentLength + index,
					finalLengthOfString - currentLength + index + 1,
					runningNumberArray[index]);
		}

		return String.valueOf(totalArray);
	}

	public static boolean isNullAndEmptySafe(String stringToBeChecked) {
		return isNullSafe(stringToBeChecked) && !isEmpty(stringToBeChecked);
	}

	public static boolean isNullOrEmpty(String stringToBeChecked) {
		return !isNullSafe(stringToBeChecked) || isEmpty(stringToBeChecked);
	}

	public static boolean isNullSafe(String stringToBeChecked) {
		return stringToBeChecked != null;
	}

	public static boolean isEmpty(String stringToBeChecked) {
		return "".equals(stringToBeChecked.trim());
	}

	public static String getMessageWithSubstitution(String bundleName,
			Locale userLocale, String key, Object[] args) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName,
				userLocale);
		String label = resourceBundle.getString(key);
		if (args != null) {
			MessageFormat formatter = new MessageFormat(label);
			label = formatter.format(args);
		}
		return label;
	}

	public static String normalizeSearchString(String searchString) {
		String searchStr = searchString.trim();
		if (searchString.contains("%") && searchString.length() > 1)
			return searchStr.replace("%", "\\%");
		else
			return searchStr;

	}
}
