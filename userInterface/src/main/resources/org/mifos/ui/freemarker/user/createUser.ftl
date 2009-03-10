[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
[#assign form=JspTaglibs["/WEB-INF/tld/spring-form.tld"]]

[@mifos.header "user.create.title" /]
  <!-- page: createUser.ftl -->
  
  [@mifos.topNavigation currentTab="Admin" /]
  [#include "adminLeftPane.ftl" ] 

	<div id="page-content">
	
		<h2 id="user.create.heading">[@spring.message "user.create.heading"/]</h2>
				
		 [@form.form action="createUser.ftl" commandName="user"]
		 <div id="form.errors">
		 [@form.errors path="*" cssClass="error-messages"/]
		 </div>
		
			<fieldset>
				<legend>User Login Details</legend>
			
				<label for="user.form.userId">[@spring.message "user.userId"/]:</label>
					[@form.input path="userId"/]
					<br/>
			
				<label  for="user.form.password">[@spring.message "user.password"/]:</label>
					[@form.input path="password"/]
					<br/>
			
				<label for="user.confirmPassword">[@spring.message "user.confirmPassword"/]:</label>
					[@form.input path="confirmPassword"/]
					<br/>
				
				<label for="user.form.roles">[@spring.message "user.roles.description"/]:</label>
				<select id="roles" name="roles" multiple>
					[#list availableRoles as role]			
						[#if user.roles?seq_contains(role)]
							<option name="roles" value="${role}" selected>
								[@spring.message "security.role.${role}"/]
							</option>
						[#else]
							<option name="roles" value = "${role}">
								[@spring.message "security.role.${role}"/]
							</option>
						[/#if]
					[/#list]
				</select>
				
				<!--
					This marker field tells Spring MVC to reset the "roles" field to be empty
					if the field is missing from the request (i.e., if the user de-selects all
					options). Otherwise the controller will not reset the field and leave its
					default value, "ROLE_USER", which is misleading because it suppresses validation
					which should return an error stating that the user must be given at least one
					role.
				-->
				<input type="hidden" name="_roles" value="1"/>
				<br/>
				
				<label for="kludge"></label>
					<input type="submit" value="[@spring.message "submit"/]" class="buttn" id="user.form.submit">
			</fieldset>
			
		[/@form.form]
  	
  	</div>
  
      [#include "footer.ftl"]    
[@mifos.footer /]
  