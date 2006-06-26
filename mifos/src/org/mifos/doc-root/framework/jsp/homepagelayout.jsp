<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/custom-menu" prefix="menu" %>
<td width="174" height="350" align="left" valign="top" class="bgorangeleft">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<tr>
          <td class="leftpanehead">
          <tiles:getAsString name="menutitle"/>
          </td>
        </tr>
       
       
       
        <tr> 
		<menu:leftmenu topMenuTab="Home"/>
        </tr>
</table>
</td>      