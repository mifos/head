/**

 * QueryInputs.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.framework.hibernate.helper;

import org.hibernate.type.Type;

/**
 *  This is the interface that is returned on a search operation. Search would typically result in a set of search result objects , these search result objects would be obtained through hibernate scroll for pagination in the front end , the associate hibernate session would be held in this object , a call to close from the front end on this interface would result in the hibernate session object getting closed.
 */
public class QueryInputs
{
 
   private String dtoPath;
   private Type[] returnTypes;
   private String[] aliasNames;
   private boolean buildDTO = true;
   private String[] queryStrings=new String[2];
   java.util.List paramList = new java.util.ArrayList();


/**
 * @return Returns the paramList.
 */
public java.util.List getParamList() {
	return paramList;
}
/**
 * @param paramList The paramList to set.
 */
public void setParamList(java.util.List paramList) {
	this.paramList = paramList;
}

/**
 * @return Returns the queryStrings.
 */
public String[] getQueryStrings() {
	return queryStrings;
}
/**
 * @param queryStrings The queryStrings to set.
 */
public void setQueryStrings(String[] queryStrings) {
	this.queryStrings = queryStrings;
}
/**
	  * Set the path which will be used for building the DTO
	  * @param path
     */
   public void setPath(String path)
   {
    	this.dtoPath = path;
   }
/**
	  * Set the return types which will be used for building the DTO
	  * @param returnTypes
     */
   public void setTypes(Type[] returnTypes)
   {
   	this.returnTypes = returnTypes;
   }

   /**
   	  * Set the alias names of the columns which will be used for building the DTO
   	  * @param aliasNames
     */
   public void setAliasNames(String[] aliasNames)
   {
   	this.aliasNames = aliasNames;
   }

     /**
        * Return the path for building the DTO
        * @return String
     */
   public String getPath()
   {
       	return dtoPath;
   }
   /**
    * Return the types for building the DTO
     * @return Type[]
     */

    public Type[] getTypes()
    {
      	return returnTypes;
    }

      /**
	    * Return the alias names for building the DTO
	     * @return String[]
     */
    public String[] getAliasNames()
   {
      	return  aliasNames;
   }

   /**
      	  * Set the boolean to indicate wether DTO has to be built
      	  * @param buildDTO
     */
   public void setBuildDTO(boolean buildDTO)
   {
	   this.buildDTO = buildDTO;
   }

    /**
     * Return the boolean to indicate wether DTO has to be built
     * @param boolean
     */
   public boolean getBuildDTO()
   {
	   return buildDTO;
   }

   public void cleanUp()
   {
		dtoPath	= null;
		returnTypes  = null;
		aliasNames = null;

   }
}