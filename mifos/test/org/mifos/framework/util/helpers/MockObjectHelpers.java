package org.mifos.framework.util.helpers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

public class MockObjectHelpers {

	public static java.sql.Date eqAnySqlDate() {
		EasyMock.reportMatcher(new IArgumentMatcher() {
			public void appendTo(StringBuffer message) {
				return;
			}
	
			public boolean matches(Object other) {
				return true;
			}});
		return null;
	}	

}
