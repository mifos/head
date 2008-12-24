<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<td width="174" height="500" align="left" valign="top" class="bgorangeleft">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
		
		<tr>
          <td class="leftpanehead" colspan="2">
          <tiles:getAsString name="menutitle"/>
          </td>
        </tr>
       
        <tiles:insert attribute="search" ignore="true"/>
       
      </table>
      
</td>      