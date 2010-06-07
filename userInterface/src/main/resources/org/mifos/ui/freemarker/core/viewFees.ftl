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
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a id="header.link.yoursettings" href="yourSettings.do?method=get">Your settings</a>

     &nbsp;|&nbsp; <a id="header.link.logout" href="javascript:fnLogout()">Logout</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
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
	<td width="174" height="500" align="left" valign="top" class="bgorangeleft">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
          <td class="leftpanehead" colspan="2">Administrative tasks</td>
        </tr>
        <tr>
          <td class="leftpanelinks">
          		<form name="custSearchActionForm" method="post" action="/mifos/custSearchAction.do?method=loadAllBranches">
				<table width="90%" border="0" cellspacing="0" cellpadding="0">
            	<tr>
              		<td class="paddingbottom03">
              			<span class="fontnormal8ptbold">Search by client name, system ID or account number</span> 
              		</td>
            	</tr>
        		</table>
            	<table width="90%" border="0" cellpadding="0" cellspacing="0">
              	<tr>
                	<td width="100%" colspan="2">
                		<input type="text" name="searchString" maxlength="200" size="20" value="">
						<input type="hidden" name="searchNode(search_officeId)" value="0"> 
						<input type="hidden" name="officeName" value="">													
					</td>
              	</tr>
            	</table>
            	<table width="143" border="0" cellspacing="0" cellpadding="10">
              	<tr>
                	<td align="right">                
                		<input type="submit" name="searchButton" value="Search" class="buttn">
                	</td>
              	</tr>
            	</table>
            	</form>            
            </td>
        </tr>
      	</table>
    </td>      
	<td align="left" valign="top" bgcolor="#FFFFFF" class="paddingleftmain" height="500">
		<span id="page.id" title="viewFees" />
		<script>		
		</script>
		<script src="pages/application/fees/js/Fees.js"></script>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> <a href="AdminAction.do?method=load">Admin</a> / </span> 
					<span class="fontnormal8ptbold"> View Fees </span>
				</td>
			</tr>
		</table>
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"> <span class="headingorange"> View Fees </span> </td>
						</tr>
						<tr>
							<td class="fontnormalbold">
								<span class="fontnormal"> Click on a fee  below to view details and make changes or 
								  <a href="createFee.ftl">define a new fee</a> <br> 
								</span> 
								<span class="fontnormalbold"> 
									<span class="fontnormalbold"> <br> </span> 
								</span> 
								<span class="fontnormalbold"> </span> 
								<span class="fontnormal"> </span> 
								<span class="fontnormalbold"> 
									<span class="fontnormalbold"> 
										<font class="fontnormalRedBold">  </font> 
									</span> 
								</span> 
								<span class="fontnormalbold"> 
									<span class="fontnormalbold"> Product Fees<br> </span> 
								</span> 
								<span class="fontnormalbold"> </span>
									<table id="productFeeTable" width="90%" border="0" cellspacing="0" cellpadding="0">
										[#list productFees as prodFee]
											<tr class="fontnormal">
												<td width="1%">
													<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
												</td>
												<td width="99%">
													<a href="feeaction.do?method=get&feeId=${prodFee.id}">${prodFee.name}</a>
													( ${prodFee.categoryType} )
													[#if prodFee.active]
													  <img src="pages/framework/images/status_closedblack.gif" width="8" height="9"/>
													     &nbsp;${prodFee.feeStatus.name}
													[/#if]
												</td>
											</tr>
										[/#list]
									</table>
								<br>
								Client Fees <br>
								<table id="clientFeeTable" width="90%" border="0" cellspacing="0" cellpadding="0">
									[#list customerFees as custFee]
										<tr class="fontnormal">
											<td width="1%">
												<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
											</td>
											<td width="99%">
												<a href="feeaction.do?method=get&feeId=${custFee.id}">${custFee.name}</a>
												( ${custFee.categoryType} )
												[#if custFee.active]
													<img src="pages/framework/images/status_closedblack.gif" width="8" height="9"/>
													     &nbsp;${custFee.feeStatus.name}
												[/#if]
											</td>
										</tr>
									[/#list]
								</table>
							</td>
						</tr>
					</table>
					<br>
				</td>
			</tr>
		</table>
		<br>
 		<input type="hidden" name="h_user_locale" value="en_GB">
	 </td>
</tr>
</table>

</body>
</html>

