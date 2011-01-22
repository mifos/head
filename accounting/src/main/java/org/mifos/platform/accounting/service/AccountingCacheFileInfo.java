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

package org.mifos.platform.accounting.service;

import org.joda.time.DateTime;

public class AccountingCacheFileInfo {

    private final DateTime lastModified;

    private final String fileName;

    private final String mfiPrefix;

    public AccountingCacheFileInfo(DateTime lastModified, String mfiPrefix, String fileName) {
        super();
        this.lastModified = lastModified;
        this.fileName = fileName;
        this.mfiPrefix = mfiPrefix;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public String getLastModifiedToString() {
        return lastModified.toString("yyyy-MMM-dd HH:mm:sss z");
    }

    public String getFileName() {
        return fileName;
    }

    public String getStartDateInString() {
        return fileName.substring(0, 10);
    }

    public String getEndDateInString() {
        return fileName.substring(14, 24);
    }

    public String getMfiPrefix() {
        return mfiPrefix;
    }
}
