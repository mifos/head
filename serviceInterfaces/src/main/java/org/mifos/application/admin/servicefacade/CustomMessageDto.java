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

package org.mifos.application.admin.servicefacade;


public class CustomMessageDto {
	private String oldMessage;
	private String newMessage;
	
	
	public CustomMessageDto(String oldMessage, String newMessage) {
		super();
		this.oldMessage = oldMessage;
		this.newMessage = newMessage;
	}
	
	public String getOldMessage() {
		return oldMessage;
	}
	public void setOldMessage(String oldMessage) {
		this.oldMessage = oldMessage;
	}
	public String getNewMessage() {
		return newMessage;
	}
	public void setNewMessage(String newMessage) {
		this.newMessage = newMessage;
	}
	
	
}
