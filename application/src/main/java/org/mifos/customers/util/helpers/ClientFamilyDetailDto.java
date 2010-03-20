package org.mifos.customers.util.helpers;

import java.sql.Date;

import org.mifos.framework.util.helpers.DateUtils;

public class ClientFamilyDetailDto {

    private final String relationship;
    private final String displayName;
    private final Date dateOfBirth;
    private final String gender;
    private final String livingStatus;

    public ClientFamilyDetailDto(final String relationship, final String displayName, final Date dateOfBirth,
            final String gender, final String livingStatus) {
        this.relationship = relationship;
        this.displayName = displayName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.livingStatus = livingStatus;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getLivingStatus() {
        return this.livingStatus;
    }

    public String getDateOfBirthForBrowser() {
        if (getDateOfBirth() != null) {
            return DateUtils.makeDateAsSentFromBrowser(getDateOfBirth());
        }
        return null;
    }

}
