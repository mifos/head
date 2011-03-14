/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.messagecustomizer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

/**
 *
 */
public class CustomizedTextDaoHibernateIntegrationTest extends MifosIntegrationTestCase {
    // class under test
    @Autowired
    private CustomizedTextDao customizedTextDao;
    
	@Test
	public void shouldGetNoCustomMessages() {
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));
	}
	
	@Test
	public void shouldSetAndGetCustomizedText() {
		// we should start out empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));
		
		// add a message
		Map<String,String> messageMap = new HashMap<String, String>();
		messageMap.put("testold", "testnew");
		customizedTextDao.setCustomizedText(messageMap);
		
		// check that the message we added comes back
		messageMap.clear();
		messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("testnew"));
		
		// remove the message
		messageMap.put("testold", "testold");
		customizedTextDao.setCustomizedText(messageMap);
		
		// check that we end up empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));		
	}
	
	@Test
	public void shouldUpdateCustomizedTextMap() {
		// we should start out empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));
		
		// add a message
		Map<String,String> messageMap = new HashMap<String, String>();
		messageMap.put("testold", "testnew");
		customizedTextDao.setCustomizedText(messageMap);
		
		// check that the message we added comes back
		messageMap.clear();
		messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("testnew"));
		
		// update the message
		messageMap.put("testold", "anothertest");
		customizedTextDao.setCustomizedText(messageMap);

		// check that the message we added comes back
		messageMap.clear();
		messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("anothertest"));
				
		// remove the message
		messageMap.put("testold", "testold");
		customizedTextDao.setCustomizedText(messageMap);
		
		// check that we end up empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));		
	}	

	@Test
	public void shouldAddRemoveCustomizedText() {
		// we should start out empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));
		
		// add a message
		customizedTextDao.addOrUpdateCustomizedText("testold", "testnew");
		
		// check that the message we added comes back
		Map<String,String> messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("testnew"));
		
		// remove the message
		customizedTextDao.removeCustomizedText("testold");
		
		// check that we end up empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));		
	}
	
	@Test
	public void shouldUpdateCustomizedText() {
		// we should start out empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));
		
		// add a message
		customizedTextDao.addOrUpdateCustomizedText("testold", "testnew");
		
		// check that the message we added comes back
		Map<String,String> messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("testnew"));
		
		// update the message
		customizedTextDao.addOrUpdateCustomizedText("testold", "anothertest");

		// check that the message we added comes back
		messageMap.clear();
		messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("anothertest"));
				
		// remove the message
		customizedTextDao.removeCustomizedText("testold");
		
		// check that we end up empty
		assertThat(customizedTextDao.getCustomizedText().isEmpty(), is(true));		
	}	
	
}
