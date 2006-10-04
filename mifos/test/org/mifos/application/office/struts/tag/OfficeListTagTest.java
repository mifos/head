package org.mifos.application.office.struts.tag;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;

public class OfficeListTagTest extends TestCase {
	
	private XmlBuilder result;
	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		result = new XmlBuilder();
		userContext = TestUtils.makeUser(1);
	}

	public void testNoBranches() throws Exception {
		new OfficeListTag().getBranchOffices(
			result, null, userContext, "Branch");
		assertWellFormedFragment(result.toString());
	}
	
	public void testBranches() throws Exception {
		OfficeBO head = makeOffice("East&West Indies", OfficeLevel.HEADOFFICE);
		OfficeBO branch = OfficeBO.makeForTest(
			userContext, OfficeLevel.BRANCHOFFICE, head, null, 
			"Trinidad&Tobago", "Trinidad&Tobago", null,
			OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
		head.setChildren(Collections.singleton(branch));
		assertEquals(1, head.getBranchOnlyChildren().size());

		List<OfficeBO> officeList = new ArrayList<OfficeBO>();
		officeList.add(head);

		new OfficeListTag().getBranchOffices(
			result, officeList, userContext, "Branch");
		String html = result.toString();
		assertWellFormedFragment(html);
		StringAssert.assertContains("Trinidad&amp;Tobago", html);
	}
	
	public void testNothingAboveBranches() throws Exception {
		new OfficeListTag().getAboveBranches(result,
			null, null, null, null);
		assertEquals("", result.toString());
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

		new OfficeListTag().getAboveBranches(result,
			offices, "Province <State>", "County <Duchy>", "Parish <City>");
		String html = result.toString();
		assertWellFormedFragment(html);
		StringAssert.assertContains("Toronto&amp;Ottawa", html);
	}
	
	private OfficeBO makeOffice(String name, OfficeLevel level)
	throws OfficeException {
		return OfficeBO.makeForTest(userContext, level, null, null, name, name, null,
			OperationMode.LOCAL_SERVER, OfficeStatus.ACTIVE);
	}

	public void testAssertWellFormed() throws Exception {
		assertWellFormedFragment("<foo />");
		assertWellFormedFragment("x y z");
		assertWellFormedFragment("x <p>this <b>shows it off</b></p> z");
		try {
			assertWellFormedFragment("<unclosed>");
			fail("Didn't get assertion failure");
		}
		catch (DocumentException e) {
			
		}
	}
	
	public void testGetLink() throws Exception {
		OfficeListTag tag = new OfficeListTag("action", "method", "flow");
		XmlBuilder link = tag.getLink((short) 234, "My Office");
		assertEquals("<a href=\"action?method=method&amp;" +
			"office.officeId=234&amp;office.officeName=My%20Office&amp;" +
			"officeId=234&amp;officeName=My%20Office&amp;" +
			"currentFlowKey=flow\">My Office</a>", link.getOutput());
	}
	
	public static void assertWellFormedFragment(String xml) throws DocumentException {
		SAXReader reader = new SAXReader();
        reader.read(new StringReader("<root>" + xml + "</root>"));
	}

}
