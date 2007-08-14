package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosTestCase;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosTextareaTagTest extends MifosTestCase {
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
