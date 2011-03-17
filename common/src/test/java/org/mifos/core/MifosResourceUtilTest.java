/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.Resource;

/**
 * Unit tests for MifosResourceUtil.
 */
public class MifosResourceUtilTest {

    @Test
    public void testGetResources() throws IOException {
        Resource[] found = MifosResourceUtil.getClassPathResourcesAsResources("org/mifos/core/resources/included/**/*.xml");
        assertEquals(2, found.length);
        for (Resource resource : found) {
            String f = resource.getFilename();
            if (!f.equals("y.xml") && !f.equals("z.xml")) {
                fail(resource + " should not have been returned");
            }
        }
    }
    
}
