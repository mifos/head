<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".clientsacclayoutmenu">
 <tiles:put name="body" type="string">
 
 <script>
 	function fnPreview(officeId,officeName) {
 		customerSearchActionForm.method.value="preview";
 		customerSearchActionForm.officeId.value=officeId;
 		customerSearchActionForm.officeName.value=officeName;
 		customerSearchActionForm.action="CustomerSearchAction.do";
 		customerSearchActionForm.submit();
 	}
 	
 	function fnofficeName(officeName) {
 		customerSearchActionForm.officeName.value=officeName;
 	}
 </script>
 
<html-el:form action="CustomerSearchAction.do" >
          <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL10" >
            <table width="90%" border="0" cellpadding="0" cellspacing="3">
              <tr>
                <td align="left" valign="top"><span class="headingorange">
<c:out value='${requestScope.Context.businessResults["Office"]}'/>                     	
               	<br>
                  </span><span class="fontnormalbold">
	                  <mifos:mifoslabel name="CustomerSearch.toreview"/>
	                  <mifos:mifoslabel name="CustomerSearch.or"/>
	                  <mifos:mifoslabel name="CustomerSearch.edit"/>
	                  <mifos:mifoslabel name="CustomerSearch.a"/>
	                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>,
	                  <c:choose>
	                 	 <c:when test="${sessionScope.isCenterHeirarchyExists==Constants.YES}">
	                 	 	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,&nbsp;
	                  		<mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />,
	                  	</c:when>
	                  	<c:otherwise>
	                  		<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  		</c:otherwise>
	                  </c:choose>
	                  <mifos:mifoslabel name="CustomerSearch.or"/>
	                  <mifos:mifoslabel name="CustomerSearch.account"/>
                  </span></td>
              </tr>
                          
            </table>
            <br>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="313" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="4">
                  <tr class="fontnormal">
                    <td width="100%" colspan="2" class="bglightblue"><span class="heading">
                    <mifos:mifoslabel name="CustomerSearch.search"/>
                    </span></td>
                  </tr>
                  <font class="fontnormalRedBold"><html-el:errors bundle="CustomerSearchUIResources"/> </font>    
                </table>
                <table width="90%" border="0" cellspacing="0" cellpadding="4">
                  <tr>
                    <td class="paddingbottom03"><span class="fontnormal">
                    	<mifos:mifoslabel name="CustomerSearch.searchstring"/>
                    </span> </td>
                  </tr>
                </table>

                  <table border="0" cellpadding="4" cellspacing="0">
                    <tr>
                      <td>
<html-el:text property="searchNode(searchString)" maxlength="200"/>
<c:choose> 
<c:when test='${requestScope.Context.businessResults["Office"]==mifos}' >
<html-el:hidden property="searchNode(searchBranch)" value="All Branches"/>      
</c:when>
<c:otherwise>
<html-el:hidden property="searchNode(searchBranch)" value='${requestScope.Context.businessResults["Office"]}'/>      
</c:otherwise>
</c:choose>

<html-el:hidden property="searchNode(search_name)" value="CustomerSearchResults"/>
                      </td>
                      <td class="paddingleft05notop">
<html-el:select style="width:136px;" property="searchNode(search_officeId)" onchange="fnofficeName(this.options[selectedIndex].text)">
<html-el:option value="0">
<mifos:mifoslabel name="CustomerSearch.all"/><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/><mifos:mifoslabel name="CustomerSearch.s"/>
</html-el:option>
<html-el:options collection="OfficesList" property="officeId" labelProperty="officeName"/>
</html-el:select>                     
						</td>
                      </tr>
                    <tr>
                      <td>&nbsp;</td>
                      <td align="right" class="paddingleft05notop">
                      <html-el:submit style="width:60px;" styleClass="buttn">
                      	<mifos:mifoslabel name="CustomerSearch.search"/>
                      </html-el:submit>
                     </td>
                      </tr>
                  </table></td>
                <td width="101" align="center" valign="top" class="headingorange">
                <mifos:mifoslabel name="CustomerSearch.or"/>&nbsp;
                </td>
                <td width="287" valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="4">
                  <tr class="fontnormal">
                    <td width="100%" colspan="2" class="bglightblue"><span class="heading">
                    <mifos:mifoslabel name="CustomerSearch.select"/>&nbsp;
                    <mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/>
                   </span></td>
                  </tr>
                </table>
<html-el:hidden property="officeId" value=""/>
<html-el:hidden property="officeName" value=""/>
<html-el:hidden property="method" value="loadAllBranches"/>
<script type="text/javascript"> 
	document.getElementById("searchNode(search_officeId)").selectedIndex=0;
	fnofficeName(document.getElementById("searchNode(search_officeId)").options[0].text);
</script>
<div id="Layer2" style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;"> 
                          <span class="fontnormal">
<c:forEach items="${requestScope.OfficesList}" var="office">
<html-el:link href="javascript:fnPreview('${office.officeId}','${office.officeName}')">
<c:out value="${office.officeName}"/>
</html-el:link><br>
</c:forEach>                          
                          </span></div>
                </td>
              </tr>
            </table>
            <br>
            <br>
          </td>
        </tr>
      </table>
      <br>
</html-el:form>
</tiles:put>
</tiles:insert>
