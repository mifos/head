<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/officetags" prefix="office"%>

<tiles:insert definition=".withmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
  
   function goToCancelPage(){
	clientTransferActionForm.action="clientTransferAction.do?method=cancel";
	clientTransferActionForm.submit();
  }
</script>
		<html-el:form
			action="clientTransferAction.do?method=confirmBranchTransfer">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <a
						href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
					<c:out value="${sessionScope.linkValues.customerOfficeName}" /></a>
					/ <c:if test="${!empty sessionScope.linkValues.customerParentName}">
						<a
							href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
						<c:out value="${sessionScope.linkValues.customerParentName}" /> </a> /  
		    </c:if> <a
						href="clientCreationAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
					<c:out value="${sessionScope.linkValues.customerName}" /> </a> </span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.oldClient.displayName}" /> -</span> <mifos:mifoslabel
								name="client.EditLink" bundle="ClientUIResources">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.BRANCHOFFICE}">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="client.MembershipLink" bundle="ClientUIResources"></mifos:mifoslabel>
							</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							<td width="94%"><span class="fontnormal"> <mifos:mifoslabel
								name="client.ChooseBranchInstructions1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.BRANCHOFFICE}">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="client.ChooseBranchInstructions2"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="client.ChooseBranchInstructions3"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.BRANCHOFFICE}">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="client.ChooseBranchInstructions4"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}">
							</mifos:mifoslabel> <mifos:mifoslabel
								name="client.ChooseBranchInstructions5"
								bundle="ClientUIResources"></mifos:mifoslabel> </span></td>
						</tr>

					</table>
					<%-- <table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="fontnormalbold"> <br>
                  <table width="95%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="61%"><span class="fontnormalbold">
                      	<span class="fontnormalbold">
                      	<mifos:mifoslabel name="label.branchoffices" bundle="GroupUIResources"></mifos:mifoslabel>
                      		
                      	</span></span> </td>
                      </tr>
                  </table>
                  <span class="fontnormalbold"> </span>
            	<c:forEach var="branch" items="${requestScope.branchList}">
	              <span class="fontnormal">
    	             	<c:out value="${branch.officeName}"/>
        	      </span>
                  <span class="fontnormalbold"> <br> </span>
                  <table width="90%" border="0" cellspacing="0" cellpadding="0">
                  	 <c:forEach var="branchOff" items="${branch.branchOffice}">
                        <tr class="fontnormal">
                          <td width="1%"><img src="images/bullet_circle.gif" width="9" height="11"></td>
                          <td width="99%">
                          <a href="clientTransferAction.do?method=confirmBranchTransfer&officeId=<c:out value="${branchOff.officeId}"/>&officeName=<c:out value="${branchOff.officeName}"/>">
                          		<c:out value="${branchOff.officeName}"/>
                          </a>
                          </td>
                        </tr>
                  	 </c:forEach>
                  </table>
                      <br>
             </c:forEach>
                  </td>
              </tr>
            </table> --%> <office:listOffices
						methodName="confirmBranchTransfer"
						actionName="clientTransferAction.do" onlyBranchOffices="yes" />
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button onclick="goToCancelPage();"
								property="cancelButton" styleClass="cancelbuttn"
								style="width:70px">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>

