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

		<html-el:form action="surveyInstanceAction.do?method=preview">
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
                          <mifos:mifoslabel name="Surveys.${sessionScope.businessObjectType.value}" bundle="SurveysUIResources"/>:
                          <c:out value="${requestScope.businessObjectName}"/>
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


              <c:forEach varStatus="status" var="response" items="${requestScope.instanceResponses}">
                <tr>
                  <td width="21%" height="30" class="drawtablerow">
                    <span class="fontnormal8ptbold">  
                      <c:out value="${status.index + 1}"/>. <c:out value="${response.question.questionText}"/>
                    </span>
                  </td>
                </tr>
                <tr>

                  <td height="30" colspan="2" class="drawtablerow fontnormal8pt">
                   <mifoscustom:surveyquestion questionId="${response.question.questionId}" isDisabled="true" value="${response}"/>
                  </td>

                </tr>
                <tr>
                  <td height="30" class="drawtablerow">&nbsp;</td>
                </tr>
              </c:forEach>

            </table>
            <br>



                  <table width="93%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center">
                        <html-el:button property="calcelButton" style="width:135px;" styleClass="buttn" onclick="window.location='${requestScope.returnUrl}'">
                          <mifos:mifoslabel name="Surveys.button.backtodetailspage" bundle="SurveysUIResources" />
                        </html-el:button>&nbsp; 
                        <html-el:button property="cancel" style="width:135px;" onclick="window.location='surveyInstanceAction.do?method=delete&value(instanceId)=${requestScope.retrievedInstance.instanceId}&value(surveyType)=${sessionScope.businessObjectType.value}'" styleClass="buttn">
                          <mifos:mifoslabel name="Surveys.button.delete" bundle="SurveysUIResources" />
                        </html-el:button>
                      </td>
                    </tr>
                  </table>

          </td>
        </tr>
      </table>
    </html-el:form>
  </tiles:put>
</tiles:insert>
