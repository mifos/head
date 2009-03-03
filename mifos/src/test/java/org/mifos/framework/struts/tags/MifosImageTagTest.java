package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class MifosImageTagTest extends MifosIntegrationTest{
	public MifosImageTagTest() throws SystemException, ApplicationException {
        super();
    }

    public void testRender() throws JspException, DocumentException{
		MifosImageTag mifosImageTag = new MifosImageTag();
		String path = "customer";
		mifosImageTag.setModuleName(path);
		mifosImageTag.setId("3");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		stringBuilder.append("<body>");
		String str = "\"pages/framework/images/status_activegreen.gif\"";
		stringBuilder.append("<img src="+str+" />");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		assertEquals(stringBuilder.toString(),mifosImageTag.render());
		assertWellFormedFragment(mifosImageTag.render());
	}
}
