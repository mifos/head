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

package org.mifos.config.business;

import org.mifos.config.cache.CacheRepository;
import org.mifos.config.cache.Key;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.Constants;

public class BaseConfig {

    private OfficeConfig officeConfig;
    private CacheRepository cacheRepo;
    private HierarchyManager hierarchyManager = HierarchyManager.getInstance();

    public BaseConfig(CacheRepository cacheRepo, OfficeConfig officeConfig) {
        this.cacheRepo = cacheRepo;
        this.officeConfig = officeConfig;
    }

    protected Object getValueFromCache(String key) {
        return getValueFromCache(new Key(officeConfig.getOfficeId(), key));
    }

    private Object getValueFromCache(Key key) {
        if (key.getOfficeId() == null) {
            return null;
        }
        Object obj = cacheRepo.getValueFromOfficeCache(key);
        if (obj != null) {
            return obj;
        }
        return getValueFromCache(new Key(hierarchyManager.getParentOfficeId(key.getOfficeId()), key.getKey()));
    }

    protected Short getShortValueFromCache(String key, Short defaultValue) {
        Object obj = getValueFromCache(key);
        return (obj != null) ? (Short) obj : defaultValue;
    }

    protected boolean getBooleanValueFromCache(String key, boolean defaultValue) {
        Object value = getValueFromCache(key);
        return (value != null) ? (getBooleanValue((Short) value)) : defaultValue;
    }

    protected String getStringValueFromCache(String key, String defaultValue) {
        Object value = getValueFromCache(key);
        return (value != null && ((String) value) != "") ? ((String) value) : defaultValue;
    }

    private boolean getBooleanValue(Short value) {
        return value.equals(Constants.YES) ? Boolean.TRUE : Boolean.FALSE;
    }
}
