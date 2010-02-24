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

/**
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class OfficeConfig {

    private AccountConfig accountConfig;
    private Short officeId;

    public OfficeConfig(CacheRepository cacheRepo, Short officeId) {
        this.officeId = officeId;
        accountConfig = new AccountConfig(cacheRepo, this);
    }

    public AccountConfig getAccountConfig() {
        return accountConfig;
    }

    public Short getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }
}
