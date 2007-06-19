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
    		<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
		<html-el:form action="surveyInstanceAction.do?method=create_entry">

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
                  <c:out value="${requestScope.businessObjectName}" /> - <span class="headingorange"><mifos:mifoslabel name="Surveys.attach_survey" bundle="SurveysUIResources" />
/span>
                </td>
              </tr>
					  </table>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">


              <tr>
                <td><br><html-el:errors bundle="SurveysUIResources"/>
                    <span class="fontnormal">
                      <mifos:mifoslabel name="Surveys.choose_page_instructions" bundle="SurveysUIResources" />
                    </span>
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
                <td width="24%" align="right" class="fontnormal"><mifos:mifoslabel name="Surveys.select_survey" bundle="SurveysUIResources" />:
</td>

                <td width="76%" align="left" valign="top" class="fontnormal">
                  <html-el:select property="value(surveyId)">
                    <c:forEach var="survey" items="${requestScope.surveysList}">
                      <option value="${survey.surveyId}"><c:out value="${survey.name}"/></option>
                    </c:forEach>
                  </html-el:select>
                </td>
              </tr>
            </table>
            <br>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp;</td>
              </tr>
            </table> 

            <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                  <html-el:submit style="width:65px;" property="button" styleClass="buttn">
                    <mifos:mifoslabel name="Surveys.button.continue" bundle="SurveysUIResources" />
                  </html-el:submit>&nbsp; 
                  <html-el:button property="cancelButton" style="width:65px;" styleClass="cancelbuttn" onclick="window.location='adminAction.do?method=load">
                    <mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
                  </html-el:button>
                </td>
              </tr>
            </table>

            <br/>
            <br/>
          </td>
        </tr>
      </table>
      <html-el:hidden property="value(globalNum)" value="${param.globalNum}"/>
    </html-el:form>
  </tiles:put>
</tiles:insert>
