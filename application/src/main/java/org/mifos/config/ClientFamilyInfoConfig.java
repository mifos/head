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

package org.mifos.config;

import org.mifos.config.exceptions.ConfigurationException;

public class ClientFamilyInfoConfig{

    public static final String AreFamilyDetailsRequired = "ClientInformation.AreFamilyDetailsRequired";
    public static final String MaximumNumberOfFamilyMembers = "ClientInformation.MaximumNumberOfFamilyMembers";
    
    public static Boolean getAreFamilyDetailsRequired() throws ConfigurationException{
        //default value is false
        Boolean required=false;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(AreFamilyDetailsRequired))
             required= configMgr.getBoolean(AreFamilyDetailsRequired);
        else
            throw new ConfigurationException("The property are family details required is not set in "+ConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME);
        return required;
    }

    public static int getMaximumNumberOfFamilyMembers() throws ConfigurationException{
        //default value is 15
        int familyMembers=15;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(MaximumNumberOfFamilyMembers))
             familyMembers= configMgr.getInt(MaximumNumberOfFamilyMembers);
        else
            throw new ConfigurationException("The Maximum Number of Family Members are not defined in "+ConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME);
        return familyMembers;
    }

}
