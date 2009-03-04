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

package org.mifos.framework.util;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DatabaseUrlDecoratorTest {
    DatabaseUrlDecorator decorator;

    @Before
    public void setUp() {
        decorator = new DatabaseUrlDecorator();
    }

    @Test
    public void testDecorateBareUrl() {
        Assert.assertEquals("blah?foo", decorator.getDecoratedDatabaseUrl("blah", "foo"));
    }

    @Test
    public void testDecorateUrlWithQueryParams() {
        Assert.assertEquals("blah?foo&amp;bar", decorator.getDecoratedDatabaseUrl("blah?foo", "bar"));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DatabaseUrlDecoratorTest.class);
    }
}
