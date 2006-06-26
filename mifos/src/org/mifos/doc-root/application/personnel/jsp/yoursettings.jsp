<!-- /**
 
 * yoursettings.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/mifos/customtags" prefix = "mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".noorangetab">
 <tiles:put name="body" type="string">

<html-el:form action="PersonnelAction.do?method=getDetails">

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
                      <a href="PersonnelAction.do?method=editPersonalInfo">
                     	 <mifos:mifoslabel name="Personnel.EditInformation"/>
                      </a></td>
                    </tr>
                  </table>                    
<mifos:mifoslabel name="Personnel.FirstName"/> 
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.firstName}"/></span><br>
<mifos:mifoslabel name="Personnel.MiddleName"/> 
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.middleName}"/></span> <br>
<mifos:mifoslabel name="Personnel.SecondLastName"/>
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.secondLastName}"/></span> <br>
<mifos:mifoslabel name="Personnel.LastName"/> 
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.lastName}"/></span> <br>
<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}"/>:
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.governmentIdNumber}"/></span> <br>
<mifos:mifoslabel name="Personnel.Email"/> 
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.emailId}"/> </span> <br>
<mifos:mifoslabel name="Personnel.DOB"/>
<span class="fontnormal"><c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,requestScope.PersonnelVO.personnelDetails.dob)}" /> </span><br>
<mifos:mifoslabel name="Personnel.Age"/>
<span class="fontnormal"><c:out value="${sessionScope.personnelAge}"/></span><br>
<mifos:mifoslabel name="Personnel.MaritalStatus"/>
<span class="fontnormal"> <mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.maritalStatus}" searchResultName="maritalStatusList"></mifoscustom:lookUpValue> </span> <br>
<mifos:mifoslabel name="Personnel.Gender"/>
<span class="fontnormal"><mifoscustom:lookUpValue id="${requestScope.PersonnelVO.personnelDetails.gender}" searchResultName="genderList"></mifoscustom:lookUpValue></span> <br>
<mifos:mifoslabel name="Personnel.LanguagePreferred"/>
<span class="fontnormal">
		<c:if test="${!empty requestScope.PersonnelVO.preferredLocale}">
			<c:out value ="${requestScope.PersonnelVO.preferredLocale.language.languageName}"/>  
		</c:if>
 </span> <br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="51%" class="fontnormal"><mifos:mifoslabel name="Personnel.UserName"/> <c:out value="${requestScope.PersonnelVO.userName}"/> </td>
    <td width="49%" align="right" class="fontnormal">
    	<a href="PersonnelAction.do?method=loadChangePassword"><mifos:mifoslabel name="Personnel.ChangePassword"/></a>
    </td>
  </tr>
</table><br><span class="fontnormalbold"><mifos:mifoslabel name="Personnel.Address"/></span>
<span class="fontnormal"><br>
</span>
<span class="fontnormal"><c:out value="${requestScope.displayAddress}"/><br>
</span><mifos:mifoslabel name="${ConfigurationConstants.CITY}"/>:
<span class="fontnormal"> <c:out value="${requestScope.PersonnelVO.personnelDetails.city}"/> <br>
</span><mifos:mifoslabel name="${ConfigurationConstants.STATE}"/>:
<span class="fontnormal"> <c:out value="${requestScope.PersonnelVO.personnelDetails.state}"/><br>
</span><mifos:mifoslabel name="Personnel.Country"/>
<span class="fontnormal"> <c:out value="${requestScope.PersonnelVO.personnelDetails.country}"/><br>
</span><mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"/>:
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.postalCode}"/><br>
<br>
</span><mifos:mifoslabel name="Personnel.Telephone"/>
<span class="fontnormal"><c:out value="${requestScope.PersonnelVO.personnelDetails.telephone}"/> </span></td>
                </tr>
              </table>
              <br>  
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>

</html-el:form>
</tiles:put>
</tiles:insert>
