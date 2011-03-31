package org.mifos.ui.core.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
@Component
public class ChangePasswordValidator extends SpringBeanAutowiringSupport implements Validator{
	
    @Autowired
    private NewLoginServiceFacade loginServiceFacade;
    
    @Autowired
    private AdminServiceFacade adminServiceFacade;
    
    @Autowired
    MessageSource messageSource;
	
	protected ChangePasswordValidator() { }

	@Override
	public boolean supports(Class<?> clazz) {
		return ChangePasswordFormBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ChangePasswordFormBean formBean = (ChangePasswordFormBean) target;
		
        Locale locale = adminServiceFacade.retreiveLocaleFromConfiguration();
        
        String oldPasswordLabel = messageSource.getMessage("login.oldpassword", null, locale);
        String newPasswordLabel = messageSource.getMessage("login.newpassword", null, locale);
        String confirmNewPasswordLabel = messageSource.getMessage("login.confirmpassword", null, locale);
		
		if(formBean.getOldPassword().isEmpty()){
			errors.reject("errors.mandatory", new String[]{oldPasswordLabel},null );
		}else{
			if(!loginServiceFacade.checkOldPassword(formBean.getUsername(), formBean.getOldPassword())){
				errors.reject("errors.invalidoldpassword");
			}else if(!formBean.getNewPassword().equals(formBean.getNewPasswordConfirmed())){
				errors.reject("errors.newconfpassword");
			}
			if(formBean.getNewPassword().equals(formBean.getOldPassword())){
				errors.reject("errors.sameoldandnewpassword");
			}
		}
		
		if(formBean.getNewPassword().isEmpty()){
			errors.reject("errors.mandatory", new String[]{newPasswordLabel}, null);
		}else if(formBean.getNewPassword().length()<6){
			errors.reject("errors.minimumlength", new String[]{newPasswordLabel, "6"},null);
		}else if(formBean.getNewPassword().length()>20){
			errors.reject("errors.maximumlength", new String[]{newPasswordLabel, "20"},null);
		}
		
		if(formBean.getNewPasswordConfirmed().isEmpty()){
			errors.reject("errors.mandatory", new String[]{confirmNewPasswordLabel},null);
		}else if(formBean.getNewPasswordConfirmed().length()<6){
			errors.reject("errors.minimumlength", new String[]{confirmNewPasswordLabel, "6"},null);
		}else if(formBean.getNewPasswordConfirmed().length()>20){
			errors.reject("errors.maximumlength", new String[]{confirmNewPasswordLabel, "20"},null);
		}
	}

}
