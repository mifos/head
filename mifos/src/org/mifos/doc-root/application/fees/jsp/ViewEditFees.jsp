<!--
/**

* ViewEditFees.jsp    version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
 -->
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">

<html-el:form action="/feesAction.do">

<script>


function fnOnAdmin(form){
	form.input.value="viewEditFees";
	form.method.value="load";
	form.action="AdminAction.do";
	form.submit();
}


function fnOnNewFee(form){
	form.input.value="viewEditFees";
	form.method.value="load";
	form.action="feeaction.do";
	form.submit();
}

function fnOnView(Id){
	document.FeesActionForm.input.value="viewEditFees";
	document.FeesActionForm.feeIdTemp.value=Id;
	document.FeesActionForm.method.value="get";
	document.FeesActionForm.action="feesAction.do";
	document.FeesActionForm.submit();
}

</script>
   
	 <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        	<span class="fontnormal8pt">
        		<html-el:link href="javascript:fnOnAdmin(FeesActionForm)">
        			<mifos:mifoslabel name="Fees.admin" bundle="FeesUIResources">
        			</mifos:mifoslabel>
        		</html-el:link>
        		/
        	</span>

        	<span class="fontnormal8ptbold">
        		<mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources">
        		</mifos:mifoslabel>
        	</span>
        </td>
      </tr>
    </table>

    <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15">
		      <table width="95%" border="0" cellpadding="3" cellspacing="0">

			    <tr>
				  <td class="headingorange">
					<span class="headingorange">
						<mifos:mifoslabel name="Fees.viewfees" bundle="FeesUIResources"></mifos:mifoslabel>
					</span>
				  </td>
				</tr>

				<tr>
				  <td class="fontnormalbold">
					<span class="fontnormal">
						<mifos:mifoslabel name="Fees.ViewFeesInstruction" bundle="FeesUIResources">
        				</mifos:mifoslabel>
						<html-el:link href="javascript:fnOnNewFee(FeesActionForm)">
						<mifos:mifoslabel name="Fees.smalldefinenewfee" bundle="FeesUIResources">
  	        			</mifos:mifoslabel>
						</html-el:link>
						<br>
					</span>				
                
					<span class="fontnormalbold">
						<span class="fontnormalbold">
						<br>
						</span>
					</span>
                
					<span class="fontnormalbold">
					</span>
				
					<span class="fontnormal">
                	</span>     	
                	
                	<span class="fontnormalbold">
                		<span class="fontnormalbold">
                		<font class="fontnormalRedBold">
                		<html-el:errors bundle="FeesUIResources" />
                		</font>                		
                		</span>
					</span>
										
					<span class="fontnormalbold">
                		<span class="fontnormalbold">
                			<mifos:mifoslabel name="Fees.productfees" bundle="FeesUIResources">
  	        	  			</mifos:mifoslabel><br>
                		</span>
               		</span> 

					<span class="fontnormalbold"> 
					</span>

					<table width="90%" border="0" cellspacing="0" cellpadding="0">
						
						<c:forEach items='${requestScope.Context.businessResults["productfeesData"]}' var="productType">
							<tr class="fontnormal">
							     <td width="1%">
					    			<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
								 </td>    
								 <td width="99%">
								     <html-el:link href="javascript:fnOnView(${productType.feeId})">
									 <c:out value="${productType.feeName}" />
                    					</html-el:link>                    					
									(<c:out value="${productType.categoryName}" />)									
									<c:if test="${productType.status == FeesConstants.FEES_INACTIVE}">
										<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;
										<mifos:mifoslabel name="Fees.inactive" bundle="FeesUIResources"/>
									</c:if>
                                 </td>  
                             </tr>
						</c:forEach>	
						
						
					</table>
					<br>
						<mifos:mifoslabel name="Fees.clientfees" bundle="FeesUIResources">
        				</mifos:mifoslabel>
				<br>

			<table width="90%" border="0" cellspacing="0" cellpadding="0">
					<c:forEach items='${requestScope.Context.businessResults["clientfeesData"]}' var="clientType">
							<tr class="fontnormal">
							     <td width="1%">
					    			<img src="pages/framework/images/bullet_circle.gif" width="9" height="11">
								 </td>    
								 <td width="99%">
								     <html-el:link href="javascript:fnOnView(${clientType.feeId})">
									 <c:out value="${clientType.feeName}" />
                    					</html-el:link>
									(<c:out value="${clientType.categoryName}" />)
									<c:if test="${clientType.status == FeesConstants.FEES_INACTIVE}">
										<img src="pages/framework/images/status_closedblack.gif" width="8" height="9">&nbsp;
										<mifos:mifoslabel name="Fees.inactive" bundle="FeesUIResources"/>
									</c:if>
                                 </td>  
                             </tr>
						</c:forEach>	
		</table>
	</td>
	</tr>
	<html-el:hidden property="input" />
	<html-el:hidden property="method" value="get"/> 
	<html-el:hidden property="feeIdTemp" value=""/> 
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
</html-el:form>
</tiles:put>
</tiles:insert>