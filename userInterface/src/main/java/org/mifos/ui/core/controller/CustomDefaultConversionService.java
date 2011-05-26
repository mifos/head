package org.mifos.ui.core.controller;

import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.core.convert.ConversionService;

public class CustomDefaultConversionService extends DefaultConversionService {

    private final StringToDateWithLocaleConverter customConverter = new StringToDateWithLocaleConverter();
    
    public CustomDefaultConversionService(ConversionService conversionService) {
        super(conversionService);
        addConverter(customConverter);
    }
    
}
