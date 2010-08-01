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

package org.mifos.security.rolesandpermission.business;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.TestLogger;
import org.testng.annotations.Test;

public class RoleActivityEntityTest extends TestCase {

    public void testEquals() throws Exception {
        RoleBO x = new RoleBO(new TestLogger(), 78);
        RoleBO notx = new RoleBO(new TestLogger(), 77);
        RoleBO y = new RoleBO(new TestLogger(), 78);
        RoleBO z = new RoleBO(new TestLogger(), 78);

        TestUtils.assertEqualsAndHashContract(x, notx, y, z);
    }

}
