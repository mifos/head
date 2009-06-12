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

package org.mifos.framework.components.logger;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class extends from the pattern parser. This class overrides the
 * finalizeConverter method to enable the logging of the class name, method name
 * and line number from where the log statement was issued with respect to the
 * mifos logger
 */
public class MifosPatternParser extends PatternParser {
    static final int FULL_LOCATION_CONVERTER = 1000;
    static final int METHOD_LOCATION_CONVERTER = 1001;
    static final int CLASS_LOCATION_CONVERTER = 1002;
    static final int LINE_LOCATION_CONVERTER = 1003;

    /**
     * Constructor: Passes the pattern string to the superclass PatternParser
     * 
     * @param arg0
     */
    public MifosPatternParser(String pattern) {
        super(pattern);

    }

    /**
     * Overridden method. If the specifier is 'L' it indicates the line number
     * from where the log statement was issued If 'M' indicates the method name,
     * 'C' indicates the class name. The MifosLocationPatternConverter will call
     * the required method based on whether the line number, method name or
     * class name is needed
     * 
     * @param c
     *            The specifier
     */
    @Override
    protected void finalizeConverter(char c) {
        PatternConverter pc = null;
        switch (c) {
        // indicated line number specifier
        case 'L':
            pc = new MifosLocationPatternConverter(formattingInfo, LINE_LOCATION_CONVERTER);
            currentLiteral.setLength(0);
            break;
        // indicated method name specifier
        case 'M':
            pc = new MifosLocationPatternConverter(formattingInfo, METHOD_LOCATION_CONVERTER);
            currentLiteral.setLength(0);
            break;
        // indicates class name specifier
        case 'C':
            pc = new MifosLocationPatternConverter(formattingInfo, CLASS_LOCATION_CONVERTER, extractPrecisionOption());
            currentLiteral.setLength(0);
            break;
        // any oter specifier the superclas method is called
        default:
            super.finalizeConverter(c);
            return;
        }
        // adds the convertor
        addConverter(pc);
    }

    /**
     * This class extends from the pattern convertor. This calls the convert
     * method. The convert method makes the decision as to whether it should
     * return the line number or method name or the class name
     */
    private class MifosLocationPatternConverter extends PatternConverter {
        /*
         * Denotes whether the line number , method name or class name has to be
         * returned
         */
        int type;
        /* Denotes the precision for displaying the class name */
        int precision;

        public MifosLocationPatternConverter(FormattingInfo formattingInfo, int type) {

            super(formattingInfo);
            this.type = type;
        }

        public MifosLocationPatternConverter(FormattingInfo formattingInfo, int type, int precision) {

            super(formattingInfo);
            this.type = type;
            this.precision = precision;
        }

        @Override
        protected String convert(LoggingEvent event) {
            // The MifosLogger is passed as the FQN.
            LocationInfo locationInfo = new LocationInfo(new Throwable(),
                    "org.mifos.framework.components.logger.MifosLogger");
            switch (type) {
            // calls the method to return method name
            case METHOD_LOCATION_CONVERTER:
                return locationInfo.getMethodName();
                // calls the method to return line number
            case LINE_LOCATION_CONVERTER:
                return locationInfo.getLineNumber();
                // calls the method to return class name
            case CLASS_LOCATION_CONVERTER:
                String n = locationInfo.getClassName();
                // depending on the precision, the rightmost token is retrieved.
                // If %C{1} and the class name is
                // org.mifos.framework.components.logger.MifosPattern, then the
                // display would show MifosPattern as class name
                // if it is %C{2} then logger.MifosPattern is shown.
                if (precision <= 0)
                    return n;
                else {
                    int len = n.length();

                    // We substract 1 from 'len' when assigning to 'end' to
                    // avoid out of
                    // bounds exception in return r.substring(end+1, len). This
                    // can happen if
                    // precision is 1 and the category name ends with a dot.
                    int end = len - 1;
                    for (int i = precision; i > 0; i--) {
                        end = n.lastIndexOf('.', end - 1);
                        if (end == -1)
                            return n;
                    }
                    return n.substring(end + 1, len);
                }
            default:
                return null;
            }
        }

    }

}
