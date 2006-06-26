<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form method="post" action="/loanAction.do">
			<td height="400" align="left" valign="top" bgcolor="#FFFFFF"
				class="paddingleftmain">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="clientsaccounts.htm">
						<mifos:mifoslabel name="loan.branch-01" bundle="loanUIResources" />
					</html-el:link> /&nbsp;<html-el:link href="CenterDetails.htm">
						<mifos:mifoslabel name="loan.kanakpura_center"
							bundle="loanUIResources" />
					</html-el:link> / <html-el:link href="GroupDetails.htm">
						<mifos:mifoslabel name="loan.hope&effort_group"
							bundle="loanUIResources" />
					</html-el:link> / <html-el:link href="loan_account_detail.htm">
			    <mifos:mifoslabel name="loan.edu_loan"  /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />

					</html-el:link> / </span></td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading">Educational
							Loan # 12334 - </span><mifos:mifoslabel
								name="loan.next_install_details" bundle="loanUIResources" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="loan.next_inst_due" bundle="loanUIResources" />
							12/12/2005<br>
							<br>
							</td>
						</tr>
					</table>
					<table width="60%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"><mifos:mifoslabel name="loan.apply_trans"
								bundle="loanUIResources" /></span>&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyPayment_loanAccount.htm">
								<mifos:mifoslabel name="loan.apply_payment"
									bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="#">
								<mifos:mifoslabel name="loan.apply_adjustment"
									bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="applyCharges_loanAccount.htm">
								<mifos:mifoslabel name="loan.apply_charges"
									bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
					</table>
					<br>
					<table width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="12%" class="drawtablehd"><mifos:mifoslabel
								name="loan.next_install_details" bundle="loanUIResources" /></td>
						</tr>
					</table>
					<table width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr class="drawtablerowboldnoline">
							<td width="60%">&nbsp;</td>
							<td width="27%" align="right"><mifos:mifoslabel
								name="loan.amount(USD)" bundle="loanUIResources" /></td>
							<td width="13%" align="right">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.principal"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentPrincipal}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentInterest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentFees}'/></td>
							<td align="right" class="drawtablerow"><html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=fees&installmentType=current'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentPenalty}'/></td>
							<td align="right" class="drawtablerow"><html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=penalty&installmentType=current'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerowbold"><mifos:mifoslabel
								name="loan.total_due_install" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].totalNextInstallment}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
					<table width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="12%" class="drawtablehdorange"><mifos:mifoslabel
								name="loan.past_due_details" bundle="loanUIResources" /></td>
						</tr>
					</table>
					<table width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr class="drawtablerowboldnoline">
							<td width="60%">&nbsp;</td>
							<td width="27%" align="right"><mifos:mifoslabel
								name="loan.amount(USD)" bundle="loanUIResources" /></td>
							<td width="13%" align="right">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.principal"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].pastDuePrincipal}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].pastDueInterest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].pastDueFees}'/></td>
							<td align="right" class="drawtablerow"><html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=fees&installmentType=overdue'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty"
								bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].pastDuePenalty}'/></td>
							<td align="right" class="drawtablerow"><html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=penalty&installmentType=overdue'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerowbold"><mifos:mifoslabel
								name="loan.past_due" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${requestScope.Context.businessResults["InstallmentDetails"].totalPastDue}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><html-el:button property="returnToAccountDetailsbutton"
								onclick="location.href='loan_account_detail.htm';"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="button.returnToAccountDetails"
									bundle="loanUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
