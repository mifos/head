/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.config.cache;

import org.mifos.config.business.SystemConfiguration;

public class CacheRepository {

    private SystemConfiguration systemConfiguration;
    private OfficeCache officeCache;
    private static CacheRepository cacheRep = new CacheRepository();

    private CacheRepository() {
        officeCache = new OfficeCache();
    }

    public OfficeCache getOfficeCache() {
        return officeCache;
    }

    public void setOfficeCache(OfficeCache officeCache) {
        this.officeCache = officeCache;
    }

    public void setSystemConfiguration(SystemConfiguration configuration) {
        this.systemConfiguration = configuration;
    }

    public Object getValueFromOfficeCache(Key key) {
        return (key != null) ? officeCache.getElement(key) : null;
    }

    public static CacheRepository getInstance() {
        return cacheRep;
    }

    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

}
