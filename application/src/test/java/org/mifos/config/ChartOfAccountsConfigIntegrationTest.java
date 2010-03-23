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

package org.mifos.config;

import java.io.ByteArrayInputStream;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.FilePaths;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Test(groups={"integration", "configTestSuite"})
public class ChartOfAccountsConfigIntegrationTest extends MifosIntegrationTestCase {

    public ChartOfAccountsConfigIntegrationTest() throws Exception {
        super();
    }

    ChartOfAccountsConfig coa;

    @Override
    public void setUp() throws Exception {
        coa = ChartOfAccountsConfig.load(FilePaths.CHART_OF_ACCOUNTS_DEFAULT);
    }

    @Override
    public void tearDown() throws Exception {
        coa = null;
    }

    public void testCreateCoa() throws Exception {
        Assert.assertNotNull("error loading chart of accounts from " + "configuration file", coa);
    }

    public void testGetAllAccounts() throws Exception {
        Set<GLAccount> glAccounts = coa.getGLAccounts();
       Assert.assertEquals("default chart of accounts should have 56 " + "general ledger accounts", 56, glAccounts.size());
        assert true;
    }

    /**
     * Make sure that the first account in the returned set is a top-level
     * account (also known as a category). A more comprehensive unit test would
     * ensure that child accounts are never seen before their parents when
     * iterating through the {@link Set} returned from
     * {@link ChartOfAccountsConfig#getGLAccounts()}.
     */
    public void testFirstIsTopLevelAccount() throws Exception {
        GLAccount first = coa.getGLAccounts().iterator().next();
        Assert.assertNull(first.parentGlCode);
    }

    public void testGetCategory() throws Exception {
        Node category = coa.getCategory(GLCategoryType.ASSET);
        Assert.assertNotNull("failed to fetch a top-level GL account " + "(aka category)", category);

        // TODO: should this be a test? do this as a runtime check in
        // FinancialInitializer?
        String name = category.getAttributes().getNamedItem(ChartOfAccountsConfig.ACCOUNT_NAME_ATTR).getNodeValue();
       Assert.assertEquals("assets category has unexpected name", "ASSETS", name);
    }

    public void testConfigWithDupes() throws Exception {
        try {
            String invalid = "<GLAccount code=\"11100\" name=\"Petty Cash Accounts\">"
                    + "<GLAccount code=\"11101\" name=\"Cash 1\" />" + "<GLAccount code=\"11101\" name=\"Cash 1\" />"
                    + "<GLAccount code=\"11102\" name=\"Cash 2\" />" + "</GLAccount>";
            ByteArrayInputStream bstr = new ByteArrayInputStream(invalid.getBytes("UTF-8"));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = dbf.newDocumentBuilder();
            Document document = parser.parse(bstr);

            ChartOfAccountsConfig.traverse(document.getFirstChild(), null);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testTraverse() throws Exception {
        String invalid = "<GLAccount code=\"11100\" name=\"Petty Cash Accounts\">"
                + "<GLAccount code=\"AB CD\" name=\"Cash 1\" />" + "<GLAccount code=\"11102\" name=\"Cash 2\" />"
                + "</GLAccount>";
        ByteArrayInputStream bstr = new ByteArrayInputStream(invalid.getBytes("UTF-8"));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = dbf.newDocumentBuilder();
        Document document = parser.parse(bstr);

        Set<GLAccount> accounts = ChartOfAccountsConfig.traverse(document.getFirstChild(), null);
       Assert.assertEquals(3, accounts.size());

        GLAccount expected = new GLAccount();
        expected.glCode = "AB CD";
        expected.name = "Cash 1";
        expected.parentGlCode = "11100";
       Assert.assertTrue(accounts.contains(expected));

        expected = new GLAccount();
        expected.glCode = "11102";
        expected.name = "Cash 2";
        expected.parentGlCode = "11100";
       Assert.assertTrue(accounts.contains(expected));

        GLAccount unExpected = new GLAccount();
        unExpected.glCode = "11102";
        unExpected.name = "Cash 3";
        unExpected.parentGlCode = "11100";
        Assert.assertFalse(accounts.contains(unExpected));

        unExpected = new GLAccount();
        unExpected.glCode = "11199";
        unExpected.name = "Cash 2";
        Assert.assertFalse(accounts.contains(unExpected));
    }
}
