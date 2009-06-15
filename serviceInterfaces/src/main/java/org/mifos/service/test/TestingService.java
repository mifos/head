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

package org.mifos.service.test;

/**
 * Acceptance tests control the application via the user interface and verify
 * results by looking at the user interface or the database.
 * <p>
 * Integration tests are also fairly "end-to-end", but they mostly avoid the
 * user interface and reach into the API much more than acceptance tests. Many
 * integration tests hit the database, but some test individual classes,
 * methods, and other non-filesystem, non-network, non-database units of code
 * (ie: true, fast "unit" tests).
 * 
 * @see TestMode
 */
public interface TestingService {
    String TEST_MODE_SYSTEM_PROPERTY = "mifos.test.mode";
    TestMode getTestMode();
    void reinitializeCaches();
}
