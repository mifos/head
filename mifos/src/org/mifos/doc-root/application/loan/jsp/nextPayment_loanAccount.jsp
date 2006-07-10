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
							<c:out value="${param.prdOfferingName}" />
						</html-el:link></span>
						<html-el:errors bundle="loanUIResources" />
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
								<c:out value="${param.prdOfferingName}"/>&nbsp;#
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
							<c:if test="${param.accountStateId==5 || param.accountStateId==9}">
							<html-el:link href="applyPaymentAction.do?method=load&input=loan&
							prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&
							accountId=${param.accountId}&accountType=${param.accountType}
							&recordOfficeId=${param.recordOfficeId}&
							recordLoanOfficerId=${param.recordLoanOfficerId}">
							<mifos:mifoslabel name="loan.apply_payment" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</c:if>
							<c:if test="${param.lastPaymentAction != 10 && (param.accountStateId==5 || param.accountStateId==9)}">							
							<html-el:link href="applyAdjustment.do?method=loadAdjustment&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}">
								<mifos:mifoslabel name="loan.apply_adjustment"	bundle="loanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							</c:if>
							<html-el:link href="AccountsApplyChargesAction.do?method=load&input=installmentDetailsPage&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}">
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
			                <td class="drawtablerowbold">
			                <mifos:mifoslabel name="loan.original_installment" />
			                </td>
			                <td align="right" class="drawtablerow">&nbsp;</td>
			                <td align="right" class="drawtablerow">&nbsp;</td>
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
							<td class="drawtablerow">
							<mifos:mifoslabel name="${ConfigurationConstants.INTEREST}" />
							</td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewUpcomingInstallmentDetails.interest}'/></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.fees"	bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow">
							<c:out value='${sessionScope.viewUpcomingInstallmentDetails.fees.amount}'/></td>
							<td align="right" class="drawtablerow">	&nbsp;
							<c:if test='${sessionScope.viewUpcomingInstallmentDetails.fees.amountDoubleValue != 0.0 }'>						 
							<html-el:link href="loanAccountAction.do?method=waiveChargeDue&prdOfferingName=${param.prdOfferingName}&accountId=${param.accountId}&WaiveType=fees&type=LoanAccount&globalAccountNum=${param.globalAccountNum}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						<tr>
							<td class="drawtablerow"><mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewUpcomingInstallmentDetails.penalty}'/></td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${sessionScope.viewUpcomingInstallmentDetails.penalty.amountDoubleValue != 0.0 }'>						 
							<html-el:link href="loanAccountAction.do?method=waiveChargeDue&accountId=${param.accountId}&WaiveType=penalty&type=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
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
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${sessionScope.viewOverDueInstallmentDetails.fees.amountDoubleValue != 0.0 }'>	
							<html-el:link href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${param.accountId}&WaiveType=fees&type=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
						</tr>
						<tr>
							<td class="drawtablerow">
							<mifos:mifoslabel name="loan.penalty" bundle="loanUIResources" /></td>
							<td align="right" class="drawtablerow"><c:out value='${sessionScope.viewOverDueInstallmentDetails.penalty}'/></td>
							<td align="right" class="drawtablerow">&nbsp;
							<c:if test='${sessionScope.viewOverDueInstallmentDetails.penalty.amountDoubleValue != 0.0 }'>
							<html-el:link href="loanAccountAction.do?method=waiveChargeOverDue&accountId=${param.accountId}&WaiveType=penalty&type=LoanAccount&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}">
								<mifos:mifoslabel name="loan.waive" bundle="loanUIResources" />
							</html-el:link>
							</c:if>
							</td>
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
			<mifos:SecurityParam property="Loan" />
			<html-el:hidden property="accountId" value="${param.accountId}"/>
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>			
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}"/>
			<html-el:hidden property="accountType" value="${param.accountType}" />													
			<html-el:hidden property="currentStatusId" value="${param.accountStateId}" />							
			
		</html-el:form>
	</tiles:put>
</tiles:insert>
