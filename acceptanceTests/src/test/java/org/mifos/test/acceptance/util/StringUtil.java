/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.util;

import org.apache.commons.lang.RandomStringUtils;

public class StringUtil {

    public static String getRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
    
    public static String formatNumber(final String number) {
    	String num = "";
        String decimal = "";
        int index = number.lastIndexOf('.');
        
        if(index >= 0) {
            num = number.substring(0, index);
            decimal = number.substring(index);
        } else {
            num = number;
        }
        
        StringBuilder builder = new StringBuilder(num);
        
        if(num.length() > 3){
            for(int i = num.length() - 3; i >= 0; i -= 3) {
                if (i > 0 && builder.charAt(i-1) != ',') {
                	builder = builder.insert(i, ',');
                }
                else if (i == 0) {
                	builder = builder.insert(i, ',');
                }
            }
        }
        
        String check =".";
        
        for(int i = 0; i < decimal.length() - 1; i++){
        	check = check.concat("0");
        }
        
		if (!decimal.equalsIgnoreCase(check)) {
			builder = builder.append(decimal);
		}
        
        return builder.toString();
    }
}

