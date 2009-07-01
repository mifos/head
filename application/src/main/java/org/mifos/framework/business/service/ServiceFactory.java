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

package org.mifos.framework.business.service;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class ServiceFactory {

    private ServiceFactory() {
    }

    private static ServiceFactory instance = new ServiceFactory();

    private Map<BusinessServiceName, BusinessService> businessServicesMap = new HashMap<BusinessServiceName, BusinessService>();

    public static ServiceFactory getInstance() {
        return instance;
    }

    /**
     * @deprecated Call the constructor of the business service directly. For
     *             example, <code>new ConfigurationBusinessService();</code>
     */
    public BusinessService getBusinessService(BusinessServiceName key) throws ServiceUnavailableException {
        if (!businessServicesMap.containsKey(key)) {
            try {
                businessServicesMap.put(key, (BusinessService) Class.forName(key.getName()).newInstance());
            } catch (Exception cnfe) {
                throw new ServiceUnavailableException(ExceptionConstants.SERVICEEXCEPTION, cnfe);
            }
        }
        return businessServicesMap.get(key);
    }

}
