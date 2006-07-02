<!--
/**

* CreateFeesPreview.jsp    version: 1.0



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
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>



<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT> 
<script>
function meetingpopup(){	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}

function fnEdit(form)
{
	form.method.value="previous";
	form.action="feesAction.do";
	form.submit();
}
function fnCancel(form)
{
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}

</script>

<html-el:form action="/feesAction.do?method=create" onsubmit="return func_disableSubmitBtn('submitBtn');">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="350" align="left" valign="top" bgcolor="#FFFFFF">
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
                  <td width="27%">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
						<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
						</td>
                        <td class="timelineboldgray">
                        	<mifos:mifoslabel name="Fees.feeinformation" bundle="FeesUIResources">
                        	</mifos:mifoslabel>
                        </td>
                      </tr>
                    </table>
                  </td>
                  <td width="73%" align="right">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td>
	                        <img src="pages/framework//images/timeline/bigarrow.gif" width="17" height="17">
                        </td>
                        <td class="timelineboldorange">
                        	<mifos:mifoslabel name="Fees.review" bundle="FeesUIResources">
                        	</mifos:mifoslabel>
                        	 &amp;
                         	<mifos:mifoslabel name="Fees.submit" bundle="FeesUIResources">
                        	</mifos:mifoslabel>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table></td>
          </tr>
        </table>
        <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
          <tr>
            <td align="left" valign="top" class="paddingleftCreates">
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="headingorange">
                  	<span class="heading">
	                  	<mifos:mifoslabel name="Fees.definenewfee" bundle="FeesUIResources"></mifos:mifoslabel> - 
                  	</span>
                  	<mifos:mifoslabel name="Fees.review" bundle="FeesUIResources"></mifos:mifoslabel>
                  	&amp;
                  	<mifos:mifoslabel name="Fees.submit" bundle="FeesUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
                <tr>
                  <td class="fontnormal">
                  <mifos:mifoslabel name="Fees.PreviewFeesInstruction" bundle="FeesUIResources"></mifos:mifoslabel>
				  <mifos:mifoslabel name="Fees.CreateFeesCancelInstruction" bundle="FeesUIResources"></mifos:mifoslabel>
                  </td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
			    <tr>
					<td class="fontnormal">
					<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
					</td>
				 </tr>
	             <tr>
                  <td width="100%" height="23" class="fontnormal">
                  <span class="fontnormalbold">
                  <mifos:mifoslabel name="Fees.previewFeeDetails" bundle="FeesUIResources">
                  </mifos:mifoslabel>
                  </span>
				    <br>
				    <span class="fontnormalbold">
				    <mifos:mifoslabel name="Fees.feename" bundle="FeesUIResources"></mifos:mifoslabel>
				    </span>
					<c:out value="${requestScope.fees.feeName}"></c:out><br>
					<span class="fontnormalbold">
					<mifos:mifoslabel name="Fees.feeappliesto" bundle="FeesUIResources"></mifos:mifoslabel>
					</span>
					<c:forEach var="code" items="${requestScope.catagory.lookUpMaster}">
						<c:if test="${code.id == fees.categoryId}">
							<c:out value="${code.lookUpValue}"></c:out>
						</c:if>
					</c:forEach>	<br>
					

					<c:choose>
					  <c:when test="${requestScope.fees.categoryId!=5}">
					  <span class="fontnormalbold">
					     <mifos:mifoslabel name="Fees.defaultFees" bundle="FeesUIResources"></mifos:mifoslabel>
					  </span>
								<c:choose>
									<c:when test="${sessionScope.FeesActionForm.adminCheck=='Yes'}">
										<mifos:mifoslabel name="Fees.DefaultFeeYes" bundle="FeesUIResources"></mifos:mifoslabel><br>
									</c:when>
									<c:otherwise>
										<mifos:mifoslabel name="Fees.DefaultFeeNo" bundle="FeesUIResources"></mifos:mifoslabel><br>
									</c:otherwise>
								</c:choose>
                      </c:when>
                     </c:choose>   

					<span class="fontnormalbold">
					<mifos:mifoslabel name="Fees.frequency" bundle="FeesUIResources"></mifos:mifoslabel>
					</span>
                    <c:choose>
					  <c:when test="${requestScope.fees.feeFrequency.feeFrequencyTypeId==1}">
					     <mifos:mifoslabel name="Fees.periodic" bundle="FeesUIResources"></mifos:mifoslabel>
                      </c:when>
                      <c:otherwise>
						 <mifos:mifoslabel name="Fees.oneTime" bundle="FeesUIResources"></mifos:mifoslabel>
					  </c:otherwise>
                     </c:choose>   <br>
					

					<c:choose>
					  <c:when test="${requestScope.fees.feeFrequency.feeFrequencyTypeId==2}">
					  	<span class="fontnormalbold">
						<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel>
						</span> 
							<c:forEach var="code" items="${requestScope.payment.lookUpMaster}">
								<c:if test="${code.id == fees.feeFrequency.feePaymentId}">
									<c:out value="${code.lookUpValue}"></c:out>
								</c:if>
							</c:forEach>
                      </c:when>
                      <c:otherwise>
					        <span class="fontnormalbold">
							<mifos:mifoslabel name="Fees.timeToCharge" bundle="FeesUIResources"></mifos:mifoslabel></span>							
							<mifos:mifoslabel name="meeting.labelRecurEvery" bundle="MeetingResources" />							
							<c:out value="${requestScope.fees.feeFrequency.feeMeetingFrequency.meetingDetails.recurAfter}"></c:out>
							


								<c:if test="${requestScope.fees.feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId==1}">
									<mifos:mifoslabel name="meeting.labelWeeks" bundle="FeesUIResources" />
								</c:if>
								<c:if test="${requestScope.fees.feeFrequency.feeMeetingFrequency.meetingDetails.recurrenceType.recurrenceId==2}">
									<mifos:mifoslabel name="meeting.labelMonths" bundle="FeesUIResources" />
								</c:if>


					  </c:otherwise>
                     </c:choose>

					<br>

					
					<br>
                      

                  
				<span class="fontnormalbold">
				      	<mifos:mifoslabel name="Fees.feecalculation" bundle="FeesUIResources"></mifos:mifoslabel>
				</span>
				<br>
				 <c:choose>
					  <c:when test="${requestScope.fees.categoryId==5 && requestScope.fees.rateFlatFalg==1}">
					  	<span class="fontnormalbold">
					     <mifos:mifoslabel name="Fees.amountcalculatedas" bundle="FeesUIResources"></mifos:mifoslabel>
					     </span>
                      </c:when>
                      <c:otherwise>
                      <span class="fontnormalbold">
						 <mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
						 </span>
					  </c:otherwise>
                 </c:choose>
 				
				<c:out value="${requestScope.fees.rateOrAmount}"></c:out>

				<c:if test="${requestScope.fees.rateFlatFalg==1}">
					<mifos:mifoslabel name="Fees.ofa" bundle="FeesUIResources"></mifos:mifoslabel>
					<c:forEach var="code" items="${requestScope.formula.lookUpMaster}">
						<c:if test="${code.id == fees.formulaId}">
							<c:out value="${code.lookUpValue}"></c:out>
						</c:if>
					</c:forEach>
				</c:if>
				<br>
				<br>
				<span class="fontnormalbold">
				      	<mifos:mifoslabel name="Fees.accounting" bundle="FeesUIResources"></mifos:mifoslabel>
				<br>
				<mifos:mifoslabel name="Fees.GLCode" bundle="FeesUIResources"></mifos:mifoslabel>
				</span>
					<c:forEach var="glCodeList" items="${requestScope.glCodeList}"> 
						<c:if test="${glCodeList.glcodeId == fees.glCodeEntity.glcodeId}">
								<c:out value="${glCodeList.glcode}"/>
						</c:if>
					</c:forEach><br>
                    <br>
                    <br>

      			<span class="fontnormal"></span>

				<html-el:button property="editBtn" styleClass="insidebuttn" onclick="javascript:fnEdit(this.form)">
	                 <mifos:mifoslabel name="Fees.edit" bundle="FeesUIResources"></mifos:mifoslabel>
                </html-el:button>
                </td>
                </tr>
              </table>

              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
					<html-el:submit property="submitBtn" styleClass="buttn" style="width:70px;">
	                  	<mifos:mifoslabel name="Fees.submit" bundle="FeesUIResources"></mifos:mifoslabel>
                  	</html-el:submit>
                    &nbsp;
					<html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:70px" onclick="javascript:fnCancel(this.form)">
	                    <mifos:mifoslabel name="Fees.cancel" bundle="FeesUIResources"></mifos:mifoslabel>
                    </html-el:button>
                    </td>
                </tr>
				<html-el:hidden property="method" value="create"/>
				<html-el:hidden property="input" value=""/>
             </table>
            <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
<br>

</html-el:form>
</tiles:put>
</tiles:insert>
