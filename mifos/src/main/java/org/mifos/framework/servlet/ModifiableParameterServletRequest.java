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
 
package org.mifos.framework.servlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections.iterators.IteratorEnumeration;
@SuppressWarnings("deprecation")
public class ModifiableParameterServletRequest extends
		HttpServletRequestWrapper {
	List<String> removedParameterList;

	public ModifiableParameterServletRequest(HttpServletRequest originalRequest) {
		super(originalRequest);
		removedParameterList = new ArrayList<String>();
	}

	public void removeParameter(String string) {
		removedParameterList.add(string);
	}

	@Override
	public String getParameter(String arg0) {
		if (removedParameterList.contains(arg0))
			return null;
		return getRequest().getParameter(arg0);
	}

	@Override
	public Map getParameterMap() {
		Map parameterMap = updatedParameterList();
		return parameterMap;
	}

	private Map<String, String> updatedParameterList() {
		Map<String, String> parameterMap = new HashMap<String, String>(
				getRequest().getParameterMap());
		for (String removeParameterName : removedParameterList) {
			parameterMap.remove(removeParameterName);
		}
		return parameterMap;
	}

	@Override
	public Enumeration getParameterNames() {
		Enumeration parameterNames = getRequest().getParameterNames();
		List paramNames = new ArrayList();
		while (parameterNames.hasMoreElements()) {
			Object nextElement = parameterNames.nextElement();
			if (!removedParameterList.contains(nextElement)) {
				paramNames.add(nextElement);
			}
		}
		return new IteratorEnumeration(paramNames.iterator());
	}

	@Override
	public String[] getParameterValues(String arg0) {
		if (removedParameterList.contains(arg0))
			return null;
		return getRequest().getParameterValues(arg0);
	}
}
