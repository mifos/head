<script language="javascript">
	function fnLogout() {
		location.href="mifoslogout.do?method=logout";
	}
    function fnYourSetting() {
		location.href="PersonnelAction.do?method=get";
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188" height="74"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a href="yourSettings.do?method=get">Your settings</a>
     &nbsp;|&nbsp; <a href="javascript:fnLogout()">Logout</a>&nbsp;|&nbsp; <a href="#">Help</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a href="CustomerSearchAction.do?method=getHomePage">Home</a></td>
          <td class="taborange"><a href="CustomerSearchAction.do?method=load" class="tabfontwhite">Clients &amp; Accounts </a></td>
          <td class="tablightorange"><a href="reportsAction.do?method=load">Reports</a></td>
          <td class="tablightorange"><a href="AdminAction.do?method=load">Admin</a></td>
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