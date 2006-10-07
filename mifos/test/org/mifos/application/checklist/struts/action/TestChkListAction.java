/**
 *
 */
package org.mifos.application.checklist.struts.action;

import java.net.URISyntaxException;

import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestChkListAction extends MifosMockStrutsTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {

			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/checklist/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCreate() {
		setRequestPathInfo("/chkListAction");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.create_success.toString());
	}

}
