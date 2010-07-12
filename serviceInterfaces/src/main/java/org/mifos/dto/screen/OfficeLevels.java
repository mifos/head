package org.mifos.dto.screen;

public enum OfficeLevels {

    HEADOFFICE(Short.valueOf("1")), REGIONALOFFICE(Short.valueOf("2")), SUBREGIONALOFFICE(Short.valueOf("3")), AREAOFFICE(
            Short.valueOf("4")), BRANCHOFFICE(Short.valueOf("5"));

    private Short value;

    private OfficeLevels(Short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }
}
