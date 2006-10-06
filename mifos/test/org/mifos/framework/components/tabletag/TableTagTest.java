package org.mifos.framework.components.tabletag;

import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.util.helpers.SearchObject;

import junit.framework.TestCase;
import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class TableTagTest extends TestCase {
	
	public void testNoResults() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addSearchTermAndOffice("Rock&Roll", TableTag.ALL_BRANCHES);
		String html =
			new TableTag("single")
				.noResults("default-office", searchObject)
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
				.noResults("default-office", searchObject)
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("office-one", html);
		assertNotContains("default-office", html);
		assertWellFormedFragment(html);
	}

	public void testNoResultsDefaultName() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
		searchObject.addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "");
		String html =
			new TableTag("multiple")
				.noResults("default-office", searchObject)
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("default-office", html);
		assertWellFormedFragment(html);
	}

}
