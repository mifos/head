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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;


public class CustomizedTextDaoHibernate implements CustomizedTextDao {
    private final GenericDao genericDao;

    @Autowired
    public CustomizedTextDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

	@Override
	public Map<String, String> getCustomizedText() {
		List<CustomizedText> messages = getAllCustomizedText();
		
		LinkedHashMap<String,String> messageMap = new LinkedHashMap<String,String>();
		
		for (CustomizedText message: messages) {
			messageMap.put(message.getOriginalText(), message.getCustomText());
		}
		return messageMap;
	}

	@Override
	public void setCustomizedText(Map<String, String> messageMap) {
		Map<String, String> currentMessages = getCustomizedText();
		
        for (Map.Entry<String, String> entry : messageMap.entrySet()) { 
        	if(entry.getKey().contentEquals(entry.getValue())) {
        		removeCustomizedText(entry.getKey());      			        	
        	} else if (!currentMessages.containsKey(entry.getKey())) {
        		addOrUpdateCustomizedText(entry.getKey(), entry.getValue());
        	} else {
        		CustomizedText message = findCustomizedTextByOriginalText(entry.getKey());
        		message.setCustomText(entry.getValue());
        	}
        }
		
	}

	@Override
    public void addOrUpdateCustomizedText(String oldMessage, String newMessage) {
		CustomizedText message = findCustomizedTextByOriginalText(oldMessage);
		if (message == null) {
			message = new CustomizedText(oldMessage, newMessage);
		} else {
			message.setCustomText(newMessage);
		}
    	genericDao.createOrUpdate(message);		
	}

	@Override
	public void removeCustomizedText(String oldMessage) {
		CustomizedText message = findCustomizedTextByOriginalText(oldMessage);
		
		if (message != null) {
			genericDao.delete(message);
		}
	}

	@SuppressWarnings("unchecked")
    private List<CustomizedText> getAllCustomizedText() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List<CustomizedText> queryResult = (List<CustomizedText>) this.genericDao.executeNamedQuery(
                "allMessagesNative", queryParameters);

        List<CustomizedText> messages = new ArrayList<CustomizedText>();

        if (queryResult != null) {
            messages.addAll(queryResult);
        }

        return messages;
    }	
	
    public CustomizedText findCustomizedTextByOriginalText(final String originalText) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("originalText", originalText);

        CustomizedText entity = (CustomizedText) this.genericDao.executeUniqueResultNamedQuery("findCustomMessageByOldMessage", queryParameters);

        return entity;
    }	
}
