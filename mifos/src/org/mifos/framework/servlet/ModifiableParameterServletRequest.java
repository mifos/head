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
