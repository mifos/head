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

package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.components.tabletag.Table;

public class FileCacheRep {
    private Map<String, Table> fileCache = null;

    private static FileCacheRep cacheRep = new FileCacheRep();

    public static FileCacheRep getInstance() {
        return cacheRep;
    }

    private FileCacheRep() {
        fileCache = new HashMap<String, Table>();
    }

    public void addTOCacherep(String key, Table object) {
        fileCache.put(key, object);
    }

    public Table getFromCacheRep(String key) {
        return fileCache.get(key);
    }

    public boolean isKeyPresent(String key) {
        return fileCache.containsKey(key);
    }
}
