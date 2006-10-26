/**

 * ConstPlugIn.java    version: 1.0



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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class ConstPlugin implements PlugIn{

	private String constantFileNames;

	public void setConstantFileNames(String constantFileNames) {
		this.constantFileNames = constantFileNames;
	}

	public void init(ActionServlet actionServlet , ModuleConfig config) throws ServletException{
		try{
			ServletContext servletContext = actionServlet.getServletContext();
			List<String> constantFileNameList = getConstantFileNames();
			List<Class> constantClassList = buildClasses(constantFileNameList);
			ConstantMapBuilder constantBuilder=ConstantMapBuilder.getInstance();
			for(Class cl:constantClassList){
				servletContext.setAttribute(getName(cl),constantBuilder.buildMap(cl));
			}
		}
		catch(Exception e){
			UnavailableException ue = new UnavailableException(e.getMessage(), 0);
			ue.initCause(e);
			throw ue;
		}
	}

	private String getName(Class cl){
		String className = cl.getName();
		String constantKeyName = className.substring(className.lastIndexOf(".")+1);
		return constantKeyName;
	}

	public void destroy() {
	}

	private List<String> getConstantFileNames(){
		StringTokenizer tokenizer = new StringTokenizer(constantFileNames,",");
		List<String> fileNameList = new ArrayList<String>();
		while(tokenizer.hasMoreTokens()){
			fileNameList.add(tokenizer.nextToken());
		}
		return fileNameList;
	}

	List<Class> buildClasses(List<String> constantFileNameList) throws ConstantsNotLoadedException{
		List<Class> constantClassNameList = new ArrayList<Class>();
		String constantFileName = null ;
		try{
			for(String fileName:constantFileNameList){
				constantFileName = fileName.trim();
				constantClassNameList.add(Class.forName(constantFileName));
			}
		}
		catch(ClassNotFoundException cnfe){
			Object[] values = new Object[]{constantFileName};
			throw new ConstantsNotLoadedException(ExceptionConstants.CONSTANTSNOTLOADEDEXCEPTION,cnfe,values);
		}
		return constantClassNameList;
	}

	public static void checkModifiers(Field field) throws ConstantsNotLoadedException{
		if(!Modifier.isFinal(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as final");
		if(!Modifier.isStatic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as static");
		if(!Modifier.isPublic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as public");
	}

	/**
	 * Builds the Map for all the constansts in the given constant file.
	 **/
	private static class ConstantMapBuilder{
		private  static ConstantMapBuilder instance = new ConstantMapBuilder();
		private  ConstantMapBuilder(){
		}
		public static ConstantMapBuilder getInstance(){
			return instance;
		}
		public Map buildMap(Class constantClass) throws ConstantsNotLoadedException{
			Map<String, Object> constantsMap = new HashMap<String, Object>();
			Field[] fields = constantClass.getDeclaredFields();
			try{
				for (int i = 0; i < fields.length; i++) {
					 checkModifiers(fields[i]);
			         String fieldName = fields[i].getName();
			         Object fieldValue = fields[i].get(null);
			         constantsMap.put(fieldName,fieldValue);
			    }
			}catch(IllegalAccessException iae){
				throw new ConstantsNotLoadedException(iae);
			}
			return constantsMap;
		}
	}
}
