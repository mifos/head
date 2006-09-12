
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
<html-el:form action="PersonAction.do?method=get">
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">
          <table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td class="headingorange">
			<mifos:mifoslabel name="Personnel.UserAdded" bundle="PersonnelUIResources"></mifos:mifoslabel>
			<br><br>
   				<font class="fontnormalRedBold">
   					<html-el:errors bundle="PersonnelUIResources"/>
   				</font>
		     
              </td>
            </tr>
            <tr>
              <td class="fontnormalbold"> 
		<mifos:mifoslabel name="Personnel.PleaseNote" bundle="PersonnelUIResources"></mifos:mifoslabel>
		<span class="fontnormal"> 
		<c:out value="${requestScope.displayName}"/>
		 <mifos:mifoslabel name="Personnel.SystemIdAssigned" bundle="PersonnelUIResources"/>
		 </span> 
		<c:out value="${requestScope.globalPersonnelNum}"/>
		<span class="fontnormal"><br>    
                            <br>
                            </span>
			<a href="PersonAction.do?method=get&globalPersonnelNum=<c:out value="${requestScope.globalPersonnelNum}"/>">
			<mifos:mifoslabel name="Personnel.ViewUserdetailsNow" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a>
			<span class="fontnormal"><br>
                            <br>
                        </span>
			<span class="fontnormal">
			 <a href="PersonAction.do?method=chooseOffice">
			<mifos:mifoslabel name="Personnel.AddNewUser" bundle="PersonnelUIResources"></mifos:mifoslabel>
			</a>
			</span></td>
            </tr>
          </table>            <br>
          </td>
        </tr>
      </table>
      <br>
 
</html-el:form>
</tiles:put>
</tiles:insert>
