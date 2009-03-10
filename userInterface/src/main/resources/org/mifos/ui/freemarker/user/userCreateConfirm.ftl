[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "user.create.title" /]
  <!-- page: creatUser.ftl -->
  
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 

	<div id="page-content">
	
		<h2 id="user.create.confirm.heading">[@spring.message "user.create.confirm.heading"/]</h2>
	
		<table>
			<tr>
				<td>[@spring.message "user.userId.description"/]</td>
				<td>${user.userId}</td>
			</tr>
			<tr>
				<td>[@spring.message "user.password.description"/]</td>
				<td>${user.password}</td>
			</tr>
			<tr>
				<td>[@spring.message "user.roles.description"/]</td>
				<td>
					<ul>
						[#list user.roles as role]
							<li> [@spring.message "user.role.${role}"/] </li>
						[/#list]
					</ul>
				</td>
			</tr>
		</table>	

  	</div>
  
      [#include "footer.ftl"]    
[@mifos.footer /]
  