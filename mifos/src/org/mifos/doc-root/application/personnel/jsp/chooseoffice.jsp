<!-- /**
 
 * chooseoffice.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/officetags" prefix="office"%>

<tiles:insert definition=".create">
 <tiles:put name="body" type="string">


<script language="javascript">

  function goToCancelPage(){
	personnelActionForm.action="PersonnelAction.do?method=cancel";
	personnelActionForm.submit();
  }
 
</script>
<html-el:form action="PersonnelAction.do?method=load">
    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
        </tr>
    </table>              
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead">  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="33%">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                        <td class="timelineboldorange">Choose office</td>
                      </tr>
                    </table>
                  </td>
                  <td width="34%" align="center">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                        <td class="timelineboldorangelight">User information</td>
                      </tr>
                    </table>
                  </td>
                  <td width="33%" align="right"><table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorangelight">Review &amp; submit</td>
                    </tr>
                  </table></td>
                </tr>
              </table></td>
          </tr>
        </table>
        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">   
               <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange"><span class="heading">Add new
                      user - </span>Choose office</td>
                </tr>
                <tr>
                  <td class="fontnormal">To select, click on an office 
                    from the list below. Click Cancel to return to Admin page.</td>
                </tr>
              </table>
                    
         <office:listOffices methodName="load" actionName="PersonnelAction.do"/>
         <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;                </td>
              </tr>
            </table>            <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">                    &nbsp;
	                  <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="PersonnelUIResources"></mifos:mifoslabel>
    	              </html-el:button>
                  </td></tr>
            </table>
            <br></td>
          </tr>
        </table>
      <br>
<html-el:hidden property="input" value="CreateUser"/> 
</html-el:form>
</tiles:put>
</tiles:insert>