<!-- /**

* viewClientDetails.jsp    version: 1.0



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
 -->
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<!-- Tils definition for the header and menu -->
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="surveyInstanceAction.do?method=create">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
            <span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
          </td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">

					  <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="heading">
                  <c:out value="${requestScope.businessObjectName}" /> - <span class="headingorange">Attach survey</span>
                </td>
              </tr>
					  </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td><br>
                    <span class="fontnormal">Select a survey  from the list below. 
                    Then click Continue. Click Cancel to return to client details
                    without submitting information</span>
                </td>
              </tr>
              <tr>
                <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>
              </tr>
            </table>
            <br/>

            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td colspan="2" align="left" valign="top" class="fontnormal">&nbsp;</td>
              </tr>
              <tr>
                <td width="24%" align="right" class="fontnormal">Select survey: </td>

                <td width="76%" align="left" valign="top" class="fontnormal">
                  <select name="survey">
                    <c:forEach var="survey" items="${requestScope.surveysList}">
                      <option value="${survey.surveyId}"><c:out value="${survey.name}"/></option>
                    </c:forEach>
                  </select>
                </td>
              </tr>
            </table>
            <br>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table> 

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                  <input name="Button3" type="button" class="buttn" value="Continue" style="width:65px;" onClick="gotopage();"/>
                  &nbsp;
                  <input name="Button32" type="button" class="cancelbuttn" value="Cancel" onClick="location.href='ClientDetailsMeeting.htm';" style="width:65px;"/>
                </td>
              </tr>
            </table>              
            <br/>
            <br/>
          </td>
        </tr>
      </table>
      <html-el:hidden property="globalAccountNum" value="${BusinessKey.customerAccount.globalAccountNum}" />
    </html-el:form>
  </tiles:put>
</tiles:insert>
