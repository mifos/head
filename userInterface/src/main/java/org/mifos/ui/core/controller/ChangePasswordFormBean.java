package org.mifos.ui.core.controller;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_UNUSUAL", justification = "using commons equals builder")
public class ChangePasswordFormBean {

    private String username;
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirmed;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmed() {
        return this.newPasswordConfirmed;
    }

    public void setNewPasswordConfirmed(String newPasswordConfirmed) {
        this.newPasswordConfirmed = newPasswordConfirmed;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}