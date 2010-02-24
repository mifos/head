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

package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;

public class FieldConfigurationHelper {

    public static String getLocalSpecificFieldNames(String fieldName, UserContext userContext) {
        try {
            String configuredLabel = getConfiguredFieldName(fieldName, userContext);
            if (configuredLabel != null) {
                return configuredLabel;
            }
            PropertyResourceBundle propertyString = (PropertyResourceBundle) PropertyResourceBundle.getBundle(
                    FilePaths.FIELD_CONF_PROPERTYFILE, userContext.getPreferredLocale());
            return propertyString.getString(fieldName);
        } catch (MissingResourceException e) {
            /*
             * I think the theory here is that it is better to show the user
             * something, than just make it an internal error. Not sure whether
             * that is what is going on for sure, though.
             */
            return fieldName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getConfiguredFieldName(String fieldName, UserContext userContext) {
        try {
            String labelName = fieldName.substring(fieldName.indexOf(".") + 1);
            labelName = MifosConfiguration.getInstance().getLabel(labelName, userContext.getPreferredLocale());
            if (labelName != null) {
                return labelName;
            } else {
                return null;
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
