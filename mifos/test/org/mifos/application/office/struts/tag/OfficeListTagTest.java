package org.mifos.application.office.struts.tag;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.UserContext;

public class OfficeListTagTest extends TestCase {
	
	private StringBuilder result;
	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		result = new StringBuilder();
		userContext = TestUtils.makeUser(1);
	}

	public void testNoBranches() throws Exception {
		new OfficeListTag().getBranchOffices(
			result, null, TestUtils.makeUser(1), "Branch");
		assertWellFormedFragment(result.toString());
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
		assertWellFormedFragment(result.toString());
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
	
	public static void assertWellFormedFragment(String xml) throws DocumentException {
		SAXReader reader = new SAXReader();
        reader.read(new StringReader("<root>" + xml + "</root>"));
	}

}
