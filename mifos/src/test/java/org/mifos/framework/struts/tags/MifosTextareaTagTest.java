package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosTextareaTagTest extends MifosTestCase {
	public MifosTextareaTagTest() throws SystemException, ApplicationException {
        super();
    }

    public void testrenderInputsForhidden() throws DocumentException{
		MifosTextareaTag mifosTextareaTag  = new MifosTextareaTag();
		mifosTextareaTag.setKeyhm("test1");
		mifosTextareaTag.setPropertyExpr("test2");
		StringBuffer inputsForhidden=new StringBuffer();
		inputsForhidden.append("<input type=\"hidden\" name=\""+"test1"+"\" value=\""+"test2"+"\" />");
		assertEquals(inputsForhidden.toString(), mifosTextareaTag.render());
		assertWellFormedFragment(mifosTextareaTag.render());
	}
}
