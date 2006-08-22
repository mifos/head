<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/officetags" prefix="office"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".withmenu">
	<tiles:put name="body" type="string">
		<script language="javascript">
  
   function goToCancelPage(){
	clientTransferActionForm.action="clientTransferAction.do?method=cancel";
	clientTransferActionForm.submit();
  }
</script>
		<html-el:form
			action="clientTransferAction.do?method=previewBranchTransfer">
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
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.BusinessKey.displayName}" /> -</span> <mifos:mifoslabel
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
					<office:listOffices
						methodName="previewBranchTransfer"
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

