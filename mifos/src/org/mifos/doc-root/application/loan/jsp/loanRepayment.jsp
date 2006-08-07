<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script>
			function fun_return(form)
					{
						form.action="loanAccountAction.do?method=get";
						form.submit();
					}
	</script>
		<html-el:form method="post" action="/loanAccountAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"> <customtags:headerLink/> </span>
					</td>
				</tr>
			</table>
			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading">
								<c:out value="${sessionScope.BusinessKey.loanOffering.prdOfferingName}"/>&nbsp;#
								<c:out value="${sessionScope.BusinessKey.globalAccountNum}"/>-
								</span>
								<mifos:mifoslabel name="loan.repayment_sched" bundle="loanUIResources" /> 
								<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.pereferedLocale)}" />

							</td>
						</tr>
						            	<tr>
					<logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><html-el:errors
							bundle="accountsUIResources" /></font></td>
						</logic:messagesPresent>
				</tr>
						
					</table>
					<c:if test="${sessionScope.BusinessKey.accountState.id != 6 and sessionScope.BusinessKey.accountState.id != 7 and sessionScope.BusinessKey.accountState.id !=8 and sessionScope.BusinessKey.accountState.id !=10}">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<p><br>
							</p>
							</td>
						</tr>
					</table>
		
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
								<span class="fontnormalbold">
									<mifos:mifoslabel name="loan.apply_trans" />
								</span>&nbsp;&nbsp;&nbsp;&nbsp;
								<c:if test="${(sessionScope.BusinessKey.accountState.id=='5' || sessionScope.BusinessKey.accountState.id=='9')}">
									<html-el:link href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountTypeId}
															&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test="${param.lastPaymentAction != '10'}">
									<c:choose>
										<c:when test="${sessionScope.BusinessKey.accountState.id=='5' || sessionScope.BusinessKey.accountState.id=='9'}">
											<html-el:link href="applyAdjustment.do?method=loadAdjustment&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}"> 
												<mifos:mifoslabel name="loan.apply_adjustment" />
											</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
									</c:choose>
								</c:if>
							
								<html-el:link href="AccountsApplyChargesAction.do?method=load&input=reviewTransactionPage&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.prdOfferingName}"> 
									<mifos:mifoslabel name="loan.apply_charges" />
								</html-el:link>
							</td>
						</tr>
					</table>
				</c:if>	
					

								<loanfn:getLoanRepaymentTable />
					
					<table width="100%" border="0" cellpadding="1" cellspacing="0">
					<tr valign="top">
                        <td colspan="4">&nbsp;</td>
                      </tr>
                    </table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="javascript:fun_return(this.form);"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="loan.returnToAccountDetails"
									bundle="loanUIResources" />
							</html-el:button></td>
						</tr>
					</table>

					<br>
					</td>
				</tr>
			</table>
						<mifos:SecurityParam property="Loan" />
		
		<html-el:hidden property="input" value="reviewTransactionPage"/>
		<html-el:hidden property="accountId" value="${sessionScope.BusinessKey.accountId}"/>
		<html-el:hidden property="accountStateId" value="${sessionScope.BusinessKey.accountState.id}"/>
		<html-el:hidden property="accountTypeId" value="${sessionScope.BusinessKey.accountType.accountTypeId}"/>
		<html-el:hidden property="recordOfficeId" value="${param.recordOfficeId}"/>
		<html-el:hidden property="recordLoanOfficerId" value="${param.recordLoanOfficerId}"/>
		<html-el:hidden property="globalAccountNum" value="${sessionScope.BusinessKey.globalAccountNum}"/>
		<html-el:hidden property="prdOfferingName" value="${sessionScope.BusinessKey.loanOffering.prdOfferingName}"/>
	</html-el:form>
	</tiles:put>
</tiles:insert>
