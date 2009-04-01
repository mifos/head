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
	   					<span id="yoursettings.error.message"><html-el:errors bundle="PersonnelUIResources"/>
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
                      <a id="yoursettings.link.edit" href="yourSettings.do?method=manage&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
                     	 <mifos:mifoslabel name="Personnel.EditInformation"/>
                      </a></td>
                    </tr>
                  </table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr id="Personnel.FirstName">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.FirstName"/>
			<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.firstName}"/></span>
		</td>
	</tr>
	<tr id="Personnel.MiddleName">
		<td class="fontnormal">
			<mifos:mifoslabel keyhm="Personnel.MiddleName" name="Personnel.MiddleName"/>
			<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.middleName}"/></span>
		</td>
	</tr>
	<tr id="Personnel.SecondLastName">
		<td class="fontnormal">
			<mifos:mifoslabel keyhm="Personnel.SecondLastName" name="Personnel.SecondLastName"/>
			<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.secondLastName}"/></span>
		</td>
	</tr>
	<tr id="Personnel.LastName">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.LastName"/>
			<span class="fontnormal"><c:out value="${Personnel.personnelDetails.name.lastName}"/></span>
		</td>
	</tr>
	<tr id="Personnel.GovernmentId">
		<td class="fontnormal">
			<mifos:mifoslabel keyhm="Personnel.GovernmentId" name="${ConfigurationConstants.GOVERNMENT_ID}"/>:
			<span class="fontnormal"><c:out value="${Personnel.personnelDetails.governmentIdNumber}"/></span>
		</td>
	</tr>
	<tr id="Personnel.Email">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.Email"/>
			<span class="fontnormal"><c:out value="${Personnel.emailId}"/> </span>
		</td>
	</tr>
	<tr id="Personnel.DOB">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.DOB"/>
			<span class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,Personnel.personnelDetails.dob)}" /> </span>
		</td>
	</tr>
	<tr id="Personnel.Age">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.Age"/>
			<span class="fontnormal"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelAge')}"/></span>
		</td>
	</tr>
	<tr id="Personnel.MaritalStatus">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.MaritalStatus"/>
			<span class="fontnormal"> <c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'MaritalStatus')}"/> </span>
		</td>
	</tr>
	<tr id="Personnel.Gender">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.Gender"/>
			<span class="fontnormal"><c:out value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Gender')}"/></span>
		</td>
	</tr>
	<tr id="Personnel.LanguagePreferred">
		<td class="fontnormal">
			<mifos:mifoslabel name="Personnel.LanguagePreferred"/>
			<span class="fontnormal">
				<c:if test="${!empty Personnel.preferredLocale}">
					<c:out value ="${Personnel.preferredLocale.language.languageName}"/>
				</c:if>
			</span>
		</td>
	</tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="51%" class="fontnormal"><mifos:mifoslabel name="Personnel.UserName"/> <c:out value="${Personnel.userName}"/> </td>
    <td width="49%" align="right" class="fontnormal">
    	<a id="yoursettings.link.changePassword" href="<c:url value='yourSettings.do'>
	   		<c:param name='method' value='loadChangePassword'/>
			<c:param name='userName' value='${Personnel.userName}'/>
			<c:param name='randomNUm' value='${sessionScope.randomNUm}'/>
			<c:param name='currentFlowKey' value='${requestScope.currentFlowKey}'/>
		</c:url>">
		<mifos:mifoslabel name="Personnel.ChangePassword"/>
	</a>
    </td>
  </tr>
</table><br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  	<td class="fontnormal">
		<span class="fontnormalbold"><mifos:mifoslabel name="Personnel.Address"/></span>
		<span class="fontnormal"><br>
		</span>
		<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.displayAddress}"/><br>
		</span>
	</td>
  </tr>
  <tr>
  	<td class="fontnormal">
		<mifos:mifoslabel name="${ConfigurationConstants.CITY}"/>:
		<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.city}"/> <br>
		</span>
	</td>
  </tr>
  <tr id="Personnel.State">
	<td class="fontnormal">
		<mifos:mifoslabel keyhm="Personnel.State" name="${ConfigurationConstants.STATE}"/>:
		<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.state}"/><br>
		</span>
	</td>
  </tr>
  <tr id="Personnel.Country">
	<td class="fontnormal">
		<mifos:mifoslabel keyhm="Personnel.Country" name="Personnel.Country"/>
		<span class="fontnormal"> <c:out value="${Personnel.personnelDetails.address.country}"/><br>
		</span>
	</td>
  </tr>
  <tr id="Personnel.PostalCode">
	<td class="fontnormal">
		<mifos:mifoslabel keyhm="Personnel.PostalCode" name="${ConfigurationConstants.POSTAL_CODE}"/>:
		<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.zip}"/><br>
		<br>
		</span>
  <tr id="Personnel.Telephone">
	<td class="fontnormal">
		<mifos:mifoslabel keyhm="Personnel.Telephone" name="Personnel.Telephone"/>
		<span class="fontnormal"><c:out value="${Personnel.personnelDetails.address.phoneNumber}"/> </span>
	</td>
  </tr>
</table>
	</td>
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
