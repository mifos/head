<!--
/**

* EditFeeDetails.jsp    version: 1.0



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
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<script>
function meetingpopup(){	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}

function showMeetingFrequency(){
	if (document.form1.frequency[0].checked == true){
		document.getElementById("dayDIV").style.display = "block";
		document.getElementById("weekDIV").style.display = "none";
		document.getElementById("monthDIV").style.display = "none";
		}
	else if (document.form1.frequency[1].checked == true){
		document.getElementById("dayDIV").style.display = "none";
		document.getElementById("weekDIV").style.display = "block";
		document.getElementById("monthDIV").style.display = "none";
		}
	else if (document.form1.frequency[2].checked == true){
		document.getElementById("dayDIV").style.display = "none";
		document.getElementById("weekDIV").style.display = "none";
		document.getElementById("monthDIV").style.display = "block";
		}
}

function showSchedule(){
	if (document.form1.frequencySel.options.selectedIndex == 1){
		document.getElementById("scheduleDIV").style.display = "block";
		document.getElementById("timeofchargeDiv").style.display = "none";
		}
	if (document.form1.frequencySel.options.selectedIndex == 0){
		document.getElementById("scheduleDIV").style.display = "none";
		document.getElementById("timeofchargeDiv").style.display = "block";
		}
}


function fnOnPreview(form){
	document.FeesActionForm.input.value="editFeeDetails";
	document.FeesActionForm.method.value="preview";
	document.FeesActionForm.action="feesAction.do";
	document.FeesActionForm.submit();
}


function fnOnAdmin(form){
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}


function fnOnCancel(Id){
	document.FeesActionForm.method.value="get";
	document.FeesActionForm.feeIdTemp.value=Id;
	document.FeesActionForm.action="feesAction.do";
	document.FeesActionForm.submit();
}

function fnOnView(form){
	form.method.value="search";
	form.action="feesAction.do";
	form.submit();
}

</script>
<html-el:form action="/feesAction.do?method=preview&input=editFeeDetails">


    <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        <span class="fontnormal8pt">
        <html-el:link href="javascript:fnOnAdmin(FeesActionForm)">
        <mifos:mifoslabel name="Fees.admin" bundle="FeesUIResources">
        </mifos:mifoslabel>
        </html-el:link>
         /
        <html-el:link href="javascript:fnOnView(FeesActionForm)">
        <mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources">
        </mifos:mifoslabel>
        </html-el:link>
         /
        <html-el:link href="javascript:fnOnCancel(${requestScope.fees.feeId})">
        <c:out value="${requestScope.fees.feeName}"></c:out>
        </html-el:link>
        </span>
        </td>
      </tr>
    </table>

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15">
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
			    <font class="fontnormalRedBold">
                <td class="headingorange">
                <span class="heading">
                	<c:out value="${requestScope.fees.feeName}"></c:out> - 
                </span>

                	<mifos:mifoslabel name="Fees.editfeeinformation" bundle="FeesUIResources">
            		</mifos:mifoslabel>
            	</td>
              </tr>

              <tr>
                <td class="fontnormal">
                	<mifos:mifoslabel name="Fees.editFeesMessage" bundle="FeesUIResources">
            		</mifos:mifoslabel>
            		<br><mifos:mifoslabel name="Fees.CreateFeesFieldInstruction" mandatory="yes" bundle="FeesUIResources"></mifos:mifoslabel>
       			</td>
              </tr>

            </table>

              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
				
					<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
				 
                <tr>
                  <td colspan="2" class="fontnormalbold">
                  	<mifos:mifoslabel name="Fees.feedetails" bundle="FeesUIResources">
                  	</mifos:mifoslabel>
                  	<br>
                  	<br>
                  </td>
                </tr>

                <!--<tr class="fontnormal">
                  <td width="27%" align="right">
                  <mifos:mifoslabel name="Fees.changeappliesto" mandatory="yes" bundle="FeesUIResources">
                  </mifos:mifoslabel>
                  </td>

                  <td width="73%" valign="top">
				  <select name="feeAppliesTo"  style="width:180px;">
                    <option selected><mifos:mifoslabel name="Fees.existingandfutureaccounts" bundle="FeesUIResources"> </mifos:mifoslabel>
                    <option><mifos:mifoslabel name="Fees.futureaccounts"  bundle="FeesUIResources">                  </mifos:mifoslabel>
                    </select>
                  </td>
                </tr>-->

                <tr class="fontnormal">
                  <td width="27%" align="right">
     			<c:choose>
				  <c:when test="${requestScope.fees.rateFlatFalg==1}"> 
                  	<mifos:mifoslabel name="Fees.calculatefeeas" mandatory="yes" bundle="FeesUIResources">
					</mifos:mifoslabel>
                  </c:when>
				  <c:otherwise>
					<mifos:mifoslabel name="Fees.enteramount" mandatory="yes" bundle="FeesUIResources">
                  	</mifos:mifoslabel>
                  </c:otherwise> 
				</c:choose>	
					
                  </td>

                  <td width="73%" valign="top">
                 <c:choose>
				   <c:when test="${requestScope.fees.rateFlatFalg==1}"> 
                      <mifos:mifosdecimalinput  name="fees" property="rate" size="3" decimalFmt="10.5" value="${requestScope.Context.valueObject.rateOrAmount}"></mifos:mifosdecimalinput>
                        <mifos:mifoslabel name="Fees.percentof" bundle="FeesUIResources"></mifos:mifoslabel>
						<c:forEach var="code" items="${requestScope.formula.lookUpMaster}">
						<c:if test="${code.id == fees.formulaId}">
							<c:out value="${code.lookUpValue}"></c:out>
						</c:if>
						</c:forEach>
				  </c:when>
				  <c:when test="${requestScope.fees.rateFlatFalg==0}"> 
				   		<mifos:mifosdecimalinput name="fees" property="amount" value="${requestScope.Context.valueObject.rateOrAmount}">
                  		</mifos:mifosdecimalinput>
                  </c:when> 
				 </c:choose>	


	
				  
					
                  </td>
                </tr>

                <tr class="fontnormal">
                  <td align="right" >
                  	<mifos:mifoslabel name="Fees.status" mandatory="yes" bundle="FeesUIResources">
                  	</mifos:mifoslabel>
                  </td>

                  <td valign="top">
                  <c:set var="statusList" scope="request" value="${requestScope.status.lookUpMaster}" />
						<mifos:select name="fees" property="status" style="width:136px;" >
					    	<html-el:options property="id"
											labelProperty="lookUpValue"
											collection="statusList" />
						</mifos:select>
                  </td>
                </tr>
              </table>

              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>

              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
					<html-el:submit property="previewBtn" styleClass="buttn" style="width:65px">
	                    <mifos:mifoslabel name="Fees.preview" bundle="FeesUIResources"></mifos:mifoslabel>
                   </html-el:submit>
                    &nbsp;
                    <html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:65px" onclick="javascript:fnOnCancel(${requestScope.fees.feeId})">
	                    <mifos:mifoslabel name="Fees.cancel" bundle="FeesUIResources"></mifos:mifoslabel>
                   </html-el:button>
                  </td>
                </tr>
				<html-el:hidden property="input" value="" />
                <html-el:hidden property="method" value="preview"/> 
				<html-el:hidden property="feeId" value="${requestScope.fees.feeId}"/>
				<html-el:hidden property="versionNo" value="${requestScope.fees.versionNo}"/>
				<html-el:hidden property="feeName" value="${requestScope.fees.feeName}"/>
				<html-el:hidden property="rate" value=""/>
				<html-el:hidden property="amount" value=""/>
				<html-el:hidden property="formulaId" value="${requestScope.fees.formulaId}"/>
				<html-el:hidden property="categoryId" value="${requestScope.fees.categoryId}"/>
                <html-el:hidden property="feeFrequencyTypeId" value="${requestScope.fees.feeFrequency.feeFrequencyTypeId}"/>
				<html-el:hidden property="feePaymentId" value="${requestScope.fees.feeFrequency.feePaymentId}"/>
				<html-el:hidden property="rateFlatFalg" value="${requestScope.fees.rateFlatFalg}"/>
				<html-el:hidden property="feeIdTemp" value=""/> 
				<html-el:hidden property="frequency" value="66"/>
				<html-el:hidden property="glCodeEntity.glcodeId" value="${requestScope.fees.glCodeEntity.glcodeId}"/>
	

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
