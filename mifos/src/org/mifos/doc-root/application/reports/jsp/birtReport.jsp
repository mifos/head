<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insert definition=".reportRender">
<tiles:put name="body" type="string">
    <iframe style="border:none; padding:0px;" width="100%" height="100%" src="<c:url value='/run' />?__report=report/<c:out value='${reportFile}' />&reportName=<c:out value='${reportName}' />&userId=<c:out value='${sessionScope.UserContext.id}'/>&__format=pdf"></iframe>
</tiles:put>
</tiles:insert>
