<script language="javascript">
	function fnLogout() {
		location.href="loginAction.do?method=logout";
	}
    function fnYourSetting() {
		location.href="PersonnelAction.do?method=getDetails";
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188" height="74"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a href="yourSettings.do?method=get&randomNUm=${sessionScope.randomNUm}">Your settings</a>
     &nbsp;|&nbsp; <a href="javascript:fnLogout()">Logout</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a href="custSearchAction.do?method=getHomePage">Home</a></td>
          <td class="tablightorange"><a href="custSearchAction.do?method=loadMainSearch">Clients &amp; Accounts </a></td>
          <td class="taborange"><a href="reportsAction.do?method=load" class="tabfontwhite">Reports</a></td>
          <td class="tablightorange"><a href="AdminAction.do?method=load" >Admin</a></td>
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