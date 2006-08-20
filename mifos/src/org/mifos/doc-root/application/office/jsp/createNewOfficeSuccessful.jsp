<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.offActionForm.method.value="cancel";
	offActionForm.submit();
  }
  function seeDetails(){
	document.offActionForm.method.value="get";
	offActionForm.submit();
  }
  function create(){
	document.offActionForm.method.value="load";
	offActionForm.submit();
  }  
</script>

		<html-el:form action="/offAction.do" >

					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="70%" align="left" valign="top" class="paddingL15T15">
							<table width="98%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td class="headingorange"><mifos:mifoslabel
										name="Office.labelCreatedSuccessfully"
										/> <br>
									<br>
									</td>
								</tr>
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelPleaseNote" />
									<span class="fontnormal"><c:out	value="${BusinessKey.officeName}"></c:out> <mifos:mifoslabel
										name="Office.labelOfficeAssignedNumber"
										/> </span> <c:out
										value="${BusinessKey.globalOfficeNum}"></c:out> <br>
									<br>

									 
									
									<html-el:link action="/OfficeAction.do?method=get&officeId=${BusinessKey.officeId}">
									 <mifos:mifoslabel
										name="Office.labelViewOfficeDetails" />
									</html-el:link>
									<span class="fontnormal"><br>
									<br>
									</span><span class="fontnormal"> 
									<html-el:link action="/offAction.do?method=load" >
									
									 <mifos:mifoslabel
										name="Office.labelAddNewOfficeNow" /></html-el:link> </span></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					<!-- </td>
				</tr>
			</table> -->
			<html-el:hidden property="input" value="createSuccess" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="method" value="get" />
			<html-el:hidden property="officeId"  />
		</html-el:form>

	</tiles:put>

</tiles:insert>
