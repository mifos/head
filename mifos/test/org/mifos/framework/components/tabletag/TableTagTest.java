package org.mifos.framework.components.tabletag;

import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.TestCase;

import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.util.helpers.SearchObject;

public class TableTagTest extends TestCase {
	
	public void testNoResults() throws Exception {
		String html =
			new TableTag("single")
				.noResults("default-office", TableTag.ALL_BRANCHES, "Rock&Roll")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock&amp;Roll", html);
		assertContains("All Branches", html);
		assertNotContains("office-one", html);
		assertNotContains("default-office", html);
		assertWellFormedFragment(html);
	}

	public void testNoResultsMultiple() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
		searchObject.addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "office-one");
		String html =
			new TableTag("multiple")
				.noResults("the-office-name", "office-one", "Rock")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("the-office-name", html);
		assertNotContains("office-one", html);
		assertWellFormedFragment(html);
	}

	public void testNoResultsNotAllBranches() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
		searchObject.addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "");
		String html =
			new TableTag("multiple")
				.noResults("the-office-name", "", "Rock")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("the-office-name", html);
		assertWellFormedFragment(html);
	}
	
	public void testCreateEndTable(){
		StringBuilder stringBuilder = new StringBuilder();
			new TableTag("single").createEndTable(stringBuilder,true);
			assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>",stringBuilder.toString());
			new TableTag("single").createEndTable(stringBuilder,false);
			assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>",stringBuilder.toString());
	}

}
