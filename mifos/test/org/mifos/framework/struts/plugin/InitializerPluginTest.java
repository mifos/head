/**

* InitializerPluginTest.java    version: xxx



* Copyright (c) 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
package org.mifos.framework.struts.plugin;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessProcessorFactory;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DAOFactory;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.StringToMoneyConverter;
import org.mifos.framework.util.helpers.ValueObjectFactory;

import org.mifos.framework.MifosMockStrutsTestCase;

/**
 * This class is used to test methods of InitializerPlugin.
 * To do this it tries to call load method of MifosProductDefinition, 
 * becuase with out calling any
 * method on any action it does not load the plugin.
 */
public class InitializerPluginTest extends MifosMockStrutsTestCase {

	public InitializerPluginTest(){
		try {
			MifosLogManager.configure(FilePaths.LOGFILE);
			System.out.println("File path is "+ResourceLoader.getURI("org/mifos/framework/util/resources/Dependency.xml").getPath());;
			File dependencyFile = new File(ResourceLoader.getURI("org/mifos/framework/util/resources/Dependency.xml"));
			BusinessProcessorFactory.getInstance().setDependencyFile(dependencyFile);
			ValueObjectFactory.getInstance().setDependencyFile(dependencyFile);
			DAOFactory.getInstance().setDependencyFile(dependencyFile);
		} catch (LoggerConfigurationException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}
	/**
	 * This sets the web.xml,struts-config.xml and prepares the userContext
	 * and activityContext and sets them in the session.
	 *
	 * @see junit.framework.MifosTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception{
		super.setUp();
		try {

			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
			setConfigFile(ResourceLoader.getURI("org/mifos/WEB-INF/struts-config.xml").getPath());
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}

		// create the user context with the preferred locale,branch id and userId.
		UserContext userContext=new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		userContext.setBranchId(Short.valueOf("1"));
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		// prepare the roles and add to the user context.
		HashSet hashSet = new HashSet();
		hashSet.add(new Short("1"));
		userContext.setRoles(hashSet);
		// prepare activity context
		ActivityContext activityContext = new ActivityContext(Short.valueOf("0"),Short.valueOf("1"));
		getRequest().getSession().setAttribute(LoginConstants.ACTIVITYCONTEXT, activityContext);
		// set user context in the session.
		request.getSession().setAttribute(Constants.USERCONTEXT,userContext);

	}

	/**
	 *This method just tries to load a jsp page just to have InitializerPlugin called.
	 *It then tests registerConverterWithBeanUtils method of InitializerPlugin.
	 */
	public void testRegisterConverterWithBeanUtils(){
		setRequestPathInfo("/mifosproddefaction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		actionPerform();
		assertEquals(BeanUtilsBean.getInstance().getConvertUtils().lookup(Money.class).getClass(),StringToMoneyConverter.class);
	}
}
