<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<script language="javascript">
  function goToCancelPage(){
	groupTransferActionForm.action="groupTransferAction.do?method=cancel";
	groupTransferActionForm.submit();
  }
</script>

<html-el:form action="centerAction.do?method=search">
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>
<html-el:hidden property="input" value="CenterSearch_TransferGroup"/> 
<html-el:hidden property="currentFlowKey" value="${param.currentFlowKey}" />
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	            	<%--<customtags:headerLink/>--%>
	          </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
                  <span class="heading">
                  <c:out value="${BusinessKey.displayName}"/>
                  </span><mifos:mifoslabel name="Center.dash" bundle="CenterUIResources"/> <mifos:mifoslabel name="Center.change" bundle="CenterUIResources"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /><mifos:mifoslabel name="Center.Membership" bundle="CenterUIResources"/></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal">
                  <mifos:mifoslabel name="Center.clickInstruction" bundle="CenterUIResources"/> 
                  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
                  <mifos:mifoslabel name="Center.nameChangeInstruction" bundle="CenterUIResources"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
                   <mifos:mifoslabel name="Center.cancelInstruction" bundle="CenterUIResources"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                  <mifos:mifoslabel name="Center.changeCenterMembershipInstruction" />
                  <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
                  <mifos:mifoslabel name="Center.Membership" bundle="CenterUIResources"/>
                 </td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td colspan="2" align="right">&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td width="20%" align="right"><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/><c:out value=" "/>
							<mifos:mifoslabel name="Center.Name" bundle="CenterUIResources"></mifos:mifoslabel></td>
                  <td width="80%"><html-el:text property="searchNode(searchString)"/>
                  	<html-el:hidden property="searchNode(search_name)" value="SearchCenter"/>
	                  <html-el:submit styleClass="buttn" style="width:70px;">
	                  	<mifos:mifoslabel name="button.Search" bundle ="CenterUIResources"></mifos:mifoslabel>
	                  </html-el:submit>
				  </td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
   					<td>
   						<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
					</td>
				</tr>
                <tr>
                  <td>
                 	   <mifos:mifostabletagdata name="groupTransfer" key="centerSearch" type="single" 
	                	className="CenterSearchResults" width="100%" border="0" cellspacing="0" cellpadding="0"/>
                  </td>
                  </tr>
              </table>
              <table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td align="center">
                    <html-el:button property="cancelButton" onclick="goToCancelPage();"  styleClass="cancelbuttn" style="width:70px;">
                    <mifos:mifoslabel name="button.cancel" bundle ="CenterUIResources"></mifos:mifoslabel> 
                    </html-el:button>
	    	        
                  </td>
                </tr>
              </table>             
          </td>
          </tr>
        </table>        

</html-el:form>