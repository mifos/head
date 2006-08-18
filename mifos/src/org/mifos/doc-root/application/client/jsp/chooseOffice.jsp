<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/officetags" prefix="office"%>

<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">


		<script language="javascript">

  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }
 
</script>
		<html-el:form action="clientCustAction.do?method=load">
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="isClientUnderGrp" value="0" />

			<table width="90%" border="0" align="center" cellpadding="0"
				cellspacing="0">
				<tr>
					<td align="center" class="heading">&nbsp;</td>
				</tr>
			</table>
			<table width="90%" border="0" align="center" cellpadding="0"
				cellspacing="0">
				<tr>
					<td class="bluetablehead">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="33%">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/bigarrow.gif"
										width="17" height="17"></td>
									<td class="timelineboldgray">
									<mifos:mifoslabel	name="client.select" bundle="ClientUIResources"></mifos:mifoslabel>
										<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel
											name="${ConfigurationConstants.BRANCHOFFICE}" />
									</td>
								</tr>
							</table>
							</td>
							<td width="34%" align="center">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/orangearrow.gif"
										width="17" height="17"></td>
									<td class="timelineboldorangelight"><mifos:mifoslabel
										name="client.UserInformation" bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							</td>
							<td width="33%" align="right">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td><img src="pages/framework/images/timeline/orangearrow.gif"
										width="17" height="17"></td>
									<td class="timelineboldorangelight"><mifos:mifoslabel
										name="client.ReviewSubmitHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<table width="90%" border="0" align="center" cellpadding="0"
				cellspacing="0" class="bluetableborder">
				<tr>
					<td align="left" valign="top" class="paddingleftCreates">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <mifos:mifoslabel
								name="client.CreateTitle" bundle="ClientUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>

							- </span> <mifos:mifoslabel name="client.SelectBranchHeading"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.BRANCHOFFICE}"></mifos:mifoslabel>
							</td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="client.SelectBranchInstructions1"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="${ConfigurationConstants.BRANCHOFFICE}"></mifos:mifoslabel>
							<mifos:mifoslabel name="client.SelectBranchInstructions2"
								bundle="ClientUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="client.CreatePageCancelInstruction1"
								bundle="ClientUIResources"></mifos:mifoslabel> </td>

						</tr>
					</table>
					<office:listOffices methodName="load"
						actionName="clientCustAction.do" onlyBranchOffices="yes" />
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="cancelBtn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="button.cancel"
									bundle="ClientUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
