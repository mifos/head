<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<form>
<%String expFileName = (String) session.getAttribute("expFileName");
%>
<input type='hidden' name='filename' value='<%=expFileName%>'>
<script>


</script>
</form>
<a href='<%=request.getContextPath()+"/pages/application/reports/uploads/"+expFileName%>' target='_blank'>Open Report</a>
</tiles:put>
</tiles:insert>
