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
    	<a id="header.link.yoursettings" href="yourSettings.do?method=get&randomNUm=1709635777919258383">Your settings</a>
     	&nbsp;|&nbsp;<a id="header.link.logout" href="javascript:fnLogout()">Logout</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   </td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a id="header.link.home" href="custSearchAction.do?method=getHomePage">Home</a></td>
          <td class="tablightorange"><a id="header.link.clientsAndAccounts" href="custSearchAction.do?method=loadMainSearch">Clients & Accounts </a></td>
          <td class="tablightorange"><a id="header.link.reports" href="reportsAction.do?method=load">Reports</a></td>
          <td class="taborange"><a id="header.link.admin" href="AdminAction.do?method=load&randomNUm=1709635777919258383" class="tabfontwhite">Admin</a></td>
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
		<td align="left" valign="top" bgcolor="#FFFFFF"
			class="paddingleftmain" height="500" >
		<span id="page.id" title="Create Fee" /> <script
			src="pages/application/fees/js/Fees.js"></script> 
			
		<form name="feeactionform" action="createFee.ftl?execution=${flowExecutionKey}" method="POST">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
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
											<td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
											<td class="timelineboldorange">Fee information</td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight">Review & submit</td>
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
									<td class="headingorange"><span class="heading"> Define a new fee - </span> Enter fee information</td>
								</tr>
								<tr>
									<td class="fontnormal">
										Complete the fields below. Then click Preview. Click Cancel to return to Admin without submitting information. 
										<br>
										<span class="mandatorytext"><font color="#FF0000">*</font></span>Fields marked with an asterisk are required.
									</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<font class="fontnormalRedBold"></font>
								<tr>
									<td colspan="2" class="fontnormalbold">Fee details <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="27%" align="right"><span class="mandatorytext"><font color="#FF0000">*</font></span>Fee Name:</td>
									<td width="73%" valign="top">
										<script src="pages/framework/js/func.js"></script>
										<script src="pages/framework/js/func_en_GB.js"></script>
										
										[@spring.formInput "feeDefintion.feeName", 
											'onkeypress="return FnCheckNumCharsOnPress(event,this);"
											 onblur="return FnCheckNumChars(event,this);return FnEscape(event,this)"'/]
										[@spring.showErrors "<br>","fontnormalRedBold" /] 
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span class="mandatorytext"><font color="#FF0000">*</font></span>Fee Applies To:</td>
									<td valign="top">
										[@spring.formSingleSelect "feeDefintion.categoryType", FeeParameters.categories, 'onchange="onPageLoad();"' /]
										[@spring.showErrors "<br>","fontnormalRedBold" /] 
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top">Default fees:</td>
									<td valign="top">
									   [@mifos.formCheckboxB "feeDefintion.customerDefaultFee"/]
									   [@spring.showErrors "<br>","fontnormalRedBold" /]
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="top"><span class="mandatorytext"><font color="#FF0000">*</font></span>Frequency:</td>
									<td valign="top">
									   [@spring.formRadioButtons "feeDefintion.feeFrequencyType", FeeParameters.frequencies, "<br/>", 'onclick="onPageLoad();"' /]
									   [@spring.showErrors "<br>","fontnormalRedBold" /]  	
									</td>
								</tr>
								
								<tr class="fontnormal">
								  <td align="right" valign="top">&nbsp;</td>
								  <td valign="top">
									<div id="timeofchargeDiv">Select time of charge for one time fees: <br>
									    <div id="loanTimeOfChargeDiv">
									        [@spring.formSingleSelect "feeDefintion.loanCharge", FeeParameters.timesOfCharging,"" /]
									        [@spring.showErrors "<br>","fontnormalRedBold" /]
									    </div>
									    <div id="customerTimeOfChargeDiv">
									       [@spring.formSingleSelect "feeDefintion.customerCharge", FeeParameters.timesOfChargingCustomers,"" /]
									       [@spring.showErrors "<br>","fontnormalRedBold" /] 
									    </div>
									    </div>
									    <div id="scheduleDIV">
									      <table width="90%" border="0" cellpadding="3" cellspacing="0">
										    <tr class="fontnormal">
											  <td align="left" valign="top"
												style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
												[@spring.formRadioButtons "feeDefintion.feeRecurrenceType", FeeParameters.recurrenceTypes, "&nbsp;&nbsp;&nbsp;&nbsp;", 'onclick="onPageLoad();"' /]
												[@spring.showErrors "<br>","fontnormalRedBold" /]
											  </td>
										    </tr>
										    <tr class="fontnormal">
											  <td width="59%" align="left" valign="top" style="border: 1px solid #CECECE;">
											    <div id="weekDIV" style="height: 40px; width: 380px; display: block;">
											  	  If weeks, specify the following: 
											  	  <table border="0" cellspacing="0" cellpadding="2">
												      <tr class="fontnormal">
													    <td colspan="4">Recur every  
													      <script src="pages/framework/js/func.js"></script>
													      <script src="pages/framework/js/func_en_GB.js"></script>
													      
													      [@spring.formInput "feeDefintion.weekRecurAfter", 
														    'maxlength="3" size="3" value="" 
														     onkeypress="return FnCheckNumberOnPress(event);" 
														     onblur="FnCheckNumber(event,"","",this);"'/] 
														  week(s)
														  [@spring.showErrors "<br>","fontnormalRedBold" /]
													    </td>
													  </tr>
											       </table>
											    </div>
											    <div id="monthDIV" style="height: 40px; width: 380px;">
												   If months, specify the following: <br>
												   <table border="0" cellspacing="0" cellpadding="2">
													  <tr class="fontnormal">
														<td>Recur every 
													  		<script src="pages/framework/js/func.js"></script>
													  		<script src="pages/framework/js/func_en_GB.js"></script>
													   		[@spring.formInput "feeDefintion.monthRecurAfter", 
														      'maxlength="3" size="3" value="" 
														       onkeypress="return FnCheckNumberOnPress(event);" 
														       onblur="FnCheckNumber(event,"","",this);"'/] 
														     month(s)
														[@spring.showErrors "<br>","fontnormalRedBold" /]
														</td>
													  </tr>
												   </table>
											    </div>
											  </td>
										    </tr>
									       </table>
									   </div>
									</td>
								</tr>
								<tr class="fontnormal">
									<td valign="top" colspan="4" class="fontnormalbold">Fee Calculation <br/><br/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span class="mandatorytext"><font color="#FF0000">*</font></span>Amount:</td>
									<td align="left">
									<table>
										<tr>
											<td>
											   [@spring.formInput "feeDefintion.amount"/]
											   [@spring.showErrors "<br>","fontnormalRedBold" /]
											</td>
											[#if FeeParameters.multiCurrencyEnabled]
												<td class="fontnormal">
													<span class="mandatorytext"><font color="#FF0000">*</font></span>[@spring.message "fees.currency" /]
												</td>
												<td>
													<div id="currencyDiv">
														[@spring.formSingleSelect "feeDefintion.currencyId", FeeParameters.currencies,"" /]
														[@spring.showErrors "<br>","fontnormalRedBold" /]
													</div>
												</td>
											[/#if]
										</tr>
									</table>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right" valign="middle" style="padding-top: 25px;">
									     <div id="rateDivHeading">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr class="fontnormal">
													<td colspan="3"><span class="mandatorytext"><font color="#FF0000">*</font></span>Calculate Fee As:</td>
												</tr>
											</table>
										  </div>
									</td>
									<td valign="top">
										<div id="rateDiv">
											<table width="90%" border="0" cellspacing="0" cellpadding="0">
												<tr class="fontnormal">
													<td colspan="3">OR</td>
												</tr>
												<tr class="fontnormal">
													<td colspan="3"><img src="pages/framework/images/trans.gif" width="5" height="1"></td>
												</tr>
												<tr class="fontnormal">
													<td width="16%">
													   [@spring.formInput "feeDefintion.rate"/] % of 
													   [@spring.showErrors "<br>","fontnormalRedBold" /]
													   [@spring.formSingleSelect "feeDefintion.feeFormula", FeeParameters.formulas,"" /]
													   [@spring.showErrors "<br>","fontnormalRedBold" /]
													</td>
											        <td width="17%">&nbsp;</td>
												</tr>
											</table>
										</div>
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right">&nbsp;</td>
									<td valign="top">&nbsp;</td>
								</tr>
								<tr class="fontnormal">
									<td colspan="2" class="fontnormalbold">Accounting <br><br> </td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><span class="mandatorytext"><font color="#FF0000">*</font></span>GL Code:</td>
									<td valign="top">
										[@spring.formSingleSelect "feeDefintion.glCode", FeeParameters.glCodes,'style="width:136px;"' /] 
									</td>
								</tr>
							</table>
							<input type="hidden" name="loanCategoryId" value="5"> <script>onPageLoad();</script>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">
										<input type="submit" name="_eventId_preview" value="preview" class="buttn"> &nbsp; 
										<input type="submit" name="_eventId_cancel" value="cancel" class="cancelbuttn">
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
		
		
<script type="text/javascript" language="JavaScript"> 
  <!--
  var focusControl = document.forms["feeactionform"].elements["feeName"];
 
  if (focusControl.type != "hidden" && !focusControl.disabled) {
     focusControl.focus();
  }
  // -->
</script>
 
	 <input type="hidden" name="h_user_locale" value="en_GB"></td>
	</tr>
</table>
 
</body>
</html>