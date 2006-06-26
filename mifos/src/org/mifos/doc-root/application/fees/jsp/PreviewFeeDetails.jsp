<!--
/**

* PreviewFeeDetails.jsp    version: 1.0



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
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">

<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT> 

<script>
function meetingpopup(){	window.open("schedulemeetingpopup.htm",null,"height=400,width=800,status=yes,scrollbars=yes,toolbar=no,menubar=no,location=no");
}

function fnOnCancel(Id){
	document.FeesActionForm.method.value="get";
	document.FeesActionForm.feeIdTemp.value=Id;
	document.FeesActionForm.action="feesAction.do";
	document.FeesActionForm.submit();
}

function fnOnAdmin(form){
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}


function fnOnSubmit(){
	document.FeesActionForm.method.value="update";
	document.FeesActionForm.action="feesAction.do";
	document.FeesActionForm.submit();
}


function fnOnEditFeeInformation(form){
	form.method.value="previous";
	form.input.value="previewFeeDetails";
	form.action="feesAction.do";
	form.submit();
}

function fnOnView(form){
	form.method.value="search";
	form.action="feesAction.do";
	form.submit();
}


</script>


<html-el:form action="/feesAction.do?method=update" onsubmit="return func_disableSubmitBtn('submitBtn');">
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
         <c:out value="${requestScope.fees.feeName}"/>
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
                <td class="headingorange">
                	<span class="heading">

                	<c:out value="${requestScope.fees.feeName}"/> - 

                	</span>
                	<mifos:mifoslabel name="Fees.previewfeeinformation" bundle="FeesUIResources">
            		</mifos:mifoslabel>
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
                	<mifos:mifoslabel name="Fees.messageForPreviewFeeDetails" bundle="FeesUIResources">
            		</mifos:mifoslabel>
                 </td>
              </tr>
            </table>
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
				 <tr>
					<td class="fontnormalbold">
					<font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
					</td>
				 </tr>
                 
                <tr>
                  <td width="100%" height="23" class="fontnormal">
                  <!--<mifos:mifoslabel name="Fees.previewChangeAppliesTo" bundle="FeesUIResources">
            		</mifos:mifoslabel><br>-->
				  <span class="fontnormalbold">
				  <c:choose>
					  <c:when test="${requestScope.fees.categoryId==5 && requestScope.fees.rateFlatFalg==1}">					  	
					     <mifos:mifoslabel name="Fees.amountcalculatedas" bundle="FeesUIResources"></mifos:mifoslabel>
                      </c:when>
                      <c:otherwise>
						 <mifos:mifoslabel name="Fees.amount" bundle="FeesUIResources"></mifos:mifoslabel>
					  </c:otherwise>
                 </c:choose>
                 </span>
				  <c:out value="${requestScope.fees.rateOrAmount}"></c:out>
				  <c:if test="${requestScope.fees.rateFlatFalg==1}">
					<mifos:mifoslabel name="Fees.ofa" bundle="FeesUIResources"></mifos:mifoslabel>
					<c:forEach var="code" items="${requestScope.formula.lookUpMaster}">
						<c:if test="${code.id == fees.formulaId}">
							<c:out value="${code.lookUpValue}"></c:out>
						</c:if>
					</c:forEach>
				  </c:if><br>

                  	  

				  <span class="fontnormalbold">
				  <mifos:mifoslabel name="Fees.status" bundle="FeesUIResources">
                  	</mifos:mifoslabel></span>
					<c:forEach var="code" items="${requestScope.status.lookUpMaster}">
						<c:if test="${code.id == fees.status}">
							<c:out value="${code.lookUpValue}"></c:out>
						</c:if>
					</c:forEach>
					<br>
				<br>

      <span class="fontnormal">
      </span>

	<html-el:button property="editBtn" styleClass="insidebuttn" onclick="javascript:fnOnEditFeeInformation(FeesActionForm)">
	   <mifos:mifoslabel name="Fees.edit" bundle="FeesUIResources"></mifos:mifoslabel>
    </html-el:button>
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
				 	<html-el:submit property="submitBtn" styleClass="buttn"  style="width:65px"> 
	                    <mifos:mifoslabel name="Fees.submit" bundle="FeesUIResources"></mifos:mifoslabel>
                   </html-el:submit>
                    &nbsp;
                    <html-el:button property="cancelBtn" styleClass="cancelbuttn" style="width:65px" onclick="javascript:fnOnCancel(${requestScope.fees.feeId})">
	                    <mifos:mifoslabel name="Fees.cancel" bundle="FeesUIResources"></mifos:mifoslabel>
                   </html-el:button>
                  </td>
                </tr>
				<html-el:hidden property="input" value="previewFeeDetails" />
                <html-el:hidden property="method" value="get"/> 
				<html-el:hidden property="feeId" value="${requestScope.fees.feeId}"/>
				<html-el:hidden property="versionNo" value="${requestScope.fees.versionNo}"/>
				<html-el:hidden property="rateFlatFalg" value="${requestScope.fees.rateFlatFalg}"/>
				<html-el:hidden property="feeIdTemp" value="${requestScope.fees.feeId}"/> 
              </table>

             <br></td>
          </tr>
        </table>
      <br></td>
  </tr>
</table>
</html-el:form>
</tiles:put>
</tiles:insert>