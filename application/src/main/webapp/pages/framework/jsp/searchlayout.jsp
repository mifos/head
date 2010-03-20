<%--
Copyright (c) 2005-2009 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<td width="174" height="500" align="left" valign="top" class="bgorangeleft">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<tr>
          <td class="leftpanehead" colspan="2">
          <tiles:getAsString name="menutitle"/>
          </td>
        </tr>
       
        <tiles:insert attribute="search" ignore="true"/>
       
      </table>
      
</td>      
