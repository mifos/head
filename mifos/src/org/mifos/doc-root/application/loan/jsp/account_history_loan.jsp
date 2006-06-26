<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/c" prefix="c"%>
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
						<mifos:mifoslabel name="loan.branch-01" bundle="LoanUIResources" />
					</html-el:link> /&nbsp;<html-el:link href="CenterDetails.htm">
						<mifos:mifoslabel name="loan.kanakpura_center"
							bundle="LoanUIResources" />
					</html-el:link> / <html-el:link href="GroupDetails.htm">
						<mifos:mifoslabel name="loan.hope&effort_group"
							bundle="LoanUIResources" />
					</html-el:link> / <html-el:link href="loan_account_detail.htm">
						<mifos:mifoslabel name="loan.edu_loan" bundle="LoanUIResources" />
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
							Loan #1234 - </span><mifos:mifoslabel name="loan.acc_statement"
								bundle="LoanUIResources" /> 05/01/2005<br>
							<br>

							</td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"><mifos:mifoslabel name="loan.apply_trans"
								bundle="LoanUIResources" /></span>&nbsp;&nbsp;&nbsp;&nbsp; <html-el:link
								href="applyPayment_loanAccount.htm">
								<mifos:mifoslabel name="loan.apply_payment"
									bundle="LoanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="#">
								<mifos:mifoslabel name="loan.apply_adjustment"
									bundle="LoanUIResources" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="applyCharges_loanAccount.htm">
								<mifos:mifoslabel name="loan.apply_charges"
									bundle="LoanUIResources" />
							</html-el:link></td>
						</tr>
					</table>
					<br>
					<table width="100%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="8">&nbsp;</td>
							<td colspan="5" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.running_bal" bundle="LoanUIResources" /></td>
						</tr>
						<tr class="drawtablerowbold">

							<td width="66" class="drawtablerowbold"><mifos:mifoslabel
								name="loan.date" bundle="LoanUIResources" /></td>
							<td width="162" class="drawtablerowbold"><mifos:mifoslabel
								name="loan.activity" bundle="LoanUIResources" /></td>
							<td width="56" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.principal" bundle="LoanUIResources" /></td>
							<td width="49" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.interest" bundle="LoanUIResources" /></td>
							<td width="31" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.fees" bundle="LoanUIResources" /></td>
							<td width="46" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.penalty" bundle="LoanUIResources" /></td>

							<td width="44" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.total" bundle="LoanUIResources" /></td>
							<td width="44" align="right" class="fontnormalbold">&nbsp;</td>
							<td width="52" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.principal" bundle="LoanUIResources" /></td>
							<td width="49" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.interest" bundle="LoanUIResources" /></td>
							<td width="30" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.fees" bundle="LoanUIResources" /></td>
							<td width="47" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.penalty" bundle="LoanUIResources" /></td>

							<td width="44" align="right" class="drawtablerowbold"><mifos:mifoslabel
										name="loan.total" bundle="LoanUIResources" /></td>
						</tr>
						<tr valign="top">
							<td class="drawtablerow">12/1/2000</td>
							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.balance_at_disburse" bundle="LoanUIResources" /></td>
							<td align="right" class="drawtablerow">6000<br>

							</td>
							<td align="right" class="drawtablerow">600<br>
							</td>
							<td align="right" class="drawtablerow">60<br>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">6660</td>

							<td align="right" class="fontnormalbold"><br>
							</td>
							<td align="right" class="drawtablerow">6000</td>
							<td align="right" class="drawtablerow">600</td>
							<td align="right" class="drawtablerow">60</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">6660</td>

						</tr>
						<tr valign="top">
							<td class="drawtablerow">12/1/2000</td>
							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.app_fee" bundle="LoanUIResources" /><br>
							</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">50</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">50</td>
							<td align="right" class="fontnormalbold"><br>
							</td>
							<td align="right" class="drawtablerow">6000</td>
							<td align="right" class="drawtablerow">600</td>
							<td align="right" class="drawtablerow">110</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">6710</td>
						</tr>
						<tr valign="top">
							<td class="drawtablerow">12/1/2000</td>
							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.payment" bundle="LoanUIResources" /><br>
							</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">-50</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">-50</td>
							<td align="right" class="fontnormalbold"><br>
							</td>
							<td align="right" class="drawtablerow">6000</td>

							<td align="right" class="drawtablerow">600</td>
							<td align="right" class="drawtablerow">60</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">6660</td>
						</tr>
						<tr valign="top">
							<td class="drawtablerow">1/1/2000<br>

							</td>
							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.payment" bundle="LoanUIResources" /></td>
							<td align="right" class="drawtablerow">-1000<br>
							</td>
							<td align="right" class="drawtablerow">-100<br>
							</td>
							<td align="right" class="drawtablerow">-10</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">-1110</td>
							<td align="right" class="fontnormalbold">&nbsp;</td>
							<td align="right" class="drawtablerow">5000</td>
							<td align="right" class="drawtablerow">500</td>
							<td align="right" class="drawtablerow">50</td>
							<td align="right" class="drawtablerow">&nbsp;</td>

							<td align="right" class="drawtablerow">5550</td>
						</tr>
						<tr valign="top">
							<td class="drawtablerow">3/1/2000<br>
							</td>
							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.penalty_02" bundle="LoanUIResources" /><br>
							</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">30</td>
							<td align="right" class="drawtablerow">30</td>
							<td align="right" class="fontnormalbold"><br>
							</td>
							<td align="right" class="drawtablerow">500</td>

							<td align="right" class="drawtablerow">500</td>
							<td align="right" class="drawtablerow">50</td>
							<td align="right" class="drawtablerow">-30</td>
							<td align="right" class="drawtablerow">5580</td>
						</tr>
						<tr valign="top">
							<td class="drawtablerow">3/1/2000</td>

							<td class="drawtablerow"><mifos:mifoslabel
										name="loan.penalty_01" bundle="LoanUIResources" /></td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">30</td>
							<td align="right" class="drawtablerow">30</td>
							<td align="right" class="fontnormalbold">&nbsp;</td>

							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
							<td align="right" class="drawtablerow">&nbsp;</td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="location.href='loan_account_detail.htm';"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="button.returnToAccountDetails"
									bundle="LoanUIResources" />
							</html-el:button></td>
						</tr>
					</table>

					<br>
					</td>
				</tr>
			</table>
			</td>
		</html-el:form>
	</tiles:put>
</tiles:insert>
