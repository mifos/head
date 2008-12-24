package org.mifos.application.reports.business;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.junit.Test;
import org.mifos.application.reports.business.validator.Errors;

public class SqlCollectionSheetReportParameterFormTest {

	@Test
	public void testValidateAcceptsDateEvenIfItHasExtraCharactersAtTheEnd() {
		SqlCollectionSheetReportParameterForm form = new SqlCollectionSheetReportParameterForm(
				"1", "1", "1", "23/06/2008ss");
		Errors errorsMock = createMock(Errors.class);
		replay(errorsMock);
		form.validate(errorsMock);
		verify(errorsMock);
	}

}
