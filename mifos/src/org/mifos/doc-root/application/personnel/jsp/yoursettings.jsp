<!-- /**
 
 * yoursettings.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/mifos/customtags" prefix = "mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".noorangetab">
 <tiles:put name="body" type="string">

<html-el:form action="yourSettings.do?method=get">
<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="Personnel" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF">  
        <table width="590" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="blueline">&nbsp;</td>
        </tr>
        
    </table>              
      <table width="590" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
      
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
            <tr>
	   			<td>
	   				<font class="fontnormalRedBold">
	   					<html-el:errors bundle="PersonnelUIResources"/>
	   				</font>
				</td>
			</tr>
                <tr>
                  <td class="headingorange"><mifos:mifoslabel name="Personnel.YourSettings"/> </td>
                </tr>
                <tr>
                  <td class="fontnormal"><mifos:mifoslabel name="Personnel.ClickEditPersonnelSettings"/> </td>
                </tr>
                <tr>
                  <td class="fontnormal"><br>
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="51%" class="fontnormal">
                      <span class="fontnormalbold"><mifos:mifoslabel name="Personnel.YourDetails"/></span></td>
                      <td width="49%" align="right" class="fontnormal">
                      <a href="yourSettings.do?method=manage&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
                     	 <mifos:mifoslabel name="Personnel.EditInformation"/>
                      </a></td>
                    </tr>
                  </table>                    
<mifos:mifoslabel name="Personnel.FirstName"/> 
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.firstName}"/></span><br>
<mifos:mifoslabel name="Personnel.MiddleName"/> 
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.middleName}"/></span> <br>
<mifos:mifoslabel name="Personnel.SecondLastName"/>
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.secondLastName}"/></span> <br>
<mifos:mifoslabel name="Personnel.LastName"/> 
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.lastName}"/></span> <br>
<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}"/>:
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.governmentIdNumber}"/></span> <br>
<mifos:mifoslabel name="Personnel.Email"/> 
<span class="fontnormal"><c:out value="${Personnel.emailId}"/> </span> <br>
<mifos:mifoslabel name="Personnel.DOB"/>
<span class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,Personnel.personnelDetails.dob)}" /> </span><br>
<mifos:mifoslabel name="Personnel.Age"/>
<span class="fontnormal"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelAge')}"/></span><br>
<mifos:mifoslabel name="Personnel.MaritalStatus"/>
<span class="fontnormal"> <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'MaritalStatus')}"/> </span> <br>
<mifos:mifoslabel name="Personnel.Gender"/>
<span class="fontnormal"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Gender')}"/></span> <br>
<mifos:mifoslabel name="Personnel.LanguagePreferred"/>
<span class="fontnormal">
		<c:if test="${!empty Personnel.preferredLocale}">
			<c:out value ="${Personnel.preferredLocale.language.languageName}"/>  
		</c:if>
 </span> <br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="51%" class="fontnormal"><mifos:mifoslabel name="Personnel.UserName"/> <c:out value="${Personnel.userName}"/> </td>
    <td width="49%" align="right" class="fontnormal">
    	<a href="yourSettings.do?method=loadChangePassword&userName=${Personnel.userName}randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}"><mifos:mifoslabel name="Personnel.ChangePassword"/></a>
    </td>
  </tr>
</table><br><span class="fontnormalbold"><mifos:mifoslabel name="Personnel.Address"/></span>
<span class="fontnormal"><br>
</span>
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.displayAddress}"/><br>
</span><mifos:mifoslabel name="${ConfigurationConstants.CITY}"/>:
<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.city}"/> <br>
</span><mifos:mifoslabel name="${ConfigurationConstants.STATE}"/>:
<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.state}"/><br>
</span><mifos:mifoslabel name="Personnel.Country"/>
<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.country}"/><br>
</span><mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"/>:
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.zip}"/><br>
<br>
</span><mifos:mifoslabel name="Personnel.Telephone"/>
<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.phoneNumber}"/> </span></td>
                </tr>
              </table>
              <br>  
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
