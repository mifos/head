package org.mifos.application.configuration.struts.tag;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class CustomFieldCategoryListTagTest extends TestCase {

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseSetup.configureLogging();
		userContext = TestUtils.makeUser();
	}

	public void testGetCategoryRow() throws Exception {
		CustomFieldCategoryListTag tag = new CustomFieldCategoryListTag("action", "method", "flow");
		String categoryName = "Personnel";
		XmlBuilder link = tag.getCategoryRow(categoryName);
		assertEquals("<tr class=\"fontnormal\"><td width=\"1%\">" 
				+ "<img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\" />"
				+ "</td><td>"
				+ "<a href=\"action?method=method&amp;"
				+ "category=" + categoryName 
				+ "&amp;currentFlowKey=flow\">" + categoryName + "</a></td></tr>", link.getOutput());
	}
	
	public void testGetCustomFieldCategoryList() throws Exception {
		CustomFieldCategoryListTag tag = new CustomFieldCategoryListTag("action", "method", "flow");
		String html = tag.getCustomFieldCategoryList(userContext);
		assertWellFormedFragment(html);
		
		for (CustomFieldCategory category : CustomFieldCategory.values()) {
			StringAssert.assertContains(category.toString(), html);			
		}
	}

}
