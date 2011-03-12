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


public class MessageCustomizerDaoHibernate implements MessageCustomizerDao {
    private final GenericDao genericDao;

    @Autowired
    public MessageCustomizerDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

	@Override
	public Map<String, String> getCustomMessages() {
		List<CustomMessage> messages = getAllMessages();
		
		LinkedHashMap<String,String> messageMap = new LinkedHashMap<String,String>();
		
		for (CustomMessage message: messages) {
			messageMap.put(message.getOldMessage(), message.getNewMessage());
		}
		return messageMap;
	}

	@Override
	public void setCustomMessages(Map<String, String> messageMap) {
		Map<String, String> currentMessages = getCustomMessages();
		
        for (Map.Entry<String, String> entry : messageMap.entrySet()) { 
        	if(entry.getKey().contentEquals(entry.getValue())) {
        		removeCustomMessage(entry.getKey());      			        	
        	} else if (!currentMessages.containsKey(entry.getKey())) {
        		addOrUpdateCustomMessage(entry.getKey(), entry.getValue());
        	} else {
        		CustomMessage message = findCustomMessageByOldMessage(entry.getKey());
        		message.setNewMessage(entry.getValue());
        	}
        }
		
	}

	@Override
    public void addOrUpdateCustomMessage(String oldMessage, String newMessage) {
		CustomMessage message = findCustomMessageByOldMessage(oldMessage);
		if (message == null) {
			message = new CustomMessage(oldMessage, newMessage);
		} else {
			message.setNewMessage(newMessage);
		}
    	genericDao.createOrUpdate(message);		
	}

	@Override
	public void removeCustomMessage(String oldMessage) {
		CustomMessage message = findCustomMessageByOldMessage(oldMessage);
		
		if (message != null) {
			genericDao.delete(message);
		}
	}

	@SuppressWarnings("unchecked")
    private List<CustomMessage> getAllMessages() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List<CustomMessage> queryResult = (List<CustomMessage>) this.genericDao.executeNamedQuery(
                "allMessagesNative", queryParameters);

        List<CustomMessage> messages = new ArrayList<CustomMessage>();

        if (queryResult != null) {
            messages.addAll(queryResult);
        }

        return messages;
    }	
	
    public CustomMessage findCustomMessageByOldMessage(final String oldMessage) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("oldMessage", oldMessage);

        CustomMessage entity = (CustomMessage) this.genericDao.executeUniqueResultNamedQuery("findCustomMessageByOldMessage", queryParameters);

        return entity;
    }	
}
