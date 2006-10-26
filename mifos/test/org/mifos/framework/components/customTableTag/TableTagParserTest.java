package org.mifos.framework.components.customTableTag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.TableTagParseException;

public class TableTagParserTest extends MifosTestCase {

	public void testParser() {
		TableTagParser tableTagParser = new TableTagParser();
		try {
			tableTagParser.parser("org/mifos/framework/struts/util/helpers/struts-config.xml");
		} catch (TableTagParseException ttpe) {
			assertEquals("exception.framework.TableTagParseException", ttpe.getKey());
		}
	}

}
