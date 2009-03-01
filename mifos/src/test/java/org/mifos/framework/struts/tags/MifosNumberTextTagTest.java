package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosNumberTextTagTest extends MifosTestCase {
	
	public MifosNumberTextTagTest() throws SystemException, ApplicationException {
        super();
    }

    public void testrenderInputsForhidden() throws DocumentException{
		MifosNumberTextTag mifosNumberTextTag = new MifosNumberTextTag();
		StringBuilder inputsForhidden=new StringBuilder();
		inputsForhidden.append("<script src="+"\"pages/framework/js/func.js\""+">");
		inputsForhidden.append("</script>");
		inputsForhidden.append("<script src="+"\"pages/framework/js/func_test.js\""+">");
		inputsForhidden.append("</script>");
		assertEquals(inputsForhidden.toString(),mifosNumberTextTag.render("test"));
		assertWellFormedFragment(mifosNumberTextTag.render("test"));
	}
}
