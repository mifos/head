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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CustomizedTextDaoHibernateIntegrationTest extends MifosIntegrationTestCase {
    // class under test
    @Autowired
    private CustomizedTextDao customizedTextDao;
    
	@Test
	public void shouldAddRemoveCustomizedText() {
		int initialCustomizedTextCount = customizedTextDao.getCustomizedText().size();
		
		// add a message
		customizedTextDao.addOrUpdateCustomizedText("testold", "testnew");
		
		// check that the message we added comes back
		Map<String,String> messageMap = customizedTextDao.getCustomizedText();
		assertThat(messageMap.get("testold"),is("testnew"));
		
		// remove the message
		customizedTextDao.removeCustomizedText("testold");
		
		// check that we end up with what we started with
		assertThat(customizedTextDao.getCustomizedText().size(), is(initialCustomizedTextCount));		
	}
	
	@Test
	public void shouldUpdateCustomizedText() {
		int initialCustomizedTextCount = customizedTextDao.getCustomizedText().size();
		
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
		
		// check that we end up with what we started with
		assertThat(customizedTextDao.getCustomizedText().size(), is(initialCustomizedTextCount));		
	}	
	
}
