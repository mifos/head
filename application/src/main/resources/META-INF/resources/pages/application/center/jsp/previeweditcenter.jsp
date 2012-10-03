<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<tiles:insert definition=".detailsCustomer">
<tiles:put name="body" type="string">
<span id="page.id" title="previeweditcenter"></span>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script language="javascript">

  function goToEditPage(){
	centerCustActionForm.action="centerCustAction.do?method=editPrevious";
	centerCustActionForm.submit();
  }
  function goToCancelPage(){
	centerCustActionForm.action="centerCustAction.do?method=cancel";
	centerCustActionForm.submit();
  }
</script>
<html-el:form action="centerCustAction.do?method=update" onsubmit="func_disableSubmitBtn('submitButton');">
<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
	   var="BusinessKey" />
		<html-el:hidden property="input" value="manage"/>
		<html-el:hidden property="customerId" value="${BusinessKey.customerId}"/>
		<html-el:hidden property="globalCustNum" value="${BusinessKey.globalCustNum}"/>
		<html-el:hidden property="versionNo" value="${BusinessKey.versionNo}"/>
		<%--  <html-el:hidden property="customerAddressDetail.customerAddressId" value="${BusinessKey.customerAddressDetail.customerAddressId}"/>  --%>

     <td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain">
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="bluetablehead05">
	            <span class="fontnormal8pt">
	            	<customtags:headerLink/>
	            </span>
	                        </td>
          </tr>
        </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15">
          <table width="93%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td class="headingorange">
                  <span class="heading"><c:out value="${BusinessKey.displayName}"/>
                  - </span>
                  
                  <fmt:message key="Center.PreviewInformationCentre">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				   </fmt:message>                  
                  
              </td>
            </tr>
            <tr>
             <td class="fontnormal"><mifos:mifoslabel name="Center.CreatePreviewPageInstruction" bundle="CenterUIResources"></mifos:mifoslabel>
                    &nbsp;      
                    
                    <fmt:message key="Center.EditPageCancelInstruction">
							<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
				   </fmt:message>    
                    
             </td>

            </tr>
          </table>
          <br>

          <table width="93%" border="0" cellpadding="0" cellspacing="0">
           <!-- Error Display if any -->
                	<font class="fontnormalRedBold"><span id="previeweditcenter.error.message"><html-el:errors bundle="CenterUIResources" /></span> </font>

           <tr>
             <td width="100%" class="fontnormal">
                <span class="fontnormalbold">
                <mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel>
                <span class="fontnormal"><c:out value="${sessionScope.centerCustActionForm.displayName}"/><br></span>
              </td>
           </tr>
           <tr>
             <td width="100%" class="fontnormal">
              	<span class="fontnormalbold">
              	<mifos:mifoslabel name="Center.LoanOfficer" bundle="CenterUIResources"></mifos:mifoslabel>
                <span class="fontnormal">
                	<c:forEach var="LO" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'loanOfficers')}">
						<c:if test = "${LO.personnelId == sessionScope.centerCustActionForm.loanOfficerId}">
							<c:out value="${LO.displayName}"/><br>
						</c:if>
					</c:forEach>
                </span>
              </td>
            </tr>
            <tr id="Center.ExternalId">
             <td width="100%" class="fontnormal">
                <span class="fontnormalbold">
		        <mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" isColonRequired="yes" keyhm="Center.ExternalId" isManadatoryIndicationNotRequired="yes"/>
		        <span class="fontnormal"> <c:out value="${sessionScope.centerCustActionForm.externalId}"/></span><br>
		      </td>
		      </tr>


             <tr>
             <td width="100%" class="fontnormal">
             <span class="fontnormalbold">
 				<mifos:mifoslabel name="Center.MfiJoiningDate" bundle="CenterUIResources"></mifos:mifoslabel>:
				<span class="fontnormal"> <c:out value="${sessionScope.centerCustActionForm.mfiJoiningDate}"/> <br></span><br>
                </span>
             </td>
             </tr>

             <tr>
             <td width="100%" class="fontnormal">
                <span class="fontnormalbold">
                <mifos:mifoslabel name="Center.OfficialTitlesHeading" bundle="CenterUIResources"></mifos:mifoslabel>
                <br>

                  	  <c:forEach var="position" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'positions')}">
						 <c:forEach var="cp" items="${sessionScope.centerCustActionForm.customerPositions}">
							<c:if test="${position.positionId==cp.positionId}">
								<c:out value="${position.name}"/>:
								<c:forEach var="client" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clients')}">
		    	            	 	<c:if test="${client.customerId==cp.customerId}">
			                	 		<span class="fontnormal"><c:out value="${client.displayName}"/></span>
									</c:if>
								</c:forEach>
							</c:if>
						</c:forEach><br>
    				  </c:forEach>
    				  <br>
    				  </span>
    		</tr>


    		<tr id="Center.Address">
             <td width="100%" class="fontnormal">
            		<span class="fontnormalbold"><mifos:mifoslabel name="Center.Address" bundle="CenterUIResources" keyhm="Center.Address" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
            		<span class="fontnormal"><br></span>
            		<span class="fontnormal">
            		<c:out value="${sessionScope.centerCustActionForm.address.displayAddress}"/><br></span>
            </td>
            </tr>

            <tr id="Center.City">
             <td width="100%" class="fontnormal">
				<span class="fontnormalbold">
            		<mifos:mifoslabel name="${ConfigurationConstants.CITY}" keyhm="Center.City" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"/>
            		<span class="fontnormal"><c:out value="${sessionScope.centerCustActionForm.address.city}"/><br></span>
            </td>
            </tr>

            <tr id="Center.State">
             <td width="100%" class="fontnormal">
             <span class="fontnormalbold">
            		<mifos:mifoslabel name="${ConfigurationConstants.STATE}" isColonRequired="yes" keyhm="Center.State" isManadatoryIndicationNotRequired="yes"/>
            		<span class="fontnormal"><c:out value="${sessionScope.centerCustActionForm.address.state}"/><br></span>
            </td>
            </tr>

            <tr id="Center.Country">
             <td width="100%" class="fontnormal">
             <span class="fontnormalbold">
            		<mifos:mifoslabel name="Center.Country" bundle="CenterUIResources" keyhm="Center.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
            		<span class="fontnormal"> <c:out value="${sessionScope.centerCustActionForm.address.country}"/><br></span>
            </td>
            </tr>

            <tr id="Center.PostalCode">
             <td width="100%" class="fontnormal">
             <span class="fontnormalbold">
            		<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" keyhm="Center.PostalCode" isManadatoryIndicationNotRequired="yes"/>
            		<span class="fontnormal"><c:out value="${sessionScope.centerCustActionForm.address.zip}"/></span>
            </td>
            </tr>

            <tr id="Center.PhoneNumber">
             <td width="100%" class="fontnormal">
             <br>
             <span class="fontnormalbold">
            		<mifos:mifoslabel name="Center.Telephone" bundle="CenterUIResources" keyhm="Center.PhoneNumber" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
            		<span class="fontnormal"><c:out value="${sessionScope.centerCustActionForm.address.phoneNumber}"/> </span>
			        </span>
            </td>
            </tr>
		
			 <tr>
             <td width="100%" class="fontnormal">
             <br>
             		<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
			        <span class="fontnormalbold">
					<mifos:mifoslabel name="Center.AdditionalInformationHeading" bundle="CenterUIResources"></mifos:mifoslabel>
					<span class="fontnormalbold"><br>
					<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
						 <c:forEach var="customField" items="${sessionScope.centerCustActionForm.customFields}">
							<c:if test="${cf.fieldId==customField.fieldId}">
								<mifos:mifoslabel name="${cf.lookUpEntityType}" bundle="CenterUIResources"></mifos:mifoslabel>:
					         	<span class="fontnormal"><c:out value="${customField.fieldValue}"/></span><br>
							</c:if>
						</c:forEach>
    				  </c:forEach>
    				 
    				</span>
				    </span>
				    </c:if>
					<br>
					<%-- Edit center information button --%>
					<html-el:button styleId="previeweditcenter.button.previous" onclick="goToEditPage()" property = "editButton" styleClass="insidebuttn">
			        <mifos:mifoslabel name="button.previous" bundle="CenterUIResources"></mifos:mifoslabel></html-el:button>

				
			   </td>
              </tr>
              
            </table>

           	  <!-- Submit and cancel buttons -->
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">&nbsp;
                  	<html-el:submit styleId="previeweditcenter.button.submit" property="submitButton" styleClass="buttn">
                  	<mifos:mifoslabel name="button.submit" bundle="CenterUIResources" ></mifos:mifoslabel></html-el:submit>
                  	&nbsp;
				  	&nbsp;
				  	<html-el:button onclick="goToCancelPage();" property = "cancelButton" styleClass="cancelbuttn">
				  	<mifos:mifoslabel name="button.cancel" bundle="CenterUIResources"></mifos:mifoslabel></html-el:button>

               	  </td>
               </tr>
             </table>
            <!-- Submit and cancel buttons end -->
            <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
          </td>
          </tr>
      </table>
      <br>
    </td>
</html-el:form>

</tiles:put>
</tiles:insert>

