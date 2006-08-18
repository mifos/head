<!-- /**
 
 * groupsearchcreateclient.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>


<html-el:form action="GroupAction.do?method=search">

     <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>      
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                <td class="timelineboldorange"><mifos:mifoslabel name="Group.select" /> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></td>
              </tr>
            </table></td>
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.personnelinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="center"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.mfiinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="right"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.reviewandsubmit" /></td>
              </tr>
            </table></td>
            </tr>
        </table>
       </td>
      </tr>
    </table>
   
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates"><table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange"><span class="heading"><mifos:mifoslabel name="Group.createnew" />
                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /> - </span><mifos:mifoslabel name="Group.select" />  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></td>
              </tr>
            </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="0">
              <tr>
   				<td>
   				<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
				</td>
			</tr>
               </table>                            
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal">
                  <mifos:mifoslabel name="Group.entera"/> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/> <mifos:mifoslabel name="Group.clickcancel"/>
                  <mifos:mifoslabel name="Group.createpagehead3" />                     
                  </span> </td>
                </tr>
              </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr class="fontnormal">
                  <td align="right">&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />  <mifos:mifoslabel name="Group.name" /></td>
                  <td>
                  	<html-el:text property="searchNode(searchString)" maxlength="200"/>
			      	<html-el:hidden property="searchNode(search_name)" value="GroupList"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="28%">&nbsp; </td>
                  <td width="72%">      
      <html-el:submit styleClass="buttn" style="width:70px;">
	      <mifos:mifoslabel name="button.proceed" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:submit>
&nbsp;
      <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
            <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:button>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td>&nbsp;</td>
                  <td>
                  <c:if test="${sessionScope.groupHierarchyRequired eq 'No'}">
                   <a href="clientCustAction.do?method=chooseOffice&amp;groupFlag=0"> 
                 <%-- <a href="clientCreationAction.do?method=load&amp;office.officeId=5&amp;isClientUnderGrp=0"> --%>
                  <br>
                    <mifos:mifoslabel name="Group.clickheretocontinueif" /> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel name="Group.membershipisnotrequiredforyour" /> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />.</a>
                  </c:if>
                  </td>
                </tr>
              </table>              
              <br>
          </td>
        </tr>
      </table>
      <br>
<html-el:hidden property="input" value="GroupSearch_CreateClient"/> 
</html-el:form>
