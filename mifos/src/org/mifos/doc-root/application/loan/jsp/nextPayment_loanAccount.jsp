<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script>
			function fun_return(form)
					{
						form.action="loanAction.do?method=get";
						form.submit();
					}
	</script>
		<html-el:form method="post" action="/loanAction.do">			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>				
					<td class="bluetablehead05">
					<span class="fontnormal8pt">
						<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
						<html-el:link action="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
							<c:out value="${param.accountName}" />
						</html-el:link></span>
					</td>
				</tr>
			</table>

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange">
							<span class="heading">
								<c:out value="${param.accountName}"/>&nbsp;#
								<c:out value="${param.globalAccountNum}"/> -
							</span>
							<mifos:mifoslabel name="loan.next_install_details" bundle="loanUIResources" /></td>
						</tr>						
					</table>
						<br>
					<table width="60%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
							<span class="fontnormalbold">
								<mifos:mifoslabel name="loan.apply_trans" bundle="loanUIResources" />
							</span>&nbsp;&nbsp;&nbsp;&nbsp; 
							<html-el:link href="#">
								<mifos:mifoslabel name="loan.apply_payment"	bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<html-el:link href="applyAdjustment.do?method=loadAdjustment&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.accountName}">
								<mifos:mifoslabel name="loan.apply_adjustment"	bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<html-el:link href="AccountsApplyChargesAction.do?method=load&input=reviewTransactionPage&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.accountName}">
								<mifos:mifoslabel name="loan.apply_charges" bundle="loanUIResources" />
							</html-el:link>
							</td>
						</tr>
					</table>
					<br>				
					
					<table width="60%" border="0" cellpadding="3" cellspacing="0">
						<tr class="drawtablerowboldnoline">
							<td width="60%">&nbsp;</td>
							<td width="27%" align="right">
							<mifos:mifoslabel name="loan.amount" bundle="loanUIResources" />
							</td>							
							<td width="13%" align="right">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow">
								<mifos:mifoslabel name="loan.principal"	bundle="loanUIResources" />
							</td>
							<td align="right" class="drawtablerow">
							<c:out value='${sessionScope.viewUpcomingInstallmentDetails.principal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewUpcomingInstallmentDetails.interest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees"	bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewUpcomingInstallmentDetails.fees}'/></td>
							<td align="right" class="drawtablerow">
							<html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=fees&installmentType=current'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewUpcomingInstallmentDetails.penalty}'/></td>
							<td align="right" class="drawtablerow">
							<html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=penalty&installmentType=current'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<em>
							<mifos:mifoslabel name="loan.subTotal" bundle="loanUIResources" />
							</em>
							</td>
							<td align="right" class="drawtablerow">
							<c:out value='${sessionScope.viewUpcomingInstallmentDetails.subTotal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>						
											
						<tr>
							<td class="drawtablerowbold">
							<mifos:mifoslabel name="loan.overdueamount" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow">
							&nbsp;
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>						
						
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.principal"	bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewOverDueInstallmentDetails.principal}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewOverDueInstallmentDetails.interest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.fees" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewOverDueInstallmentDetails.fees}'/></td>
							<td align="right" class="drawtablerow">
							<html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=fees&installmentType=overdue'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewOverDueInstallmentDetails.penalty}'/></td>
							<td align="right" class="drawtablerow"><html-el:link href='loanAction.do?method=waive&accountId=${requestScope.Context.businessResults["InstallmentDetails"].accountId}&installmentId=${requestScope.Context.businessResults["InstallmentDetails"].nextInstallmentId}&type=penalty&installmentType=overdue'>
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link></td>
						</tr>
						
						<tr>
							<td class="drawtablerow">
							<em>
							<mifos:mifoslabel name="loan.subTotal" bundle="loanUIResources" />
							</em>
							</td>
							<td align="right" class="drawtablerow">
							<c:out value='${sessionScope.viewOverDueInstallmentDetails.subTotal}'/>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>	
						
						<tr>
							<td class="drawtablerowbold">
							<mifos:mifoslabel name="loan.totalDueOn" bundle="loanUIResources" />
								<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.pereferedLocale)}" />
								</td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:out value='${sessionScope.totalAmountOverDue}'/>
							</td>
							
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<html-el:button property="returnToAccountDetailsbutton"	onclick="javascript:fun_return(this.form);"	styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="loan.returnToAccountDetails" bundle="loanUIResources" />
							</html-el:button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
