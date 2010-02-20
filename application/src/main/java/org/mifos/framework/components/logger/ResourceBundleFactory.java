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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mifos.framework.exceptions.ResourceBundleNotFoundException;

/**
 * This class is a singleton whose instance will return the resource bundle for
 * a particular locale
 */

public class ResourceBundleFactory {
    /** instance of the resource bundle factory */
    private static ResourceBundleFactory instance = new ResourceBundleFactory();
    /** The resource bundle for a particular locale */
    private static ResourceBundle resourceBundle = null;

    /** Private constructor. */
    private ResourceBundleFactory() {

    }

    /**
     * Function to obtain the resource bundle for an MfiLocale
     */
    public static ResourceBundleFactory getInstance() {

        return instance;
    }

    /**
     * Obtains the resource bundle
     * 
     * @return The resource bundle
     * @throws ResourceBundleNotFoundException
     */
    public ResourceBundle getResourceBundle(String loggerName, Locale mfiLocale) throws ResourceBundleNotFoundException {
        try {
            resourceBundle = ResourceBundle.getBundle(loggerName, mfiLocale);
        } catch (MissingResourceException mre) {
            throw new ResourceBundleNotFoundException(mre);
        }
        return resourceBundle;
    }

}
