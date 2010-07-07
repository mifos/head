[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

[@mifos.header "login" /]

    <span id="page.id" title="ChangeFirstTimeUserPassword" />

	<div class="page-content">

		<div id="formsection">
     		<form method="post" action="changePassword.ftl" id="changepassword.form">

					<div class="error-messages">
					[@spring.bind "formBean" /]
  					[@spring.showErrors "<br>" /]
					</div>
					
					<label for="changePassword.input.username">[@spring.message "UserName" /]:</label>
					[@spring.bind "formBean.username" /]
					<input type="text" id="changePassword.input.username" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
					[@spring.showErrors "<br>" /]
					
					<label for="changePassword.input.oldpassword">Old Password:</label>
					[@spring.bind "formBean.oldPassword" /]
					<input type="password" id="changePassword.input.oldpassword" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
					[@spring.showErrors "<br>" /]
					
				    <label for="changePassword.input.newpassword">New Password:</label>
					[@spring.bind "formBean.newPassword" /]
					<input type="password" id="changePassword.input.newpassword" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
					[@spring.showErrors "<br>" /]
					
					<label for="changePassword.input.newpasswordconfirmed">New Password Confirmation:</label>
					[@spring.bind "formBean.newPasswordConfirmed" /]
					<input type="password" id="changePassword.input.newpasswordconfirmed" name="${spring.status.expression}" value="${spring.status.value?default("")}" />
					[@spring.showErrors "<br>" /]

				    <input type="submit" name="submit" value="Submit" />
					<input type="submit" id="CANCEL" name="CANCEL" value="Cancel" />
			</div>
		</form>
		
	</div> <!-- page-content -->
[@mifos.footer /]

   