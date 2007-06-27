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
<%@taglib uri="/tags/date" prefix="date"%>
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

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
            <span class="fontnormal8pt"> <customtags:headerLink selfLink="false" /> </span>
          </td>
				</tr>
			</table>

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >              
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange">
                  <span class="heading">
                    <c:out value="${requestScope.retrievedInstance.survey.name}"/> -
                  </span>
                  <mifos:mifoslabel name="Surveys.Details" bundle="SurveysUIResources"/> 
                </td>
              </tr>
            </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td><br>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="fontnormalbold">Date of Survey: 
							          <c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,requestScope.retrievedInstance.dateConducted)}" />
                      </td>
                    </tr>
                    <tr>

                      <td class="fontnormalbold">Surveyed by: 
                        <c:out value="${requestScope.retrievedInstance.officer.displayName}"/>
                      </td>
                    </tr>
                    <tr>
                      <td class="fontnormalbold">Entered into the system by: 
                        <c:out value="${requestScope.retrievedInstance.creator.displayName}"/> 
                      </td>
                    </tr>
                    <tr>
                      <td class="fontnormal">&nbsp;</td>
                    </tr>

                    <tr>
                      <td class="fontnormal">
                        <p>
                          <mifos:mifoslabel name="Surveys.${requestScope.businessObjectType}" bundle="SurveysUIResources"/>
                          <c:out value="${requestScope.businessObjectName}"/>:
                          <br>
                          System ID #: <c:out value="${requestScope.globalNum}"/> 
                        </p>
                      </td>
                    </tr>
                  </table></td>
              </tr>
              <tr>
                <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="12"></td>

              </tr>
            </table>
            <br>
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="21%" height="30" class="drawtablerow"><span class="fontnormal8ptbold">1. What is your annual family income?</span></td>
              </tr>
              <tr>

                <td height="30" class="drawtablerow"><input name="radiobutton" type="radio" value="radiobutton" disabled="disabled">
                  Less than 10000<br>
                  <input name="radiobutton" type="radio" disabled="disabled" value="radiobutton" checked>
                  <strong>10000 to 20000</strong><br>
                  <input name="radiobutton" type="radio" value="radiobutton" disabled="disabled">
                  Above 20000 </td>
              </tr>

              <tr>
                <td height="30" class="drawtablerow">&nbsp;</td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow"><span class="fontnormal8ptbold">2. Was the training provided effective?</span></td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow"><input name="radiobutton2" type="radio" value="radiobutton" disabled="disabled">

                  Yes<br>
                  <input name="radiobutton2" type="radio" disabled="disabled" value="radiobutton" checked>
                  <strong>No</strong></td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow">&nbsp;</td>
              </tr>
              <tr>

                <td height="30" class="drawtablerow"><span class="fontnormal8ptbold">3. How was the quality of service?</span></td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow"><textarea name="textarea" cols="70" rows="10" style=" font-weight:bold; font-family: Arial, Helvetica, sans-serif; overflow:hidden;" disabled="disabled">The service was satisfactory</textarea></td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow">&nbsp;</td>
              </tr>

              <tr>
                <td height="30" class="drawtablerow"><span class="fontnormal8ptbold">4. What kind of relationship do you have with our bank?</span></td>
              </tr>
              <tr>
                <td height="30" class="drawtablerow"><input name="checkbox2" type="checkbox"  disabled="disabled" value="checkbox" checked>
                    <strong>Loan</strong><br>
                    <input name="checkbox22" type="checkbox" disabled="disabled" value="checkbox" checked >
                    <strong>Savings</strong><br>

                    <input type="checkbox" name="checkbox23" value="checkbox" disabled="disabled" >
                  Insurance</td>
              </tr>
              <tr>
                <td height="30" class="blueline">&nbsp;</td>
              </tr>
            </table>
            <br>

              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center"><input name="Button" type="button" class="buttn" value="Back to details page" onClick="location.href='ClientDetailsMeeting.htm';" style="width:135px;">
                    &nbsp;
                    <input name="Button2" type="button" class="buttn" value="Remove survey" onClick="location.href='ClientDetailsMeeting.htm';" style="width:135px;"></td>
                </tr>
              </table>              <br>
              <br>

          </td>
        </tr>
      </table>
  </tiles:put>
</tiles:insert>
