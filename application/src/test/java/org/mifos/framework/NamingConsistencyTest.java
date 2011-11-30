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

package org.mifos.framework;

import static org.junit.Assert.fail;

import java.io.IOException;
import org.junit.Test;
import org.mifos.platform.util.ClassUtils;

public class NamingConsistencyTest {

    /**
     * test Consistency of test's name according to surefire-maven-plugin settings <br>
     * <br>
     * see <a href=http://www.mifos.org/developers/wiki/MavenIntegrationTests>
     * http://www.mifos.org/developers/wiki/MavenIntegrationTests</a>
     */
    @Test
    public void integrationTestsNameCheck() throws ClassNotFoundException, IOException {
        for (Class<?> clazz : ClassUtils.getClasses("org.mifos", "Test")) {
            final String clazzName = clazz.getName();
            if (isNotMatchingConvension(clazzName) && isMifosTestCase(clazz)) {
                 fail(clazz + " Integration test naming convension voilation");
            }
        }
    }

    private boolean isNotMatchingConvension(final String clazzName) {
        return !clazzName.endsWith("IntegrationTest") && !clazzName.endsWith("StrutsTest");
    }

    private boolean isMifosTestCase(final Class<?> clazz) {
        return clazz.getSuperclass().equals(MifosIntegrationTestCase.class)
                || clazz.getSuperclass().equals(MifosMockStrutsTestCase.class);
    }
}
