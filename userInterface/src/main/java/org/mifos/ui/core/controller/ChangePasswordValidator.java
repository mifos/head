package org.mifos.ui.core.controller;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Component
public class ChangePasswordValidator extends SpringBeanAutowiringSupport implements Validator{
	
    @Autowired
    private NewLoginServiceFacade loginServiceFacade;
    
    @Autowired
    private AdminServiceFacade adminServiceFacade;
    
    @Autowired
    MessageSource messageSource;

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
        
		if(formBean.getOldPassword().isEmpty()){
			errors.reject("errors.mandatory", new String[]{oldPasswordLabel},null );
		}else{
			if(loginServiceFacade.checkOldPassword(formBean.getUsername(), formBean.getOldPassword())){
				if(!formBean.getNewPassword().equals(formBean.getNewPasswordConfirmed())){
					errors.reject("errors.newconfpassword");
				}
			}else {
				errors.reject("errors.invalidoldpassword");
			}
			if(formBean.getNewPassword().equals(formBean.getOldPassword())){
				errors.reject("errors.sameoldandnewpassword");
			}
		}
		
		if(StringUtils.isNotBlank(formBean.getNewPassword())) {
				
			loginServiceFacade.validatePassword(formBean.getNewPassword(), errors);
	        
	        if (!formBean.getNewPassword().equals(formBean.getNewPasswordConfirmed())) {
	        	errors.reject("errors.newconfpassword");
	        }
	        
		} else {
			errors.reject("errors.mandatory", new String[]{newPasswordLabel}, null);
		}
		
		
	}

}
