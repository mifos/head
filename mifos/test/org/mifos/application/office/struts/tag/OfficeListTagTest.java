package org.mifos.application.office.struts.tag;

import java.io.StringReader;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.framework.TestUtils;

public class OfficeListTagTest extends TestCase {
	
	public void testGetBranchOffices() throws Exception {
		StringBuilder result = new StringBuilder();
		new OfficeListTag().getBranchOffices(
			result, null, TestUtils.makeUser(1), "Branch");
		assertWellFormedFragment(result.toString());
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
	
	private void assertWellFormedFragment(String xml) throws DocumentException {
		SAXReader reader = new SAXReader();
        reader.read(new StringReader("<root>" + xml + "</root>"));
	}

}
