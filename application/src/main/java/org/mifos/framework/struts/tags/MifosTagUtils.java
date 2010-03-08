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

package org.mifos.framework.struts.tags;

public class MifosTagUtils {

    /**
     * There's no need for an instance of this class.
     */
    private MifosTagUtils() {
    }

    /**
     * Filter the specified string for characters that are sensitive to HTML/XML
     * interpreters, returning the string with these characters replaced by the
     * corresponding character entities.
     *
     * @param input
     *            The string to be filtered and returned
     */
    public static String xmlEscape(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }

        StringBuilder result = null;
        String filtered = null;
        for (int i = 0; i < input.length(); i++) {
            filtered = null;
            switch (input.charAt(i)) {
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
                    result = new StringBuilder(input.length() + 50);
                    if (i > 0) {
                        result.append(input.substring(0, i));
                    }
                    result.append(filtered);
                }
            } else {
                if (filtered == null) {
                    result.append(input.charAt(i));
                } else {
                    result.append(filtered);
                }
            }
        }

        return result == null ? input : result.toString();
    }

}
