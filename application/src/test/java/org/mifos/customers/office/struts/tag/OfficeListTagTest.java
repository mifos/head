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

package org.mifos.customers.office.struts.tag;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.dom4j.DocumentException;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.office.util.helpers.OperationMode;
import org.mifos.framework.TestUtils;
import org.mifos.framework.spring.SpringUtil;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.security.util.UserContext;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class OfficeListTagTest extends TestCase {

    private XmlBuilder result;

    private UserContext userContext;

    private OfficeBO head;

    private OfficeBO regional;

    private OfficeBO branch;

    private OfficeBO branch2;

    public OfficeListTagTest() {
        super();
        initialize();
    }

    public OfficeListTagTest(String name) {
        super(name);
        initialize();
    }

    private void initialize() {
        SpringUtil.initializeSpring();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        result = new XmlBuilder();
        userContext = TestUtils.makeUser();
    }

    public void testNoBranches() throws Exception {
        new OfficeListTag().getBranchOffices(result, null, userContext.getPreferredLocale(), null, "Branch");
        assertWellFormedFragment(result.toString());
    }

    public void testBranches() throws Exception {
        createSomeOffices();
        Assert.assertEquals(2, regional.getBranchOnlyChildren().size());
        List<OfficeBO> officeList = new ArrayList<OfficeBO>();
        officeList.add(regional);

        List<OfficeHierarchyDto> officeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(officeList);

        new OfficeListTag().getBranchOffices(result, officeHierarchy, userContext.getPreferredLocale(), branch
                .getSearchId(), "Branch");
        String html = result.toString();
        assertWellFormedFragment(html);
        StringAssert.assertContains("Trinidad&amp;Tobago", html);
        StringAssert.assertNotContains("TheGambia", html);
    }

    public void testNothingAboveBranches() throws Exception {
        new OfficeListTag().getAboveBranches(result, null, null, null, null);
       Assert.assertEquals("", result.toString());
    }

    public void testAboveBranches() throws Exception {
        List<OfficeBO> offices = new ArrayList<OfficeBO>();
        offices.add(makeOffice("Trinidad&Tobago", OfficeLevel.HEADOFFICE));
        offices.add(makeOffice("Ontario&Quebec", OfficeLevel.REGIONALOFFICE));
        offices.add(makeOffice("Alberta", OfficeLevel.REGIONALOFFICE));
        offices.add(makeOffice("Vancouver", OfficeLevel.SUBREGIONALOFFICE));
        offices.add(makeOffice("Toronto&Ottawa", OfficeLevel.SUBREGIONALOFFICE));
        offices.add(makeOffice("SoHo", OfficeLevel.AREAOFFICE));
        offices.add(makeOffice("Tribeca&Chelsea", OfficeLevel.AREAOFFICE));

        new OfficeListTag().getAboveBranches(result, offices, "Province <State>", "County <Duchy>", "Parish <City>");
        String html = result.toString();
        assertWellFormedFragment(html);
        StringAssert.assertContains("Toronto&amp;Ottawa", html);
    }

    public void testAssertWellFormed() throws Exception {
        assertWellFormedFragment("<foo />");
        assertWellFormedFragment("x y z");
        assertWellFormedFragment("x <p>this <b>shows it off</b></p> z");
        try {
            assertWellFormedFragment("<unclosed>");
            Assert.fail("Didn't get assertion failure");
        } catch (DocumentException e) {

        }
    }

    public void testGetLink() throws Exception {
        OfficeListTag tag = new OfficeListTag("action", "method", "flow");
        XmlBuilder link = tag.getLink((short) 234, "My Office");
       Assert.assertEquals("<a href=\"action?method=method&amp;"
                + "office.officeId=234&amp;office.officeName=My%20Office&amp;"
                + "officeId=234&amp;officeName=My%20Office&amp;" + "currentFlowKey=flow\">My Office</a>", link
                .getOutput());
    }

    public void testGetOfficeListOnlyBranchs() throws Exception {
        createSomeOffices();
        OfficeListTag tag = new OfficeListTag("action", "method", "flow");
        tag.setOnlyBranchOffices("onlybranchOffices");

        List<OfficeHierarchyDto> officeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(Collections.singletonList(regional));

        String html = tag.getOfficeList(userContext.getPreferredLocale(), headRegionalBranch(), branch.getSearchId(),
                officeHierarchy, headRegional());
        StringAssert.assertNotContains("East&amp;West Indies", html);
        StringAssert.assertContains("West Indies Only", html); // is this right?
        StringAssert.assertContains("Trinidad&amp;Tobago", html);
        StringAssert.assertNotContains("TheGambia", html);
    }

    public void testGetOfficeListAllOffices() throws Exception {
        createSomeOffices();
        OfficeListTag tag = new OfficeListTag("action", "method", "flow");
        tag.setOnlyBranchOffices(null);

        List<OfficeHierarchyDto> officeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(Collections.singletonList(regional));

        String html = tag.getOfficeList(userContext.getPreferredLocale(), headRegionalBranch(), branch.getSearchId(),
                officeHierarchy,
                headRegional());
        StringAssert.assertContains("East&amp;West Indies", html);
        StringAssert.assertContains("West Indies Only", html);
        StringAssert.assertContains("Trinidad&amp;Tobago", html);
        StringAssert.assertNotContains("TheGambia", html);
    }

    private List<OfficeDetailsDto> headRegionalBranch() {
        List<OfficeDetailsDto> levels = new ArrayList<OfficeDetailsDto>();
        levels.add(new OfficeDetailsDto(null, null, OfficeLevel.HEADOFFICE, "Head", 0));
        levels.add(new OfficeDetailsDto(null, null, OfficeLevel.REGIONALOFFICE, "Regional", 0));
        levels.add(new OfficeDetailsDto(null, null, OfficeLevel.BRANCHOFFICE, "Branch", 0));
        return Collections.unmodifiableList(levels);
    }

    private List<OfficeBO> headRegional() {
        List<OfficeBO> offices = new ArrayList<OfficeBO>();
        offices.add(head);
        offices.add(regional);
        return Collections.unmodifiableList(offices);
    }

    private void createSomeOffices() throws OfficeException {
        head = makeOffice("East&West Indies", OfficeLevel.HEADOFFICE);
        regional = makeOffice("West Indies Only", OfficeLevel.REGIONALOFFICE, head);
        head.setChildren(Collections.singleton(regional));
        branch = OfficeBO.makeForTest(userContext, OfficeLevel.BRANCHOFFICE, regional, "1.1.1.1.", null,
                "Trinidad&Tobago", "Trin", null, OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
        branch2 = OfficeBO.makeForTest(userContext, OfficeLevel.BRANCHOFFICE, regional, "1.1.1.10.", null,
                "TheGambia", "Gambia", null, OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
        Set<OfficeBO> regionalChildren = new HashSet<OfficeBO>();
        regionalChildren.add(branch);
        regionalChildren.add(branch2);
        regional.setChildren(regionalChildren);
    }

    private OfficeBO makeOffice(String name, OfficeLevel level) throws OfficeException {
        return makeOffice(name, level, null);
    }

    private OfficeBO makeOffice(String name, OfficeLevel level, OfficeBO parent) throws OfficeException {
        String shortName = name.substring(0, 3);
        return OfficeBO.makeForTest(userContext, level, parent, "1.1", null, name, shortName, null,
                OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
    }

}
