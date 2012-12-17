package org.mifos.ui.core.controller;

import java.util.List;

import org.mifos.application.admin.servicefacade.CoaDto;

public class CoaListViewModel {
    
    private List<CoaDto> coaList;

    private boolean modifiable;
    
    public List<CoaDto> getCoaList() {
        return coaList;
    }

    public void setCoaList(List<CoaDto> coaList) {
        this.coaList = coaList;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

}
