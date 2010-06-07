[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

<html lang="EN">
<head>
<title>Mifos</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href='pages/framework/css/cssstyle.css' rel="stylesheet" type="text/css">
</head>

<body>
	<script language="javascript">
		function fnLogout() {
			location.href="loginAction.do?method=logout";
		}
	</script>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	  <tr>
	    <td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188" height="74"></td>
	    <td align="right" bgcolor="#FFFFFF" class="fontnormal">
	    	<a id="header.link.yoursettings" href="yourSettings.do?method=get">Your settings</a>
		     &nbsp;|&nbsp; <a id="header.link.logout" href="javascript:fnLogout()">Logout</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
	  </tr>
	  <tr>
	    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
	        <tr>
	          <td class="tablightorange"><a id="header.link.home" href="custSearchAction.do?method=getHomePage">Home</a></td>
	          <td class="tablightorange"><a id="header.link.clientsAndAccounts" href="custSearchAction.do?method=loadMainSearch">Clients & Accounts </a></td>
	
	          <td class="tablightorange"><a id="header.link.reports" href="reportsAction.do?method=load">Reports</a></td>
	          <td class="taborange"><a id="header.link.admin" href="AdminAction.do?method=load" class="tabfontwhite">Admin</a></td>
	        </tr>
	      </table>
	        
	    </td>
	  </tr>
	  <tr>
	    <td colspan="2" class="bgorange"><img src="pages/framework/images/trans.gif" width="6" height="6"></td>
	  </tr>
	  <tr>
	    <td colspan="2" class="bgwhite"><img src="pages/framework/images/trans.gif" width="100" height="2"></td>
	  </tr>
	</table>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain" height="500" > 
				<span id="page.id" title="previewfeescreate" />
		
			<script src="pages/framework/js/CommonUtilities.js"></script>
			<script src="pages/application/fees/js/Fees.js"></script>
			<form name="feeactionform" action="createFee.ftl?execution=${flowExecutionKey}" method="POST" onsubmit="return func_disableSubmitBtn('submitBtn');">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
	
								<tr>
									<td align="center" class="heading">
										&nbsp;
									</td>
								</tr>
							</table>
							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
								<tr>
									<td class="bluetablehead">
	
										<table width="100%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td width="27%">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<img src="pages/framework/images/timeline/tick.gif" width="17" height="17">
															</td>
															<td class="timelineboldgray">
																Fee information
															</td>
														</tr>
													</table>
												</td>
												<td width="73%" align="right">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<img src="pages/framework//images/timeline/bigarrow.gif" width="17" height="17">
															</td>
															<td class="timelineboldorange">
																Review & submit
															</td>
														</tr>
													</table>
												</td>
											</tr>
	
										</table>
									</td>
								</tr>
							</table>
							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
								<tr>
									<td align="left" valign="top" class="paddingleftCreates">
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<tr>
												<td class="headingorange">
													<span class="heading"> Define a new fee - </span>
													Preview fee information
												</td>
											</tr>
											<tr>
												<td class="fontnormal">
													Review the information below. Click Submit if you are satisfied or click Edit to make changes.
													Click Cancel to return to Admin without submitting information.
												</td>
	
											</tr>
										</table>
										<br>
										<table width="93%" border="0" cellpadding="3" cellspacing="0">
											<tr>
												<td class="fontnormal">
													<font class="fontnormalRedBold"> </font>
												</td>
											</tr>
											<tr>
												<td width="100%" height="23" class="fontnormal">
													<span class="fontnormalbold"> Fee Details </span><br>
													<span class="fontnormalbold"> Fee Name: </span>${feeDefintion.feeName}<br>
													<span class="fontnormalbold"> Fee Applies To: </span>
													[@spring.message "app.fees.categoryType.${feeDefintion.categoryType}"/]<br>
													All Customers<br>
													
													[#if !feeDefintion.categoryLoan]
														<span class="fontnormalbold"> Default fees: </span>
														[#if feeDefintion.customerDefaultFee]
															Yes
														[#else]
															No	
														[/#if]
													[/#if]
													<br>
													<span class="fontnormalbold"> Frequency: </span> [@spring.message "app.fees.frequencyType.${feeDefintion.feeFrequencyType}"/]<br>
													<span class="fontnormalbold"> Time of charge: </span>
													[#if feeDefintion.periodic]
														[@spring.message "fees.labelRecurEvery"/]
														[#if feeDefintion.weeklyRecurrence]
															${feeDefintion.weekRecurAfter} [@spring.message "fees.labelWeeks"/]
														[#elseif feeDefintion.monthlyRecurrence]
															${feeDefintion.monthRecurAfter} [@spring.message "fees.labelMonths"/]
														[/#if]
													[#else]
														[@spring.formSingleSelect "feeDefintion.customerCharge", FeeParameters.timesOfChargingCustomers,"" /]
													[/#if]
													
													[#if FeeParameters.multiCurrencyEnabled && feeDefintion.categoryLoan]
													   <span class="fontnormalbold">[@spring.message "fees.currency" /]</span>
													   [@spring.formSingleSelect "feeDefintion.currencyId", FeeParameters.currencies,"" /]
													[/#if]
													
													<span class="fontnormalbold"> Fee Calculation </span>
													<br>
													
													[#if feeDefintion.categoryLoan && feeDefintion.rateFee]
														<span class="fontnormalbold"> [@spring.message "fees.amountCalculatedAs" /] </span>
														${feeDefintion.rate}
													[#else]
													    <span class="fontnormalbold"> [@spring.message "fees.amount" /] </span>
														${feeDefintion.amount}	
													[/#if]
													
													[#if feeDefintion.rateFee]
														[@spring.message "fees.ofa" /]
														[@spring.formSingleSelect "feeDefintion.feeFormula", FeeParameters.formulas,"" /]
													[/#if]
													<br>
												    <br>
													
													<span class="fontnormalbold"> 
													  [@spring.message "fees.accounting" /] <br> 
													  [@spring.message "fees.glCode" /] 
													</span>
													[@spring.formSingleSelect "feeDefintion.glCode", FeeParameters.glCodes,'style="width:136px;"' /] 
													<br>
													<br>
													<br>
													<span class="fontnormal"></span>
													   <input type="submit" name="_eventId_edit" value="Edit fee information" onclick="javascript:fnEdit(this.form)"  class="insidebuttn"> 
												</td>
											</tr>
										</table>
	
										<table width="93%" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="center" class="blueline">&nbsp;</td>
											</tr>
										</table>
										<br>
										<table width="93%" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="center">
													<input type="submit" name="_eventId_proceed" value="Submit" class="buttn">&nbsp;
													<input type="button" name="_eventId_cancel" value="Cancel"  
													  onclick="javascript:fnCancel(this.form)" class="cancelbuttn">
												</td>
											</tr>
										</table>
										<br>
									</td>
								</tr>
							</table>
							<br>
						</td>
					</tr>
				</table>
				<br>
			</form>
	
		 <input type="hidden" name="h_user_locale" value="en_GB"></td>
		</tr>
	</table>

</body>
</html>

