/**

 * MifosMifosMockStrutsTestCase.java    version: xxx



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

package org.mifos.framework;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

public class MifosMockStrutsTestCase extends MockStrutsTestCase {
	static {
		try {
			Class.forName(TestCaseInitializer.class.getName());
		} catch (ClassNotFoundException e) {
			throw new Error("Failed to start up", e);
		}
	}
	
	protected void addRequestDateParameter(String param, String dateStr) {
		java.sql.Date date = DateUtils.getDateAsSentFromBrowser(dateStr);
		if (date != null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);

			addRequestParameter(param + "DD", Integer.toString(cal
					.get(Calendar.DAY_OF_MONTH)));
			addRequestParameter(param + "MM", Integer.toString(cal
					.get(Calendar.MONTH) + 1));
			addRequestParameter(param + "YY", Integer.toString(cal
					.get(Calendar.YEAR)));
		}
		else {
			addRequestParameter(param + "DD", "");
			addRequestParameter(param + "MM", "");
			addRequestParameter(param + "YY", "");
		}
	}
	
	protected int getErrorSize(String field){
		ActionErrors errors = (ActionErrors)request.getAttribute(Globals.ERROR_KEY);
		return errors != null ? errors.size(field) : 0;
	}
	
	protected int getErrorSize(){
		ActionErrors errors = (ActionErrors)request.getAttribute(Globals.ERROR_KEY);
		return errors == null ? 0 : errors.size();
	}
	
	protected void matchValues(AuditLogRecord auditLogRecord, String oldValue, String newValue){
		assertEquals(oldValue, auditLogRecord.getOldValue());
		assertEquals(newValue, auditLogRecord.getNewValue());
	}
	
	@Override
	protected void tearDown() throws Exception {		
		doCleanUp(request);
		doCleanUp(request.getSession());
		TestObjectFactory.cleanUpTestObjects();
		super.tearDown();
		
		
	}
	public  void doCleanUp(HttpSession session){
		Enumeration keys = session.getAttributeNames();
		String attributeKey = null;
		if(null != keys ){
			while(keys.hasMoreElements()){
				
				attributeKey = (String)keys.nextElement();
				
				Object obj = session.getAttribute(attributeKey);
					session.removeAttribute(attributeKey);
				if(obj.getClass().getName().equals("java.util.ArrayList"))
				{
					
					List l = (List)obj;

					while(l.size() != 0)
					{
						l.remove(0);
					}
				}
			}// end-while
		}// end-if
		session = null;
	}// end-doCleanUp

	public  void doCleanUp(HttpServletRequest request){
		Enumeration keys = request.getAttributeNames();
		String attributeKey = null;
		if(null != keys ){
			while(keys.hasMoreElements()){
				attributeKey = (String)keys.nextElement();
				request.removeAttribute(attributeKey);
			}// end-while
		}// end-if
		request = null;
	}// end-doCleanUp

	protected String createFlow(HttpServletRequest request, Class flowClass) {
		Flow flow = new Flow();
		String flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow, flowClass.getName());
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		return flowKey;
	}

	protected String createFlowAndAddToRequest(Class flowClass) {
		String key = createFlow(request, flowClass);
		request.setAttribute(Constants.CURRENTFLOWKEY, key);
		addRequestParameter(Constants.CURRENTFLOWKEY, key);
		return key;
	}

	protected void loadAccountStrutsConfig() throws URISyntaxException {
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/accounts/struts-config.xml").getPath());
	}

	protected void performNoErrors() {
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

}
