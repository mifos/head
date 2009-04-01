<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<script language="javascript">
	function fnLogout() {
		location.href="loginAction.do?method=logout";
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="188" rowspan="2"><img src="pages/framework/images/logo.gif" width="188" height="74"></td>
    <td align="right" bgcolor="#FFFFFF" class="fontnormal"><a id="header.link.yoursettings" href="yourSettings.do?method=get&randomNUm=${sessionScope.randomNUm}"><mifos:mifoslabel name="framework.yoursettings" bundle="FrameworkUIResources"></mifos:mifoslabel></a>
     &nbsp;|&nbsp; <a id="header.link.logout" href="javascript:fnLogout()"><mifos:mifoslabel name="framework.logout" bundle="FrameworkUIResources"></mifos:mifoslabel></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
  </tr>
  <tr>
    <td align="left" valign="bottom" bgcolor="#FFFFFF"><table border="0" cellspacing="1" cellpadding="0">
        <tr>
          <td class="tablightorange"><a id="header.link.home" href="custSearchAction.do?method=getHomePage"><mifos:mifoslabel name="framework.home" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="tablightorange"><a id="header.link.clientsAndAccounts" href="custSearchAction.do?method=loadMainSearch"><mifos:mifoslabel name="framework.clientsAndAccounts" bundle="FrameworkUIResources"></mifos:mifoslabel> </a></td>
          <td class="tablightorange"><a id="header.link.reports" href="reportsAction.do?method=load"><mifos:mifoslabel name="framework.reports" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
          <td class="taborange"><a id="header.link.admin" href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}" class="tabfontwhite"><mifos:mifoslabel name="framework.admin" bundle="FrameworkUIResources"></mifos:mifoslabel></a></td>
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