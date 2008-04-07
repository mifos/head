<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:insert definition=".create">
	<script type="text/javascript">
	function goToCancelPage(form){
		form.action = "AdminAction.do?method=load";
		form.submit();
  	}
	</script>
  <tiles:put name="body" type="string">
		<html-el:form method="post"
			action="/reportsCategoryAction.do?method=addNewCategory">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="470" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="reportsCategory.information"/>
												</td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="reportsCategory.review&submit" /></td>
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
							<td width="70%" height="24" align="left" valign="top"
								class="paddingleftCreates">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td class="headingorange">
										<span class="heading"> 
											<mifos:mifoslabel name="reportsCategory.defineNewReportCategory" /> -
										</span>  
									<mifos:mifoslabel
												name="reportsCategory.review&submit" /></td>
								</tr>
								<tr>
									<td class="fontnormalbold"><span class="fontnormal">
									    <mifos:mifoslabel name="reportsCategory.reviewStatement" />
										<mifos:mifoslabel name="reportsCategory.clickSubmit" />
										<mifos:mifoslabel name="reportsCategory.clickCancel" />
										</span>
									</td>
								</tr>
								<tr>
                                    <td class="fontnormalbold"/>
                                </tr>
								<tr>
								    <td class="fontnormalbold"> 
								    	<br />
								       <mifos:mifoslabel name="reportsCategory.name" />
								       :
								       <span class="fontnormal">
										    <c:out value="${categoryName}" />
								       </span>
								    </td>
								</tr>
								<tr>
									<td>
										<font class="fontnormalRedBold">
											<html-el:errors bundle="reportsCategoryUIResources" /> 
										</font>
									</td>
								</tr>
								<tr class="fontnormal">
									<td>
										<input class="insidebuttn" type="button" onclick="javascript:history.go(-1)" 
											value="<mifos:mifoslabel name="reportsCategory.editReportInformation" />" name="Button222"/>
									</td>
								</tr>
							</table>
						 <table width="90%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td align="center" class="blueline">
												&nbsp;
											</td>
										</tr>
						  </table><br>	
						<table width="90%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="reportsCategory.submit"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage(this.form);" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="reportsCategory.cancel"/>
									</html-el:button></td>
								</tr>
						</table><br>
							</td>
						</tr>
					</table><br>
					</td>
				</tr>
			</table>
			<html:hidden property="method" value="search" />
			
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
