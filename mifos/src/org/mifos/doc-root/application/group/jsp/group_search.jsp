
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">

<script type="text/javascript">

function goToCancelPage()
{
    groupCustActionForm.method.value="cancel";
    groupCustActionForm.input.value="createGroup";
    groupCustActionForm.submit();
}
</script>
<html-el:form action="groupCustAction.do">

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
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                <td class="timelineboldorange"><mifos:mifoslabel name="Group.select" /> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></td>
              </tr>
            </table></td>
            <td width="25%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.personnelinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="center"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.mfiinformation" /></td>
              </tr>
            </table></td>
            <td width="25%" align="right"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Group.reviewandsubmit" /></td>
              </tr>
            </table></td>
            </tr>
        </table>
       </td>
      </tr>
    </table>
   
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates"><table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange"><span class="heading"><mifos:mifoslabel name="Group.createnew" />
                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /> - </span><mifos:mifoslabel name="Group.select" />  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></td>
              </tr>
            </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="0">
              <tr><logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><html-el:errors
						bundle="GroupUIResources" /></font><br></td>
					</logic:messagesPresent>
				</tr>
               </table>                            
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal">
                  <mifos:mifoslabel name="Group.entera"/> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/> <mifos:mifoslabel name="Group.clickcancel"/>
                  <mifos:mifoslabel name="Group.createpagehead3" />                     
                  </span> </td>
                </tr>
              </table>
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
                <tr class="fontnormal">
                  <td align="right">&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td align="right"><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />  <mifos:mifoslabel name="Group.name" /></td>
                  <td>
                  	<html-el:text property="input" maxlength="200"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="28%">&nbsp; </td>
                  <td width="72%">      
      <html-el:submit styleClass="buttn" style="width:70px;">
	      <mifos:mifoslabel name="button.proceed" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:submit>
&nbsp;
      <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
            <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
      </html-el:button>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td>&nbsp;</td>
                  <td>
                  <c:if test="${sessionScope.groupHierarchyRequired eq 'No'}">
                   <a href="clientCustAction.do?method=chooseOffice&amp;groupFlag=0"> 
                 <%-- <a href="clientCreationAction.do?method=load&amp;office.officeId=5&amp;isClientUnderGrp=0"> --%>
                  <br>
                    <mifos:mifoslabel name="Group.clickheretocontinueif" /> <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /> <mifos:mifoslabel name="Group.membershipisnotrequiredforyour" /> <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />.</a>
                  </c:if>
                  </td>
                </tr>
              </table>              
              <br>
          </td>
        </tr>
      </table>
      <br>
<html-el:hidden property="method" value="search"/>       
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
