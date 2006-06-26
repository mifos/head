<!-- 

/**

 * createmeeting.jsp    version: 1.0

 

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

 */

-->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">


		<script>
function showMeetingFrequency(){

	var fraq = document.getElementsByName("frequency");
	if (document.meetingActionForm.frequency[0].checked == true){
		document.getElementById("weekDIV").style.display = "block";
		fraq[1].disabled=true;
		document.getElementById("months").style.color="gray";
		document.getElementById("monthDIV").style.display = "none";
		document.getElementsByName("recurWeek")[0].disabled=true;
		}
	else if (document.meetingActionForm.frequency[1].checked == true){
	
		fraq[0].disabled=true;
				document.getElementById("weeks").style.color="gray";
		
		document.getElementById("weekDIV").style.display = "none";
		document.getElementById("monthDIV").style.display = "block";
		
			//get the month type
			if(document.getElementsByName("monthType")[0].checked == true)
			{
				document.getElementsByName("monthType")[1].disabled=true;
				document.getElementsByName("monthRank")[0].disabled=true;
				document.getElementsByName("monthWeek")[0].disabled=true;
				document.getElementsByName("monthMonthRank")[0].disabled=true;
				document.getElementsByName("monthMonth")[0].disabled=true;
				document.getElementById("labelThe").style.color="gray";
				document.getElementById("labelOfEvery_1").style.color="gray";
				document.getElementById("labelMonths_2").style.color="gray";
				
				
			}
			else if ( document.getElementsByName("monthType")[1].checked == true)
			{
				document.getElementsByName("monthMonthRank")[0].disabled=true;
				document.getElementsByName("monthType")[0].disabled=true;		
				document.getElementsByName("monthDay")[0].disabled=true;		
				document.getElementsByName("monthMonth")[0].disabled=true;
				document.getElementById("labelDay").style.color="gray";
				document.getElementById("labelOfEvery").style.color="gray";
				document.getElementById("labelMonths_1").style.color="gray";
				
			}
		
		
		
		}
}
function goToCancelPage(){
	document.meetingActionForm.method.value="cancel";
	meetingActionForm.submit();
	
	
  }
