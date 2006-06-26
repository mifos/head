/**

 * MifosTagUtils.java    version: 1.0

 

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
package org.mifos.framework.struts.tags;

/**
 * @author rajenders
 *
 */
public class MifosTagUtils {

	/**
	 * private constructor to prevent multiple instance creation.
	 */
	private MifosTagUtils() {
	}
	/**
	 * The static instance of the class
	 */

	private static MifosTagUtils instance = new MifosTagUtils();

	/**
	 * static method to obatin the instance of the class.
	 */
	public static MifosTagUtils getInstance() {
		return instance;
	}
	
	/*
	public String filter(String value) {

        if (value == null) {
            return (null);
        }

        char content[] = value.toCharArray();
        StringBuffer result = null;

        int i = 0;
        scanLoop: for (; i < content.length; i++) {
            switch (content[i]) {
                case '<':
                case '>':
                case '&':
                case '"':
                case '\'':
                    result = new StringBuffer(content.length + 50);
                    result.append(content, 0, i);
                    break scanLoop;
                default:
            }
        }
        if (result == null) {
           // No special characters found so just return the originalstring.
           return value;
        }

        for (; i < content.length; i++) {
            switch (content[i]) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '\'':
                    result.append("&#39;");
                    break;
                default:
                    result.append(content[i]);
            }
        }
        return result.toString();
    }
    */
	
    /**
     * Filter the specified string for characters that are sensitive to
     * HTML interpreters, returning the string with these characters replaced
     * by the corresponding character entities.
     *
     * @param value The string to be filtered and returned
     */
    public  String filter(String value) {

        if (value == null || value.length() == 0) {
            return value;
        }

        StringBuilder result = null;
        String filtered = null;
        for (int i = 0; i < value.length(); i++) {
            filtered = null;
            switch (value.charAt(i)) {
                case '<':
                    filtered = "&lt;";
                    break;
                case '>':
                    filtered = "&gt;";
                    break;
                case '&':
                    filtered = "&amp;";
                    break;
                case '"':
                    filtered = "&quot;";
                    break;
                case '\'':
                    filtered = "&#39;";
                    break;
            }

            if (result == null) {
                if (filtered != null) {
                    result = new StringBuilder(value.length() + 50);
                    if (i > 0) {
                        result.append(value.substring(0, i));
                    }
                    result.append(filtered);                       
                }
            } else {
                if (filtered == null) {
                    result.append(value.charAt(i));
                } else {
                    result.append(filtered);
                }
            }
        }

        return result == null ? value : result.toString();
    }

}
