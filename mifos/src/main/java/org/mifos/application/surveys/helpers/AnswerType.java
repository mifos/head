/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.surveys.helpers;

public enum AnswerType {
	MULTISELECT(1),
	FREETEXT(2),
	NUMBER(3),
	CHOICE(4),
	DATE(5);
	
	private int value;
	
	private AnswerType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getString() {
		return this.toString();
	}
	
	public static AnswerType fromInt(int type) {
		for (AnswerType candidate : AnswerType.values()) {
			if (type == candidate.getValue()) {
				return candidate;
			}
		}
		throw new RuntimeException("no answer type " + type);
	}
}