</script>

		<%-- <html-el:form action="/MeetingAction.do" onsubmit="return validateMeetingActionForm(this);">
		<html-el:javascript formName="/MeetingAction" bundle="MeetingResources"/> --%>
		<html-el:form action="/MeetingAction.do">




			<c:choose>
			  	<c:when test="${sessionScope.meetingActionForm.input eq 'ClientDetails'}">
	  <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            	<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> 
					 /
	            </span>
	            <c:if test="${!empty sessionScope.linkValues.customerCenterName}">
	               <span class="fontnormal8pt">
	               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerCenterName}"/>
			       	</a>  /  </span>
		    	</c:if>
	           <c:if test="${!empty sessionScope.linkValues.customerParentName}">
	               <span class="fontnormal8pt">
	               	<a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
				       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
			       	</a>  /  </span>
		    	</c:if>
	            <!-- Name of the client -->
	            <span class="fontnormal8pt">
	            	<a href="clientCreationAction.do?method=get&customerId=<c:out value="${sessionScope.linkValues.customerId}"/>">
	            	<c:out value="${sessionScope.linkValues.customerName}"/>
	            	</a>
	            </span>
            </td>
          </tr>
        </table>
	  </c:when>
				<c:when
					test="${sessionScope.meetingActionForm.input eq 'CenterDetails'}">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt"> <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            			<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a> / 
	            	</span> <span class="fontnormal8pt"> <a
								href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
							<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>
							</td>
						</tr>
					</table>
				</c:when>
				<c:otherwise>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="bluetablehead05"><span class="fontnormal8pt"> <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	            			<c:out value="${sessionScope.linkValues.customerOfficeName}"/></a>
							/ <c:if
								test="${!empty sessionScope.linkValues.customerParentName}">
								<a
									href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
								<c:out value="${sessionScope.linkValues.customerParentName}" />
								</a> /  
		    </c:if> <a
								href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
							<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span></td>
						</tr>
					</table>
				</c:otherwise>
			</c:choose>
				<!-- 			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350"  valign="top" bgcolor="#FFFFFF"> -->
			<table width="90%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15"><span
						class="heading"><c:out
						value="${sessionScope.linkValues.customerName}" /> - </span><span
						class="headingorange"><mifos:mifoslabel
						name="meeting.labelMeetingSchedule" bundle="MeetingResources" />
					</span><!-- END People need to pass this information to me in rqquest parameter -->
					</td>
				</tr>
				<!-- 					<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="10"></td>
						</tr> -->
			</table>

			<table width="90%" border="0" cellpadding="0" cellspacing="0">
				<tr align="center">
					<td align="left" valign="top" class="paddingL15T15"><font
						class="fontnormalRedBold"><html-el:errors
						bundle="MeetingResources" /> </font></td>
				</tr>
				<tr>
					<td align="left" valign="top" class="paddingL15T15"><span
						class="fontnormal"> <mifos:mifoslabel
						name="meeting.labelCreateInstruction" mandatory="yes"
						bundle="MeetingResources">
					</mifos:mifoslabel> </span><br>
					<br>
					<table width="90%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							<td align="right" valign="top"><mifos:mifoslabel
								name="meeting.labelFrequencyOfMeeting" mandatory="yes"
								bundle="MeetingResources">
							</mifos:mifoslabel></td>
							<td align="left" valign="top"
								style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
							<table width="98%" border="0" cellspacing="0" cellpadding="2">
								<tr valign="top" class="fontnormal">
									<!-- 
											<td width="21%"><html-el:radio property="frequency" value="1"
												onclick="showMeetingFrequency();" /> <mifos:mifoslabel
												name="meeting.labelDays" bundle="MeetingResources" /></td>
												-->



									<td width="24%"><html-el:radio property="frequency" value="1"
										onclick="showMeetingFrequency();" /> <SPAN id="weeks"> <mifos:mifoslabel
										name="meeting.labelWeeks" bundle="MeetingResources" /> </SPAN>
									</td>
									<td width="55%"><html-el:radio property="frequency" value="2"
										onclick="showMeetingFrequency();" /> <SPAN id="months"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </SPAN>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="22%" align="right" valign="top">&nbsp;</td>
							<td width="59%" align="left" valign="top"
								style="border: 1px solid #CECECE;">

							<div id="weekDIV" style="height:40px; width:380px; "><mifos:mifoslabel
								name="meeting.labelRecurWeeks" bundle="MeetingResources" />



							<table border="0" cellspacing="0" cellpadding="2">
								<tr class="fontnormal">
									<td colspan="4"><mifos:mifoslabel
										name="meeting.labelRecurEvery" bundle="MeetingResources" /> <mifos:mifosnumbertext
										property="recurWeek" size="3" maxlength="3" /> <mifos:mifoslabel
										name="meeting.labelWeeks" bundle="MeetingResources" /> <c:set
										var="weekDaysList" scope="request"
										value="${requestScope.WeekDayList.lookUpMaster}" /> <mifos:select
										property="weekDay">
										<html-el:options collection="weekDaysList" property="id"
											labelProperty="lookUpValue" />
									</mifos:select></td>
								</tr>
							</table>
							</div>
							<div id="monthDIV" style="height:60px; width:380px; "><mifos:mifoslabel
								name="meeting.labelRecurMonths" bundle="MeetingResources" /> <br>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr class="fontnormal">
									<td><html-el:radio property="monthType" value="1" /></td>
									<td><span id="labelDay"> <mifos:mifoslabel
										name="meeting.labelDay" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="monthDay" size="3"
										maxlength="2" /> <span id="labelOfEvery"> <mifos:mifoslabel
										name="meeting.labelOfEvery" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="monthMonth" size="3"
										maxlength="3" /> <span id="labelMonths_1"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </span>

									</td>
								</tr>
								<tr class="fontnormal">
									<td><html-el:radio property="monthType" value="2" /></td>
									<td><span id="labelThe"> <mifos:mifoslabel
										name="meeting.labelThe" bundle="MeetingResources" /> </span>


									<c:set var="weekrankList" scope="request"
										value="${requestScope.WeekRankList.lookUpMaster}" /> <mifos:select
										property="monthRank">

										<html-el:options collection="weekrankList" property="id"
											labelProperty="lookUpValue" />

									</mifos:select> <mifos:select property="monthWeek">
										<html-el:options collection="weekDaysList" property="id"
											labelProperty="lookUpValue" />
									</mifos:select> <span id="labelOfEvery_1"> <mifos:mifoslabel
										name="meeting.labelOfEvery" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="monthMonthRank" size="3"
										maxlength="3" /> <span id="labelMonths_2"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </span>


									</td>
								</tr>
							</table>
							</div>

							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right">&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="meeting.labelPlaceOfMeeting" bundle="MeetingResources"
								mandatory="yes" /></td>
							<td><html-el:text property="meetingPlace" /></td>
						</tr>

					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<SCRIPT> showMeetingFrequency();</SCRIPT> <br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<!-- Next are submit and cancel button -->
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px;">
								<mifos:mifoslabel name="meeting.button.save"
									bundle="MeetingResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp; <html-el:button
								onclick="goToCancelPage();" property="cancelButton"
								value="Cancel" styleClass="cancelbuttn" style="width:70px">
								<mifos:mifoslabel name="office.button.cancel"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:button></td>

						</tr>
					</table>
					<br>
				</tr>
			</table>
			<!--  <br>
					</td>
				</tr>
			</table> -->
			<html-el:hidden property="method" value="update" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="meetingId"
				value="${requestScope.Context.valueObject.meetingId}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.Context.valueObject.versionNo}" />

		</html-el:form>
	</tiles:put>

</tiles:insert>


