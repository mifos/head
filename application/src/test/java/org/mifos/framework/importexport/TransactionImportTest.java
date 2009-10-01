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

package org.mifos.framework.importexport;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.junit.Ignore;

import junit.framework.TestCase;

// FIXME: remove @Ignore once these are implemented
@Ignore
public class TransactionImportTest extends TestCase {
    private TransactionImport transactionImport = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        transactionImport = new TransactionImportExample();
    }

    @Override
    protected void tearDown() throws Exception {
        transactionImport = null;
        super.tearDown();
    }
    
    public void testGoodImport() {
        assertTrue("unimplemented", false);
    }

    public void testMissingData() {
        BufferedReader input = new BufferedReader(new StringReader("blah"));
        List<String> errors = transactionImport.parseTransactions(input);
        assertEquals(1, errors.size());
    }

    public void testBadDateFormat() {
        assertTrue("unimplemented", false);
    }

    public void testMissingSerialNumber() {
        assertTrue("unimplemented", false);
    }
}
