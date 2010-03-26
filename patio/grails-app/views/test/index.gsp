<h2><g:message code="mifos.client.list"/></h2>

<ul>
<g:each var="client" in="${clients}">
    <li><g:link action="viewClient" id="${client.id}">${client.firstName} ${client.lastName}</g:link></li>
</g:each>
</ul>
